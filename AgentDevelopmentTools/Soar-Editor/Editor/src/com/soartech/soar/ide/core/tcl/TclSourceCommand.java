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
package com.soartech.soar.ide.core.tcl;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import org.eclipse.core.runtime.Path;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.RelocatableTclInterpreter;
import tcl.lang.TclException;
import tcl.lang.TclNumArgsException;
import tcl.lang.TclObject;

/**
 * @author ray
 */
public class TclSourceCommand implements Command
{
    private RelocatableTclInterpreter interp;
    private boolean changeDir;
    private boolean countOnly;
    private Stack<String> sourceStack = new Stack<String>();
    ArrayList<String> comments;
    
    /**
     * @param interp
     */
    public TclSourceCommand(RelocatableTclInterpreter interp, boolean changeDir, boolean countOnly, ArrayList<String> comments)
    {
        super();
        this.interp = interp;
        this.changeDir = changeDir;
        this.countOnly = countOnly;
        this.comments = comments;
    }
    
    public String getCurrentFile()
    {
        if(sourceStack.isEmpty())
        {
            return null;
        }
        return new File(interp.getWorkingDir(), 
                        sourceStack.peek()).toString();
    }

    /* (non-Javadoc)
     * @see tcl.lang.Command#cmdProc(tcl.lang.Interp, tcl.lang.TclObject[])
     */
    public void cmdProc(Interp interp, TclObject[] args) throws TclException
    {
    	
        assert interp == this.interp;
        
        if(args.length != 2)
        {
            throw new TclNumArgsException(interp, 1, args, "fileName");
        }
        
        String fileName = args[1].toString();
        boolean changedDir = false;
        if(changeDir)
        {
            File file = new File(fileName);
            if(file.getParent() != null)
            {
                this.interp.eval("pushd \"" + file.getParent().replace('\\', '/') + "\"");
                fileName = file.getName();
                changedDir = true;
            }
        }
        
        final String fullPath = new File(this.interp.getWorkingDir(), fileName).getPath();
        
        sourceStack.push(fileName);
        Path path = new Path(fullPath);
        // TODO: fix
        // this.interp.addVisitedFile(path); 
        File externalFile = path.toFile();
        if(externalFile.exists()) {
        	//interp.evalFile(fileName);
            /*
        	if (countOnly) {
        		//SoarDatabaseUtil.countRulesFromFile(externalFile, this.interp);
        	} else {
        		SoarDatabaseUtil.importRules(externalFile, this.interp, comments);
        	}
        	*/
        }
        else
        {
            throw new TclException(interp, "File not found: " + fileName);
        }
        // In the event of an exception, the stack will not be popped so that
        // it is possible to recover the name of the file being processed when
        // the exception was thrown
        sourceStack.pop();
        if(changedDir)
        {
            this.interp.eval("popd");
        }
        
    }
}
