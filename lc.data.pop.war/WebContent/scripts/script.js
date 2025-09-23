function showXML()
{
	var xml = Blockly.Xml.workspaceToDom(Blockly.mainWorkspace);
	//var xml_text = Blockly.Xml.domToPrettyText(xml);
	var xml_text = Blockly.Xml.domToText(xml);
	document.getElementById("codebox").value = "";
	document.getElementById("codebox").value = xml_text;
}


function showCode()
{
	var code = Blockly.Generator.workspaceToCode('Connections');
	document.getElementById("codebox").value = "";
	document.getElementById("codebox").value = code;
}

function importXML()
{

	console.log("value inside importXML " + document.getElementById('import-id').value)
	var code = document.getElementById("import-id").value;
	var xml = Blockly.Xml.textToDom(code);
	Blockly.Xml.domToWorkspace(Blockly.mainWorkspace, xml);
}

function increaseFrame()
{
	//document.getElementById("theFrame").width = parseInt(document.getElementById("theFrame").width) + 20;
	document.getElementById("theFrame").height = parseInt(document.getElementById("theFrame").height) + 100;
}


function decreaseFrame()
{
	//document.getElementById("theFrame").width = parseInt(document.getElementById("theFrame").width) - 20;
	document.getElementById("theFrame").height = parseInt(document.getElementById("theFrame").height) - 100;
	if(document.getElementById("theFrame").height < 250) {
		document.getElementById("theFrame").height = 250;
	}
}

//get the Blockly XML value of the block arrangement
function getXML() {
	var xml = Blockly.Xml.workspaceToDom(Blockly.mainWorkspace);
	var xml_text = Blockly.Xml.domToPrettyText(xml);
	return xml_text;
}

//set the hidden values to be passed to the servlet and submit
function processRequest() {
	document.getElementById("connections-data-id").value = Blockly.Generator.workspaceToCode('Connections'); //set the data values
	document.getElementById("blockly-blocks-id").value = getXML(); //set the XML block data	
	
	var loading_img = '<img title="" alt="" class="lotusLoading" ' + 
	'src="http://connections.swg.usma.ibm.com/connections/resources/web/' + 
	'com.ibm.lconn.core.styles.oneui3/images/loading.gif"><span>Processing population request...</span>';
	var loading = document.createElement('div');
	loading.className = "lotusMessage lotusInfo";
	loading.innerHTML = loading_img;
	document.getElementById("main").insertBefore(loading, document.getElementById("content"));
	
	document.getElementById("subform-id").submit(); //submit the form
}

//set the value of the blocks to the download writer
function saveBlocks() {
	document.getElementById("blockly-xml-id").value = getXML(); //set the XML block data			
	document.getElementById("saveform-id").submit(); //submit the form
}

//get the file selected by the file dialog
function getFile(){
    document.getElementById("upfile-id").click();
}

//check the file name to see if its empty or not before submitting it
function checkFilename() {
	var name = document
	.getElementById("filename-id").value;
	if ((name != "") && (name != null)) { //if the filename is not blank, enable button
		document.getElementById("download-id").setAttribute("class", "lotusBtn lotusBtnSpecial");
		document.getElementById("download-id").disabled = false;
	} else {
		document.getElementById("download-id").disabled = true;
	}
}

//read the selected file
function handleFileSelect(evt) {
	var files = evt.target.files; //fileList object

	//loop through the FileList
	for (var i = 0, f; f = files[i]; i++) {
		var reader = new FileReader();

		//closure to capture the file information.
		reader.onload = (function(theFile) {
			return function(e) {

			//read the file
			var text = e.target.result;
			document.getElementById('import-id').value = text; 
			};
		})(f);
		
		//once the reader has finished loading
		reader.onloadend = function(evt) {
			//console.log("value inside loadend " + document.getElementById('import-id').value);
			importXML();
		};

		// Read in file as text.
		reader.readAsText(f);
	}
}