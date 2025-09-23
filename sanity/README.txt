			
BEFORE RUNNING:

create your config file

	- example config file available at: <path to sanity>/sanity/config.json

		this file acts as the default config file and can be edited instead of creating a new file

	- review parameters, change values to match those of your system

		this means specifying file paths, user names, passwords, etc.

		parameters to note:

			ldap.java-	location of installed java on system
					should be a folder containing the jre
					ex: "ldap.java":"/home/lcuser/jre1.7.0_25/bin/java"

			db2.servers-	this is an array of servers
					each json element should contain the fields listed in the example server
					you should have a json element for each server

		array of tests- this comes after the parameters

			changing tests is optional- we suggest you run them all unless you are sure you don't need them

			tests can be removed or added to the array as needed

			tests execute in the order they are listed

			in addition, you can specify children and dependencies for each test

HOW TO RUN:

two options to run: command line or web browser

command line

	- navigate to location of cmd_frontend.py
		found in: <path to sanity>/sanity/

	- run cmd_frontend.py

		specify the path to python 2.6 or greater either at the beginning of the command or by path variable

		to specify a test or group of tests, use the -t flag and list the tests you wish to run, separated by commas

		once it starts, supply the path to your config file (default path is to conf.json, in the same directory as cmd_frontend.py)

		in addition to onscreen output, output can be viewed in the log files: <path to sanity>/sanity/testresults.json

web interface

	- navigate to location of web_frontend.py
		found in: <path to sanity>/sanity/

	- run web_frontend.py
		specify the path to python 2.6 or greater either at the beginning of the command or by path variable

	- open browser, navigate to localhost:8000

	- from here, you can run the tool or review previous results
