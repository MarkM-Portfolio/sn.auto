require 'nokogiri'

def combineResults(file1, file2, file3)
  doc1 = Nokogiri::XML(File.open(file1))
  doc2 = Nokogiri::XML(File.open(file2))

  tests2 = doc2.xpath("//test")
  tests2.each do |test2|
    test2.xpath("class/test-method[@status='PASS'][not(@is-config)]").each do |method|
      name = method.attribute("name").value
      doc1.xpath("//test/class/test-method[@name='"+name+"']").first.replace(method)
    end
  end
  File.open(file3, 'w') { |file| file.write(doc1.to_xml)}
end

if ARGV.count != 3
  abort("Expecting 3 arguments <Results1, Results2, output>")
end

combineResults(ARGV[0], ARGV[1], ARGV[2])
