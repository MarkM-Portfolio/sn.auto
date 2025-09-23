require 'minitest/spec'

describe 'recipe::robin_test::install' do


  it "runtime is installed" do
    directory("#{node[:wlp][:base_dir]}/wlp").must_exist
  end

  it "server is created" do
    file("#{node[:wlp][:base_dir]}/wlp/usr/servers/testone/server.xml").must_exist
  end


  it "server is created" do
    file("#{node[:wlp][:base_dir]}/wlp/usr/servers/testtwo/server.xml").must_exist
  end

  it "runtime is installed" do
    directory("#{node[:ihs][:paths][:install]}").must_exist
  end

  it "server is created" do
    file("#{node[:ihs][:paths][:install]}/conf/httpd.conf").must_exist
  end

  it "plugin-cfg is created" do
    file("#{node[:ihs][:paths][:install]}/conf/plugin-cfg.xml").must_exist
  end

  #Test that the round robin succeeded - each jsp file only exists on one server so if both successfully downloaded IHS is serving round robin.

  it "runtime is installed" do
    file("/tmp/testone.jsp").must_exist
  end

  it "runtime is installed" do
    file("/tmp/testtwo.jsp").must_exist
  end

end
