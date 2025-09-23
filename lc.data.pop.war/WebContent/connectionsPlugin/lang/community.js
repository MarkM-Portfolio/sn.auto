Blockly.Language.community_community = {
  category: 'Community',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(90);
    this.setOutput(false);
	this.appendTitle('Community');
	
	var name = new Blockly.FieldTextInput('title');
    this.appendTitle(name, 'TITLE');
	this.appendTitle(' ');
	var pword = new Blockly.FieldTextInput('content');
    this.appendTitle(pword, 'SUM');
	this.appendTitle(' ');
	var tags = new Blockly.FieldTextInput('tags');
    this.appendTitle(tags, 'TAGS');
	this.appendTitle(' ');
	var priv = new Blockly.FieldDropdown([['public', 'public'],['private', 'private'],['moderated', 'moderated']]);
    this.appendTitle(priv, 'VIS');
	this.appendInput('', Blockly.INPUT_VALUE, 'MEM0', null);
	this.setMutator(new Blockly.Mutator(['community_member_individual']));
	this.itemCount_ = 1;
	this.appendInput('', Blockly.NEXT_STATEMENT, 'action');
	this.setPreviousStatement(true);
	this.setNextStatement(true);
    this.setTooltip('a community');
  },
  mutationToDom: function(workspace) {
    var container = document.createElement('mutation');
    container.setAttribute('items', this.itemCount_);
    return container;
  },
  domToMutation: function(container) {
    for (var x = 0; x < this.itemCount_; x++) {
      this.removeInput('MEM' + x);
    }
    this.itemCount_ = window.parseInt(container.getAttribute('items'), 10);
    for (var x = 0; x < this.itemCount_; x++) {
      this.appendInput('Member', Blockly.INPUT_VALUE, 'MEM' + x, null);
    }
  },
  decompose: function(workspace) {
    var memgroup = new Blockly.Block(workspace, 'community_member_container');
    memgroup.initSvg();
    var connection = memgroup.inputList[0];
    for (var x = 0; x < this.itemCount_; x++) {
      var itemBlock = new Blockly.Block(workspace, 'community_member_individual');
      itemBlock.initSvg();
      // Store a pointer to any connected blocks.
      itemBlock.valueInput_ = this.getInput('MEM' + x).targetConnection;
      connection.connect(itemBlock.previousConnection);
      connection = itemBlock.nextConnection;
    }
    return memgroup;
  },
  compose: function(memgroup) {
    // Disconnect all input blocks and destroy all inputs.
    for (var x = 0; x < this.itemCount_; x++) {
      this.removeInput('MEM' + x);
    }
    this.itemCount_ = 0;
    // Rebuild the block's inputs.
    var itemBlock = memgroup.getInputTargetBlock('STACK');
    while (itemBlock) {
      var input =
          this.appendInput('Member', Blockly.INPUT_VALUE, 'MEM' + this.itemCount_, null);
      // Reconnect any child blocks.
      if (itemBlock.valueInput_) {
        input.connect(itemBlock.valueInput_);
      }
      this.itemCount_++;
      itemBlock = itemBlock.nextConnection &&
          itemBlock.nextConnection.targetBlock();
    }
  }
  
};

Blockly.Language.community_member = {
  category: 'Community',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(90);
    this.setOutput(false);
	
	this.setOutput(true);
	var name = new Blockly.FieldTextInput('name');
    this.appendTitle(name, 'EMAIL');

    this.setTooltip('an activity');
  }
};


Blockly.Language.community_member_container = {
  // Container.
  init: function() {
    this.setColour(90);
    this.appendTitle('Community');
    this.appendInput('', Blockly.NEXT_STATEMENT, 'STACK');
    this.contextMenu = false;
  }
};

Blockly.Language.community_member_individual = {
  // Add items.
  init: function() {
    this.setColour(90);
    this.appendTitle('Member');
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.contextMenu = false;
  }
};

Blockly.Language.community_amy_member = {
  category: 'Community',
  helpUrl: 'http://www.google.com',
  init: function() {
    this.setColour(90);
    this.setOutput(false);
	this.appendTitle('Add Amy Jones');
	this.appendTitle(new Blockly.FieldTextInput('1', function(text) {
      // Ensure that only a number may be entered.
      // TODO: Handle cases like 'o', 'ten', '1,234', '3,14', etc.
      var n = window.parseFloat(text || 0);
      return window.isNaN(n) ? null : String(n);
    }), 'NUM');
	this.appendTitle('time(s)');
	this.setOutput(true);

    this.setTooltip('an activity');
  }
};
