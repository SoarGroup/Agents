package edu.umich.soar.editor;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

public class SoarResourceChangeListener implements IResourceChangeListener
{

    @Override
    public void resourceChanged(IResourceChangeEvent event)
    {
        System.out.println("Resource Changed event");
        //IResource changed = event.getDelta().getResource();
        for (IResourceDelta delta : event.getDelta().getAffectedChildren())
        {
            System.out.println("Affected: " + delta.getProjectRelativePath());
        }
        
    }

}
