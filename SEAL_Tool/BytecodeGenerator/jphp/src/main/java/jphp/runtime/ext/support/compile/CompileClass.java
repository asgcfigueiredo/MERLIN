package jphp.runtime.ext.support.compile;

import jphp.runtime.ext.support.Extension;
import jphp.runtime.reflection.support.ReflectionUtils;

public class CompileClass {
    protected final Class<?> nativeClass;
    protected String name;
    protected String lowerName;
    protected Extension extension;

    public CompileClass(Extension extension, Class<?> nativeClass) {
        this.nativeClass = nativeClass;
        this.extension = extension;

        this.name = ReflectionUtils.getClassName(nativeClass);
        this.lowerName = this.name.toLowerCase();
    }

    public Class<?> getNativeClass() {
        return nativeClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLowerName() {
        return lowerName;
    }

    public Extension getExtension() {
        return extension;
    }
}
