/**
* connections API blockly blocks
*
* blog blocks
*/

if (!Blockly.Language) Blockly.Language = {};

Blockly.Language.blog_blog = {
  category: 'Blog',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(300);
    this.setOutput(false);
	this.appendTitle('Blog');
	
	var name = new Blockly.FieldTextInput('title');
    this.appendTitle(name, 'TITLE');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('handle');
    this.appendTitle(pword, 'HANDLE');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('summary');
    this.appendTitle(pword, 'SUM');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('tag string');
    this.appendTitle(pword, 'TAG');
	this.appendTitle(' comments allowed for ');
	this.appendTitle(new Blockly.FieldTextInput('5', function(text) {
      // Ensure that only a number may be entered.
      // TODO: Handle cases like 'o', 'ten', '1,234', '3,14', etc.
      var n = window.parseFloat(text || 0);
      return window.isNaN(n) ? null : String(n);
    }), 'NUM');
	this.appendTitle('days ');
	var priv = new Blockly.FieldDropdown([['comments allowed', 'true'],['comments not allowed', 'false']]);
    this.appendTitle(priv, 'COM');
	this.appendTitle(' ');
	var priv = new Blockly.FieldDropdown([['comments are not moderated', 'false'],['comments are moderated', 'true']]);
    this.appendTitle(priv, 'MOD');
	this.appendInput('', Blockly.NEXT_STATEMENT, 'action');
	this.setPreviousStatement(true);
	this.setNextStatement(true);
    this.setTooltip('a blog');
  }
};

Blockly.Language.blog_entry = {
  category: 'Blog',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(300);
    this.setOutput(false);
	this.appendTitle('Entry');
	
	var name = new Blockly.FieldTextInput('title');
    this.appendTitle(name, 'TITLE');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('content');
    this.appendTitle(pword, 'CONTENT');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('tag string');
    this.appendTitle(pword, 'TAG');
	this.appendTitle(' comments allowed for ');
	this.appendTitle(new Blockly.FieldTextInput('5', function(text) {
      // Ensure that only a number may be entered.
      // TODO: Handle cases like 'o', 'ten', '1,234', '3,14', etc.
      var n = window.parseFloat(text || 0);
      return window.isNaN(n) ? null : String(n);
    }), 'NUM');
	this.appendTitle('days ');
	var priv = new Blockly.FieldDropdown([['comments allowed', 'true'],['comments not allowed', 'false']]);
    this.appendTitle(priv, 'COM');
	this.appendInput('', Blockly.NEXT_STATEMENT, 'action');
	this.setPreviousStatement(true);
	this.setNextStatement(true);
    this.setTooltip('a blog entry');
  }
};

Blockly.Language.blog_comment = {
  category: 'Blog',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(300);
    this.setOutput(false);
	this.appendTitle('Comment');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('content');
    this.appendTitle(pword, 'CONTENT');
	this.setPreviousStatement(true);
	this.setNextStatement(true);
    this.setTooltip('a blog comment');
  }
};
