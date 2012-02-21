package edu.umich.soar.editor.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class DatamapFileWizard extends Wizard implements INewWizard {

    private IStructuredSelection selection;
    private DatamapFileWizardPage page;
    private IWorkbench workbench;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("New Datamap File");
        this.workbench = workbench;
        this.selection = selection;
	}
	
    @Override
    public void addPages() {
        page = new DatamapFileWizardPage(selection);
        addPage(page);
    }

	@Override
	public boolean performFinish() {
		IFile file = page.createNewFile();
        if (file != null)
            return true;
        else
            return false;
    }

}
