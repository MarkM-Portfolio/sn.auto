Blockly.Connections = Blockly.Generator.get('Connections');

Blockly.Connections.bookmark_bookmark = function() {
  // Text value.
  var code = '<bookmark inc="title" title="'+this.getTitleText('TITLE')+'" content="'+this.getTitleText('CONTENT')+'" tagsString="'+this.getTitleText('TAG')+'" linkHref="'+this.getTitleText('LINK')+'"/>';
  return code;
};