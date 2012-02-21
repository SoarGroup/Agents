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

import java.util.Stack;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.RelocatableTclInterpreter;
import tcl.lang.TclException;
import tcl.lang.TclNumArgsException;
import tcl.lang.TclObject;

/**
 * @author ray
 */
public class TclWorkingDirectoryCommands
{
    private RelocatableTclInterpreter interp;
    private Stack<String> directoryStack = new Stack<String>();
    private Pushd pushd = new Pushd();
    private Popd popd = new Popd();
    
    public TclWorkingDirectoryCommands(RelocatableTclInterpreter interp)
    {
        this.interp = interp;
        directoryStack.push(this.interp.getWorkingDir().getPath());
        install();
    }
    
    public void install()
    {
        interp.createCommand("pushd", pushd);
        interp.createCommand("popd", popd);
    }
    
    public void uninstall()
    {
        interp.deleteCommand("pushd");
        interp.deleteCommand("popd");
    }
    
    public void reset()
    {
        while(directoryStack.size() > 1)
        {
            directoryStack.pop();
        }
    }
    
    private void pushd(TclObject[] args) throws TclException
    {
        if(args.length != 2)
        {
            throw new TclNumArgsException(interp, 1, args, "dir");
        }
        
        String dir = args[1].toString();
        
        directoryStack.push(interp.getWorkingDir().getPath());
        interp.setWorkingDir(dir);
        interp.setResult(interp.getWorkingDir().getPath());
    }
    
    private void popd(TclObject[] args) throws TclException
    {
        if(args.length != 1)
        {
            throw new TclNumArgsException(interp, 1, args, "");
        }
        
        if(!directoryStack.isEmpty())
        {
            interp.setWorkingDir(directoryStack.pop());
        }
        else
        {
            throw new TclException(interp, "Directory stack is empty"); 
        }
    }
    
    private class Pushd implements Command
    {

        /* (non-Javadoc)
         * @see tcl.lang.Command#cmdProc(tcl.lang.Interp, tcl.lang.TclObject[])
         */
        public void cmdProc(Interp interp, TclObject[] args) throws TclException
        {
            assert interp == TclWorkingDirectoryCommands.this.interp;
            pushd(args);
        }
        
    }
    
    private class Popd implements Command
    {

        /* (non-Javadoc)
         * @see tcl.lang.Command#cmdProc(tcl.lang.Interp, tcl.lang.TclObject[])
         */
        public void cmdProc(Interp interp, TclObject[] args) throws TclException
        {
            assert interp == TclWorkingDirectoryCommands.this.interp;
            popd(args);
        }
        
    }
    

}
