<?php
	
	@include( 'gnuplot-config.inc.php' );
	
	if ( defined( 'GNUPLOT' ) )
	{
		class gnuplot_plot
		{
			private $config;
			private $data;
			
			function __construct()
			{
				$this->config = array();
				$this->data = array();
			}
			
			static function data_file_const( $set )
			{
				return ( '{data_' . $set . '}' );
			}
			
			function get_data( $set )
			{
				if ( isset( $this->data[ $set ] ) )
				{
					return $this->data[ $set ];
				}
				else
				{
					return null;
				}
			}
			
			function get_config()
			{
				return $this->config;
			}
			
			function set_data( $set, $table )
			{
				$this->data[ $set ] = $table;
			}
			
			function add_config( $conf, $end = true )
			{
				if ( $end )
				{
					$this->config[] = $conf;
				}
				else
				{
					array_unshift( $this->config, $conf );
				}
			}
			
			function plot( $columns, $content_type = null )
			{
				$fn_conf = tempnam( GNUPLOT_TMP, 'conf-' );
				$fn_img = tempnam( GNUPLOT_TMP, 'img-' );
				
				// default format: svg
				if ( is_null ( $content_type ) )
				{
					$this->add_config( 'set terminal svg', false );
					$content_type = 'image/svg+xml';
				}
				
				// set output image file
				$this->add_config( 'set out "' . $fn_img . '"', false );
				
				// data file names
				$data_files = array();
				
				// write data
				foreach ( $this->data as $set => $data )
				{
					// get file name
					$data_files[ $set ] = tempnam( GNUPLOT_TMP, 'data-' );
					
					// write appropriate columns
					$out_data = array();
					foreach ( $data as $row )
					{
						$out_row = array();
						
						foreach ( $columns[ $set ] as $c )
						{
							$out_row[] = $row[ $c ];
						}
						
						$out_data[] = implode( ' ', $out_row );
					}
					
					file_put_contents( $data_files[ $set ], implode( "\n", $out_data ) );
				}
				
				// write configuration
				{
					$config_out = array();
					
					foreach ( $this->config as $v )
					{
						foreach ( $data_files as $set => $fn )
						{
							$v = str_replace( gnuplot_plot::data_file_const( $set ), $fn, $v );
						}
						
						$config_out[] = $v;
					}
					
					file_put_contents( $fn_conf, implode( "\n", $config_out ) );
				}
				
				// execute gnuplot
				shell_exec( 'cat ' . $fn_conf . ' | ' . GNUPLOT );
				
				Header( 'Content-type: ' . $content_type );
				readfile( $fn_img );
				
				unlink( $fn_conf );
				unlink( $fn_img );
				
				foreach ( $data_files as $fn )
				{
					unlink( $fn );
				}
			}
		}
	}
	
?>
