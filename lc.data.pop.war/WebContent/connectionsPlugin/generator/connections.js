Blockly.Connections = Blockly.Generator.get('Connections');

Blockly.Connections.init = function() {
  //set up operations.
  };
  
Blockly.Connections.finish = function(code) {
	
	//closing operations
  return "<root>\n" + code + "\n</root>"
};
  
  
  //copied from the javascript generator, don't understand yet 
  /**
 * Common tasks for generating JavaScript from blocks.
 * Handles comments for the specified block and any connected value blocks.
 * Calls any statements following this block.
 * @param {!Blockly.Block} block The current block.
 * @param {string} code The JavaScript code created for this block.
 * @return {string} JavaScript code with comments and subsequent blocks added.
 * @private
 */
Blockly.Connections.scrub_ = function(block, code) {
  if (code === null) {
    // Block has handled code generation itself.
    return '';
  }
  
  var nextBlock = block.nextConnection && block.nextConnection.targetBlock();
  var nextCode = this.blockToCode(nextBlock);
  return code + nextCode;
};