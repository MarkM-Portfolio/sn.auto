folder = ARGV[0]
path = File.join(folder, "overview.html")
result = false
files = []
start = false
file = File.open(path, "rb")
file.each { |line|
  if line.include? "columnHeadings"
    start = true
    next
  end
  if line.include? "href" and start
    files << line.scan(/"([^"]*)"/).first.first
  end
}
files.each{|f|
  content = File.open(File.join(folder, f), "rb") { |f| f.read }
  result = result | content.include?("UnreachableBrowserException") | content.include?("Error forwarding the new session") | content.include?("Process timed out after waiting") | content.include?("Unable to bind to locking port") | content.include?("Failed to create a thread: retVal") | content.include?("There is not enough space on the disk") | content.include?("Cannot create temp directory") | content.include?("Error opening socket to server")
}
puts result
