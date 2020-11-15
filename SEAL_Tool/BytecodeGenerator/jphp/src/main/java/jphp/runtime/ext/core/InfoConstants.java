package jphp.runtime.ext.core;

import jphp.runtime.ext.support.compile.ConstantsContainer;
import jphp.runtime.Information;
import jphp.runtime.Memory;
import jphp.runtime.common.Constants;
import jphp.runtime.memory.StringMemory;

public class InfoConstants extends ConstantsContainer {
    public static Memory JPHP_VERSION = StringMemory.valueOf(Information.CORE_VERSION);
    public static Memory PATH_SEPARATOR = StringMemory.valueOf(Constants.PATH_SEPARATOR);
    public static Memory DIRECTORY_SEPARATOR = StringMemory.valueOf(Constants.DIRECTORY_SEPARATOR);
}
