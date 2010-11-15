<?php

	function jquery_tabs( $id )
	{
		return '<script type="text/javascript">$(function() {$("#' . $id . '").tabs(); });</script>';
	}
	
	function jquery_button( $id )
	{
		return '<script type="text/javascript">$("#' . $id . '").button();</script>';
	}

?>
