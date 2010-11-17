<?php

	{
		require 'common/private/start.inc.php';
		
		$page_info['title'] = 'Sample 4';
	}
	
?>

	<?php
		
		$exp_id = 5;
		$format = ( ( isset( $_GET['format'] ) )?( $_GET['format'] ):( 'web' ) );
		$key = ( ( isset( $_GET['key'] ) )?( $_GET['key'] ):( '' ) );
		
		// report data
		$my_data = new report_data();
		
		// data sets are really only different in their handling of y-field
		foreach ( array( 'time', 'rss' ) as $data_key )
		{
			$schema = array( 'machine'=>EXP_TYPE_STRING, 'dcs'=>EXP_TYPE_INT, $data_key=>EXP_TYPE_DOUBLE );
			
			$sql = ( 'SELECT' . 
					' ' . exp_field_name( 'machine' ) . ' AS machine' . 
					', (FLOOR(' . exp_field_name( 'decisions' ) . '/10000000)) AS dcs' . 
					', AVG(' . exp_field_name( $data_key ) . ( ( $data_key == 'time' )?(''):('/1024') ) . ') AS ' . $data_key . 
					' FROM' . 
					' ' . exp_table_name( $exp_id ) .
					' WHERE' . 
					' ' . exp_field_name( 'connection' ) . '=' . db_quote_smart( 'cpp', $db ) .
					' AND ' . exp_field_name( 'branch' ) . '=' . db_quote_smart( 'nlderbin-epmem-smem', $db ) .
					' AND ' . exp_field_name( 'revision' ) . '=' . db_quote_smart( 11581, $db ) .
					' GROUP BY' .
					' ' . exp_field_name( 'machine' ) .
					', ' . exp_field_name( 'decisions' ) .
					' ORDER BY' .
					' ' . exp_field_name( 'machine' ) . ' DESC' .
					', ' . exp_field_name( 'decisions' ) . ' ASC' );
			
			$my_data->add_set( $data_key, $sql, $schema );
		}
		
		if ( ( $format === 'csv' ) && $my_data->set_exists( $key ) )
		{
			$page_info['type'] = 'blank';
			
			echo tables_csv( $my_data->get_schema( $key ), $my_data->get_data( $key ) );
		}
		else if ( ( $format === 'img' ) && $my_data->set_exists( $key ) )
		{
			$page_info['type'] = 'blank';
			require 'common/private/phplot/phplot.php';
			
			$graph_data = $my_data->convert_set_data( $key, 'dcs', false, $key, 'machine' );
			
			$plot = new PHPlot( 1000, 300 );
			$plot->SetDataValues( $graph_data['data'] );
			$plot->SetPlotAreaWorld( NULL, 0 );
			$plot->SetLegendPixels( 40, 0 );
			
			foreach ( $graph_data['sets'] as $set )
			{
				$plot->SetLegend( $set );
			}
			
			$plot->SetDataType('data-data');				
			$plot->SetXTickIncrement( $graph_data['stats']['x-max'] / count( $graph_data['data'] ) );
			
			// subtle differences between charts
			if ( $key == 'time' )
			{
				$plot->SetTitle( 'CPU Time (s) vs. Decisions (x10M)' );
			}
			else
			{
				$plot->SetTitle( 'Memory Usage (MB) vs. Decisions (x10M)' );
			}
			
			$plot->DrawGraph();
		}
		else
		{			
			function tab_contents( $tab_id )
			{
				global $my_data;
				
				echo '<div class="section">';
					echo '<div class="body">';
				
						echo ( '<img src="?format=img&key=' . $tab_id . '" />' );
				
						echo tables_make_perty( $my_data->get_schema( $tab_id ), $my_data->get_data( $tab_id ) );
						echo ( '<a href="?format=csv&key=' . $tab_id . '">download as csv</a>' );
				
						echo '<pre class="sh_sql">';
							echo htmlentities( misc_sql_break( $my_data->get_sql( $tab_id ) ) );
						echo '</pre>';
				
					echo '</div>';
				echo '</div>';
			}
			
			$tabs = array(
				'time' => 'Compare Time',
				'rss' => 'Compare Memory',
			);
			report_create_tabs( 'tabs', $tabs, 'tab_contents' );
		}
		
	?>

<?php
	
	{
		require 'common/private/end.inc.php';
	}

?>
