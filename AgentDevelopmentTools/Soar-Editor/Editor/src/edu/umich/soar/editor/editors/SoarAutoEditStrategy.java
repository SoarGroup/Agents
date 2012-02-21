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
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

/**
 * @author aron
 *
 */
public class SoarAutoEditStrategy extends DefaultIndentLineAutoEditStrategy 
{
	/**
     * This variable keeps track of the ratio of opening to closing parens. An
     * '(' is +1 and a ')' is -1. If the value is 0, then there is a closing
     * paren for every open paren.
     * 
     * NOTE: It is up to any method in this class auto-generates code to
     * update this stack count if/when necessary.
     * 
     */
    private int parenStack = 0;
    
    /**
     * The length of the document. 
     * 
     * Used to track changes to the document that were made with commands that
     * do not get tracked in this object (Undo/Redo). 
     */
    private int documentLength = 0;
    
    /**
     * Is this the first time this document has been opened this session?
     */
    private boolean initialOpen = true;
    
    /*
    private TabPrefs prefs;
    
    public void setIndentPrefs(TabPrefs prefs)
    {
        this.prefs = prefs;
    }

    public TabPrefs getIndentPrefs()
    {
        if (this.prefs == null)
        {
            this.prefs = new TabPrefs();
        }
        return this.prefs;
    }
    */

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.IAutoEditStrategy#customizeDocumentCommand(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.DocumentCommand)
     */
    public void customizeDocumentCommand(IDocument document, DocumentCommand command)
    {
        //check for changes to the document made without this object's knowledge
        if(initialOpen || documentLength != document.getLength())
        {
            stackParens(document.get(), true);
            documentLength = document.getLength();
            initialOpen = false;
        }
        
        updateParenStack(document, command);
        
        //System.out.println(command.length + ", |" + command.text + "|");
        
        if(command.length == 0 && command.text != null && isNewline(document, command))
        {
            autoIndent(document, command);
        }
        else if(command.length == 0 && command.text != null && command.text.equals(">"))
        {
            unindentArrow(document, command);
        }
        else if(command.length == 0 && command.text != null && command.text.equals("}"))
        {
            unindentBrace(document, command);
        }
        else if(command.length == 0 && command.text != null && command.text.equals("^"))
        {
            unindentNegativeAttribute(document, command);
        }
        
        // getIndentPrefs().convertToStd(document, command);
        
        //update the stored length document length to reflect changes
        //this should be done after all auto-edit changes
        if(command.length == 0)
        {
            documentLength += command.text.length();
        }
        else
        {
            documentLength -= command.length;
        }
    }
    

    /**
     * Update the paren stack with the given command.
     * 
     * @param document The document
     * @param command The command
     */
    private void updateParenStack(IDocument document, DocumentCommand command)
    {
        if(command.text == null)
        {
            return;
        }
        
        String text = command.text;
        
        //if true, then it it is a delete or 'cut' command
        //do this because the command doesn't hold the text that was deleted
        if(text.length() < command.length)
        {
            String documentText = removeDeletedText(document, command);
            stackParens(documentText, true);
            return;
        }
        else
        {
            stackParens(text, false);
        }
    }
    
    /**
     * Iterate over the characters in the given string updating the 
     * paren stack whenever a paren is read.
     * 
     * @param text The text to scan.
     * @param reset Reset the paren stack before scanning?
     */
    private void stackParens(String text, boolean reset)
    {
        if(reset)
        {
            parenStack = 0;
        }
        
        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            
            if(c == '(')
            {
                parenStack++;
            }
            else if(c == ')')
            {
                parenStack--;
            }
        }
    }
    
    
    /**
     * Is the command that was typed a newline?
     * 
     * @param document The document
     * @param command The command
     * @return
     */
    private boolean isNewline(IDocument document, DocumentCommand command)
    {
        return command.text.endsWith("\n");
    }
    
    /**
     * Return the document's full text without the part that has been deleted
     * with the given command.
     * 
     * @param document The document
     * @param command The command
     * @return the new document text
     */
    private String removeDeletedText(IDocument document, DocumentCommand command)
    {
        int offset = command.offset;
        int range = command.length;
        
        String doc = document.get();
        
        String firstPart = doc.substring(0, offset);
        String secondPart = doc.substring(offset + range, document.getLength());
        
        return firstPart + secondPart;
    }
    
    private void autoIndent(IDocument document, DocumentCommand command)
    {
        int amount = getAutoIndentAmount(document, command);
        if (amount != -1)
        {
            command.text = addWhitespace(command.text, amount);
        }
    }

    /**
     * Add spaces to the given command's text to indent it as much as the
     * previous line.
     * 
     * @param document The document.
     * @param command The command.
     */
    private int getAutoIndentAmount(IDocument document, DocumentCommand command)
    {
        //check the validity of the document
        int docLength = document.getLength();
        if(command.offset == -1 || docLength == 0)
        {
            return -1;
        }
        
        //get the command offset
        int offset = command.offset;
        
        //get the line numbers of the current and new lines
        //NOTE: the document's line numbers are 0-based, while the displayed
        //      line numbers in the editor are 1-based.
        int newLineNumber = 0;
        try {
            newLineNumber = document.getLineOfOffset(offset) + 1;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        int curLineNumber = newLineNumber - 1;
        
        String curLine = null;

        // Gets the last line of Soar code (skips blanks lines and comment lines)
        if (newLineNumber <= 0) return -1;

        boolean goodLine = false;
        while ((curLineNumber > -1) && !goodLine)
        {
            try
            {
                int curLineStart = document.getLineOffset(curLineNumber);
                curLine = document.get(curLineStart, offset - curLineStart);
            }
            catch (BadLocationException e)
            {
                e.printStackTrace();
            }

            if ((curLine == null) || curLine.trim().startsWith("#") || (curLine.trim().length() == 0))
            {
                curLineNumber--;
            }
            else
            {
                goodLine = true;
            }
        }

        int numSpaces = 0;
        
        String trimmed = curLine.trim();
        if(
           ((trimmed.startsWith("(")) && (trimmed.endsWith(")"))) ||
           ((trimmed.startsWith("[")) && (trimmed.endsWith("]"))) ||
           (trimmed.startsWith(":")))
        {
            numSpaces = getIndentAmount(curLine);
        }
        else if(((trimmed.startsWith("sp")) && (trimmed.indexOf("{") != -1)) ||
                (trimmed.startsWith("-->")))
        {   
            numSpaces = 3;
        }
        else if((trimmed.endsWith(")")))
        {
            //align indent with matching open paren
            int openParenOffset = SoarPairMatcher.findMatchingOpenParen(document, offset - 1, SoarPairMatcher.parens);
            if (openParenOffset >= 0) {
                try {
                    int openParenLine = document.getLineOfOffset(openParenOffset);
                    int openParenLineStart = document.getLineOffset(openParenLine);
                    numSpaces = openParenOffset - openParenLineStart;
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
        else if((trimmed.startsWith("(") || trimmed.startsWith("^") || trimmed.startsWith("-^"))
                  && (trimmed.indexOf(")") == -1)
                  && (trimmed.indexOf("^") != -1) )
        {
            //align indent with the ^
            numSpaces = curLine.indexOf("^");
        }
        
        return numSpaces;
    }

    /**
     * Add spaces to the given command's text to indent it as much as the
     * previous line.
     * 
     * @param document The document.
     * @param command The command.
     */
    private String getPreviousLine(IDocument document, DocumentCommand command)
    {
        //check the validity of the document
        int docLength = document.getLength();
        if(command.offset == -1 || docLength == 0)
        {
            return null;
        }
        
        //get the command offset
        int offset = command.offset;
        
        //get the line numbers of the current and new lines
        //NOTE: the document's line numbers are 0-based, while the displayed
        //      line numbers in the editor are 1-based.
        int newLineNumber = 0;
        try {
            newLineNumber = document.getLineOfOffset(offset) + 1;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        int curLineNumber = newLineNumber - 1;
        
        String curLine = null;

        // Gets the last line of Soar code (skips blanks lines and comment lines)
        if (newLineNumber <= 0) return null;

        boolean goodLine = false;
        int numLines = 0;
        while ((curLineNumber > -1) && !goodLine)
        {
            try
            {
                int curLineStart = document.getLineOffset(curLineNumber);
                curLine = document.get(curLineStart, offset - curLineStart);
            }
            catch (BadLocationException e)
            {
                e.printStackTrace();
            }

            if ((curLine == null) || curLine.trim().startsWith("#") || (curLine.trim().length() == 0) || numLines < 1)
            {
                curLineNumber--;
                offset -= curLine.length();
            }
            else
            {
                goodLine = true;
            }
            ++numLines;
        }
        
        return curLine;
    }
    
    private static int getCaratOffsetForLine(String line)
    {
        String trimmed = line.trim();
        int numSpaces = -1;
        if(
           ((trimmed.startsWith("(")) && (trimmed.endsWith(")"))) ||
           ((trimmed.startsWith("[")) && (trimmed.endsWith("]"))) ||
           (trimmed.startsWith(":")))
        {
            //align indent with previous line
            numSpaces = getIndentAmount(line);
            return -1;
        }
        else if(((trimmed.startsWith("sp")) && (trimmed.indexOf("{") != -1)) ||
                (trimmed.startsWith("-->")))
        {   
            numSpaces = 3; // getIndentPrefs().getTabWidth();
            return -1;
        }
        /*
        else if((current.endsWith(")")))
        {
            //align indent with matching open paren
            int openParenOffset = SoarPairMatcher.findMatchingOpenParen(document, offset - 1, SoarPairMatcher.parens);
            if (openParenOffset >= 0) {
                try {
                    int openParenLine = document.getLineOfOffset(openParenOffset);
                    int openParenLineStart = document.getLineOffset(openParenLine);
                    numSpaces = openParenOffset - openParenLineStart;
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
        */
        else if((trimmed.startsWith("(") || trimmed.startsWith("^") || trimmed.startsWith("-^"))
                  && (trimmed.indexOf(")") == -1)
                  && (trimmed.indexOf("^") != -1) )
        {
            //align indent with the ^
            numSpaces = line.indexOf("^");
        }
        return numSpaces;
    }
    
    /**
     * Get the indent amount for the next line.
     * 
     * @param curLine The current line (preceding the indented line) of text.
     * @return the indent amount
     */
    private static int getIndentAmount(String curLine)
    {
    	String trimmed = curLine.trim();
    	int indent = curLine.indexOf(trimmed.substring(0, 1));
    	
    	return indent;
    }
    
    /**
     * Add whitespace to the end of the string.
     * 
     * @param text The string to add whitespace to.
     * @param length The amount to add.
     * @return the new string with whitespace added
     */
    private String addWhitespace(String text, int length)
    {
        for(int i = 0; i < length; i++)
        {
            text += " ";
        }
        return text;
    }
    
    /**
     * @param document
     * @param command
     */
    private void unindentArrow(IDocument document, DocumentCommand command)
    {
        assert command.text.length() == 1;
        
        if(command.offset <= 2)
        {
            return;
        }
        
        try
        {
            // Check if > is preceded by two dashes
            char dash1 = document.getChar(command.offset - 1);
            char dash0 = document.getChar(command.offset - 2);
            
            if(dash1 != '-' || dash0 != '-')
            {
                return;
            }
            
            int begin = command.offset - 3;
            while(begin >= 0)
            {
                char c = document.getChar(begin);
                if(!Character.isWhitespace(c))
                {
                    return;
                }
                if(c == '\n' || c == '\r')
                {
                    break;
                }
                --begin;
            }
            
            if(begin < 0)
            {
                return;
            }
            ++begin; // skip line feed.
            
            // Now modify the command so that it replaces the line with -->.
            int replacedLength = (command.offset - 2) - begin;
            System.out.println("arrow begin: " + begin);
            command.offset = begin;
            command.length = replacedLength + 2;
            command.text = "-->";
        }
        catch (BadLocationException e)
        {
        }
    }
    
    /**
     * @param document
     * @param command
     */
    private void unindentNegativeAttribute(IDocument document, DocumentCommand command)
    {
        assert command.text.length() == 1;
        
        if(command.offset <= 1)
        {
            return;
        }
        
        try
        {
            // Check if > is preceded by one dashes
            char dash1 = document.getChar(command.offset - 1);
            
            if(dash1 != '-')
            {
                return;
            }
            
            int begin = command.offset - 2;
            while(begin >= 0)
            {
                char c = document.getChar(begin);
                if(!Character.isWhitespace(c))
                {
                    return;
                }
                if(c == '\n' || c == '\r')
                {
                    break;
                }
                --begin;
            }
            
            if(begin < 0)
            {
                return;
            }
            ++begin; // skip line feed.
            
            String previousLine = getPreviousLine(document, command);
            int indent = getCaratOffsetForLine(previousLine);
            if (indent == -1) return;
            indent -= 1;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indent; ++i)
            {
                sb.append(" ");
            }
            
            // Now modify the command so that it replaces the line with -->.
            int replacedLength = (command.offset - 2) - begin;
            System.out.println("arrow begin: " + begin);
            command.offset = begin;
            command.length = replacedLength + 2;
            command.text = sb.toString() + "-^";
        }
        catch (BadLocationException e)
        {
        }
    }
    
    
    
    /**
     * @param document
     * @param command
     */
    /*
    private void unindentNegativeAttribute(IDocument document, DocumentCommand command)
    {
        assert command.text.length() == 1;
        
        if(command.offset <= 1)
        {
            return;
        }
        
      //check the validity of the document
        int docLength = document.getLength();
        if(command.offset == -1 || docLength == 0)
        {
            return;
        }
        
        //get the command offset
        int offset = command.offset;
        
        //get the line numbers of the current and new lines
        //NOTE: the document's line numbers are 0-based, while the displayed
        //      line numbers in the editor are 1-based.
        int newLineNumber = 0;
        try {
            newLineNumber = document.getLineOfOffset(offset) + 1;
        } catch (BadLocationException e) {
            e.printStackTrace();
            return;
        }
        int prevLineNumber = newLineNumber - 2;
        
        int thisLineNumber = newLineNumber - 1;
        int thisLineStart;
        try
        {
            thisLineStart = document.getLineOffset(thisLineNumber);
            String thisLine = document.get(thisLineStart, offset - thisLineStart);
            offset -= thisLine.length();
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
            return;
        }
        
        String previousLine = null;

        // Gets the last line of Soar code (skips blanks lines and comment lines)
        if (newLineNumber <= 0) return;

        boolean goodLine = false;
        while ((prevLineNumber > -1) && !goodLine)
        {
            try
            {
                int curLineStart = document.getLineOffset(prevLineNumber);
                previousLine = document.get(curLineStart, offset - curLineStart);
            }
            catch (BadLocationException e)
            {
                e.printStackTrace();
            }

            if ((previousLine == null) || previousLine.trim().startsWith("#") || (previousLine.trim().length() == 0))
            {
                prevLineNumber--;
            }
            else
            {
                goodLine = true;
            }
        }
        
        try
        {
            // Check if ^ is preceded by -
            char dash = document.getChar(command.offset - 1);
            
            if(dash != '-')
            {
                return;
            }
            
            int begin = command.offset - 2;
            while(begin >= 0)
            {
                char c = document.getChar(begin);
                if(!Character.isWhitespace(c))
                {
                    return;
                }
                if(c == '\n' || c == '\r')
                {
                    break;
                }
                --begin;
            }
            ++begin; // skip line feed
            
            int indentAmount = getOffsetForLine(previousLine);

            if (indentAmount == -1) return;
            System.out.println("arrow begin: " + begin);
            //command.offset = thisLineStart + indentAmount - 1;
            command.offset = begin;
            command.length = 2;
            command.text = "-^";
        }
        catch (BadLocationException e)
        {
        }
    }
    */
    
    /**
     * @param document
     * @param command
     */
    private void unindentBrace(IDocument document, DocumentCommand command)
    {
        assert command.text.length() == 1;
        
        try
        {
            int begin = command.offset - 1;
            while(begin >= 0)
            {
                char c = document.getChar(begin);
                if(!Character.isWhitespace(c))
                {
                    return;
                }
                if(c == '\n' || c == '\r')
                {
                    break;
                }
                --begin;
            }
            
            if(begin < 0)
            {
                return;
            }
            ++begin; // skip line feed.
            
            // Now modify the command so that it replaces the line with -->.
            int replacedLength = (command.offset - 2) - begin;
            command.offset = begin;
            command.length = replacedLength + 2;
            command.text = "}";
        }
        catch (BadLocationException e)
        {
        }
    }
}
