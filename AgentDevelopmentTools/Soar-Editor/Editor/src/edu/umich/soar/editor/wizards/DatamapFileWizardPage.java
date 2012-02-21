package edu.umich.soar.editor.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.xml.sax.InputSource;

import edu.umich.soar.editor.Activator;

public class DatamapFileWizardPage extends WizardNewFileCreationPage {

    public DatamapFileWizardPage(IStructuredSelection selection) {
        super("NewSoarFileWizardPage", selection);
        setTitle("Soar Datamap");
        setDescription("Creates a new Datamap file");
        setFileExtension("dm");
    }

    @Override
    protected InputStream getInitialContents() {
        try {
        	InputStream s = Activator.getDefault().getBundle().getEntry("/resources/default.dm").openStream();
        	StringBuffer sb = new StringBuffer();
        	while (true)
        	{
        		int i = s.read();
        		if (i == -1) break;
        		sb.append((char)i);
        	}
        	String str = sb.toString();
        	String name = getFileName();
        	int dot = name.indexOf('.');
        	if (dot != -1)
        	{
        		name = name.substring(0, dot);
        	}
        	str = str.replace("state-name", name);
        	return new ByteArrayInputStream(str.getBytes());
        } catch (IOException e) {
        	e.printStackTrace();
            return null;
        }
    }
}
