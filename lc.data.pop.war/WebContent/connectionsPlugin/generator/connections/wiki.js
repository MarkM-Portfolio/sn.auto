Blockly.Connections = Blockly.Generator.get('Connections');

Blockly.Connections.wiki_wiki = function() {
  // action block
  
  var hasMem;
  if(this.itemCount_ > 0){
  hasMem = "true";
  }else{
  hasMem = "false";
  }
  
  var openTag = '<wiki inc="title" title="'+this.getTitleText('TITLE')+'" summary="'+this.getTitleText('SUM')+'" tagsString="'+this.getTitleText('TAGS')+'" hasMembers="'+hasMem+'" isPublic="'+this.getTitleText('VIS')+'">';
  
  var members = new Array(this.itemCount_);
  for (var n = 0; n < this.itemCount_; n++) {
    members[n] = Blockly.Connections.valueToCode(this, 'MEM' + n);
  }
  members = "\n"+ members.join("\n") +"\n";
  var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+members+ inside + "\n</wiki>\n";
};

Blockly.Connections.wiki_member = function() {
  // Text value.
  var code = '<member id="'+this.getTitleText('EMAIL')+'" type="'+this.getTitleText('ROLE')+'"/>';
  return [code];
};

Blockly.Connections.wiki_amy_member = function() {
  // Text value.  
  var code = "";
  for (var n = 1; n <= this.getTitleValue('NUM'); n++) {
    code = code + '\n<member id="amy jones'+n+'" type="'+this.getTitleText('ROLE')+'"/>';
  }
  return [code];
};

Blockly.Connections.wiki_page = function() {
  // Text value.
  var code = '<wikipage inc="title" title="'+this.getTitleValue('TITLE')+'" content="'+this.getTitleValue('CONTENT')+'" tagsString="'+this.getTitleValue('TAG')+'"/>';
  return code;
};
