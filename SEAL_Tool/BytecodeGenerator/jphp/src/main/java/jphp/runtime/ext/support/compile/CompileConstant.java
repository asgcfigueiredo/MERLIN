package jphp.runtime.ext.support.compile;

import jphp.runtime.Memory;

public class CompileConstant {
    public final String name;
    public final Memory value;

    public CompileConstant(String name, Memory value) {
        this.name = name;
        this.value = value;
    }

    public CompileConstant(String name, Object value){
        this(name, Memory.wrap(null, value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompileConstant)) return false;

        CompileConstant that = (CompileConstant) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
