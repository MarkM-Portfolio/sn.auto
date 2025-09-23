Blockly.Connections = Blockly.Generator.get('Connections');

Blockly.Connections.activity_activity = function() {
  // action block
  
  var hasMem;
  if(this.itemCount_ > 0){
  hasMem = "true";
  }else{
  hasMem = "false";
  }
  
  var openTag = '<activity inc="title" title="'+this.getTitleValue('TITLE')+'" content="'+this.getTitleValue('CONTENT')+'" isComplete="false" dueDate="'+this.getTitleValue('DATE')+'" tagsString="'+this.getTitleValue('TAGS')+'" isPrivate="'+this.getTitleValue('VIS')+'" isCommunityActivity="false" hasMembers="'+hasMem+'">';

  var members = new Array(this.itemCount_);
  for (var n = 0; n < this.itemCount_; n++) {
    members[n] = Blockly.Connections.valueToCode(this, 'MEM' + n);
  }
  members = "\n"+ members.join("\n") +"\n";
  var inside = Blockly.Connections.statementToCode(this, 'action');
  return openTag +"\n"+members+ inside + "\n</activity>\n";
};

Blockly.Connections.activity_member = function() {
  // Text value.
  var code = '<member id="'+this.getTitleText('EMAIL')+'" type="member"/>';
  return [code];
};

Blockly.Connections.activity_amy_member = function() {
  // Text value.  
  var code = "";
  for (var n = 1; n <= this.getTitleValue('NUM'); n++) {
    code = code + '\n<member id="ajones'+n+'@janet.iris.com" type="member"/>';
  }
  return [code];
};

Blockly.Connections.activity_entry = function() {
  // action block
  var openTag = '<activityEntry inc="title" title="'+ this.getTitleValue('TITLE') +'" content="'+this.getTitleValue('CONTENT')+'" tagsString="'+this.getTitleValue('TAGS')+'" position="1" isTemplate="false" isPrivate="'+this.getTitleValue('VIS')+'">';

    var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+ inside + "\n</activityEntry>\n";
};

Blockly.Connections.activity_section = function() {
  // action block
  var openTag = '<section inc="title" title="'+ this.getTitleValue('TITLE') +'" position="1">';

   var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+ inside + "\n</section>\n";
};

Blockly.Connections.activity_todo = function() {
  // action block
  var openTag = '<todo inc="title" title="'+ this.getTitleValue('TITLE') +'" content="'+this.getTitleValue('CONTENT')+'" tagsString="'+this.getTitleValue('TAGS')+'" isComplete="false" isPrivate="'+this.getTitleValue('VIS')+'" position="1">';

    var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+ inside + "\n</todo>\n";
};

Blockly.Connections.activity_reply = function() {
  // action block
  var openTag = '<reply inc="title" title="'+ this.getTitleValue('TITLE') +'" content="'+this.getTitleValue('CONTENT')+'" isPrivate="'+this.getTitleValue('VIS')+'" position="1">';

    var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+ inside + "\n</reply>\n";
};