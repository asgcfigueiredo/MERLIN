# MERLIN

MERLIN is a tool capable of detecting security vulnerabilities in web applications written in multiple programming languages.

Regardless  of  the  programming  language,  source  code  is translated  into  a  common  intermediate  code  representation: Jimple.  Analysis  of  the  intermediate  code  representation  results  in  a  language-independent  tool,  making  it  possible  touse  it  for  processing  web  applications  written  in  different languages,  such  as  Java,  PHP,  JavaScript,  and  Python.  For now,  we  chose  to  focus  on  code  written  in  PHP  and  Java, which are languages widely used to develop the back-end of web applications. Our approach does not require explicit coding for each vulnerability.  

Machine  learning  classifiers  are  trained  with  code samples  properly  identified  as  vulnerable  or  non-vulnerable. With  this  training,  the  classifiers  learn  which  categories  ofinstructions are associated with the presence of vulnerabilities.

## Getting Started

### Prerequisites

MERLIN was designed in a Windows environment. To run the tool is necessary  Java 1.8.0, Python 3.8.0 and [Maven 3.6.0](https://maven.apache.org/).

### Installing

Before running the main script, you need to install the required libs to you local Maven repository. In the SEAL_Tool\CSGandAE directory, you need to execute the following command:

```
mvn clean install
```

If you want to use JPHP, you also need to execute in the SEAL_Tool\BytecodeGenerator\jphp directory the following command:

```
mvn clean install
```
You also need to install the python dependencies needed to execute the start-script and the classifier.

## Running MERLIN

MERLIN already has the necessary configuration files for Java e PHP. If you want to execute the tool with other programming languages, you need to provide two configuration files: one configuration file with the sources, sanitization functions and sensitive sinks and other configuration file with the information regarding the attributes. The filenames of the configuration files should be `SensitiveSinks*LanguageExtension*.json` and `AttributesInfo*LanguageExtension*.json` respectively. The configuration files should be placed in the SEAL_Tool\CSGandAE\src\main\resources directory. Take a look at the configuration files available for Java and PHP, to build a similar one for the language to be considered. 
To execute the tool, you can use the start-script.py in the main directory. If you use the `-help` flag, you can understand how you can use this script. The flags available in these script are:

* --mount [path] - Directory to be mount
* --config [path] - Path for the RockFS configuration file (For example: config/safecloudfs.properties). More about the configuration file [here](doc/CONFIG_FILE.md).
* -- debug <ALL, SIMPLE, WARNING, SEVERE, INFO, FINE, FINER, FINEST> - Execute with debug log messages

## Run submodule JPHP

To run JPHP separately, execute the following command:

```
mvn exec:java -Dexec.args="[filepath]"
```

## Run submodule Soot

To generate Jimple code, you can use the following command:

```
java -cp sootclasses-trunk-jar-with-dependencies.jar soot.Main -cp . -allow-phantom-refs -pp -f J [nameOftheClassFile]
```

To generate the CFGs, you can use the following command:
```
java -cp sootclasses-trunk-jar-with-dependencies.jar soot.Main -cp . -allow-phantom-refs -pp -dump-cfg ALL -f J [nameOftheClassFile]
```

## Run submodule CSGandAE 

To execute this submodule is necessary to provide Jimple code and the CFGs of the file. This information should be inside a folder named with the filename, the jimple code should also be named with the filename, the methods should be name with their corresponding name and should be placed inside a directory also named with the filename (the start-script produces the right format automatically). To execute the submodule CSGandAE, you can use the following command: 

```
mvn exec:java -Dexec.args="-[flags]"
```

The available flags are the following:


## Authors

* **[Alexandra Figueiredo](https://github.com/asgcfigueiredo)**
* **[Tatjana Lide]**
* **[David Matos](https://github.com/mpcorreia)**
* **[Prof. Miguel Correia](https://github.com/mpcorreia)**
