
# Runs the OSSC tool automatically against all the apps  
# Once the OSSC tool is finished running it splits the information into different files based on apps
# Apps,Blogs, Communities, DBoard, Dogear, Files, Forums, Help, Lconn, Metrics, Mobile, Moderation, News, Oa, Oembed, Profiles, Scee, Search, Widgets and Wikis
# Runs a second build scan and creates diff file, if user chooses  
# An email is sent to the desired recipients with result files and diff file 

# Variable Declarations
# Path Names as variable declarations 
osscPath="/local/ossc"
toolsDir="/home/lcuser/lc-update"
appPath="/opt/IBM/WebSphere/AppServer/java/bin/java"
kitPath="${osscPath}/sn.live/lwp/build/lc.live.installer//xkit/installableApps"
contactsPath="${osscPath}/contacts/lwp/build/contacts.ear/lib"
installableApps=`ls ${kitPath}`
appList=""
tempDir="/local/ossc/tmp/"
surveysPath="$tempDir/Surveys"
buildInfo=`cat ${osscPath}/xkit/version.txt`

# Converts application name into recognizable tool name 
parse_app() { 
 case $1 in
        "urlpreview")          appName="oembed.ear";;
        "activities")          appName="oa.ear";;
        "pushnotification")    appName="lconn.pushnotification.ear";;
        "homepage")            appName="dboard.ear";;
        "proxy")               appName="connections.proxy.ear";;
        "common")              appName="connections.common.ear";;
        "install")             appName="";;
    esac
    
    if [[ $1 != *.ear ]] && [[ $1 != "install" ]] && [[ $1 != "surveys" ]]
      then
        appName=${1}.ear
    elif [[ $1 == "install" ]] || [[ $1 == "surveys" ]] 
    then 
      appName=""
    else 
      appName=$1
     fi 

} 

# Runs the OSSC tool and puts the results into different files with the app name. (Blogs= blogs.ear.txt)
run_command () {
for app in $installableApps
  do   
   parse_app $app
   $appPath -classpath $osscPath/ossc.jar:$osscPath/commons-cli-1.1.jar com/ibm/wplc/tools/ossc/VerifyModules -approvedJars $osscPath/approved-modules -ignoreFile $osscPath/ignore-modules.list -hits -ignores -ibm -fullPath -hash -targets ${kitPath}/${appName} | tee $osscPath/${appName}.${1}.txt

# Checks that build succeeded to create applist. Does not create a list of all files if build does not succeed.  
if [ "$?" -eq "0" ] 
then
  appList="-a ${appName}.${1}.txt ${appList}"
else 
  exit 1
fi 
done
} 

# Emails user selected recipients the scan results and diff if applicable 
# Subject of includes build version and the date of the scan 
send_email () { 
echo "" | mutt -s "Results for OSSC Build: $build,$compare on $(date)" ${appList} -- ${email//,/ } 
}

# Gets the build number/version. 
# Gives error message and exits program if build is not found. 
# If build is Surveys build  
get_build () {

if [[ "${1}" != *Surveys* ]] 
then 
  ${toolsDir}/bin/get-build.sh -b ${1}
    if [ "$?" -ne "0" ] 
    then 
      echo "Failed to get the build"
      exit 1
     fi  
else 
  mkdir $surveysPath
  cd $surveysPath
  
  cp /net/mlsa2/ibm/releng/LotusLive/dailybuilds/${1%_*}/${1}/WebKits/SC-AC-Connections-Surveys-Media-*.rpm .
  chmod 755 ./SC-AC-Connections-Surveys-Media-*.rpm
  rpm2cpio ./SC-AC-Connections-Surveys-Media-*.rpm | cpio -idmv
  if [ "$?" -ne "0" ] 
  then 
    echo "Failed to get build" 
    exit 1
  fi 
  cd $osscPath 
fi
buildInfo=`cat ${osscPath}/xkit/version.txt`
}

# Checks to see that user provides an email for the files to be sent to. If not it gives an error message 

if [ $# -lt 2 ]
  then
    echo "Please provide an email address"  
    exit 1
fi
# Parameters for the build (-e=email, -a=activities, -b=build, -c=compare build) 
# If build is not provided, gives an error message 
while [ -n "$1" ]
  do
     case $1 in 
        "-e")    email="$2"
                 shift 2;;
        "-a")    apps="$2"
                 shift 2;;
        "-b")    build="$2"
                 shift 2;;
        "-c")    compare="$2"
                 shift 2;;
         *)      echo "Error" 
                 exit 1;;
     esac
  done

if [ ! -z "$apps"  ] 
  then 
    installableApps=${apps//,/ } 
fi

# Checks to see if a temporary folder exists and creates one if it does not exist  
if [ ! -d "$tempDir" ]
  then
    mkdir $tempDir
fi 

# Checks to see if there are two builds 
# If there are two builds, scans both of them and creates a diff file  
compare_builds () { 
    echo "InstallableApps $installableApps"
    for apps in $installableApps
    do 
      parse_app $apps
      appsName=${appName}
    
    if [ ! -f ${appName}.${compare}.txt ]
    then      
        run_scan $compare          
    fi
     
   diff -u ${appsName}.${build}.txt ${appsName}.${compare}.txt >> build_differences.txt
   done   
   appList="-a build_differences.txt ${appList}"
}

# Checks to see the type of build- Contacts or Connections 
run_scan () { 
cd ${osscPath} 
 get_build $1  
 if [[ "${1}" == *Contacts* ]]
 then 
   installableApps=`ls ${contactsPath}`
   kitPath=$contactsPath
 elif [[ "${1}" == *Install* ]]
 then
   installableApps="install"  
   kitPath="/local/IBM_Connections_Install"
   ##Remove application files
   rm -fr /local/IBM_Connections_Install/IBMConnections/native/product_*
  elif [[ "${1}" == *Surveys* ]] 
  then 
  installableApps="surveys" 
  kitPath=$surveysPath/opt/ll/apps/surveys
 fi 
 run_command $1
} 

# Makes sure that build is provided and runs the scan if it is 
# Removes previous build_differences.txt     
# If two builds are provided runs scan a second time and creates a diff file (build_differences.txt)  
if [ ! -z "$build" ] 
then  
 if [ -f build_differences.txt ]
 then
   rm build_differences.txt
 fi
run_scan $build 
if [ ! -z "$compare" ] 
then 
  compare_builds 
fi 
fi

# If build is successful, sends an email with the scan results and diff file   
send_email
rm -fr $surveysPath
