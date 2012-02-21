package edu.umich.soar.editor.contexts;

import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

import edu.umich.soar.editor.editors.SoarEditor;


public class SoarTemplateResolver extends TemplateVariableResolver {

	public static enum Variable
	{
		PROBLEMSPACE,
		OPERATOR,
		SUPERSTATEOPERATOR;
		
		public String getName()
		{
			return toString().toLowerCase();
		}
	}
	
	private Variable variable;
	private SoarEditor editor;
	
	public SoarTemplateResolver(Variable variable) {
		super(variable.getName(), "Resolver for Soar rule templates.");
		this.variable = variable;
	}

	@Override
	protected String resolve(TemplateContext context)
	{
	    if (editor == null) return super.resolve(context);
	    if (variable == Variable.PROBLEMSPACE) return editor.getFolderName();
	    if (variable == Variable.OPERATOR) return editor.getFileName();
	    if (variable == Variable.SUPERSTATEOPERATOR) {
	        String parentName = editor.getParentFolderName();
	        if (parentName != null && !parentName.isEmpty())
	        {
	            return parentName;
	        }
	        return "superstate-operator";
	    }
	    return super.resolve(context);
	}
	
	@Override
	public void resolve(TemplateVariable variable, TemplateContext context)
	{
	    // TODO Auto-generated method stub
	    super.resolve(variable, context);
	}
	
	public void setEditor(SoarEditor editor)
	{
	    this.editor = editor;
	}

    public void setVars(String problemSpace, String operator)
    {
        // TODO Auto-generated method stub
        
    }
	
}
