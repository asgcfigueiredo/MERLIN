package codeSliceExtractor.processJimple.processFuncs;

import codeSliceExtractor.subModProcess.ProcessWebApp;
import codeSliceExtractor.processJimple.processInstructions.Tokenizer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Retrieves complete information regarding functions
 */
public class ProcessFuncInfo {

    private String filename;
    HashMap<String, ArrayList<JimpFuncInfo>> funcsInfo = new HashMap<String, ArrayList<JimpFuncInfo>>();
    private Tokenizer tokenizer;

    public ProcessFuncInfo(String filename) {
        this.filename = filename;
        this.tokenizer = new Tokenizer();
    }


    public HashMap<String, ArrayList<JimpFuncInfo>> process() {
        File file = new File(this.filename);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while(line != null) {
                if(line.contains("(")) {
                    /**Put func to retrieve the exact funcName**/
                    String procFuncName = this.tokenizer.retrieveFuncName(line);
                    if(this.isUselessFunc(procFuncName)) {
                        while(!line.equals("    }")) {
                            line = br.readLine();
                        }
                    }
                    else {
                        //This is not an useless function
                        ArrayList<JimpFuncInfo> funcInfo =  new ArrayList<JimpFuncInfo>();
                        while (!line.equals("")) {
                            //Starts instructions
                            line = br.readLine();
                            if(line == null) { break; }
                        }
                        //Starts with zero
                        int counter = -1;
                        if(line != null) {
                            while (!line.equals("    }")) {
                                line = br.readLine();
                                if (line == null) { break; }
                                if(!line.equals("") && !line.startsWith("     label")) {
                                    counter++;
                                    funcInfo.add(new JimpFuncInfo());
                                }

                                if(line.contains("(") && line.contains(")") && line.contains("<") && line.contains(">") &&
                                        line.contains(":")) {
                                    /** Only the lines that contain this characters are potencially functions **/
                                    ArrayList<String> tokensLine = this.tokenizer.splitLine(line);

                                    for (int i = 0; i < tokensLine.size(); i++) {
                                        if(tokensLine.get(i).equals("<")) {
                                            String[] splitVarFunc = tokensLine.get(i-1).split("\\.");
                                            i++;
                                            String funcName1 = tokensLine.get(i);
                                            //Skip the functionName1, ':', space, token, space
                                            i += 5;
                                            String funcName2 = tokensLine.get(i);
                                            //In the case the function has the following form: ola.<init>
                                            if(funcName2.equals("<")) {
                                                funcName2 = "";
                                                while (!tokensLine.get(i).equals(">")) {
                                                    funcName2 += tokensLine.get(i);
                                                    i++;
                                                }
                                                funcName2 += tokensLine.get(i);
                                            }
                                            String funcName = funcName1 + "." + funcName2;
                                            JimpFuncInfo jimpAux = funcInfo.get(counter);
                                            jimpAux.setVarFunc(splitVarFunc[0]);
                                            jimpAux.setFuncName(funcName);
                                            String paramsString = tokensLine.get(i+2);
                                            if(paramsString.equals(")")) { paramsString = ""; }
                                            jimpAux.setParamsString(paramsString);
                                            //System.out.println("--------------\nNodeID: " + counter + "\nTokensLine: " + line);
                                            funcInfo.set(counter, jimpAux);
                                            break;
                                        }
                                    }
                                }
                                else if(line.contains("(") && line.contains(")") && !line.contains(";")) {
                                    while (!line.equals("        };")) {
                                        line = br.readLine();
                                        if (line == null) {
                                            break;
                                        }
                                    }
                                }
                                this.funcsInfo.put(procFuncName, (ArrayList<JimpFuncInfo>) funcInfo.clone());
                            }
                        }
                    }
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Jimple file does not exist.");
        } catch (IOException e) {
            System.out.println("Error reading the Jimple file");
        }
        return funcsInfo;
    }

    public boolean isUselessFunc(String line) {
        ProcessWebApp processWebApp = ProcessWebApp.getInstance();
        for (String funcName : processWebApp.getNoProcessFuncs()) {
            if (line.equals(funcName)) { return true; }
        }
        return false;
    }

}
