package edu.umich.soar.editor.editors.datamap.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.umich.soar.editor.editors.datamap.DatamapAttribute;

public class ShowLinkedAttributesAction extends Action {

	private DatamapAttribute attr = null;
	
	public ShowLinkedAttributesAction(DatamapAttribute attr)
	{
		super("Show Linked Attributes");
		this.attr = attr;
	}
	
	@Override
	public void run() {
		List<DatamapAttribute> linkedAttributes = attr.getLinkedAttributes();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < linkedAttributes.size(); ++i)
		{
			DatamapAttribute linked = linkedAttributes.get(i);
			sb.append(linked.getPathString());
			if (i + 1 < linkedAttributes.size())
			{
				sb.append('\n');
			}
		}
		String name = attr.name;
		Shell shell = Display.getDefault().getActiveShell();
		String title = "Attributes linked to " + name;
		Image titleImage = null;
		String message = sb.toString();
		int imageType = 0;
		String[] buttons = { "OK" };
		int defaultIndex = 0;
		MessageDialog dialog = new MessageDialog(shell, title, titleImage, message, imageType, buttons, defaultIndex);
		dialog.open();
	}
}
