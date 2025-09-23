from com.ibm.lc.install import XmlFile
import glob

def getStats(statString):
	index1 = statString.find('(')
	index2 = statString.find('/',index1)
	hits = statString[index1+1:index2]
	numHits = float(hits)
	
	index3 = statString.find(')',index2)
	items = statString[index2+1:index3]
	numItems = float(items)
	
	return [numHits, numItems]

def outputStats(statType, statList):
	numHits = statList[0]
	numItems = statList[1]

	if numItems == 0:
		print "No items found for \"%s\"" %(statType)
		return
	else:
		resultPercent = (numHits / numItems) * 100.0
	retString = "\t%s %d%% (%d/%d)\n" %(statType, resultPercent, numHits, numItems)
	print retString
	return retString

coverageXmlFileDir = sys.argv[0]
usePatternFlag = sys.argv[1]
fileNamePattern = sys.argv[2]
outputFilePath = sys.argv[3]

if usePatternFlag == 'true':
	coverageXmlFilePattern = "%s/*%s*.xml" % (coverageXmlFileDir, fileNamePattern)
else:
	coverageXmlFilePattern = "%s/*.xml" % (coverageXmlFileDir)

coverageXmlFiles = glob.glob(coverageXmlFilePattern)
coverageXmlFiles.sort()
if len(coverageXmlFiles) < 1:
	raise Exception, "Unable to find xml files in %s with pattern: %s" %(coverageXmlFileDir, fileNamePattern)
	
statTypeList = ['class', 'method', 'block', 'line']
overallResultsDict = {statTypeList[0]:[0,0],statTypeList[1]:[0,0],statTypeList[2]:[0,0],statTypeList[3]:[0,0]}

outputFile = open(outputFilePath, 'w')

if usePatternFlag == 'true':
	outputFile.write('\nSummary Coverage Report for %s\n\n' %(fileNamePattern))
else:
	outputFile.write('\nSummary Coverage Report\n\n')

bodyString = ""
for coverageXmlFilePath in coverageXmlFiles:
	print "coverageXmlFilePath: %s" %(coverageXmlFilePath)
	tokens = coverageXmlFilePath.split('/')
	
	coverageXmlFileName = tokens[len(tokens) - 1]
	tokens = coverageXmlFileName.split('_')
	component = tokens[0]
	coverageXmlFile = XmlFile(coverageXmlFilePath)

	print '\n%s' %(component)
	bodyString = bodyString + '\n%s\n' %(component)
	for statType in statTypeList:
		xpath = "//all[@name='all classes']/coverage[contains(@type,'%s')]" %(statType)
		result = coverageXmlFile.get(xpath)
				
		if result == None:
			print "Skipping overall summary for \"%s\"..." %(statType)
			continue
			
		statList = getStats(result[0])

		numHits = statList[0]
		numItems = statList[1]
	
		overallResultsDict[statType][0] += numHits
		overallResultsDict[statType][1] += numItems
		
		statString = outputStats(statType, statList)
		bodyString = bodyString + statString

summaryString = '\nOverall\n'
print summaryString
outputFile.write(summaryString)
for statType in statTypeList:
	summaryString = outputStats(statType, overallResultsDict[statType])
	outputFile.write(summaryString)

outputFile.write(bodyString)
outputFile.close()
