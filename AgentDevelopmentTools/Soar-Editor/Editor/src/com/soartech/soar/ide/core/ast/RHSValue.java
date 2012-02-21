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

public final class RHSValue implements HasPair, Serializable {

	private static final long serialVersionUID = 8124944147017142921L;

	// Data Members
	private Constant d_constant;
	private Pair d_variable;
	private FunctionCall d_functionCall;
	
	// Constructors
	
	public RHSValue(Constant c) {
		d_constant = c;
	}
	
	public RHSValue(Pair variable) {
		d_variable = variable;
	}
	
	public RHSValue(FunctionCall functionCall) {
		d_functionCall = functionCall;
	}
	
	// Member Functions	
	public final boolean isConstant() {
		return d_constant != null;
	}
	
	public final boolean isVariable() {
		return d_variable != null;
	}
	
	public final boolean isFunctionCall() {
		return d_functionCall != null;
	}

	public final Constant getConstant() {
		if(!isConstant())
			throw new IllegalArgumentException("Not a Constant");
		else
			return d_constant;
	}
	
	public final Pair getVariable() {
		if(!isVariable()) 
			throw new IllegalArgumentException("Not a Variable");
		else
			return d_variable;
	}
	
	public final FunctionCall getFunctionCall() {
		if(!isFunctionCall())
			throw new IllegalArgumentException("Not a Function Call");
		else
			return d_functionCall;
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if(isConstant())
        {
            return d_constant.toString();
        }
        else if(isVariable())
        {
            return d_variable.toString();
        }
        else
        {
            return d_functionCall.toString();
        }
    }

	@Override
	public Pair getPair() {
		if (isVariable()) {
			return d_variable;
		}
		if (isConstant()) {
			return d_constant.getPair();
		}
		if (isFunctionCall()) {
			return d_functionCall.getPair();
		}
		return null;
	}
    
}
