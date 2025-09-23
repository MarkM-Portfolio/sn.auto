/*!
 * Functions to help element location with javascript. - Ruairi Pidgeon <ruaipidg@ie.ibm.com>, 2012-05-09
 */
 
 var waffleCallback = arguments[arguments.length - 1];
 
 var ICCAWaffleLoc = function(){
 
 }
 
ICCAWaffleLoc.toParentElement = function(elem) {
	
	var parentNode = elem.parentNode;
	if (!parentNode) return false;
	if (parentNode.nodeType == 1) return parentNode;
	else return toParentElement(parentNode);
 }
 
ICCAWaffleLoc.toParentElementByTagName = function(elem, tagName){
 
	var parentNode = toParentElement(elem);
	if (parentNode == false) return false;
	if (parentNode.tagName.toLowerCase() == tagName) return parentNode;
	else return toParentElementByTagName(parentNode, tagName);
 }
 
 ICCAWaffleLoc.jsEscape = function(string){
 
	if(string != null){
		return escape(string);
	}else{
		return "";
	}
 }

 if(!window.ICCAWaffleLoc){ window.ICCAWaffleLoc = ICCAWaffleLoc;};
 waffleCallback(true);
// window.addEventListener ? 
//window.addEventListener("load",loadWaffleLoc,false) : 
//window.attachEvent && window.attachEvent("onload",loadWaffleLoc);