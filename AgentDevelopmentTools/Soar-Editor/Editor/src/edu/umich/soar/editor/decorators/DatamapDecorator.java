package edu.umich.soar.editor.decorators;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.umich.soar.editor.icons.SoarIcons;
import edu.umich.soar.editor.icons.SoarIcons.IconFiles;

public class DatamapDecorator implements ILabelDecorator {

	@Override
	public void addListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image decorateImage(Image arg0, Object arg1) {
		return SoarIcons.get(IconFiles.S_FLAG);
	}

	@Override
	public String decorateText(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
