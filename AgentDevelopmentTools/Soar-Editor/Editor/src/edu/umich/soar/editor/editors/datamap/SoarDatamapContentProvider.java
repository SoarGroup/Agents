package edu.umich.soar.editor.editors.datamap;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SoarDatamapContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

	}

	@Override
	public Object[] getElements(Object arg0) {
		return new Object[] { "Hello", "World" };
	}

	@Override
	public Object getParent(Object arg0) {
		return null;
	}

	@Override
	public boolean hasChildren(Object arg0) {
		return true;
	}
	
	@Override
	public Object[] getChildren(Object arg0) {
		return new Object[] { "One", "Two" };
	}

}
