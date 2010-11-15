<?php

	{
		require 'common/private/start.inc.php';
		
		$page_info['title'] = 'Reports';
		$page_info['head'] = jquery_tabs( 'tabs' );
	}
	
?>

	<?php
		$exps = exp_list();
		
		// tabs -> experiment -> (file->title)
		$reports = array();
		@include( 'index.inc.php' );
		
		if ( !empty( $reports ) )
		{
			echo '<div id="tabs">';
			
				echo '<ul>';
				
					$tab_counter = 0;
					foreach ( $reports as $tab_id => $reps )
					{
						echo '<li><a href="#tabs-' . htmlentities( $tab_counter++ ) . '">' . htmlentities( $tab_id ) . '</a></li>';
					}
				
				echo '</ul>';
				
				$tab_counter = 0;
				foreach ( $reports as $tab_id => $reps )
				{
					echo '<div id="tabs-' . htmlentities( $tab_counter++ ) . '">';
					
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
					
					echo '</div>';
				}
			
			echo '</div>';
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
