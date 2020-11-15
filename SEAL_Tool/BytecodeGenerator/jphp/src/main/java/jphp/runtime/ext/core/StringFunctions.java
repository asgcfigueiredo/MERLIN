package jphp.runtime.ext.core;

import jphp.runtime.ext.support.compile.FunctionsContainer;
import jphp.runtime.memory.output.serialization.Deserializer;
import jphp.runtime.memory.output.serialization.Serializer;
import jphp.runtime.Memory;
import jphp.runtime.annotation.Runtime;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;

public class StringFunctions extends FunctionsContainer {
    @Runtime.Immutable
    public static String chr(int codePoint){
        return String.valueOf((char) codePoint);
    }

    @Runtime.Immutable
    public static int ord(char value){
        return (int) value;
    }

    public static String serialize(Environment env, TraceInfo trace, Memory value){
        StringBuilder writer = new StringBuilder();
        Serializer serializer = new Serializer(env, trace, writer);

        serializer.write(value);
        return writer.toString();
    }

    public static Memory unserialize(Environment env, TraceInfo trace, String value){
        Deserializer deserializer = new Deserializer(env, trace);
        return deserializer.read(value);
    }
}
