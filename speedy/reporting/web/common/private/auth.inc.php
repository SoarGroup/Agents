<?php

	{
		$authorized_modifiers = array(
			'AA.BB.CC.DD',
			'WW.XX.YY.ZZ',
		);
		
		define( 'AUTH_MODIFIER', in_array( $_SERVER['REMOTE_ADDR'], $authorized_modifiers ) );
	}

?>
