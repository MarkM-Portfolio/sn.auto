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

=begin
#<

Installs an IBM Offering using the IBM Installation Manager.

@action install Installs an IBM Offering

@section Examples

Installing an offering using a response hash.

```ruby
iim_iim 'Websphere 8.5.5' do
  response_hash(
    :'clean' => true,
        :'temporary' => false,
        :'server' => {
                :'repository' => {
                        :'location' => 'http://some.url'
                }
        },
        :'profile' => {
                :'id' => 'IBM Websphere Application Server V8.5',
                :'installLocation' => '/some/dir',
                :'data' => [
                        {:'key' => 'cic.selector.os', :'value' => 'linux'},
                        {:'key' => 'cic.selector.ws', :'value' => 'gtk'},
                        {:'key' => 'cic.selector.arch', :'value' => 'x86_64'},
                        {:'key' => 'cic.selector.nl', :'value' => 'en'},
                ]
        },
        :'install' => {
                :'modify' => false,
                :'offering' => [
                        {
                                :'id' => 'com.ibm.websphere.BASE.v85',
                                :'version' => 'someversion',
                                :'profile' => 'IBM Websphere Application Server V8.5',
                                :'features' => 'core.feature',
                                :'installFixes' => 'none'
                        }
                ]
        }
)
end
```

Installing an offering using a response file. The template resource must be provided by you.

```ruby
im_response_file = '/var/tmp/my-response-file'

template im_response_file do
  source 'response-file.xml.erb'
  variables(
    :repository_url => 'some_url'
  )
end

iim_iim 'Websphere 8.5.5' do
  response_file im_response_file
end
```

#>
=end

actions :install
default_action :install

#<> @attribute response_file The response file for the IBM Installation Manager. Takes priority over response_hash
attribute :response_file, :kind_of => String, :default => nil
#<> @attribute response_hash A hash representation of the response files xml content.
attribute :response_hash, :kind_of => String, :default => nil
#<> @attribute secure_storage_file Sets the secureStorageFile imcl option.
attribute :secure_storage_file, :kind_of => String, :default => nil
#<> @attribute master_password_file Sets the masterPasswordFile imcl option.
attribute :master_password_file, :kind_of => String, :default => nil

