package utils;

/**
 * Interface for interacting with tasks that should be cancelable.
 */
public interface ProcessCallback {
    void onProgress(String message);
    boolean shouldCancel();
}