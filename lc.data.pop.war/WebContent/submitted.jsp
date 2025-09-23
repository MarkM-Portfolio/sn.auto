
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
	<head>
	   <meta http-equiv="X-LConn-Login" content="true">
	   <meta http-equiv="content-type" content="text/html;charset=UTF-8">
	   <meta http-equiv="pragma" content="no-cache">
	   <meta http-equiv="cache-control" content="no-cache">

			<title>Connections Data Pop</title>
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
		<link rel="stylesheet" type="text/css" id="lotusBaseStylesheet" href="http://icstage.swg.usma.ibm.com/ic4/connections/resources/web/_style?include=com.ibm.lconn.core.styles.oneui3/base/package3.css" appname="homepage" base="https://icstage.swg.usma.ibm.com/ic4/connections/resources/web/" query="?version=oneui3&amp;rtl=false&amp;etag=20120801.064735" theme="default" defaulttheme="default" oneui="3">
		<link rel="stylesheet" type="text/css" id="lotusSpritesStylesheet" href="http://icstage.swg.usma.ibm.com/ic4/connections/resources/web/_style?include=com.ibm.lconn.core.styles.oneui3/sprites.css">
		<link rel="stylesheet" type="text/css" id="lotusThemeStylesheet" href="http://icstage.swg.usma.ibm.com/ic4/connections/resources/web/_lconntheme/default.css?version=oneui3&amp;rtl=false">
		<!-- Copyright IBM Corp. 2012  All Rights Reserved.                    -->
		<link rel="stylesheet" type="text/css" id="lotusAppStylesheet" href="http://icstage.swg.usma.ibm.com/ic4/connections/resources/web/_lconnappstyles/default/homepage.css?version=oneui3&amp;rtl=false">


		   <link rel="shortcut icon" href="http://icstage.swg.usma.ibm.com/ic4/connections/resources/web/com.ibm.oneui3.styles/imageLibrary/Branding/Other/ConnectionsBlue16.ico" type="image/x-icon">
	</head>
	<body class="lotusui lotusLogin2 lotusui30_body lotusui30_fonts lotusui30 ">
		<div class="lotusui30_layout">
		<div role="banner" class="lotusBanner">
		<div class="lotusRightCorner">
		<div class="lotusInner">
		<div class="lotusLogo" id="lotusLogo">
		
		<span class="lotusAltText">IBM Connections</span></div>
		
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
	
		<div class="lotusTitleBar2">
			<div class="lotusWrapper">
				<div class="lotusInner"> 
					<div class="lotusTitleBarContent">
						<h1 class="lotusHeading">
						<span class="lotusText">Connections Data Population</span></h1>
					</div>
				</div>
			</div>
		</div><!--end titleBar-->
		<div class="lotusTitleBarExt">
			<div class="lotusWrapper">
		
			</div>
		</div><!--end lotusTitleBarExt-->

	</header>
		<div class="lotusMain">
	
		<section class="lotusSection2 lotusPortlet">
			<!-- header is an HTML5 element. Use div if you are using HTML4. -->
			<header class="lotusSectionHeader">
			<div class="lotusInner">
				<h2 class="lotusHeading lotusFirst">
				<!-- header goes here -->
					<!-- Processing Complete -->
				</h2>
			</div>
			</header>
	
			<div class="lotusSectionBody">	
				<!-- Content goes here -->
				
				<body>
				<!-- <h1>Population Processing Complete!</h1> -->

				<h2>Submission Report:</h2>
				<% String report = (String) request.getAttribute("report");%>
				<%if (report == null) { %>
				Your submission populated successfully!
				<%} else { %>
				<%= report %>
				<%} %>

				<br/>
				<br/>
				
				<form name="backform" id="backform-id" action="./index.jsp" method="get">
					<input type="submit" value="Back to Builder">
				</form>

				
			</div><!--end section body-->
			<div class="lotusSectionFooter">
			
			</div>
		</section>

			<!-- footer is an HTML5 element. Use div if you are using HTML4. -->
		<footer role="contentinfo">
		<table class="lotusLegal" cellspacing="0" role="presentation">
			<tbody><tr>
			<td><img class="lotusIBMLogoFooter" src="../../css_OneUI-3.0.3_20120802-1336/images/blank.gif" alt="IBM"></td>
			<td class="lotusLicense">© Copyright IBM Corporation &lt;Year 1&gt;, &lt;Year 2&gt;. Replace this example with copyright information.</td>
			</tr>
		</tbody></table>
		</footer>

		</div>

		   <table class="lotusLegal lotusLoginBoxWide" role="contentinfo"><tbody><tr><td class="lotusLicense">Licensed Materials - Property of IBM Corp. 5724-S68 © IBM Corporation 2007, 2012. IBM, the IBM logo, ibm.com and Lotus are trademarks of IBM Corporation in the United States, other countries, or both. U.S. Government Users Restricted Rights: Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp. Please see the About page for further information.</td></tr></tbody></table>
		   
		 </div>
   




</body>
	
	
	

</html>
