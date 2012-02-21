package edu.umich.soar.editor.contexts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.templates.TemplateContextType;

import edu.umich.soar.editor.contexts.SoarTemplateResolver.Variable;
import edu.umich.soar.editor.editors.SoarEditor;

public class SoarContextType extends TemplateContextType {

	public static final String CONTEXT_ID = "edu.umich.soar.editor.contexts.SoarContext";

	List<SoarTemplateResolver> resolvers = new ArrayList<SoarTemplateResolver>();
	
	public SoarContextType()
	{
		for (Variable variable : SoarTemplateResolver.Variable.values())
		{
		    SoarTemplateResolver resolver = new SoarTemplateResolver(variable);
		    resolvers.add(resolver);
			addResolver(resolver);
		}
	}
	
	public void setEditor(SoarEditor editor)
	{
	    for (SoarTemplateResolver resolver : resolvers)
	    {
	        resolver.setEditor(editor);
	    }
	}
}