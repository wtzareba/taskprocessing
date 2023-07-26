package wz.taskprocessing.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import wz.taskprocessing.task.data.ProcessingResult;
import wz.taskprocessing.task.data.TaskStatus;
import wz.taskprocessing.task.data.TaskStatusData;
import wz.taskprocessing.task.runnable.BestMatchFinderTask;

import java.util.UUID;

public class BestMatchFinderTaskTest {

    TaskManager taskManager;

    @BeforeEach
    void setUpEach() {
        taskManager = new TaskManager();
    }


    @Test
    void emptyInput() {
        UUID taskId1 = UUID.randomUUID();
        UUID taskId2 = UUID.randomUUID();
        new BestMatchFinderTask(taskId1, "ABCD", "", taskManager).find();
        new BestMatchFinderTask(taskId2, "ABCD", null, taskManager).find();
        Assertions.assertEquals(TaskStatus.CANCELLED, taskManager.getTaskData(taskId1).getTaskStatusData().getTaskStatus());
        Assertions.assertEquals(TaskStatus.CANCELLED, taskManager.getTaskData(taskId2).getTaskStatusData().getTaskStatus());
    }

    @Test
    void emptyPattern() {
        UUID taskId1 = UUID.randomUUID();
        UUID taskId2 = UUID.randomUUID();
        UUID taskId3 = UUID.randomUUID();
        UUID taskId4 = UUID.randomUUID();
        new BestMatchFinderTask(taskId1, "", "", taskManager).find();
        new BestMatchFinderTask(taskId2, null, null, taskManager).find();
        new BestMatchFinderTask(taskId3, "", "ABCD", taskManager).find();
        new BestMatchFinderTask(taskId4, null, "ABCD", taskManager).find();
        Assertions.assertEquals(TaskStatus.CANCELLED, taskManager.getTaskData(taskId1).getTaskStatusData().getTaskStatus());
        Assertions.assertEquals(TaskStatus.CANCELLED, taskManager.getTaskData(taskId2).getTaskStatusData().getTaskStatus());
        Assertions.assertEquals(TaskStatus.CANCELLED, taskManager.getTaskData(taskId3).getTaskStatusData().getTaskStatus());
        Assertions.assertEquals(TaskStatus.CANCELLED, taskManager.getTaskData(taskId4).getTaskStatusData().getTaskStatus());
    }

    @Test
    void patternLongerThanInput() {
        UUID test1 = UUID.randomUUID();
        new BestMatchFinderTask(test1, "123", "12", taskManager).find();
        Assertions.assertEquals(TaskStatus.CANCELLED, taskManager.getTaskData(test1).getTaskStatusData().getTaskStatus());
    }



    @Test
    void bestMatchFindCheck() {
        UUID taskId = UUID.randomUUID();
        new BestMatchFinderTask(taskId, "BCD", "ABCD", taskManager, 1L).find();
        Assertions.assertEquals(new ProcessingResult(1, 0), taskManager.getTaskData(taskId).getProcessingResult());
        Assertions.assertEquals(new TaskStatusData(TaskStatus.FINISHED, 100), taskManager.getTaskData(taskId).getTaskStatusData());
        new BestMatchFinderTask(taskId, "BWD", "ABCD", taskManager, 1L).find();
        Assertions.assertEquals(new ProcessingResult(1, 1), taskManager.getTaskData(taskId).getProcessingResult());
        Assertions.assertEquals(new TaskStatusData(TaskStatus.FINISHED, 100), taskManager.getTaskData(taskId).getTaskStatusData());
        new BestMatchFinderTask(taskId, "CFG", "ABCDEFG", taskManager, 1L).find();
        Assertions.assertEquals(new ProcessingResult(4, 1), taskManager.getTaskData(taskId).getProcessingResult());
        Assertions.assertEquals(new TaskStatusData(TaskStatus.FINISHED, 100), taskManager.getTaskData(taskId).getTaskStatusData());
        new BestMatchFinderTask(taskId, "ABC", "ABCABC", taskManager, 1L).find();
        Assertions.assertEquals(new ProcessingResult(0, 0), taskManager.getTaskData(taskId).getProcessingResult());
        Assertions.assertEquals(new TaskStatusData(TaskStatus.FINISHED, 100), taskManager.getTaskData(taskId).getTaskStatusData());
        new BestMatchFinderTask(taskId, "ABC", "ABC", taskManager, 1L).find();
        Assertions.assertEquals(new ProcessingResult(0, 0), taskManager.getTaskData(taskId).getProcessingResult());
        Assertions.assertEquals(new TaskStatusData(TaskStatus.FINISHED, 100), taskManager.getTaskData(taskId).getTaskStatusData());
        new BestMatchFinderTask(taskId, "TDD", "ABCDEFG", taskManager, 1L).find();
        Assertions.assertEquals(new ProcessingResult(1, 2), taskManager.getTaskData(taskId).getProcessingResult());
        Assertions.assertEquals(new TaskStatusData(TaskStatus.FINISHED, 100), taskManager.getTaskData(taskId).getTaskStatusData());
        new BestMatchFinderTask(taskId, "ABC", "DEF", taskManager, 1L).find();
        Assertions.assertEquals(new ProcessingResult(0, 3), taskManager.getTaskData(taskId).getProcessingResult());
        Assertions.assertEquals(new TaskStatusData(TaskStatus.FINISHED, 100), taskManager.getTaskData(taskId).getTaskStatusData());
    }

    @Test
    void checkProgressUpdateWithOneTypo() {
        TaskManager taskManagerMock = Mockito.mock(TaskManager.class);
        InOrder order = Mockito.inOrder(taskManagerMock);
        UUID taskId = UUID.randomUUID();
        String pattern = "BCDA", input = "ABCDEFG";
        double progressStep = 1.0 / (input.length() - pattern.length() + 1);
        new BestMatchFinderTask(taskId, pattern, input, taskManagerMock, 1L).find();
        int iteration = 1;
        order.verify(taskManagerMock).createTaskData(taskId);
        order.verify(taskManagerMock).setTaskData(taskId, new TaskStatusData(TaskStatus.IN_PROGRESS, 0));
        order.verify(taskManagerMock).setTaskData(taskId, new TaskStatusData(TaskStatus.IN_PROGRESS, (int) (progressStep * iteration++ * BestMatchFinderTask.MAX_PROGRESS)));
        order.verify(taskManagerMock).setTaskData(taskId, new TaskStatusData(TaskStatus.IN_PROGRESS, (int) (progressStep * iteration++ * BestMatchFinderTask.MAX_PROGRESS)));
        order.verify(taskManagerMock).setTaskData(taskId, new TaskStatusData(TaskStatus.IN_PROGRESS, (int) (progressStep * iteration * BestMatchFinderTask.MAX_PROGRESS)));
        order.verify(taskManagerMock).setTaskData(taskId, new TaskStatusData(TaskStatus.FINISHED, BestMatchFinderTask.MAX_PROGRESS), new ProcessingResult(1, 1));
    }

    @Test
    void checkProgressUpdateWithNoTypo() {
        TaskManager taskManagerMock = Mockito.mock(TaskManager.class);
        InOrder order = Mockito.inOrder(taskManagerMock);
        UUID taskId = UUID.randomUUID();
        String pattern = "BCD", input = "ABCDEFG";
        double progressStep = 1.0 / (input.length() - pattern.length() + 1);
        new BestMatchFinderTask(taskId, pattern, input, taskManagerMock, 1L).find();
        order.verify(taskManagerMock).createTaskData(taskId);
        order.verify(taskManagerMock).setTaskData(taskId, new TaskStatusData(TaskStatus.IN_PROGRESS, 0));
        order.verify(taskManagerMock).setTaskData(taskId, new TaskStatusData(TaskStatus.IN_PROGRESS, (int) (progressStep * BestMatchFinderTask.MAX_PROGRESS)));
        order.verify(taskManagerMock).setTaskData(taskId, new TaskStatusData(TaskStatus.FINISHED, BestMatchFinderTask.MAX_PROGRESS), new ProcessingResult(1, 0));
    }

}
