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

import java.io.Serializable;

public final class Pair implements Comparable, Serializable {

	private static final long serialVersionUID = -5511702947312847336L;

/////////////////////////////////////
// Data Members
/////////////////////////////////////
	private String d_string;
	
	// There are 1-indexed
	private int beginOffset;
    private int endOffset;
	
/////////////////////////////////////
// Constructors
/////////////////////////////////////
	
	public Pair(String string,int beginOffset, int endOffset) {
		d_string = string;
		this.beginOffset = beginOffset;
        this.endOffset = endOffset + 1; // TODO: This is a hack to get errors to appear correctly in Eclipse.
	}
	
	public Pair(String string) {
		this (string, -1, -1);
	}
	
/////////////////////////////////////
// Accessors
/////////////////////////////////////
	public final String getString() {
		return d_string;
	}
	
	public final int getOffset() {
		return beginOffset;
	}
	
	public final int getEndOffset() {
		return endOffset;
	}
    
    public final int getLength()
    {
        return endOffset - beginOffset;
    }
	
	public final boolean equals(Object o) {
		if(o instanceof Pair) {
			Pair p = (Pair)o;
			return d_string.equals(p.getString());
		}
		return false;
	}
	
	public final int compareTo(Object o) {
		Pair p = (Pair)o;
		return d_string.compareTo(p.getString());
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return d_string;
    }
    
    /**
     * Helper function to get a pair from an instance of
     * HasPair or HasConstant.
     */
    public static Pair getPair(Object item) {
		if (item instanceof HasPair) {
			return ((HasPair)item).getPair();
		}
		if (item instanceof HasConstant) {
			return ((HasConstant)item).getConstant().toPair();
		}
		return null;
    }
}
