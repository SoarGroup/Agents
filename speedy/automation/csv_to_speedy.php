<?php

	function usage(&$args)
	{
		echo ( 'php ' . $args[0] . ' <header: Y/N> <file name 1> [field name 2] ...' . "\n" );
	}
	
	if ( $argc < 3 )
	{
		usage($argv);
		exit(1);
	}
	
	$header = ( strtolower( $argv[1] ) == 'y' );
	
	$in = file( 'php://stdin' );
	foreach ( $in as $k => $v )
	{
		$in[ $k ] = explode( ',', trim( $v ) );
	}
	
	if ( count( $in[0] ) != ( $argc - 2 ) )
	{
		echo ( 'Invalid field count!' . "\n" );
		
		usage($argv);
		exit(1);
	}
	
	// nix header row
	if ( $header )
	{
		unset( $in[0] );
	}
	
	foreach ( $in as $k => $v )
	{
		$out = array();
		foreach ( $v as $ik => $iv )
		{
			$out[] = ( $argv[ $ik + 2 ] . '=' . $iv );
		}
		echo ( implode( ' ', $out ) . "\n" );
	}
	
?>
