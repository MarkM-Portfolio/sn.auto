/**
* connections API blockly blocks
*
* activity blocks
*/

if (!Blockly.Language) Blockly.Language = {};

Blockly.Language.forum_forum = {
  category: 'Forums',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(180);
    this.setOutput(false);
	this.appendTitle('Forum');
	
	var name = new Blockly.FieldTextInput('title');
    this.appendTitle(name, 'TITLE');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('content');
    this.appendTitle(pword, 'CONTENT');
	this.appendInput('', Blockly.NEXT_STATEMENT, 'action');
	this.setPreviousStatement(true);
	this.setNextStatement(true);
    this.setTooltip('a forum');
  }
};

Blockly.Language.forum_topic = {
  category: 'Forums',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(180);
    this.setOutput(false);
	this.appendTitle('Topic');
	
	var name = new Blockly.FieldTextInput('title');
    this.appendTitle(name, 'TITLE');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('content');
    this.appendTitle(pword, 'CONTENT');
	this.appendTitle(' ');
	var priv = new Blockly.FieldDropdown([['not pinned', 'false'],['pinned', 'true']]);
    this.appendTitle(priv, 'PIN');
	this.appendTitle(' ');
	var priv = new Blockly.FieldDropdown([['unlocked', 'false'],['locked', 'true']]);
    this.appendTitle(priv, 'LOK');
	this.appendTitle(' ');
	var priv = new Blockly.FieldDropdown([['not question', 'false'],['question', 'true']]);
    this.appendTitle(priv, 'Q');
	this.appendTitle(' ');
	var priv = new Blockly.FieldDropdown([['not answer', 'false'],['answer', 'true']]);
    this.appendTitle(priv, 'A');
	this.appendInput('', Blockly.NEXT_STATEMENT, 'action');
	this.setPreviousStatement(true);
	this.setNextStatement(true);
    this.setTooltip('an activity');
  }
};

Blockly.Language.forum_reply = {
  category: 'Forums',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(180);
    this.setOutput(false);
	this.appendTitle('Reply');
	
	var name = new Blockly.FieldTextInput('title');
    this.appendTitle(name, 'TITLE');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('content');
    this.appendTitle(pword, 'CONTENT');
		this.appendTitle(' ');
	var priv = new Blockly.FieldDropdown([['not answer', 'false'],['answer', 'true']]);
    this.appendTitle(priv, 'A');
	this.setPreviousStatement(true);
	this.setNextStatement(true);
    this.setTooltip('an activity');
  }
};
