package jphp.runtime.ext.core.classes.lib;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.env.Environment;
import jphp.runtime.invoke.Invoker;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.lang.exception.BaseTypeError;
import jphp.runtime.memory.helper.ObservableMemory;
import jphp.runtime.reflection.ClassEntity;

import java.util.Set;

@Reflection.Name("php\\lib\\rx")
public class RxUtils extends BaseObject {
    public RxUtils(Environment env) {
        super(env);
    }

    public RxUtils(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature
    private Memory __construct(Environment env, Memory... args) { return Memory.NULL; }

    @Signature
    public static Memory observable() {
        return observable(Memory.NULL);
    }

    @Signature
    public static Memory observable(Memory value) {
        return new ObservableMemory(value);
    }

    @Signature
    public static Memory subscribe(Environment env, Memory observable, Memory callback) {
        Invoker invoker = Invoker.create(env, callback);

        if (observable instanceof ObservableMemory) {
            if (invoker == null) {
                env.exception(BaseTypeError.class, "Argument 2 must be callable");
                return Memory.NULL;
            }

            ((ObservableMemory) observable).addObserver(new ObservableMemory.Observer() {
                @Override
                public void update(ObservableMemory observable, Memory oldValue, Memory newValue) {
                    invoker.callNoThrow(newValue, oldValue);
                }
            }, callback);
            return Memory.NULL;
        } else {
            env.exception(BaseTypeError.class, "Argument 1 must be observable");
            return Memory.NULL;
        }
    }

    @Signature
    public static Memory unsubscribe(Environment env, Memory observable, Memory callback) {
        Invoker invoker = Invoker.create(env, callback);

        if (observable instanceof ObservableMemory) {
            if (invoker == null) {
                env.exception(BaseTypeError.class, "Argument 2 must be callable");
                return Memory.NULL;
            }

            ObservableMemory.Observer observer = ((ObservableMemory) observable).removeObserver(callback);

            return observer == null ? Memory.FALSE : Memory.TRUE;
        } else {
            env.exception(BaseTypeError.class, "Argument 1 must be observable");
            return Memory.NULL;
        }
    }

    @Signature
    public static void unsubscribeAll(Environment env, Memory observable) {
        if (observable instanceof ObservableMemory) {
            ((ObservableMemory) observable).clearObservers();
        } else {
            env.exception(BaseTypeError.class, "Argument 1 must be observable");
        }
    }

    @Signature
    public static Set<Memory> subscribers(Environment env, Memory observable) {
        if (observable instanceof ObservableMemory) {
            return ((ObservableMemory) observable).observerKeys();
        } else {
            env.exception(BaseTypeError.class, "Argument 1 must be observable");
            return null;
        }
    }
}
