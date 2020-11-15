package jphp.runtime.reflection;

import jphp.runtime.ext.support.Extension;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.lang.BaseWrapper;
import jphp.runtime.lang.IObject;

public class WrapCompileMethodEntity extends CompileMethodEntity {
    public WrapCompileMethodEntity(Extension extension) {
        super(extension);
    }

    @Override
    public Memory invokeDynamic(IObject _this, Environment env, TraceInfo trace, Memory... arguments) throws Throwable {
        BaseWrapper aThis = (BaseWrapper) _this;
        return super.invokeDynamic(aThis == null ? null : aThis.getWrappedObject(), env, trace, arguments);
    }
}
