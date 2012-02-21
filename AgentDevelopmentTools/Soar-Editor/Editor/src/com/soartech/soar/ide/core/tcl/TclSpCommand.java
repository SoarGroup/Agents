package com.soartech.soar.ide.core.tcl;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;

public class TclSpCommand implements Command {
	
	int numRulesCollected = 0;
	ArrayList<String> rules;
	ArrayList<String> comments;
	boolean countOnly;
	IProgressMonitor monitor;
		
	public TclSpCommand() {
		this(false);
	}
	
	public TclSpCommand(boolean countOnly) {
		this.countOnly = countOnly;
		rules = new ArrayList<String>();
	}
	
	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
	
	public IProgressMonitor getMonitor() {
		return monitor;
	}
	
	@Override
	public void cmdProc(Interp interp, TclObject[] argv) throws TclException {
		StringBuffer buff = new StringBuffer();
		if (!countOnly) {
			for (String comment : comments) {
				buff.append(comment + '\n');
			}
			comments.clear();
			String rule = argv[0].toString() + " {" + argv[1].toString().trim() + "\n}";
			rules.add(buff.toString() + rule);
		}
		++numRulesCollected;
	}
	
	public static void main(String[] args) {
		Interp i = new Interp();
		i.createCommand("sp", new TclSpCommand());
		try {
			i.eval("puts { Hello, world! }");
			i.eval("sp { 1 }");
			i.eval("puts { line 1 }\nputs { line 2 }");
		} catch (TclException e) {
			e.printStackTrace();
		}
		i.dispose();
	}

	public int getNumRulesCollected() {
		return numRulesCollected;
	}
	
	public ArrayList<String> getRules() {
		return rules;
	}
	
	public void resetRules() {
		rules.clear();
		numRulesCollected = 0;
	}
	
	public void setComments(ArrayList<String> comments) {
		this.comments = comments;
	}

}
