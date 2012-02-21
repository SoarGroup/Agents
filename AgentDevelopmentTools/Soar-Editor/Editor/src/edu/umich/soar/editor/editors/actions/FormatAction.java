package edu.umich.soar.editor.editors.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ResourceAction;
import org.eclipse.ui.texteditor.TextEditorAction;

import edu.umich.soar.editor.Activator;

/**
 * An action which toggles comment prefixes on the selected lines. see
 * org.eclipse.jdt.internal.ui.javaeditor.ToggleCommentAction
 */
public class FormatAction extends TextEditorAction
{

    ITextOperationTarget target;
    private Map<String, String[]> prefixesMap;

    /**
     * Constructor for a <code>FormatAction</code> object.
     * 
     * @param bundle
     *            resource bundle
     * @param prefix
     *            a prefix to be prepended to the various resource keys
     *            (described in <code>ResourceAction</code> constructor), or
     *            <code>null</code> if none
     * @param editor
     *            the text editor
     * @see ResourceAction#ResourceAction(ResourceBundle, String, int)
     */
    public FormatAction(ResourceBundle bundle, String prefix, ITextEditor editor)
    {
        super(bundle, prefix, editor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.TextEditorAction#update()
     */
    @Override
    public void update()
    {
        super.update();

        if (!canModifyEditor())
        {
            setEnabled(false);
            return;
        }

        ITextEditor editor = getTextEditor();
        if (target == null && editor != null)
        {
            target = (ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
        }

        boolean enabled = (target != null && target.canDoOperation(ITextOperationTarget.PREFIX) && target.canDoOperation(ITextOperationTarget.STRIP_PREFIX));

        setEnabled(enabled);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run()
    {

        if (target == null || prefixesMap == null)
        {
            return;
        }

        ITextEditor editor = getTextEditor();
        if (editor == null)
        {
            return;
        }

        if (!validateEditorInputState())
        {
            return;
        }

        final int operationCode;
        if (isSelectionCommented(editor.getSelectionProvider().getSelection()))
        {
            operationCode = ITextOperationTarget.STRIP_PREFIX;
        }
        else
        {
            operationCode = ITextOperationTarget.PREFIX;
        }

        final Shell shell = editor.getSite().getShell();
        if (!target.canDoOperation(operationCode))
        {
            if (shell != null)
            {
                MessageDialog.openError(shell, "ToggleComment Error", "ToggleComment Error");
            }
            return;
        }

        IWorkbench wb = PlatformUI.getWorkbench();
        IProgressService ps = wb.getProgressService();
        try
        {
            ps.busyCursorWhile(new IRunnableWithProgress()
            {
                public void run(IProgressMonitor pm)
                {
                    shell.getDisplay().syncExec(new Runnable()
                    {
                        public void run()
                        {
                            target.doOperation(operationCode);
                        }
                    });
                }
            });
        }
        catch (InvocationTargetException exception)
        {
            Activator plugin = Activator.getDefault();
            IStatus status = new Status(IStatus.ERROR, plugin.getBundle().getSymbolicName(), 0, "Format Error", exception);
            plugin.getLog().log(status);
            MessageDialog.openError(shell, "Format Error", "Format Error");
        }
        catch (InterruptedException exception)
        {
            // ignore
        }
    }

    /**
     * Is the given selection single-line commented?
     * 
     * @param selection
     *            Selection to check
     * @return <code>true</code> iff all selected lines are commented
     */
    private boolean isSelectionCommented(ISelection selection)
    {

        if (!(selection instanceof ITextSelection))
        {
            return false;
        }

        ITextSelection textSelection = (ITextSelection) selection;
        if (textSelection.getStartLine() < 0 || textSelection.getEndLine() < 0)
        {
            return false;
        }

        IDocument document = getTextEditor().getDocumentProvider().getDocument(getTextEditor().getEditorInput());

        ITypedRegion block = getTextBlockFromSelection(textSelection, document);

        String[] prefixes = prefixesMap.get(block.getType());
        if (prefixes != null && prefixes.length > 0)
        {
            return isBlockCommented(textSelection.getStartLine(), textSelection.getEndLine(), prefixes, document);
        }

        return true;
    }

    /**
     * Determines whether each line is prefixed by one of the prefixes.
     * 
     * @param startLine
     *            Start line in document
     * @param endLine
     *            End line in document
     * @param prefixes
     *            Possible comment prefixes
     * @param document
     *            The document
     * @return <code>true</code> iff each line from <code>startLine</code> to
     *         and including <code>endLine</code> is prepended by one of the
     *         <code>prefixes</code>, ignoring whitespace at the begin of line
     */
    private boolean isBlockCommented(int startLine, int endLine, String[] prefixes, IDocument document)
    {

        try
        {
            for (int i = startLine; i <= endLine; i++)
            {

                IRegion line = document.getLineInformation(i);
                String text = document.get(line.getOffset(), line.getLength());

                int[] found = TextUtilities.indexOf(prefixes, text, 0);
                if (found[0] == -1)
                {
                    return false;
                }

                String s = document.get(line.getOffset(), found[0]);
                s = s.trim();
                if (s.length() != 0)
                {
                    return false; // found a line that is not commented
                }
            }

            return true;
        }
        catch (org.eclipse.jface.text.BadLocationException exception)
        {
            exception.printStackTrace();
        }

        return false;
    }

    /**
     * Creates a region describing the text block (something that starts at the
     * beginning of a line) completely containing the current selection.
     * 
     * @param selection
     *            The selection to use
     * @param document
     *            The document
     * @return the region describing the text block comprising the given
     *         selection
     */
    private ITypedRegion getTextBlockFromSelection(ITextSelection selection, IDocument document)
    {

        try
        {
            IRegion line = document.getLineInformationOfOffset(selection.getOffset());
            int length = selection.getLength() == 0 ? line.getLength() : selection.getLength() + (selection.getOffset() - line.getOffset());
            return new TypedRegion(line.getOffset(), length, document.getContentType(selection.getOffset()));
        }
        catch (org.eclipse.jface.text.BadLocationException exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.TextEditorAction#setEditor(
     * org.eclipse.ui.texteditor.ITextEditor )
     */
    @Override
    public void setEditor(ITextEditor editor)
    {
        super.setEditor(editor);
        target = null;
    }

    public void configure(ISourceViewer sourceViewer, SourceViewerConfiguration configuration)
    {
        prefixesMap = null;

        String[] types = configuration.getConfiguredContentTypes(sourceViewer);
        Map<String, String[]> map = new HashMap<String, String[]>(types.length);

        for (int i = 0; i < types.length; i++)
        {
            String type = types[i];
            String[] prefixes = configuration.getDefaultPrefixes(sourceViewer, type);
            if (prefixes != null && prefixes.length > 0)
            {

                int emptyPrefixes = 0;
                for (int j = 0; j < prefixes.length; j++)
                {
                    if (prefixes[j].length() == 0) emptyPrefixes++;
                }

                if (emptyPrefixes > 0)
                {

                    String[] nonemptyPrefixes = new String[prefixes.length - emptyPrefixes];

                    for (int j = 0, k = 0; j < prefixes.length; j++)
                    {
                        String prefix = prefixes[j];
                        if (prefix.length() != 0)
                        {
                            nonemptyPrefixes[k] = prefix;
                            k++;
                        }
                    }

                    prefixes = nonemptyPrefixes;
                }

                map.put(type, prefixes);
            }
        }

        prefixesMap = map;
    }
}