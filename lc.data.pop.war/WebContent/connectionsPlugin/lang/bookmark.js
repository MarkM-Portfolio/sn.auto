/**
* connections API blockly blocks
*
* activity blocks
*/

if (!Blockly.Language) Blockly.Language = {};

Blockly.Language.bookmark_bookmark = {
  category: 'Bookmark',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(250);
    this.setOutput(false);
	this.appendTitle('Bookmark');
	
	var name = new Blockly.FieldTextInput('title');
    this.appendTitle(name, 'TITLE');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('content');
    this.appendTitle(pword, 'CONTENT');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('tag string');
    this.appendTitle(pword, 'TAG');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('link');
    this.appendTitle(pword, 'LINK');
	this.setPreviousStatement(true);
	this.setNextStatement(true);
    this.setTooltip('an activity');
  }
};