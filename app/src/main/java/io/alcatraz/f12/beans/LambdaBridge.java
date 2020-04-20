package io.alcatraz.f12.beans;

public class LambdaBridge<T> {
    private T target;

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }
}
