package edu.umich.soar.editor.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFolderMainPage;

public class SoarProblemSpaceWizardPage extends WizardNewFolderMainPage {
	
    SoarProblemSpaceWizard wizard;

    public SoarProblemSpaceWizardPage(IStructuredSelection selection, SoarProblemSpaceWizard wizard) {
        super("Soar Problem Space", selection);
        setTitle("Soar Problem Space");
        setDescription("Creates a new Soar Problem Space");
        this.wizard = wizard;
    }

    /*
    @Override
    protected InputStream getInitialContents() {
       	return new ByteArrayInputStream(wizard.getTemplatesString().getBytes());
    }
    */
}
