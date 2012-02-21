package edu.umich.soar.editor.editors.datamap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.soartech.soar.ide.core.ast.Pair;

import edu.umich.soar.editor.editors.datamap.DatamapNode.NodeType;

public class Triple {
	
	public final static String STRING_VALUE = "@string"; 
	
	// This is a String that begins with '<' and ends with '>'.
	public String variable;
	public int variableOffset = -1;

	// This is a String. If it begins with '<' and ends with '>',
	// it's a variable. Otherwise, it's a constant.
	public String attribute;
	public int attributeOffset = -1;
	
	// This is a String. If it begins with '<' and ends with '>',
	// it's a variable. Otherwise, it's a constant.
	public String value;
	public int valueOffset = -1;
	
	// Whether this triple's variable is the root node <s>.
	public boolean hasState = false;
	
	// The row to which this triple belongs.
	// public SoarDatabaseRow rule;
    
    // This is a list of triples from the same rule,
    // whose values are the same as this triple's variable.
    // This gets populated by TraveralUtil#addAttributePathInformationToTriples.
    public ArrayList<Triple> parentTriples;
    
    // Similar to parent triples but for children
    public ArrayList<Triple> childTriples;
    
    private ArrayList<ArrayList<Triple>> attributePaths = null;
    
    // Set by GenerateDatamapFromVisualSoarFileAction.run() when reading from Visual Soar datamaps
    public String comment;
    
    // Whether this was created from the RHS of a soar production.
    // Set by TripleExtractor.java
    public boolean rhs = false;
    
	public Triple(String variable, String attribute, String value) {
		this.variable = variable;
		this.attribute = attribute;
		this.value = value;
	}
	
	public Triple(Pair variable, Pair attribute, Pair value) {
		try {
		this.variable = variable.getString();
		variableOffset = variable.getOffset();
		this.attribute = attribute.getString();
		attributeOffset = attribute.getOffset();
		this.value = value.getString();
		valueOffset = value.getOffset();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return True if the attribute is a variable
	 * (it begins with '<' and ends with '>'), false
	 * if it's a constant.
	 */
	public boolean attributeIsVariable() {
		int lastIndex = attribute.length() - 1;
		return attribute.indexOf('<') == 0 && attribute.lastIndexOf('>') == lastIndex;
	}

	public boolean attributeIsConstant() {
		return !attributeIsVariable();
	}

	/**
	 * @return True if the value is a variable
	 * (it's a String that begins with '<' and ends with '>'), false
	 * otherwise.
	 */
	public boolean valueIsVariable() {
		if (value == null) return false;
		int lastIndex = value.length() - 1;
		return value.indexOf('<') == 0 && value.lastIndexOf('>') == lastIndex;
	}
	
	public boolean valueIsConstant() {
		return !valueIsVariable();
	}

	public boolean valueIsString() {
		return !valueIsVariable() && !valueIsInteger() && !valueIsFloat();
	}

	@Override
	public String toString() {
		return "(" + variable + " ^" + attribute + " " + value.toString() + ")";
	}
	
	public boolean valueIsInteger() {
		Pattern pattern = Pattern.compile("^-?\\d*$");
		Matcher matcher = pattern.matcher(value);
		return matcher.matches() && !value.equals("-");
	}
	
	public boolean valueIsFloat() {
		if (valueIsInteger()) {
			return false;
		}
		Pattern pattern = Pattern.compile("^-?\\d*\\.\\d*$");
		Matcher matcher = pattern.matcher(value);
		return matcher.matches() && !value.equals("-");
	}
	
	public String getTypeString()
	{
		if (valueIsVariable())
		{
			return "variable";
		}
		if (valueIsInteger())
		{
			return "integer";
		}
		if (valueIsFloat())
		{
			return "float";
		}
		return "string";
	}

    public NodeType getNodeType()
    {
        if (valueIsVariable())
        {
            return NodeType.SOAR_ID;
        }
        if (valueIsInteger())
        {
            return NodeType.INT_RANGE;
        }
        if (valueIsFloat())
        {
            return NodeType.FLOAT_RANGE;
        }
        return NodeType.STRING; 
    }
	
	/**
	 * 
	 * @return The list of the list of attribute Strings to get from state &lt;s&gt; to this Triple's variable.
	 */
	public ArrayList<ArrayList<Triple>> getTriplePathsFromState() {
		ArrayList<ArrayList<Triple>> ret = new ArrayList<ArrayList<Triple>>();
		
		LinkedList<ArrayList<Triple>> tripleStack = new LinkedList<ArrayList<Triple>>();
		LinkedList<Integer> indexStack = new LinkedList<Integer>();
		LinkedList<HashSet<Triple>> visitedStack = new LinkedList<HashSet<Triple>>();
		
		ArrayList<Triple> thisList = new ArrayList<Triple>();
		thisList.add(this);
		tripleStack.push(thisList);
		
		indexStack.push(new Integer(0));
		
		HashSet<Triple> thisHash = new HashSet<Triple>();
		thisHash.add(this);
		visitedStack.push(thisHash);
		
		while(tripleStack.size() > 0) {
			
			Triple leaf = tripleStack.peek().get(indexStack.peek());
			
			if (leaf.hasState) {
				// Found a solution -- don't need to look at parents.
				ArrayList<Triple> retPath = new ArrayList<Triple>();
				// Use iterators for fast performance on linked lists.
				Iterator<ArrayList<Triple>> tripleIt = tripleStack.iterator();
				Iterator<Integer> indexIt = indexStack.iterator();
				while (tripleIt.hasNext()) {
					ArrayList<Triple> nextTriples = tripleIt.next();
					Integer nextIndex = indexIt.next();
					Triple nextTriple = nextTriples.get(nextIndex);
					retPath.add(nextTriple);
				}
				ret.add(retPath);
			} else {
				// Not a solution -- keep searching.
				ArrayList<Triple> parents = leaf.parentTriples;
				ArrayList<Triple> newParents = new ArrayList<Triple>();
				HashSet<Triple> newVisited = new HashSet<Triple>(visitedStack.peek());
				for (Triple parent : parents) {
					if (!(visitedStack.peek().contains(parent))) {
						newParents.add(parent);
						newVisited.add(parent);
					}
				}
				if (newParents.size() > 0) {
					// Push onto all stacks.
					tripleStack.push(newParents);
					visitedStack.push(newVisited);
					indexStack.push(new Integer(-1));
				}
			}
			
			// Increment index
			Integer currentIndex = indexStack.pop();
			indexStack.push(new Integer(currentIndex + 1));
			
			// Pop all stacks if neccesarry.
			while (indexStack.size() > 0 && indexStack.peek() >= tripleStack.peek().size()) {
				tripleStack.pop();
				indexStack.pop();
				visitedStack.pop();
				
				if (indexStack.size() > 0) {
					// Increment index
					currentIndex = indexStack.pop();
					indexStack.push(new Integer(currentIndex + 1));
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @return The list of the list of attribute Strings to get from state &lt;s&gt; to this Triple's variable.
	 */
	public ArrayList<ArrayList<Triple>> oldGetTriplePathsFromState() {
	
		if (attributePaths != null) return attributePaths;
		
		//if (parentTriples == null) return null;
		ArrayList<ArrayList<Triple>> ret = new ArrayList<ArrayList<Triple>>();
		class TriplePath {
			public ArrayList<Triple> path;
			public HashSet<Triple> visited;
			public TriplePath(Triple triple) {
				path = new ArrayList<Triple>();
				path.add(triple);
				visited = new HashSet<Triple>();
				visited.add(triple);
			}			
			private TriplePath(Triple triple, ArrayList<Triple> path, HashSet<Triple> visited) {
				this.path = path;
				this.visited = visited;
			}
			public TriplePath next(Triple triple) {
				TriplePath ret = new TriplePath(triple, new ArrayList<Triple>(path), new HashSet<Triple>(visited));
				ret.path.add(triple);
				ret.visited.add(triple);
				return ret;
			}
			public Triple last() {
				return path.get(path.size() - 1);
			}
		}
		
		ArrayList<TriplePath> leaves = new ArrayList<TriplePath>();
		leaves.add(new TriplePath(this));
		
		while (leaves.size() > 0) {
			System.out.println("leaves.size(): " + leaves.size());
			ArrayList<TriplePath> newLeaves = new ArrayList<TriplePath>();
			for (TriplePath path : leaves) {
				if (path.last().hasState) {
					ret.add(path.path);
					System.out.println("Added to ret: " + path.path);
				}
				else {
					ArrayList<Triple> parentTriples = path.last().parentTriples;
					System.out.println("  " + path.path);
					System.out.println("  parentTriples.size(): " + parentTriples.size());
					System.out.println("  path.visited.size(): " + path.visited.size());
					int numNewLeaves = 0;
					for (Triple triple : parentTriples) {
						if (!path.visited.contains(triple)) {
							newLeaves.add(path.next(triple));
							++numNewLeaves;
						}
					}
					System.out.println("  numNewLeaves: " + numNewLeaves);
				}
			}
			leaves = newLeaves;
		}
		
		for (ArrayList<Triple> ar : ret) {
			Collections.reverse(ar);
		}
		
		// Cache result
		attributePaths = ret;
		
		return ret;
	}
	
	public ArrayList<ArrayList<String>> getAttributePathsFromState() {
		ArrayList<ArrayList<Triple>> triplePaths = getTriplePathsFromState();
		
		ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
		for (ArrayList<Triple> triplePath : triplePaths) {
			ArrayList<String> stringPath = new ArrayList<String>(); 
			for (Triple triple : triplePath) {
				stringPath.add(triple.attribute);
			}
			ret.add(stringPath);
		}
		return ret;
	}
	
	class AttributePathNode {
		String attribute;
		ArrayList<AttributePathNode> childNodes;
		public AttributePathNode(String attribute, ArrayList<AttributePathNode> childNodes) {
			this.attribute = attribute;
			this.childNodes = childNodes;
		}
	}
	
	public boolean matchesPath(String[] matchPath) {
		ArrayList<ArrayList<String>> paths = getAttributePathsFromState();
		if (paths == null) return false;
		for (ArrayList<String> path : paths) {
			if (path.size() == matchPath.length) {
				boolean match = true;
				for (int i = 0; i < path.size() && match; ++i) {
					if (!path.get(i).equals(matchPath[i])) {
						match = false;
					}
				}
				if (match) {
					return match;
				}
			}
		}
		return false;
	}
	
	public boolean isStateName() {
		return matchesPath(new String[] {"name"});
	}
	
	public boolean isOperatorName() {
		return matchesPath(new String[] {"operator", "name"});
	}
	
	public boolean isSuperstateName() {
		return matchesPath(new String[] {"superstate", "name"});
	}
	
	public boolean isSuperstateOperatorName() {
		return matchesPath(new String[] {"superstate", "operator", "name"});
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Triple)) return false;
		Triple other = (Triple) obj;
		return this.variable.equals(other.variable)
				&& this.attribute.equals(other.attribute)
				&& this.value.equals(other.value);
	}
}
