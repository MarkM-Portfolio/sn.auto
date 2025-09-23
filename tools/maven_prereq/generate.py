import os, subprocess
from threading import Thread
from string import ascii_letters
from custom_names import custom_names

os.chdir('/home/lcuser')

valid_filetypes = 'jar, zip, swf, war'.split(', ')

def get_public_repos():
	import urllib2
	import json

	prereqs_dir = '/home/lcuser/sn.prereqs/lwp/prereqs.sn'
	os.chdir(prereqs_dir)
	potentially_public = []
	for root, dirs, files in os.walk(prereqs_dir):
		for file in files:
			if file[-4:] != '.jar':
				continue
			if (os.system('unzip -l '+root+os.sep+file+' | grep maven*') != ''):
				potentially_public.append(root+os.sep+file+'\n')

	for search in potentially_public:
		data = json.loads(urllib2.urlopen('http://search.maven.org/solrsearch/select?wt=json&q='+search).read())
		data['response']['docs']

	return public_repos

def get_version(path):
	baseName = os.path.basename(path)
	lastCharIndex = 0
	name = path.split(os.sep)[-1]
	for i in range(len(name)):
		if name[i] not in ascii_letters:
			if i+1 == len(name) or name[i+1] not in ascii_letters:
				break
		lastCharIndex += 1
	name = baseName[:lastCharIndex]
	version = baseName[lastCharIndex:]
	name = name.strip('._-')

	try:
		f = open(path+os.sep+'component.xml', 'rb').read()
		component_version = f.split('spec-version="')[1]
		component_version = component_version.split('"')[0].strip()
		if component_version != None and component_version != '':
			return (name, component_version)
	except IOError:
		pass
	
	if version != None and version != '':
		return (name, version) # This is more correct than the manifest version in many cases.

	try:
		f = open(path+os.sep+'META-INF'+os.sep+'MANIFEST.MF', 'rb').read()
		manifest_version = f.split('Manifest-Version:')[1]
		manifest_version = manifest_version.split('\n')[0].strip()
		if manifest_version != None and manifest_version != '':
			return (name, manifest_version)
	except IOError:
		pass

	return (name, '1.0')

def dependency(version, dir, file, groupId):
	extension = file.split('.')[-1]
	print 'Deploying', extension, 'file:', file
	ret = subprocess.call('cd '+dir+'; mvn deploy:deploy-file -DrepositoryId=connections-prereqs -Durl=http://artifactory.cwp.pnp-hcl.com/artifactory/connections-prereqs -DgroupId='+groupId+' -DartifactId='+file_to_artId(file)+' -Dversion='+version+' -Dpackaging='+extension+' -Dfile='+file+' -q', shell=True)
	#ret = subprocess.call('mvn install:install-file -DgroupId='+groupId+' -DartifactId='+artId+' -Dversion='+version+' -Dpackaging=jar -Dfile='+file+' -q', shell=True)
	if ret == 33280:
		raise Exception('Control-C')

def build_pom_file(version, files, groupId, artId):
	print 'Deploying pom.xml file for', groupId, 'encompassing', len(files), len(files)==1 and 'file.' or 'files.'
	os.system('rm pom.xml') # If we end in an invalid state (Control-C, etc) this file is corrupt. Maven will still try to read it, and then throws an error.
	f = open('pom.xml', 'wb')
	f.write('''
<project>
	<modelVersion>4.0.0</modelVersion>
	<groupId>'''+groupId+'''</groupId>
	<artifactId>'''+artId+'''</artifactId>
	<version>'''+version+'''</version>
	<packaging>pom</packaging>

	<repositories>
		<repository>
			<id>central</id>
			<name>Maven Repository Switchboard</name>
			<layout>default</layout>
			<url>http://repo1.maven.org/maven2</url>
			<snapshots>
			<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>

	<dependencies>''')
	for file in files:
		if file[1].split('.')[-1] not in valid_filetypes:
			continue
		name = file_to_artId(file[1])
		if name == artId: # Don't add dependencies for ourself.
			continue
		f.write('''
		<dependency>
			<groupId>'''+groupId+'''</groupId>
			<artifactId>'''+name+'''</artifactId>
			<version>'''+version+'''</version>
		</dependency>''')
	f.write('''
	</dependencies>
</project>''')
	f.close()
	ret = subprocess.call('mvn deploy:deploy-file -DrepositoryId=connections-prereqs -Durl=http://artifactory.cwp.pnp-hcl.com/artifactory/connections-prereqs -DgroupId='+groupId+' -DartifactId='+artId+' -Dversion='+version+' -Dpackaging=pom -DpomFile=pom.xml -Dfile=pom.xml -q', shell=True)
	#ret = subprocess.call('mvn install:install-file -DgroupId='+groupId+' -DartifactId='+artId+' -Dversion='+version+' -Dpackaging=pom -DpomFile=pom.xml -Dfile=pom.xml -e -q', shell=True)
	if ret == 33280:
		raise Exception('Control-C')

def file_to_artId(name):
	name, _ = get_version(name)
	if name.split('.')[-1] in valid_filetypes:
		name = name[:-4]
	return name

if __name__ == '__main__':
	import re, time, copy
	start_time = time.time()
	custom_names2 = {}

	maven_central = []
	try:
		f = open('maven_central.txt', 'rb').read()
		for match in re.findall('<groupId>(.*?)</groupId><artifactId>(.*?)</artifactId>', f):
			maven_central.append(match)
	except IOError:
		pass

	prereqs_dir = '/home/lcuser/sn.prereqs/lwp/prereqs.sn'
	os.chdir(prereqs_dir)
	master_pom = open('master_pom.xml', 'wb')
	master_pom.write('''
<project>
	<modelVersion>4.0.0</modelVersion>
	<groupId>prereqPom</groupId>
	<artifactId>prereqPom</artifactId>
	<version>1.0</version>
	<packaging>pom</packaging>

	<dependencies>
''')

	unique_packages = {}
	for dir in os.listdir(prereqs_dir):
		name, version = get_version(prereqs_dir + os.sep + dir)
		if name not in unique_packages:
			unique_packages[name] = {'version': version, 'dir': dir}
		else:
			if unique_packages[name]['version'] < version:
				unique_packages[name] = {'version': version, 'dir': dir}

	for name in unique_packages.keys():
		print 'Processing:', name
		os.chdir(prereqs_dir)
		dependencies = []
		for root, dirs, files in os.walk(unique_packages[name]['dir']):
			for file in files:
				if file.split('.')[-1] not in valid_filetypes:
					continue
				dependencies.append([root, file])
			try:
				group = custom_names[name]
				custom_names2[name] = custom_names[name]
			except KeyError, e:
				print 'KeyError:', e


		if len(dependencies) == 0:
			print 'WARNING: No files found for', name, '!'
			continue
		
		threads = []
		for file in dependencies:
			#dependency(unique_packages[name]['version'], file[1], group)
			thread = Thread(target=dependency, kwargs={
				'version': unique_packages[name]['version'],
				'dir': prereqs_dir+os.sep+file[0],
				'file': file[1],
				'groupId': group,
			})
			threads.append(thread)
			thread.start()
		for thread in threads:
			thread.join()

		build_pom_file(unique_packages[name]['version'], dependencies, group, name)
		master_pom.write('''
		<dependency>
			<groupId>'''+group+'''</groupId>
			<artifactId>'''+name+'''</artifactId>
			<version>'''+unique_packages[name]['version']+'''</version>
		</dependency>
''')

	master_pom.write('''
	</dependencies>
</project>
''')
	master_pom.close()
	os.chdir('/home/lcuser')
	subprocess.call('mvn deploy:deploy-file -DrepositoryId=connections-prereqs -Durl=http://artifactory.cwp.pnp-hcl.com/artifactory/connections-prereqs -DgroupId=prereqPom -DartifactId=preqreqPom -Dversion=1.0 -Dpackaging=pom -DpomFile=master_pom.xml -Dfile=master_pom.xml -q', shell=True)

	print 'Average time per group:', float(time.time()-start_time)/len(unique_packages)

