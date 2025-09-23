from com.ibm.lc.install import XmlFile

# getStats
#
# Parses a string from the coverage XML file
# the looks like:
#
#     <coverage type="class, %" value="100% (4/4)"/>
# Returns a list that contains the numerator (numHits)
# and denominator (numItems) of the fraction in
# parentheses.
def getStats(statString):
	index1 = statString.find('(')
	index2 = statString.find('/',index1)
	hits = statString[index1+1:index2]
	numHits = float(hits)
	
	index3 = statString.find(')',index2)
	items = statString[index2+1:index3]
	numItems = float(items)
	
	return [numHits, numItems]

def outputStats(statType, statList, outputFile):
	numHits = statList[0]
	numItems = statList[1]

	if numItems == 0:
		print "No items found for \"%s\"" %(statType)
		return
	else:
		resultPercent = (numHits / numItems) * 100.0
	outputString = "\t%s %d%% (%d/%d)" %(statType, resultPercent, numHits, numItems)
	print outputString
	outputFile.write('%s\n' %(outputString))
	return

componentPackageXmlFilePath = sys.argv[0]
coverageXmlFilePath = sys.argv[1]
outputFilePath = sys.argv[2]

componentPackageXmlFile = XmlFile(componentPackageXmlFilePath)
coverageXmlFile = XmlFile(coverageXmlFilePath)

xpath = "//component/@name"
componentList = componentPackageXmlFile.get(xpath)

statTypeList = ['class', 'method', 'block', 'line']
componentResultsDict = {}

for component in componentList:
	print "component: %s" %(component)
	
	componentResultsDict[component] = {statTypeList[0]:[0,0],statTypeList[1]:[0,0],statTypeList[2]:[0,0],statTypeList[3]:[0,0]}
	
	xpath = "//component[@name='%s']/package/@name" %(component)
	packageList = componentPackageXmlFile.get(xpath)
	
	if packageList == None:
		print "Skipping component %s..." %(component)
		continue
		
	for package in packageList:
		print "package: %s" %(package)

		#statTypeList = componentResultsDict[component].keys()
		for statType in statTypeList:
			xpath = "//package[starts-with(@name,'%s')]/coverage[contains(@type,'%s')]" %(package, statType)
			resultList = coverageXmlFile.get(xpath)
				
			if resultList == None:
				print "Skipping \"%s\" for package %s for component %s..." %(statType, package, component)
				continue
			
			for result in resultList:
				statList = getStats(result)
				numHits = statList[0]
				numItems = statList[1]
	
				individualComponentResultsDict = componentResultsDict[component]
				individualComponentResultsDict[statType][0] += numHits
				individualComponentResultsDict[statType][1] += numItems

outputFile = open(outputFilePath, 'w')

print 'Overall'
outputFile.write('Overall\n')
for statType in statTypeList:
	xpath = "//all[@name='all classes']/coverage[contains(@type,'%s')]" %(statType)
	result = coverageXmlFile.get(xpath)
				
	if result == None:
		print "Skipping overall summary for \"%s\"..." %(statType)
		continue
			
	statList = getStats(result[0])
	outputStats(statType, statList, outputFile)
	
for component in componentList:
	print "\n\n%s" %(component)
	outputFile.write('\n\n%s\n' %(component))
	
	individualComponentResultsDict = componentResultsDict[component]
	
	for statType in statTypeList:
		outputStats(statType, individualComponentResultsDict[statType], outputFile)

outputFile.close()

