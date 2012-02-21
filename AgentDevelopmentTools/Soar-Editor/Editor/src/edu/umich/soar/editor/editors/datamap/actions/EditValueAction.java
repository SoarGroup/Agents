package edu.umich.soar.editor.editors.datamap.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import edu.umich.soar.editor.editors.datamap.DatamapNode;

public class EditValueAction extends Action {

	private String name;
	private DatamapNode node;
	private boolean integer;
	private boolean min;
	
	public EditValueAction(String name, DatamapNode node, boolean integer, boolean min)
	{
		super(name);
		this.name = name;
		this.node = node;
		this.integer = integer;
		this.min = min;
	}
	
	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		String title = "Edit " + name;
		String message = "New " + name;
		String initialValue = (integer && min) ? "" + node.intMin :
			integer ? "" + node.intMax :
				(integer && min) ? "" + node.floatMin :
					"" + node.floatMax;
		InputDialog dialog = new InputDialog(shell, title, message, initialValue, null);
		dialog.open();
		String result = dialog.getValue();
		if (result != null && result.length() > 0) {
			if (integer)
			{
				try
				{
					Integer value = Integer.valueOf(result);
					if (min)
					{
						node.intMin = value;
					}
					else
					{
						node.intMax = value;
					}
					node.datamap.contentChanged(node);
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				try
				{
					Double value = Double.valueOf(result);
					if (min)
					{
						node.floatMin = value;
					}
					else
					{
						node.floatMax = value;
					}
					node.datamap.contentChanged(node);
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
