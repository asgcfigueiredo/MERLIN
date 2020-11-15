package jphp.runtime.ext.core.classes.format;

import jphp.runtime.ext.core.classes.stream.Stream;
import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection;
import jphp.runtime.annotation.Reflection.Arg;
import jphp.runtime.annotation.Reflection.Optional;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.reflection.ClassEntity;

import java.io.IOException;
import java.util.Scanner;

@Reflection.Name("php\\format\\IniProcessor")
public class IniProcessor extends WrapProcessor {
    public IniProcessor(Environment env) {
        super(env);
    }

    public IniProcessor(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature(@Arg(value = "flags", optional = @Optional("0")))
    public Memory __construct(Environment env, Memory... args) {
        return Memory.NULL;
    }

    private static String escapeValue(Memory value) {
        switch (value.getRealType()) {
            case BOOL:
                return value.toBoolean() ? "1" : "0";
            default:
                return value
                        .toString()
                        .replace("\r", "\\r")
                        .replace("\t", "\\t")
                        .replace("\n", "\\n");
        }
    }

    private static String unescapeValue(Memory value) {
        return value
                .toString()
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\n", "\n");
    }

    @Override
    @Signature
    public Memory parse(Environment env, Memory... args) {
        Scanner scanner;

        if (args[0].instanceOf(Stream.class)) {
            scanner = new Scanner(Stream.getInputStream(env, args[0]), env.getDefaultCharset().name());
        } else {
            scanner = new Scanner(args[0].toString());
        }

        String section = null;
        ArrayMemory result = ArrayMemory.createHashed();
        ArrayMemory sectionArray = null;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.trim().isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                continue;
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                section = line.substring(1, line.length() - 1);
                sectionArray = ArrayMemory.createHashed();
                result.putAsKeyString(section, sectionArray);
            } else {
                String[] parts = line.split("=", 2);

                if (parts.length == 2) {
                    String key = parts[0];
                    String _value = parts[1];

                    Memory value = StringMemory.valueOf(_value);
                    if (StringMemory.toNumeric(_value, false, null) != null) {
                        value = value.toNumeric();
                    }

                    if (sectionArray == null) {
                        result.putAsKeyString(key, value);
                    } else {
                        sectionArray.putAsKeyString(key, value);
                    }
                }
            }
        }

        return result.toConstant();
    }

    @Override
    @Signature
    public Memory format(Environment env, Memory... args) {
        StringBuilder sb = new StringBuilder();
        Memory value = args[0];

        ForeachIterator iterator = value.getNewIterator(env);

        if (iterator != null) {
            while (iterator.next()) {
                String key = iterator.getStringKey();
                Memory item = iterator.getValue();

                ForeachIterator itemIterator = item.getNewIterator(env);
                if (itemIterator != null) {
                    sb.append("\r\n").append("[").append(key).append("]").append("\r\n");

                    while (itemIterator.next()) {
                        sb.append(itemIterator.getStringKey()).append("=").append(escapeValue(itemIterator.getValue())).append("\r\n");
                    }
                } else {
                    sb.append(key).append("=").append(escapeValue(iterator.getValue())).append("\r\n");
                }
            }

            return StringMemory.valueOf(sb.toString());
        } else {
            env.exception(ProcessorException.class, "value for ini must be array or iterable");
            return Memory.NULL;
        }
    }

    @Override
    public Memory formatTo(Environment env, Memory... args) {
        try {
            args[1].toObject(Stream.class).write(env, format(env, args), Memory.NULL);
        } catch (IOException e) {
            env.forwardThrow(e);
        }

        return Memory.NULL;
    }
}
