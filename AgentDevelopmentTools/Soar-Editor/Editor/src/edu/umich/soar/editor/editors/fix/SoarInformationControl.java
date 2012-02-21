package edu.umich.soar.editor.editors.fix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import edu.umich.soar.editor.editors.SoarEditor;
import edu.umich.soar.editor.editors.datamap.Correction;
import edu.umich.soar.editor.editors.datamap.Datamap;

public class SoarInformationControl extends AbstractInformationControl implements IInformationControlExtension2
{

    private SoarEditor editor;
    private Object input;
    private Composite parent;

    public SoarInformationControl(SoarEditor editor, Shell shell)
    {
        super(shell, true);
        this.editor = editor;
        create();
    }

    @Override
    public boolean hasContents()
    {
        return input != null;
    }

    @Override
    public void setInput(Object input)
    {
        // getShell().setSize(600, 300);
        this.input = input;
        int numLines = 3;
        int lineWidth = 10;
        if (input instanceof String)
        {
            Label label = new Label(parent, SWT.NONE);
            label.setBackground(parent.getBackground());
            label.setForeground(parent.getForeground());
            String text = (String) input;
            label.setText((String) input);
            lineWidth = text.length();
        }
        else if (input instanceof MarkerAnnotation)
        {
            MarkerAnnotation annotation = (MarkerAnnotation) input;
            IMarker marker = annotation.getMarker();
            String text = (marker.getAttribute(IMarker.MESSAGE, ""));
            Label label = new Label(parent, SWT.NONE);
            label.setBackground(parent.getBackground());
            label.setForeground(parent.getForeground());
            label.setText(text);
            Correction correction = editor.findCorrection(marker);
            if (correction != null)
            {
                int numSolutions = correction.getNumSolutions();
                Datamap datamap = correction.node.getDatamap();
                String datamapName = datamap.getFilename();
                Link link = new Link(parent, SWT.NONE);
                link.setText("<a>Open datamap \"" + datamapName + "\"</a>");
                link.setBackground(parent.getBackground());
                link.setForeground(parent.getForeground());
                link.addSelectionListener(correction);
                link.setData(new Integer(-1));

                String baseSolutionText = correction.getBaseSolutionText();
                if (baseSolutionText != null)
                {
                    Label baseLabel = new Label(parent, SWT.None);
                    baseLabel.setText(baseSolutionText);
                    baseLabel.setBackground(parent.getBackground());
                    baseLabel.setForeground(parent.getForeground());
                }

                for (int i = 0; i < numSolutions; ++i)
                {
                    link = new Link(parent, SWT.NONE);
                    link.setText("<a>" + correction.getEndSolutionText(i) + "</a>");
                    link.setBackground(parent.getBackground());
                    link.setForeground(parent.getForeground());
                    link.addSelectionListener(correction);
                    link.setData(new Integer(i));
                }
                numLines += (numSolutions + 1) * 2;
            }
            lineWidth = text.length();
        }
        Point constraint = computeSizeConstraints(lineWidth, numLines);
        setSizeConstraints(constraint.x, constraint.y);
        getShell().setMinimumSize(constraint.x, constraint.y);
    }

    @Override
    protected void createContent(Composite parent)
    {
        System.out.println("creating content");
        this.parent = parent;
        this.parent.setLayout(new GridLayout(1, false));
        setBackgroundColor(this.parent.getBackground());
        setForegroundColor(this.parent.getForeground());
        // new Button(parent, 0);
    }

    @Override
    public IInformationControlCreator getInformationPresenterControlCreator()
    {
        return new IInformationControlCreator()
        {

            @Override
            public IInformationControl createInformationControl(Shell shell)
            {
                SoarInformationControl ret = new SoarInformationControl(editor, shell);
                return ret;
            }
        };
    }

}
