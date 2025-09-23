#
# Cookbook Name:: create_db2_user_instance
# Recipe:: default
#
# Copyright 2015, IBM
#
# All rights reserved - Do Not Redistribute
#

db2iadmArray = []
db2fadmArray = []
db2instArray = []
db2fencArray = []

(0..9).each do |i|
  db2iadmArray.push("db2iadm#{i}")
  db2fadmArray.push("db2fadm#{i}")
  db2instArray.push("db2inst#{i}")
  db2fencArray.push("db2fenc#{i}")
end

#create 10 db2iadm groups
db2iadmArray.each do |db2iadm|
  group db2iadm do
  end
end

#create 10 db2fadm groups
db2fadmArray.each do |db2fadm|
  group db2fadm do
  end 
end

#create 10 db2inst users
db2instArray.each do |db2inst|
  user db2inst do
    db2instIndex = db2inst[-1]
    db2instGID = "db2iadm".concat(db2instIndex)
    db2instHome = "/home/".concat(db2inst)
    gid db2instGID
    home db2instHome
  end
end

#create 10 db2fenc users
db2fencArray.each do |db2fenc|
  user db2fenc do
    db2fencIndex = db2fenc[-1]
    db2fencGID = "db2fadm".concat(db2fencIndex)
    db2fencHome = "/home/".concat(db2fenc)
    gid db2fencGID
    home db2fencHome
  end
end

execute "create an instance for each user" do
  headPartCommand = '/opt/ibm/db2/V10.5/instance/db2icrt -a server -p 50000 -u db2fenc'
  tailPartCommand = ' db2inst'
  (0..9).each do |i|
    wholeCommand = headPartCommand + i.to_s + tailPartCommand + i.to_s
    command wholeCommand
  end

end


