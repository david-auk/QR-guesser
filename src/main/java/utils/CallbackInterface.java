package utils;

/**
 * Interface for interacting with tasks that should be cancelable.
 */
public interface CallbackInterface<T> {
    void update(T status);
    Boolean shouldCancel();
}