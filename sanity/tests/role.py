import sys
import string

returnCode = '00'
output = []

output.append('CHECKING FOR COGNOS ADMIN:')

aliases = AdminTask.listAuthDataEntries().splitlines()

for alias in aliases:
    entry = alias.split('] [')   
    if string.find(entry[0],'cognosAdmin')!=(-1):
        tempId = entry[1].split(' ')
        tempIdList = tempId[1:]
        if (len(tempIdList)>1 and tempId[1]!=''):
            cogAd = ' '.join(tempIdList)
            if (cogAd[0] == "[") and (cogAd[-1] == "]"):
                cogAd = cogAd[1:-1]
            output.append('COGNOS ADMIN '+cogAd+' FOUND')
        else:
            output.append('NO COGNOS ADMIN FOUND')

output.append('CHECKING FOR CONNECTIONS ADMIN:')

userId = ''
for alias in aliases:
    entry = alias.split('] [')   
    if string.find(entry[0],'connectionsAdmin')!=(-1):
        tempId = entry[1].split(' ')
        tempIdList = tempId[1:]
        if len(tempIdList)>1:
            userId = ' '.join(tempIdList) 
            if (userId[0] == "[") and (userId[-1] == "]"):
                userId = userId[1:-1]
        else:
            userId = tempId[1]
                
#we find a userId
if userId!='':
    output.append('USERID '+userId+' FOUND')
    
    #list the applications
    applications = AdminApp.list().splitlines() 
    #go to each application and search for the roles
    for app in applications:
        view = AdminApp.view(app).splitlines()
        
        #two scenarios...
        #either we are looking at search or apps that aren't search
        #if we're looking at the search app we only car about the mapping to "admin"
        #if we're in any other app, then we care about "search-admin" and "dsx-admin"
        
        #if we're looking at any app other than search
        if app != "Search":
            theOutput = ': '
            outputTracker = 0
            for idx in range(len(view)):
                #check for the roles search-admin or dsx-admin
                if (view[idx]==('Role:  search-admin') or view[idx]==('Role:  dsx-admin')):
                    outputTracker=outputTracker+1
                    searchAdmin = view[idx+3].split('  ')
                    if searchAdmin[1].find(userId) == -1:
                        returnCode = '1'+returnCode[1]                   
                        #give feedback to the user
                        if outputTracker<2:
                            if view[idx]=='Role:  search-admin':
                                theOutput+='MUST MAP TO SEARCH-ADMIN '
                            else:
                                theOutput+='MUST MAP TO DSX-ADMIN '
                        else:
                            if view[idx]=='Role:  search-admin':
                                theOutput+='AND MUST MAP TO SEARCH-ADMIN'
                            else:
                                theOutput+='AND MUST MAP TO DSX-ADMIN'
                    else:
                        if outputTracker<2:
                            #output.append(app+': '+userId+' IS MAPPED CORRECTLY')
                            if view[idx]=='Role:  search-admin':
                                theOutput+='MAPS TO SEARCH-ADMIN '
                            else:
                                theOutput+='MAPS TO DSX-ADMIN '
                        else:
                            if view[idx]=='Role:  search-admin':
                                theOutput+='AND MAPS TO SEARCH-ADMIN '
                            else:
                                theOutput+='AND MAPS TO DSX-ADMIN '
            if theOutput!=': ':
                output.append(app+theOutput)
        #if we're looking at search
        else:
            #'Role:  admin' exists twice in the output and we want the first one
            #this counter keeps track so we return the right one
            outputTracker = 1
            for idx in range(len(view)):
                #check for the admin role
                if (view[idx]==('Role:  admin') and outputTracker==1):
                    outputTracker=outputTracker+1
                    searchAdmin = view[idx+3].split('  ')
                    if searchAdmin[1].find(userId) == -1:
                        returnCode = '1'+returnCode[1]                   
                        #give feedback to the user
                        output.append(app+': MUST MAP TO ADMIN')
                    else:
                        output.append(app+': MAPS TO ADMIN')
                elif (view[idx]==('Role:  admin') and outputTracker==1):
                        outputTracker=2

#we don't find a userId
else:
    output.append('NO USERID FOUND FOR CONNECTIONS ADMIN')
    returnCode = returnCode[0]+'1'
#make it return output
output.append(returnCode)
for item in output:
    print item