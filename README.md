**IC10 -** [Build Status](https://jenkins.cwp.pnp-hcl.com/cnx-non-fed/job/Core/job/Pink/job/sn.auto/job/sn.auto/job/master/)
# Connections BVT Suite
This package includes both the GUI and API BVT tests.

## Create workspace
Start with [gradle.build.readme](https://git.cwp.pnp-hcl.com/connections/sn.auto/blob/master/gradle.build.readme) for Gradle and Eclipse setup.

## Build
Also outline in [gradle.build.readme](https://git.cwp.pnp-hcl.com/connections/sn.auto/blob/master/gradle.build.readme) 

Build command: `gradle build`

Build will create a zip file under `build/distribution/bvt.zip`, it will also create individual jars inside each project in `<project>/build/libs/<jar>`

## Run GUI Tests
If running on Windows, install [Visual Studio C++ Redistributable 2015-2019](https://support.microsoft.com/en-us/help/2977003/the-latest-supported-visual-c-downloads).
While most test cases can be run on Mac, it is not officially supported. We recommend to either run it on Windows or use the Selenoid Grid.

Supported browsers: Firefox (v94 certified), Chrome (v95 certified), MS Edge (v95 certified).

### From Zip
- Extract zip file built above
- Make a copy `test_config/testTemplate.xml` to the root of the extracted directory
- Update testTemplate.xml to set server to test against, test selection and test level (groups)
  * `browser_url` - server to run tests against
  * `server_is_grid_hub` - `true` to run on Selenoid Grid, `false` to run locally.
  * `browser_start_command` - *browser_version_os*  
  (eg. `ff_0_Windows` will use whatever is the latest Firefox installed on your Windows system, set it to `ff_0_Linux` if running on Selenoid Grid)
  * `groups` - test group(s) to execute, can include and exclude groups. Some available groups: *level2*, *mtlevel2*, *cplevel2*
  * `<test></test>` sections - set `enable="true"` on the components you wish to test, add more if necessary.
- **Execute command: `java -jar bvt.gui-IC10-SNAPSHOT.jar -HCLTemplate testTemplate.xml`**
- Test result report is in *test-output/html/index.html*

(NO LONGER SUPPORTED) If sending tests to BrowserStack:
- Set testTemplate.xml:
	* `server_is_browserstack` - set to true if tests should run on BrowserStack
	* `browserstack_username`, `browserstack_key` - BrowserStack username and password
- Set test_config/browserstack/browserstackconfig.properties:
	* `deleteBSArtifacts` - true if test artifacts (eg. video, logs) should be preserved on BrowserStack.com.  You will need to delete them manually to clean up. 


### From Workspace
Requires TestNG 7.4 Eclipse plugin
- From Eclipse, Help -> Install New Software, paste `https://testng.org/testng-eclipse-update-site/7.4.0/` in "Work with". Press Enter, select "TestNG" then follow the wizard.
- Open `bvt.gui/test_config/testTemplate.xml`, configure it same as in running from zip section above.
- Right click testTemplate.xml -> Run As -> TestNG Suite.  The test will run.
- In order to generate html log for individual test, right click testTemplate.xml again -> Run As -> Run Configuration -> Classpath tab.  Select User Entries then click Advanced -> Add Folders to add `bvt.gui/test_config/log`. Click OK, Apply, Close.
- Test result report is in *bvt.gui/test-output/html/index.html*

## Common problems

- Eclipse is showing compile errors on the top level, but there are no errors in the code - might happen on first time import.
  - Solution: Delete project from Eclipse (only eclipse, not disk) and re-import
- Multiple compile errors in source code and "Project and External Dependencies" (inside each project) does not contain jars - gradle.properties is missing or contains incorrect credentials, or no access to [Artifactory connections-prereqs repo](https://artifactory.cwp.pnp-hcl.com/artifactory/connections-prereqs/)
  - Solution: Make sure to follow "Build" section steps to create gradle.properties with correct credential. Contact Chris Griffin if need access to connections-prereqs.
  
## Selenoid Grid
* [Dashboard](http://testautolb.cnx.cwp.pnp-hcl.com:8080/)
* Click the target test to see live browser running the job.  Click the "lock" icon on top left to unlock the session to interact with the browser.

## Run REST Assured API Tests
Setup and Run instructions are basically the same as the GUI steps above except the following:
### From Zip
- **Execute command: `java -jar bvt.restassured.api-IC10-SNAPSHOT.jar -HCLTemplate testTemplate.xml`**

### From Workspace
- Visual Studio C++ Redistributable is not needed even for Windows.
- Use test template xml in subproject `bvt.restassured.api`, configure it same as in running from zip section above.
- Right click the xml -> Run As -> TestNG Suite.  The test will run.
- In order to generate html log for individual test, right click the xml again -> Run As -> Run Configuration -> Classpath tab.  Select User Entries then click Advanced -> Add Folders to add `bvt.restassured.api/test_config/log`. Click OK, Apply, Close.
- Test result report is in *bvt.restassured.api/test-output/html/index.html*

## Coding Best Practices
[GUI Automation Framework Coding Standards](https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T529)

## Legacy API BVT and the Data Population Tool
This is the new API framework, not the Janet tool.

`java -jar bvt.api.jar http://yourserver.com/`

 * Test Users are recorded in ./resources/ProfileData_{BVTserver}.properties inside bvt.api.jar

 * Get a properties file, such as ProfileData_lc45linux1.properties, as sample.  Create a ProfileData_{yourserver}.properties

 * ProfileData_{yourserver}.properties should be under ./resources inside or outside bvt.api.jar

 * For AS Search API, modify ./resources/ASSearch_crawler.properties for Connections admin user/password

This will run all the tests and data population against the specified server. 

### (old material, have not attempted since HCL move) Admin Tests
```
cd bvt.adm2
export WAS_HOME=/opt/IBM/WebSphere/Appserver
export SERVER_HOST=lc45linux.cwp.pnp-hcl.com
export SERVER_SOAP=8880
export ADMIN_USER=wasadmin
export ADMIN_PSWD=passw0rd
export IC_ADMIN_DIR=/opt/IBM/WebServer/profiles/AppSrv01/bin
./runtest.sh
```


