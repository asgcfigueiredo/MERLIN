package jphp.runtime.ext;

import jphp.runtime.env.handler.ProgramShutdownHandler;
import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.ext.core.*;
import jphp.runtime.ext.core.classes.*;
import jphp.runtime.ext.core.classes.format.IniProcessor;
import jphp.runtime.ext.core.classes.format.WrapProcessor;
import jphp.runtime.ext.core.classes.lib.*;
import jphp.runtime.ext.core.classes.lib.legacy.OldBinUtils;
import jphp.runtime.ext.core.classes.lib.legacy.OldItemsUtils;
import jphp.runtime.ext.core.classes.lib.legacy.OldMirrorUtils;
import jphp.runtime.ext.core.classes.lib.legacy.OldNumUtils;
import jphp.runtime.ext.core.classes.net.WrapServerSocket;
import jphp.runtime.ext.core.classes.net.WrapSocket;
import jphp.runtime.ext.core.classes.net.WrapSocketException;
import jphp.runtime.ext.core.classes.stream.*;
import jphp.runtime.ext.core.classes.time.WrapTime;
import jphp.runtime.ext.core.classes.time.WrapTimeFormat;
import jphp.runtime.ext.core.classes.time.WrapTimeZone;
import jphp.runtime.ext.core.classes.time.WrapTimer;
import jphp.runtime.ext.core.classes.util.*;
import jphp.runtime.ext.core.reflection.*;
import jphp.runtime.ext.support.Extension;
import jphp.runtime.ext.support.compile.CompileConstant;
import jphp.runtime.lang.Closure;
import jphp.runtime.loader.sourcemap.SourceMap;
import jphp.runtime.env.CompileScope;
import jphp.runtime.env.Environment;
import jphp.runtime.ext.core.*;
import jphp.runtime.ext.core.classes.*;
import jphp.runtime.ext.core.classes.lib.*;
import jphp.runtime.ext.core.classes.stream.*;
import jphp.runtime.ext.core.classes.time.*;
import jphp.runtime.ext.core.classes.util.*;
import jphp.runtime.ext.core.reflection.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

public class CoreExtension extends Extension implements ProgramShutdownHandler {
    public final static String NAMESPACE = "php\\";

    @Override
    public String getName() {
        return "Core";
    }

    @Override
    public String[] getPackageNames() {
        return new String[]{"std", "core"};
    }

    @Override
    public Status getStatus() {
        return Status.STABLE;
    }

    @Override
    public void onRegister(CompileScope scope) {
        long t = System.currentTimeMillis();

        scope.registerProgramShutdownHandler(this);

        registerFunctions(new LangFunctions());
        registerConstants(new LangConstants());

        registerConstants(new InfoConstants());
        registerFunctions(new InfoFunctions());

        registerConstants(new MathConstants());
        registerFunctions(new MathFunctions());

        registerFunctions(new StringFunctions());

        registerConstants(new ArrayConstants());
        registerFunctions(new ArrayFunctions());

        registerConstants(new OutputConstants());
        registerFunctions(new OutputFunctions());

        // T_ERROR
        for (ErrorType el : ErrorType.values()) {
            CompileConstant compileConstant = new CompileConstant(el.name(), el.value);
            constants.put(el.name(), compileConstant);
        }

        registerJavaException(scope, WrapJavaExceptions.IllegalArgumentException.class, IllegalArgumentException.class);
        registerJavaException(scope, WrapJavaExceptions.IllegalStateException.class, IllegalStateException.class);
        registerJavaException(scope, WrapJavaExceptions.NumberFormatException.class, NumberFormatException.class);
        registerJavaException(scope, WrapJavaExceptions.InterruptedException.class, InterruptedException.class);

        registerClass(scope,
                CharUtils.class, StrUtils.class, BinUtils.class, NumUtils.class, ItemsUtils.class,
                MirrorUtils.class, FsUtils.class, OldBinUtils.class, OldItemsUtils.class, OldNumUtils.class, OldMirrorUtils.class,
                RxUtils.class, WeakReference.class
        );

        registerClass(scope,
                SharedUtils.SharedMemory.class, SharedUtils.SharedCollection.class, SharedUtils.SharedValue.class,
                SharedUtils.SharedStack.class, SharedUtils.SharedQueue.class, SharedUtils.SharedMap.class,
                SharedUtils.class, SharedUtils.SharedThreadLocal.class,

                WrapPackageLoader.class, WrapPackageLoader.WrapLauncherPackageLoader.class,
                WrapClassLoader.class, WrapClassLoader.WrapLauncherClassLoader.class,

                WrapLocale.class, WrapScanner.class, WrapFlow.class, WrapConfiguration.class, WrapRegex.class
        );

        registerJavaExceptionForContext(scope, WrapRegex.RegexException.class, WrapRegex.class);

        registerClass(scope, WrapTimeZone.class, WrapTimeFormat.class, WrapTime.class);
        registerWrapperClass(scope, TimerTask.class, WrapTimer.class);

        registerClass(scope, WrapInvoker.class, WrapModule.class, WrapPackage.class, Closure.ClosureInvoker.class);
        registerWrapperClass(scope, SourceMap.class, WrapSourceMap.class);

        registerClass(scope,
                WrapEnvironment.class, WrapEnvironmentVariables.class, WrapThreadGroup.class,
                WrapThread.class, WrapSystem.class,

                Reflector.class, Reflection.class, ReflectionException.class, ReflectionExtension.class,
                ReflectionType.class, ReflectionFunctionAbstract.class, ReflectionFunction.class,
                ReflectionParameter.class, ReflectionProperty.class, ReflectionMethod.class, ReflectionClass.class,
                ReflectionObject.class, ReflectionClassConstant.class
        );

        registerClass(scope, WrapJavaExceptions.NotImplementedException.class);

        // stream
        registerJavaException(scope, WrapIOException.class, IOException.class);
        registerClass(scope,
                FileObject.class, Stream.class, FileStream.class, MiscStream.class, MemoryMiscStream.class,
                ResourceStream.class
        );

        // net
        registerClass(scope, WrapSocket.class, WrapServerSocket.class);
        registerJavaException(scope, WrapSocketException.class, SocketException.class);

        registerClass(scope, WrapThreadPool.class, WrapFuture.class, WrapPromise.class);
        registerJavaException(scope, WrapJavaExceptions.TimeoutException.class, TimeoutException.class);

        registerClass(scope, WrapProcessor.class, IniProcessor.class, WrapProcessor.ProcessorException.class, WrapProcess.class);
    }

    @Override
    public void onLoad(Environment env) {
        Stream.initEnvironment(env);
        WrapProcessor.registerCode(env, "ini", IniProcessor.class);
    }

    @Override
    public void onShutdown(CompileScope scope, Environment env) {
        WrapTimer.shutdownAll();
    }
}
