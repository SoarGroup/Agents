package edu.umich.soar.editor.editors.datamap.actions;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class DatamapDragAdapter implements DragSourceListener {

	@Override
	public void dragFinished(DragSourceEvent event) {
		
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		LocalSelectionTransfer.getTransfer().setSelection(getSelectionFromEvent(event));
	}

	@Override
	public void dragStart(DragSourceEvent event) {

	}

	private ISelection getSelectionFromEvent(DragSourceEvent event) {
		Widget widget = event.widget;
		if (widget instanceof DragSource) {
			DragSource dragSource = (DragSource) widget;
			Control control = dragSource.getControl();
			if (control instanceof Tree) {
				TreeItem[] selectedItems = ((Tree)control).getSelection();
				StructuredSelection selection = new StructuredSelection(selectedItems);
				return selection;
			}
		}
		return new StructuredSelection();
	}
	
	private TreeItem[] getSelectedItemsFromEvent(DragSourceEvent event) {
		Widget widget = event.widget;
		if (widget instanceof DragSource) {
			DragSource dragSource = (DragSource) widget;
			Control control = dragSource.getControl();
			if (control instanceof Tree) {
				TreeItem[] selectedItems = ((Tree)control).getSelection();
				return selectedItems;
			}
		}
		return new TreeItem[0];
	}
}
