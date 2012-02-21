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
package com.soartech.soar.ide.core.ast;

import java.io.IOException;
import java.io.Reader;

/**
 * @author ray
 */
public class SoarCharStream extends SimpleCharStream
{
    public int currentOffset;
    public int beginOffset;
    public int endOffset;
    
    public SoarCharStream(Reader reader, int offset)
    {
        super(reader);
        this.currentOffset = offset;
    }

    /* (non-Javadoc)
     * @see com.soartech.soar.ide.core.ast.SimpleCharStream#readChar()
     */
    @Override
    public char readChar() throws IOException
    {
        ++currentOffset;
        return super.readChar();
    }

    /* (non-Javadoc)
     * @see com.soartech.soar.ide.core.ast.SimpleCharStream#BeginToken()
     */
    @Override
    public char BeginToken() throws IOException
    {
        char c = super.BeginToken();
        beginOffset = currentOffset;
        return c;
    }

    /* (non-Javadoc)
     * @see com.soartech.soar.ide.core.ast.SimpleCharStream#backup(int)
     */
    @Override
    public void backup(int amount)
    {
        super.backup(amount);
        currentOffset -= amount;
    }
    
    
}
