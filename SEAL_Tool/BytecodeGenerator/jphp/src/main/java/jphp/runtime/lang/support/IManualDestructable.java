package jphp.runtime.lang.support;


import jphp.runtime.env.Environment;

public interface IManualDestructable {
    void onManualDestruct(Environment env);
}
