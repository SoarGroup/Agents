package edu.umich.soar.editor.editors;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;

import tcl.lang.RelocatableTclInterpreter;
import tcl.lang.TclException;

import com.soartech.soar.ide.core.ast.ParseException;
import com.soartech.soar.ide.core.ast.SoarParser;
import com.soartech.soar.ide.core.ast.SoarProductionAst;
import com.soartech.soar.ide.core.ast.Token;
import com.soartech.soar.ide.core.ast.TokenMgrError;
import com.soartech.soar.ide.core.tcl.SoarModelTclCommands;
import com.soartech.soar.ide.core.tcl.TclSpCommand;

public class SoarRuleParser
{

    public static class SoarParseError
    {
        public String message;
        public int start;
        public int length;

        public SoarParseError(String message, int start, int length)
        {
            this.message = message;
            this.start = start;
            this.length = length;
        }
    }

    public static void parseRules(String text, IProgressMonitor monitor, List<SoarParseError> errors, List<SoarProductionAst> asts, String basePath,
            boolean sourceCommands)
    {
        RelocatableTclInterpreter interp = new RelocatableTclInterpreter();
        if (basePath != null)
        {
            try
            {
                interp.setWorkingDir(basePath);
            }
            catch (TclException e)
            {
                e.printStackTrace();
            }
        }
        ArrayList<String> comments = new ArrayList<String>();
        SoarModelTclCommands.installSoarCommands(interp, false, false, comments, sourceCommands);

        try
        {
            interp.eval(text);
        }
        catch (TclException e1)
        {
            int charOffset = getCharOffset(text, interp.getErrorLine());
            String errorMessage = interp.getResult().toString();
            errors.add(new SoarParseError(errorMessage, charOffset, 0));
        }

        TclSpCommand spCommand = (TclSpCommand) interp.getCommand("sp");
        for (String rule : spCommand.getRules())
        {
            rule = rule.substring(4, rule.length() - 1);
            Scanner s = new Scanner(rule);
            String ruleName = s.next();
            
            // int ruleOffset = text.indexOf(ruleName);
            int ruleOffset = -1;
            Pattern pattern = Pattern.compile("sp\\s*\\{\\s*(" + Pattern.quote(ruleName) + ")\\s");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find())
            {
                ruleOffset = matcher.start(1);
            }
            
            // rule = removeComments(rule);

            // Parse the rule into an AST.
            StringReader reader = new StringReader(rule);
            SoarParser parser = new SoarParser(reader);
            try
            {
                SoarProductionAst ast = parser.soarProduction();
                ast.setRuleOffset(ruleOffset);
                asts.add(ast);
            }
            catch (ParseException e)
            {
                // e.printStackTrace();
                String message = e.getLocalizedMessage();
                Token currentToken = e.currentToken;
                Token errorToken = currentToken.next;

                // Get the range of the error, based on the string
                // being parsed and the given column and row
                int start = 0;
                for (int i = 1; i < errorToken.beginLine;)
                {
                    char c = rule.charAt(start);
                    if (c == '\n')
                    {
                        ++i;
                    }
                    ++start;
                }

                // -1 for columns counting starting with 1
                start += errorToken.beginColumn - 1;

                int length = 1;
                errors.add(new SoarParseError(message, start + ruleOffset, length));
            }
            catch (TokenMgrError e)
            {
                // e.printStackTrace();

                String message = e.getLocalizedMessage();
                assert (message.startsWith("Lexical error at line "));
                int lineIndex = message.indexOf("line ") + 5;
                int columnWordIndex = message.indexOf("column ");
                int columnIndex = columnWordIndex + 7;
                int periodIndex = message.indexOf('.');
                int parsedLine = Integer.parseInt(message.substring(lineIndex, columnWordIndex - 2));
                int parsedColumn = Integer.parseInt(message.substring(columnIndex, periodIndex));
                message = message.substring(periodIndex + 2);
                // Get the range of the error, based on the string
                // being parsed and the given column and row
                int start = 0;
                for (int i = 1; i < parsedLine;)
                {
                    char c = rule.charAt(start);
                    if (c == '\n')
                    {
                        ++i;
                    }
                    ++start;
                }

                // -1 for columns counting starting with 1
                start += parsedColumn - 1;

                int length = 1;
                errors.add(new SoarParseError(message, start + ruleOffset, length));

                /**
                 * Get the range of the error, based on the string being parsed
                 * and the given column and row
                 */
                /*
                 * int start = 0; for (int i = 1; i < e.getErrorLine();) { char
                 * c = rule.charAt(start); if (c == '\n') { ++i; } ++start; }
                 */
                /*
                 * int start = 0; for (int i = 1; i < e.getErrorLine();) { char
                 * c = parseText.charAt(start); if (c == '\n') { ++i; } ++start;
                 * }
                 * 
                 * start += beginIndex; // -1 for columns counting starting with
                 * 1 start += e.getErrorColumn() - 1;
                 * 
                 * int length = 2; // (errorToken.endOffset - //
                 * errorToken.beginOffset) + 1; if (input != null) {
                 * input.addProblem(SoarProblem.createError(message, start,
                 * length)); }
                 */
            }

            // Add child triples to this rule.

            if (monitor != null)
            {
                monitor.worked(1);
            }
        }
    }

    private static int getCharOffset(String text, int lineOffset)
    {
        int ret = 0;
        for (byte b : text.getBytes())
        {
            if (b == '\n')
            {
                --lineOffset;
                if (lineOffset == 0)
                {
                    return ret;
                }
            }
            ++ret;
        }
        return 0;
    }

    public static String removeComments(String rule)
    {
        return rule;
        /*
         * StringBuilder sb = new StringBuilder(); Scanner s = new
         * Scanner(rule); while (s.hasNextLine()) { String line = s.nextLine();
         * if (!line.trim().startsWith("#")) { sb.append(line + '\n'); } }
         * return sb.toString();
         */
    }
}
