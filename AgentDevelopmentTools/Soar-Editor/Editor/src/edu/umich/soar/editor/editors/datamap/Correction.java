package edu.umich.soar.editor.editors.datamap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Link;

import edu.umich.soar.editor.editors.SoarEditor;
import edu.umich.soar.editor.editors.datamap.DatamapNode.NodeType;

/**
 * Represents a single correction to be made to the existing datamap. The user
 * can choose which corrections to apply and which to ignore.
 * 
 * @author miller
 * 
 */
public class Correction implements SelectionListener
{

    public DatamapNode node;
    public ArrayList<Triple> addition;
    public ArrayList<Triple> links = new ArrayList<Triple>();
    public Triple pathTriple;
    public boolean anyChildren;
    public List<Triple> pathSoFar;

    /**
     * Class constructor.
     * 
     * @param row
     * @param addition
     * @param links
     * @param pathTriple
     * @param anyChildren
     */
    public Correction(DatamapNode node, ArrayList<Triple> addition, ArrayList<Triple> links, Triple pathTriple, boolean anyChildren, List<Triple> pathSoFar)
    {
        this.node = node;
        this.addition = addition;
        this.links = links;
        this.pathTriple = pathTriple;
        this.anyChildren = anyChildren;
        this.pathSoFar = pathSoFar;

        // Handle special case where pathTriple's value is variable
        // and node is not of type SOAR_ID
        if (node.type != NodeType.SOAR_ID && pathTriple.valueIsVariable())
        {

        }
    }

    public String toString(List<Datamap> datamaps, String folderName)
    {
        StringBuffer sb = new StringBuffer();
        if (anyChildren)
        {
            sb.append("Value \"" + pathTriple.value + "\"");
        }
        else
        {
            sb.append("Attribute \"" + pathTriple.attribute + "\" of type " + pathTriple.getTypeString());
        }
        sb.append(" not found in datamap");
        if (datamaps.size() > 1)
        {
            sb.append("s: ");
            for (int i = 0; i < datamaps.size(); ++i)
            {
                Datamap datamap = datamaps.get(i);
                sb.append(datamap.getFilename());
                if (i + 1 < datamaps.size())
                {
                    sb.append(", ");
                }
            }
        }
        else if (datamaps.size() > 0)
        {
            sb.append(" " + datamaps.get(0).getFilename());
        }
        // sb.append(", problem space \"" + folderName + "\"");

        sb.append("\n<s>");
        for (int i = 0; i < pathSoFar.size(); ++i)
        {
            Triple t = pathSoFar.get(i);
            sb.append("." + t.attribute);
        }

        sb.append("." + pathTriple.attribute + " " + pathTriple.value);

        return sb.toString();
    }

    public int getErrorOffset()
    {
        return anyChildren ? pathTriple.valueOffset : pathTriple.attributeOffset;
    }

    public int getErrorLength()
    {
        return anyChildren ? pathTriple.value.length() : pathTriple.attribute.length();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event)
    {
        applySolution((Integer) event.widget.getData());
        Object source = event.getSource();
        System.out.println("soruce: " + source);
        if (source instanceof Link)
        {
            ((Link) source).getShell().setVisible(false);
        }
    }

    @Override
    public void widgetSelected(SelectionEvent event)
    {
        applySolution((Integer) event.widget.getData());
        Object source = event.getSource();
        System.out.println("soruce: " + source);
        if (source instanceof Link)
        {
            ((Link) source).getShell().setVisible(false);
        }
    }

    public void applySolution(int solutionIndex)
    {
        System.out.println("Applying correction");
        System.out.println("node: " + node);
        System.out.println("addition: " + addition);
        System.out.println("links: " + links);
        System.out.println("pathTriple: " + pathTriple);
        System.out.println("anyChildren: " + anyChildren);
        System.out.println("pathSoFar: " + pathSoFar);
        if (solutionIndex == -1)
        {
            // Open corresponding datamap
            node.getDatamap().openInEditor();
            return;
        }
        if (!anyChildren)
        {
            if (pathTriple.getNodeType() == NodeType.STRING)
            {
                if (solutionIndex == 0)
                {
                    DatamapNode child = node.addChild(pathTriple.attribute, NodeType.ENUMERATION);

                    // Also add enumeration value
                    if (child != null)
                    {
                        child.values.add(pathTriple.value);
                    }
                }
                else
                {
                    node.addChild(pathTriple.attribute, NodeType.STRING);
                }
            }
            else if (pathTriple.getNodeType() == NodeType.SOAR_ID)
            {
                NodeType type = NodeType.values()[solutionIndex];
                node.addChild(pathTriple.attribute, type);
            }

            else
            {
                // Add new child of type matching pathTriple
                node.addChild(pathTriple.attribute, pathTriple.getNodeType());
            }
        }
        else
        {
            // Add new value to existing enumeration
            NodeType childType = pathTriple.getNodeType();
            if (childType == NodeType.STRING)
            {
                List<DatamapNode> enumNodes = node.getChildren(pathTriple.attribute, NodeType.ENUMERATION);
                if (enumNodes != null && enumNodes.size() > 0)
                {
                    enumNodes.get(0).values.add(pathTriple.value);
                    node.getDatamap().contentChanged(node);
                }
            }
        }
    }

    public int getNumSolutions()
    {
        if (!anyChildren && pathTriple.getNodeType() == NodeType.STRING)
        {
            return 2;
        }
        if (!anyChildren && pathTriple.getNodeType() == NodeType.SOAR_ID)
        {
            return NodeType.values().length;
        }
        return 1;
    }

    public String getBaseSolutionText()
    {
        if (anyChildren)
        {
            return null;
        }
        return "Add attribute \"" + pathTriple.attribute + "\" of type:";
    }

    public String getEndSolutionText(int i)
    {
        if (anyChildren)
        {
            return "Add value \"" + pathTriple.value + "\"";
        }
        if (pathTriple.getNodeType() == NodeType.STRING)
        {
            if (i == 0)
            {
                return NodeType.ENUMERATION.getName();
            }
            else
            {
                 return NodeType.STRING.getName();
            }
        }
        else if (pathTriple.getNodeType() == NodeType.SOAR_ID)
        {
            NodeType type = NodeType.values()[i];
            return type.getName();
        }
        else
        {
            return pathTriple.getTypeString();
        }
    }
    
    public String getSolutionText(int i)
    {
        return getBaseSolutionText() + " " + getEndSolutionText(i);
    }

    /**
     * Applys this correction to its datamap.
     */
    /*
     * public void apply() { SoarDatabaseRow currentRow = row; for (int i = 0; i
     * < addition.size(); ++i) { Triple triple = addition.get(i); if
     * (triple.valueIsVariable()) { currentRow =
     * createJoinedChildIfNotExists(currentRow, Table.DATAMAP_IDENTIFIERS,
     * triple.attribute); } else if (triple.valueIsInteger()) { currentRow =
     * createJoinedChildIfNotExists(currentRow, Table.DATAMAP_INTEGERS,
     * triple.attribute); editMinMaxValues(currentRow, triple); } else if
     * (triple.valueIsFloat()) { currentRow =
     * createJoinedChildIfNotExists(currentRow, Table.DATAMAP_FLOATS,
     * triple.attribute); editMinMaxValues(currentRow, triple); } else if
     * (triple.valueIsString()) { if (triple.value.equals(Triple.STRING_VALUE))
     * { currentRow = createJoinedChildIfNotExists(currentRow,
     * Table.DATAMAP_STRINGS, triple.attribute); } else {
     * ArrayList<ISoarDatabaseTreeItem> enumerations =
     * currentRow.getDirectedJoinedChildrenOfType(Table.DATAMAP_ENUMERATIONS,
     * false, false); SoarDatabaseRow enumeration = null; for
     * (ISoarDatabaseTreeItem enumItem : enumerations) { SoarDatabaseRow enumRow
     * = (SoarDatabaseRow) enumItem; if (enumRow.getName().equals(triple.value))
     * { enumeration = enumRow; break; } }
     * 
     * if (enumeration == null) { enumeration =
     * createJoinedChildIfNotExists(currentRow, Table.DATAMAP_ENUMERATIONS,
     * triple.attribute); } ArrayList<SoarDatabaseRow> enumValues =
     * enumeration.getChildrenOfType(Table.DATAMAP_ENUMERATION_VALUES); boolean
     * hasValue = false; for (SoarDatabaseRow valueRow : enumValues) { if
     * (valueRow.getName().equals(triple.value)) { hasValue = true; break; } }
     * if (!hasValue) {
     * enumeration.createChild(Table.DATAMAP_ENUMERATION_VALUES, triple.value);
     * } currentRow = enumeration; } } if (triple.comment != null) {
     * currentRow.setComment(triple.comment); } } tail = currentRow; }
     */

    /**
     * Edits min_value and max_value to include the value of the triple.
     * 
     * @param row
     * @param triple
     */
    /*
     * private static void editMinMaxValues(SoarDatabaseRow row, Triple triple)
     * { assert row.getTable() == Table.DATAMAP_FLOATS || row.getTable() ==
     * Table.DATAMAP_INTEGERS; Object minVal = row.getColumnValue("min_value");
     * Object maxVal = row.getColumnValue("max_value"); if
     * (triple.valueIsFloat()) { Double minValue = (Double) minVal; Double
     * maxValue = (Double) maxVal; double value =
     * Double.parseDouble(triple.value); if (minValue == null) { minValue =
     * value; } if (maxValue == null) { maxValue = value; } if (value <
     * minValue) { minValue = value; } if (value > maxValue) { maxValue = value;
     * } if (!minValue.equals(minVal)) { row.updateValue("min_value", "" +
     * minValue); } if (!maxValue.equals(maxVal)) { row.updateValue("max_value",
     * "" + maxValue); } } else if (triple.valueIsInteger()) { Integer minValue
     * = (Integer) minVal; Integer maxValue = (Integer) maxVal; int value =
     * Integer.parseInt(triple.value); if (minValue == null) { minValue = value;
     * } if (maxValue == null) { maxValue = value; } if (value < minValue) {
     * minValue = value; } if (value > maxValue) { maxValue = value; } if
     * (!minValue.equals(minVal)) { row.updateValue("min_value", "" + minValue);
     * } if (!maxValue.equals(maxVal)) { row.updateValue("max_value", "" +
     * maxValue); } } }
     */

    /**
     * Once all corrections have been applied, this is called to link items in
     * the corrections to each other where needed.
     */
    /*
     * public void applyLinks() { for (Triple link : links) {
     * ArrayList<SoarDatabaseRow> rows =
     * link.getDatamapRowsFromProblemSpace(row.
     * getAncestorRow(Table.PROBLEM_SPACES)); for (SoarDatabaseRow row : rows) {
     * SoarDatabaseRow.joinRows(row, tail, row.getDatabaseConnection()); } } }
     */

    /**
     * Looks for a child of the given row, of the given type. If none exists,
     * creates a new row and returns that.
     * 
     * @param currentRow
     * @param table
     * @param named
     * @return
     */
    /*
     * private SoarDatabaseRow createJoinedChildIfNotExists(SoarDatabaseRow
     * currentRow, Table table, String named) { ArrayList<ISoarDatabaseTreeItem>
     * childItems = currentRow.getDirectedJoinedChildrenOfType(table, false,
     * false); for (ISoarDatabaseTreeItem childItem : childItems) {
     * SoarDatabaseRow childRow = (SoarDatabaseRow) childItem; if
     * (childRow.getName().equals(named)) { return childRow; } } return
     * currentRow.createJoinedChild(table, named); }
     */
}
