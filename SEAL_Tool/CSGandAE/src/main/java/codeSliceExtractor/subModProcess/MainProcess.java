package codeSliceExtractor.subModProcess;

import codeSliceExtractor.VulnerabilityInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainProcess {
    private ArrayList<String> files;
    private String ext;
    private String filenameInfo;
    private String baseDir;

    public MainProcess(ArrayList<String> files, String fileDirectory, String filenameInfo, String ext, boolean output) {
        this.ext = ext;
        this.baseDir = processBaseDir(output, fileDirectory);
        this.filenameInfo = this.processFilenameInfo(filenameInfo, fileDirectory, ext);
        this.files = files;
        processFiles(output);
    }

    public String processBaseDir(boolean output, String fileDirectory) {
        //fileDirectory corresponds to the directory where is this module
        if(output) {
            int p1 = fileDirectory.lastIndexOf("\\");
            String auxFileDir1 = fileDirectory.substring(0,p1);
            fileDirectory = auxFileDir1;
            p1 = fileDirectory.lastIndexOf("\\");
            fileDirectory = fileDirectory.substring(0,p1);
            return fileDirectory + "\\output\\";
        }
        else {
            return fileDirectory + "\\src\\test\\resources\\input\\";
        }
    }

    /**Files with the sources, sanitization functions and sensitives sinks regarding each vulnerability**/
    public String processFilenameInfo(String filenameInfo, String fileDirectory, String ext) {
        if(filenameInfo.equals("")) {
            if (this.ext.equals("java") || this.ext.equals("jsp")) {
                return fileDirectory + "\\src\\main\\resources\\sensitiveSinksJava.json";
            }
            else if(this.ext.equals("php")) {
                return fileDirectory + "\\src\\main\\resources\\sensitiveSinksPhp.json";
            }
            else {
                ext = ext.substring(0, 1).toUpperCase() + ext.substring(1);
                System.out.println("SENSITIVE SINKS: "+ fileDirectory + "\\src\\main\\resources\\sensitiveSinks" + ext + ".json\n\n\n");
                return fileDirectory + "\\src\\main\\resources\\sensitiveSinks" + ext + ".json";
            }
        }
        return filenameInfo;
    }

    public void processFiles(boolean output) {
        if(!output) {
            processInputFiles("");
        }
    }

    /**Get the files to process inside the input directory**/
    public void processInputFiles(String auxDir) {
        String dir = this.baseDir + "\\" + auxDir;
        File file = new File(dir);
        String[] dirFiles = file.getAbsoluteFile().list();
        if(dirFiles != null) {
            for(String dirFile : dirFiles) {
                String dirAux = auxDir;
                String fullDirFilename = dir + dirFile;
                File fullDirFile = new File(fullDirFilename);
                if(fullDirFile.isDirectory() && dirFile.equals("sootOutput")) {
                    String filename = dir.substring(this.baseDir.length());
                    filename = filename.substring(1, filename.length() - 1);
                    this.files.add(filename);
                }
                else if(fullDirFile.isDirectory()) {
                    dirAux += dirFile + "\\";
                    processInputFiles(dirAux);
                }
            }
        }
        return;
    }

    public boolean filesToProcess() { return !this.files.isEmpty(); }

    public void process() {
        ProcessWebApp processWebApp;
        HashMap<String, VulnerabilityInfo> infoVulns = this.extractInfo();

        if (this.ext.equals("java") || this.ext.equals("jsp")) {
            processWebApp = ProcessJavaWepApp.getInstance();
            processWebApp.setFileType("java");
        }
        else if (this.ext.equals("php")) {
            processWebApp = ProcessPhpWebApp.getInstance();
            processWebApp.setFileType("php");
        }
        else { processWebApp = ProcessWebApp.getInstance(); }
        processWebApp.init(this.files, infoVulns, this.baseDir);
        processWebApp.process();
        processWebApp.getPottentiallyVulnCodes();
    }


    private HashMap<String, VulnerabilityInfo> extractInfo() {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = (JSONArray) parser.parse(new FileReader(this.filenameInfo));
        } catch (IOException e) {
            System.out.println("The info filename provided: " + this.filenameInfo + " does not exist");
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        HashMap<String, VulnerabilityInfo> infoVulns = new HashMap<String, VulnerabilityInfo>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String vulnerabilityName = (String) jsonObject.get("vulnerability");
            JSONArray sinksJson = (JSONArray) jsonObject.get("sinks");
            ArrayList<String> sinks = new ArrayList<String>();
            if(sinksJson != null) {
                for (int j = 0; j < sinksJson.size(); j++) {
                    sinks.add((String) sinksJson.get(j));
                }
            }
            else {
                System.out.println("No sensitive sinks were provided for the following vulnerability: " + vulnerabilityName);
            }
            JSONArray sourcesJson = (JSONArray) jsonObject.get("sources");
            ArrayList<String> sources = new ArrayList<String>();
            if(sourcesJson != null) {
                for (int j = 0; j < sourcesJson.size(); j++) {
                    sources.add((String) sourcesJson.get(j));
                }
            }
            else {
                System.out.println("No sources were provided for the following vulnerability: " + vulnerabilityName);
            }
            JSONArray sanJson = (JSONArray) jsonObject.get("sanitizers");
            ArrayList<String> sanFuncs = new ArrayList<String>();
            if(sanJson != null) {
                for (int j = 0; j < sanJson.size(); j++) {
                    sanFuncs.add((String) sanJson.get(j));
                }
            }
            else {
                System.out.println("No sanitization functions were provided for the following vulnerability: " + vulnerabilityName);
            }
            VulnerabilityInfo vulnerabilityInfo = new VulnerabilityInfo(vulnerabilityName, sinksJson, sourcesJson, sanFuncs);
            infoVulns.put(vulnerabilityName, vulnerabilityInfo);
        }
    return infoVulns;
    }
}
