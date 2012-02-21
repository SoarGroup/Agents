package edu.umich.soar.editor.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import edu.umich.soar.editor.editors.datamap.Datamap;
import edu.umich.soar.editor.editors.datamap.DatamapNode;

public class ExplodeDatamapAction implements IObjectActionDelegate
{
    
    private IFile file;

    @Override
    public void run(IAction action)
    {
        if (file == null)
        {
            showErrorDialog("No file selected.");
            return;
        }
        
        String name = file.getName();
        IPath parent = file.getFullPath().removeLastSegments(1);
        
        int dot = name.indexOf('.');
        if (dot != -1)
        {
            name = name.substring(0, dot);
        }
        Datamap datamap = Datamap.read(file);
        if (datamap == null)
        {
            showErrorDialog("Bad datamap file.");
            return;
        }
        for (String stateName : datamap.getStateNames())
        {
            DatamapNode stateNode = datamap.getStateNode(stateName);
            String newFilename = /* name + "_" + */ stateName + ".dm";
            IPath newPath = new Path(parent.toOSString() + File.separator + newFilename);
            IFile newFile = ResourcesPlugin.getWorkspace().getRoot().getFile(newPath);
            if (newFile.exists())
            {
                // TODO show warning / confirmation dialog
            }
            stateNode.writeProblemSpaceToFile(newFile);
        }
    }

    private void showErrorDialog(String string)
    {
        // TODO Show error dialog
        
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection)
    {
        if (!(selection instanceof StructuredSelection))
        {
            return;
        }
        
        StructuredSelection ss = (StructuredSelection) selection;
        Object obj = ss.getFirstElement();
        
        if (!(obj instanceof IFile))
        {
            return;
        }
        
        file = (IFile) obj;
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart part)
    {
    }

}
