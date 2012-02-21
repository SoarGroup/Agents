package edu.umich.soar.editor.editors.datamap.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.internal.resources.Marker;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchEvent;

public class DatamapSearchResultSet extends AbstractTextSearchResult // implements ISearchResult
{

    public static class ResultItem extends Match
    {
        public IFile file;
        public int offset;
        
        public ResultItem(IFile file, int offset, int length)
        {
            super(file, offset, length);
            this.file = file;
            this.offset = offset;
        }
        
        @Override
        public String toString()
        {
            return file.getName() + ":" + offset;
        }
    }
    
    public static class ResultEvent extends MatchEvent
    {
        private static final long serialVersionUID = 6760544304418308510L;
        public ResultItem item;
        
        public ResultEvent(DatamapSearchResultSet result, ResultItem item)
        {
            super(result);
            setKind(ADDED);
            setMatch(item);
            this.item = item;
        }
    }
    
    private List<ResultItem> resultItems;
    private String title;
    private Collection<ISearchResultListener> listeners;
    
    public DatamapSearchResultSet(String title)
    {
        resultItems = new ArrayList<ResultItem>();
        this.title = title;
        listeners = new HashSet<ISearchResultListener>();
    }
    
    public void addResult(ResultItem resultItem)
    {
        resultItems.add(resultItem);
        MatchEvent event = new ResultEvent(this, resultItem);
        for (ISearchResultListener listener : listeners)
        {
            listener.searchResultChanged(event);
        }
        addMatch(resultItem);
    }

    @Override
    public void addListener(ISearchResultListener listener)
    {
        super.addListener(listener);
        listeners.add(listener);
    }

    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }

    @Override
    public String getLabel()
    {
        return title;
    }

    @Override
    public ISearchQuery getQuery()
    {
        return null;
    }

    @Override
    public String getTooltip()
    {
        return title;
    }

    @Override
    public void removeListener(ISearchResultListener listener)
    {
        super.removeListener(listener);
        listeners.remove(listener);
    }

    @Override
    public IEditorMatchAdapter getEditorMatchAdapter()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IFileMatchAdapter getFileMatchAdapter()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
