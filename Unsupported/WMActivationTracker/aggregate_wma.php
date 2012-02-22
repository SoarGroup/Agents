<?php

	if ( $argc != 2 )
	{
		exit;
	}
	
	define( 'NUM_DECISIONS', intval( $argv[1] ) );
		
	// read and organize
	$output = array();
	for ( $i=1; $i<=NUM_DECISIONS; $i++ )
	{
		$file_data = file( 'wma_' . $i . '.txt' );
		foreach ( $file_data as $line => $data )
		{
			$data = trim( $data );
			
			if ( strlen( $data ) )
			{
				$data = explode( ' ', $data );
				
				// timetag
				$data[0] = intval( substr( $data[0], 1, strlen( $data[0] ) - 2 ) );
				
				// activation
				$data[4] = floatval( substr( $data[4], 1, strlen( $data[4] ) - 2 ) );
				
				if ( !isset( $output[ $data[0] ] ) )
				{
					$output[ $data[0] ] = array();
					$output[ $data[0] ]['wme'] = ( $data[1] . ' ' . $data[2] . ' ' . $data[3] );
					$output[ $data[0] ]['history'] = array();
				}
				
				$output[ $data[0] ]['history'][ $i ] = $data[4];
			}
		}
	}
	ksort( $output );
	
	// get rid of useless wmes
	foreach ( $output as $key => $val )
	{
		if ( count( $val['history'] ) < 3 )
		{
			unset( $output[ $key ] );
		}
	}
	
	// get rid of architectural wmes
	foreach ( $output as $key => $val )
	{
		$temp = array_unique( $val['history'] );
		
		if ( count( $temp ) == 1 )
		{
			unset( $output[ $key ] );
		}
	}
	
	// output
	$csv = array();
	{
		
		// timetags
		{
			$line = array();
			$line[] = '';
			
			foreach ( $output as $timetag => $data )
			{
				$line[] = $timetag;
			}
			
			$csv[] = implode( ',', $line );
		}
		
		// wmes
		{
			$line = array();
			$line[] = '';
			
			foreach ( $output as $timetag => $data )
			{
				$line[] = ( '"' . $data['wme'] . '"' );
			}
			
			$csv[] = implode( ',', $line );
		}
		
		// data
		for ( $i=1; $i<=NUM_DECISIONS; $i++ )
		{
			$line = array();
			$line[] = $i;
			
			foreach ( $output as $timetag => $data )
			{
				if ( isset( $data['history'][ $i ] ) )
				{
					$line[] = $data['history'][ $i ];
				}
				else
				{
					$line[] = '';
				}
			}
			
			$csv[] = implode( ',', $line );
		}
	}
	
	echo ( implode( "\n", $csv ) . "\n" );

?>
