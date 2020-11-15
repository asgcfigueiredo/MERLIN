package jphp.runtime.ext;

import jphp.runtime.ext.core.classes.net.WrapNetAddress;
import jphp.runtime.ext.core.classes.stream.Stream;
import jphp.runtime.ext.net.WrapNetStream;
import jphp.runtime.ext.net.WrapProxy;
import jphp.runtime.ext.net.WrapURL;
import jphp.runtime.ext.net.WrapURLConnection;
import jphp.runtime.ext.support.Extension;
import jphp.runtime.env.CompileScope;
import jphp.runtime.env.Environment;

import java.net.*;

public class NetExtension extends Extension {
    public final static String NAMESPACE = "php\\net\\";

    @Override
    public String getVersion() {
        return "~";
    }

    @Override
    public Status getStatus() {
        return Status.STABLE;
    }

    @Override
    public String[] getPackageNames() {
        return new String[] { "std", "net" };
    }

    @Override
    public void onRegister(CompileScope scope) {
        registerClass(scope, WrapNetStream.class);

        registerWrapperClass(scope, Proxy.class, WrapProxy.class);
        registerWrapperClass(scope, URLConnection.class, WrapURLConnection.class);
        registerWrapperClass(scope, URL.class, WrapURL.class);
        registerWrapperClass(scope, InetAddress.class, WrapNetAddress.class);
    }

    @Override
    public void onLoad(Environment env) {
        Stream.registerProtocol(env, "http", WrapNetStream.class);
        Stream.registerProtocol(env, "https", WrapNetStream.class);
        Stream.registerProtocol(env, "ftp", WrapNetStream.class);
    }
}
