<?php

	{
		require 'common/private/start.inc.php';
		
		$page_info['title'] = 'Sample 5';
	}
	
?>

	<?php
		
		$format = ( ( isset( $_GET['format'] ) )?( $_GET['format'] ):( 'web' ) );
		
		// report data
		$schema = array( 'x1'=>EXP_TYPE_INT, 'x2'=>EXP_TYPE_INT, 'y1'=>EXP_TYPE_INT, 'y2'=>EXP_TYPE_INT, 'z'=>EXP_TYPE_DOUBLE );
		$my_data = array();
		
		for ( $x=1; $x<=10; $x++ )
		{
			for ( $y=1; $y<=10; $y++ )
			{
				$my_data[] = array( 'x1'=>$x, 'x2'=>$y, 'y1'=>$y, 'y2'=>$x, 'z'=>( pow( $x, 3 ) + pow( $y, 2 ) ) );
			}
		}
		
		
		if ( $format === 'csv' )
		{
			$page_info['type'] = 'blank';
			
			echo tables_csv( $schema, $my_data );
		}
		else if ( $format === 'img' )
		{
			require 'common/private/gnuplot.inc.php';
			
			$page_info['type'] = 'blank';
			$plot = new gnuplot_plot();
			
			$plot->set_data( '3d', $my_data );
			
			$plot->add_config('reset');
			$plot->add_config('set style data points');
			$plot->add_config('set key');
			$plot->add_config('set grid');
			$plot->add_config('set dgrid3d 10,10');
			
			if ( isset( $_GET['top'] ) )
			{
				$plot->add_config('set view 0,0');
				$plot->add_config('set contour');
				$plot->add_config('set nosurface');
				
				if ( $_GET['top'] == '1' )
				{
					$plot->add_config('splot "' . gnuplot_plot::data_file_const('3d') . '" using 1:2:3 with lines t "3d-1"');
				}
				else
				{
					$plot->add_config('splot "' . gnuplot_plot::data_file_const('3d') . '" using 2:1:3 with lines t "3d-2"');
				}
			}
			else
			{
				$plot->add_config('splot "' . gnuplot_plot::data_file_const('3d') . '" using 1:2:3 with lines t "3d-1", "' . gnuplot_plot::data_file_const('3d') . '" using 2:1:3 with lines t "3d-2"');
			}
			
			$plot->plot( array( '3d'=>array('x1','y1','z') ) );
		}
		else
		{			
			function tab_contents( $tab_id )
			{
				global $my_data;
				global $schema;
				
				echo '<div class="section">';
					echo '<div class="body">';
				
						echo ( '<img src="?format=img' . ( ( $tab_id=='side' )?(''):('&top=' . $tab_id) ) . '" />' );
				
						echo tables_make_perty( $schema, $my_data );
						echo ( '<a href="?format=csv">download as csv</a>' );
				
					echo '</div>';
				echo '</div>';
			}
			
			$tabs = array(
				'side' => 'Side',
				'1' => 'Top-1',
				'2' => 'Top-2',
			);
			report_create_tabs( 'tabs', $tabs, 'tab_contents' );
		}
				
				
	?>

<?php
	
	{
		require 'common/private/end.inc.php';
	}

?>
