package wz.taskprocessing.rest;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wz.taskprocessing.exception.NoDataAvailableException;
import wz.taskprocessing.task.TaskService;
import wz.taskprocessing.task.data.ProcessingResult;
import wz.taskprocessing.task.data.TaskData;
import wz.taskprocessing.task.data.TaskStatus;
import wz.taskprocessing.task.data.TaskStatusData;

import java.util.Set;
import java.util.UUID;

@RestController
@Validated
public class TaskController {
    @Autowired
    TaskService service;

    @GetMapping(ApiConstants.CREATE_TASK_API)
    public String createTask(@RequestParam("pattern") @NotBlank String pattern, @RequestParam("input") @NotBlank String input) {
        return service.invokeNewTask(pattern, input).toString();
    }

    @GetMapping(ApiConstants.GET_TASK_STATUS_API)
    public TaskStatusData getTaskStatus(@PathVariable("taskId") @NotBlank String taskIdString) {
        UUID taskId = UUID.fromString(taskIdString);
        TaskData data = service.getTaskData(taskId);
        checkTaskDataAvailability(taskId, data);
        return data.getTaskStatusData();
    }

    @GetMapping(ApiConstants.GET_TASK_RESULT_API)
    public ProcessingResult getTaskProcessingResult(@PathVariable("taskId") @NotBlank String taskIdString) {
        UUID taskId = UUID.fromString(taskIdString);
        TaskData data = service.getTaskData(taskId);
        checkTaskDataAvailability(taskId, data);
        if(TaskStatus.FINISHED.equals(data.getTaskStatusData().getTaskStatus())) {
            return data.getProcessingResult();
        }
        throw new NoDataAvailableException("Task is still ongoing");
    }

    @GetMapping(ApiConstants.GET_ALL_TASK_IDS_API)
    public Set<UUID> getAllTasks() {
        return service.getAllTaskIds();
    }

    private static void checkTaskDataAvailability(UUID taskId, TaskData data) {
        if (data == null) {
            throw new NoDataAvailableException("Task for id: " + taskId + " does not exist");
        }
    }
}
