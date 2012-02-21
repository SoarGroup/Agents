package edu.umich.soar.editor;

import java.io.IOException;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.umich.soar.editor.contexts.SoarContextType;
import edu.umich.soar.editor.icons.SoarIcons;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.umich.soar.editor.SoarEditor"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
    private ContributionTemplateStore templateStore;
    private ContributionContextTypeRegistry contextRegistry;
    
    /** Unique key for referencing this plug-in's template store */
    private static final String CUSTOM_TEMPLATES_KEY = "edu.umich.soar.editor.template.CustomTemplates";

	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		/*
		IResourceChangeListener listener = new SoarResourceChangeListener();
		int mask = IResourceChangeEvent.POST_CHANGE;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, mask);
		*/
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		SoarIcons.init(registry);
	}
	
    /**
     * @return the plugin's template store
     */
    public TemplateStore getTemplateStore()
    {
        if (templateStore == null)
        {
            templateStore = new ContributionTemplateStore(
                    getContextTypeRegistry(), Activator.getDefault()
                            .getPreferenceStore(), CUSTOM_TEMPLATES_KEY);
            try
            {
                templateStore.load();
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }
        }
        return templateStore;
    }
    
    /**
     * @return the context type registry for this plug-in instance
     */
    public ContextTypeRegistry getContextTypeRegistry()
    {
        if (contextRegistry == null)
        {
            contextRegistry = new ContributionContextTypeRegistry();
            contextRegistry.addContextType(SoarContextType.CONTEXT_ID);
        }
        return contextRegistry;
    }
}
