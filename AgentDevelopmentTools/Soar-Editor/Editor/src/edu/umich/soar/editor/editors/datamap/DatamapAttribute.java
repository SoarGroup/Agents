package edu.umich.soar.editor.editors.datamap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.umich.soar.editor.editors.datamap.DatamapNode.NodeType;

public class DatamapAttribute
{
	public int from;
	public int to;
	public String name;
	public Datamap datamap;
	
	public DatamapAttribute(int from, String name, int to, Datamap datamap)
	{
		this.from = from;
		this.name = name;
		this.to = to;
		this.datamap = datamap;
	}
	
	@Override
	public String toString() {
		DatamapNode dest = datamap.getNode(to);
		if (dest == null) return name;
		if (dest.type == NodeType.SOAR_ID)
		{
		    StringBuilder sb = new StringBuilder();
		    sb.append(name);
		    
		    DatamapNode target = getTarget();
		    if (target != null)
		    {
		        Collection<DatamapNode> nameNodes = target.getChildren("name", NodeType.ENUMERATION);
		        if (!nameNodes.isEmpty())
		        {
		            List<String> names = new ArrayList<String>();
		            for (DatamapNode nameNode : nameNodes)
		            {
		                assert(nameNode.type == NodeType.ENUMERATION);
		                for (String nameValue : nameNode.values)
		                {
		                    names.add(nameValue);
		                }
		            }
		            sb.append(" [");
		            for (int i = 0; i < names.size(); ++i)
		            {
		                sb.append(names.get(i));
		                if (i +1 < names.size())
		                {
		                    sb.append(", ");
		                }
		            }
		            sb.append("]");
		        }
		    }
		    
		    sb.append(" <");
		    sb.append(dest.id);
		    sb.append(">");
		    return sb.toString();
		}
		else if (dest.type == NodeType.ENUMERATION)
		{
			StringBuffer sb = new StringBuffer();
			sb.append(name + " (");
			for (int i = 0; i < dest.values.size(); ++i)
			{
				String value = dest.values.get(i);
				sb.append(value);
				if (i + 1 < dest.values.size())
				{
					sb.append(", ");
				}
			}
			sb.append(")");
			return sb.toString() + " <" + to + ">";
		}
		else if (dest.type == NodeType.INT_RANGE)
		{
			return name + " (" + dest.intMin + ", " + dest.intMax + ")";
		}
		else if (dest.type == NodeType.FLOAT_RANGE)
		{
			return name + " (" + dest.floatMin + ", " + dest.floatMax + ")";
		}
		else if (dest.type == NodeType.LINKED_DATAMAP)
		{
            return name + " (" + dest.relativePath + ")";
		}
		return name;
	}
	
	public DatamapNode getFrom()
	{
		return datamap.getNode(from);
	}
	
	public DatamapNode getTarget()
	{
		return datamap.getNode(to);
	}

	public void setFrom(int from) {
		datamap.removeAttribute(this);
		this.from = from;
		datamap.addAttribute(this);
		datamap.contentChanged(this);
	}

	public Object getSaveString() {
		return "" + from + " " + name + " " + to;
	}

	public void setName(String name) {
		this.name = name;
		datamap.contentChanged(this);
	}

	public void delete()
	{
		datamap.removeAttribute(this);
		datamap.contentChanged(this);
	}

	public boolean isLinked() {
		return datamap.getAttributesTo(to).size() > 1;
	}

	public List<DatamapAttribute> getLinkedAttributes() {
		return datamap.getAttributesTo(to);
	}

	/**
	 * 
	 * @return A path from state <s> to this node.
	 */
	public Object getPathString() {
	    List<Object> pathList = getPathList();
	    StringBuilder sb = new StringBuilder();
	    for (int i = pathList.size() - 1; i >= 0; --i)
	    {
	        Object obj = pathList.get(i);
	        if (obj == null)
	        {
	            sb.append("...");
	        }
	        else if (obj instanceof DatamapAttribute)
	        {
	            sb.append("." + ((DatamapAttribute)obj).name);
	        }
	        else if (obj instanceof DatamapNode)
	        {
	            String[] names = ((DatamapNode) obj).getStateNames();
	            for (int j = 0; j < names.length; ++j)
	            {
	                sb.append(name);
	                if (j + 1 < names.length)
	                {
	                    sb.append('/');
	                }
	            }
	        }
	    }
	    return sb.toString();
	}
	
	/**
	 * 
	 * @return A path of datamap attributes and nodes from this attribute back to a state node.
	 *         The objects may be of type DatmapAttribute, DatamapNode (for the root &lt;s&gt; node),
	 *         or null (if the path gets too long, this will be the last element).
	 */
	public List<Object> getPathList()
	{
	    int maxLength = 20;
        DatamapAttribute current = this;
        List<Object> ret = new ArrayList<Object>();
        ret.add(this);
        for (int i = 0; i < maxLength; ++i)
        {
            if (current.getFrom().hasState())
            {
                DatamapNode from = current.getFrom();
                ret.add(from);
                return ret;
            }
            List<DatamapAttribute> parents = datamap.getAttributesTo(current.from);
            if (parents == null || parents.size() == 0)
            {
                ret.add(null);
                return ret;
            }
            current = parents.get(0);
            ret.add(current);
        }
        ret.add(null);
        return ret;
	}
}
