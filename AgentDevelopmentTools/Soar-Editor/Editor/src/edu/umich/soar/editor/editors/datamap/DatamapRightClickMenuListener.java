package edu.umich.soar.editor.editors.datamap;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import edu.umich.soar.editor.editors.datamap.DatamapNode.NodeType;
import edu.umich.soar.editor.editors.datamap.actions.CreateChildAction;
import edu.umich.soar.editor.editors.datamap.actions.DeleteAttributeAction;
import edu.umich.soar.editor.editors.datamap.actions.EditEnumValuesAction;
import edu.umich.soar.editor.editors.datamap.actions.EditValueAction;
import edu.umich.soar.editor.editors.datamap.actions.FindTestingRulesAction;
import edu.umich.soar.editor.editors.datamap.actions.OpenDatamapAction;
import edu.umich.soar.editor.editors.datamap.actions.RenameAttributeAction;
import edu.umich.soar.editor.editors.datamap.actions.ShowLinkedAttributesAction;

public class DatamapRightClickMenuListener implements IMenuListener {

	private TreeViewer tree;
	private Datamap datamap;
	
	public DatamapRightClickMenuListener(TreeViewer tree, Datamap datamap)
	{
		this.tree = tree;
		this.datamap = datamap;
	}
	
	@Override
	public void menuAboutToShow(IMenuManager manager) {
		manager.removeAll();
		ISelection selection = tree.getSelection();
		if (!(selection instanceof IStructuredSelection)) return;
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		if (element instanceof DatamapAttribute)
		{
	        DatamapAttribute attr = (DatamapAttribute) element;
	          
		    // Make sure this element belongs to this datamap.
		    // If not, ad an option to open its datamap in a new editor.
		    Datamap attrDatamap = attr.datamap;
		    if (attrDatamap != datamap)
		    {
		        manager.add(new OpenDatamapAction(attrDatamap));
		        return;
		    }
		    
			manager.add(new RenameAttributeAction(attr));
			manager.add(new DeleteAttributeAction(attr));
			manager.add(new FindTestingRulesAction(attr, true, false));
			manager.add(new FindTestingRulesAction(attr, false, true));
			manager.add(new FindTestingRulesAction(attr, true, true));
			
			if (attr.getTarget().type == NodeType.SOAR_ID)
			{
				MenuManager addChildManager = new MenuManager("Add Child");
				for (NodeType nodeType : NodeType.values())
				{
					addChildManager.add(new CreateChildAction(attr, nodeType));
				}
				manager.add(addChildManager);
				
				if (attr.isLinked())
				{
					manager.add(new ShowLinkedAttributesAction(attr));
				}
			}
			
			else if (attr.getTarget().type == NodeType.FLOAT_RANGE)
			{
				MenuManager editValueManager = new MenuManager("Edit");
				editValueManager.add(new EditValueAction("Min value", attr.getTarget(), false, true));
				editValueManager.add(new EditValueAction("Max value", attr.getTarget(), false, false));
				manager.add(editValueManager);
			}
			
			else if (attr.getTarget().type == NodeType.INT_RANGE)
			{
				MenuManager editValueManager = new MenuManager("Edit");
				editValueManager.add(new EditValueAction("Min value", attr.getTarget(), true, true));
				editValueManager.add(new EditValueAction("Max value", attr.getTarget(), true, false));
				manager.add(editValueManager);
			}
			
			else if (attr.getTarget().type == NodeType.ENUMERATION)
			{
				manager.add(new EditEnumValuesAction(attr.getTarget()));
			}
		}
		else if (element instanceof DatamapNode && ((DatamapNode)element).hasState())
		{
			DatamapNode node = (DatamapNode) element;
			MenuManager addChildManager = new MenuManager("Add Child");
			for (NodeType nodeType : NodeType.values())
			{
				addChildManager.add(new CreateChildAction(node, nodeType));
			}
			manager.add(addChildManager);
		}
	}
}
