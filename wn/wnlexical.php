<?php

	$handling_functions = array(
		'S' => 'handle_s_copy',
		'SK' => 'handle_sk_copy',
		'G' => 'handle_g_copy',
		'SYNTAX' => 'handle_syntax_copy',
		'HYP' => 'handle_hyp_copy',
		'INS' => 'handle_ins_copy',
		'ENT' => 'handle_ent_copy',
		'SIM' => 'handle_sim_copy',
		'MM' => 'handle_mm_copy',
		'MS' => 'handle_ms_copy',
		'MP' => 'handle_mp_copy',
		'DER' => 'handle_der_copy',
		'CLS' => 'handle_cls_copy',
		'CS' => 'handle_cs_copy',
		'VGP' => 'handle_vgp_copy',
		'AT' => 'handle_at_copy',
		'ANT' => 'handle_ant_copy',
		'SA' => 'handle_sa_copy',
		'PPL' => 'handle_ppl_copy',
		'PER' => 'handle_per_copy',
		'FR' => 'handle_fr_copy',
	);
	
	//
	
	function handle_s_copy( $chunk, $format )
	{
		// (S-111793403-5 ISA S SYNSET-ID 111793403 W-NUM 5 WORD "Xanthosoma sagittifolium" SS-TYPE "n" SENSE-NUMBER 1 TAG-COUNT 0)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 's',			
			'synset-id' => intval( $chunk[4] ),
			'w-num' => intval( $chunk[6] ),
			'word' => ( '|' . $chunk[8] . '|' ),
			'word-lower' => ( '|' . strtolower( $chunk[8] ) . '|' ),
			'ss-type' => ( '|' . strtolower( $chunk[10] ) . '|' ),
			'sense-number' => intval( $chunk[12] ),
			'tag-count' => intval( $chunk[14] ),
		);
		
		if ( in_array( str_replace( '|', '', $chunk['ss-type'] ), array( 'n', 'v', 'a', 's', 'r' ) ) )
		{
			echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );
		}
	}
	
	function handle_sk_copy( $chunk, $format )
	{				
		// (SK-113516312-5 ISA SK SYNSET_ID 113516312 W_NUM 5 SENSE_KEY "ore_dressing%1:22:00::")
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'sk',
			'synset-id' => intval( $chunk[4] ),
			'w-num' => intval( $chunk[6] ),
			'sense-key' => ( '|' . $chunk[8] . '|' ),
		);	
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );
	}
	
	function handle_g_copy( $chunk, $format )
	{
		// (G-200082081-1 ISA G SYNSET-ID 200082081 GLOSS "improve the condition of; \"These pills will help the patient\"")
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'g',
			'synset-id' => intval( $chunk[4] ),
			'gloss' => ( '|' . $chunk[6] . '|' ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );
	}
	
	function handle_syntax_copy( $chunk, $format )
	{		
		// (SYNTAX-300197576-1 ISA SYNTAX SYNSET_ID 300197576 W_NUM 1 SYNTAX "a")
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'syntax',
			'synset-id' => intval( $chunk[4] ),
			'w-num' => intval( $chunk[6] ),
			'syntax' => ( '|' . $chunk[8] . '|' ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );
	}
	
	function handle_hyp_copy( $chunk, $format )
	{
		// (HYP-110359300-1 ISA HYP SYNSET-ID 110359300 SYNSET-ID2 109738708)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'hyp',
			'synset-id' => intval( $chunk[4] ),			
			'synset-id2' => intval( $chunk[6] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_ins_copy( $chunk, $format )
	{
		// (INS-110698649-1 ISA INS SYNSET_ID 110698649 SYNSET_ID2 109805324)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'ins',
			'synset-id' => intval( $chunk[4] ),			
			'synset-id2' => intval( $chunk[6] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_ent_copy( $chunk, $format )
	{
		// (ENT-200015713-1 ISA ENT SYNSET-ID 200015713 SYNSET-ID2 200014742)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'ent',
			'synset-id' => intval( $chunk[4] ),			
			'synset-id2' => intval( $chunk[6] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_sim_copy( $chunk, $format )
	{
		// (SIM-300039259-1 ISA SIM SYNSET-ID 300039259 SYNSET-ID2 300038750)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'sim',
			'synset-id' => intval( $chunk[4] ),			
			'synset-id2' => intval( $chunk[6] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_mm_copy( $chunk, $format )
	{
		// (MM-101359070-1 ISA MM SYNSET-ID 101359070 SYNSET-ID2 101358259)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'mm',
			'synset-id' => intval( $chunk[4] ),			
			'synset-id2' => intval( $chunk[6] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_ms_copy( $chunk, $format )
	{
		// (MS-107783667-1 ISA MS SYNSET-ID 107783667 SYNSET-ID2 107783827)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'ms',
			'synset-id' => intval( $chunk[4] ),			
			'synset-id2' => intval( $chunk[6] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_mp_copy( $chunk, $format )
	{
		// (MP-100661847-1 ISA MP SYNSET-ID 100661847 SYNSET-ID2 100700000)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'mp',
			'synset-id' => intval( $chunk[4] ),			
			'synset-id2' => intval( $chunk[6] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_der_copy( $chunk, $format )
	{
		// (DER-202335629-6 ISA DER SYNSET-ID 202335629 W-NUM1 3 SYNSET-ID2 103130340 W-NUM2 1)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'der',
			'synset-id' => intval( $chunk[4] ),
			'w-num1' => intval( $chunk[6] ),			
			'synset-id2' => intval( $chunk[8] ),
			'w-num2' => intval( $chunk[10] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_cls_copy( $chunk, $format )
	{
		// (CLS-301019450-2 ISA CLS SYNSET-ID 301019450 SYNSET-ID2 0 CLASS-TYPE 108860123 NIL 0 NIL "r")
		// NOTE: I'm assuming, per w3c, this is a mistake and it should be CLS(SYNSET-ID, SYNSET-ID2, CLASS-TYPE)
		$chunk_id = $chunk[0];
		
		if ( isset( $chunk[12] ) )
		{
			$chunk = array(
				'isa' => 'cls',
				'synset-id' => intval( $chunk[4] ),						
				'synset-id2' => intval( $chunk[8] ),
				'class-type' => ( '|' . $chunk[12] . '|' ),
			);
		}
		else
		{
			$chunk = array(
				'isa' => 'cls',
				'synset-id' => intval( $chunk[4] ),						
				'synset-id2' => intval( $chunk[6] ),
				'class-type' => ( '|' . $chunk[8] . '|' ),
			);
		}
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_cs_copy( $chunk, $format )
	{
		// (CS-200025654-1 ISA CS SYNSET-ID 200025654 SYNSET-ID2 200026385)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'cs',
			'synset-id' => intval( $chunk[4] ),			
			'synset-id2' => intval( $chunk[6] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_vgp_copy( $chunk, $format )
	{
		// (VGP-200136991-1 ISA VGP SYNSET-ID 200136991 W-NUM1 0 SYNSET-ID2 202730135 W-NUM2 0)
		// NOTE: I'm doing a direct copy, though it appears as though all w-nums are 0 and there are symmetric copies
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'vgp',
			'synset-id' => intval( $chunk[4] ),
			'w-num1' => intval( $chunk[6] ),			
			'synset-id2' => intval( $chunk[8] ),
			'w-num2' => intval( $chunk[10] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_at_copy( $chunk, $format )
	{
		// (AT-100844254-2 ISA AT SYNSET-ID 100844254 SYNSET-ID2 301201422)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'at',
			'synset-id' => intval( $chunk[4] ),			
			'synset-id2' => intval( $chunk[6] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_ant_copy( $chunk, $format )
	{
		// (ANT-302254264-1 ISA ANT SYNSET-ID 302254264 W-NUM1 1 SYNSET-ID2 302253964 W-NUM2 1)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'ant',
			'synset-id' => intval( $chunk[4] ),
			'w-num1' => intval( $chunk[6] ),			
			'synset-id2' => intval( $chunk[8] ),
			'w-num2' => intval( $chunk[10] ),
		);
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_sa_copy( $chunk, $format )
	{
		// (SA-300177963-1 ISA SA SYNSET-ID 300177963 W-NUM1 0 SYNSET-ID2 300995468 W-NUM2 0)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'sa',
			'synset-id' => intval( $chunk[4] ),
			'w-num1' => intval( $chunk[6] ),			
			'synset-id2' => intval( $chunk[8] ),
			'w-num2' => intval( $chunk[10] ),
		);	
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_ppl_copy( $chunk, $format )
	{
		// (PPL-303155306-1 ISA PPL SYNSET-ID 303155306 W-NUM1 1 SYNSET-ID2 200538571 W-NUM2 1)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'ppl',
			'synset-id' => intval( $chunk[4] ),
			'w-num1' => intval( $chunk[6] ),			
			'synset-id2' => intval( $chunk[8] ),
			'w-num2' => intval( $chunk[10] ),
		);	
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_per_copy( $chunk, $format )
	{
		// (PER-400264759-1 ISA PER SYNSET-ID 400264759 W-NUM1 1 SYNSET-ID2 300899226 W-NUM2 1)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'per',
			'synset-id' => intval( $chunk[4] ),
			'w-num1' => intval( $chunk[6] ),			
			'synset-id2' => intval( $chunk[8] ),
			'w-num2' => intval( $chunk[10] ),
		);	
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	function handle_fr_copy( $chunk, $format )
	{
		// (FR-202546075-4 ISA FR SYNSET-ID 202546075 F-NUM 0 W-NUM 11)
		$chunk_id = $chunk[0];
		$chunk = array(
			'isa' => 'fr',
			'synset-id' => intval( $chunk[4] ),
			'f-num' => intval( $chunk[6] ),
			'w-num' => intval( $chunk[8] ),			
		);	
		
		echo ( chunk_to_string( $chunk_id, $chunk, $format ) . "\n" );		
	}
	
	//
	
	$formats = array( 'smem', 'sqlite', 'mysql' );
	
	function chunk_to_string( $chunk_id, $chunk, $format )
	{
		$return_val = '';
		
		if ( $format == 'smem' )
		{
			$return_val = '(';
			
			$return_val .= ( '<' . $chunk_id . '>' );
			
			foreach ( $chunk as $slot => $value )
			{
				$return_val .= ( ' ^' . $slot . ' ' . $value );
			}
			
			$return_val .= ')';
		}
		else if ( ( $format == 'sqlite' ) || ( $format == 'mysql' ) )
		{
			$isa = $chunk['isa'];
			unset( $chunk['isa'] );
			
			$return_val = ( 'INSERT INTO wn_chunk_' . $isa . ' (chunk_id,' . str_replace( '-', '_', implode( ',', array_keys( $chunk ) ) ) . ') VALUES (' . ( '\'' . $chunk_id . '\',' ) . str_replace( '|', "'", str_replace( "'", "''", implode( ',', array_values( $chunk ) ) ) ) . ');' );
		}
		
		return $return_val;
	}
	
	function format_open( $format )
	{
		if ( $format == 'smem' )
		{
			echo ( 'smem --add {' . "\n" );	
		}
		else if ( ( $format == 'sqlite' ) || ( $format == 'mysql' ) )
		{
			$return_val = array();
			
			$return_val[] = ( 'BEGIN;' );
			
			$return_val[] = ( 'CREATE TABLE wn_chunk_s (chunk_id TEXT, synset_id INTEGER, w_num INTEGER, word TEXT, word_lower TEXT, ss_type TEXT, sense_number INTEGER, tag_count INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_sk (chunk_id TEXT, synset_id INTEGER, w_num INTEGER, sense_key TEXT);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_g (chunk_id TEXT, synset_id INTEGER, gloss TEXT);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_syntax (chunk_id TEXT, synset_id INTEGER, w_num INTEGER, syntax TEXT);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_hyp (chunk_id TEXT, synset_id INTEGER, synset_id2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_ins (chunk_id TEXT, synset_id INTEGER, synset_id2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_ent (chunk_id TEXT, synset_id INTEGER, synset_id2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_sim (chunk_id TEXT, synset_id INTEGER, synset_id2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_mm (chunk_id TEXT, synset_id INTEGER, synset_id2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_ms (chunk_id TEXT, synset_id INTEGER, synset_id2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_mp (chunk_id TEXT, synset_id INTEGER, synset_id2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_der (chunk_id TEXT, synset_id INTEGER, w_num1 INTEGER, synset_id2 INTEGER, w_num2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_cls (chunk_id TEXT, synset_id INTEGER, synset_id2 INTEGER, class_type TEXT);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_cs (chunk_id TEXT, synset_id INTEGER, synset_id2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_vgp (chunk_id TEXT, synset_id INTEGER, w_num1 INTEGER, synset_id2 INTEGER, w_num2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_at (chunk_id TEXT, synset_id INTEGER, synset_id2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_ant (chunk_id TEXT, synset_id INTEGER, w_num1 INTEGER, synset_id2 INTEGER, w_num2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_sa (chunk_id TEXT, synset_id INTEGER, w_num1 INTEGER, synset_id2 INTEGER, w_num2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_ppl (chunk_id TEXT, synset_id INTEGER, w_num1 INTEGER, synset_id2 INTEGER, w_num2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_per (chunk_id TEXT, synset_id INTEGER, w_num1 INTEGER, synset_id2 INTEGER, w_num2 INTEGER);' );
			$return_val[] = ( 'CREATE TABLE wn_chunk_fr (chunk_id TEXT, synset_id INTEGER, f_num INTEGER, w_num INTEGER);' );
			
			$return_val = implode( "\n", $return_val );
			if ( $format != 'sqlite' )
			{
				$return_val = str_replace( 'TEXT', 'VARCHAR(250)', $return_val );
			}
			
			echo $return_val;
		}
	}
	
	function format_close( $format )
	{
		if ( $format == 'smem' )
		{
			echo ( '}' . "\n" );
		}
		else if ( ( $format == 'sqlite' ) || ( $format == 'mysql' ) )
		{
			echo ( 'COMMIT;' . "\n" );
		}
	}
	
	//
	
	function usage( &$argv )
	{
		global $formats;
		
		echo "\n";
		echo ( 'php ' . $argv[0] . ' <format: ' . implode( ', ', $formats ) . '> [path to WN-Lexical data file]' . "\n" );
		echo ( 'if no argument #2, stdin is WN-Lexical data' . "\n\n" );
	}
	
	if ( ( ( $argc != 2 ) && ( $argc != 3 ) ) || ( !in_array( $argv[1], $formats ) ) )
	{
		usage( $argv );
		exit;
	}
	
	$format = $argv[1];
	
	$input_file = 'php://stdin';
	if ( $argc == 3 )
	{
		$input_file = $argv[2];
	}
	
	//
	
	format_open( $format );
	{
		$std_in = fopen( $input_file, 'r' );	
		if ( $std_in )
		{			
			while ( !feof( $std_in ) )
			{
				$line = fgets( $std_in );
				
				if ( $line !== false )
				{				
					$line = str_replace( '\"', '\'', str_replace( "''", "'", trim( $line ) ) );
					$line = str_getcsv( substr( $line, 1, strlen( $line ) - 2 ), ' ', '"' );
					
					$chunk_type = $line[2];
					if ( isset( $handling_functions[ $chunk_type ] ) && ( $handling_functions[ $chunk_type ] !== false ) )
					{
						$f_name = $handling_functions[ $chunk_type ];					
						$f_name( $line, $format );
					}
				}			
			}
			
			fclose( $std_in );
		}
	}
	format_close( $format );

?>
