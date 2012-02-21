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

public final class Action implements Serializable {

	private static final long serialVersionUID = 8159976944781150629L;

	// Data Members
	private VarAttrValMake d_varAttrValMake;
	private FunctionCall d_functionCall;
	
	// Constructors
	public Action(VarAttrValMake varAttrValMake) {
		d_varAttrValMake = varAttrValMake;
	}
	
	public Action(FunctionCall functionCall) {
		d_functionCall = functionCall;
	}
	// Accessors
	public final boolean isVarAttrValMake() {
		return d_varAttrValMake != null;
	}
	
	public final VarAttrValMake getVarAttrValMake() {
		if(!isVarAttrValMake())
			throw new IllegalArgumentException("Not Variable Attribute Value Make");
		else
			return d_varAttrValMake;
	}
	
	public final FunctionCall getFunctionCall() {
		if(isVarAttrValMake()) 
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
        return isVarAttrValMake() ? d_varAttrValMake.toString() : 
                                    d_functionCall.toString();
    }
    
    
}
