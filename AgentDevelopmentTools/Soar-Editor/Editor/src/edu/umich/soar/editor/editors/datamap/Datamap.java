package edu.umich.soar.editor.editors.datamap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import edu.umich.soar.editor.editors.datamap.DatamapNode.NodeType;
import edu.umich.soar.editor.search.SoarSearchResultsView;

public class Datamap implements ITreeContentProvider
{

    public static interface DatamapSavedListener
    {
        boolean onDatamapSaved(Datamap datamap);
    }

    public static interface DatamapChangedListener
    {
        boolean onDatamapChanged(Datamap datamap, Object changed);
    }

    private static Map<IFile, Datamap> filesToDatamaps = new HashMap<IFile, Datamap>();

    /**
     * For use by the datmap editor
     * 
     * @author miller
     * 
     */
    /*
     * public static class SuperstateAttribute { private DatamapNode
     * superstateNode;
     * 
     * public SuperstateAttribute(DatamapNode superstateNode) {
     * this.superstateNode = superstateNode; }
     * 
     * @Override public String toString() { return "superstate (" +
     * superstateNode.datamap.filename + ": " +
     * superstateNode.getStateNamesString() + ")"; }
     * 
     * public DatamapNode getSuperstateNode() { return superstateNode; } }
     */

    public DatamapNode makeNode(String line)
    {
        String[] tokens = line.split("\\s+");
        if (tokens.length < 2)
        {
            return null;
        }
        String type = tokens[0];
        int id;
        try
        {
            id = Integer.valueOf(tokens[1]);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return null;
        }
        DatamapNode node = new DatamapNode(type, id, this);
        if (node.type == NodeType.ENUMERATION)
        {
            for (int i = 3; i < tokens.length; ++i)
            {
                node.values.add(tokens[i]);
            }
        }
        else if (node.type == NodeType.FLOAT_RANGE)
        {
            try
            {
                node.floatMin = Double.valueOf(tokens[2]);
                node.floatMax = Double.valueOf(tokens[3]);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
                valid = false;
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                e.printStackTrace();
                valid = false;
            }
        }
        else if (node.type == NodeType.INT_RANGE)
        {
            try
            {
                node.intMin = Integer.valueOf(tokens[2]);
                node.intMax = Integer.valueOf(tokens[3]);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
                valid = false;
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                e.printStackTrace();
                valid = false;
            }
        }
        else if (node.type == NodeType.LINKED_DATAMAP)
        {
            node.relativePath = tokens[2];
        }
        return node;
    }

    public DatamapAttribute makeAttrbute(String line)
    {
        String[] tokens = line.split("\\s+");
        if (tokens.length < 3) return null;
        int from = Integer.valueOf(tokens[0]);
        String name = tokens[1];
        int to;
        try
        {
            to = Integer.valueOf(tokens[2]);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            throw e;
        }
        DatamapAttribute ret = new DatamapAttribute(from, name, to, this);
        return ret;
    }

    // Maps node.id onto that node.
    private Map<Integer, DatamapNode> nodes;

    // Maps attribute.from values onto a list of attributes with that .from
    // value.
    private Map<Integer, ArrayList<DatamapAttribute>> attributes;

    // Maps attribute.to values onto a list of attributes with that .to value.
    private Map<Integer, ArrayList<DatamapAttribute>> reversedAttributes;
    private int maxId = -1;
    private boolean valid = true;
    private String filename;
    private IFile input;

    // Maps the name of a state onto root nodes for that state.
    private Map<String, DatamapNode> stateNodeMap;
    private List<DatamapNode> stateNodeList;

    // Event listeners
    private Set<DatamapSavedListener> datamapSavedListeners;
    private Set<DatamapChangedListener> datamapChangedListeners;

    private Datamap(IFile input)
    {
        nodes = new HashMap<Integer, DatamapNode>();
        attributes = new HashMap<Integer, ArrayList<DatamapAttribute>>();
        reversedAttributes = new HashMap<Integer, ArrayList<DatamapAttribute>>();
        stateNodeMap = new HashMap<String, DatamapNode>();
        stateNodeList = new ArrayList<DatamapNode>();
        datamapSavedListeners = new HashSet<Datamap.DatamapSavedListener>();
        datamapChangedListeners = new HashSet<Datamap.DatamapChangedListener>();

        InputStream is;
        filename = input.getName();
        this.input = input;
        try
        {
            is = input.getContents();
            Scanner s = new Scanner(is);
            int num_nodes = -1;
            while (s.hasNextLine())
            {
                String line = s.nextLine().trim();
                if (line.trim().isEmpty()) continue;
                if (num_nodes == -1)
                {
                    num_nodes = Integer.valueOf(line);
                }
                else
                {
                    DatamapNode node = makeNode(line);
                    if (node == null)
                    {
                        valid = false;
                        continue;
                    }
                    checkId(node.id);
                    nodes.put(node.id, node);
                    --num_nodes;
                    if (num_nodes == 0)
                    {
                        break;
                    }
                }
            }
            int num_attributes = -1;
            while (s.hasNextLine())
            {
                String line = s.nextLine();
                if (line.trim().isEmpty()) continue;
                if (num_attributes == -1)
                {
                    num_attributes = Integer.valueOf(line);
                }
                else
                {
                    DatamapAttribute attribute = makeAttrbute(line);
                    if (attribute == null)
                    {
                        valid = false;
                        continue;
                    }
                    addAttribute(attribute);
                    --num_attributes;
                    if (num_attributes == 0)
                    {
                        break;
                    }
                }
            }
            findStates();
        }
        catch (CoreException e)
        {
            e.printStackTrace();
            valid = false;
        }
    }

    public String[] getStateNames()
    {
        return stateNodeMap.keySet().toArray(new String[0]);
    }

    public List<DatamapNode> getStateNodes()
    {
        return stateNodeList;
    }

    private void findStates()
    {
        for (DatamapNode node : nodes.values())
        {
            if (node.type != NodeType.SOAR_ID) continue;
            List<DatamapNode> typeNodes = node.getChildren("type", NodeType.ENUMERATION);
            boolean foundTypeState = false;
            for (DatamapNode typeNode : typeNodes)
            {
                if (typeNode.values.contains("state"))
                {
                    foundTypeState = true;
                    break;
                }
            }
            if (!foundTypeState)
            {
                continue;
            }
            ArrayList<String> names = new ArrayList<String>();
            List<DatamapNode> nameNodes = node.getChildren("name", NodeType.ENUMERATION);
            for (DatamapNode nameNode : nameNodes)
            {
                names.addAll(nameNode.values);
            }
            if (nameNodes.size() == 0) continue;
            node.setHasState(true);
            stateNodeList.add(node);
            for (String name : names)
            {
                stateNodeMap.put(name, node);
                node.addStateName(name);
            }
        }
    }

    private void checkId(int id)
    {
        if (id >= maxId)
        {
            maxId = id + 1;
        }
    }

    public int newId()
    {
        maxId += 1;
        return maxId - 1;
    }

    public String getFilename()
    {
        return filename;
    }

    public File getFile()
    {
        return input.getRawLocation().makeAbsolute().toFile();
    }

    public IFile getIFile()
    {
        return input;
    }

    public void contentChanged(Object changed)
    {
        contentChanged(changed, false);
    }

    public void contentChanged(Object changed, boolean closing)
    {
        if (!closing)
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(input.getName());
            try
            {
                FileEditorInput fileEditorInput = new FileEditorInput(input);
                IEditorPart part = page.findEditor(fileEditorInput);
                if (part == null)
                {
                    part = page.openEditor(fileEditorInput, desc.getId());
                    /*
                     * if (part instanceof DatamapEditor) { DatamapEditor editor
                     * = (DatamapEditor) part; editor.setDatamap(this); }
                     */
                }
            }
            catch (PartInitException e)
            {
                e.printStackTrace();
            }
        }

        // Notify listeners
        List<DatamapChangedListener> toRemove = new ArrayList<Datamap.DatamapChangedListener>();
        for (DatamapChangedListener listener : datamapChangedListeners)
        {
            if (!listener.onDatamapChanged(this, changed))
            {
                toRemove.add(listener);
            }
        }
        datamapSavedListeners.removeAll(toRemove);

        /*
         * if (editor != null) { editor.contentChanged(changed); }
         */
    }

    public void addAttribute(DatamapAttribute attribute)
    {
        ArrayList<DatamapAttribute> list = attributes.get(attribute.from);
        if (list == null)
        {
            list = new ArrayList<DatamapAttribute>();
            attributes.put(attribute.from, list);
        }
        list.add(attribute);
        list = reversedAttributes.get(attribute.to);
        if (list == null)
        {
            list = new ArrayList<DatamapAttribute>();
            reversedAttributes.put(attribute.to, list);
        }
        list.add(attribute);
    }

    public void removeAttribute(DatamapAttribute attribute)
    {
        ArrayList<DatamapAttribute> list = attributes.get(attribute.from);
        if (list != null)
        {
            list.remove(attribute);
        }
        list = reversedAttributes.get(attribute.to);
        if (list != null)
        {
            list.remove(attribute);
        }
    }

    public List<DatamapAttribute> getAttributesFrom(int fromId)
    {
        List<DatamapAttribute> ret = attributes.get(fromId);
        if (ret != null) return ret;
        return new ArrayList<DatamapAttribute>();
    }

    public List<DatamapAttribute> getAttributesTo(int toId)
    {
        List<DatamapAttribute> ret = reversedAttributes.get(toId);
        if (ret != null) return ret;
        return new ArrayList<DatamapAttribute>();
    }

    public void addNode(DatamapNode node)
    {
        nodes.put(node.id, node);
    }

    private void removeNode(DatamapNode node)
    {
        nodes.remove(node.id);
    }

    @Override
    public void dispose()
    {

    }

    @Override
    public void inputChanged(Viewer arg0, Object arg1, Object arg2)
    {

    }

    private static Object[] staticGetChildren(Object parent)
    {
        List<Object> ret = null;
        if (parent instanceof DatamapNode)
        {
            DatamapNode node = (DatamapNode) parent;

            ret = new ArrayList<Object>();
            Collection<DatamapAttribute> childAttributes = node.datamap.getAttributesFrom(node.id);
            ret.addAll(childAttributes);
        }
        else if (parent instanceof DatamapAttribute)
        {
            ret = new ArrayList<Object>();
            DatamapAttribute attribute = (DatamapAttribute) parent;
            DatamapNode child = attribute.datamap.nodes.get(attribute.to);

            if (child.type == NodeType.LINKED_DATAMAP)
            {
                Datamap linkedDatamap = child.getLinkedDatamap();
                if (linkedDatamap != null)
                {
                    List<DatamapNode> linkedStateNodes = linkedDatamap.getStateNodes();
                    if (linkedStateNodes.size() > 0)
                    {
                        linkedDatamap.addDatamapChangedListener(child);
                        return staticGetChildren(linkedStateNodes.get(0));
                    }
                }
            }
            Collection<DatamapAttribute> childAttributes = child.datamap.getAttributesFrom(child.id);
            if (childAttributes == null) return null;
            ret.addAll(childAttributes);
        }

        if (ret == null)
        {
            return null;
        }

        Collections.sort(ret, new Comparator<Object>()
        {

            @Override
            public int compare(Object o1, Object o2)
            {
                String s1 = "" + o1;
                String s2 = "" + o2;
                String[] a1 = s1.split("[\\<\\>]");
                String[] a2 = s2.split("[\\<\\>]");
                int ret = (a1[0]).compareTo(a2[0]);
                if (ret != 0) return ret;

                if (o1 instanceof DatamapNode && o2 instanceof DatamapNode)
                {
                    DatamapNode n1 = (DatamapNode) o1;
                    DatamapNode n2 = (DatamapNode) o2;
                    if (n1.id == n2.id) return 0;
                    return n1.id < n2.id ? 1 : -1;
                }

                return s1.compareTo(s2);
            }
        });

        return ret.toArray(new Object[0]);
    }

    @Override
    public Object[] getChildren(Object parent)
    {
        return staticGetChildren(parent);
    }

    @Override
    public Object[] getElements(Object parent)
    {
        if (parent instanceof Object[])
        {
            return (Object[]) parent;
        }
        return getChildren(parent);
    }

    @Override
    public Object getParent(Object parent)
    {
        return null;
    }

    @Override
    public boolean hasChildren(Object parent)
    {
        return getChildren(parent).length > 0;
    }

    public boolean writeToFile(IFile file, IProgressMonitor monitor)
    {
        Collection<DatamapAttribute> attribtuesCollection = new HashSet<DatamapAttribute>();
        for (ArrayList<DatamapAttribute> attributeList : attributes.values())
        {
            attribtuesCollection.addAll(attributeList);
        }

        if (!writeToFile(file, nodes.values(), attribtuesCollection, monitor))
        {
            return false;
        }

        List<DatamapSavedListener> toRemove = new ArrayList<Datamap.DatamapSavedListener>();
        for (DatamapSavedListener listener : datamapSavedListeners)
        {
            if (!listener.onDatamapSaved(this))
            {
                toRemove.add(listener);
            }
        }
        datamapSavedListeners.removeAll(toRemove);
        return true;
    }

    public static boolean writeToFile(IFile file, Collection<DatamapNode> nodes, Collection<DatamapAttribute> attributes, IProgressMonitor monitor)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(nodes.size());
        sb.append('\n');
        for (DatamapNode node : nodes)
        {
            sb.append(node.getSaveString());
            sb.append('\n');
        }
        int numAttribtues = attributes.size();
        sb.append(numAttribtues);
        sb.append('\n');
        for (DatamapAttribute attribute : attributes)
        {
            sb.append(attribute.getSaveString());
            sb.append('\n');
        }
        ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
        try
        {
            if (!file.exists())
            {
                file.create(is, false, monitor);
            }
            else
            {
                file.setContents(is, 0, monitor);
            }
            return true;
        }
        catch (CoreException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public Map<Integer, ArrayList<DatamapAttribute>> getAttributes()
    {
        return attributes;
    }

    public Map<Integer, DatamapNode> getNodes()
    {
        return nodes;
    }

    public static Datamap read(IFile file)
    {
        System.out.println("Datamap.read, file: " + file);
        if (filesToDatamaps.containsKey(file))
        {
            Datamap datamap = filesToDatamaps.get(file);
            System.out.println("Found datamap in map, returning: " + datamap);
            return datamap;
        }
        Datamap datamap = new Datamap(file);
        if (datamap.valid)
        {
            filesToDatamaps.put(file, datamap);
            System.out.println("Added datamap to map, returning: " + datamap);
            return datamap;
        }
        System.out.println("Problem reading datama, returning null.");
        return null;
    }

    public DatamapNode getStateNode(String stateName)
    {
        return stateNodeMap.get(stateName);
    }

    public DatamapNode getNode(int to)
    {
        return nodes.get(to);
    }

    public List<IFile> findSuperstateDatamapFiles()
    {
        List<IFile> datamapFiles = new ArrayList<IFile>();
        IContainer parent = input.getParent().getParent();
        if (parent == null)
        {
            return null;
        }
        try
        {
            for (IResource member : parent.members())
            {
                if (member instanceof IFile && member.getFileExtension().equalsIgnoreCase("dm"))
                {
                    datamapFiles.add((IFile) member);
                }
            }
        }
        catch (CoreException e)
        {
            e.printStackTrace();
            return null;
        }
        if (datamapFiles.size() != 0) return datamapFiles;
        parent = parent.getParent();
        if (parent == null)
        {
            return null;
        }
        try
        {
            for (IResource member : parent.members())
            {
                if (member instanceof IFile && member.getFileExtension().equalsIgnoreCase("dm"))
                {
                    datamapFiles.add((IFile) member);
                }
            }
        }
        catch (CoreException e)
        {
            e.printStackTrace();
            return null;
        }
        return datamapFiles;
    }

    public List<Datamap> findSuperstateDatamaps()
    {
        List<IFile> superstateDatamapFiles = findSuperstateDatamapFiles();
        if (superstateDatamapFiles == null) return null;
        List<Datamap> ret = new ArrayList<Datamap>();
        for (IFile file : superstateDatamapFiles)
        {
            Datamap datamap = Datamap.read(file);
            if (datamap != null)
            {
                ret.add(datamap);
            }
        }
        return ret;
    }

    public void addDatamapSavedListener(DatamapSavedListener listener)
    {
        if (!datamapSavedListeners.contains(listener))
        {
            datamapSavedListeners.add(listener);
        }
    }

    public void addDatamapChangedListener(DatamapChangedListener listener)
    {
        if (!datamapChangedListeners.contains(listener))
        {
            datamapChangedListeners.add(listener);
        }
    }

    public static void datamapClosed(Datamap datamap)
    {
        System.out.println("Datamap.datamapClosed, datamap: " + datamap);
        IFile file = datamap.input;
        System.out.println("Input is: " + file);
        if (filesToDatamaps.containsKey(file))
        {
            System.out.println("...Removing datmap from map");
            filesToDatamaps.remove(file);
        }
        datamap.contentChanged(null, true);
    }

    public DatamapEditor openInEditor()
    {
        return openInEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
    }

    public DatamapEditor openInEditor(IWorkbenchPage page)
    {
        DatamapEditor view = null;
        String ID = DatamapEditor.ID;

        try
        {
            FileEditorInput editorInput = new FileEditorInput(input);
            view = (DatamapEditor) page.findEditor(editorInput);
            if (view == null)
            {
                view = (DatamapEditor) page.openEditor(editorInput, ID);
            }
            else
            {
                page.activate(view);
            }
            return view;
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
