package jphp.runtime.ext.core.classes.net;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.BaseWrapper;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.reflection.ClassEntity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

@Name("php\\net\\NetAddress")
public class WrapNetAddress extends BaseWrapper<InetAddress> {
    interface WrappedInterface {
        boolean isSiteLocalAddress();
        boolean isMulticastAddress();
        boolean isAnyLocalAddress();
        boolean isLoopbackAddress();
        boolean isLinkLocalAddress();

        boolean isMCGlobal();
        boolean isMCNodeLocal();
        boolean isMCLinkLocal();
        boolean isMCSiteLocal();
        boolean isMCOrgLocal();

        boolean isReachable(int timeout);
    }

    public WrapNetAddress(Environment env, InetAddress wrappedObject) {
        super(env, wrappedObject);
    }

    public WrapNetAddress(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature
    public Memory __debugInfo() {
        return ArrayMemory.ofPair("*value", __toString());
    }

    @Signature
    public static List<InetAddress> getAllByName(String host) throws UnknownHostException {
        return Arrays.asList(InetAddress.getAllByName(host));
    }

    @Signature
    public static InetAddress getByAddress(byte[] address) throws UnknownHostException {
        return InetAddress.getByAddress(address);
    }

    @Signature
    public static InetAddress getLoopbackAddress() throws UnknownHostException {
        return InetAddress.getLoopbackAddress();
    }

    @Signature
    public static InetAddress getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    @Signature
    public void __construct(String host) throws UnknownHostException {
        __wrappedObject = InetAddress.getByName(host);
    }

    @Signature
    public String hostName() {
        return getWrappedObject().getHostName();
    }

    @Signature
    public String hostAddress() {
        return getWrappedObject().getHostAddress();
    }

    @Signature
    public Memory address() {
        ArrayMemory r = new ArrayMemory();

        for (byte b : getWrappedObject().getAddress()) {
            r.add(b & 255);
        }

        return r.toConstant();
    }

    @Signature
    public String canonicalHostName() {
        return getWrappedObject().getCanonicalHostName();
    }

    @Signature
    public String __toString() {
        return getWrappedObject().toString();
    }
}
