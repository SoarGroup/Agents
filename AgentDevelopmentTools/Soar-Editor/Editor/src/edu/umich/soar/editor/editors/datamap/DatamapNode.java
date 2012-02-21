package edu.umich.soar.editor.editors.datamap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

import edu.umich.soar.editor.editors.datamap.Datamap.DatamapChangedListener;

public class DatamapNode implements DatamapChangedListener
{
    public static enum NodeType
    {
        SOAR_ID, ENUMERATION, FLOAT_RANGE, INT_RANGE, STRING, LINKED_DATAMAP;

        public String getName()
        {
            switch (this)
            {
            case ENUMERATION:
                return "Enumeration";
            case FLOAT_RANGE:
                return "Float";
            case INT_RANGE:
                return "Integer";
            case SOAR_ID:
                return "Identifier";
            case STRING:
                return "String";
            case LINKED_DATAMAP:
                return "Linked Datamap";
            }
            return toString();
        }

        public static NodeType get(String name)
        {
            if (name.startsWith("INT"))
            {
                return INT_RANGE;
            }
            return NodeType.valueOf(name);
        }
    }

    public NodeType type;
    public int id;
    public Datamap datamap;

    // For use only by enumerations
    public List<String> values;

    // For use only by floats
    public double floatMin;
    public double floatMax;

    // For use only by ints
    public int intMin;
    public int intMax;

    // For use only by identifiers
    private boolean hasState = false;
    private List<String> stateNames = new ArrayList<String>();

    // For use only by linked datamaps
    public String relativePath;

    public DatamapNode(NodeType type, int id, Datamap datamap)
    {
        this.type = type;
        this.id = id;
        this.datamap = datamap;
        if (type == NodeType.ENUMERATION)
        {
            values = new ArrayList<String>();
        }
    }

    public DatamapNode(String type, int id, Datamap datamap)
    {
        this(NodeType.get(type), id, datamap);
    }

    @Override
    public String toString()
    {
        if (hasState())
        {
            return "state <s>";
        }
        return "Node, " + type + ", " + id;
    }

    public boolean hasState()
    {
        return type == NodeType.SOAR_ID && hasState;
    }

    public void setHasState(boolean hasState)
    {
        this.hasState = hasState;
    }

    public void addStateName(String stateName)
    {
        stateNames.add(stateName);
    }

    public String[] getStateNames()
    {
        return stateNames.toArray(new String[0]);
    }

    public String getStateNamesString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stateNames.size(); ++i)
        {
            String name = stateNames.get(i);
            sb.append(name);
            if (i + 1 < stateNames.size())
            {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public Object getSaveString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(type.toString() + " " + id);
        switch (type)
        {
        case ENUMERATION:
            sb.append(" " + values.size());
            for (String value : values)
            {
                sb.append(" " + value);
            }
            break;
        case FLOAT_RANGE:
            sb.append(" " + floatMin + " " + floatMax);
            break;
        case INT_RANGE:
            sb.append(" " + intMin + " " + intMax);
            break;
        case SOAR_ID:

            break;
        case STRING:

            break;
        case LINKED_DATAMAP:
            sb.append(" " + relativePath);
            break;
        default:

            break;
        }
        return sb.toString();
    }

    public DatamapNode addChild(String name, NodeType nodeType)
    {
        if (type == NodeType.LINKED_DATAMAP)
        {
            Datamap linked = getLinkedDatamap();
            if (linked != null)
            {
                List<DatamapNode> stateNodes = linked.getStateNodes();
                if (!stateNodes.isEmpty())
                {
                    return stateNodes.get(0).addChild(name, nodeType);
                }
            }
        }
        // Don't add a child to a non-id node.
        if (type != NodeType.SOAR_ID) return null;
        DatamapNode child = new DatamapNode(nodeType, datamap.newId(), datamap);
        DatamapAttribute attribute = new DatamapAttribute(this.id, name, child.id, datamap);
        datamap.addNode(child);
        datamap.addAttribute(attribute);
        datamap.contentChanged(this);
        return child;
    }

    public DatamapNode addLinkedDatamapChild(String name, String relativePath)
    {
        if (type == NodeType.LINKED_DATAMAP)
        {
            Datamap linked = getLinkedDatamap();
            if (linked != null)
            {
                List<DatamapNode> stateNodes = linked.getStateNodes();
                if (!stateNodes.isEmpty())
                {
                    return stateNodes.get(0).addLinkedDatamapChild(name, relativePath);
                }
            }
        }
        // Don't add a child to a non-id node.
        if (type != NodeType.SOAR_ID) return null;
        DatamapNode child = new DatamapNode(NodeType.LINKED_DATAMAP, datamap.newId(), datamap);
        child.relativePath = relativePath;
        DatamapAttribute attribute = new DatamapAttribute(this.id, name, child.id, datamap);
        datamap.addNode(child);
        datamap.addAttribute(attribute);
        datamap.contentChanged(this);
        return child;
    }

    public void addLink(String name, DatamapNode child)
    {
        DatamapAttribute attribute = new DatamapAttribute(this.id, name, child.id, datamap);
        datamap.addAttribute(attribute);
        datamap.contentChanged(this);
    }

    public List<DatamapNode> getChildren()
    {
        if (this.type == NodeType.LINKED_DATAMAP)
        {
            Datamap datamap = getLinkedDatamap();
            if (datamap != null)
            {
                List<DatamapNode> stateNodes = datamap.getStateNodes();
                List<DatamapNode> ret = new ArrayList<DatamapNode>();
                for (DatamapNode stateNode : stateNodes)
                {
                    ret.addAll(stateNode.getChildren());
                }
                return ret;
            }
        }

        List<DatamapNode> children = new ArrayList<DatamapNode>();
        for (DatamapAttribute attribute : datamap.getAttributesFrom(id))
        {
            DatamapNode child = datamap.getNode(attribute.to);
            children.add(child);
        }
        return children;
    }

    public List<DatamapNode> getChildren(String name)
    {
        if (this.type == NodeType.LINKED_DATAMAP)
        {
            Datamap datamap = getLinkedDatamap();
            if (datamap != null)
            {
                List<DatamapNode> ret = new ArrayList<DatamapNode>();
                List<DatamapNode> stateNodes = datamap.getStateNodes();
                for (DatamapNode stateNode : stateNodes)
                {
                    ret.addAll(stateNode.getChildren(name));
                }
                return ret;
            }
        }

        List<DatamapNode> children = new ArrayList<DatamapNode>();
        for (DatamapAttribute attribute : datamap.getAttributesFrom(id))
        {
            if (attribute.name.equals(name))
            {
                DatamapNode child = datamap.getNode(attribute.to);
                if (child != null)
                {
                    children.add(child);
                }
            }
        }
        /*
         * if (hasState && name.equals("superstate")) { List<Datamap>
         * superstates = datamap.findSuperstateDatamaps(); if (superstates !=
         * null) { for (Datamap ss : superstates) { List<DatamapNode> ssNodes =
         * ss.getStateNodes(); children.addAll(ssNodes); } } }
         */
        return children;
    }

    public List<DatamapNode> getChildren(String name, NodeType type)
    {
        if (this.type == NodeType.LINKED_DATAMAP)
        {
            Datamap datamap = getLinkedDatamap();
            if (datamap != null)
            {
                List<DatamapNode> ret = new ArrayList<DatamapNode>();
                List<DatamapNode> stateNodes = datamap.getStateNodes();
                for (DatamapNode stateNode : stateNodes)
                {
                    ret.addAll(stateNode.getChildren(name, type));
                }
                return ret;
            }
        }

        List<DatamapNode> children = new ArrayList<DatamapNode>();
        for (DatamapAttribute attribute : datamap.getAttributesFrom(id))
        {
            if (attribute.name.equals(name))
            {
                DatamapNode child = datamap.getNode(attribute.to);
                if (child != null && child.type == type)
                {
                    children.add(child);
                }
            }
        }

        return children;
    }

    /*
     * public List<DatamapNode> getChildren(NodeType type) { List<DatamapNode>
     * children = new ArrayList<DatamapNode>(); for (DatamapAttribute attribute
     * : datamap.getAttributesFrom(id)) { DatamapNode child =
     * datamap.getNode(attribute.to); if (child != null && child.type == type) {
     * children.add(child); } }
     * 
     * return children; }
     */

    public String tabName()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < stateNames.size(); ++i)
        {
            sb.append(stateNames.get(i));
            if (i + 1 < stateNames.size())
            {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public void writeProblemSpaceToFile(IFile file)
    {
        // Find the subset of this problem space that is pointed to by this
        // problem space,
        // not including <s>.superstate.
        if (!hasState)
        {
            return;
        }
        Collection<DatamapNode> nodes = new HashSet<DatamapNode>();
        Collection<DatamapAttribute> attributes = new HashSet<DatamapAttribute>();

        Collection<DatamapNode> currentNodes = new HashSet<DatamapNode>();

        nodes.add(this);

        for (DatamapAttribute attribute : datamap.getAttributesFrom(id))
        {
            DatamapNode child = datamap.getNode(attribute.to);
            if (child.hasState())
            {
                continue;
            }
            attributes.add(attribute);
            nodes.add(child);
            currentNodes.add(child);
        }

        while (currentNodes.size() > 0)
        {
            Set<DatamapNode> childNodes = new HashSet<DatamapNode>();
            for (DatamapNode node : currentNodes)
            {
                for (DatamapAttribute attribute : datamap.getAttributesFrom(node.id))
                {
                    if (attributes.contains(attribute))
                    {
                        continue;
                    }
                    attributes.add(attribute);
                    DatamapNode child = datamap.getNode(attribute.to);
                    if (nodes.contains(child))
                    {
                        continue;
                    }
                    nodes.add(child);
                    childNodes.add(child);
                }
            }
            currentNodes = childNodes;
        }

        Datamap.writeToFile(file, nodes, attributes, null);
    }

    public Datamap getLinkedDatamap()
    {
        if (type != NodeType.LINKED_DATAMAP) return null;
        if (relativePath == null) return null;

        IContainer datamapParent = datamap.getIFile().getParent();
        IPath datamapParentPath = datamapParent.getProjectRelativePath();

        // IPath linkedDatamapPath = datamapParentPath.append("/" +
        // relativePath);
        IResource linkedDatamapPath = datamapParent.findMember(relativePath);
        if (!(linkedDatamapPath instanceof IFile)) return null;

        // IFile linkedDatamapFile =
        // ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(linkedDatamapPath);
        Datamap linkedDatamap = Datamap.read((IFile) linkedDatamapPath);
        return linkedDatamap;
    }

    /**
     * 
     * @return If this is a linked datamap node, return the linked datamap.
     *         Otherwise, return this node's datamap.
     */
    public Datamap getDatamap()
    {
        if (type == NodeType.LINKED_DATAMAP)
        {
            Datamap ret = getLinkedDatamap();
            if (ret != null) return ret;
        }
        return datamap;
    }

    @Override
    public boolean onDatamapChanged(Datamap datamap, Object changed)
    {
        if (datamap != this.datamap)
        {
            this.datamap.contentChanged(null);
        }
        return true;
    }
}
