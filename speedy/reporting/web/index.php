<?php

	{
		require 'common/private/start.inc.php';
		
		$page_info['title'] = 'Reports';
	}
	
?>

	<?php
		$exps = exp_list();
		
		// tabs -> experiment -> (file->title)
		$reports = array();
		@include( 'index.inc.php' );
		
		if ( !empty( $reports ) )
		{
			function tab_contents( $tab_id )
			{
				global $exps;
				global $reports;
				
				$reps = $reports[ $tab_id ];
				
				if ( !empty( $reps ) )
				{
					foreach ( $exps as $exp_id => $exp_name )
					{
						if ( isset( $reps[ $exp_id ] ) )
						{
							echo '<div class="section">';
								echo '<div class="title">';
									echo htmlentities( $exp_name );
								echo '</div>';
							
								echo '<div class="body">';
									echo '<ul>';
										foreach ( $reps[ $exp_id ] as $file_name => $report_name )
										{
											echo '<li><a href="' . htmlentities( $file_name ) . '">' . htmlentities( $report_name ) . '</a></li>';
										}
									echo '</ul>';
								echo '</div>';
							echo '</div>';
						}
					}
				}
				else
				{
					echo '<div class="section">';
						echo '<div class="body">';
							echo '<p>No reports found.</p>';
						echo '</div>';
					echo '</div>';
				}
			}
			
			
			$tabs = array();
			$tab_counter = 0;
			foreach ( $reports as $tab_title => $reps )
			{
				$new_key = ( 'tab-' . $tab_counter++ );
				
				$tabs[ $new_key ] = $tab_title;
				$reports[ $new_key ] =& $reports[ $tab_title ]; 
			}
			report_create_tabs( 'tabs', $tabs, 'tab_contents' );
		}
		else
		{
			echo '<div class="section">';
				echo '<div class="body">';
					echo '<p>No reports found.</p>';
				echo '</div>';
			echo '</div>';
		}
	?>

<?php
	
	{
		require 'common/private/end.inc.php';
	}

?>
