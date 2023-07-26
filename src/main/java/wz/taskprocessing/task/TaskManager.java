package wz.taskprocessing.task;

import org.springframework.stereotype.Component;
import wz.taskprocessing.task.data.ProcessingResult;
import wz.taskprocessing.task.data.TaskData;
import wz.taskprocessing.task.data.TaskStatus;
import wz.taskprocessing.task.data.TaskStatusData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class TaskManager {

    private final Map<UUID, TaskData> tasksMap = new HashMap<>();

    public TaskData getTaskData(UUID taskId) {
        return tasksMap.get(taskId);
    }

    public void createTaskData(UUID taskId) {
        tasksMap.put(taskId, new TaskData(new TaskStatusData(TaskStatus.NEW)));
    }

    public void setTaskData(UUID taskId, TaskStatusData taskStatusData) {
        var data = getTaskData(taskId);
        if(data == null) {
            throw new IllegalStateException("Task for id: " + taskId + "doesn't exist. Can't set task status data");
        }
        data.setTaskStatusData(taskStatusData);
    }

    public void setTaskData(UUID taskId, TaskStatusData taskStatusData, ProcessingResult processingResult) {
        var data = getTaskData(taskId);
        if(data == null) {
            throw new IllegalStateException("Task for id: " + taskId + "doesn't exist. Can't update task data");
        }
        data.setTaskStatusData(taskStatusData);
        data.setProcessingResult(processingResult);
    }

    public Set<UUID> getAllTaskIds() {
        return tasksMap.keySet();
    }

}
