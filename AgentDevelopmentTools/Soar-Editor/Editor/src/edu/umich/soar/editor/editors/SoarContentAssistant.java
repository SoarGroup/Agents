package edu.umich.soar.editor.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;

class SoarContentAssistant extends ContentAssistant {

	/**
     * Constructor for a <code>SoarContentAssistant</code> object.
     *
     * @param configuration The current <code>SoarSourceEditorConfiguration</code>
     */
    public SoarContentAssistant(SoarConfiguration configuration) {
        super();
        this.setContentAssistProcessor(new SoarCompletionProcessor(configuration), IDocument.DEFAULT_CONTENT_TYPE);
        this.enableAutoActivation(true);
        this.setProposalPopupOrientation(IContentAssistant.PROPOSAL_STACKED);
        //this.setInformationControlCreator(new SoarInformationControlCreator());
    }
}
