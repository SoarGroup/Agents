package edu.umich.soar.editor.editors.datamap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import edu.umich.soar.editor.editors.datamap.Datamap.DatamapChangedListener;

public class DatamapEditor extends MultiPageEditorPart implements DatamapChangedListener
{
    public static final String ID = "edu.umich.soar.editor.editors.datamap.DatamapEditor";

    List<DatamapTreeEditor> treeEditors;
    List<TreePath[]> treePaths;
    Datamap datamap;

    public DatamapEditor()
    {
        super();
    }

    @Override
    protected void createPages()
    {
        treeEditors = new ArrayList<DatamapTreeEditor>();
        datamap = Datamap.read(((FileEditorInput) getEditorInput()).getFile());
        datamap.addDatamapChangedListener(this);
        initPages();
    }

    @Override
    public void doSave(IProgressMonitor monitor)
    {
        IEditorInput input = getEditorInput();
        IFile file = ((FileEditorInput) input).getFile();
        boolean wrote = datamap.writeToFile(file, monitor);
        if (wrote)
        {
            for (int i = 0; i < getPageCount() - 1; ++i)
            {
                IEditorPart part = getEditor(i);
                ((DatamapTreeEditor) part).setDirty(false);
            }
        }
    }

    @Override
    public void doSaveAs()
    {
        doSave(null);
    }

    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        super.init(site, input);
        if (!(input instanceof FileEditorInput))
        {
            throw new PartInitException("Editor input not instanceof FileEditorInput.");
        }
        setTitle(input.getName());
    }

    public void setDatamap(Datamap datamap)
    {
        int activePage = getActivePage();
        this.datamap = datamap;

        // Save expanded tree paths
        treePaths = new ArrayList<TreePath[]>();
        for (DatamapTreeEditor treeEditor : treeEditors)
        {
            treePaths.add(treeEditor.getTree().getExpandedTreePaths());
        }
        treeEditors = new ArrayList<DatamapTreeEditor>();
        while (getPageCount() > 0)
        {
            removePage(0);
        }
        initPages();
        setActivePage(activePage == -1 ? 0 : activePage);

        // re-expand trees
        for (int i = 0; i < treeEditors.size() && i < treePaths.size(); ++i)
        {
            final TreePath[] paths = treePaths.get(i);
            final TreeViewer viewer = treeEditors.get(i).getTree();
            Display.getDefault().asyncExec(new Runnable()
            {

                @Override
                public void run()
                {
                    try
                    {
                        viewer.refresh();
                        viewer.setExpandedTreePaths(paths);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void initPages()
    {
        try
        {
            List<DatamapNode> stateNodes = datamap.getStateNodes();
            int i = 0;
            for (DatamapNode stateNode : stateNodes)
            {
                DatamapTreeEditor treeEditor = new DatamapTreeEditor(datamap, stateNode);
                addPage(treeEditor, getEditorInput());
                setPageText(i++, stateNode.tabName());
                treeEditors.add(treeEditor);
            }
            addPage(new TextEditor(), getEditorInput());
            setPageText(getPageCount() - 1, "Raw Text");
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
    }

    public Datamap getDatamap()
    {
        return datamap;
    }

    @Override
    public boolean onDatamapChanged(Datamap datamap, Object changed)
    {
        for (int i = 0; i < getPageCount(); ++i)
        {
            try
            {
                IEditorPart part = getEditor(i);
                if (part instanceof DatamapTreeEditor)
                {
                    ((DatamapTreeEditor) part).contentChanged(changed);
                }
            }
            catch (SWTException e)
            {
                return false;
            }
        }
        return true;
    }
}
