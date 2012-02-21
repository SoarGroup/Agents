package edu.umich.soar.editor.editors.fix;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class SoarCompletionProposal implements ICompletionProposal
{

    private IQuickAssistInvocationContext context;
    
    public SoarCompletionProposal(IQuickAssistInvocationContext context)
    {
        this.context = context;
    }

    @Override
    public void apply(IDocument arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getAdditionalProposalInfo()
    {
        return "additional info";
    }

    @Override
    public IContextInformation getContextInformation()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDisplayString()
    {
        return "display string";
    }

    @Override
    public Image getImage()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Point getSelection(IDocument arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
