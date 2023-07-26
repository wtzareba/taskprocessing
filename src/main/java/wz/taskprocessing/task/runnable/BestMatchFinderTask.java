package wz.taskprocessing.task.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import wz.taskprocessing.task.TaskManager;
import wz.taskprocessing.task.data.ProcessingResult;
import wz.taskprocessing.task.data.TaskStatus;
import wz.taskprocessing.task.data.TaskStatusData;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class BestMatchFinderTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(BestMatchFinderTask.class);
    public static final int MAX_PROGRESS = 100;
    private static final long DEFAULT_DELAY = 10000L;

    private final String pattern;
    private final String input;
    private final UUID taskId;

    private final TaskManager manager;
    private final Long delay;

    public BestMatchFinderTask(UUID taskId, String pattern, String input, TaskManager manager) {
        this(taskId, pattern, input, manager, DEFAULT_DELAY);
    }

    public BestMatchFinderTask(UUID taskId, String pattern, String input, TaskManager manager, Long delay) {
        this.taskId = taskId;
        this.pattern = pattern;
        this.input = input;
        this.manager = manager;
        this.delay = delay;
        this.manager.createTaskData(taskId);
    }

    public void find() {
        manager.setTaskData(taskId, new TaskStatusData(TaskStatus.IN_PROGRESS));
        if (checkValidity(pattern, input)) {
            logger.error("Task: " + taskId + ". Provided input or pattern parameters are invalid. Cancelling task");
            manager.setTaskData(taskId, new TaskStatusData(TaskStatus.CANCELLED));
            return;
        }

        ProcessingResult processingResult = new ProcessingResult();
        var maxProcessingLength = input.length() - pattern.length() + 1;
        var progressStep = 1.0 / maxProcessingLength;
        for (int i = 0; i < maxProcessingLength; i++) {
            updateProcessingResult(processingResult, i);

            if (processingResult.getTypos() != null && processingResult.getTypos() == 0) {
                logger.info("Task: " + taskId + ". Found exact match: " + processingResult);
                manager.setTaskData(taskId, new TaskStatusData(TaskStatus.FINISHED, MAX_PROGRESS), processingResult);
                return;
            } else {
                updateProgress(progressStep, i);
            }

            sleepThread();
        }
        logger.info("Task: " + taskId + ". Finished with match: " + processingResult);
        manager.setTaskData(taskId, new TaskStatusData(TaskStatus.FINISHED, MAX_PROGRESS), processingResult);
    }

    private void updateProgress(double progressStep, int iteration) {
        var adjustedProgress = (int) ((iteration + 1) * progressStep * MAX_PROGRESS);
        logger.info("Task: " + taskId + ". Progress: " + adjustedProgress);
        manager.setTaskData(taskId, new TaskStatusData(TaskStatus.IN_PROGRESS, adjustedProgress));
    }

    private void updateProcessingResult(ProcessingResult processingResult, int iteration) {
        var tmpProcessingResult = new ProcessingResult(iteration, getTypos(pattern, input, iteration));
        if (processingResult.getPosition() == null || tmpProcessingResult.getTypos() < processingResult.getTypos()) {
            processingResult.setPosition(tmpProcessingResult.getPosition());
            processingResult.setTypos(tmpProcessingResult.getTypos());
        }
    }

    private void sleepThread() {
        try {
            Thread.sleep(delay);
        } catch (Exception ignored) {
        }
    }

    private boolean checkValidity(String pattern, String input) {
        return !StringUtils.hasText(pattern) || !StringUtils.hasText(input) || pattern.length() > input.length();
    }

    private int getTypos(String pattern, String input, int processedInputIndex) {
        var typos = new AtomicInteger();
        IntStream.range(0, pattern.length()).forEach(j -> {
            if (pattern.charAt(j) != input.charAt(processedInputIndex + j)) {
                typos.getAndIncrement();
            }
        });
        return typos.get();
    }

    @Override
    public void run() {
        find();
    }
}
