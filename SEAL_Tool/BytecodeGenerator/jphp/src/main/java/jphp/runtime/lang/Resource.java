package jphp.runtime.lang;

import jphp.runtime.annotation.Reflection;

@Reflection.Ignore
public interface Resource {
    String getResourceType();
}
