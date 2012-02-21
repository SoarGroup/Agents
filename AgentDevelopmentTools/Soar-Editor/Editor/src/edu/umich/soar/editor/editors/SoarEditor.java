package edu.umich.soar.editor.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

import com.soartech.soar.ide.core.ast.SoarProductionAst;

import edu.umich.soar.editor.editors.SoarRuleParser.SoarParseError;
import edu.umich.soar.editor.editors.actions.FormatAction;
import edu.umich.soar.editor.editors.actions.SoarEditorActionStrings;
import edu.umich.soar.editor.editors.actions.ToggleCommentAction;
import edu.umich.soar.editor.editors.datamap.Correction;
import edu.umich.soar.editor.editors.datamap.Datamap;
import edu.umich.soar.editor.editors.datamap.Datamap.DatamapChangedListener;

public class SoarEditor extends TextEditor implements DatamapChangedListener
{

    public static final String ID = "edu.umich.soar.editor.editors.SoarEditor";

    private Map<String, Correction> correctionMap = new HashMap<String, Correction>();

    private ColorManager colorManager;
    private List<IFile> datamapFiles;
    private List<Datamap> datamaps;
    String folderName;
    String parentFolderName;

    public SoarEditor()
    {
        super();
        colorManager = new ColorManager();
        setSourceViewerConfiguration(new SoarConfiguration(colorManager, this));
        setDocumentProvider(new SoarDocumentProvider());
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        super.init(site, input);
        findDatamaps(input);
        FileEditorInput fileInput = (FileEditorInput) input;
        IFile file = fileInput.getFile();
        findStateNames(file);
        SoarEditorUtil.findProblems(file, this, getProgressMonitor());
    }
    
    @Override
    protected void createActions()
    {
        super.createActions();
        getSite().getKeyBindingService().setScopes(new String[] { SoarEditorActionStrings.SOAR_SCOPE_ID });
        ListResourceBundle bundle = new ListResourceBundle()
        {

            @Override
            protected Object[][] getContents()
            {
                return new Object[][] {};
            }
            
        };
        ToggleCommentAction action = new ToggleCommentAction(bundle, "ToggleComment", this);
        action.setActionDefinitionId(SoarEditorActionStrings.TOGGLE_COMMENT);
        setAction("ToggleComment", action);
        markAsStateDependentAction("ToggleComment", true);
        action.configure(getSourceViewer(), getSourceViewerConfiguration());
        
        FormatAction format = new FormatAction(bundle, "Format", this);
        action.setActionDefinitionId(SoarEditorActionStrings.FORMAT);
        setAction("Format", format);
        markAsStateDependentAction("Format", true);
        action.configure(getSourceViewer(), getSourceViewerConfiguration());
    }

    private void findStateNames(IFile file)
    {
        // Find folder name
        // and parent folder name
        IContainer folder = SoarEditorUtil.getProblemSpaceParent(file);
        if (folder == null) return;
        folderName = folder.getName();

        IContainer parent = SoarEditorUtil.getProblemSpaceParent(folder);
        if (parent == null) return;
        parentFolderName = parent.getName();
    }
   

    public List<Datamap> getDatamaps()
    {
        datamapFiles = findDatamaps(getEditorInput());
        datamaps = buildDatamaps(datamapFiles, this);
        return datamaps;
    }
    
    public static List<Datamap> staticGetDatamaps(IResource input, SoarEditor editor)
    {
        if (editor != null) return editor.getDatamaps();
        List<IFile> datamapFiles = findDatamaps(input);
        List<Datamap> datamaps = buildDatamaps(datamapFiles, editor);
        return datamaps;
    }

    private static List<Datamap> buildDatamaps(List<IFile> datamapFiles, SoarEditor editor)
    {
        List<Datamap> ret = new ArrayList<Datamap>();
        for (IFile file : datamapFiles)
        {
            if (file.getName().equals("comment.dm"))
            {
                continue;
            }

            // Datamap datamap = null;
            /*
             * IWorkbenchPage page =
             * PlatformUI.getWorkbench().getActiveWorkbenchWindow
             * ().getActivePage(); IEditorPart part = page.findEditor(new
             * FileEditorInput(file)); if (part != null && part instanceof
             * DatamapEditor) { DatamapEditor editor = (DatamapEditor) part;
             * datamap = editor.getDatamap(); }
             */

            // if (datamap == null)
            // {
            Datamap datamap = Datamap.read(file);
            // }
            if (datamap != null)
            {
                ret.add(datamap);
                if (editor != null)
                {
                    datamap.addDatamapChangedListener(editor);
                }
            }
        }
        return ret;
    }
    
    /**
     * Find a list of datamap files corresponding to the given IEditorInput.
     * @param input
     * @return
     */
    private static ArrayList<IFile> findDatamaps(IEditorInput input)
    {
        if (!(input instanceof FileEditorInput))
        {
            return new ArrayList<IFile>();
        }
        
        IFile file = ((FileEditorInput) input).getFile();
        return findDatamaps(file);
    }

    /**
     * Find a list of datamap files corresponding to the given IResource.
     * @param input
     * @return
     */
    private static ArrayList<IFile> findDatamaps(IResource input)
    {
        ArrayList<IFile> ret = new ArrayList<IFile>();
        IContainer parent = input.getParent();
        if (parent == null) return ret;
        try
        {
            for (IResource member : parent.members())
            {
                if (member instanceof IFile && member.getFileExtension().equalsIgnoreCase("dm"))
                {
                    ret.add((IFile) member);
                }
            }
        }
        catch (CoreException e)
        {
            e.printStackTrace();
            return ret;
        }
        if (ret.size() != 0) return ret;
        parent = parent.getParent();
        if (parent == null) return ret;
        try
        {
            for (IResource member : parent.members())
            {
                if (member instanceof IFile)
                {
                    String extension = member.getFileExtension();
                    if (extension != null && extension.equalsIgnoreCase("dm")) {
                        ret.add((IFile) member);
                    }
                }
            }
        }
        catch (CoreException e)
        {
            e.printStackTrace();
            return ret;
        }
        return ret;
    }

    public void dispose()
    {
        colorManager.dispose();
        super.dispose();
    }

    @Override
    public void doSave(IProgressMonitor progressMonitor)
    {
        super.doSave(progressMonitor);
        IEditorInput input = getEditorInput();
        if (input == null) return;
        if (!(input instanceof FileEditorInput)) return;
        FileEditorInput fileInput = (FileEditorInput) input;
        IFile file = fileInput.getFile();
        SoarEditorUtil.findProblems(file, this, progressMonitor);
    }


    public static void addErrors(IResource resource, List<SoarParseError> errors)
    {
        for (SoarParseError error : errors)
        {
            addError(resource, error);
        }
    }

    public static void addError(IResource resource, SoarParseError error)
    {
        System.out.println("ERROR, " + error.message + ", " + error.start);
        IMarker marker;
        try
        {
            marker = resource.createMarker(IMarker.PROBLEM);
            marker.setAttribute(IMarker.CHAR_START, error.start);
            marker.setAttribute(IMarker.CHAR_END, error.start + error.length);
            marker.setAttribute(IMarker.MESSAGE, error.message);
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

    public void addCorrections(IResource resource, List<Correction> corrections, SoarProductionAst ast)
    {
        for (Correction correction : corrections)
        {
            addCorrection(resource, correction, ast);
        }
    }

    private void addCorrection(IResource resource, Correction correction, SoarProductionAst ast)
    {
        System.out.println(correction);
        IMarker marker;
        try
        {
            marker = resource.createMarker(IMarker.PROBLEM);
            marker.setAttribute(IMarker.CHAR_START, ast.getRuleOffset() + correction.getErrorOffset() - 1); // 1-indexed
                                                                                                            // to
                                                                                                            // 0-indexed
            marker.setAttribute(IMarker.CHAR_END, ast.getRuleOffset() + correction.getErrorOffset() + correction.getErrorLength() - 1);
            marker.setAttribute(IMarker.MESSAGE, correction.toString(datamaps, folderName));
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            addCorrection(marker, correction);
            correction.node.datamap.addDatamapChangedListener(this);
            Datamap linkedDatamap = correction.node.getLinkedDatamap();
            if (linkedDatamap != null) linkedDatamap.addDatamapChangedListener(this);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

    private static String keyForMarker(IMarker marker)
    {
        return marker.getResource().toString() + "_" + marker.getId();
    }

    public Correction findCorrection(IMarker marker)
    {
        return findCorrection(keyForMarker(marker));
    }

    private void addCorrection(IMarker marker, Correction correction)
    {
        correctionMap.put(keyForMarker(marker), correction);
    }

    public void removeCorrection(IMarker marker)
    {
        correctionMap.remove(marker);
    }

    private Correction findCorrection(String key)
    {
        if (!correctionMap.containsKey(key)) return null;
        return correctionMap.get(key);
    }

    public String getFolderName()
    {
        return folderName;
    }

    public String getParentFolderName()
    {
        return parentFolderName;
    }

    public String getFileName()
    {
        IEditorInput input = getEditorInput();
        if (!(input instanceof FileEditorInput)) return null;
        FileEditorInput fileEditorInput = (FileEditorInput) input;
        return fileEditorInput.getPath().removeFileExtension().lastSegment();
    }

    @Override
    public boolean onDatamapChanged(Datamap datamap, Object changed)
    {
        FileEditorInput fileInput = (FileEditorInput) getEditorInput();
        IFile file = fileInput.getFile();
        return SoarEditorUtil.findProblems(file, this, getProgressMonitor());
    }
}
