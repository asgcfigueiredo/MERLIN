package jphp.runtime.memory;

public class NativeMemory<T> extends ReferenceMemory {
    private final T object;

    public NativeMemory(T object) {
        super();
        this.object = object;
    }

    public T getObject() {
        return object;
    }
}
