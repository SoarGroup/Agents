package edu.umich.soar.editor.wizards;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class SoarFileWizard extends Wizard implements INewWizard {

    private IStructuredSelection selection;
    private SoarFileWizardPage page;
    //private SoarFileTemplatesWizardPage templatesPage;
    private IWorkbench workbench;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("New Soar File");
        this.workbench = workbench;
        this.selection = selection;
	}
	
    @Override
    public void addPages() {
        page = new SoarFileWizardPage(selection, this);
        addPage(page);
        /*
        templatesPage = new SoarFileTemplatesWizardPage(this);
        addPage(templatesPage);
        */
    }

	@Override
	public boolean performFinish() {
		IFile file = page.createNewFile();
		if (file == null) return false;
		
		// Add this file to source.soar, if it exists.
		IContainer parent = file.getParent();
		if (parent != null && parent.exists())
		{
		    IFile sourceFile = parent.getFile(new Path(parent.getName() + "_source.soar"));
		    if (sourceFile != null && sourceFile.exists())
		    {
		        String filename = page.getFileName();
		        String append = "\n\n# Souce file \"" + filename + "\"\nsource " + filename;
		        try
                {
                    sourceFile.appendContents(new ByteArrayInputStream(append.getBytes()), false, true, null);
                }
                catch (CoreException e)
                {
                    e.printStackTrace();
                }
		    }
		}
		
		return true;
    }

	public String getFileName() {
		// TODO Auto-generated method stub
		return page.getFileName();
	}
	
	public String getFolderName()
	{
		return page.getContainerFullPath().lastSegment();
	}

	/*
	public String getTemplatesString() {
		return templatesPage.getTemplates();
	}
	*/
}
