<?php
	
	function _wn_sensekey_to_pos( $num )
	{
		switch ( $num )
		{
			case 1:
				return 'n';
				
			case 2:
				return 'v';
				
			case 3:
				return 'a';
				
			case 4:
				return 'r';
				
			case 5:
				return 's';
				
			default:
				echo 'bad sense key';
				exit;
		}
	}
	
	function wn_sensekey_to_pos( $key )
	{
		$key = explode( ':', $key );
		
		return _wn_sensekey_to_pos( intval( $key[0] ) );
	}
	
	function wn_is_adjective( $pos )
	{
		return ( ( $pos == 'a' ) || ( $pos == 's' ) );
	}
	
	// param 1: file with parsed semcor
	// param 2: SQLite3 object
	// output: array( sentences=array( queries=array( 'word', 'pos', 'options'=>array( synset_id=>tag-count ), 'assignments'=>array( synset_id ) ) ) )
	function wn_semcor_parse( $semcor, $db )
	{
		$output = array();
		$current_sentence = array();
		
		// prep database statements
		$query_statements = array();
		{
			$query_statements['key'] = $db->prepare( 'SELECT synset_id, w_num FROM wn_chunk_sk WHERE sense_key=:sk' );
			$query_statements['key-verify'] = $db->prepare( 'SELECT DISTINCT word FROM wn_chunk_s WHERE synset_id=:synset AND w_num=:wnum' );
			
			$query_statements['sense-confirm'] = $db->prepare( 'SELECT * FROM wn_chunk_s WHERE synset_id=:synset AND w_num=:wnum AND sense_number=:sn AND ss_type=:st AND word_lower=:word' );
			
			$query_statements['sense_choices'] = $db->prepare( 'SELECT s.*, g.gloss FROM wn_chunk_s s INNER JOIN wn_chunk_g g ON s.synset_id=g.synset_id WHERE s.word_lower=:word AND s.ss_type=:st' );
		}
		
		while ( !feof( $semcor ) )
		{
			$line = fgets( $semcor );
			
			if ( $line !== false )
			{				
				$line = trim( $line );
				
				if ( !empty( $line ) )
				{
					$line = explode( ' ', $line );
					
					if ( $line[0] == 'end-s' )
					{
						$output[] = $current_sentence;
						
						$current_sentence = array();
					}
					else if ( $line[0] == 'wf' )
					{
						$temp = array();
						for ( $i=1; $i<count($line); $i++ )
						{
							$temp2 = explode( '=', $line[$i] );
							
							$temp[ $temp2[0] ] = $temp2[1];
						}
						
						// turns out there are multiple possibilities
						// separated by ;
						$possibilities = array();
						{
							foreach ( explode( ';', $temp['wn-sense-num'] ) as $key => $val )
							{
								$possibilities[ $key ] = array( 'wn-sense-num'=>intval( $val ) );
							}
							
							foreach ( explode( ';', $temp['wn-sense-key'] ) as $key => $val )
							{
								$possibilities[ $key ]['wn-sense-key'] = $val;
							}
							
							foreach ( $possibilities as $k => $p )
							{
								$possibilities[ $k ]['wn-sense-word'] = $temp['wn-sense-word'];
								$possibilities[ $k ]['wn-pos'] = wn_sensekey_to_pos( $p['wn-sense-key'] );
								
								if ( !isset( $temp['wn-pos'] ) )
								{
									$temp['wn-pos'] = $possibilities[ $k ]['wn-pos'];
								}
								else
								{
									if ( ( $possibilities[ $k ]['wn-pos'] != $temp['wn-pos'] ) && 
										!( wn_is_adjective( $possibilities[ $k ]['wn-pos'] ) && wn_is_adjective( $temp['wn-pos'] ) ) )
									{
										echo ( 'possibility pos conflict!' . "\n" );
										var_dump( $temp );
										var_dump( $possibilities);
										exit;
									}
								}
							}
						}
						
						// get synset, w_num
						foreach ( $possibilities as $k => $p )
						{
							$query_statements['key']->bindValue( ':sk', ( $p['wn-sense-word'] . '%' . $p['wn-sense-key'] ), SQLITE3_TEXT );
							$res = $query_statements['key']->execute();
							
							$row = $res->fetchArray( SQLITE3_ASSOC );
							if ( !is_array( $row ) )
							{
								echo ( 'bad key search!!!' . "\n" );
								var_dump( $line );
								var_dump( $temp );
								var_dump( $possibilities );
								exit;
							}
							
							// there shouldn't be multiple returns unless:
							// - they are identical
							// - the matching sense differs in case only
							$next = $res->fetchArray( SQLITE3_ASSOC );
							while ( is_array( $next ) )
							{
								if ( $row['synset_id'] != $next['synset_id'] )
								{
									echo ( 'multiple returns:' . "\n" );
									var_dump( $line );
									var_dump( $row );
									var_dump( $next );
									exit;
								}
								else if ( $row['w_num'] != $next['w_num'] )
								{
									$v_words = array( 'first'=>array( 'num'=>$row['w_num'] ), 'second'=>array( 'num'=>$next['w_num'] ) );
									
									foreach ( $v_words as $v_key => $v_val )
									{
										$query_statements['key-verify']->bindValue( ':synset', $next['synset_id'], SQLITE3_INTEGER );
										$query_statements['key-verify']->bindValue( ':wnum', $v_val['num'], SQLITE3_INTEGER );
										$res_v = $query_statements['key-verify']->execute();
										
										$row_v = $res_v->fetchArray( SQLITE3_ASSOC );
										if ( !is_array( $row_v ) )
										{
											echo ( 'could not verify multiple return "' . $v_key . '":' . "\n" );
											var_dump( $line );
											var_dump( $row );
											var_dump( $next );
											exit;
										}
										
										if ( is_array( $res_v->fetchArray() ) )
										{
											echo ( 'multiple returns while verifying multiple return "' . $v_key . '":' . "\n" );
											var_dump( $line );
											var_dump( $row );
											var_dump( $next );
											var_dump( $row_v );
											exit;
										}
										
										$v_words[ $v_key ]['word'] = $row_v['word'];
										
										$res_v->finalize();
										$query_statements['key-verify']->reset();
									}
									
									if ( strcasecmp( $v_words['first']['word'], $v_words['second']['word'] ) != 0 )
									{
										echo ( 'multiple return differed in more than case:' . "\n" );
										var_dump( $line );
										var_dump( $row );
										var_dump( $next );
										var_dump( $v_words );
										exit;
									}
								}
								
								$next = $res->fetchArray( SQLITE3_ASSOC );
							}
							
							$possibilities[ $k ]['wn-synset'] = $row['synset_id'];
							$possibilities[ $k ]['wn-word-num'] = $row['w_num'];
							
							$res->finalize();
							$query_statements['key']->reset();
						}
						
						// make sure all possibilities are valid
						foreach ( $possibilities as $k => $p )
						{
							// synset_id=:synset AND w_num=:wnum AND sense_number=:sn AND ss_type=:st AND word=:word
							$query_statements['sense-confirm']->bindValue( ':synset', $p['wn-synset'], SQLITE3_INTEGER );
							$query_statements['sense-confirm']->bindValue( ':wnum', $p['wn-word-num'], SQLITE3_INTEGER );
							$query_statements['sense-confirm']->bindValue( ':sn', $p['wn-sense-num'], SQLITE3_INTEGER );
							$query_statements['sense-confirm']->bindValue( ':st', $p['wn-pos'], SQLITE3_TEXT );
							$query_statements['sense-confirm']->bindValue( ':word', str_replace( '_', ' ', $p['wn-sense-word'] ), SQLITE3_TEXT );
							
							$res = $query_statements['sense-confirm']->execute();
							
							$row = $res->fetchArray( SQLITE3_ASSOC );
							if ( !is_array( $row ) )
							{
								echo ( 'invalid possibility' . "\n" );
								var_dump( $p );
								exit;
							}
							
							$res->finalize();
							$query_statements['sense-confirm']->reset();
						}
						
						// formulate query
						{
							$query = array();
							$query['word'] = str_replace( '_', ' ', $temp['wn-sense-word'] );
							
							$query['pos'] = array();
							if ( wn_is_adjective( $temp['wn-pos'] ) )
							{
								$query['pos'][] = 'a';
								$query['pos'][] = 's';
							}
							else
							{
								$query['pos'][] = $temp['wn-pos'];
							}
							
							$query['options'] = array();
							foreach ( $query['pos'] as $pos )
							{
								// SELECT * FROM wn_chunk_s WHERE word_lower=:word AND ss_type=:st
								$query_statements['sense_choices']->bindValue( ':word', $query['word'], SQLITE3_TEXT );
								$query_statements['sense_choices']->bindValue( ':st', $pos, SQLITE3_TEXT );									
								$res = $query_statements['sense_choices']->execute();
								
								do
								{
									$row = $res->fetchArray( SQLITE3_ASSOC );
									
									if ( is_array( $row ) )
									{
										if ( !isset( $query['options'][ $row['sense_number'] ] ) )
										{
											$query['options'][ $row['sense_number'] ] = $row;
										}
										else
										{
											$good = true;
											foreach ( $row as $key => $val )
											{
												if ( !in_array( $key, array( 'chunk_id', 'word', 'w_num', 'gloss' ) ) && ( $val != $query['options'][ $row['sense_number'] ][ $key ] ) )
												{
													$good = false;
												}
											}
											
											if ( !$good )
											{
												echo ( 'duplicate sense number!!!' . "\n" );
												var_dump( $temp );
												var_dump( $query['options'][ $row['sense_number'] ] );
												var_dump( $row );
												exit;
											}
										}
									}
								} while ( is_array( $row ) );
								
								$res->finalize();
								$query_statements['sense_choices']->reset();
							}
							
							$options = array();
							foreach ( $query['options'] as $k => $o )
							{
								$options[ $o['synset_id'] ] = array( 'tag-count'=>$o['tag_count'], 'gloss'=>$o['gloss'] );
							}
							$query['options'] = $options;
							
							$assignments = array();
							foreach ( $possibilities as $p )
							{
								$assignments[ $p['wn-synset'] ] = $p['wn-synset'];
							}
							$query['assignments'] = array_values( $assignments );
							
							if ( !( count( $query['assignments'] ) == count( array_intersect( $query['assignments'], array_keys( $query['options'] ) ) ) ) )
							{
								echo ( 'bad assignments/options combination!!!' . "\n" );
								var_dump( $query );
								exit;
							}
							
							$current_sentence[] = $query;
						}
					}
				}
			}			
		}
		
		return $output;
	}
	
	function _wn_sql_quote( $in )
	{
		return ( "'" . str_replace( '\'', '\'\'', $in ) . "'" );
	}
	
	$sql_formats = array( 'mysql', 'sqlite' );
	
	function wn_semcor_sql( &$queries, $c_id, $format, $table_create = false )
	{
		global $sql_formats;
		
		$r = array();
		
		if ( in_array( $format, $sql_formats ) )
		{			
			$r[] = 'BEGIN;';			
			
			// tables
			if ( $table_create )
			{
				$r[] = 'CREATE TABLE wsd_sentences ( c_id INTEGER, s_id INTEGER, w_id INTEGER, w_lex TEXT, w_pos TEXT );';
				$r[] = 'CREATE INDEX wsd_s_c_s_w ON wsd_sentences (c_id,s_id,w_id);';
				$r[] = 'CREATE INDEX wsd_s_s ON wsd_sentences (s_id);';
				$r[] = 'CREATE INDEX wsd_s_w ON wsd_sentences (w_id);';
				$r[] = 'CREATE INDEX wsd_s_lex ON wsd_sentences (w_lex);';
				$r[] = 'CREATE INDEX wsd_s_pos ON wsd_sentences (w_pos);';
				$r[] = 'CREATE INDEX wsd_s_lex_pos ON wsd_sentences (w_lex, w_pos);';
				
				$r[] = 'CREATE TABLE wsd_word_options ( c_id INTEGER, s_id INTEGER, w_id INTEGER, w_synset INTEGER, w_tag_count INTEGER, w_gloss TEXT );';
				$r[] = 'CREATE INDEX wsd_wo_c_s_w_syn ON wsd_word_options (c_id,s_id,w_id,w_synset);';
				
				$r[] = 'CREATE TABLE wsd_word_assignments ( c_id INTEGER, s_id INTEGER, w_id INTEGER, w_synset INTEGER );';
				$r[] = 'CREATE INDEX wsd_wa_c_s_w ON wsd_word_assignments (c_id,s_id,w_id);';
				
				if ( $format == 'mysql' )
				{
					$r[] = 'CREATE TABLE wsd_ambiguity ( c_id INTEGER, w_pos VARCHAR(1), ambig DOUBLE, ambig_prop DOUBLE );';
				}
				else
				{
					$r[] = 'CREATE TABLE wsd_ambiguity ( c_id INTEGER, w_pos TEXT, ambig REAL, ambig_prop REAL );';
				}
				$r[] = 'CREATE INDEX wsd_a_c_w ON wsd_ambiguity (c_id,w_pos);';
				$r[] = 'CREATE INDEX wsd_a_w ON wsd_ambiguity (w_pos);';
			}
			
			foreach ( $queries as $s_id => $words )
			{
				foreach ( $words as $w_id => $word )
				{
					$r[] = ( 'INSERT INTO wsd_sentences (c_id,s_id,w_id,w_lex,w_pos) VALUES (' . implode( ',', array( $c_id, $s_id, $w_id, _wn_sql_quote( $word['word'] ), _wn_sql_quote( ( ( count( $word['pos'] ) == 1 )?( $word['pos'][0] ):( 'a' ) ) ) ) ) . ');' );
					
					foreach ( $word['options'] as $w_synset => $opt )
					{
						$r[] = ( 'INSERT INTO wsd_word_options (c_id,s_id,w_id,w_synset,w_tag_count,w_gloss) VALUES (' . implode( ',', array( $c_id, $s_id, $w_id, $w_synset, $opt['tag-count'], _wn_sql_quote( $opt['gloss'] ) ) ) . ');' );
					}
					
					foreach ( $word['assignments'] as $w_synset )
					{
						$r[] = ( 'INSERT INTO wsd_word_assignments (c_id,s_id,w_id,w_synset) VALUES (' . implode( ',', array( $c_id, $s_id, $w_id, $w_synset ) ) . ');' );
					}
				}
			}
			
			$r[] = 'DELETE FROM wsd_ambiguity;';
			if ( $format == 'mysql' )
			{				
				$r[] = 'INSERT INTO wsd_ambiguity SELECT c.c_id, c.w_pos, c.ambig, (c.ambig_ct/(SELECT COUNT(*) FROM wsd_sentences WHERE w_pos=c.w_pos AND c_id=c.c_id)) AS ambig_prop FROM (SELECT b.c_id, b.w_pos, b.ambig, COUNT(*) AS ambig_ct FROM (SELECT a.c_id, a.w_pos, (a.a_ct/a.opt_ct) AS ambig FROM (SELECT s.c_id, s.s_id, s.w_id, s.w_pos, (SELECT COUNT(*) AS opt_ct FROM wsd_word_options wo WHERE wo.c_id=s.c_id AND wo.s_id=s.s_id AND wo.w_id=s.w_id) AS opt_ct, (SELECT COUNT(*) AS a_ct FROM wsd_word_assignments wa WHERE wa.c_id=s.c_id AND wa.s_id=s.s_id AND wa.w_id=s.w_id) AS a_ct FROM wsd_sentences s GROUP BY s.c_id, s.s_id, s.w_id) a) b GROUP BY b.c_id, b.w_pos, b.ambig) c ORDER BY c.c_id ASC, c.w_pos ASC, c.ambig ASC;';
			}
			else
			{
				$r[] = 'INSERT INTO wsd_ambiguity SELECT c.c_id, c.w_pos, c.ambig, ((CAST (c.ambig_ct AS REAL))/(SELECT COUNT(*) FROM wsd_sentences WHERE w_pos=c.w_pos AND c_id=c.c_id)) AS ambig_prop FROM (SELECT b.c_id, b.w_pos, b.ambig, COUNT(*) AS ambig_ct FROM (SELECT a.c_id, a.w_pos, ((CAST (a.a_ct AS REAL))/a.opt_ct) AS ambig FROM (SELECT s.c_id, s.s_id, s.w_id, s.w_pos, (SELECT COUNT(*) AS opt_ct FROM wsd_word_options wo WHERE wo.c_id=s.c_id AND wo.s_id=s.s_id AND wo.w_id=s.w_id) AS opt_ct, (SELECT COUNT(*) AS a_ct FROM wsd_word_assignments wa WHERE wa.c_id=s.c_id AND wa.s_id=s.s_id AND wa.w_id=s.w_id) AS a_ct FROM wsd_sentences s GROUP BY s.c_id, s.s_id, s.w_id) a) b GROUP BY b.c_id, b.w_pos, b.ambig) c ORDER BY c.c_id ASC, c.w_pos ASC, c.ambig ASC;';
			}
			
			$r[] = 'COMMIT;';
		}
		$r = implode( "\n", $r );
		
		if ( $format == 'mysql' )
		{
			$r = str_replace( 'TEXT', 'VARCHAR(500)', $r );
		}
		
		return $r;
	}
	
	//
	
	function usage( &$argv )
	{
		global $sql_formats;
		
		echo ( "\n" );
		echo ( 'php ' . $argv[0] . ' <WordNet SQLite3 file> <SQL format: ' . implode( ', ', $sql_formats ) . '> <Corpus ID: integer> <Create Tables: y/n> [path to parsed semcor file]' . "\n" );
		echo ( 'if no argument #3, read from stdin' . "\n\n" );
	}
	
	if ( ( $argc != 5 ) && ( $argc != 6 ) )
	{
		usage( $argv );
		exit;
	}
	
	if ( !in_array( $argv[2], $sql_formats ) )
	{
		usage( $argv );
		exit;
	}
	
	$input_path = 'php://stdin';
	if ( $argc == 6 )
	{
		$input_path = $argv[5];
	}
	
	$db = NULL;
	try 
	{
		$db = new SQLite3( $argv[1], SQLITE3_OPEN_READONLY );
	}
	catch (Exception $e)
	{
		echo ( 'Invalid SQLite3 file!' . "\n" );
		usage( $argv );
		exit;
	}
	
	{
		$file_in = fopen( $input_path, 'r' );
		if ( $file_in )
		{
			$queries = wn_semcor_parse( $file_in, $db );
			
			fclose( $file_in );
			
			echo wn_semcor_sql( $queries, intval( $argv[3] ), $argv[2], ( strtoupper( $argv[4] ) == 'Y' ) );
		}
	}
	
?>
