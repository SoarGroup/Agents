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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.graphics.Color;

/**
 * <code>KeywordRule</code> serves as base class for all <code>WordRule</code>s
 * used by <code>SoarRuleScanner</code>.
 *
 * @author annmarie.steichmann@soartech.com
 * @version $Revision: 578 $ $Date: 2009-06-22 13:05:30 -0400 (Mon, 22 Jun 2009) $
 */
public abstract class KeywordRule
    extends WordRule {

    /**
     * Constructor for a <code>KeywordRule</code> object.
     * @param detector
     */
    public KeywordRule( KeywordDetector detector ) {

        super( detector );
        initialize();
    }
    
    protected abstract Color getFgColor();
    
    protected abstract Color getBgColor();
    
    protected abstract int getProperty();
    
    protected abstract String[][] getAllKeywords();

    private void initialize() {
        
        IToken keywordToken = new Token( 
            new TextAttribute( getFgColor(), getBgColor(), getProperty() ) );
        
        for ( String[] keywords: getAllKeywords() ) {
            for ( String keyword: keywords )
                this.addWord( keyword, keywordToken );
        }
    }

    /**
     * Reset the <code>KeywordDetector</code>.
     */
    public void resetDetector() {
        
        ((KeywordDetector)fDetector).reset();
    }
}
