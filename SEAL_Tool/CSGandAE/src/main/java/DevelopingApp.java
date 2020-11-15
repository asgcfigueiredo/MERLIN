import attributeExtractor.MainAttributeExtractor;
import codeSliceExtractor.processJimple.codeData.Instruction;
import codeSliceExtractor.subModProcess.MainProcess;
import codeSliceExtractor.subModProcess.ProcessWebApp;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

public class DevelopingApp {
    public static void main(String args[]) {
        String ext = "php";
        String filenameSensitive = "";
        ArrayList<String> files = new ArrayList<String>();
        boolean output = false;
        ArrayList<String> flags = new ArrayList<String>(Arrays.asList("-t", "-vulnInfo", "-f", "-file"));

        for (int i = 0; i < args.length; i++) {
            //Flag corresponding to the extension of the files to be processed ex: java, php
            if(args[i].equals("-t")) {
                i++;
                if(i < args.length) {
                    ext = args[i];
                }
                else {
                    System.out.println("Error: Please specify the extension of the files to process.");
                    System.exit(1);
                }
            }
            //Flag corresponding to the vulnerability information files
            else if(args[i].equals("-vulnInfo")) {
                i++;
                if(i < args.length) {
                    filenameSensitive = args[i];
                }
                else {
                    System.out.println("Error: Please specify the file with information regarding the vulnerabilities to be detected.");
                    System.exit(1);
                }
            }
            //Flag corresponding to the files to be processed
            else if (args[i].equals("-f")) {
                int j = i + 1;
                output = true;
                boolean moreProcessing = false;
                while(j < args.length) {
                    if(flags.contains(args[j])) {
                        moreProcessing = true;
                        break;
                    }
                    else {
                        files.add(args[j]);
                    }
                    j++;
                }
                if(moreProcessing) { i = j-1; }
                else { i = j; }
            }
            //Files to be processed inside the file
            else if(args[i].equals("-file")) {
                i++;
                output = true;
                if(i < args.length) {
                    String filename = args[i];
                    File file = new File(filename);
                    if(file.exists()) {
                        BufferedReader reader;
                        try {
                            reader = new BufferedReader(new FileReader(filename));
                            String line = reader.readLine();
                            while (line != null) {
                                // read next line
                                line = line.replace("\n", "");
                                files.add(line);
                                line = reader.readLine();
                            }
                        } catch (FileNotFoundException e) {
                            System.out.println("Something went wrong reading the file with the filenames to be processed");
                            System.exit(1);
                        } catch (IOException e) {
                            System.out.println("Something went wrong reading the file with the filenames to be processed");
                            System.exit(1);
                        }
                    }
                }
                else {
                    System.out.println("Please provide the file with the filenames to be processed.");
                    System.exit(1);
                }
            }
        }

        //Current File Directory
        Properties props = new Properties();
        String fileDirectory = "";
        try {
            props.load(MainProcess.class.getClassLoader().getResourceAsStream("project.properties"));
            fileDirectory = props.getProperty("fileDirectory");
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainProcess mainProcess = new MainProcess(files, fileDirectory, filenameSensitive, ext, output);
        if(mainProcess.filesToProcess()) {
            mainProcess.process();
            ProcessWebApp processWebApp = ProcessWebApp.getInstance();
            HashMap<String, ArrayList<Instruction>> potVulnCodeSlices = processWebApp.getPottentiallyVulnCodes();
            File file = new File(fileDirectory + "\\output-attributes\\");
            if(file.getAbsoluteFile().exists()) {
                try {
                    FileUtils.cleanDirectory(file.getAbsoluteFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(potVulnCodeSlices.isEmpty()) {
                System.out.println("No vulnerabilities were found");
                System.exit(0);
            }
            MainAttributeExtractor mainAttributeExtractor = new MainAttributeExtractor(processWebApp.getInfoVulns(), potVulnCodeSlices, processWebApp.getFileType());
            mainAttributeExtractor.extractAttributes();
        }
        else {
            System.out.println("No files to process");
            System.exit(1);
        }
    }
}
