package edu.umich.soar.editor.editors.datamap;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import edu.umich.soar.editor.editors.datamap.DatamapNode.NodeType;
import edu.umich.soar.editor.editors.datamap.actions.EditEnumValuesAction;

public class DatamapDoubleClickListener implements IDoubleClickListener
{
    
    private TreeViewer tree;
    private Datamap datamap;

    public DatamapDoubleClickListener(TreeViewer tree, Datamap datamap)
    {
        this.tree = tree;
        this.datamap = datamap;
    }

    @Override
    public void doubleClick(DoubleClickEvent event)
    {
        ISelection selection = event.getSelection();
        if (!(selection instanceof StructuredSelection)) return;
        StructuredSelection ss = (StructuredSelection) selection;
        Object obj = ss.getFirstElement();
        if (obj == null || !(obj instanceof DatamapAttribute)) return;
        DatamapAttribute attr = (DatamapAttribute) obj;
        DatamapNode node = attr.getTarget();
        if (node.type == NodeType.ENUMERATION)
        {
            EditEnumValuesAction action = new EditEnumValuesAction(node);
            action.run();
        }
    }

}
