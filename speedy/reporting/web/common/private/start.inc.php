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
	require_once 'report.inc.php';
	
	// "constants"
	$page_info = array();
	
	$page_info['title'] = '';
	$page_info['align'] = 'left';
	$page_info['head'] = '';
	
	$page_info['nav'] = 'hola';
	{
		$nav_info = array(
			array( 'title'=>'experiments', 'url'=>'experiments.php' ),
		);
		@include( 'nav-config.inc.php' );
		
		if ( !empty( $nav_info ) )
		{
			foreach ( $nav_info as $key => $val )
			{
				$nav_info[ $key ] = ( '<a href="' . htmlentities( $val['url'] ) . '"' . ( ( isset( $val['new'] ) )?( ' target="_blank"' ):('') ) . '>' . htmlentities( $val['title'] ) . '</a>' );
			}
			
			$page_info['nav'] .= ( ' - ' . implode( ' | ', $nav_info ) );
		}
	}
		
	// currently supported: full, blank
	$page_info['type'] = 'full';
	
	ob_start();
?>
