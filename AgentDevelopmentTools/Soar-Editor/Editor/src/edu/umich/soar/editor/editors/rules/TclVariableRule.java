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

import java.util.Set;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import edu.umich.soar.editor.editors.SyntaxColorManager;

/**
 * <code>TclVariableRule</code> <code>IRule</code> implementation for Tcl
 * variables.  e.g. (state <s> ^$myvar $myval)
 *
 * @author annmarie.steichmann@soartech.com
 * @version $Revision: $ $Date: $
 */
public class TclVariableRule extends RegexRule {
    
    private IToken token;
    
    /**
     * Constructor for a <code>TclVariableRule</code> object.
     */
    public TclVariableRule() {
    	
    	super( "([\\w-])+" );
    	this.token = new Token( 
                new TextAttribute( SyntaxColorManager.getTclVarColor() ) );
    }
        
    /**
     * @return All Tcl variables that have been stored during scanning
     * @see <code>TclVariableRule#evaluate</code>
     */
    public String[][] getAllTclVariables() {
        
    	Set<String> uniqueStoreValues = getUniqueStoreValues();
        String[] variables = new String[ uniqueStoreValues.size() ];
        int i = 0;
        for ( String text: uniqueStoreValues ) {
        	if ( text != null && text.trim().length() > 0 ) {
        		variables[i++] = "$" + text;
        	}
        }
        return new String[][] { variables };
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
     */
    public IToken evaluate( ICharacterScanner scanner ) {

        if ( sequenceDetected( scanner, "$".toCharArray() ) ) {
            
            StringBuffer charsRead = new StringBuffer();
            
            // TODO: What other characters can 'terminate' a Tcl variable?
            while ( !sequenceDetected( scanner, ")] $\".".toCharArray() ) &&
                    !endOfLineDetected( scanner ) && 
                    !eofDetected( scanner ) ) {
                int c = scanner.read();
                charsRead.append( (char)c );
            }
            
            if ( matchesRegex( charsRead ) ) {
                addCharsToStore( scanner, charsRead );
                scanner.unread();
                return token;
            } else if ( charsRead.length() == 0 ) {
            	clearCharsToStore( scanner );
            	return token;
            }
            
            unreadBuffer( scanner, charsRead );
        }
        return Token.UNDEFINED;
    }    
    
    /* (non-Javadoc)
     * @see com.soartech.soar.ide.ui.editors.text.rules.RegexRule#sequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner, char[])
     */
    protected boolean sequenceDetected( ICharacterScanner scanner, char[] sequence ) {

    	int c = scanner.read();
    	for ( char s : sequence ) {
    		if ( c == s ) return true;
    	}
    	scanner.unread();
        return false;
    }     
}
