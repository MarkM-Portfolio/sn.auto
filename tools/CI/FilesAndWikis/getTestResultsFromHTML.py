
def getCount(line):
	count = line.replace('<td>', '').replace('</td>', '').strip()
	return count

testResultsFilePath = sys.argv[0]
outputFilePath = sys.argv[1]

testResultsFile = open(testResultsFilePath, 'r')
outputFile = open(outputFilePath, 'w')

lineList = testResultsFile.readlines()
		
foundTestCount = 0
foundErrorCount = 0
foundFailureCount = 0

for i in range(len(lineList)):
	if not foundTestCount:
		if lineList[i].find('Test Count:') == -1:
			continue
		else:
			testCount = getCount(lineList[i+1])
			outputFile.write('testCount:%s\n' %(testCount))
			foundTestCount = 1
			continue
	
	if not foundErrorCount:
		if lineList[i].find('Error Count:') == -1:
			continue
		else:
			errorCount = getCount(lineList[i+1])
			outputFile.write('errorCount:%s\n' %(errorCount))
			foundErrorCount = 1
			continue
	
	if not foundFailureCount:
		if lineList[i].find('Failure Count:') == -1:
			continue
		else:
			failureCount = getCount(lineList[i+1])
			outputFile.write('failureCount:%s\n' %(failureCount))
			foundFailureCount = 1
			continue
	
testResultsFile.close()
outputFile.close()
