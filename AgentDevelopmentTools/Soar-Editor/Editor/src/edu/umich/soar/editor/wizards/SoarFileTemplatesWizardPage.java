package edu.umich.soar.editor.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.umich.soar.editor.Activator;

public class SoarFileTemplatesWizardPage extends WizardPage {

	String[] templateNames;
	List<Button> boxes;
	SoarFileWizard wizard;
	
	public SoarFileTemplatesWizardPage(SoarFileWizard wizard) {
		super("Templates", "Select Rule Tempaltes", null);
		templateNames = new String[] { "Propose", "Elaborate", "Compare", "Apply" };
		this.wizard = wizard;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		composite.setLayout(gl);
		boxes = new ArrayList<Button>();
		for (String templateName : templateNames)
		{
			new Label(composite, SWT.NONE).setText(templateName);
			boxes.add(new Button(composite, SWT.CHECK));
		}
		setControl(composite);
	}
	
    protected String getTemplates() {
    	StringBuilder ret = new StringBuilder();
		for (int i = 0; i < boxes.size(); ++i)
		{
			Button box = boxes.get(i);
			if (box.getSelection())
			{
				String templateName = "template-" + templateNames[i].toLowerCase() + ".soar";
		    	InputStream is = null;
		        try {
		        	is = Activator.getDefault().getBundle().getEntry("/resources/" + templateName).openStream();
		        } catch (IOException e) {
		        	e.printStackTrace();
		            return null;
		        }
	        	StringBuffer sb = new StringBuffer();
	        	while (true)
	        	{
	        		int ii;
					try {
						ii = is.read();
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
	        		if (ii == -1) break;
	        		sb.append((char)ii);
	        	}
	        	String str = sb.toString();
	        	String name = wizard.getFileName();
	        	String folder = wizard.getFolderName();
	        	int dot = name.indexOf('.');
	        	if (dot != -1)
	        	{
	        		name = name.substring(0, dot);
	        	}
	        	str = str.replace("${operator}", name);
	        	str = str.replace("${problemspace}", folder);
	        	ret.append(str + "\n\n");
			}
		}
		return ret.toString();
    }

}
