Blockly.Connections = Blockly.Generator.get('Connections');

Blockly.Connections.tools_login = function() {
  // action block
  var openTag = '<action url="https://'+this.getTitleValue('URL')+'" uname="'+this.getTitleValue('UNAME')+'" password="'+this.getTitleValue('PWORD')+'">';

  var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+ inside + "\n</action>";
};

Blockly.Connections.tools_loop = function() {
  // action block
  var openTag = '<loop iteration="'+this.getTitleValue('NUM')+'"  inc="'+this.getTitleValue('INC')+'" start="'+this.getTitleValue('START')+'">';

  var inside = Blockly.Connections.statementToCode(this, 'action');

  return openTag +"\n"+ inside + "\n</loop>";
};