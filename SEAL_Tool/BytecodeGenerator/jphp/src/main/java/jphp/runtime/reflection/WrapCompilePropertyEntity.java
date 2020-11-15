package jphp.runtime.reflection;


import jphp.runtime.Memory;
import jphp.runtime.env.Context;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.lang.BaseWrapper;

import java.lang.reflect.Field;

public class WrapCompilePropertyEntity extends CompilePropertyEntity {
    public WrapCompilePropertyEntity(Context context, Field field) {
        super(context, field);
    }

    @Override
    public Memory assignValue(Environment env, TraceInfo trace, Object object, String name, Memory value) {
        return super.assignValue(env, trace, ((BaseWrapper)object).getWrappedObject(), name, value);
    }

    @Override
    public Memory getValue(Environment env, TraceInfo trace, Object object) {
        return super.getValue(env, trace, ((BaseWrapper)object).getWrappedObject());
    }
}
