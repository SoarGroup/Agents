package edu.umich.soar.editor.editors.datamap.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.umich.soar.editor.editors.datamap.DatamapAttribute;

public class DeleteAttributeAction extends Action {

	private DatamapAttribute attribute;
	
	public DeleteAttributeAction(DatamapAttribute attribute)
	{
		super("Delete");
		this.attribute = attribute;
	}
	
	@Override
	public void run() {
		String name = attribute.name;
		Shell shell = Display.getDefault().getActiveShell();
		String title = "Delete " + name + "?";
		Image titleImage = null;
		String message = "Delete " + name +"?";
		int imageType = 0;
		String[] buttons = { "OK", "Cancel" };
		int defaultIndex = 0;
		MessageDialog dialog = new MessageDialog(shell, title, titleImage, message, imageType, buttons, defaultIndex);
		int result = dialog.open();
		if (result == 0)
		{
			attribute.delete();
		}
	}
}
