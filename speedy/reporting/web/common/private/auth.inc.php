<?php

	{
		$authorized_modifiers = array(
			'AA.BB.CC.DD',
			'WW.XX.YY.ZZ',
		);
		
		// default
		define( 'AUTH_MODIFIER', in_array( $_SERVER['REMOTE_ADDR'], $authorized_modifiers ) );
		
		// use if you provide your own authentication procedures
		// define( 'AUTH_MODIFIER', true );
	}

?>
