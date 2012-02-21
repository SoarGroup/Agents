/*
 *Copyright (c) 2009, Soar Technology, Inc.
 *All rights reserved.
 *
 *Redistribution and use in source and binary forms, with or without modification,   *are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *  * Neither the name of Soar Technology, Inc. nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 *THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY  *EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED   *WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.   *IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,   *INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT   *NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR   *PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,    *WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)   *ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE    *POSSIBILITY OF SUCH *DAMAGE. 
 *
 * 
 */
package edu.umich.soar.editor.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;

/**
 * Character pair matcher class for Soar. 
 * 
 * An instance of this class is given to a MatchingCharacterPainter to make
 * paren highlighting work. The methods in this class figure out the 
 * locations of the matching parens and pass them to the painter for display.
 * 
 * It is currently implemented to only match pairs of parens.
 * 
 * @author aron
 *
 */
public class SoarPairMatcher implements ICharacterPairMatcher 
{
    public static final char[] parens = { '(', ')' };
     public static final char[] braces = { '{', '}' };
	
//	private final char openParen = '(';
//    private final char closeParen = ')';
    
    private int offset;
    
    private IDocument document;
    
    private int anchor;
    
    private int beginOffset;
    
    private int endOffset;

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#dispose()
     */
    public void dispose()
    {
        clear();
        document = null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#clear()
     */
    public void clear()
    {
        //nothing to do here as of now
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#match(org.eclipse.jface.text.IDocument, int)
     */
    public IRegion match(IDocument document, int offset)
    {
        this.offset = offset;
        
        if(offset < 0 || document.getLength() == 0)
        {
            return null;
        }
        
        this.document = document;
        
        if(document != null && matchPair(parens) && beginOffset != endOffset)
        {
            IRegion r = new Region(beginOffset, endOffset - beginOffset + 1); 
            return r;
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#getAnchor()
     */
    public int getAnchor()
    {
        return anchor;
    }
    
    /**
     * If the cursor is adjacent to a paren, find its match and save 
     * the location.
     * 
     * @return true if the cursor is adjacent to a paren, false otherwise
     */
    private boolean matchPair(char[] pair)
    {
    	char open = pair[0];
    	char close = pair[1];
        beginOffset = -1;
        endOffset = -1;
        
        try {
            char c = document.getChar(Math.max(offset - 1, 0));
            
            if(c == open)
            {
                beginOffset = offset - 1;
            }
            
            if(c == close)
            {
                endOffset = offset - 1;
            }
            
            if(endOffset > -1)
            {
                anchor = RIGHT;
                beginOffset = findMatchingOpenParen(document, endOffset, pair);
                
                if(beginOffset > -1)
                {
                    return true;
                }
                else
                {
                    endOffset = -1;
                }
            }
            else if(beginOffset > -1)
            {
                anchor = LEFT;
                endOffset = findMatchingCloseParen(document, beginOffset, pair);
                
                if(endOffset > -1)
                {
                    return true;
                }
                else
                {
                    beginOffset = -1;
                }
            }            
        } catch (BadLocationException e) { e.printStackTrace(); }
        
        return false;
    }
    
    /**
     * Find the closing paren that matches the opening paren with the given 
     * offset.
     * 
     * @param document The document.
     * @param beginOffset The offset of the opening paren.
     * @return the offset of the matching paren
     */
    public static int findMatchingCloseParen(IDocument document, int beginOffset, char[] pair)
    {
        int stack = 1;
        int localOffset = beginOffset + 1;
        int docLength = document.getLength();
        char open = pair[0];
        char close = pair[1];
        
        try {
            char c;
            while(localOffset < docLength)
            {
                c = document.getChar(localOffset);
                
                if(c == open && c != close)
                {
                    stack++;
                }
                else if(c == close)
                {
                    stack--;
                }
                
                if(stack == 0)
                {
                    return localOffset;
                }
                
                localOffset++;
            }
        } catch (BadLocationException e) { e.printStackTrace(); }
        
        return -1;
    }
    
    
    /**
     * Find the opening paren that matches the closing paren with the given 
     * offset.
     * 
     * @param document The document.
     * @param endOffset The offset of the closing paren.
     * @return the offset of the matching paren
     */
    public static int findMatchingOpenParen(IDocument document, int endOffset, char[] pair)
    {
        int stack = 1;
        int localOffset = endOffset - 1;
        char open = pair[0];
        char close = pair[1];
        
        try {
            char c;
            while(localOffset >= 0)
            {
                c = document.getChar(localOffset);
                
                if(c == close && c != open)
                {
                    stack++;
                }
                else if(c == open)
                {
                    stack--;
                }
                
                if(stack == 0)
                {
                    return localOffset;
                }
                
                localOffset--;
            }
        } catch (BadLocationException e) { e.printStackTrace(); }
        
        return -1;
    }
}
