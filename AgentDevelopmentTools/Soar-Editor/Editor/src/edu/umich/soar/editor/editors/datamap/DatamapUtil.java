package edu.umich.soar.editor.editors.datamap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.umich.soar.editor.editors.datamap.DatamapNode.NodeType;

/**
 * getInconsistancies and getCorrections are the two public methods.
 * @author miller
 */
public class DatamapUtil {
	
	/*
	@SuppressWarnings("unchecked")
	public static ArrayList<DatamapInconsistency> getInconsistancies(SoarDatabaseRow rule) {
		ArrayList<Triple> triples = TraversalUtil.getTriplesForRule(rule);
		HashMap<String, ArrayList<Triple>> triplesByVariable = TraversalUtil.triplesWithVariable(triples);
		ArrayList<Triple> stateTriples = new ArrayList<Triple>();
		for (Triple triple : triples) {
			if (triple.hasState) {
				stateTriples.add(triple);
			}
		}
		ArrayList<DatamapInconsistency> ret = new ArrayList<DatamapInconsistency>();
		ArrayList<SoarDatabaseRow> problemSpaces = rule.getDirectedJoinedParentsOfType(Table.PROBLEM_SPACES);
		ArrayList<SoarDatabaseRow> operators = rule.getDirectedJoinedParentsOfType(Table.OPERATORS);
		for (SoarDatabaseRow operator : operators) {
			problemSpaces.addAll(operator.getDirectedJoinedParentsOfType(Table.PROBLEM_SPACES));
		}
		HashSet<Triple> visitedTriples = new HashSet<Triple>();
		for (SoarDatabaseRow problemSpace : problemSpaces) {
			ArrayList<SoarDatabaseRow> roots = new ArrayList<SoarDatabaseRow>();
			roots.addAll((Collection<? extends SoarDatabaseRow>) problemSpace.getChildrenOfType(Table.DATAMAP_IDENTIFIERS).get(0).getDirectedJoinedChildren(false));
			for (Triple stateTriple : stateTriples) {
				getInconsistencies(rule, problemSpace, stateTriple, roots, triplesByVariable, visitedTriples, ret);
			}
		}
		return ret;
	}
	*/
	
	/**
	 * Recursively build a list of inconsistencies between a set of triples and a datamap.
	 * @param rule The current rule.
	 * @param problemSpace The current problem space.
	 * @param currentTriple The current triple (begin with triples that have state).
	 * @param currentNodes  The current datamap nodes (begin with children of root state).
	 * @param triplesByVariable Maps variable names onto triples.
	 * @param visitedNodes Triples that have been visited so far.
	 * @param inconsistencies Output variable.
	 */
	/*
	@SuppressWarnings("unchecked")
	private static void getInconsistencies(SoarDatabaseRow rule,
			SoarDatabaseRow problemSpace,
			Triple currentTriple, ArrayList<SoarDatabaseRow> currentNodes,
			HashMap<String, ArrayList<Triple>> triplesByVariable,
			HashSet<Triple> visitedTriples,
			ArrayList<DatamapInconsistency> inconsistencies) {
		
		// Avoid infinite recursion
		if (visitedTriples.contains(currentTriple)) return;
		visitedTriples.add(currentTriple);
		
		// Find datamap nodes whose attribute and value type match the attribute and value type of the current triple.
		// Also, find string and enumeration nodes for triples whose values are constant.
		// Also, find enumeration nodes with a correct value for triples whose values are cosntant.
		// If no nodes are found, add an error to the list.
		ArrayList<SoarDatabaseRow> matchingNodes = new ArrayList<SoarDatabaseRow>();
		
		HashSet<Table> matchingTypes = new HashSet<Table>();
		
		if (currentTriple.valueIsVariable()) matchingTypes.add(Table.DATAMAP_IDENTIFIERS);
		else if (currentTriple.valueIsInteger()) matchingTypes.add(Table.DATAMAP_INTEGERS);
		else if (currentTriple.valueIsFloat()) matchingTypes.add(Table.DATAMAP_FLOATS);
		if (currentTriple.valueIsConstant()) {
			matchingTypes.add(Table.DATAMAP_STRINGS);
			matchingTypes.add(Table.DATAMAP_ENUMERATIONS);
		}
		
		for (SoarDatabaseRow node : currentNodes) {
			if (node.getName().equals(currentTriple.attribute) &&
					matchingTypes.contains(node.getTable())) {
				matchingNodes.add(node);
			}
		}
		
		if (matchingNodes.size() == 0) {
			inconsistencies.add(new DatamapInconsistency(rule, problemSpace, "Invalid attribute: " + currentTriple.attribute, currentTriple.attributeOffset));
			return;
		}
		
		// Make sure the value of the triple is a valid value in at least one of the matching datamap nodes.
		// Otherwise, add an error.
		if (currentTriple.valueIsConstant()) {
			boolean match = false;
			for (SoarDatabaseRow node : matchingNodes) {
				if (node.getTable() == Table.DATAMAP_STRINGS) {
					match = true;
					break;
				}
				else if (node.getTable() == Table.DATAMAP_ENUMERATIONS) {
					for (SoarDatabaseRow enumValue : node.getChildrenOfType(Table.DATAMAP_ENUMERATION_VALUES)) {
						if (enumValue.getName().equals(currentTriple.value)) {
							match = true;
							break;
						}
					}
					if (match) break;
				}
				else if (node.getTable() == Table.DATAMAP_INTEGERS) {
					int tripleValue = Integer.parseInt(currentTriple.value);
					int min = (Integer) node.getColumnValue("min_value");
					int max = (Integer) node.getColumnValue("max_value");
					if (tripleValue >= min && tripleValue <= max) {
						match = true;
						break;
					}
				}
				else if (node.getTable() == Table.DATAMAP_FLOATS) {
					float tripleValue = Float.parseFloat(currentTriple.value);
					float min = (Float) node.getColumnValue("min_value");
					float max = (Float) node.getColumnValue("max_value");
					if (tripleValue >= min && tripleValue <= max) {
						match = true;
						break;
					}
				}
			}
			if (!match) {
				inconsistencies.add(new DatamapInconsistency(rule, problemSpace, "Invalid value: " + currentTriple.value, currentTriple.valueOffset));
				return;
			}
		}
		
		// If the value of the current triple is an identifier, recursively check chidlren of this triple.
		if (currentTriple.valueIsVariable()) {
			ArrayList<SoarDatabaseRow> childNodes = new ArrayList<SoarDatabaseRow>();
			for (SoarDatabaseRow matchingNode : matchingNodes) {
				childNodes.addAll((Collection<? extends SoarDatabaseRow>) matchingNode.getDirectedJoinedChildren(false));
			}
			ArrayList<Triple> triplesWithVariable = triplesByVariable.get(currentTriple.value);
			if (triplesWithVariable != null) {
				for (Triple child : triplesWithVariable) {
					getInconsistencies(rule, problemSpace, child, childNodes, triplesByVariable, visitedTriples, inconsistencies);
				}
			}
		}
	}
	*/
	
	/**
	 * @param triples Triples from the rule.
	 * @param datamap The datamap for the problem space.
	 * @param stateVariables Names of variables that have state. Usually just "<s>".
	 */
	public static ArrayList<Correction> getCorrections(List<Triple> triples, Datamap datamap, List<String> stateVariables, String stateName) {
		// Paths from the rule
		ArrayList<TerminalPath> paths = terminalPathsForTriples(triples);
		
		// Empty now, add more as the algorithm runs.
		ArrayList<Correction> corrections = new ArrayList<Correction>();
		
		// Iterate over all paths to build corrections
		//long buildCorrectionsStart = new Date().getTime();
		for (TerminalPath terminalPath : paths) {
			ArrayList<Triple> path = terminalPath.path;
			Collection<DatamapNode> currentNodes = new HashSet<DatamapNode>();
			
			// Add initial state triples.
			//currentNodes.add(root);
			/*
			DatamapNode stateNode = datamap.getStateNode(stateName);
			if (stateNode != null)
			{
				currentNodes.add(stateNode);
			}
			*/
			currentNodes.addAll(datamap.getStateNodes());
			
			// Walk down the path, keeping track of which datamap nodes
			// correspond to the current location on the path.
			
			for (int i = 0; i < path.size(); ++i) {
			    // The current triple for this path.
                Triple pathTriple = path.get(i);
                
                if (pathTriple.attributeIsVariable())
                {
                    break;
                }
                
				boolean anyChildren = false;
				
				// Children of the current datamap node that match the current triple.
				Collection<DatamapNode> childNodes = new HashSet<DatamapNode>();
				
				for (DatamapNode node : currentNodes) {
					if (pathTriple.valueIsVariable()) {
						List<DatamapNode> variableChildren = node.getChildren(pathTriple.attribute);
						childNodes.addAll(variableChildren);
					} else if (pathTriple.valueIsInteger()) {
						List<DatamapNode> integerChildren = node.getChildren(pathTriple.attribute, NodeType.INT_RANGE);
						childNodes.addAll(integerChildren);
					} else if (pathTriple.valueIsFloat()) {
						List<DatamapNode> floatChildren = node.getChildren(pathTriple.attribute, NodeType.FLOAT_RANGE);
						childNodes.addAll(floatChildren);
					} else if (pathTriple.valueIsString()) {
						List<DatamapNode> enumChildren = node.getChildren(pathTriple.attribute, NodeType.ENUMERATION);
						for (DatamapNode childNode : enumChildren)
						{
							anyChildren = true;
							if (childNode.values.contains(pathTriple.value))
							{
								childNodes.add(childNode);
							}
						}
					}
					
					List<DatamapNode> stringChildren = node.getChildren(pathTriple.attribute, NodeType.STRING);
					childNodes.addAll(stringChildren);
				}
				
				// if currentNodes.size == 0 and there's more triples,
				// propose adding triples and continue to next path
				if (childNodes.size() == 0) {
					
					// TODO debug
					// System.out.println("No more child nodes.");
					
					for (DatamapNode leafNode : currentNodes) {
						if (leafNode.type == NodeType.STRING)
						{
							continue;
						}
						// TODO debug
						// System.out.println("Leaf: " + leafNode);
						
						ArrayList<Triple> addition = new ArrayList<Triple>();
						for (int j = i; j < path.size(); ++j) {
							addition.add(path.get(j));
						}
						if (addition.size() > 0) {
							List<DatamapNode> stringNodes = leafNode.getChildren(pathTriple.attribute, NodeType.STRING);
							List<Triple> pathSoFar = path.subList(0, i);
							Correction correction = new Correction(leafNode, addition, terminalPath.links, pathTriple, anyChildren, pathSoFar);
							corrections.add(correction);
						}
					}
					break;
				}
				currentNodes = childNodes;
			}
		}
		return corrections;
	}

	public static ArrayList<TerminalPath> terminalPathsForTriples(List<Triple> triples) {
		ArrayList<TerminalPath> ret = new ArrayList<TerminalPath>();
		HashSet<String> usedVariables = new HashSet<String>();
		
		boolean grew = true;
		while (grew) {
			grew = false;
			ArrayList<Triple> nextTriples = new ArrayList<Triple>();
			
			// Iterate over all triples.
			for (Triple triple : triples) {
				
				// Keep track of whether any path from this triple was added to 'ret'.
				boolean added = false;
				
				// If the paths from this triple are naturally terminal --
				// if the triple has no child triples.
				boolean terminal = !triple.valueIsVariable() || triple.childTriples == null;
				
				// Iterate over each path from this triple.
				ArrayList<ArrayList<Triple>> triplePathsFromState = triple.getTriplePathsFromState();
				for (ArrayList<Triple> path : triplePathsFromState) {
					
					// Figure out if the path loops onto itself
					// e.g. (<s> ^attr <v1>),(<v1> ^attr <v2>),(<v2> ^attr <v3>),(<v3> ^attr <v1>)
					// In the example, this triple is the last one and its value loops back to the
					// previous reference to the variable <v1>.
					HashSet<String> pathVariables = new HashSet<String>();
					addVariablesToHashSet(path, pathVariables);
					boolean loops = triple.valueIsVariable() && pathVariables.contains(triple.value);
					
					// Also figure out if this path loops onto any path in 'ret'.
					// 'Loops' in this context is used lightly -- there may not be any
					// cyclic paths.
					ArrayList<Triple> links = new ArrayList<Triple>();
					boolean loopsIntoPath = false;
					for (TerminalPath retPath : ret) {
						Triple t = pathLoopsIntoPath(path, retPath.path);
						if (t != null) {
							links.add(t);
							loopsIntoPath = true;
							// break;
						}
					}
					
					if (terminal || loops || loopsIntoPath) {
					    // This path is a candidate for inclusion in the terminal paths,
					    // as long as it's not too long (i.e. it collides with an existing path)
					    // and it's not identical to an existing path.

						// Make sure the path isn't too long
						boolean tooLong = false;
						for (TerminalPath retPath : ret) {
							if (pathCollidesWithPath(path, retPath.path)) {
								tooLong = true;
								break;
							}
						}
						
						// Make sure the path isn't identical to a path that's already been proposed,
						// and that it's not redundant,
						// and doesn't make a previously proposed path redundant.
						boolean identical = false; // Identical (or precluded by redundancy to) existing path
						ArrayList<TerminalPath> pathsToRemove = new ArrayList<TerminalPath>(); // Paths to remove in case they're redundant
						for (TerminalPath retPath : ret) {
							if (path.equals(retPath.path)) {
								identical = true;
								// This path won't be included because it's equivalent to retPath.
								// Make the productionSide of retPath include the productionSide of this path's last triple.
								Triple pathEnd = lastTriple(path);
								if (pathEnd.rhs)
								{
								    retPath.setActionSide();
								}
								else
								{
								    retPath.setConditionSide();
								}
								break;
							} else if (pathsAreRedundant(path, retPath.path)) {
								if (path.size() > retPath.path.size()) {
									// The existing path is too short. Remove it and replace it with this one.
									pathsToRemove.add(retPath);
									break;
								} else {
									identical = true;
									break;
								}
							}
						}
						ret.removeAll(pathsToRemove);
						
						if (!tooLong && !identical) {
							grew = true;
							added = true;
							TerminalPath newPath = new TerminalPath(path, links);
							ret.add(newPath);
							addVariablesToHashSet(path, usedVariables);
							
							// TODO debug
							/*
							System.out.println("Added path: " + path);
							System.out.println("terminal: " + terminal);
							System.out.println("loops: " + loops);
							System.out.println("loopsIntoPath: " + loopsIntoPath + '\n');
							*/
						}
					}
				}
				if (!added) nextTriples.add(triple);
			}
			triples = nextTriples;
		}
		
		return ret;
	}
	
	private static Triple lastTriple(List<Triple> triples)
	{
	    return triples.get(triples.size() - 1);
	}
	
	private static boolean pathsAreRedundant(ArrayList<Triple> path, ArrayList<Triple> retPath) {
		int len = Math.min(path.size(), retPath.size());
		for (int i = 0; i < len; ++i) {
			if (!path.get(i).equals(retPath.get(i))) {
				return false;
			}
		}
		return true;
	}

	private static void addVariablesToHashSet(ArrayList<Triple> triples, HashSet<String> variables) {
		for (Triple triple : triples) {
			variables.add(triple.variable);
		}
	}
	
	/**
	 * 
	 * @param path
	 * @param retPath
	 * @return True if the two paths diverge and than converge again.
	 */
	private static boolean pathCollidesWithPath(ArrayList<Triple> path, ArrayList<Triple> retPath) {
		int index = 0;
		for ( ; index < path.size() - 1; ++index) {
			if (index >= retPath.size()) {
				return false;
			}
			if (!path.get(index).equals(retPath.get(index))) {
				break;
			}
		}
		if (index == path.size() - 1) {
			return false;
		}
		
		HashSet<String> retPathVariables = new HashSet<String>();
		for (Triple t : retPath) {
			retPathVariables.add(t.variable);
		}
		
		for ( ; index < path.size() - 1; ++index) {
			/*
			if (index >= retPath.size()) {
				return false;
			}
			*/
			if (retPathVariables.contains(path.get(index).value)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param path
	 * @param retPath
	 * @return If the last triple in path points to a variable that is also pointed to
	 *         by any triple in retPath, returns that triple from retPath. Returns
	 *         null otherwise.
	 */
	private static Triple pathLoopsIntoPath(ArrayList<Triple> path, ArrayList<Triple> retPath) {
		Triple last = lastTriple(path);
		if (!last.valueIsVariable()) return null;
		for (int i = 0; i < retPath.size(); ++i) {
			Triple retTriple = retPath.get(i);			
			if (last.value.equals(retTriple.value)) {
				if (!retTriple.equals(last)) {
					return retTriple;
				}
			}
			
		}
		return null;
	}
}
