package edu.umich.soar.editor.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import edu.umich.soar.editor.editors.SoarEditor;
import edu.umich.soar.editor.editors.datamap.actions.DatamapSearchResultSet.ResultItem;

public class SoarSearchResultsView extends ViewPart implements IDoubleClickListener
{

    public static final String ID = "edu.umich.soar.editor.search.SoarSearchResultsView";

    TableViewer table;

    @Override
    public void createPartControl(Composite parent)
    {
        table = new TableViewer(parent);
        table.addDoubleClickListener(this);
        table.setContentProvider(new ArrayContentProvider());
        table.setLabelProvider(new LabelProvider());
        // table.setLabelProvider(SoarLabelProvider.createFullLabelProvider());
        table.setInput(null);
    }

    @Override
    public void setFocus()
    {

    }

    public void setSearchResults(Object[] results)
    {
        table.setInput(results);
    }

    public static void setResults(Object[] results)
    {
        try
        {
            SoarSearchResultsView view = null;
            if (results.length == 0)
            {
                view = (SoarSearchResultsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ID);
            }
            else
            {
                view = (SoarSearchResultsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
            }
            if (view == null)
            {
                view = (SoarSearchResultsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
            }
            if (view != null)
            {
                view.setSearchResults(results);
            }
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void doubleClick(DoubleClickEvent event)
    {
        ISelection selection = event.getSelection();
        if (!(selection instanceof StructuredSelection)) return;
        StructuredSelection ss = (StructuredSelection) selection;
        Object element = ss.getFirstElement();
        if (!(element instanceof ResultItem)) return;
        ResultItem item = (ResultItem) element;
        IFile file = item.file;
        try
        {
            IEditorPart part = IDE.openEditor(getSite().getPage(), file);
            if (part != null && part instanceof SoarEditor)
            {
                SoarEditor editor = (SoarEditor) part;
                int begin = item.getOffset() - 1; // -1 to convert 1-indexed value
                int length = item.getLength();
                editor.resetHighlightRange();
                editor.setHighlightRange(begin, length, true);
            }
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
    }
}
