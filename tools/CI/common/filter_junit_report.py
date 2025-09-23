import sys, shutil

file_in = sys.argv[1]
file_out = 'test_result_xml_file_filtered.xml'

fin = open(file_in, 'r')
fout = open(file_out, 'w')

print "Filtering %s..." %(file_in)
for line in fin:
    fout.write(line)
    if line.find('</testsuite>') != -1:
        break
fin.close()
fout.close()

print "Copying filtered file in place..."
shutil.copy(file_out, file_in)
