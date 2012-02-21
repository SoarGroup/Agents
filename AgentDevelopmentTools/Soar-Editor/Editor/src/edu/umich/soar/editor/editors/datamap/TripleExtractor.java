package edu.umich.soar.editor.editors.datamap;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.soartech.soar.ide.core.ast.Action;
import com.soartech.soar.ide.core.ast.AttributeTest;
import com.soartech.soar.ide.core.ast.AttributeValueMake;
import com.soartech.soar.ide.core.ast.AttributeValueTest;
import com.soartech.soar.ide.core.ast.Condition;
import com.soartech.soar.ide.core.ast.ConditionForOneIdentifier;
import com.soartech.soar.ide.core.ast.ConjunctiveTest;
import com.soartech.soar.ide.core.ast.Constant;
import com.soartech.soar.ide.core.ast.DisjunctionTest;
import com.soartech.soar.ide.core.ast.FunctionCall;
import com.soartech.soar.ide.core.ast.Pair;
import com.soartech.soar.ide.core.ast.PositiveCondition;
import com.soartech.soar.ide.core.ast.PreferenceSpecifier;
import com.soartech.soar.ide.core.ast.RHSValue;
import com.soartech.soar.ide.core.ast.SimpleTest;
import com.soartech.soar.ide.core.ast.SingleTest;
import com.soartech.soar.ide.core.ast.SoarProductionAst;
import com.soartech.soar.ide.core.ast.Test;
import com.soartech.soar.ide.core.ast.ValueMake;
import com.soartech.soar.ide.core.ast.ValueTest;
import com.soartech.soar.ide.core.ast.VarAttrValMake;

public class TripleExtractor {

	static long visitingRuleNodes;
	static long applyingState;
	static long addingAttributePathInfo;

	public static void resetLoggingTimes() {
		visitingRuleNodes = 0;
		applyingState = 0;
		addingAttributePathInfo = 0;
	}

	public static void printLoggingTimes() {
		System.out.println("Time Spent visiting rules nodes: " + visitingRuleNodes);
		System.out.println("Time Spent applying state: " + applyingState);
		System.out.println("Time Spent adding attribute path info: " + addingAttributePathInfo);
	}

	public static HashMap<String, ArrayList<Triple>> triplesWithVariable(ArrayList<Triple> triples) {
		HashMap<String, ArrayList<Triple>> triplesWithVariable = new HashMap<String, ArrayList<Triple>>();
		for (Triple triple : triples) {
			ArrayList<Triple> variableList = triplesWithVariable.get(triple.variable);
			if (variableList == null) {
				variableList = new ArrayList<Triple>();
				triplesWithVariable.put(triple.variable, variableList);
			}
			variableList.add(triple);
		}
		return triplesWithVariable;
	}

	public static HashMap<String, ArrayList<Triple>> triplesWithValue(ArrayList<Triple> triples) {
		HashMap<String, ArrayList<Triple>> triplesWithValue = new HashMap<String, ArrayList<Triple>>();
		for (Triple triple : triples) {
			if (triple.valueIsVariable()) {
				ArrayList<Triple> valueList = triplesWithValue.get(triple.value);
				if (valueList == null) {
					valueList = new ArrayList<Triple>();
					triplesWithValue.put(triple.value, valueList);
				}
				valueList.add(triple);
			}
		}
		return triplesWithValue;
	}
	
	public static List<Triple> makeTriples(SoarProductionAst ast, ArrayList<String> stateVariables) {
	      return makeTriples(ast, stateVariables, true);
	}
	
	public static List<Triple> makeTriples(SoarProductionAst ast, ArrayList<String> stateVariables, boolean includeRhs)
	{
		ArrayList<Triple> triples = new ArrayList<Triple>();
		visitingRuleNodes -= new Date().getTime();
		
		visitSoarProductionAst(ast, triples, stateVariables, includeRhs);
		
		visitingRuleNodes += new Date().getTime();
		applyingState -= new Date().getTime();
		applyStateToTriples(triples, stateVariables);
		applyingState += new Date().getTime();
		addingAttributePathInfo -= new Date().getTime();
		addAttributePathInformationToTriples(triples);
		addingAttributePathInfo += new Date().getTime();
		
		return triples;
	}
	
	/**
	 * Records attribute path information for each triple from root node state
	 * <s>
	 * 
	 * @param triples
	 */
	private static void addAttributePathInformationToTriples(ArrayList<Triple> triples) {
		HashMap<String, ArrayList<Triple>> triplesWithVariable = triplesWithVariable(triples);
		HashMap<String, ArrayList<Triple>> triplesWithValue = triplesWithValue(triples);

		for (Triple triple : triples) {
			ArrayList<Triple> parentTriples = triplesWithValue.get(triple.variable);
			if (parentTriples != null) {
				triple.parentTriples = parentTriples;
			} else {
				triple.parentTriples = new ArrayList<Triple>();
			}
			if (triple.valueIsVariable()) {
				ArrayList<Triple> childTriples = triplesWithVariable.get(triple.value);
				if (childTriples != null) {
					triple.childTriples = childTriples;
				}
			}
		}
	}

	static boolean debug = true;

	private static void debug(String str) {
		return;
		/*
		if (!debug)
			return;
		System.out.println(str);
		*/
	}

	private static void applyStateToTriples(ArrayList<Triple> triples, ArrayList<String> stateVariables) {
		for (Triple triple : triples) {
			for (String stateVariable : stateVariables) {
				if (triple.variable.equals(stateVariable)) {
					triple.hasState = true;
				}
			}
		}
	}

	private static void visitSoarProductionAst(SoarProductionAst ast, ArrayList<Triple> triples, ArrayList<String> stateVariables, boolean includeRhs) {
		debug("visitSoarProductionAst: " + ast);
		try {
			for (Condition condition : ast.getConditions()) {
				visitCondition(condition, triples, stateVariables);
			}
		    if (includeRhs)
		    {
		        int numTriples = triples.size();
		        for (Action action : ast.getActions())
		        {
		            visitAction(action, triples);
		        }
		        for (int i = numTriples; i < triples.size(); ++i)
		        {
		            triples.get(i).rhs = true;
		        }
		    }
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private static void visitCondition(Condition condition, ArrayList<Triple> triples, ArrayList<String> stateVariables) {
		debug("visitCondition: " + condition);
		PositiveCondition posCon = condition.getPositiveCondition();
		if (posCon.isConjunction()) {
			for (Condition c : posCon.getConjunction()) {
				visitCondition(c, triples, stateVariables);
			}
		} else {
			ConditionForOneIdentifier cfoi = posCon.getConditionForOneIdentifier();
			String variable = "" + cfoi.getVariable();
			if (cfoi.hasState()) {
				stateVariables.add(variable);
			}
			Pair variablePair = cfoi.getVariable();
			visitConditionForOneIdentifier(cfoi, triples, variablePair);
		}
	}
	
	private static void visitAction(Action action, ArrayList<Triple> triples) {
		debug("visitAction: " + action);
		if (action.isVarAttrValMake()) {
			visitVarAttrValMake(action.getVarAttrValMake(), triples);
		} else {
			visitFunctionCall(action.getFunctionCall());
		}
	}
	
	private static void visitFunctionCall(FunctionCall fc) {
		// Do nothing for now.
	}

	private static void visitConditionForOneIdentifier(ConditionForOneIdentifier cfoi, ArrayList<Triple> triples, Pair variable) {
		debug("visitConditionForOneIdentifier: " + cfoi);
		for (AttributeValueTest avt : cfoi.getAttributeValueTests()) {
			ArrayList<ArrayList<Pair>> attributes = new ArrayList<ArrayList<Pair>>();
			ArrayList<Pair> values = new ArrayList<Pair>();
			visitAttributeValueTest(avt, attributes, values, triples, variable);
			ArrayList<Pair> variables = new ArrayList<Pair>();
			variables.add(variable);
			ArrayList<Triple> newTriples = triplesForVariablesNamesAttributes(variables, attributes, values);
			triples.addAll(newTriples);
		}
	}

	private static ArrayList<Triple> triplesForVariablesNamesAttributesShortList(ArrayList<Pair> variables, ArrayList<Pair> attributes, ArrayList<Pair> values) {
		// Turn the attributes into an ArrayList<ArrayList<String>>
		ArrayList<ArrayList<Pair>> attributesList = new ArrayList<ArrayList<Pair>>();
		for (Pair attribute : attributes) {
			ArrayList<Pair> singleAttribute = new ArrayList<Pair>();
			singleAttribute.add(attribute);
			attributesList.add(singleAttribute);
		}
		debug("Values: " + values + " (triplesForVariablesNamesAttributesShortList)");
		if (values == null) {
			debug("NULL");
		}
		ArrayList<Triple> ret = triplesForVariablesNamesAttributes(variables, attributesList, values);
		return ret;
	}

	private static ArrayList<Triple> triplesForVariablesNamesAttributes(ArrayList<Pair> variables, ArrayList<ArrayList<Pair>> attributes, ArrayList<Pair> values) {
		debug("triplesForVariablesNamesAttributes, values: " + values);
		ArrayList<Triple> ret = new ArrayList<Triple>();
		ArrayList<Pair> newVariables = new ArrayList<Pair>();
		if (attributes.size() > 0) {
			ArrayList<Pair> currentAttributes = attributes.get(0);
			if (attributes.size() == 1) {
				// This is the last set of variables.
				// Create triples with the current variables, attributes,
				// values.

				for (Pair variable : variables) {
					for (Pair attribute : currentAttributes) {
						for (Pair value : values) {
							Triple triple = new Triple(variable, attribute, value);
							ret.add(triple);
						}
					}
				}

			} else {
				// We're in dot notation.
				// Create a new set of variables using the current attributes.
				// Create triples using the current variables, current
				// attributes, and variables created from the attributes.
				// Then add all with a recursive call to this function.

				for (Pair variable : variables) {
					for (Pair attribute : currentAttributes) {
						String variableName = variable.getString().substring(1, variable.getString().length() - 1);
						if (!variableName.startsWith("_")) {
							variableName = "_" + variableName;
						}
						Pair newVariable = new Pair("<" + variableName + "_" + attribute + ">");
						Triple triple = new Triple(variable, attribute, newVariable);
						ret.add(triple);
						newVariables.add(newVariable);
					}
				}

				ArrayList<ArrayList<Pair>> newAttributes = new ArrayList<ArrayList<Pair>>();
				for (int i = 1; i < attributes.size(); ++i) {
					newAttributes.add(attributes.get(i));
				}
				debug("Values: " + values + " (triplesForVariablesNamesAttributes)");
				ArrayList<Triple> recurse = triplesForVariablesNamesAttributes(newVariables, newAttributes, values);
				ret.addAll(recurse);
			}
		}
		return ret;
	}

	private static void visitAttributeValueTest(AttributeValueTest avt, ArrayList<ArrayList<Pair>> attributes, ArrayList<Pair> values, ArrayList<Triple> triples, Pair variable) {
		debug("visitAttributeValueTest: " + avt);
		for (AttributeTest at : avt.getAttributeTests()) {
			ArrayList<Pair> names = new ArrayList<Pair>();
			visitAttributeTest(at, names);
			attributes.add(names);	
		}
		for (ValueTest vt : avt.getValueTests()) {
			visitValueTest(vt, attributes, values, triples, variable);
		}
	}

	private static void visitAttributeTest(AttributeTest at, ArrayList<Pair> attributes) {
		debug("visitAttributeTest: " + at);
		visitTest(at.getTest(), attributes);
	}

	private static void visitDisjunctionTest(DisjunctionTest dt, ArrayList<Pair> names) {
		debug("visitDisjunctionTest: " + dt);
		for (Constant c : dt.getConstants()) {
			visitConstant(c, names);
		}
	}

	private static void visitConjunctiveTest(ConjunctiveTest ct, ArrayList<Pair> names) {
		debug("visitConjunctiveTest: " + ct);
		for (SimpleTest st : ct.getSimpleTests()) {
			visitSimpleTest(st, names);
		}
	}
	
	private static void visitTest(Test t, ArrayList<Pair> names) {
		debug("visitTest: " + t);
		if (t.isConjunctiveTest()) {
			visitConjunctiveTest(t.getConjunctiveTest(), names);
		} else {
			visitSimpleTest(t.getSimpleTest(), names);
		}
	}
	
	private static void visitSimpleTest(SimpleTest st, ArrayList<Pair> names) {
		debug("visitSimpleTest: " + st);
		if (st.isDisjunctionTest()) {
			visitDisjunctionTest(st.getDisjunctionTest(), names);
		} else {
			visitSingleTest(st.getRelationalTest().getSingleTest(), names);
		}
	}

	private static void visitValueTest(ValueTest vt, ArrayList<ArrayList<Pair>> attributes, ArrayList<Pair> values, ArrayList<Triple> triples, Pair variable) {
		debug("visitValueTest: " + vt);
		visitTest(vt.getTest(), values);
		if (vt.isStructuredValueNotation()) {
			for (AttributeValueTest avt : vt.getAttributeValueTests()) {
				String svVariable = null;
				Pair svVariablePair = null;
				if (vt.hasVariable()) {
					svVariablePair = vt.getVariable();
				} else {
					// make fake variable for structured-value expression
					String variableName = variable.getString().substring(1, variable.getString().length() - 1);
					if (!variableName.startsWith("_"))
						variableName = "_" + variableName;
					svVariable = "<" + variableName + "_" + attributes + ">";
					svVariablePair = new Pair(svVariable);
				}
				values.add(svVariablePair);
				ArrayList<ArrayList<Pair>> svAttributes = new ArrayList<ArrayList<Pair>>();
				ArrayList<Pair> svValues = new ArrayList<Pair>();
				visitAttributeValueTest(avt, svAttributes, svValues, triples, svVariablePair);
				ArrayList<Pair> svVariables = new ArrayList<Pair>();
				svVariables.add(svVariablePair);
				debug("Values: " + values + " (visitValueTest, structured value)");
				ArrayList<Triple> newTriples = triplesForVariablesNamesAttributes(svVariables, svAttributes, svValues);
				triples.addAll(newTriples);
			}
		}
	}

	private static void visitSingleTest(SingleTest st, ArrayList<Pair> names) {
		debug("visitSingleTest: " + st);
		if (st.isConstant()) {
			visitConstant(st.getConstant(), names);
		} else {
			names.add(st.getVariable());
		}
	}

	private static void visitConstant(Constant c, ArrayList<Pair> names) {
		debug("visitConstant: " + c);
		names.add(c.getPair());
	}

	private static void visitVarAttrValMake(VarAttrValMake vavm, ArrayList<Triple> triples) {
		debug("visitVarAttrValMake: " + vavm);
		for (AttributeValueMake avm : vavm.getAttributeValueMakes()) {
			ArrayList<Pair> attributes = new ArrayList<Pair>();
			ArrayList<Pair> values = new ArrayList<Pair>();
			visitAttributeValueMake(avm, attributes, values);
			
			// TODO: for debugging
			if (values.contains(null)) {
				System.out.println("NULL");
			}
			
			ArrayList<Pair> variables = new ArrayList<Pair>();
			variables.add(vavm.getVariable());
			ArrayList<Triple> newTriples = triplesForVariablesNamesAttributesShortList(variables, attributes, values);
			triples.addAll(newTriples);
		}
	}

	private static void visitAttributeValueMake(AttributeValueMake avm, ArrayList<Pair> attributes, ArrayList<Pair> values) {
		debug("visitAttributeValueMake: " + avm);
		
		// Hack to deal with numeric indifferent preferences
		boolean wasEquals = false;
		
		for (ValueMake vm : avm.getValueMakes()) {
		    RHSValue rhsValue = vm.getRHSValue();
		    if (rhsValue.isConstant())
		    {
		        String stringValue = rhsValue.getPair().getString();
		        boolean isNumber = stringValue.matches("(\\d+)|(\\d*\\.\\d+)");
		        if (isNumber && wasEquals)
		        {
		            wasEquals = false;
		            continue;
		        }
		    }
			visitValueMake(vm, values);
			wasEquals = false;
		    if (rhsValue.isVariable())
            {
		        Iterator<PreferenceSpecifier> it = vm.getPreferenceSpecifiers();
		        while(it.hasNext())
		        {
		            PreferenceSpecifier ps = it.next();
		            // Let's be lenient here to avoid unnecessary flags in edge cases
		            if (ps.isUnaryPreference() /* && ps.getPreferenceSpecifierType() == PreferenceSpecifier.EQUAL */ )
		            {
		                wasEquals = true;
		            }
		        }
            }
		}
		for (RHSValue rv : avm.getRHSValues()) {
			if (rv.isVariable()) {
				attributes.add(rv.getPair());
			} else {
				visitRHSValue(rv, attributes);
			}
		}
	}

	private static void visitRHSValue(RHSValue rv, ArrayList<Pair> names) {
		debug("visitRHSValue");
		Pair pair = rv.getPair();
		if (rv.isFunctionCall())
		{
		    pair = new Pair("<__FUNCTION__" + pair.getString() + ">", pair.getOffset(), pair.getEndOffset());
		}
		else if (rv.isConstant())
		{
	        debug("visitRHSValue");
		}
		names.add(pair);
	}

	private static void visitValueMake(ValueMake vm, ArrayList<Pair> values) {
		debug("visitValueMake: " + vm);
		visitRHSValue(vm.getRHSValue(), values);
	}
}
