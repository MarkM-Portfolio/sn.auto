<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
	<head>
		<script src="scripts/script.js"></script>
		<script src="scripts/XMLBlockTemplates.js"></script>
	
	   <meta http-equiv="X-LConn-Login" content="true">
	   <meta http-equiv="content-type" content="text/html;charset=UTF-8">
	   <meta http-equiv="pragma" content="no-cache">
	   <meta http-equiv="cache-control" content="no-cache">

			<title>Connections Data Population</title>
			<script src="scripts/theme.js"></script>
	<script type="text/javascript">
	   if(self!=top){
		  try {
			 // reset location if in different host.
			 // per Same Origin Policy, try to get top location attributes, 
			 // will get permission denied if in different origin.
			 if (self.location.hostname!=top.location.hostname)
				top.location=self.location;
		  }catch(e){
			 // reset location if in different origin
			 top.location=self.location;
		  }
	   }
	</script><script type="text/javascript">
		__iContainer_skip_init__ = true;
	</script>

		   <!-- Copyright IBM Corp. 2012  All Rights Reserved.                    -->

		<!--[if IE 6]><script type="text/javascript">document.getElementsByTagName("html")[0].className+=" lotusui_ie lotusui_ie6";</script><![endif]-->
		<!--[if IE 7]><script type="text/javascript">document.getElementsByTagName("html")[0].className+=" lotusui_ie lotusui_ie7";</script><![endif]-->
		<!--[if IE 8]><script type="text/javascript">document.getElementsByTagName("html")[0].className+=" lotusui_ie lotusui_ie8";</script><![endif]-->
		<link rel="stylesheet" type="text/css" id="lotusBaseStylesheet" href="http://connections.swg.usma.ibm.com/connections/resources/web/_style?include=com.ibm.lconn.core.styles.oneui3/base/package3.css" appname="homepage" base="http://connections.swg.usma.ibm.com/connections/resources/web/" query="?version=oneui3&amp;rtl=false&amp;etag=20120727.005221" theme="default" defaulttheme="default" oneui="3">		<link rel="stylesheet" type="text/css" id="lotusSpritesStylesheet" href="http://connections.swg.usma.ibm.com/connections/resources/web/_style?include=com.ibm.lconn.core.styles.oneui3/sprites.css">
		<link rel="stylesheet" type="text/css" id="lotusThemeStylesheet" href="http://connections.swg.usma.ibm.com/connections/resources/web/_lconntheme/default.css?version=oneui3&amp;rtl=false">	
		<!-- Copyright IBM Corp. 2012  All Rights Reserved.                    -->
		<link rel="stylesheet" type="text/css" id="lotusAppStylesheet" href="http://connections.swg.usma.ibm.com/connections/resources/web/_lconnappstyles/default/homepage.css?version=oneui3&amp;rtl=false">
		<link rel="stylesheet" type="text/css" id="lotusSpritesStylesheet" href="http://connections.swg.usma.ibm.com/connections/resources/web/_style?include=com.ibm.lconn.core.styles.oneui3/sprites.css&amp;etag=20120810.185201">

		   <link rel="shortcut icon" href="http://connections.swg.usma.ibm.com/connections/resources/web/com.ibm.oneui3.styles/imageLibrary/Branding/Other/ConnectionsBlue16.ico" type="image/x-icon">
	</head>
	<body class="lotusui lotusLogin2 lotusui30_body lotusui30_fonts lotusui30 ">
		<div class="lotusui30_layout">
		<div role="banner" class="lotusBanner"><div class="lotusRightCorner">
		<div class="lotusInner">
		<div class="lotusLogo" id="lotusLogo">
		
		<span class="lotusAltText">IBM Connections</span>
		
		</div>
		<!-- The help button in the Connections banner -->
		<ul id="headerMenuContainer" class="lotusInlinelist lotusUtility lotusNowrap">
		<li id="headerHelpLi">
		<a id="headerHelpLink" href="help.html" aria-label="Open help window" role="button">
			<img class="iconsOther16 iconsOther16-bannerHelpNormal16" src="images/help.png" alt="" role="presentation">
			<span class="lotusAltText">?</span>
		</a>
		</li>
		<li><span class="lotusBranding"><img src="http://connections.swg.usma.ibm.com/connections/resources/web/com.ibm.lconn.core.styles.oneui3/images/blank.gif?etag=20120810.185201" alt="IBM" class="lotusIBMLogo"><span class="lotusAltText">IBM</span></span></li>
		</ul> 
		
		</div></div></div>
		
		<div class="lotusFrame lotusui30_layout">
			<!-- header is an HTML5 element. Remove header if you are using HTML4. -->
			<header role="banner">
			
			<!--  <a href="help.html">Help</a> </br> -->
			
				<div class="lotusTitleBar2">
					<div class="lotusWrapper">
						<div class="lotusInner">
							<div class="lotusTitleBarContent">
								<h1 class="lotusHeading">
									<span class="lotusText">Connections Data Population</span>
								</h1>
							</div>
						</div>
					</div>
				</div>
				<!--end titleBar-->
				<div class="lotusTitleBarExt">
					<div class="lotusWrapper"></div>
				</div>
				<!--end lotusTitleBarExt-->

			</header>
			<div class="lotusMain" id="main">

					<div class="lotusSectionBody" id="content">
						<!-- Content goes here -->
						<section>
							<article>
							<br/>
							<div class="lotusBtnContainer" style="width: 95.6%">	
	
								<!--
								<button class="lotusBtn lotusBtnSpecial" onclick="showXML()">export blocks</button>
								<button class="lotusBtn lotusBtnSpecial" onclick="importXML()">import blocks</button>
								<button onclick="showCode()">show code</button>
								<button onclick="increaseFrame()">bigger frame</button>
								<button onclick="decreaseFrame()">smaller frame</button> --> 	
								
								<!--  File import form -->
								<form style="display:inline;" name="importform" id="importform-id" enctype="multipart/form-data">
									<!-- <input type="file" id="file-id" size="2" width="2"/> -->
									<button class="lotusBtn lotusBtnSpecial" type="button" id="file-id" onclick="getFile()">Import From File</button>
									<div style='display:none; height:0px;width:0px; overflow:hidden;'>
										<input name="upfile" id="upfile-id" type="file" value="nothing"/>
									</div>
									<input type="hidden" name="import" id="import-id" value="nothing"/>
									<!-- <button class="lotusBtn lotusBtnSpecial" type="button" name="import-btn" id="import-btn-id" onclick="importXML()">Import Blocks</button> -->
						
									<!-- HTML5 File API -->
									<script>
  									document.getElementById('upfile-id').addEventListener('change', handleFileSelect, false);
									</script> 
								</form>
								
								<!-- Import template controllers -->
								<span id="templateSelect"></span>
								<script>setUpTemplateSelect();</script>
								<button class="lotusBtn lotusBtnSpecial" onclick="loadSelectedTemplate()">Import Template</button>
							
								
								<!-- Save block form -->
								<form style="display:inline;" name="saveform" id="saveform-id" method="post" action="Download">
									<input type="hidden" name="blockly-xml" id="blockly-xml-id" value="nothing" />
									<input type="text" name="filename" id="filename-id" value="Name your download here" onChange="checkFilename()" size="25" />
									<button class="lotusBtn lotusBtnDisabled" type="button" name="download" id="download-id" onclick="saveBlocks()" disabled="disabled">Export To File</button>
								</form>
								
								<!--  Submission form -->
								<form style="display:inline;" name="subform" id="subform-id" method="post" action="ProcessingComplete">
									<input type="hidden" name="connections-data" id="connections-data-id" value="nothing" />
									<input type="hidden" name="blockly-blocks" id="blockly-blocks-id" value="nothing" />
									<input type="hidden" name="report" id="report-id" value="nothing" />
									<button class="lotusBtn lotusBtnSpecial"  type="button" onclick="processRequest()" style="
									display: inline;
									float: right;
									position:relative;">Submit Components</button>
								</form>
								
								</div><!--end button container-->
								
								
								
								<script>
										function blocklyLoaded(blockly) {
										  // Called once Blockly is fully loaded.
										  window.Blockly = blockly;
										}
									</script>
								<iframe id="theFrame" height="750" width="95%" src="frame.html"></iframe>

							</article>
							<!--<article>
								<textarea id="codebox" rows="20" cols="120">
								</textarea>
							</article>-->
							
						</section>
						
					</div>
					<!--end section body-->
					<div class="lotusSectionFooter"></div>



				<!-- footer is an HTML5 element. Use div if you are using HTML4. -->
				<footer role="contentinfo">
					<table class="lotusLegal" cellspacing="0" role="presentation">
						<tbody>
							<tr>
								<td></td>
								<td class="lotusLicense"> Copyright IBM Corporation </td>
							</tr>
						</tbody>
					</table>
				</footer>

			</div>

			<table class="lotusLegal lotusLoginBoxWide" role="contentinfo">
				<tbody>
					<tr>
						<td class="lotusLicense">Licensed Materials - Property of IBM
							Corp. 5724-S68 IBM Corporation 2007, 2012. IBM, the IBM logo,
							ibm.com and Lotus are trademarks of IBM Corporation in the United
							States, other countries, or both. U.S. Government Users
							Restricted Rights: Use, duplication or disclosure restricted by
							GSA ADP Schedule Contract with IBM Corp. Please see the About
							page for further information.</td>
					</tr>
				</tbody>
			</table>

		</div></body>
	
		<div class='lotusFooter' id='lotusFooter'>
			<ul>
				<li><a href="http://connections.swg.usma.ibm.com/forums/html/topic?id=910fafbd-6f10-457a-ad1b-830d5612c2cb">Feedback</a></li>
			</ul>
		</div>

	

</html>
