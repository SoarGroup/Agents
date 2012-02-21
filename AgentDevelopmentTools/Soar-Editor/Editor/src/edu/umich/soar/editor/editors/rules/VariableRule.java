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
import org.eclipse.jface.text.rules.Token;

import edu.umich.soar.editor.editors.SyntaxColorManager;

/**
 * <code>VariableRule</code> <code>RegexRule</code> implementation for Soar
 * variables.
 * 
 * @author annmarie.steichmann@soartech.com
 * @version $Revision: 578 $ $Date: 2007-08-08 14:11:22 -0400 (Wed, 08 Aug
 *          2007) $
 */
public class VariableRule extends RegexRule
{
    /**
     * Constructor for a <code>VariableRule</code> object.
     */
    public VariableRule()
    {

        super("<", ">", "([\\w-])+", new Token(new TextAttribute(
                SyntaxColorManager.getVariableColor())));
    }

    /**
     * @return All variables that have been stored during scanning
     * @see <code>RegexRule#evaluate</code>
     */
    public String[][] getAllVariables()
    {

        Set<String> uniqueStoreValues = getUniqueStoreValues();
        String[] variables = new String[uniqueStoreValues.size()];
        int i = 0;
        for (String text : uniqueStoreValues)
        {
            if (text != null && text.trim().length() > 0)
            {
                variables[i++] = "<" + text + ">";
            }
        }
        return new String[][] { variables };
    }
}
