<?php

	{
		$authorized_modifiers = array(
		);
		@include( 'auth-config.inc.php' );
		
		// default
		define( 'AUTH_MODIFIER', in_array( $_SERVER['REMOTE_ADDR'], $authorized_modifiers ) );
		
		// use if you provide your own authentication procedures
		// define( 'AUTH_MODIFIER', true );
		
		unset( $authorized_modifiers );
	}

?>
