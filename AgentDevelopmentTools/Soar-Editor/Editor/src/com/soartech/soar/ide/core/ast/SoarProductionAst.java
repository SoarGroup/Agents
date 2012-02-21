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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Brad Jones
 * @version 0.75 3 Mar 2000
 */
public final class SoarProductionAst implements Serializable {

    private static final long serialVersionUID = 2213831053951547710L;

    // Data Members
    private String name = "";
    private int startLine;
    private int ruleOffset;
    private String productionType = "";
    private String comment = "";
    private List<Condition> conditions = new ArrayList<Condition>();
    private List<Action> actions = new ArrayList<Action>();
    
    // Constructors
    public SoarProductionAst() {}

    // Accessors
    public final void setName(String name) {
        this.name = name;
    }

    public final void setProductionType(String productionType) {
        this.productionType = productionType;
    }

    public final void setStartLine(int start) {
        this.startLine = start;
    }

    public final void addCondition(Condition c) {
        conditions.add(c);
    }

    public final void addAction(Action a) {
        actions.add(a);
    }
    
    public final int getStartLine() {
        return startLine;
    }

    public final String getName() {
        return name;
    }

    public final String getProductionType() {
        return productionType;
    }

    /**
     * @return the actions
     */
    public List<Action> getActions()
    {
        return actions;
    }

    /**
     * @return the conditions
     */
    public List<Condition> getConditions()
    {
        return conditions;
    }
    
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return name + ":" + productionType + "\n" + conditions + "\n" + actions;
    }

    public void setRuleOffset(int ruleOffset) {
        this.ruleOffset = ruleOffset;
    }

    public int getRuleOffset() {
        return ruleOffset;
    }
}
