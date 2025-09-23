Blockly.Connections = Blockly.Generator.get('Connections');

Blockly.Connections.blog_blog = function() {
  // action block
  var openTag = '<blog inc="handle" title="'+this.getTitleText('TITLE')+'" handle="'+this.getTitleText('HANDLE')+'" tagsString="'+this.getTitleText('TAG')+'" summary="'+this.getTitleText('SUM')+'" numDaysCommentsAllowed="'+this.getTitleText('NUM')+'" allowedComments="'+this.getTitleValue('COM')+'" commentModerated="'+this.getTitleValue('MOD')+'">';

  var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+ inside + "\n</blog>";
};

Blockly.Connections.blog_entry = function() {
  // action block
  var openTag = '<blogentry inc="title" title="'+this.getTitleText('TITLE')+'" content="'+this.getTitleText('CONTENT')+'" tagsString="'+this.getTitleText('TAG')+'" allowedComments="'+this.getTitleValue('COM')+'" numDaysCommentsAllowed="'+this.getTitleValue('NUM')+'">';

  var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+ inside + "\n</blogentry>";
};

Blockly.Connections.blog_comment = function() {
  // Text value.
  var code = '<blogcomment inc="content" content="'+this.getTitleText('CONTENT')+'"/>';
  return code;
};