<?php

	$handlers = array(
		'contextfile' => 'handle_default',
		'context'=>'handle_default',
		'p'=>'handle_default',
		's'=>'handle_default',
		'wf'=>'handle_wf',
		'punc'=>'handle_ignore',
		'/s'=>'handle_end',
		'/p'=>'handle_end',
		'/context'=>'handle_end',
		'/contextfile'=>'handle_end',
	);
	
	
	function handle_default( &$line )
	{
		echo ( implode( ' ', $line ) . "\n" );
	}
	
	function handle_ignore( &$line )
	{
	}
	
	function handle_end( &$line )
	{
		echo ( 'end-' . substr( $line[0], 1 ) . "\n" );
	}
	
	function handle_wf( &$line )
	{
		// cmd == done
		$cmd = explode( '=', $line[1] );
		if ( ( $cmd[0] == 'cmd' ) && ( $cmd[1] == 'done' ) )
		{
			$temp = array();
			for ( $i=2; $i< ( count( $line ) - 1 ); $i++ )
			{
				$temp2 = explode( '=', $line[$i] );
				$temp[ $temp2[0] ] = $temp2[1];
			}
			$temp['word'] = $line[ count($line) - 1 ];
			
			if ( isset( $temp['ot'] ) )
			{
				return;
			}
			
			if ( !isset( $temp['wnsn'] ) )
			{
				return;
			}
			
			// there can be multiple word sense/key pairs
			// we need to test each and only pass through 
			// acceptable ones
			$wnsn = null;
			$lexsn = null;
			{
				$wnsn = explode( ';', $temp['wnsn'] );
				$lexsn = explode( ';', $temp['lexsn'] );
				
				foreach ( $wnsn as $k => $v )
				{
					if ( $v == '0' )
					{
						unset( $wnsn[ $k ] );
						unset( $lexsn[ $k ] );
					}
				}
				
				if ( empty( $wnsn ) )
				{
					return;
				}
				else
				{
					$wnsn = implode( ';', $wnsn );
					$lexsn = implode( ';', $lexsn );
				}
			}
			
			// unsure why, but semcor has lots of instances
			// of (stuff), where "stuff" is a string.
			// wordnet doesn't have this, so I'm removing
			$pattern = '/\(.+\)/';
			
			$final = array(
				( 'wf' ),
				( 'pos=' . $temp['pos'] ),
				( 'wn-sense-word=' . $temp['lemma'] ),
				( 'wn-sense-num=' . $wnsn ),
				( 'wn-sense-key=' . preg_replace( $pattern, '', $lexsn ) ),
				( 'word=' . $temp['word'] ),
			);
			
			echo ( implode( ' ', $final ) . "\n" );
		}
	}
	
	//
	
	function usage( &$argv )
	{
		echo ( "\n" );
		echo ( 'php ' . $argv[0] . ' [path to semcor tag file]' . "\n" );
		echo ( 'if no argument, read from stdin' . "\n\n" );
	}
	
	$input_path = 'php://stdin';
	if ( $argc == 2 )
	{
		$input_path = $argv[1];
	}
	
	{
		$std_in = @fopen( $input_path, 'r' );	
		if ( $std_in )
		{			
			while ( !feof( $std_in ) )
			{
				$line = fgets( $std_in );
				
				if ( $line !== false )
				{				
					$line = trim( $line );
					
					if ( !empty( $line ) )
					{
					
						$line = substr( $line, 1, strlen( $line ) - 2 );
						
						$line = str_replace( '</wf', '', $line );
						$line = str_replace( '</punc', '', $line );
						$line = str_replace( '>', ' ', $line );
						
						$line = explode( ' ', $line );
						
						if ( isset( $handlers[ $line[0] ] ) )
						{
							$f_name = $handlers[ $line[0] ];
							$f_name( $line );
						}
						else
						{
							var_dump( $line );
							exit;
						}
						
					}
				}			
			}
			
			fclose( $std_in );
		}
		else
		{
			usage( $argv );
		}
	}
	
?>
