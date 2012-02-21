package edu.umich.soar.editor.editors.datamap.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import edu.umich.soar.editor.editors.datamap.DatamapNode;

public class EditEnumValuesAction extends Action
{

    private class EnumInputDialog extends InputDialog
    {
        public EnumInputDialog(Shell shell, String title, String message, String initialValue)
        {
            super(shell, title, message, initialValue, null);
        }

        /**
         * Override this method to make the text field multilined and give it a
         * scroll bar. But...
         */
        @Override
        protected int getInputTextStyle()
        {
            return SWT.MULTI | SWT.BORDER | SWT.V_SCROLL;
        }

        /**
         * ...it still is just one line high. This hack is not very nice, but at
         * least it gets the job done... ;o)
         */
        @Override
        protected Control createDialogArea(Composite parent)
        {
            Control res = super.createDialogArea(parent);
            ((GridData) this.getText().getLayoutData()).heightHint = 100;
            return res;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent)
        {
            super.createButtonsForButtonBar(parent);
            String value = getValue();
            getText().setSelection(value.length());
        }
    }

    private DatamapNode node;

    public EditEnumValuesAction(DatamapNode node)
    {
        super("Edit enum values");
        this.node = node;
    }

    @Override
    public void run()
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        String title = "Edit enum values";
        String message = "Enter enum values, one per line";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < node.values.size(); ++i)
        {
            sb.append(node.values.get(i));
            if (i + 1 < node.values.size())
            {
                sb.append('\n');
            }
        }

        final String initialValue = sb.toString();

        // from http://www.eclipse.org/forums/index.php/m/650641/
        EnumInputDialog dialog = new EnumInputDialog(shell, title, message, initialValue);
        dialog.open();
        String result = dialog.getValue();
        if (result != null && result.length() > 0)
        {
            String[] lines = result.split("\\n");
            node.values.clear();
            for (String line : lines)
            {
                line = line.trim();
                if (line.length() > 0)
                {
                    node.values.add(line);
                }
            }
            node.datamap.contentChanged(node);
        }
    }
}
