package wz.taskprocessing.task.data;

import java.util.Objects;

public class TaskStatusData {

    private TaskStatus taskStatus;
    private Integer progress = 0;

    private TaskStatusData() {}
    public TaskStatusData(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
    public TaskStatusData(TaskStatus taskStatus, Integer progress) {
        this(taskStatus);
        this.progress = progress;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskStatusData that = (TaskStatusData) o;
        return getTaskStatus() == that.getTaskStatus() && Objects.equals(getProgress(), that.getProgress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTaskStatus(), getProgress());
    }
}
