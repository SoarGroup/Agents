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

import org.eclipse.swt.graphics.RGB;

/**
 * <code>RGBDefinitions</code> maintains the <code>RGB</code>s to be used for
 * highlighting in the <code>SoarEditor</code>
 *
 * @author annmarie.steichmann@soartech.com
 * @version $Revision: 578 $ $Date: 2009-06-22 13:05:30 -0400 (Mon, 22 Jun 2009) $
 */
public interface RGBDefinitions {

	public static final RGB BLOCK_COMMENT_RGB_VALUE = new RGB(0, 127, 0);
	public static final RGB INLINE_COMMENT_RGB_VALUE = new RGB(0, 127, 0);
    public static final RGB COMMAND_RGB_VALUE = new RGB(127, 0, 85);
    public static final RGB VARIABLE_RGB_VALUE = new RGB(0, 0, 192);
    public static final RGB FUNCTION_RGB_VALUE = new RGB(0, 0, 255);
    public static final RGB ARROW_FG_RGB_VALUE = new RGB(0, 0, 0);
    public static final RGB ARROW_BG_RGB_VALUE = new RGB(255, 250, 205);
    public static final RGB TCL_FG_RGB_VALUE = new RGB(160, 82, 45);
    public static final RGB TCL_BG_RGB_VALUE = new RGB(222, 184, 135);
    public static final RGB STRING_RGB_VALUE = new RGB(0, 0, 255);
    public static final RGB FLAG_RGB_VALUE = new RGB(0, 0, 255);
    public static final RGB DISJUNCT_RGB_VALUE = new RGB(127, 0, 85);
    public static final RGB TCL_VAR_RGB_VALUE = new RGB(160, 82, 45);
	
	public static final String BLOCK_COMMENT_RGB = "block_comment_rgb";
	public static final String INLINE_COMMENT_RGB = "inline_comment_rgb";
	public static final String COMMAND_RGB = "command_rgb";
	public static final String VARIABLE_RGB = "variable_rgb";
	public static final String FUNCTION_RGB = "function_rgb";
	public static final String ARROW_FG_RGB = "arrow_fg_rgb";
	public static final String ARROW_BG_RGB = "arrow_bg_rgb";
	public static final String TCL_FG_RGB = "tcl_fg_rgb";
	public static final String TCL_BG_RGB = "tcl_bg_rgb";
	public static final String STRING_RGB = "string_rgb";
	public static final String FLAG_RGB = "flag_rgb";
	public static final String DISJUNCT_RGB = "disjunct_rgb";
	public static final String TCL_VAR_RGB = "tcl_var_rgb";
	
}
