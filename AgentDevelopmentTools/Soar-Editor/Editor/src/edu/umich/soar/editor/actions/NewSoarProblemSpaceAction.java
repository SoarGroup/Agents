package edu.umich.soar.editor.actions;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import edu.umich.soar.editor.wizards.SoarProblemSpaceWizard;

public class NewSoarProblemSpaceAction implements IObjectActionDelegate
{
    StructuredSelection ss;
    String defaultName;

    @Override
    public void run(IAction action)
    {
        if (ss == null)
        {
            return;
        }
        SoarProblemSpaceWizard wizard = new SoarProblemSpaceWizard();
        wizard.setDefaultName(defaultName);
        IWorkbench workbench = PlatformUI.getWorkbench();
        wizard.init(workbench, ss);
        Shell shell = workbench.getActiveWorkbenchWindow().getShell();
        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.open();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection)
    {
        defaultName = null;
        if (!(selection instanceof StructuredSelection))
        {
            return;
        }
        ss = (StructuredSelection) selection;
        Object s = ss.getFirstElement();
        if (s instanceof IContainer)
        {
            return;
        }
        if (s instanceof IFile)
        {
            IFile file = (IFile) s;
            IContainer parent = file.getParent();
            ss = new StructuredSelection(parent);
            defaultName = file.getName().split(".")[0];
            return;
        }
        ss = null;
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        System.out.println("setActivePart");
    }

}
