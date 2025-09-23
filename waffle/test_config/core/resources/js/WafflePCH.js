/*!
 * Functions to help page change handling. - Ruairi Pidgeon <ruaipidg@ie.ibm.com>, 2012-05-09
 */

window.waffleCallback = arguments[arguments.length - 1];
  
var ICCAWafflePCH = function(){
	 
}
	 
ICCAWafflePCH.dojoOnLoad = function() {
		
	if(window.dojo){
		window.dojo.addOnLoad(function(){ window.waffleCallback(true) });
	}else{
		window.waffleCallback(true);
	}
}

if(!window.ICCAWafflePCH) window.ICCAWafflePCH = ICCAWafflePCH;

window.waffleCallback(true);