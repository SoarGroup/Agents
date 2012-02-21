package edu.umich.soar.editor.editors.fix;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHoverExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import edu.umich.soar.editor.editors.SoarEditor;

public class SoarHover implements IAnnotationHover, IAnnotationHoverExtension, IInformationControlCreator, ITextHover, ITextHoverExtension,
        ITextHoverExtension2
{

    private SoarEditor editor;
    private ISourceViewer viewer;

    public SoarHover(SoarEditor editor, ISourceViewer viewer)
    {
        this.editor = editor;
        this.viewer = viewer;
    }

    @Override
    public String getHoverInfo(ISourceViewer viewer, int lineNumber)
    {
        IDocument document = viewer.getDocument();
        IAnnotationModel annotationModel = viewer.getAnnotationModel();
        Iterator<?> it = annotationModel.getAnnotationIterator();
        while (it.hasNext())
        {
            Object obj = it.next();
            if (!(obj instanceof MarkerAnnotation)) continue;
            MarkerAnnotation marker = (MarkerAnnotation) obj;
            try
            {
                int markerPosition = annotationModel.getPosition(marker).getOffset();
                int markerLine = document.getLineOfOffset(markerPosition);
                if (markerLine == lineNumber)
                {
                    return marker.getMarker().getAttribute(IMarker.MESSAGE, (String) null);
                }
            }
            catch (BadLocationException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean canHandleMouseCursor()
    {
        return false;
    }

    @Override
    public IInformationControlCreator getHoverControlCreator()
    {
        return this;
    }

    @Override
    public Object getHoverInfo(ISourceViewer viewer, ILineRange lineRange, int numVisibleLines)
    {
        return getHoverInfo(viewer, lineRange.getStartLine());
    }

    @Override
    public ILineRange getHoverLineRange(ISourceViewer viewer, int lineNumber)
    {
        return new LineRange(lineNumber, 1);
    }

    @Override
    public IInformationControl createInformationControl(Shell shell)
    {
        SoarInformationControl ret = new SoarInformationControl(editor, shell);
        return ret;
    }

    @Override
    public String getHoverInfo(ITextViewer viewer, IRegion region)
    {
        if (viewer instanceof ISourceViewer)
        {
            IDocument document = viewer.getDocument();
            try
            {
                int line = document.getLineOfOffset(region.getOffset());
                return getHoverInfo((ISourceViewer) viewer, line);
            }
            catch (BadLocationException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public IRegion getHoverRegion(ITextViewer viewer, int offset)
    {
        return new Region(offset, 0);
    }

    @Override
    public Object getHoverInfo2(ITextViewer viewer, IRegion region)
    {
        if (!(viewer instanceof ISourceViewer)) return null;
        IAnnotationModel annotationModel = ((ISourceViewer) viewer).getAnnotationModel();
        Iterator<?> it = annotationModel.getAnnotationIterator();
        while (it.hasNext())
        {
            Object obj = it.next();
            if (!(obj instanceof MarkerAnnotation)) continue;
            MarkerAnnotation marker = (MarkerAnnotation) obj;
            Position position = annotationModel.getPosition(marker);
            if (position.offset <= region.getOffset() && position.offset + position.length > region.getOffset())
            {
                return marker;
            }
        }
        return null;
    }

}
