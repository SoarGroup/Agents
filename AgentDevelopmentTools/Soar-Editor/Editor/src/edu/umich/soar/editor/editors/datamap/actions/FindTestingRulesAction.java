package edu.umich.soar.editor.editors.datamap.actions;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.action.Action;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import com.soartech.soar.ide.core.ast.SoarProductionAst;

import edu.umich.soar.editor.editors.SoarRuleParser;
import edu.umich.soar.editor.editors.SoarRuleParser.SoarParseError;
import edu.umich.soar.editor.editors.datamap.Datamap;
import edu.umich.soar.editor.editors.datamap.DatamapAttribute;
import edu.umich.soar.editor.editors.datamap.DatamapNode;
import edu.umich.soar.editor.editors.datamap.DatamapNode.NodeType;
import edu.umich.soar.editor.editors.datamap.DatamapUtil;
import edu.umich.soar.editor.editors.datamap.TerminalPath;
import edu.umich.soar.editor.editors.datamap.Triple;
import edu.umich.soar.editor.editors.datamap.TripleExtractor;
import edu.umich.soar.editor.editors.datamap.actions.DatamapSearchResultSet.ResultItem;
import edu.umich.soar.editor.search.SoarSearchResultsView;

public class FindTestingRulesAction extends Action implements ISearchQuery
{
    private DatamapAttribute attribute;
    private boolean test;
    private boolean create;

    public FindTestingRulesAction(DatamapAttribute attribute, boolean test, boolean create)
    {
        super("Find rules that" + (test && !create ? " test " : test && create ? " test or create " : !test && create ? " create " : " ??? ")
                + "this attribute");
        this.attribute = attribute;
        this.test = test;
        this.create = create;
    }

    @Override
    public void run()
    {
        Datamap datamap = attribute.datamap;
        List<Object> attributePathList = attribute.getPathList();
        File datamapFile = datamap.getFile();
        IFile datamapIFile = datamap.getIFile();
        File datamapDir = null;
        IContainer datamapIDir = null;
        try
        {
            datamapDir = datamapFile.getCanonicalFile().getParentFile();
            datamapIDir = datamapIFile.getParent();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            return;
        }
        File[] datamapDirFiles = datamapDir.listFiles();
        IResource[] datamapIDirFiles;
        try
        {
            datamapIDirFiles = datamapIDir.members();
        }
        catch (CoreException e1)
        {
            e1.printStackTrace();
            return;
        }

        List<ResultItem> results = new ArrayList<ResultItem>();

        while (datamapIDirFiles.length > 0)
        {
            List<IResource> nextList = new ArrayList<IResource>();
            for (IResource resource : datamapIDirFiles)
            {
                if (resource instanceof IFile)
                {
                    IFile file = (IFile) resource;
                    if (file.getName().endsWith(".soar"))
                    {
                        // Check this rules file for this attribute
                        StringBuilder sb = new StringBuilder();
                        Scanner scanner;
                        try
                        {
                            scanner = new Scanner(file.getContents());
                        }
                        catch (CoreException e)
                        {
                            e.printStackTrace();
                            continue;
                        }
                        while (scanner.hasNextLine())
                        {
                            sb.append(scanner.nextLine() + '\n');
                        }
                        String text = sb.toString();
                        List<SoarParseError> errors = new ArrayList<SoarParseError>();
                        List<SoarProductionAst> asts = new ArrayList<SoarProductionAst>();
                        IContainer container = file.getParent();
                        IPath path = container.getLocation();
                        IPath absolutePath = path.makeAbsolute();
                        URI uri = container.getRawLocationURI();
                        IPath absolute = path.makeAbsolute();
                        String osstring = absolute.toOSString();
                        SoarRuleParser.parseRules(text, null, errors, asts, file.getParent().getLocation().makeAbsolute().toOSString(), false);
                        for (SoarProductionAst ast : asts)
                        {
                            System.out.println("ast " + ast.getName() + ", " + ast.getRuleOffset());
                            ArrayList<String> stateVariables = new ArrayList<String>();
                            List<Triple> triples = TripleExtractor.makeTriples(ast, stateVariables, create);

                            // For full path matching:
                            // ResultItem result = pathMatchesTriples(triples,
                            // attributePathList, file, ast, test, create);

                            // For more lenient matching:
                            List<ResultItem> theseResults = attributeMatchesTriples(triples, ast, attribute, file, test, create);
                            results.addAll(theseResults);
                        }
                    }
                }
                else if (resource instanceof IFolder)
                {
                    try
                    {
                        for (IResource grandchild : ((IFolder) resource).members())
                        {
                            nextList.add(grandchild);
                        }
                    }
                    catch (CoreException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            datamapIDirFiles = nextList.toArray(new IResource[0]);
        }

        // TODO: display results, somehow
        /*
         * NewSearchUI.activateSearchResultView(); ISearchResultViewPart
         * resultsView = NewSearchUI.getSearchResultView(); if (resultsView !=
         * null) { ISearchResultPage page = resultsView.getActivePage();
         * page.setInput(results, null); }
         */
        SoarSearchResultsView.setResults(results.toArray());
    }

    /**
     * 
     * @param triples
     *            Triples extracted from an ast of a production
     * @param attributePathList
     *            The path from the attribute we're searching for, back to the
     *            state node
     * @param file
     *            The file that the production came from
     * @param test
     *            If we're searching for rules that test this attribute
     * @param create
     *            If we're searching for tules that create this attribute
     * @return If this ast matches the attribute, a result item for that match;
     *         otherwise null
     */
    private ResultItem pathMatchesTriples(List<Triple> triples, List<Object> attributePathList, IFile file, SoarProductionAst ast, boolean test, boolean create)
    {
        if (attributePathList.size() == 0)
        {
            // Shouldn't happen
            return null;
        }

        if (attributePathList.get(attributePathList.size() - 1) == null)
        {
            // Attribute path was too long
            return null;
        }

        ArrayList<TerminalPath> paths = DatamapUtil.terminalPathsForTriples(triples);

        for (TerminalPath terminalPath : paths)
        {
            ResultItem result = pathMatchesTerminalPath(attributePathList, terminalPath, file, ast, test, create);
            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

    /**
     * 
     * @param triples
     * @param attribute
     * @param file
     * @return
     */
    private List<ResultItem> attributeMatchesTriples(List<Triple> triples, SoarProductionAst ast, DatamapAttribute attribute, IFile file, boolean test,
            boolean create)
    {
        List<ResultItem> ret = new ArrayList<ResultItem>();
        if (attribute.name.equals("operator"))
        {
            Collection<DatamapNode> nameNodes = attribute.getTarget().getChildren("name", NodeType.ENUMERATION);
            HashSet<String> datamapNames = new HashSet<String>();
            for (DatamapNode nameNode : nameNodes)
            {
                assert (nameNode.type == NodeType.ENUMERATION);
                for (String nameValue : nameNode.values)
                {
                    datamapNames.add(nameValue);
                }
            }
            HashMap<String, Triple> operatorNodeNames = new HashMap<String, Triple>();
            for (Triple triple : triples)
            {
                if (triple.attribute.equals("operator") && triple.valueIsVariable())
                {
                    operatorNodeNames.put(triple.value, triple);
                }
            }
            for (Triple triple : triples)
            {
                if ((triple.rhs && create) || (!triple.rhs && test))
                {
                    if (operatorNodeNames.containsKey(triple.variable) && triple.attribute.equals("name") && triple.valueIsConstant()
                            && datamapNames.contains(triple.value))
                    {
                        Triple operatorTriple = operatorNodeNames.get(triple.variable);
                        ResultItem result = new ResultItem(file, ast.getRuleOffset() + operatorTriple.attributeOffset, operatorTriple.attribute.length());
                        ret.add(result);
                    }
                }
            }
        }
        else
        {
            for (Triple triple : triples)
            {
                if ((triple.rhs && create) || (!triple.rhs && test))
                {
                    if (triple.attributeIsConstant() && triple.attribute.equals(attribute.name))
                    {
                        ResultItem result = new ResultItem(file, ast.getRuleOffset() + triple.attributeOffset, triple.attribute.length());
                        ret.add(result);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 
     * @param attributePathList
     *            The path from the attribute we're searching for, back to the
     *            state node
     * @param terminalPath
     *            A terminal path found from the triples from the current ast
     *            from the current rule
     * @param file
     *            The file that contains the current rule
     * @param test
     *            If we're searching for rules that test this attribute
     * @param create
     *            If we're searching for rules that create this attribute
     * @return If this terminal path contains the attribute that we're looking
     *         for, return a result item indicating that match; otherwise,
     *         return null
     */
    private ResultItem pathMatchesTerminalPath(List<Object> attributePathList, TerminalPath terminalPath, IFile file, SoarProductionAst ast, boolean test,
            boolean create)
    {
        int pathSize = attributePathList.size();
        if (pathSize == 0 || attributePathList.get(0) == null)
        {
            return null;
        }
        for (int i = 0; i < pathSize - 1; ++i) // -1 to skip past state node /
                                               // null node
        {
            if (terminalPath.path.size() <= i)
            {
                return null;
            }
            Triple terminalTriple = terminalPath.path.get(i);
            Object attributePathObject = attributePathList.get(pathSize - 2 - i); // -1
                                                                                  // for
                                                                                  // last
                                                                                  // element,
                                                                                  // -1
                                                                                  // to
                                                                                  // skip
                                                                                  // past
                                                                                  // state
                                                                                  // node
                                                                                  // /
                                                                                  // null
                                                                                  // node
            if (!(attributePathObject instanceof DatamapAttribute))
            {
                return null;
            }
            DatamapAttribute attribute = (DatamapAttribute) attributePathObject;
            if (!attribute.name.equals(terminalTriple.attribute))
            {
                return null;
            }
        }
        Triple matchingTriple = terminalPath.path.get(pathSize - 2);
        if ((test && terminalPath.hasConditionSide()) || (create && terminalPath.hasActionSide()))
        {
            return new ResultItem(file, ast.getRuleOffset() + matchingTriple.attributeOffset, matchingTriple.attribute.length());
        }
        return null;
    }

    @Override
    public boolean canRerun()
    {
        return false;
    }

    @Override
    public boolean canRunInBackground()
    {
        return false;
    }

    @Override
    public String getLabel()
    {
        return "Search thing";
    }

    @Override
    public ISearchResult getSearchResult()
    {
        return null;
    }

    @Override
    public IStatus run(IProgressMonitor arg0) throws OperationCanceledException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
