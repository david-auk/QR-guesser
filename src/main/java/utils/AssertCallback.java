package utils;

import java.util.HashMap;
import java.util.Map;

public class AssertCallback implements CallbackInterface<Map<String, String>>{

    static private final Map<String, Map<String, String>> globalTasksStatus = new HashMap<>();
    static private final Map<String, Boolean> globalTasksCancelStatus = new HashMap<>();

    private final String taskId;
    private final Map<String, String> tasksStatus;

    public AssertCallback(String taskId) {
        this.taskId = taskId;

        if (globalTasksStatus.containsKey(taskId)) {
            tasksStatus = globalTasksStatus.get(taskId);
        }else{
            tasksStatus = null;
            globalTasksStatus.put(taskId, null);
            globalTasksCancelStatus.put(taskId, false);
        }
    }

    @Override
    public void update(Map<String, String> status) {
        tasksStatus.clear();
        tasksStatus.putAll(status);
        //globalTasksStatus.put(taskId, tasksStatus); TODO Check if tasksStatus automatically edits globalTasksStatus
    }

    public Map<String, String> getTasksStatus() {
        return tasksStatus;
    }

    public void cancel() {
        globalTasksCancelStatus.put(this.taskId, true);
    }

    @Override
    public Boolean shouldCancel() {
        return globalTasksCancelStatus.get(taskId);
    }

    public void remove() {
        globalTasksStatus.remove(taskId);
        globalTasksCancelStatus.remove(taskId);
    }
}
