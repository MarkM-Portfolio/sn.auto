#
# Cookbook Name:: iim
# Provider:: iim_iim
#
# (C) Copyright IBM Corporation 2013.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
require 'tempfile'

action :install do

im_base_dir = "#{node[:im][:base_dir]}"
im_dir = "#{im_base_dir}/eclipse/tools"
im_user = node[:im][:user]
im_group = node[:im][:group]

maybe_master_password_file = new_resource.master_password_file
maybe_secure_storage_file = new_resource.secure_storage_file
maybe_response_file = new_resource.response_file
  credentials_bash_snippet = '' #this goes here for later

  #First check for a secure_storage file. 

  unless maybe_secure_storage_file.nil?
      credentials_bash_snippet = "-secureStorageFile #{maybe_secure_storage_file}"
    unless maybe_master_password_file.nil?
      #TODO: add a warning if there's a master password file but no secure storage file?
      credentials_bash_snippet = "-secureStorageFile #{maybe_secure_storage_file} -masterPasswordFile #{maybe_master_password_file}"
      end
   end

  if ::File.file?(maybe_response_file)
    response_file = maybe_response_file
  elsif new_resource.response_hash
	new_contents = []
    generate_xml('  ', 'agent-input', new_resource.response_hash, new_contents)
    response_file = Tempfile.new("ibm-installation-manager-responsefile-for-#{new_resource.name}", :encoding => 'utf-8')

    file response_file do
      owner im_user
      group im_group
      content new_contents.join('\n')
      backup false
    end
  end

  #TODO: to check if an application has been installed, we should check if the responsefile has been altered and remove the response in case the execute call fails
  install_command = "#{im_dir}/imcl -showProgress #{'-accessRights nonAdmin' unless im_user == 'root'} -acceptLicense input #{::File.path(response_file)} -log /tmp/install_log.xml #{credentials_bash_snippet}"
  execute "install #{new_resource.name}" do
    user im_user
    group im_group
    cwd "#{im_dir}"
    command install_command
    # allow to create executable files and allow to read and write for others in the same group but not execution, read for others
    # if this is not set the installer will fail because it cannot lock files below /opt/IBM/IM/installationLocation/configuration
    # see https://www-304.ibm.com/support/docview.wss?uid=swg21455334
    umask '013' unless im_user == 'root'
   end
end



def generate_xml(indent, name, map, output)
  
  attributes = {}
  elements = {}

  map.each_pair do |key, value|
    if value.is_a?(Hash)
      elements[key] = value
    elsif value.is_a?(Array)
      elements[key] = value
    else
      attributes[key] = value
    end
  end

  line = "<#{name}"
  attributes.each_pair do |key, value|
    line << " #{key}=\"#{evaluate_value(value)}\""
  end

  if !elements.empty?
    line << '>'
    output <<  "#{indent}#{line}"
    
    next_indent = "  #{indent}"
    elements.each_pair do |key, value|
      if value.is_a?(Hash)
        generate_xml(next_indent, key, value, output)
      elsif value.is_a?(Array)
        if value.empty?
           output << "#{next_indent}<#{key}/>"
        elsif value.first.is_a?(Hash)
          value.each do |item|
            generate_xml(next_indent, key, item, output)
          end
        else 
          value.each do |item|
            output << "#{next_indent}<#{key}>#{evaluate_value(item)}</#{key}>"
          end
        end
      end
    end

    output << "#{indent}</#{name}>"
  else
    line << '/>'
    output << "#{indent}#{line}"
  end
  end

def evaluate_value(value)
  if value.is_a?(Proc)
    return value.call
  else
    return value
  end
end
