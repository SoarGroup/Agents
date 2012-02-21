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

public class PreferenceSpecifier implements Serializable {

	private static final long serialVersionUID = 7896863753738814633L;

	// Data Members
	private boolean d_isUnaryPreference;
	private RHSValue d_rhs;
	private int d_specType;
	
	// Enumeration
	public static final int ACCEPTABLE = 0;
	public static final int REJECT = 1;
	public static final int EQUAL = 2;
	public static final int GREATER = 3;
	public static final int LESS = 4;
	public static final int REQUIRE = 5;
	public static final int PROHIBIT = 6;
	public static final int AMPERSAND = 7;
	public static final int ATSIGN = 8;
	
	public static final String[] PREFERENCES = {
		"ACCEPTABLE",
		"REJECT",
		"EQUAL",
		"GREATER",
		"LESS",
		"REQUIRE",
		"PROHIBIT",
		"AMPERSAND",
		"ATSIGN"
	};
	              
	// Constructors
	protected PreferenceSpecifier(int type) {
		d_specType = type;
		d_isUnaryPreference = true;
	}
	
	protected PreferenceSpecifier(int type,RHSValue rhsval) {
		d_specType = type;
		d_isUnaryPreference = false;
		d_rhs = rhsval;
	}
	
	// Accessors
	public final boolean isUnaryPreference() {
		return d_isUnaryPreference;
	}
	
	public final int getPreferenceSpecifierType() {
		return d_specType;
	}
	
	public final RHSValue getRHS() {
		if(isUnaryPreference()) 
			throw new IllegalArgumentException("Not a binary preference");
		else
			return d_rhs;
	}
}
