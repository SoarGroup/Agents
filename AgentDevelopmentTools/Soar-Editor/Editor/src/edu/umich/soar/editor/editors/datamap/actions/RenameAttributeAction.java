package edu.umich.soar.editor.editors.datamap.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import edu.umich.soar.editor.editors.datamap.DatamapAttribute;

public class RenameAttributeAction extends Action {

	private DatamapAttribute attribute;
	
	public RenameAttributeAction(DatamapAttribute attribute)
	{
		super("Rename...");
		this.attribute = attribute;
	}
	
	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		String title = "Rename " + attribute.name;
		String message = "Enter Name:";
		String initialValue = attribute.name;
		InputDialog dialog = new InputDialog(shell, title, message, initialValue, null);
		dialog.open();
		String result = dialog.getValue();
		if (result != null && result.length() > 0) {
			attribute.setName(result);
		}
	}
}
