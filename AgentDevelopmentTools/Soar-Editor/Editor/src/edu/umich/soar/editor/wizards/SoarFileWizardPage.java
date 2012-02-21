package edu.umich.soar.editor.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class SoarFileWizardPage extends WizardNewFileCreationPage {
	
	SoarFileWizard wizard;

    public SoarFileWizardPage(IStructuredSelection selection, SoarFileWizard wizard) {
        super("Soar File", selection);
        setTitle("Soar File");
        setDescription("Creates a new Soar file");
        setFileExtension("soar");
        this.wizard = wizard;
    }

    /*
    @Override
    protected InputStream getInitialContents() {
       	return new ByteArrayInputStream(wizard.getTemplatesString().getBytes());
    }
    */
}
