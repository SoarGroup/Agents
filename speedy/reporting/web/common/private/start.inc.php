<?php
	session_start();
	
	// required libraries
	require_once 'db.inc.php';
	require_once 'auth.inc.php';
	require_once 'experiment.inc.php';
	require_once 'tables.inc.php';
	require_once 'graphs.inc.php';
	require_once 'misc.inc.php';
	require_once 'jquery.inc.php';
	
	// "constants"
	$page_info = array();
	
	$page_info['title'] = '';
	$page_info['align'] = 'left';
	$page_info['head'] = '';
	
	$page_info['nav'] = 'hola - <a href="experiments.php">experiments</a>';
		
	// currently supported: full, blank
	$page_info['type'] = 'full';
	
	ob_start();
?>
