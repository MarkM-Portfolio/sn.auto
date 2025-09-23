<html>
	<head>
		<title>%title%</title>
		<link href="../assets/core.css" rel="stylesheet" type="text/css" />
		<link href="../assets/defaultTheme.css" rel="stylesheet" type="text/css" />
		<link href="../assets/custom.css" rel="stylesheet" type="text/css" />
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
			
			<div class="container">
				<a href="view_results/" class="heavy">&larr; Results</a><br />
				<div class="panel" id="summary">
					<h1>%result%</h1>
					<h2>%summaryValues%</h2>
				</div>
				<div class="panel">
					<div class="right">
						<p>%jsonLink%</p>
						<p>%logLink%</p>
					</div>
				</div>
				<hr />
				<h2>Test Results:</h2>
				%resultsList%
			</div>
			
		</div>
	</body>
</html>
