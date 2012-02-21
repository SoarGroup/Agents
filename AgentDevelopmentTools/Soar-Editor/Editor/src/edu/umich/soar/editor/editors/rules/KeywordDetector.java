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
package edu.umich.soar.editor.editors.rules;

import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * <code>KeywordDetector</code> serves as the base class for all
 * <code>IWordDetector</code>s used by a <code>WordRule</code> instance
 * within the Soar Editor.
 * 
 * @author annmarie.steichmann@soartech.com
 * @version $Revision: 578 $ $Date: 2009-06-22 13:05:30 -0400 (Mon, 22 Jun 2009) $
 */
public abstract class KeywordDetector
    implements IWordDetector {
    
    // Track the previous char along with the current char
    // to ensure that quasi matches (i.e. "soar_source" for "source", etc.)
    // don't get erroneously made.
    protected char previousChar = '\0';
    
    protected abstract String getStartRegex();
    
    protected abstract String getPartRegex();
    
    protected abstract void setPreviousChar( char c );

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
     */
    public boolean isWordStart( char c ) {

        StringBuffer sb = new StringBuffer(2);
        
        if ( previousChar != '\0' ) {
            sb.append(previousChar);
        }
        
        sb.append(c);
        
        setPreviousChar( c );
        
        return Pattern.matches( getStartRegex(), sb );
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
     */
    public boolean isWordPart( char c ) {

        StringBuffer sb = new StringBuffer(1);
        sb.append(c);
 
        return Pattern.matches( getPartRegex(), sb );
    }
    
    /**
     * Resets the previous char.
     */
    public void reset() {
        
        previousChar = '\0';
    }
}
