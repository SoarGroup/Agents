package edu.umich.soar.editor.editors;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.soartech.soar.ide.core.ast.SoarProductionAst;

import edu.umich.soar.editor.editors.SoarRuleParser.SoarParseError;
import edu.umich.soar.editor.editors.datamap.Correction;
import edu.umich.soar.editor.editors.datamap.Datamap;
import edu.umich.soar.editor.editors.datamap.DatamapUtil;
import edu.umich.soar.editor.editors.datamap.Triple;
import edu.umich.soar.editor.editors.datamap.TripleExtractor;

public class SoarEditorUtil
{
    
    public static final String NEWLINE = System.getProperty("line.separator");
    
    public static boolean findProblems(IResource resource)
    {
        return findProblems(resource, null, null);
    }

    public static boolean findProblems(IResource resource, SoarEditor editor, IProgressMonitor monitor)
    {
        // First, remove all corrections and error markers from the resource. 
        removeErrorsAndCorrections(resource, editor);
        
        // Read the contents of the resource.
        String text = readResourceContents(resource);
        if (text == null) return false;
        
        // Get abstract syntax trees for all the rules in the resource.
        IContainer parentContainer = resource.getParent();
        String basePath = parentContainer.getLocation().toOSString();
        List<SoarParseError> errors = new ArrayList<SoarParseError>();
        List<SoarProductionAst> asts = new ArrayList<SoarProductionAst>();
        SoarRuleParser.parseRules(text, monitor, errors, asts, basePath, true);
        
        // Add any errors found to the resource.
        SoarEditor.addErrors(resource, errors);
        
        // Find the datamaps related to the resource.
        List<Datamap> datamaps = SoarEditor.staticGetDatamaps(resource, editor);
        
        // For each rule, check it for consistency against the datamaps.
        ArrayList<String> stateVariables = new ArrayList<String>();
        for (SoarProductionAst ast : asts)
        {
            // System.out.println("ast " + ast.getName() + ", " + ast.getRuleOffset());
            stateVariables.clear();
            List<Triple> triples = TripleExtractor.makeTriples(ast, stateVariables);
            List<Correction> corrections = null;
            boolean first = true;

            if (stateVariables.size() == 0)
            {
                SoarEditor.addError(resource, new SoarParseError("No state variables found in rule " + ast.getName(), ast.getRuleOffset(), 0));
            }

            String folderName = getProblemSpaceParent(resource).getName();
            for (Datamap datamap : datamaps)
            {
                if (first)
                {
                    first = false;
                    corrections = DatamapUtil.getCorrections(triples, datamap, stateVariables, folderName);
                }
                else
                {
                    List<Correction> newCorrections = DatamapUtil.getCorrections(triples, datamap, stateVariables, folderName);
                    List<Correction> toRemove = new ArrayList<Correction>();
                    for (Correction correction : corrections)
                    {
                        if (!newCorrections.contains(correction))
                        {
                            toRemove.add(correction);
                        }
                    }
                    corrections.removeAll(toRemove);
                }
            }
            if (corrections != null && editor != null)
            {
                editor.addCorrections(resource, corrections, ast);
            }
        }
        return true;
    }
    
    private static void removeErrorsAndCorrections(IResource resource, SoarEditor editor)
    {
        try
        {
            if (editor != null)
            {           
                IMarker[] markers = resource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
                for (IMarker marker : markers)
                {
                    editor.removeCorrection(marker);
                }
            }
        }
        catch (CoreException e2)
        {
            e2.printStackTrace();
        }
        try
        {
            resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
        }
        catch (CoreException e1)
        {
            e1.printStackTrace();
        }
    }
    
    private static String readResourceContents(IResource resource)
    {
        if (!(resource instanceof IFile))
        {
            return null;
        }
        IFile fileResource = (IFile) resource;
        StringBuilder sb = new StringBuilder();
        try
        {
            InputStream is = fileResource.getContents();
            Scanner scanner = new Scanner(is);
            while (scanner.hasNext())
            {
                sb.append(scanner.nextLine() + NEWLINE);
            }
            scanner.close();
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        String text = sb.toString();
        return text;
    }
    
    /**
     * Gets the parent container of this IResource,
     * going up one additional level if the first parent is 
     * named "elaborations".
     * @param resource
     * @return
     */
    public static IContainer getProblemSpaceParent(IResource resource)
    {
        IContainer parent = resource.getParent();
        if (parent == null) return null;
        String name = parent.getName();
        if (name.equals("elaborations"))
        {
            parent = parent.getParent();
            if (parent == null)
            {
                return null;
            }
        }
        return parent;
    }
}
