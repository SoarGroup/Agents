<?php

	{
		require 'common/private/start.inc.php';
		
		$page_info['title'] = 'Experiments';
		
		{
			$add_field_js = array();
			$add_field_js[] = '<script type="text/javascript">';
			$add_field_js[] = 'num_fields = 1;';
			$add_field_js[] = 'function add_field() {';
			$add_field_js[] = '$("#schema > tbody:last").append("<tr><td><input type=\"text\" name=\"field_name[" + num_fields + "]\" value=\"\" style=\"width: 200px\" /></td><td><select name=\"field_type[" + num_fields + "]\" style=\"width: 100px\"><option value=\"' . htmlentities( EXP_TYPE_INT ) . '\">' . htmlentities( exp_type_2_english( EXP_TYPE_INT ) ) . '</option><option value=\"' . htmlentities( EXP_TYPE_DOUBLE ) . '\">' . htmlentities( exp_type_2_english( EXP_TYPE_DOUBLE ) ) . '</option><option value=\"' . htmlentities( EXP_TYPE_STRING ) . '\">' . htmlentities( exp_type_2_english( EXP_TYPE_STRING ) ) . '</option></select></td></tr>");';
			$add_field_js[] = 'num_fields++;';
			$add_field_js[] = '}';
			$add_field_js[] = '</script>';
			
			$page_info['head'] = ( jquery_tabs( 'tabs' ) . implode( "\n", $add_field_js ) );
		}
	}
	
?>
	
		<?php
			// respond to commands
			$show_forms = true;
			{
				if ( isset( $_GET['cmd'] ) )
				{
					if ( $_GET['cmd'] === 'drop' )
					{
						if ( AUTH_MODIFIER )
						{
							if ( isset( $_GET['exp'] ) )
							{
								if ( exp_valid( $_GET['exp'] ) )
								{
									exp_remove_experiment( $_GET['exp'] );
									
									echo '<div class="section">';
										echo '<div class="body">';
											echo ( '<p class="ui-state-highlight">REMOVED EXPERIMENT: ' . htmlentities( $_GET['exp'] ) . '</p>' );
										echo '</div>';
									echo '</div>';
								}
							}
						}
					}
					else if ( $_GET['cmd'] === 'clear' )
					{
						if ( AUTH_MODIFIER )
						{
							if ( isset( $_GET['exp'] ) )
							{
								if ( exp_valid( $_GET['exp'] ) )
								{
									exp_clear_data( $_GET['exp'] );
									
									echo '<div class="section">';
										echo '<div class="body">';
											echo ( '<p class="ui-state-highlight">CLEARED EXPERIMENT: ' . htmlentities( $_GET['exp'] ) . '</p>' );
										echo '</div>';
									echo '</div>';
								}
							}
						}
					}
					else if ( $_GET['cmd'] == 'add' )
					{
						if ( AUTH_MODIFIER )
						{
							if ( isset( $_GET['exp'] ) && isset( $_GET['field_name'] ) && isset( $_GET['field_type'] ) )
							{
								if ( is_array( $_GET['field_name'] ) && is_array( $_GET['field_type'] ) && ( count( $_GET['field_name'] ) == count( $_GET['field_type'] ) ) )
								{
									$exp_fields = array();
									{
										foreach ( $_GET['field_name'] as $key => $val )
										{
											if ( isset( $_GET['field_type'][ $key ] ) )
											{
												$exp_fields[ trim( strval( $_GET['field_name'][ $key ] ) ) ] = intval( $_GET['field_type'][ $key ] );
											}
										}
									}
									
									if ( count( $exp_fields ) == count( $_GET['field_name'] ) )
									{
										$exp_id = exp_add_experiment( strval( $_GET['exp'] ), $exp_fields );
										
										echo '<div class="section">';
											echo '<div class="body">';
										
												if ( is_null( $exp_id ) )
												{
													echo ( '<p class="ui-state-error">DUPLICATE EXPERIMENT NAME</p>' );
												}
												else
												{
													echo ( '<p class="ui-state-highlight">ADDED EXPERIMENT: ' . htmlentities( $exp_id ) . '</p>' );
												}
											echo '</div>';
										echo '</div>';
									}
								}
							}
						}
					}
					else if ( $_GET['cmd'] == 'data' )
					{
						if ( AUTH_MODIFIER )
						{
							if ( isset( $_GET['exp_id'] ) )
							{
								$exp_id = intval( $_GET['exp_id'] );
								
								if ( exp_valid( $exp_id ) )
								{
									$schema = exp_schema( $exp_id );
									$values = array();
									
									foreach ( $schema as $field_name => $field_type )
									{
										if ( isset( $_GET[ $field_name ] ) )
										{
											$val = $_GET[ $field_name ];
											
											switch ( $field_type )
											{
												case EXP_TYPE_INT: $val = intval( $val ); break;
												case EXP_TYPE_DOUBLE: $val = doubleval( $val ); break;
												case EXP_TYPE_STRING: $val = strval( $val ); break;
											}
											
											$values[ $field_name ] = $val;
										}
									}
									
									if ( count( $schema ) == count( $values ) )
									{
										$res = exp_add_datum( $exp_id, $values );
										
										echo '<div class="section">';
											echo '<div class="body">';
												if ( !is_null( $res ) )
												{
													echo ( '<p class="ui-state-highlight">ADDED DATUM ' . htmlentities( $res ) . ' TO EXPERIMENT ' . htmlentities( $exp_id ) . '</p>' );
												}
												else
												{
													echo '<p class="ui-state-error">INVALID DATA</p>';
												}
											echo '</div>';
										echo '</div>';
									}
								}
							}
						}
					}
					else if ( $_GET['cmd'] == 'view' )
					{
						if ( isset( $_GET['exp'] ) )
						{
							$exp_id = intval( $_GET['exp'] );
							
							if ( exp_valid( $exp_id ) )
							{
								$show_forms = false;
								
								// format
								$format = 'html';
								if ( isset( $_GET['format'] ) )
								{
									if ( in_array( $_GET['format'], array('csv', 'img') ) )
									{
										$format = $_GET['format'];
									}
								}
								
								// data
								$page_no = ( ( isset( $_GET['p'] ) )?( intval( $_GET['p'] ) ):( 0 ) );
								$exp_data = exp_data( $exp_id, ( ( isset( $_GET['qry'] ) )?( db_strip_slashes( strval( $_GET['qry'] ) ) ):( NULL ) ), ( ( $format != 'html' )?( NULL ):( $page_no ) ) );
								
								if ( $format == 'html' )
								{
									$exps = exp_list(false);
									$page_info['title'] = $exps[ $exp_id ];									
									$go_chart = NULL;
									
									echo '<div class="section">';
										echo '<div class="title">';
											echo ( 'Query' );
										echo '</div>';
										echo '<div class="body">';
										
											if ( !is_null( $exp_data['err'] ) )
											{
												echo '<p style="color: red">';
													echo htmlentities( $exp_data['err'] );
												echo '</p>';
											}
											
											echo '<ul>';
												echo '<li>' . htmlentities( '{table} is replaced with the experiment data table' ) . '</li>';
												echo '<li>' . htmlentities( '{primary} is replaced with the experiment data primary key' ) . '</li>';
												echo '<li>' . htmlentities( '{field_*} is replaced with a field name' ) . '</li>';
												echo '<li>For some help getting started, use the <a href="?' . htmlentities( http_build_query( array( 'cmd'=>'query', 'exp'=>$exp_id ) ) ) . '">query builder</a></li>';
											echo '</ul>';
										
											echo '<form method="get" action="">';
												echo '<input type="hidden" name="cmd" value="view" />';
												echo ( '<input type="hidden" name="exp" value="' . htmlentities( $exp_id ) . '" />' );
												
												echo ( '<textarea name="qry" cols="60" rows="4">' . htmlentities( $exp_data['sql'] ) . '</textarea>' );
												echo '<br />';
												echo '<input id="send-query" type="submit" value="query" />';
												echo jquery_button('send-query');
											echo '</form>';
										echo '</div>';
									echo '</div>';
									
									echo '<div id="tabs">';
				
										echo '<ul>';
											echo '<li><a href="#tabs-1">Table</a></li>';
											echo '<li><a href="#tabs-2">Chart</a></li>';
										echo '</ul>';								
									
										echo '<div id="tabs-1">';
										
											echo '<div class="section">';
												echo '<div class="title">';	
													{																									
														// previous
														$prev = '';
														{
															if ( $page_no > 0 )
															{
																$my_query = '';
																
																if ( isset( $_GET['p'] ) )
																{
																	$my_query = array();
																	foreach ( $_GET as $key => $val )
																	{
																		if ( $key != 'p' )
																		{
																			$my_query[ $key ] = db_strip_slashes( strval( $val ) );
																		}
																		else
																		{
																			$my_query[ $key ] = ( $page_no - 1 );
																		}
																	}
																	$my_query = http_build_query( $my_query );
																}
																else
																{
																	$my_query = ( $_SERVER['QUERY_STRING'] . '&p=' . ( $page_no - 1 ) );
																}
																
																$prev = '<a href="?' . htmlentities( $my_query ) . '">previous</a>, ';
															}
														}
														
														// next
														$next = '';
														{														
															if ( count( $exp_data['data'] ) == VIEW_LIMIT )
															{															
																$my_query = '';
																
																if ( isset( $_GET['p'] ) )
																{
																	$my_query = array();
																	foreach ( $_GET as $key => $val )
																	{
																		if ( $key != 'p' )
																		{																
																			$my_query[ $key ] = db_strip_slashes( strval( $val ) );
																		}
																		else
																		{
																			$my_query[ $key ] = ( $page_no + 1 );
																		}
																	}
																	$my_query = http_build_query( $my_query );
																}
																else
																{
																	$my_query = ( $_SERVER['QUERY_STRING'] . '&p=' . ( $page_no + 1 ) );
																}
																
																$next = '<a href="?' . htmlentities( $my_query ) . '">next</a>, ';																
															}
														}														
														
														echo ( 'Query Result (' . ( ( $page_no * VIEW_LIMIT + 1 ) . '-' . ( $page_no * VIEW_LIMIT + count( $exp_data['data'] ) ) . ': ' ) . ( $prev ) . ( $next ) . '<a href="?' . htmlentities( $_SERVER['QUERY_STRING'] ) . '&amp;format=csv' . '">csv</a>)' );
													}
												echo '</div>';
												echo '<div class="body">';
													echo '<pre class="sh_sql">';
														echo htmlentities( misc_sql_break( $exp_data['modified'] ) );
													echo '</pre>';
												
													echo tables_make_perty( $exp_data['schema'], $exp_data['data'] );
												echo '</div>';
											echo '</div>';
											
										echo '</div>';
									
										echo '<div id="tabs-2">';
									
											echo '<div class="section">';
												echo '<div class="title">Configuration</div>';
												echo '<div class="body">';
													echo '<form action="" method="GET" onsubmit="document.getElementById(\'chart-counter\').value++; document.getElementById(\'my-chart\').src=\'?' . ( htmlentities( http_build_query( array( 'cmd'=>'view', 'exp'=>$exp_id, 'format'=>'img', 'qry'=>$exp_data['sql'] ) ) ) ) . '&ct=\' + document.getElementById(\'chart-counter\').value + \'&type=\' + document.getElementById(\'chart-type\').options[document.getElementById(\'chart-type\').selectedIndex].value + \'&x-field=\' + document.getElementById(\'chart-x\').options[document.getElementById(\'chart-x\').selectedIndex].value + \'&y-field=\' + document.getElementById(\'chart-y\').options[document.getElementById(\'chart-y\').selectedIndex].value + \'&set-field=\' + document.getElementById(\'chart-set\').options[document.getElementById(\'chart-set\').selectedIndex].value; document.getElementById(\'chart-div\').style.display=\'\'; return false;">';
														
														echo 'type: ';
														echo '<select id="chart-type">';
														{
															echo '<option value="line">line</option>';
															echo '<option value="bar">bar</option>';
														}
														echo '</select> &nbsp;&nbsp;&nbsp; ';
									
														echo 'x-axis: ';
														echo '<select id="chart-x">';
															foreach ( $exp_data['schema'] as $col => $type )
															{
																echo '<option value="' . htmlentities( $col ) . '">' . htmlentities( $col ) . '</option>';
															}
														echo '</select> &nbsp;&nbsp;&nbsp; ';
									
														echo 'y-axis: ';
														echo '<select id="chart-y">';
														foreach ( $exp_data['schema'] as $col => $type )
														{
															echo '<option value="' . htmlentities( $col ) . '">' . htmlentities( $col ) . '</option>';
														}
														echo '</select> &nbsp;&nbsp;&nbsp; ';
									
														echo 'grouping: ';
														echo '<select id="chart-set"><option value=""></option>';
														foreach ( $exp_data['schema'] as $col => $type )
														{
															echo '<option value="' . htmlentities( $col ) . '">' . htmlentities( $col ) . '</option>';
														}
														echo '</select> &nbsp;&nbsp;&nbsp; ';
														
														echo '<input id="chart-counter" type="hidden" name="chart-counter" value="0" />';
														echo '<input type="submit" value="refresh" />';
													echo '</form>';
												echo '</div>';
											echo '</div>';
											
											echo '<div id="chart-div" class="section" style="display:none">';
												echo '<div class="title">Result</div>';
												echo '<div class="body">';
													echo '<img id="my-chart" src="common/public/favico.ico" />';
												echo '</div>';
											echo '</div>';
										echo '</div>';
										
									echo '</div>';
								}
								else if ( $format == 'csv' )
								{
									$page_info['type'] = 'blank';
									
									echo tables_csv( $exp_data['schema'], $exp_data['data'] );
								}
								else if ( $format == 'img' )
								{
									$page_info['type'] = 'blank';
									require 'common/private/phplot/phplot.php';
									
									$x = $_GET['x-field'];
									$y = $_GET['y-field'];
									$set = ( ( empty( $_GET['set-field'] ) )?(NULL):( $_GET['set-field'] ) );
									$type = $_GET['type'];
									
									//
									
									$graph_data = report_data::convert_data( $exp_data['data'], $x, ( $type == 'bar' ), $y, $set );
									
									$plot = new PHPlot( 1000, 300 );
									$plot->SetDataValues( $graph_data['data'] );
									$plot->SetPlotAreaWorld( NULL, 0 );
									$plot->SetLegendPixels( 40, 0 );
									
									if ( isset( $graph_data['sets'] ) )
									{
										foreach ( $graph_data['sets'] as $set )
										{
											$plot->SetLegend( $set );
										}
									}
									
									if ( $type == 'line' )
									{
										$plot->SetDataType('data-data');
										$plot->SetXTickIncrement( $graph_data['stats']['x-max'] / count( $graph_data['data'] ) );
									}
									else
									{
										$plot->SetDataType('text-data');
										$plot->SetPlotType('bars');
									}
									
									$plot->DrawGraph();
									
								}
							}
						}
					}
					else if ( $_GET['cmd'] == 'query' )
					{
						if ( isset( $_GET['exp'] ) )
						{
							$exp_id = intval( $_GET['exp'] );
							
							if ( exp_valid( $exp_id ) )
							{
								$show_forms = false;
								
								$page_info['title'] = 'Query Builder';
								
								$schema = exp_schema( $exp_id );
								
								echo '<script type="text/javascript">';
								echo 'var wheres = new Array;';
								echo 'var groups = new Array;';
								echo 'var selects = new Array;';
								echo 'var orders = new Array;';
								
								//
								
								echo 'function updateQuery() {';
								
								echo 'var tempWheres = new Array;';
								echo 'for (i=0; i<wheres.length; i++) if ((typeof wheres[i] != "undefined") && (wheres[i]!=null)) tempWheres[tempWheres.length]=wheres[i];';
								echo 'var myWhere=\'\';';
								echo 'if (tempWheres.length) myWhere = (\' WHERE \' + tempWheres.join(\' \' + document.getElementById(\'where-join\').options[document.getElementById(\'where-join\').selectedIndex].value + \' \'));';
								
								echo 'var tempGroups = new Array;';
								echo 'for (i=0; i<groups.length; i++) if ((typeof groups[i] != "undefined") && (groups[i]!=null)) tempGroups[tempGroups.length]=(\'{field_\' + groups[i] + \'}\');';
								echo 'var myGroup=\'\';';
								echo 'if (tempGroups.length) myGroup = (\' GROUP BY \' + tempGroups.join(\',\'));';
								
								echo 'var tempSelects = new Array;';
								echo 'for (i=0; i<selects.length; i++) if ((typeof selects[i] != "undefined") && (selects[i]!=null)) tempSelects[tempSelects.length]=(selects[i]);';
								echo 'var mySelect=\'*\';';
								echo 'if (tempSelects.length) mySelect = tempSelects.join(\',\');';
								
								echo 'var tempOrders = new Array;';
								echo 'for (i=0; i<orders.length; i++) if ((typeof orders[i] != "undefined") && (orders[i]!=null)) tempOrders[tempOrders.length]=(orders[i]);';
								echo 'var myOrder=\'\';';
								echo 'if (tempOrders.length) myOrder = ( \' ORDER BY \' + tempOrders.join(\',\') );';
								
								echo 'var sql = (\'SELECT \' + mySelect + \' FROM {table}\' + myWhere + myGroup + myOrder);';
								echo 'document.getElementById(\'qry-preview\').innerHTML=sql;';
								echo 'sh_highlightDocument();';
								echo 'document.getElementById(\'qry-val\').value=sql;';
								echo '}';
								
								//
								
								echo 'function updateWhere(id) {';
								echo 'if ((document.getElementById(\'where-op-\' + id).selectedIndex!=0) && (document.getElementById(\'where-value-\' + id).value!=\'\')) {';
								echo 'wheres[id]=(\'{field_\' + document.getElementById(\'where-field-\' + id).innerText + \'} \' + document.getElementById(\'where-op-\' + id).options[document.getElementById(\'where-op-\' + id).selectedIndex].value + " \'" + document.getElementById(\'where-value-\' + id).value + "\'");';
								echo '} else {';
								echo 'wheres[id]=null;';
								echo '}';
								echo 'updateQuery();';
								echo '}';
								
								//
								
								echo 'function updateGroup(id) {';
								echo 'if (document.getElementById(\'group-\' + id).checked) {';
								echo 'groups[id]=document.getElementById(\'group-\' + id).value;';
								echo '} else {';
								echo 'groups[id]=null;';
								echo '}';
								echo 'updateQuery();';
								echo '}';
								
								//
								
								echo 'function updateSelect(id) {';
								echo 'if (document.getElementById(\'select-\' + id).checked) {';
								echo 'selects[id]=( document.getElementById(\'select-func-\' + id).options[document.getElementById(\'select-func-\' + id).selectedIndex].value + \'({field_\' + document.getElementById(\'group-\' + id).value + \'})\' );';
								echo '} else {';
								echo 'selects[id]=null;';
								echo '}';
								echo 'updateQuery();';
								echo '}';
								
								//
								
								echo 'function updateOrder(id) {';
								echo 'if (document.getElementById(\'order-\' + id).selectedIndex!=0) {';
								echo 'var temp = document.getElementById(\'order-\' + id).options[document.getElementById(\'order-\' + id).selectedIndex].value;';
								echo 'orders[id]=( \'{field_\' + temp.split(\' \')[0] + \'} \' + temp.split(\' \')[1] );';
								echo '} else {';
								echo 'orders[id]=null;';
								echo '}';
								echo 'updateQuery();';
								echo '}';
								
								//
								
								echo '</script>';
								
								echo '<div class="section">';
								echo '<div class="title">select</div>';
								echo '<div class="body">';
								
								echo '<table>';
								echo '<thead>';
								echo '<tr>';
								echo '<td style="text-decoration: underline; text-align: center; width: 120px">function</td>';
								echo '<td></td>';
								echo '<td style="text-decoration: underline; text-align: center; width: 120px">field</td>';
								echo '<td></td>';
								echo '<td style="text-decoration: underline; text-align: center; width: 60px">return</td>';
								echo '<td style="text-decoration: underline; text-align: center; width: 60px">aggregate</td>';
								echo '</tr>';
								echo '</thead>';
								echo '<tbody>';
								
								$select_counter = 0;
								foreach ( $schema as $field => $type )
								{
									echo '<tr>';
									
									echo '<td style="text-align: center">';
									echo '<select id="select-func-' . htmlentities( $select_counter ) . '" onchange="updateSelect(' . htmlentities( $select_counter ) . ');">';
									echo '<option value=""></option>';
									echo '<option value="AVG">average</option>';
									echo '<option value="COUNT">count</option>';
									echo '<option value="SUM">sum</option>';
									echo '<option value="MAX">max</option>';
									echo '<option value="MIN">min</option>';
									echo '<option value="STDDEV_SAMP">std. deviation</option>';
									echo '</select>';
									echo '</td>';
									
									echo '<td>(</td>';
									echo '<td style="text-align: center">' . htmlentities( $field ) . '</td>';
									echo '<td>)</td>';
									
									echo '<td style="text-align: center"><input type="checkbox" id="select-' . htmlentities( $select_counter ) . '" onclick="updateSelect(' . htmlentities( $select_counter ) . ');" /></td>';
									echo '<td style="text-align: center"><input type="checkbox" id="group-' . htmlentities( $select_counter ) . '" value="' . htmlentities( $field ) . '" onclick="updateGroup(' . htmlentities( $select_counter ) . ');" /></td>';
									
									echo '</tr>';
									
									$select_counter++;
								}
								
								echo '</tbody>';
								echo '</table>';
								
								echo '</div>';
								echo '</div>';
								
								//
								
								echo '<div class="section">';
								echo '<div class="title">where</div>';
								echo '<div class="body">';
								
								echo '<p><select onchange="updateQuery();" id="where-join"><option value="AND">all</option><option value="OR">any</option></select> of the following conditions are TRUE.</p>';
								
								echo '<table style="width: 100%">';
								echo '<thead>';
								echo '<tr>';
								echo '<td style="text-decoration: underline; text-align: center; width: 120px">field</td>';
								echo '<td style="text-decoration: underline; text-align: center; width: 200px">comparator</td>';
								echo '<td style="text-decoration: underline; text-align: center; width: 200px">value</td>';
								echo '<td></td>';
								echo '</tr>';
								echo '</thead>';
								echo '<tbody>';
								
								$where_counter = 0;
								foreach ( $schema as $field => $type )
								{
									echo '<tr>';
									
									echo '<td id="where-field-' . htmlentities( $where_counter ) . '" style="text-align: center">' . htmlentities( $field ) . '</td>';
									
									echo '<td style="text-align: center">';
									echo '<select onchange="updateWhere(' . htmlentities( $where_counter ) . ');" id="where-op-' . htmlentities( $where_counter ) . '">';
									echo '<option value=""></option>';
									echo '<option value="=">is</option>';
									echo '<option value="LIKE">is like</option>';
									echo '<option value="<>">is not</option>';
									echo '<option value=">">greater than</option>';
									echo '<option value="<">less than</option>';
									echo '<option value=">=">greater than or equal to</option>';
									echo '<option value="<=">less than or equal to</option>';
									echo '</select>';
									echo '</td>';
									
									$dist = array();
									$hints = array();
									if ( $type != EXP_TYPE_DOUBLE )
									{
										if ( exp_field_distinct_count( $exp_id, $field ) < 20 )
										{
											$dist = exp_field_distinct( $exp_id, $field );
											foreach ( $dist as $key => $val )
											{
												$dist[ $key ] = ( '"' . htmlentities( $val ) . '"' );
												$hints[] = htmlentities( $val );
											}										
										}
									}
									
									echo '<td style="text-align: center"><input onchange="updateWhere(' . htmlentities( $where_counter ) . ');" id="where-value-' . htmlentities( $where_counter ) . '" type="text" style="width: 180px" /></td>';
									
									echo '<td style="font-size: 80%">' . ( ( empty($hints) )?(''):(' (' . misc_shorten( implode( ', ', $hints ), 50 ) . ')') ) . '</td>';
									
									if ( !empty( $dist ) )
									{
										echo '<script>';
										echo '$("input#where-value-' . htmlentities( $where_counter ) . '").autocomplete({ source: [' . implode( ',', $dist ) . '], change: function(event, ui) {updateWhere(' . htmlentities( $where_counter ) . ');} });'; 
										echo '</script>';
									}
									
									
									
									echo '</tr>';
									
									$where_counter++;
								}
								
								echo '</tbody>';
								echo '</table>';
								
								echo '</div>';
								echo '</div>';
								
								//
								
								echo '<div class="section">';
								echo '<div class="title">sorting</div>';
								echo '<div class="body">';
								
								echo '<table>';
								echo '<thead>';
								echo '<tr>';
								echo '<td style="text-decoration: underline; text-align: center; width: 120px">field</td>';
								echo '<td style="width: 120px"></td>';
								echo '</tr>';
								echo '</thead>';
								echo '<tbody>';
								
								$sort_counter=0;
								foreach ( $schema as $field => $type )
								{
									echo '<tr>';
									
									echo '<td style="text-align: center">';
									echo '<select id="order-' . htmlentities( $sort_counter ) . '" onchange="document.getElementById(\'sort-' . $sort_counter . '\').innerHTML=this.options[this.selectedIndex].value.split(\' \')[0]; updateOrder(' . htmlentities( $sort_counter ) . ');">';
									echo '<option value=""></option>';
									
									foreach ( $schema as $k => $v )
									{
										echo '<optgroup label="' . htmlentities( $k ) . '">';
										echo '<option value="' . htmlentities( $k ) . ' ASC">ascending</option>';
										echo '<option value="' . htmlentities( $k ) . ' DESC">descending</option>';
										echo '</optgroup>';
									}
									echo '</select>';
									echo '</td>';
									
									echo '<td id="sort-' . htmlentities( $sort_counter ) . '"></td>';
									
									echo '</tr>';
									
									$sort_counter++;
								}
								
								echo '</tbody>';
								echo '</table>';
								
								echo '</div>';
								echo '</div>';
								
								//
								
								echo '<div class="section">';
								echo '<div class="title">preview</div>';
								echo '<div class="body">';
								echo '<pre id="qry-preview" class="sh_sql">';
								echo '</pre>';
								echo '</div>';
								
								//
								
								echo '<form method="GET" action="" onsubmit="updateQuery(); return true;">';
								echo '<input type="hidden" name="cmd" value="view" />';
								echo ( '<input type="hidden" name="exp" value="' . htmlentities( $exp_id ) . '" />' );
								
								echo '<input type="hidden" id="qry-val" name="qry" value="" />';
								
								echo '<input id="finish" type="submit" value="query" />';
								echo jquery_button('finish');
								echo '</form>';
							}
						}
					}
				}
			}
		?>
		
		<?php
			if ( $show_forms )
			{
				$exps = exp_list(false);
				
				echo '<div id="tabs">';
				
					echo '<ul>';
						echo '<li><a href="#tabs-1">Existing</a></li>';
						
						if ( AUTH_MODIFIER )
						{
							echo '<li><a href="#tabs-2">New</a></li>';
						}
					echo '</ul>';
					
					echo '<div id="tabs-1">';
				
						if ( empty( $exps ) )
						{
							echo '<div class="section">';
								echo '<div class="body">';
									echo 'No experiments!';
								echo '</div>';
							echo '</div>';
						}
						else
						{
							foreach ( $exps as $exp_id => $exp_name )
							{
								echo '<div class="section">';
									echo '<div class="title">';
										echo ( htmlentities( $exp_id . ': ' . $exp_name ) . ( ', ' . htmlentities( exp_data_size( $exp_id ) ) ) . ' (<a href="?cmd=view&amp;exp=' . htmlentities( $exp_id ) . '">view</a>, <a href="?cmd=query&amp;exp=' . htmlentities( $exp_id ) . '">query</a>' . ( ( AUTH_MODIFIER )?(', '):('') ) . '<span  ' . ( ( AUTH_MODIFIER )?(''):('style="display:none"') ) . ' id="safety_' . htmlentities( $exp_id ) . '"><a href="#" onclick="document.getElementById(\'modify_' . htmlentities( $exp_id ) . '\').style.display=\'\';document.getElementById(\'safety_' . htmlentities( $exp_id ) . '\').style.display=\'none\'; return false;">modify</a></span><span style="display:none" id="modify_' . htmlentities( $exp_id ) . '"><a href="?cmd=clear&amp;exp=' . htmlentities( $exp_id ) . '" onclick="return confirm(\'Are you sure you wish to clear your data?\');">clear</a>, <a href="?cmd=drop&amp;exp=' . htmlentities( $exp_id ) . '" onclick="return confirm(\'Are you sure you wish to drop your experiment?\');">drop</a></span>)' );
									echo '</div>';
									echo '<div class="body">';
								
										echo '<form method="get" action="">';
											echo ( '<input type="hidden" name="cmd" value="data" />' );
											echo ( '<input type="hidden" name="exp_id" value="' . htmlentities( $exp_id ) . '" />' );
										
											echo '<table class="perty">';
												echo '<thead>';
													echo '<tr>';
														echo '<td style="width: 200px">Field</td>';
														echo '<td style="width: 80px">Type</td>';
														if ( AUTH_MODIFIER )
														{
															echo '<td style="width: 80px"></td>';
														}
													echo '</tr>';
												echo '</thead>';
											
												echo '<tbody>';
												
													$schema = exp_schema( $exp_id );
													foreach ( $schema as $field_name => $field_type )
													{
														echo '<tr>';
															echo ( '<td>' . htmlentities( $field_name ) . '</td>' );
															echo ( '<td>' . htmlentities( exp_type_2_english( $field_type ) ) . '</td>' );
															
															if ( AUTH_MODIFIER )
															{
																$field_id = ( 'i-' . $exp_id . '-' . $field_name );															
																
																echo ( '<td><input id="' . htmlentities( $field_id ) . '" type="text" name="' . htmlentities( $field_name ) . '" value="" style="width: 70px" /></td>' );
																
																/*
																if ( $field_type != EXP_TYPE_DOUBLE )
																{
																	if ( exp_field_distinct_count( $exp_id, $field_name ) < 20 )
																	{
																		$dist = exp_field_distinct( $exp_id, $field_name );
																		foreach ( $dist as $key => $val )
																		{
																			$dist[ $key ] = ( '"' . htmlentities( $val ) . '"' );
																		}
																		
																		echo '<script>';
																			echo '$("input#' . htmlentities( $field_id ) . '").autocomplete({ source: [' . implode( ',', $dist ) . '] });'; 																		
																		echo '</script>';
																	}
																}
																*/
															}
														echo '</tr>';
													}
													
													if ( AUTH_MODIFIER )
													{
														echo '<tr>';
															echo '<td>&nbsp;</td>';
															echo '<td>&nbsp;</td>';
														echo '<td style="text-align: center"><input id="send-add-' . $exp_id . '" type="submit" value="add" /></td>';
														echo jquery_button('send-add-' . $exp_id);
														echo '</tr>';
													}
													
												echo '</tbody>';
											
											echo '</table>';
										echo '</form>';
										
									echo '</div>';
								echo '</div>';
							}
						}
					echo '</div>';
					
					if ( AUTH_MODIFIER )
					{
						echo '<div id="tabs-2">';
							echo '<form method="get" action="">';
								
								echo '<div class="section">';
									echo '<div class="body">';
										echo ( 'Experiment name: &nbsp;&nbsp; <input type="text" name="exp" value="" style="width: 250px" />' );
									echo '</div>';
								echo '</div>';
								
								echo '<div class="section">';
									echo '<div class="title">';
										echo 'Schema';
									echo '</div>';
									echo '<div class="body">';
										echo '<table class="perty" id="schema">';
											echo '<thead>';
												echo '<tr>';
													echo '<td style="width: 250px">Field</td>';
													echo '<td style="width: 150px">Type</td>';
												echo '</tr>';
											echo '</thead>';
											echo '<tbody>';
												echo '<tr>';
													echo '<td><input type="text" name="field_name[0]" value="" style="width: 200px" /></td>';
													echo '<td><select name="field_type[0]" style="width: 100px">';
														echo '<option value="' . htmlentities( EXP_TYPE_INT ) . '">' . htmlentities( exp_type_2_english( EXP_TYPE_INT ) ) . '</option>';
														echo '<option value="' . htmlentities( EXP_TYPE_DOUBLE ) . '">' . htmlentities( exp_type_2_english( EXP_TYPE_DOUBLE ) ) . '</option>';
														echo '<option value="' . htmlentities( EXP_TYPE_STRING ) . '">' . htmlentities( exp_type_2_english( EXP_TYPE_STRING ) ) . '</option>';
													echo '</select></td>';
												echo '</tr>';
											echo '</tbody>';
										echo '</table>';
									echo '</div>';
								echo '</div>';
								
								echo '<div class="section">';
									echo '<div class="body">';
								
										echo '<input type="hidden" name="cmd" value="add" />';
										echo '<input id="send-add" type="button" value="add field" onclick="add_field(); return false;" /> &nbsp;&nbsp; <input id="send-save" type="submit" value="save" />';
						
										echo jquery_button('send-add');
										echo jquery_button('send-save');
										
									echo '</div>';
								echo '</div>';
								
							echo '</form>';
						echo '</div>';
					}
					
				echo '</div>';
			}
		?>
		
<?php
	
	{
		require 'common/private/end.inc.php';
	}

?>
