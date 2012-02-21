package edu.umich.soar.editor.editors.datamap.actions;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.TreeItem;

import edu.umich.soar.editor.editors.datamap.DatamapAttribute;
import edu.umich.soar.editor.editors.datamap.DatamapNode;
import edu.umich.soar.editor.editors.datamap.DatamapNode.NodeType;

public class DatamapDropAdapter extends ViewerDropAdapter {

	//DatamapAttribute attributeTarget;
	DatamapNode nodeTarget;
	
	public DatamapDropAdapter(Viewer viewer) {
		super(viewer);
	}

	@Override
	public boolean performDrop(Object data) {
		if (!(data instanceof StructuredSelection)) {
			return false;
		}
		
		StructuredSelection ss = (StructuredSelection) data;
		Object dataObj = ss.getFirstElement();
		
		if (!(dataObj instanceof TreeItem)) {
			return false;
		}
		
		TreeItem treeItem = (TreeItem) dataObj;
		Object draggedObject = treeItem.getData();
		DatamapAttribute draggedNode = null;

		if (draggedObject instanceof DatamapAttribute)
		{
			draggedNode = (DatamapAttribute) draggedObject;
		}
		
		if (draggedNode == null) {
			return false;
		}
		
		int operation = getCurrentOperation();
		
		if (operation == DND.DROP_MOVE) {
			if (!childCanBeMovedToParent(draggedNode, nodeTarget)) {
				return false;
			}
			draggedNode.setFrom(nodeTarget.id);
		}
		
		else if (operation == DND.DROP_LINK)
		{
			if (!childCanBeMovedToParent(draggedNode, nodeTarget)) {
				return false;
			}
			DatamapNode original = draggedNode.getTarget();
			//DatamapNode newParent = attributeTarget.getTarget();
			if (nodeTarget.type != NodeType.SOAR_ID)
			{
				return false;
			}
			nodeTarget.addLink(draggedNode.name, original);
		}
		
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData targetType) {
		this.nodeTarget = null;
		
		if (target instanceof DatamapAttribute)
		{
			DatamapAttribute attrTarget = (DatamapAttribute) target;
			if (attrTarget.getTarget().type == NodeType.SOAR_ID)
			{
				this.nodeTarget = attrTarget.getTarget();
				return true;
			}
		}
		
		// Allow drops onto root <s> node
		if (target instanceof DatamapNode)
		{
		    DatamapNode nodeTarget = (DatamapNode) target;
		    if (nodeTarget.type == NodeType.SOAR_ID)
		    {
		        this.nodeTarget = nodeTarget;
		        return true;
		    }
		}
		return true;
	}
	
    private boolean childCanBeMovedToParent(DatamapAttribute child, DatamapAttribute parent) {
        if (child == null || parent == null) return false;
        return childCanBeMovedToParent(child, parent.getTarget());
    }
    
    private boolean childCanBeMovedToParent(DatamapAttribute child, DatamapNode parent) {
        if (child == null || parent == null) return false;
        if (child.datamap != parent.getDatamap()) return false;
        return true;
    }
}
