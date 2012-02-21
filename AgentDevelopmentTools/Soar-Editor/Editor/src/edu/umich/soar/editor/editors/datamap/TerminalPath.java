package edu.umich.soar.editor.editors.datamap;

import java.util.ArrayList;

/**
 * Represents a path from the root node &lt;s&gt; that cannot extend any
 * farther. Either the last node has no child nodes, or the path loops into
 * itself, or it loops into another terminal path, or something like that.
 * 
 * @author miller
 * 
 */
public class TerminalPath
{

    public static enum ProductionSide
    {
        UNKNOWN, CONDITION, ACTION, CONDITION_AND_ACTION;
    }

    public ArrayList<Triple> path;
    public ArrayList<Triple> links = new ArrayList<Triple>();
    public ProductionSide productionSide;

    public TerminalPath(ArrayList<Triple> path, ArrayList<Triple> links)
    {
        this.path = path;
        this.links = links;
        productionSide = ProductionSide.UNKNOWN;
        checkTripleSide(getLastTriple());
    }
    
    public Triple getLastTriple()
    {
        return path.get(path.size() - 1);
    }

    @Override
    public String toString()
    {
        StringBuffer buff = new StringBuffer();
        buff.append("" + path);
        if (links != null)
        {
            buff.append(", loops to: " + links);
        }
        return buff.toString();
    }

    public void setConditionSide()
    {
        switch (productionSide)
        {
        case UNKNOWN:
            productionSide = ProductionSide.CONDITION;
            break;
        case ACTION:
            productionSide = ProductionSide.CONDITION_AND_ACTION;
            break;
        default:
            break;
        }
    }

    public void setActionSide()
    {
        switch (productionSide)
        {
        case UNKNOWN:
            productionSide = ProductionSide.ACTION;
            break;
        case CONDITION:
            productionSide = ProductionSide.CONDITION_AND_ACTION;
            break;
        default:
            break;
        }
    }
    
    public void checkTripleSide(Triple triple)
    {
        if (triple.rhs)
        {
            setActionSide();
        }
        else
        {
            setConditionSide();
        }
    }

    public boolean hasConditionSide()
    {
        return productionSide == ProductionSide.CONDITION || productionSide == ProductionSide.CONDITION_AND_ACTION;
    }
    
    public boolean hasActionSide()
    {
        return productionSide == ProductionSide.ACTION || productionSide == ProductionSide.CONDITION_AND_ACTION;
    }
}
