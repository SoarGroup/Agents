package edu.umich.soar.editor.editors.datamap;

import org.eclipse.core.resources.IFile;

/**
 * Represents an inconsistancy between a row and
 * a datamap.
 * 
 * @author miller
 *
 */
public class DatamapInconsistency {
	public IFile datamap;
	public IFile file;
	public String message;
	public int offset; // 0-indexed
	public int line;
	public int column;
	
	public DatamapInconsistency(IFile datamap, IFile file, String message, int offset) {
		this.datamap = datamap;
		this.file = file;
		this.message = message;
		this.offset = offset - 1; // Convert from 1-indexed
		// setColumnAndRow();
		//addExtraLines();
	}

	public DatamapInconsistency(IFile file, String message, int line) {
		this.datamap = null;
		this.file = file;
		this.message = message;
		this.offset = -1; // Convert from 1-indexed
		this.line = line;
		if (line == -1 && message.startsWith("Lexical error at line ")) {
			int lineIndex = message.indexOf("line ") + 5;
			int columnWordIndex = message.indexOf("column ");
			int columnIndex = columnWordIndex + 7;
			int periodIndex = message.indexOf('.');
			int parsedLine = Integer.parseInt(message.substring(lineIndex, columnWordIndex - 2));
			int parsedColumn = Integer.parseInt(message.substring(columnIndex, periodIndex));
			this.line = parsedLine;
			this.column = parsedColumn;
			this.message = message.substring(periodIndex + 2);
		}
		//addExtraLines();
	}

	/*
	private void setColumnAndRow() {
		
		String text = SoarDatabaseRow.removeComments(rule.getText());
		String trimmed = text.trim();
		int beginIndex = 0;
		if (trimmed.startsWith("sp") || trimmed.startsWith("gp")) {
			beginIndex = text.indexOf("sp") + 2;
			if (beginIndex == 1) beginIndex = text.indexOf("gp") + 2;
			trimmed = text.substring(beginIndex).trim();
			if (trimmed.startsWith("{")) {
				beginIndex = text.indexOf("{", beginIndex) + 1;
				trimmed = text.substring(beginIndex).trim();
			} else {
				// error
				column = -1;
				line = -1;
				return;
			}
		} else {
			// error
			column = -1;
			line = -1;
			return;
		}
		offset += beginIndex;
		
		column = 0;
		line = 1;
		int i = 0;
		while (i < offset) {
			int next = text.indexOf('\n', i + 1);
			if (next == -1 || next > offset) {
				break;
			}
			++line;
			i = next;
		}
		column = offset - i - 1;
	}
	*/
	
	/*
	private void addExtraLines() {
		String ruleText = rule.getText();
	}
	*/
	
	public String toString() {
		if (datamap != null) {
			return datamap.getName() + ":" + (line >= 0 ? line : "") + " (" + datamap.getName() + ") " + message;
		}
		return file.getName() + ":" + (line >= 0 ? line : "") + " " + message;
	}
}