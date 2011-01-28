<?php

	function misc_shorten( $str, $len )
	{
		if ( strlen( $str ) > $len )
		{
			$str = ( substr( $str, 0, ( $len - 3 ) ) . '...' );
		}
			
		return $str;
	}
	
	function misc_sql_break( $sql )
	{
		$breaks = array( 'FROM', 'WHERE', 'GROUP BY', 'HAVING', 'ORDER BY', 'UNION' );
		
		foreach ( $breaks as $break )
		{
			$sql = str_replace( ( ' ' . $break . ' ' ), ( "\n" . $break . ' ' ), $sql );
		}
		
		return $sql;
	}

?>
