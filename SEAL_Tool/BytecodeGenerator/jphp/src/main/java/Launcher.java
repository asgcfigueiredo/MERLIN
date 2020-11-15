import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;
import jphp.runtime.env.CompileScope;
import jphp.runtime.env.ConcurrentEnvironment;
import jphp.runtime.env.Context;
import jphp.runtime.env.Environment;
import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.reflection.ModuleEntity;
import org.develnext.jphp.core.compiler.jvm.JvmCompiler;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Launcher {
    protected   Environment environment;
    protected CompileScope compileScope;
    protected OutputStream out;
    private ClassLoader classLoader = Launcher.class.getClassLoader();

    public Launcher() {
        this.compileScope = new CompileScope();
        this.out = System.out;
    }

    protected void initExtensions() {
        this.environment = new ConcurrentEnvironment(compileScope, out);
        environment.setErrorFlags(ErrorType.E_ALL.value ^ ErrorType.E_NOTICE.value);
        environment.getDefaultBuffer().setImplicitFlush(true);
    }

    public InputStream getResource(String name) {
        InputStream stream = classLoader.getResourceAsStream(name);
        if (stream == null) {
            try {
                return new FileInputStream(name);
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        return stream;
    }

    protected Context getContext(String file) {
        InputStream bootstrap = getResource(file);
        if (bootstrap != null) {
            return new Context(bootstrap, file, environment.getDefaultCharset());
        } else
            return null;
    }

    public void run(String filename) {
        this.initExtensions();
        Properties props = new Properties();
        String fileDirectory = "";
        try {
            props.load(Launcher.class.getClassLoader().getResourceAsStream("project.properties"));
            fileDirectory = props.getProperty("fileDirectory");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("This is the filedirectory: " + fileDirectory    );

        String filenameOutput = fileDirectory + "\\output\\";
        String auxFuncName = "";
        File directory = new File(filenameOutput);
        directory.mkdir();
        try {
            FileUtils.cleanDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(filename.equals("")) {
            filename = fileDirectory + "\\src\\test\\resources\\index.php";
            filenameOutput = filenameOutput + "bytecode-output.class" ;
        }
        else {
            int lastIndexOf = filename.lastIndexOf(".");
            String realFilename = filename.substring(0, lastIndexOf);
            if(realFilename.contains("\\")) {
                String[] fParts = realFilename.split("\\\\");
                auxFuncName = filenameOutput + fParts[fParts.length - 1];
                filenameOutput = filenameOutput + fParts[fParts.length - 1] + ".class";
            }
            else if(realFilename.contains("/")) {
                String[] fParts = realFilename.split("/");
                auxFuncName = filenameOutput + fParts[fParts.length - 1];
                filenameOutput = filenameOutput + fParts[fParts.length - 1] + ".class";
            }
            else {
                auxFuncName = filenameOutput + realFilename;
                filenameOutput = filenameOutput + realFilename + ".class";
            }
            filename = realFilename + ".php";
        }


        try {
            FileOutputStream  myObj = new FileOutputStream(filenameOutput);
            Context cAux = this.getContext(filename);
            if(cAux != null) {
                JvmCompiler jcompiler = new JvmCompiler(environment, cAux);
                ModuleEntity m_compile = jcompiler.compile(true);
                ClassReader classReader = new ClassReader(m_compile.getData());
                StringWriter writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printWriter);
                classReader.accept(traceClassVisitor, ClassReader.SKIP_DEBUG);
                File fileAux = new File(filenameOutput);
                if(fileAux.exists()) {
                    filenameOutput = auxFuncName + "_1.class";
                    myObj = new FileOutputStream(filenameOutput);
                }
                myObj.write(m_compile.getData());
                myObj.close();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
