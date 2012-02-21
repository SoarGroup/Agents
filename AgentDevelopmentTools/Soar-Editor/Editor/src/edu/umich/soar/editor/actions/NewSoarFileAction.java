package edu.umich.soar.editor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import edu.umich.soar.editor.wizards.DatamapFileWizard;
import edu.umich.soar.editor.wizards.SoarFileWizard;

public class NewSoarFileAction implements IObjectActionDelegate
{
    StructuredSelection ss;

    @Override
    public void run(IAction action)
    {
        if (ss == null)
        {
            return;
        }
        SoarFileWizard wizard = new SoarFileWizard();
        IWorkbench workbench = PlatformUI.getWorkbench();
        wizard.init(workbench, ss);
        Shell shell = workbench.getActiveWorkbenchWindow().getShell();
        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.open();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection)
    {
        System.out.println("selectionChanged");
        if (!(selection instanceof StructuredSelection))
        {
            return;
        }
        ss = (StructuredSelection) selection;
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        System.out.println("setActivePart");
    }

}
