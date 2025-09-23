/**
* connections API blockly blocks
*
* tool blocks
*/

if (!Blockly.Language) Blockly.Language = {};

Blockly.Language.tools_login = {
  category: 'Tools',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(50);
    this.setOutput(false);
	this.appendTitle('Login');
	var url = new Blockly.FieldTextInput('url');
    this.appendTitle(url, 'URL');
	this.appendTitle(' ');
	var name = new Blockly.FieldTextInput('username');
    this.appendTitle(name, 'UNAME');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('password');
    this.appendTitle(pword, 'PWORD');
	this.appendInput('', Blockly.NEXT_STATEMENT, 'action');
	this.setPreviousStatement(true);
	this.setNextStatement(true);
    this.setTooltip('a user logging in');
  }
};

Blockly.Language.tools_loop = {
  category: 'Tools',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(50);
    this.setOutput(false);
	this.appendTitle('Loop from');
    this.appendTitle(new Blockly.FieldTextInput('1', function(text) {
      // Ensure that only a number may be entered.
      // TODO: Handle cases like 'o', 'ten', '1,234', '3,14', etc.
      var n = window.parseFloat(text || 0);
      return window.isNaN(n) ? null : String(n);
    }), 'START');
	
	this.appendTitle('to');
	this.appendTitle(new Blockly.FieldTextInput('5', function(text) {
      // Ensure that only a number may be entered.
      // TODO: Handle cases like 'o', 'ten', '1,234', '3,14', etc.
      var n = window.parseFloat(text || 0);
      return window.isNaN(n) ? null : String(n);
    }), 'NUM');
	
	this.appendTitle(', incrementing by');
	this.appendTitle(new Blockly.FieldTextInput('1', function(text) {
      // Ensure that only a number may be entered.
      // TODO: Handle cases like 'o', 'ten', '1,234', '3,14', etc.
      var n = window.parseFloat(text || 0);
      return window.isNaN(n) ? null : String(n);
    }), 'INC');

	this.appendInput('', Blockly.NEXT_STATEMENT, 'action');
	this.setPreviousStatement(true);
	this.setNextStatement(true);
    this.setTooltip('a user logging in');
  }
};

