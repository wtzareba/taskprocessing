package wz.taskprocessing.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import wz.taskprocessing.task.data.TaskData;
import wz.taskprocessing.task.runnable.BestMatchFinderTask;

import java.util.Set;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    TaskManager taskManager;

    public TaskData getTaskData(UUID taskId) {
        return taskManager.getTaskData(taskId);
    }

    public Set<UUID> getAllTaskIds() {
        return taskManager.getAllTaskIds();
    }

    public UUID invokeNewTask(String pattern, String input) {
        var executor = new SimpleAsyncTaskExecutor();
        var newTaskId = UUID.randomUUID();
        executor.execute(new BestMatchFinderTask(newTaskId, pattern, input, taskManager));
        return newTaskId;
    }

}
