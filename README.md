# MERLIN

MERLIN is a tool capable of detecting security vulnerabilities in web applications written in multiple programming languages.

Regardless  of  the  programming  language,  source  code  is translated  into  a  common  intermediate  code  representation: Jimple.  Analysis  of  the  intermediate  code  representation  results  in  a  language-independent  tool,  making  it  possible  touse  it  for  processing  web  applications  written  in  different languages,  such  as  Java,  PHP,  JavaScript,  and  Python.  For now,  we  chose  to  focus  on  code  written  in  PHP  and  Java, which are languages widely used to develop the back-end of web applications. Our approach does not require explicit coding for each vulnerability.  

Machine  learning  classifiers  are  trained  with  code samples  properly  identified  as  vulnerable  or  non-vulnerable. With  this  training,  the  classifiers  learn  which  categories  ofinstructions are associated with the presence of vulnerabilities.

## Getting Started

### Prerequisites

MERLIN was designed in a Windows environment. To run the tool is necessary Python v3, Java v8 and [maven](https://maven.apache.org/).

### Installing

Before running the main script, you need to install the required libs to you local Maven repository. In the SEAL_Tool\CSGandAE directory, you need to execute the following command:

```
mvn clean compile
```

If you want to use JPHP, you also need to execute in the SEAL_Tool\BytecodeGenerator\jphp directory the following command:

```
mvn clean compile
```
You also need to install the python dependencies needed to execute the start-script and the classifier.

## Running MERLIN

To execute the tool, you can use the start-script.py in the main directory. If you use the `-help` flag, you can understand how you can use this script. The flags available in these script are:

* --mount [path] - Directory to be mount
* --config [path] - Path for the RockFS configuration file (For example: config/safecloudfs.properties). More about the configuration file [here](doc/CONFIG_FILE.md).
* -- debug <ALL, SIMPLE, WARNING, SEVERE, INFO, FINE, FINER, FINEST> - Execute with debug log messages

## Run submodule JPHP

