package io.alcatraz.f12;

public interface AsyncInterface<T> {
    void onDone(T result);
    void onFailure(String why);
}
