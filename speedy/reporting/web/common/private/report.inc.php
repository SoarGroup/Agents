<?php

	// tabs  = array( id=>title )
	// callback = 'function_name' || array( object, 'function_name' ) : ( $id )
	function report_create_tabs( $tab_id, $tabs, $callback )
	{
		global $page_info;
		
		$page_info['head'] .= jquery_tabs( $tab_id );
		
		echo ( '<div id="' . htmlentities( $tab_id ) . '">' );
		
			echo '<ul>';
			foreach ( $tabs as $tk => $tv )
			{
				echo ( '<li><a href="#' . htmlentities( $tk ) . '">' . htmlentities( $tv ) . '</a></li>' );
			}
			echo '</ul>';
		
			foreach ( $tabs as $tk => $tv )
			{
				echo ( '<div id="' . htmlentities( $tk ) . '">' );
				
					call_user_func_array( $callback, array( $tk ) );
				
				echo '</div>';
			}
		
		echo '</div>';
	}
	
?>
