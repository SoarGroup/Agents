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

public class Constant implements HasPair, Serializable {

	private static final long serialVersionUID = 3226865893048083615L;

	// Data Members
	private int beginOffset;
    private int endOffset;
	private int d_constType;
	private int d_intConst;
	private float d_floatConst;
	private String d_symConst;

	// Enumeration
	public static final int INTEGER_CONST = 0;
	public static final int SYMBOLIC_CONST = 1;
	public static final int FLOATING_CONST = 2;

	// Constructors
	public Constant(String symConst,int beginOffset, int endOffset) {
		this.beginOffset = beginOffset;
        this.endOffset = endOffset;
        this.d_symConst = symConst;
		d_constType = SYMBOLIC_CONST;
	}
	
	public Constant(int intConst,int beginOffset, int endOffset) {
        this.beginOffset = beginOffset;
        this.endOffset = endOffset;
		d_intConst = intConst;
		d_constType = INTEGER_CONST;
	}
	
	public Constant(float floatConst,int beginOffset, int endOffset) {
        this.beginOffset = beginOffset;
        this.endOffset = endOffset;
		d_floatConst = floatConst;
		d_constType = FLOATING_CONST;
	}
	
	
	// Accessor Functions
	public int getConstantType() {
		return d_constType;
	}
	
	public int getIntConst() {
		if(d_constType != INTEGER_CONST)
			throw new IllegalArgumentException("Not a Integer Constant");
		else
			return d_intConst;
	}
	
	public float getFloatConst() {
		if(d_constType != FLOATING_CONST)
			throw new IllegalArgumentException("Not a Floating Point Constant");
		else
			return d_floatConst;
	}
	
	public String getSymConst() {
		if(d_constType != SYMBOLIC_CONST)
			throw new IllegalArgumentException("Not a Symbolic Constant");
		else
			return d_symConst;
	}
	
	public Pair toPair() {
		return new Pair(toString(), beginOffset, endOffset);
	}
	
	public String toString() {
		switch(d_constType) {
			case INTEGER_CONST:
				return Integer.toString(d_intConst);
			case FLOATING_CONST:
				return Float.toString(d_floatConst);
			case SYMBOLIC_CONST:
				return d_symConst;
			default:
				return "Unknown Type";
		}	
	}

	@Override
	public Pair getPair() {
		return new Pair(toString(), beginOffset, endOffset);
	}
}
