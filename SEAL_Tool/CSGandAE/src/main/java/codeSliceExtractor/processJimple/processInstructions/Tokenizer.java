package codeSliceExtractor.processJimple.processInstructions;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Tokenizer {
    //includes assignment operator
    private String[] operators = {"+", "-", "*", "/", "%", "&", "|", "^", ">", "<",
                                  "--", "++", "=",  "+=", "-=", "*=", "/=", "%=",
                                  "&=", "|=", "^=", ">>=", "<<=", "==", "!=", ">=",
                                  "<=", "&&", "||", "!", "instanceof"};
    private String[] delimiters = {".", ",", " ", "(", ")", ":="};
    private List<String> listOperators;
    private List<String> listDelimiters;

    public Tokenizer() {
        this.listOperators = Arrays.asList(operators);
        this.listDelimiters = Arrays.asList(delimiters);
    }

    public List<String> getListOperators() { return this.listOperators; }

    /**
     * Process instructions from control flow graph. It divides them in tokens: constants, vars, operators, delimiters
     * @param instruction
     * @return
     */
    public ArrayList<String> splitInstruction(String instruction) {
        ArrayList<String> tokenInstruction = new ArrayList<String>();
        char c;
        String token = "";
        boolean constant = false;
        for (int i = 0; i < instruction.length(); i++) {
            c = instruction.charAt(i);
            if(this.listOperators.contains(String.valueOf(c)) && !constant) {
                token += String.valueOf(c);
                if (i + 1 < instruction.length()) {
                    if (this.listOperators.contains(String.valueOf( instruction.charAt(i+1)))) {
                        i++;
                        c = instruction.charAt(i);
                        token += String.valueOf(c);
                        if (i+1 < instruction.length()) {
                            if (this.listOperators.contains(String.valueOf(instruction.charAt(i+1)))) {
                                i++;
                                c = instruction.charAt(i);
                                token += String.valueOf(c);
                            }
                        }
                    }
                }
                tokenInstruction.add(token);
                token = "";
            }

            else if(this.listDelimiters.contains(String.valueOf(c)) && !constant) {
                if(token != "") {
                    tokenInstruction.add(token);
                }
                tokenInstruction.add(String.valueOf(c));
                token = "";
            }

            else if(c == ':' && !constant) {
                if (i + 1 < instruction.length()) {
                    if (instruction.charAt(i+1) == '=') {
                        tokenInstruction.add(":=");
                        if(token != "") {
                            tokenInstruction.add(token);
                        }
                        i++;
                    }
                    else {
                        token += String.valueOf(c);
                    }
                }

            }
            else if (c == '\\' && constant) {
                if (i + 1 < instruction.length()) {
                    if (instruction.charAt(i+1) == '\"') {
                        token += "\'";
                        //This is not the normal behavior, but for some reason the digraph replaces all \" by only ".
                        //So when we have \\\\" this will be replaced by \\\" and this originates a bug
                        //Note: I actually have no idea what is going on with constants, but this seems to work
                        i++;
                        if (i + 1 < instruction.length()) {
                            if (instruction.charAt(i+1) == '\"') {
                                constant = false;
                                i++;
                            }
                            else if(this.listDelimiters.contains(String.valueOf(instruction.charAt(i+1)))) { i--; }
                        }
                        else { constant = false; }
                    }
                    else if (instruction.charAt(i+1) == '\'' && token.charAt(0) == '\'') {
                        token += "\'";
                        constant = false;
                        i++;
                    }
                }
            }
            else if((c == '\"' && !constant) || (c == '\'' && !constant)) {
                constant = true;
                token += String.valueOf(c);
            }
            else if((c == '\"' && constant) || (c == '\'' && constant)) {
                token += String.valueOf(c);
                if (i + 1 < instruction.length()) {
                    if (this.listDelimiters.contains(String.valueOf(instruction.charAt(i+1)))) {
                        tokenInstruction.add(token);
                        constant = false;
                        token = "";
                    }
                }
            }
            else {
                token += String.valueOf(c);
            }
        }
        if (token != "") {
            tokenInstruction.add(token);
        }
        return tokenInstruction;
    }

    /**
     * Process the functions from jimple file
     * @param line
     * @return
     */
    public ArrayList<String> splitLine(String line) {
        ArrayList<String> tokensLine = new ArrayList<String>();
        boolean constant = false;
        char c;
        String token = "";

        for (int i = 0; i < line.length(); i++) {
            c = line.charAt(i);

            if((c == ' ' || c == '<' || c == '>' || c == ':' || c == '(' || c == ')') && !constant) {
                if(!token.equals("")) {
                    tokensLine.add(token);
                    token = "";
                }
                //to avoid inserting multiple spaces
                if (tokensLine.size() > 0) {
                    if (!(c == ' ' && tokensLine.get(tokensLine.size()-1).equals(" "))) { tokensLine.add(String.valueOf(c)); }
                }
                else {
                    tokensLine.add(String.valueOf(c));
                }
            }
            else if(c == '\\' && constant) {
                if (i + 1 < line.length()) {
                    if (line.charAt(i+1) == '\"') { token += "\\\'"; }
                    if (line.charAt(i+1) == '\'') { token += "\\\'"; }
                    i++;
                }
            }

            else if((c == '\"' && !constant) || (c == '\'' && !constant)) {
                constant = true;
                token += String.valueOf(c);
            }
            else if((c == '\"' && constant) || (c == '\'' && constant)) {
                token += String.valueOf(c);
                tokensLine.add(token);
                constant = false;
                token = "";
            }
            else {
                token += String.valueOf(c);
            }
        }
            return tokensLine;
    }

    /**
     * Retrieves the name function from the jimple instruction
     * @param line
     * @return
     */
    public String retrieveFuncName(String line) {
        ArrayList<String> tokens = new ArrayList<String>();
        String token = "";
        String funcName = "";
        char c;
        boolean param = false;
        for (int i = 0; i < line.length(); i++) {
            c = line.charAt(i);
            if (c == ' ' && !param) {
                tokens.add(token);
                token = "";
            }
            else if (c == '(') {
                param = true;
                token += c;
            }
            else if (c == ')' && param) {
                token += c;
                break;
            }
            else {
                token +=c;
            }
        }
        //funcName = dataType + " " + token;
        funcName = token;
        return funcName;
    }

    /**
     *******    Extra Methods   ******
     */
    public HashMap<String, String> splitArray(String array) {
        HashMap<String, String> tokens = new HashMap<String, String>();
        String[] splitVarArray = array.split("\\[");
        String var = splitVarArray[0];
        String[] splitIndex = splitVarArray[1].split("\\]");
        String index = splitIndex[0];
        tokens.put("var", var);
        tokens.put("index", index);
        return tokens;
    }

    public boolean isConstant(String token) {
        if (!token.equals(" ") && !token.equals("null") && !token.contains("\"") && !StringUtils.isNumeric(token) &&
                !token.equals("true") && !token.equals("false") && !NumberUtils.isCreatable(token)) {
            return false;
        }
        return true;
    }
}
