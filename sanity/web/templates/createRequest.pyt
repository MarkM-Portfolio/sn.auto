<html> 
	<head>
		<title>%title%</title>
		<link href="../assets/core.css" rel="stylesheet" type="text/css" />
		<link href="../assets/defaultTheme.css" rel="stylesheet" type="text/css" />
		<link href="../assets/custom.css" rel="stylesheet" type="text/css" />
		
		<script type="text/javascript">
			function showLoader() {
				document.getElementById("dark").style.visibility = "visible";
				document.getElementById("loading").style.visibility = "visible";
				self.setInterval(function(){animateDots()}, 500);
			}
			
			function animateDots() {
				dots = document.getElementById("dots");
				if (dots.textContent.length < 5) {
					dots.textContent += ".."
				} else {
					dots.textContent = "."
				}
			}
		</script>
		
	</head>
	<body class="lotusui30 lotusui30_body lotusui30_fonts">
		<div class="lotusui30_layout">
			<div class="lotusBanner" role="banner">
				<div class="lotusRightCorner"><div class="lotusInner">
					<div class="lotusLogo" id="lotusLogo">
						<a href="/" class="lotusAltText">IBM Sanity</a>
					</div>
				</div></div>
			</div>
			<div id="loading">
				<h1>Running Tests</h1>
				<h3>Please wait...</h3>
				<p id="dots">.</p>
			</div>
			<div id="dark"></div>
			<div class="container">
				<h1>Create Request</h1><hr />
				<form action="/start_tool" method="get">
					<h2>Parameters:</h2> 
					<div class="lotusMessage lotusInfo" role="status">
					<img src="http://www-12.lotus.com/ldd/doc/oneuidoc/docResources/icons/iconInfo16.png" />
					<p>Parameters should be formatted as such: param1:value1,param2:value2, etc. Use the configuration file in the Sanity root directory (<strong>conf.json</strong> by default) to adjust parameters permanently</p></div>
					<input type="text" name="params" id="params"/>
					
					<h2>Tests:</h2>
					<table id="testTable" border="0">
						<tr>
							<td class="shaded">Run</td>
							<td class="shaded">Test</td>
						</tr>
						%tests%
					</table>
					<div class="center">
						<input type="submit" value="Run" class="lotusBtn" onclick="showLoader()"/>
					</div>
				</form>
			</div>
		</div>
	</body>
</html>
