package wz.taskprocessing.task.data;

public class TaskData {

    private ProcessingResult processingResult;
    private TaskStatusData taskStatusData;

    public TaskData(TaskStatusData taskStatusData) {
        this.taskStatusData = taskStatusData;
    }

    public TaskData(TaskStatusData taskStatusData, ProcessingResult processingResult) {
        this.taskStatusData = taskStatusData;
        this.processingResult = processingResult;
    }

    public ProcessingResult getProcessingResult() {
        return processingResult;
    }

    public void setProcessingResult(ProcessingResult processingResult) {
        this.processingResult = processingResult;
    }

    public TaskStatusData getTaskStatusData() {
        return taskStatusData;
    }

    public void setTaskStatusData(TaskStatusData taskStatusData) {
        this.taskStatusData = taskStatusData;
    }

}
