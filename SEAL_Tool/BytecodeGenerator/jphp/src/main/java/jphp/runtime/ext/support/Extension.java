package jphp.runtime.ext.support;

import jphp.runtime.ext.support.compile.CompileConstant;
import jphp.runtime.ext.support.compile.CompileFunctionSpec;
import jphp.runtime.ext.support.compile.ConstantsContainer;
import jphp.runtime.ext.support.compile.FunctionsContainer;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.Information;
import jphp.runtime.annotation.Reflection;
import jphp.runtime.env.CompileScope;
import jphp.runtime.env.Environment;
import jphp.runtime.exceptions.CriticalException;
import jphp.runtime.ext.java.JavaException;
import jphp.runtime.ext.support.compile.*;
import jphp.runtime.lang.BaseWrapper;
import jphp.runtime.lang.IObject;

import java.util.*;

abstract public class Extension {
    public enum Status { EXPERIMENTAL, BETA, STABLE, LEGACY, ZEND_LEGACY, DEPRECATED }

    public interface Extensible {
        Extension getExtension();
    }

    protected final Map<String, CompileConstant> constants = new LinkedHashMap<>(300);
    protected final Map<String, CompileFunctionSpec> functions = new LinkedHashMap<>(300);
    protected final Map<String, Class<?>> classes = new LinkedHashMap<>(300);

    public String getName() {
        return getClass().getName();
    }

    public String[] getPackageNames() {
        return new String[0];
    }

    public String getVersion() {
        return Information.CORE_VERSION;
    }

    abstract public Status getStatus();

    @Deprecated
    public String[] getRequiredExtensions(){
        return new String[0];
    }

    @Deprecated
    public String[] getOptionalExtensions(){
        return new String[0];
    }

    @Deprecated
    public String[] getConflictExtensions(){
        return new String[0];
    }

    public Map<String, String> getINIEntries(){
        return new HashMap<>();
    }

    abstract public void onRegister(CompileScope scope);

    public void onLoad(Environment env){
        // nop
    }

    public Map<String, CompileConstant> getConstants() {
        return constants;
    }

    public Map<String, CompileFunctionSpec> getFunctions() {
        return functions;
    }

    public Collection<Class<?>> getClasses() {
        return classes.values();
    }

    @Deprecated
    public void registerNativeClass(CompileScope scope, Class<?> clazz) {
        registerClass(scope, clazz);
    }

    public void registerClass(CompileScope scope, Class<?> clazz) {
        registerClass(scope, new Class[] { clazz });
    }

    public void registerClass(CompileScope scope, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (BaseWrapper.class.isAssignableFrom(clazz) && !clazz.isAnnotationPresent(Reflection.NotWrapper.class)) {
                throw new CriticalException("Please use registerWrapperClass() method instead of this for wrapper classes");
            }

            if (this.classes.put(clazz.getName(), clazz) != null) {
                throw new CriticalException("Class already registered - " + clazz.getName());
            }
        }
    }

    public <T> void registerWrapperClass(CompileScope scope, Class<T> clazz, Class<? extends BaseWrapper> wrapperClass) {
        if (classes.put(clazz.getName(), wrapperClass) != null) {
            throw new CriticalException("Class already registered - " + clazz.getName());
        }

        MemoryOperation.registerWrapper(clazz, wrapperClass);
    }

    public void registerMemoryOperation(Class<? extends MemoryOperation> clazz) {
        try {
            MemoryOperation.register(clazz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CriticalException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void registerJavaException(CompileScope scope, Class<? extends JavaException> javaClass,
                                      Class<? extends Throwable>... classes) {
        registerClass(scope, javaClass);
        if (classes != null) {
            for (Class<? extends Throwable> el : classes) {
                scope.registerJavaException(javaClass, el);
            }
        }
    }

    public void registerJavaExceptionForContext(CompileScope scope, Class<? extends JavaException> javaClass,
                                                Class<? extends IObject> context) {
        registerClass(scope, javaClass);
        scope.registerJavaExceptionForContext(javaClass, context);
    }

    public void registerConstants(ConstantsContainer container){
        for(CompileConstant constant : container.getConstants()){
            constants.put(constant.name, constant);
        }
    }

    public void registerFunctions(FunctionsContainer container){
        for(CompileFunctionSpec function : container.getFunctionSpecs()){
            functions.put(function.getLowerName(), function);
        }
    }
}
