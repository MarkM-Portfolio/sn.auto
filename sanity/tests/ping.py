import sys

#when looking for operations in wsadmin use: print Help.operations(<full object name>)

#each digit represents a test group
#0 means everything went alright, 1 is bad... something went wrong
returnCode = '00000'
output = []

output.append('PING WEB SERVERS:')

webServers = AdminTask.listServers('[-serverType WEB_SERVER]').splitlines()
ws = AdminControl.queryNames('type=WebServer,*').splitlines()
w = ws[0]
for server in webServers:    
    props = server.replace('(','/').replace(')','').split('/')
    httpServer = '['+props[2]+' '+props[4]+' '+props[0]+']'
    #AdminControl.invoke('WebSphere:name=WebServer,process=dmgr,platform=common,node=ictools2CellManager01,version=8.0.0.6,type=WebServer,mbeanIdentifier=WebServer,cell=ictools2Cell01,spec=1.0', 'ping', '[ictools2Cell01 theDaVinciNode webserver1]', '[java.lang.String java.lang.String java.lang.String]') 
    ac = AdminControl.invoke(w, 'ping', httpServer, '[java.lang.String java.lang.String java.lang.String]')
    theOutput = props[0] + ': ' + ac 
    output.append(theOutput)
    if ac!='RUNNING':
        returnCode = returnCode[:4] + '1'

output.append('CHECK NODE SYNCHRONIZATION:')

nodeList = AdminTask.listNodes().splitlines()
mNode = AdminControl.getNode()
nodeList.remove(mNode)
cell = AdminControl.getCell()
output.append(mNode + ': DEPLOYMENT MANAGER NODE')
for node in nodeList:
    #node could be anything but the deployment manager at this point
    if AdminConfig.getid("/Cell:" + cell + "/Node:" + node + "/Server:nodeagent")!='':
        #node has a nodeAgent, so it is managed
        fullNodeName = AdminControl.completeObjectName('type=NodeSync,node=' + node + ',*')    
        if fullNodeName!='':
            status = AdminControl.invoke(fullNodeName,'isNodeSynchronized')        
            if status=='true':
                output.append(node + ': SYNCHRONIZED')
            else:
                output.append(node + ': NOT SYNCHRONIZED')
                returnCode = returnCode[:3] + '1' + returnCode[4]
        else:
                output.append(node + ': NOT SYNCHRONIZED')
                returnCode = returnCode[:3] + '1' + returnCode[4]
    else:
        output.append(node + ': UNMANNAGED NODE')
    


output.append('PING CLUSTERS:')

cellName = AdminControl.getCell()
#AdminConfig.list('ServerCluster', AdminConfig.getid( '/Cell:' + cellName + '/')) 
clusters = AdminControl.queryNames('type=Cluster,*').splitlines()

for cluster in clusters:
    props = cluster.replace('(','/').replace(')','').split(',') 
    status = AdminControl.getAttribute(cluster,'state')
    if status=='websphere.cluster.running':
        output.append(props[0][props[0].index('=')+1:] + ': RUNNING')
    else:
        output.append(props[0][props[0].index('=')+1:] + ': NOT RUNNING')
        returnCode = returnCode[:2] + '1' + returnCode[3:]
                                     
output.append('PING APPLICATION SERVERS:')
    
serverList = AdminTask.listServers('[-serverType APPLICATION_SERVER ]').splitlines()
for s in serverList:
    props = s.replace('(','/').replace(')','').split('/')
    server = AdminControl.completeObjectName('cell='+props[2]+',node='+props[4]+',name='+props[0]+',type=Server,*')
    if server!='':
        output.append(AdminControl.getAttribute(server,'name') + ': ' + AdminControl.getAttribute(server,'state'))
    else:
        output.append(props[0]+': NOT STARTED')
        returnCode = returnCode[0] + '1' + returnCode[2:]

output.append('PING APPLICATIONS:')

#get the names of all the apps
appList = AdminApp.list().splitlines()

for app in appList:
    #returns '' if it isn't running
    appManager = AdminControl.queryNames('type=Application,name='+app+',*').splitlines()
    if len(appManager)>0:
        output.append(app + ': RUNNING')
    else:
        output.append(app + ': NOT RUNNING')
        returnCode = '1' + returnCode[1:]
#make it return output
output.append(returnCode)
for item in output:
    print item