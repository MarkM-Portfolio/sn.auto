# Description

This is a library cookbook, it installs the IBM Installation Manager and provides a LWRP to install IBM products using
the installaiton Manager.


see http://www-01.ibm.com/support/knowledgecenter/SSDV2W_1.8.0/com.ibm.cic.commandline.doc/topics/r_tools_imcl.html?lang=en
# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

*No dependencies defined*

# Attributes

* `node[:im][:user]` - User and Group name under which the server will be installed and running. Defaults to `"im"`.
* `node[:im][:group]` -  Defaults to `"im-admin"`.
<<<<<<< HEAD
* `node[:im][:base_dir]` - Base installation directory. Defaults to `"/opt/IBM/IM"`.
=======
* `node[:im][:user_home_dir]` - Home directory for im user. Ignored if im user is root.
For non-root installs IM's registary will be found at user_home_dir/etc/.ibm/registry/InstallationManager.dat
The registary path MUST NOT be equal to, a parent directory, or a subdirectory of base_dir. Defaults to `"/home/im"`.
* `node[:im][:base_dir]` - Base installation directory. Defaults to `"/opt/IBM/InstallationManager"`.
* `node[:im][:data_dir]` - Data directory. Defaults to `"/var/ibm/InstallationManager"`.
>>>>>>> 26475c5d8f416c77cd984743d33699003987a85b
* `node[:im][:install_zip][:file]` - The IM install zip file. Set this if the installer is on a local filesystem. Defaults to `"nil"`.
* `node[:im][:install_zip][:url]` - The IM install zip url. Set this if the installer is on a remote fileserver. Defaults to `"nil"`.

# Recipes

* iim::default

# Resources

* [iim_iim](#iim_iim) - Installs an IBM Offering using the IBM Installation Manager.

## iim_iim


Installs an IBM Offering using the IBM Installation Manager.

### Actions

- install: Installs an IBM Offering Default action.

### Attribute Parameters

<<<<<<< HEAD
- response_file: The response file for the IBM Installation Manager. Defaults to <code>nil</code>.
- response_hash: A hash representation of the response files xml content Defaults to <code>nil</code>.
- secure_storage_file: Sets the secureStorageFile imcl option Defaults to <code>nil</code>.
- master_password_file: Sets the masterPasswordFile imcl option Defaults to <code>nil</code>.
=======
- response_file: The response file for the IBM Installation Manager. Takes priority over response_hash Defaults to <code>nil</code>.
- response_hash: A hash representation of the response files xml content. Defaults to <code>nil</code>.
- secure_storage_file: Sets the secureStorageFile imcl option. Defaults to <code>nil</code>.
- master_password_file: Sets the masterPasswordFile imcl option. Defaults to <code>nil</code>.
>>>>>>> 26475c5d8f416c77cd984743d33699003987a85b

### Examples

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

# Author

Author:: Benjamin Confino

Author:: Felix Simmendinger (<felix.simmendinger@coremedia.com>)
