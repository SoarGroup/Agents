<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Speedy {dash_title}</title>
		
		<link href="common/public/speedy.css" rel="stylesheet" type="text/css" media="all" />
		<link rel="shortcut icon" href="common/public/favico.ico" >
		
		<link type="text/css" href="common/public/jquery/jquery-ui-1.8.5.custom.css" rel="Stylesheet" />
		<link type="text/css" href="common/public/shjs/sh_style.css" rel="Stylesheet" />
		
		<script type="text/javascript" src="http://www.google.com/jsapi"></script>
		<script type="text/javascript">
			google.load( "jquery", "1.4" );
			google.load( "jqueryui", "1.8" );
		</script>
		
		<script type="text/javascript" src="common/public/shjs/sh_main.min.js"></script>
		<script type="text/javascript" src="common/public/shjs/sh_sql.min.js"></script>
		
		<meta name="format-detection" content="telephone=no">
		
		{head}
	</head>
	
	<body onload="sh_highlightDocument();">
		<div id="content">
			
			<div id="header">
				<div style="text-align: {align}"><a href="index.php"><img src="common/public/logo.png" /></a></div>
				<div style="text-align: {align}" class="nav">&nbsp;{nav}</div>
			</div>
			
			<div id="title">
				{title}
			</div>
			<br />
		
			{content}
			
		</div>
	</body>
	
</html>