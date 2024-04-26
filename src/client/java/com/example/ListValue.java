package com.example;

import java.util.function.Consumer;

public class ListValue<T> {

    private T value;
    private Consumer<T> onUpdate;

    public ListValue(Consumer<T> onUpdate) {
        this.onUpdate = onUpdate;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        if (onUpdate != null)
            onUpdate.accept(value);
    }

}
