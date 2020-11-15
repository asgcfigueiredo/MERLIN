package jphp.runtime.launcher;

import jphp.runtime.loader.StandaloneLoader;

public class StandaloneLauncher {

    public static void main(String[] args) {
        StandaloneLoader loader = new StandaloneLoader();
        loader.setClassLoader(StandaloneLauncher.class.getClassLoader());
        loader.run();
    }
}
