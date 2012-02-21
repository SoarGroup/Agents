package edu.umich.soar.editor.editors.datamap;

import java.awt.event.KeyEvent;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import edu.umich.soar.editor.editors.datamap.actions.DatamapDragAdapter;
import edu.umich.soar.editor.editors.datamap.actions.DatamapDropAdapter;
import edu.umich.soar.editor.editors.datamap.actions.DeleteAttributeAction;

public class DatamapTreeEditor extends EditorPart implements IPropertyListener, DisposeListener {

	private Composite parent;
	private TreeViewer tree;
	private Datamap datamap;
	private boolean dirty;
	private DatamapNode stateNode;
	
	public DatamapTreeEditor(Datamap datamap, DatamapNode stateNode)
	{
		super();
		this.datamap = datamap;
		this.stateNode = stateNode;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInputWithNotify(input);
		addPropertyListener(this);
		dirty = false;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		tree = new TreeViewer(parent); // , SWT.FULL_SELECTION /* | SWT.MULTI */ | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		//tree.getTree().setHeaderVisible(true);
		//tree.getTree().setLinesVisible(true);
        
		
		TreeColumn leftColumn = new TreeColumn(tree.getTree(), SWT.NONE);
		
		leftColumn.setWidth(500);
		leftColumn.setResizable(true);
		leftColumn.setText("Datamap Nodes");
		
		/*
		TreeColumn rightColumn = new TreeColumn(tree.getTree(), SWT.NONE);
		rightColumn.setWidth(200);
		rightColumn.setResizable(true);
		rightColumn.setText("Comments");
		*/
		
        // getSite().setSelectionProvider(tree);

		tree.setContentProvider(datamap);
		tree.setLabelProvider(new SoarDatamapLabelProvider());
		tree.setInput(new Object[] { stateNode });
		tree.expandToLevel(2);
		
		tree.getControl().addKeyListener(new org.eclipse.swt.events.KeyListener() {
			
			@Override
			public void keyReleased(org.eclipse.swt.events.KeyEvent event) {
				
			}
			
			@Override
			public void keyPressed(org.eclipse.swt.events.KeyEvent event) {
				if (event.keyCode == KeyEvent.VK_DELETE)
				{
					ISelection selection = tree.getSelection();
					if (selection instanceof StructuredSelection)
					{
						StructuredSelection ss = (StructuredSelection) selection;
						Object obj = ss.getFirstElement();
						if (obj instanceof DatamapAttribute)
						{
							DatamapAttribute attr = (DatamapAttribute) obj;
							DeleteAttributeAction action = new DeleteAttributeAction(attr);
							action.run();
						}
					}
				}
			}
		});
		
		/*
		tree.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					// selectedRows = (IStructuredSelection) selection;
				}
			}
		});
		*/

		MenuManager manager = new MenuManager();
		manager.addMenuListener(new DatamapRightClickMenuListener(tree, datamap));
		Menu menu = manager.createContextMenu(tree.getTree());
		tree.getTree().setMenu(menu);
		tree.addDoubleClickListener(new DatamapDoubleClickListener(tree, datamap));
		tree.addDragSupport(DND.DROP_MOVE | DND.DROP_LINK, new Transfer[] {LocalSelectionTransfer.getTransfer()}, new DatamapDragAdapter());
        tree.addDropSupport(DND.DROP_MOVE | DND.DROP_LINK, new Transfer[] {LocalSelectionTransfer.getTransfer()}, new DatamapDropAdapter(tree));
        
        parent.addDisposeListener(this);
   	}

	@Override
	public void setFocus() {
		
	}
	
	public void contentChanged(final Object changed)
	{
	    if (changed != null)
	    {
	        setDirty(true);
	    }
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				try {
			        TreePath[] paths = tree.getExpandedTreePaths();
					tree.refresh();
					tree.setExpandedTreePaths(paths);
			        if (changed != null && changed instanceof DatamapNode)
			        {
			            DatamapNode node = (DatamapNode) changed;
			            List<DatamapAttribute> attrs = node.datamap.getAttributesTo(node.id);
			            for (DatamapAttribute attr : attrs)
			            {
			                tree.setExpandedState(attr, true);
			            }
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void doSave(IProgressMonitor arg0) {

	}

	@Override
	public void doSaveAs() {

	}

    public TreeViewer getTree()
    {
        return tree;
    }

    @Override
    public void propertyChanged(Object source, int propertyId)
    {
        if (propertyId == PROP_INPUT)
        {
            contentChanged(null);
        }
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
        System.out.println("widgetdisposed: " + this);
        Datamap.datamapClosed(datamap);
    }

}
