/*!
 * Functions for waffle that don't have anywhere else to go. - Ruairi Pidgeon <ruaipidg@ie.ibm.com>, 2012-05-09
 */
 
var waffleCallback = arguments[arguments.length - 1];

var ICCAWaffleMisc = function() {

}

ICCAWaffleMisc.getISOLocalStringCurrentTime = function() {
    function pad(n) {
        return n < 10 ? '0' + n : n
    }
    var d = new Date();
    return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()) + 'T' + pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds())
    }

if(!window.ICCAWaffleMisc){ window.ICCAWaffleMisc = ICCAWaffleMisc;}
waffleCallback(true);