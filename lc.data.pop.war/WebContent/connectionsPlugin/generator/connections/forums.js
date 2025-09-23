Blockly.Connections = Blockly.Generator.get('Connections');

Blockly.Connections.forum_forum = function() {
  // action block
  var openTag = '<forum inc="title" title="'+this.getTitleValue('TITLE')+'" content="'+this.getTitleValue('CONTENT')+'">';

  var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+ inside + "\n</forum>";
};

Blockly.Connections.forum_topic = function() {
  // action block
  var openTag = '<forumtopic inc="title" title="'+this.getTitleText('TITLE')+'" content="'+this.getTitleText('CONTENT')+'" isPinned="'+this.getTitleValue('PIN')+'" isLocked="'+this.getTitleValue('LOK')+'" isQuestion="'+this.getTitleValue('Q')+'" isAnswer="'+this.getTitleValue('A')+'">';

  var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+ inside + "\n</forumtopic>";
};

Blockly.Connections.forum_reply = function() {
  // Text value.
  var code = '<forumreply inc="title" title="'+this.getTitleText('TITLE')+'" content="'+this.getTitleText('CONTENT')+'" isAnswer="'+this.getTitleValue('A')+'"/>';
  return code;
};