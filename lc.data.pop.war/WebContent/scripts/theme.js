//Mike Della Donna

function showHeader(title)
{
	var marginWidth = "25%";
	document.write('<header class="'+"pagetop"+'">\n');
	document.write(''+title+'\n');
	document.write('	<nav id="'+"navbar"+'">\n');
	document.write('		<a href="index.html">Home</a>  <a href="help.html">Help</a>\n');
	document.write('	</nav>\n');
	document.write('</header>\n');
	document.getElementById("navbar").style.marginLeft=marginWidth;
	document.getElementById("navbar").style.marginRight=marginWidth;
}