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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * <code>RegexRule</code> implementation of <code>IRule</code>
 * that supports using a regular expression to match on.  Similar
 * to <code>SingleLineRule</code> except it matches on the middle
 * and not just the end characters.
 * 
 * TODO Right now this supports just a subset of the regular expressions.
 * May need to support other regular expressions.
 *
 * @author annmarie.steichmann@soartech.com
 * @version $Revision: 578 $ $Date: 2009-06-22 13:05:30 -0400 (Mon, 22 Jun 2009) $
 */
public class RegexRule implements IRule {
    
    private Map<Integer,String> store = new HashMap<Integer,String>();
    private char[] startSequence;
    private char[] endSequence;
    private String regex;
    private IToken token;
    
    /**
     * Constructor for a <code>RegexRule</code> object.
     * @param regex Matches the middle of the evaluation
     */    
    public RegexRule( String regex ) {
    	
    	this.regex = regex;
    }
    
    /**
     * Constructor for a <code>RegexRule</code> object.
     * @param startSequence The pattern's start sequence
     * @param endSequence The pattern's end sequence
     * @param regex Matches the middle of the evaluation, <code>null</code> 
     * is a legal value
     * @param token The token which will be returned on success
     */
    public RegexRule( String startSequence, String endSequence, 
                      String regex, IToken token ) {
        
        this.startSequence = startSequence.toCharArray();
        this.endSequence = endSequence.toCharArray();
        this.regex = regex;
        this.token = token;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
     */
    public IToken evaluate( ICharacterScanner scanner ) {

        if ( sequenceDetected( scanner, startSequence ) ) {
            
            StringBuffer charsRead = new StringBuffer();
            
            while ( !sequenceDetected( scanner, endSequence ) &&
                    !endOfLineDetected( scanner ) && 
                    !eofDetected( scanner )) 
            {
                // If we see the start sequence again, start over at that point. This
                // handles bug 1416.
                if(sequenceDetected( scanner, startSequence ))
                {
                    for(int i = 0; i < startSequence.length + charsRead.length(); ++i)
                    {
                        scanner.unread();
                    }
                    return Token.UNDEFINED;
                }
                int c = scanner.read();
                charsRead.append( (char)c );
            }
            
            if ( matchesRegex( charsRead ) ) {
                addCharsToStore( scanner, charsRead );
                return token;
            } else if ( charsRead.length() == 0 ) {
            	clearCharsToStore( scanner );
            	return token;
            }
            
            unreadBuffer( scanner, charsRead );
        }
        return Token.UNDEFINED;
    }
    
    protected Set<String> getUniqueStoreValues() {
    	
    	return new HashSet<String>( store.values() );
    }
    
    protected void clearCharsToStore( ICharacterScanner scanner ) {
    	
    	store.remove( scanner.getColumn() );
    }
    
    protected void addCharsToStore( ICharacterScanner scanner, StringBuffer charsRead ) {

    	store.put( scanner.getColumn() - charsRead.length(), charsRead.toString() );
    }

    protected boolean matchesRegex( StringBuffer charsRead ) {
        
        Pattern pattern = Pattern.compile( regex );
        Matcher matcher = pattern.matcher( charsRead );
        return matcher.matches();
    }

    protected void unreadBuffer( ICharacterScanner scanner, StringBuffer charsRead ) {

        for ( int i = 0; i < charsRead.length(); i++ ) {
            scanner.unread();
        }
    }
    
    protected boolean eofDetected( ICharacterScanner scanner ) {
        
        int c = scanner.read();
        if ( c == ICharacterScanner.EOF ) return true;
        scanner.unread();
        return false;
    }
    
    protected boolean endOfLineDetected( ICharacterScanner scanner ) {
        
        int c = scanner.read();
        if ( c == '\n' || c == '\r' ) return true;
        scanner.unread();
        return false;
    }

    protected boolean sequenceDetected( ICharacterScanner scanner, char[] sequence ) {

        int i = 0;
        for ( char s: sequence ) {
            i++;
            int c = scanner.read();
            
            if ( c != s ) {                
                for ( int j = 0; j < i; j++ ) {
                    scanner.unread();
                }
                return false;
            }
        }
        
        return true;
    }    
}
