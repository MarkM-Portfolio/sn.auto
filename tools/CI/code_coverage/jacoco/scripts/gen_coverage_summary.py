import os
import sys
from lxml import etree

args = sys.argv[1:]
coverageXmlFilePath = args[0]
outputFilePath = args[1]

tree = etree.parse(coverageXmlFilePath)

xpath_to_find = "/report"
result = tree.xpath(xpath_to_find)
component = result[0].attrib['name']
print "Component: %s" %(component)
print "Build label: %s" %(os.environ['BUILD_LABEL'])
print "Job name: %s" %(os.environ['JOB_NAME'])

xpath_to_find = "/report/counter[@type='LINE']"
result =  tree.xpath(xpath_to_find)
covered_lines = result[0].attrib['covered']
missed_lines = result[0].attrib['missed']
total_lines = int(covered_lines) + int(missed_lines)

line_coverage = float(covered_lines)/float(total_lines)
print "Total lines: %s" %(total_lines)
print "Covered lines: %s" %(covered_lines)
print "Missed lines: %s" %(missed_lines)
print "Line coverage: %f" %(line_coverage)

f = open(outputFilePath, 'w')
f.write("Component: %s\n" %(component))
f.write("Build label: %s\n" %(os.environ['BUILD_LABEL']))
f.write("Job name: %s\n" %(os.environ['JOB_NAME']))
f.write("Total lines: %s\n" %(total_lines))
f.write("Covered lines: %s\n" %(covered_lines))
f.write("Missed lines: %s\n" %(missed_lines))
f.write("Line coverage: %f\n" %(line_coverage))
f.close()

