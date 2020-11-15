package attributeExtractor;

import codeSliceExtractor.VulnerabilityInfo;
import codeSliceExtractor.processJimple.codeData.Instruction;
import codeSliceExtractor.processJimple.codeData.Tag;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MainAttributeExtractor {
    private HashMap<String, VulnerabilityInfo> vulnerabilityInfos;
    private HashMap<String, ArrayList<String>> attributes;
    private HashMap<String, ArrayList<Instruction>> codeSlices;
    private String baseDir;
    private final ArrayList<String> attributesName = new ArrayList<String>(Arrays.asList("Entry Point", "Sanitization function", "Operation", "Cast", "If",
            "Substring Extraction", "String Concatenation", "Replace String", "Remove Whitespace", "Type Checking", "IsSet Source", "Pattern Control", "Whitelist",
            "Error/Exit", "Encoding", "Encryption", "Numeric Conversion", "Add Char", "SQL Query - Agg Function", "SQL Query - From clause", "SQL Query - Numeric Entry",
            "SQL Query - Complex Query"));
    private final int numAttributes = 22;


    public MainAttributeExtractor(HashMap<String, VulnerabilityInfo> vulnerabilityInfos, HashMap<String, ArrayList<Instruction>> codeSlices, String type) {
        this.vulnerabilityInfos = vulnerabilityInfos;
        this.codeSlices = codeSlices;
        this.baseDir = this.processBaseDir();
        this.attributes = this.processAttributeInfo(type);
    }

    private String processBaseDir() {
        Properties props = new Properties();
        String fileDirectory = "";
        try {
            props.load(MainAttributeExtractor.class.getClassLoader().getResourceAsStream("project.properties"));
            fileDirectory = props.getProperty("fileDirectory");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileDirectory;
    }

    private HashMap<String, ArrayList<String>> processAttributeInfo(String type) {
        type = type.substring(0, 1).toUpperCase() + type.substring(1);
        String filenameAtt = this.baseDir +  "\\src\\main\\resources\\AttributesInfo" + type + ".json";
        HashMap<String, ArrayList<String>> attributes = new HashMap<String, ArrayList<String>>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filenameAtt));
            for (int i = 0; i < jsonArray.size(); i++) {
                ArrayList<String> attrs = new ArrayList<String>();
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String attributeCategory = (String) jsonObject.get("Attribute Category");
                JSONArray attsNameJson = (JSONArray) jsonObject.get("Instructions");
                ArrayList<String> attsName = new ArrayList<String>();
                for (int j = 0; j < attsNameJson.size(); j++) {
                    attsName.add((String) attsNameJson.get(j));
                }
                attributes.put(attributeCategory, attsName);
            }
        } catch (IOException e) {
            System.out.println("The Attribute filename: " + filenameAtt + " does not exist. Needed to extract the attributes");
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return attributes;
    }

    public void extractAttributes() {
        String directory = this.baseDir + "\\output-attributes";
        JSONObject jsonObject = new JSONObject();
        int counter = 0;
        File fDir = new File(directory);
        fDir.mkdir();
        try {
            FileUtils.cleanDirectory(fDir);
        } catch (IOException e) { e.printStackTrace(); }
        HashMap<String, ArrayList<ArrayList<Integer>>> vectors = new HashMap<String, ArrayList<ArrayList<Integer>>>();
        for(String funcName : this.codeSlices.keySet()) {
            if(this.codeSlices.get(funcName)!=null) {
                String filenameAux = funcName;
                int indexNode = filenameAux.lastIndexOf("_");
                int nodeID = Integer.parseInt(filenameAux.substring(indexNode + 1));
                String filename = filenameAux.substring(0,indexNode);
                int auxFileSplit = filename.lastIndexOf("_");
                String fileWoutId = filename.substring(0, auxFileSplit) + "_" + nodeID;
                if(funcName.contains("\\")) {
                    int p = funcName.lastIndexOf("\\");
                    filename = funcName.substring(0, p);
                    filename = filename.replace("\\", ".");
                    String lastFuncname = funcName.substring(p+1);
                    filename = filename + " " + lastFuncname;
                }
                this.codeSlices.get(funcName).size();
                String vulnerabilityName = this.codeSlices.get(funcName).get(this.codeSlices.get(funcName).size()-1).getName();
                String vulnerabilityCategory = "";
                for (String vulnerability : this.vulnerabilityInfos.keySet()) {
                    if (this.vulnerabilityInfos.get(vulnerability).getSinks().contains(vulnerabilityName)) {
                        vulnerabilityCategory = vulnerability;
                    }
                }
                String fullFuncName = filename + "-" + vulnerabilityCategory.replace("/", "_") + ".csv";
                String idFile = vulnerabilityCategory.replace("/", "_") + "_" + counter +  ".csv";
                ArrayList<Integer> attributeVector = this.processAttributes(this.codeSlices.get(funcName), vulnerabilityCategory);
                boolean newVector = true;
                if(vectors.containsKey(fileWoutId)) {
                    if(vectors.get(fileWoutId).contains(attributeVector)) {
                        newVector = false;
                    }
                }
                if (newVector) {
                    try {
                        ArrayList<ArrayList<Integer>> vectorAttributes;
                        if(vectors.containsKey(fileWoutId)) {
                            vectorAttributes = vectors.get(fileWoutId);
                        }
                        else {
                            vectorAttributes = new ArrayList<ArrayList<Integer>>();
                        }
                        vectorAttributes.add(attributeVector);
                        vectors.put(fileWoutId, vectorAttributes);
                        jsonObject.put(idFile, fullFuncName);
                        counter++;
                        FileWriter fileWriter = new FileWriter(directory + "\\" + idFile);
                        ArrayList<String> attributesName = this.attributesName;
                        fileWriter.write(attributesName.get(0));
                        for(int i = 1; i < attributesName.size(); i++) {
                            String auxAttr = "," + attributesName.get(i);
                            fileWriter.write(auxAttr);
                        }
                        fileWriter.write("\n");
                        fileWriter.write(attributeVector.get(0).toString());
                        for(int i = 1; i < attributeVector.size(); i++) {
                            String auxAttr = "," + attributeVector.get(i);
                            fileWriter.write(auxAttr);
                        }
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        FileWriter file = new FileWriter(directory + "\\mapFuncId.json");
                        file.write(jsonObject.toJSONString());
                        file.close();
                    } catch (IOException e) {
                        /** ...do nothing... **/
                    }
                }
            }
        }
    }

    public ArrayList<Integer> processAttributes(ArrayList<Instruction> codeSlice, String vulnerabilityCategory) {
        ArrayList<Integer> attributeVector = new ArrayList<Integer>(Collections.nCopies(this.numAttributes, 0));
        for(Instruction inst : codeSlice) {
            if(inst.getTag() == Tag.SOURCE) {
                String[] partsNameSource = inst.getName().split("\\(");
                String sourceName = partsNameSource[0];
                if(this.vulnerabilityInfos.get(vulnerabilityCategory).getSources().contains(sourceName)) {
                    this.setEntryPoint(attributeVector);
                }
            }
            else if(inst.getTag() == Tag.FUNCTION) {
                boolean attDone = false;
                if(this.vulnerabilityInfos.get(vulnerabilityCategory).getSanFuncs().contains(inst.getName())) {
                    this.setSanFunc(attributeVector);
                    attDone = true;
                }
                if(this.attributes.get("Extract Substring") != null && !attDone) {
                    if(this.attributes.get("Extract Substring").contains(inst.getName())) {
                        this.setSubStrExt(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.get("String Concatenation") != null && !attDone) {
                    if(this.attributes.get("String Concatenation").contains(inst.getName())) {
                        this.setStrConcat(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.get("Replace String") != null && !attDone) {
                    if(this.attributes.get("Replace String").contains(inst.getName())) {
                        this.setRepStr(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.get("Remove Whitespace") != null && !attDone) {
                    if(this.attributes.get("Remove Whitespace").contains(inst.getName())) {
                        this.setRemWhiteSpace(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.get("Type Checking") != null && !attDone) {
                    if(this.attributes.get("Type Checking").contains(inst.getName())) {
                        this.setTypeCheck(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.get("IsSet Source") != null && !attDone) {
                    if(this.attributes.get("IsSet Source").contains(inst.getName())) {
                        this.setIsSetSrc(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.get("Pattern Control") != null && !attDone) {
                    if(this.attributes.get("Pattern Control").contains(inst.getName())) {
                        this.setPattControl(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.get("Whitelist") != null && !attDone) {
                    if(this.attributes.get("Whitelist").contains(inst.getName())) {
                        this.setWhitelist(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.get("Error") != null && !attDone) {
                    if(this.attributes.get("Error").contains(inst.getName())) {
                        this.setErrorRet(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.get("Encoding") != null && !attDone) {
                    if(this.attributes.get("Encoding").contains(inst.getName())) {
                        this.setEncode(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.get("Encryption") != null && !attDone) {
                    if(this.attributes.get("Encryption").contains(inst.getName())) {
                        this.setEncryption(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.keySet().contains("Numeric conversion") && !attDone) {
                    if(this.attributes.get("Numeric conversion").contains(inst.getName())) {
                        this.setNumConv(attributeVector);
                        attDone = true;
                    }
                }
                if(this.attributes.keySet().contains("Add Char") && !attDone) {
                    if(this.attributes.get("Add Char").contains(inst.getName())) {
                        this.setAddChar(attributeVector);
                    }
                }
            }
            else if(inst.getTag() == Tag.OPERATION) {
                if(inst.getName().equals("instanceof")) {
                    this.setTypeCheck(attributeVector);
                }
                else { this.setOp(attributeVector); }
            }
            else if(inst.getTag() == Tag.CAST) {
                this.setCast(attributeVector);
            }
            else if(inst.getTag() == Tag.IF) {
                this.setIf(attributeVector);
            }
            else if(inst.getTag() == Tag.RETURN) {
                this.setErrorRet(attributeVector);
            }
            else if(inst.getTag() == Tag.THROW) {
                this.setErrorRet(attributeVector);
            }
            else if(inst.getTag() == Tag.CONSTANT) {
                if(vulnerabilityCategory.startsWith("SQL Injection")) {
                    if(this.attributes.get("SQL Query - Agg Function") != null) {
                        for(String subStr : this.attributes.get("SQL Query - Agg Function")) {
                            if(inst.getName().contains(subStr)) {
                                this.setSqlAggFunc(attributeVector);
                                break;
                            }
                        }
                    }
                    if(this.attributes.get("SQL Query - From clause") != null) {
                        for(String subStr : this.attributes.get("SQL Query - From clause")) {
                            if(inst.getName().contains(subStr)) {
                                this.setSqlFrom(attributeVector);
                                break;
                            }
                        }
                    }
                    if(this.attributes.get("SQL Query - Numeric Entry") != null) {
                        for(String subStr : this.attributes.get("SQL Query - Numeric Entry")) {
                            if(inst.getName().contains(subStr)) {
                                this.setSqlNumEntry(attributeVector);
                                break;
                            }
                        }
                    }
                    if(this.attributes.get("SQL Query - Complex Query") != null) {
                        for(String subStr : this.attributes.get("SQL Query - Complex Query")) {
                            if(inst.getName().contains(subStr)) {
                                this.setSqlComplex(attributeVector);
                                break;
                            }
                        }
                    }
                }

            }
        }

        return attributeVector;
    }

    /**Entry Point corresponds to the first index on the attribute vector**/
    private void setEntryPoint(ArrayList<Integer> attributeVector) {
        attributeVector.set(0, 1);
    }

    /**Sanitization function corresponds to the second index on the attribute vector**/
    private void setSanFunc(ArrayList<Integer> attributeVector) {
        attributeVector.set(1, 1);
    }

    /**Operation instructions correspond to the third index on the attribute vector**/
    private void setOp(ArrayList<Integer> attributeVector) {
        attributeVector.set(2, 1);
    }

    /**Cast instructions correspond to the fourth index on the attribute vector**/
    private void setCast(ArrayList<Integer> attributeVector) {
        attributeVector.set(3, 1);
    }

    /**If instructions correspond to the fifth index on the attribute vector**/
    private void setIf(ArrayList<Integer> attributeVector) {
        attributeVector.set(4, 1);
    }

    /**Substring Extraction function corresponds to the sixth index on the attribute vector**/
    private void setSubStrExt(ArrayList<Integer> attributeVector) {
        attributeVector.set(5, 1);
    }

    /**String Concatenation function corresponds to the seventh index on the attribute vector**/
    private void setStrConcat(ArrayList<Integer> attributeVector) {
        attributeVector.set(6, 1);
    }

    /**Replace String function corresponds to the eighth index on the attribute vector**/
    private void setRepStr(ArrayList<Integer> attributeVector) {
        attributeVector.set(7, 1);
    }

    /**Remove Whitespace function corresponds to the ninth index on the attribute vector**/
    private void setRemWhiteSpace(ArrayList<Integer> attributeVector) {
        attributeVector.set(8, 1);
    }

    /**Type Checking function corresponds to the tenth index on the attribute vector**/
    private void setTypeCheck(ArrayList<Integer> attributeVector) {
        attributeVector.set(9, 1);
    }

    /**IsSet Entry Point instruction corresponds to the eleventh index on the attribute vector**/
    private void setIsSetSrc(ArrayList<Integer> attributeVector) {
        attributeVector.set(10, 1);
    }

    /**Pattern Control function corresponds to the twelfth index on the attribute vector**/
    private void setPattControl(ArrayList<Integer> attributeVector) {
        attributeVector.set(11, 1);
    }

    /**Whitelist function corresponds to the thirteenth index on the attribute vector**/
    private void setWhitelist(ArrayList<Integer> attributeVector) {
        attributeVector.set(12, 1);
    }

    /**Error/Return isntruction corresponds to the fourteenth index on the attribute vector**/
    private void setErrorRet(ArrayList<Integer> attributeVector) {
        attributeVector.set(13, 1);
    }

    /**Encoding function corresponds to the fifteenth index on the attribute vector**/
    private void setEncode(ArrayList<Integer> attributeVector) {
        attributeVector.set(14, 1);
    }

    /**Encryption function corresponds to the sixteenth index on the attribute vector**/
    private void setEncryption(ArrayList<Integer> attributeVector) {
        attributeVector.set(15, 1);
    }

    /**Numeric Conversion function corresponds to the seventeenth index on the attribute vector**/
    private void setNumConv(ArrayList<Integer> attributeVector) {
        attributeVector.set(16, 1);
    }

    /**Add Char function corresponds to the eighteenth index on the attribute vector**/
    private void setAddChar(ArrayList<Integer> attributeVector) {
        attributeVector.set(17, 1);
    }

    /**SQL Query - Aggregation function corresponds to the nineteenth index on the attribute vector**/
    private void setSqlAggFunc(ArrayList<Integer> attributeVector) {
        attributeVector.set(18, 1);
    }

    /**SQL Query - From Clause corresponds to the twentieth index on the attribute vector**/
    private void setSqlFrom(ArrayList<Integer> attributeVector) {
        attributeVector.set(19, 1);
    }

    /**SQL Query - Numeric Entry clause corresponds to the twenty first index on the attribute vector**/
    private void setSqlNumEntry(ArrayList<Integer> attributeVector) {
        attributeVector.set(20, 1);
    }

    /**SQL Query - ComplexQuery corresponds to the twenty second index on the attribute vector**/
    private void setSqlComplex(ArrayList<Integer> attributeVector) {
        attributeVector.set(21, 1);
    }
}
