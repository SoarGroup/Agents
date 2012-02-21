<?php

	define( 'SEMCOR_SCRIPT', 'php semcor.php' );
	
	function usage( &$argv )
	{
		echo ( "\n" );
		echo ( 'php ' . $argv[0] . ' <semcor tagfiles directory>' . "\n\n" );
	}
	
	$contents = null;
	if ( ( $argc != 2 ) || !( $contents = @scandir( $argv[1] ) ) )
	{
		usage( $argv );
		exit;
	}
	
	foreach ( $contents as $tagfile )
	{
		if ( $tagfile[0] != '.' )
		{
			$tag_path = ( $argv[1] . DIRECTORY_SEPARATOR . $tagfile );
			
			$cmd = ( SEMCOR_SCRIPT . ' ' . escapeshellarg( $tag_path ) );
			
			echo ( shell_exec( $cmd ) );
		}
	}
	
?>
