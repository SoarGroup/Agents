package edu.umich.soar.editor.wizards;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class SoarProblemSpaceWizard extends Wizard implements INewWizard
{

    private IStructuredSelection selection;
    private SoarProblemSpaceWizardPage page;
    // private SoarFileTemplatesWizardPage templatesPage;
    private IWorkbench workbench;

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        setWindowTitle("New Soar Problem Space");
        this.workbench = workbench;
        this.selection = selection;
    }

    @Override
    public void addPages()
    {
        page = new SoarProblemSpaceWizardPage(selection, this);
        // page.set
        addPage(page);
    }

    @Override
    public boolean performFinish()
    {
        IFolder folder = page.createNewFolder();
        if (folder == null) return false;

        // Add source.soar to the problem space.
        IFile sourceFile = folder.getFile(folder.getName() + "_source.soar");
        try
        {
            sourceFile.create(new ByteArrayInputStream("".getBytes()), false, null);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
            return false;
        }

        // Add this problem space to parent folder's source.soar, if it exists.
        // If it doesn't exits, create it.
        IContainer parent = folder.getParent();
        if (parent != null)
        {
            IFile parentSourceFile = parent.getFile(new Path(parent.getName() + "_source.soar"));
            if (parentSourceFile != null)
            {
                String newlines = "\n\n";
                if (!parentSourceFile.exists())
                {
                    try
                    {
                        parentSourceFile.create(new ByteArrayInputStream("".getBytes()), false, null);
                        newlines = "";
                    }
                    catch (CoreException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (parentSourceFile.exists())
                {
                    String folderName = folder.getName();
                    String append = newlines + "# Source child problem space \"" + folderName + "\"\npushd " + folder.getName() + "\nsource " + folderName + "_source.soar\npopd";
                    try
                    {
                        parentSourceFile.appendContents(new ByteArrayInputStream(append.getBytes()), false, true, null);
                    }
                    catch (CoreException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        return true;
    }

    public void setDefaultName(String name)
    {
        if (name == null)
        {
            return;
        }
    }

    /*
     * public String getFileName() { // TODO Auto-generated method stub return
     * page.getFileName(); }
     */

    /*
     * public String getFolderName() { return
     * page.getContainerFullPath().lastSegment(); }
     */

    /*
     * public String getTemplatesString() { return templatesPage.getTemplates();
     * }
     */
}
