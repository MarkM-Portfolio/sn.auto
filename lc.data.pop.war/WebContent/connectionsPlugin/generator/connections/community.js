Blockly.Connections = Blockly.Generator.get('Connections');

Blockly.Connections.community_community = function() {
  // action block
  
  var hasMem;
  if(this.itemCount_ > 0){
  hasMem = "true";
  }else{
  hasMem = "false";
  }
  
  var openTag = '<community inc="title" title="'+this.getTitleText('TITLE')+'" content="'+this.getTitleText('SUM')+'" tagsString="'+this.getTitleText('TAGS')+'" hasMembers="'+hasMem+'" permission="'+this.getTitleText('VIS')+'">';
  
  var members = new Array(this.itemCount_);
  for (var n = 0; n < this.itemCount_; n++) {
    members[n] = Blockly.Connections.valueToCode(this, 'MEM' + n);
  }
  members = "\n"+ members.join("\n") +"\n";
  var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+members+ inside + "\n</community>\n";
};

Blockly.Connections.community_member = function() {
  // Text value.
  var code = '<member id="'+this.getTitleText('EMAIL')+'" type="member"/>';
  return [code];
};

Blockly.Connections.community_amy_member = function() {
  // Text value.  
  var code = "";
  for (var n = 1; n <= this.getTitleValue('NUM'); n++) {
    code = code + '\n<member id="amy jones'+n+'" type="member"/>';
  }
  return [code];
};