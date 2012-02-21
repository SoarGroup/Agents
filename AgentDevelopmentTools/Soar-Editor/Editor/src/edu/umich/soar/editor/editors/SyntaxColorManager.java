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

import java.util.HashMap;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;


/**
 * <code>SyntaxColorManager</code> manages the colors that will be used
 * during syntax highlighting and ensures that colors get properly disposed.
 *
 * @author annmarie.steichmann@soartech.com
 * @version $Revision: 578 $ $Date: 2009-06-22 13:05:30 -0400 (Mon, 22 Jun 2009) $
 */
public class SyntaxColorManager implements RGBDefinitions {

    private static HashMap<RGB,Color> colors = new HashMap<RGB,Color>();
    
    static {
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), BLOCK_COMMENT_RGB, BLOCK_COMMENT_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), INLINE_COMMENT_RGB, INLINE_COMMENT_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), COMMAND_RGB, COMMAND_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), VARIABLE_RGB, VARIABLE_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), FUNCTION_RGB, FUNCTION_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), ARROW_FG_RGB, ARROW_FG_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), ARROW_BG_RGB, ARROW_BG_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), TCL_FG_RGB, TCL_FG_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), TCL_BG_RGB, TCL_BG_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), STRING_RGB, STRING_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), FLAG_RGB, FLAG_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), DISJUNCT_RGB, DISJUNCT_RGB_VALUE );
    	PreferenceConverter.setDefault( EditorsUI.getPreferenceStore(), TCL_VAR_RGB, TCL_VAR_RGB_VALUE );
    }
    
    /**
     * Reset all the colors back to their defaults from the preferences.
     */
    public static void reset() {
    	
    	EditorsUI.getPreferenceStore().setToDefault( BLOCK_COMMENT_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( INLINE_COMMENT_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( COMMAND_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( VARIABLE_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( FUNCTION_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( ARROW_FG_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( ARROW_BG_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( TCL_FG_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( STRING_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( STRING_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( FLAG_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( DISJUNCT_RGB );
    	EditorsUI.getPreferenceStore().setToDefault( TCL_VAR_RGB );
    }
    
    /**
     * Dispose of color resources.
     */
    public static void dispose() {
        
        for ( RGB rgb: colors.keySet() ) {
            Color color = colors.get( rgb );
            color.dispose();
        }
        colors.clear();
    }
    
    /**
     * @param key The <code>RGB</code> preferences key
     * @return The associated <code>Color</code> for the given <code>RGB</code>
     */
    public static Color getColor( final String key ) {
        
    	RGB rgb = PreferenceConverter.getColor( EditorsUI.getPreferenceStore(), key );
        Color color = colors.get( rgb );
        if ( color == null ) {
            color = new Color( Display.getCurrent(), rgb );
            colors.put( rgb, color );
        }
        return color;
    }
    
    public static Color getForegroundColor() {
    	return getColor( AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND );
    }
    public static Color getBackgroundColor() {
    	return getColor( AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND );
    }
    public static Color getBlockCommentColor() {
    	return getColor( BLOCK_COMMENT_RGB );
    }
    public static Color getInlineCommentColor() {
    	return getColor( INLINE_COMMENT_RGB );
    }
    public static Color getCommandColor() {
    	return getColor( COMMAND_RGB );
    }
    public static Color getVariableColor() {
    	return getColor( VARIABLE_RGB );
    }
    public static Color getFunctionColor() {
    	return getColor( FUNCTION_RGB );
    }
    public static Color getArrowFgColor() {
    	return getColor( ARROW_FG_RGB );
    }
    public static Color getArrowBgColor() {
    	return getColor( ARROW_BG_RGB );
    }
    public static Color getTclFgColor() {
    	return getColor( TCL_FG_RGB );
    }
    public static Color getTclBgColor() {
    	return getColor( TCL_BG_RGB );
    }
    public static Color getStringColor() {
    	return getColor( STRING_RGB );
    }
    public static Color getFlagColor() {
    	return getColor( FLAG_RGB );
    }
    public static Color getDisjunctColor() {
    	return getColor( DISJUNCT_RGB );
    }
    public static Color getTclVarColor() {
    	return getColor( TCL_VAR_RGB );
    }
}
