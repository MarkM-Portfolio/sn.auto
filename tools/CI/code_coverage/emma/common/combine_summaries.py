from com.ibm.lc.install import XmlFile

bodyString = ""

def getStats(statString):
	index1 = statString.find('(')
	index2 = statString.find('/',index1)
	numHits = statString[index1+1:index2]
	
	index3 = statString.find(')',index2)
	numItems = statString[index2+1:index3]
	
	return [numHits, numItems]

def getHits(statString):
	index1 = statString.find('(')
	index2 = statString.find('/',index1)
	hits = statString[index1+1:index2]
	return hits

def getPackageDict(filePath):
	coverageXmlFile = XmlFile(filePath)
	packageDict = {}

	# Need to get all the packages that are in this file.
	xpath = "/report/data/all/package/@name"
	packageList = coverageXmlFile.get(xpath)
	for packageName in packageList:
		classDict = {}
		packageDict[packageName] = classDict

		# Need to get all the classes that are in this package.
		xpath = "/report/data/all/package[@name='%s']//class/@name" %(packageName)
		classList = coverageXmlFile.get(xpath)
		if classList == None:
			print "Empty classList for package %s." %(packageName)
			continue
		
		for className in classList:
			# Need to see if this class got hit or not.
			xpath = "/report/data/all/package[@name='%s']//class[@name='%s']/coverage[contains(@type,'class')]" %(packageName, className)
			statList = coverageXmlFile.get(xpath)
			if statList == None:
				print "EMMA: WARNING: Empty statList for class %s in package %s." %(className, packageName)
				continue
		
			hits = getHits(statList[0])

			classDict[className] = {}
			methodDict = {}
			classDict[className] = (hits, methodDict)
		
			#Need to get all the methods that are in this class.
			xpath = "/report/data/all/package[@name='%s']//class[@name='%s']/method/@name" %(packageName, className)
			methodList = coverageXmlFile.get(xpath)
			if methodList == None:
				print "EMMA: Empty methodList for class %s in package %s." %(className, packageName)
				continue

			for methodName in methodList:
				# Need to see if this method got hit or not.
				xpath = "/report/data/all/package[@name='%s']//class[@name='%s']/method[@name='%s']/coverage[contains(@type,'method')]" %(packageName, className, methodName)
				statList = coverageXmlFile.get(xpath)
				if statList == None:
					print "EMMA: WARNING: Empty statList for method %s in class %s in package %s." %(methodName, className, packageName)
					continue
				
				hits = getHits(statList[0])
				methodDict[methodName] = hits

	return packageDict

#####

def combineSummaries(componentName, xmlFilePath1, xmlFilePath2):
	global bodyString
	packageDict1 = {}
	packageDict1 = getPackageDict(coverageXmlFilePath1)
	
	packageDict2 = {}
	if(xmlFilePath2 != None and len(xmlFilePath2) > 0):
		packageDict2 = getPackageDict(coverageXmlFilePath2)
	
	# Will have a "classHitsDict" dictionary and a "methodsHits" dictionary.
	# Then iterate through both package dictionaries, adding "hit" entries.
	# Maybe not efficient, but simple.

	classHitsDict = {}
	methodHitsDict = {}
	numClasses = 0
	numClassHits = 0
	numMethods = 0
	numMethodHits = 0

	packageDictList = [packageDict1, packageDict2]
		
	# Initialize all "hit" dictionaries
	for packageDict in packageDictList:
		packageNameList = packageDict.keys()
		for packageName in packageNameList:
			if not classHitsDict.has_key(packageName):
				classHitsDict[packageName] = {}
			
			if not methodHitsDict.has_key(packageName):
				methodHitsDict[packageName] = {}
			
			classDict = packageDict[packageName]
			classNameList = classDict.keys()
			for className in classNameList:
				if not methodHitsDict[packageName].has_key(className):
					methodHitsDict[packageName][className] = {}	

	for packageDict in packageDictList:
		packageNameList = packageDict.keys()
		for packageName in packageNameList:
			classDict = packageDict[packageName]			
			classNameList = classDict.keys()
			if classNameList == None:
				print "EMMA: WARNING: Empty classNameList for package %s" %(packageName)
				continue
	
			for className in classNameList:
				classTuple = classDict[className]
				classNameHitList = classHitsDict[packageName].keys()
		
				if (not className in classNameHitList):
					numClasses = numClasses + 1
				
				if (not className in classNameHitList) or (classHitsDict[packageName][className] == '0') :
					wasHit = classTuple[0]
					classHitsDict[packageName][className] = wasHit
					if wasHit == '1':
						numClassHits = numClassHits + 1
								
				methodDict = classTuple[1]
				methodNameList = methodDict.keys()
				if methodNameList == None:
					print "EMMA: WARNING: Empty methodNameList for class %s in package %s" %(className, packageName)
					continue
			
				for methodName in methodNameList:
					methodNameHitList = methodHitsDict[packageName][className].keys()
			
					if (not methodName in methodNameHitList):
						numMethods = numMethods + 1

					if (not methodName in methodNameHitList) or (methodHitsDict[packageName][className][methodName] == '0'):
						wasHit = methodDict[methodName]
						methodHitsDict[packageName][className][methodName] = wasHit
						if wasHit == '1':
							numMethodHits = numMethodHits + 1

	print "%s" %(component)
	bodyString = bodyString + "%s\n" %(component)

	classPercent = (float(numClassHits) / float(numClasses)) * 100.0
	print "\tclass %d%% (%s/%s)\n" %(classPercent, numClassHits, numClasses)
	bodyString = bodyString + "\tclass %d%% (%s/%s)\n\n" %(classPercent, numClassHits, numClasses)

	methodPercent = (float(numMethodHits) / float(numMethods)) * 100.0
	print "\tmethod %d%% (%s/%s)\n" %(methodPercent, numMethodHits, numMethods)
	bodyString = bodyString + "\tmethod %d%% (%s/%s)\n\n" %(methodPercent, numMethodHits, numMethods)
	
	retDict = {	'numClassHits':numClassHits,
				'numClasses':numClasses,
				'numMethodHits':numMethodHits,
				'numMethods':numMethods
			  }
			  
	return retDict

#####

coverageXmlFilePathsFilePath = sys.argv[0]
outputFilePath = sys.argv[1]

coverageXmlFilePathsFile = open(coverageXmlFilePathsFilePath, 'r')
outputFile = open(outputFilePath, 'w')

totalNumClassHits = 0
totalNumClasses = 0
totalNumMethodHits = 0
totalNumMethods = 0

coverageXmlFilePathsList = coverageXmlFilePathsFile.readlines()
coverageXmlFilePathsList.sort()
for line in coverageXmlFilePathsList:
	line = line.replace('\n', '')
	tokens = line.split(',')
	component = tokens[0]
	coverageXmlFilePath1 = tokens[1]
	coverageXmlFilePath2 = tokens[2]
	
	print "Processing component %s..." %(component)
	
	if (coverageXmlFilePath2 != None) and (len(coverageXmlFilePath2) > 0):
		statDict = combineSummaries(component, coverageXmlFilePath1, coverageXmlFilePath2)
	
		totalNumClassHits += statDict['numClassHits']
		totalNumClasses += statDict['numClasses']
		totalNumMethodHits += statDict['numMethodHits']
		totalNumMethods += statDict['numMethods']
	else:
		coverageXmlFile = XmlFile(coverageXmlFilePath1)
		xpath = "//all[@name='all classes']/coverage[contains(@type,'class')]"
		result = coverageXmlFile.get(xpath)
		statList = getStats(result[0])
		numClassHits = statList[0]
		numClasses = statList[1]
		totalNumClassHits += int(numClassHits)
		totalNumClasses += int(numClasses)
		
		xpath = "//all[@name='all classes']/coverage[contains(@type,'method')]"
		result = coverageXmlFile.get(xpath)
		statList = getStats(result[0])
		numMethodHits = statList[0]
		numMethods = statList[1]
		totalNumMethodHits += int(numMethodHits)
		totalNumMethods += int(numMethods)

		print "%s" %(component)
		bodyString = bodyString + "%s\n" %(component)

		classPercent = (float(numClassHits) / float(numClasses)) * 100.0
		print "\tclass %d%% (%s/%s)\n" %(classPercent, numClassHits, numClasses)
		bodyString = bodyString + "\tclass %d%% (%s/%s)\n\n" %(classPercent, numClassHits, numClasses)

		methodPercent = (float(numMethodHits) / float(numMethods)) * 100.0
		print "\tmethod %d%% (%s/%s)\n" %(methodPercent, numMethodHits, numMethods)
		bodyString = bodyString + "\tmethod %d%% (%s/%s)\n\n" %(methodPercent, numMethodHits, numMethods)

print "Overall"
outputFile.write("Overall\n")

classPercent = (float(totalNumClassHits) / float(totalNumClasses)) * 100.0
print "\tclass %d%% (%s/%s)\n" %(classPercent, totalNumClassHits, totalNumClasses)
outputFile.write("\tclass %d%% (%s/%s)\n\n" %(classPercent, totalNumClassHits, totalNumClasses))

methodPercent = (float(totalNumMethodHits) / float(totalNumMethods)) * 100.0
print "\tmethod %d%% (%s/%s)\n" %(methodPercent, totalNumMethodHits, totalNumMethods)
outputFile.write("\tmethod %d%% (%s/%s)\n\n" %(methodPercent, totalNumMethodHits, totalNumMethods))

outputFile.write(bodyString)
outputFile.close()
coverageXmlFilePathsFile.close()
