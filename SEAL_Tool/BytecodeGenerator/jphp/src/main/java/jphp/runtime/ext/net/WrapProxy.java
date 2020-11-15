package jphp.runtime.ext.net;

import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.env.Environment;
import jphp.runtime.ext.NetExtension;
import jphp.runtime.lang.BaseWrapper;
import jphp.runtime.reflection.ClassEntity;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Name(NetExtension.NAMESPACE + "Proxy")
public class WrapProxy extends BaseWrapper<Proxy> {
    public WrapProxy(Environment env, Proxy wrappedObject) {
        super(env, wrappedObject);
    }

    public WrapProxy(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature
    public void __construct(Proxy.Type type, String host, int port) {
        __wrappedObject = new Proxy(type, new InetSocketAddress(host, port));
    }

    @Signature
    public String address() {
        return getWrappedObject().address().toString();
    }

    @Signature
    public Proxy.Type type() {
        return getWrappedObject().type();
    }
}
