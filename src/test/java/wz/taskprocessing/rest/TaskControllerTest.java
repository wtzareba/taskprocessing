package wz.taskprocessing.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import wz.taskprocessing.task.TaskService;
import wz.taskprocessing.task.data.ProcessingResult;
import wz.taskprocessing.task.data.TaskData;
import wz.taskprocessing.task.data.TaskStatus;
import wz.taskprocessing.task.data.TaskStatusData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    TaskService taskService;

    @Test
    void nonEmptyTaskList() throws Exception {
        UUID testValue = UUID.randomUUID();
        Set<UUID> set = new HashSet<>();
        set.add(testValue);
        Mockito.when(taskService.getAllTaskIds()).thenReturn(set);
        mvc.perform(get(ApiConstants.GET_ALL_TASK_IDS_API))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value(testValue.toString()));
    }

    @Test
    void emptyTaskList() throws Exception {
        Mockito.when(taskService.getAllTaskIds()).thenReturn(new HashSet<>());
        mvc.perform(get(ApiConstants.GET_ALL_TASK_IDS_API))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void missingParamsWhenRunningTask() throws Exception {
        mvc.perform(get(ApiConstants.CREATE_TASK_API))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mvc.perform(get(ApiConstants.CREATE_TASK_API).param("pattern", ""))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mvc.perform(get(ApiConstants.CREATE_TASK_API).param("input", ""))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mvc.perform(get(ApiConstants.CREATE_TASK_API).param("input", "").param("pattern", ""))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mvc.perform(get(ApiConstants.CREATE_TASK_API).param("input", "AAA").param("pattern", ""))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mvc.perform(get(ApiConstants.CREATE_TASK_API).param("input", "").param("pattern", "AAA"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void taskCreated() throws Exception {
        String pattern = "ABC";
        String input = "ABCD";
        UUID taskId = UUID.randomUUID();
        Mockito.when(taskService.invokeNewTask(pattern, input)).thenReturn(taskId);
        mvc.perform(get(ApiConstants.CREATE_TASK_API).param("pattern", pattern).param("input", input))
                .andExpect(status().isOk()).andExpect(content().string(taskId.toString()));
    }

    @Test
    void invalidAttemptToGetTaskStatusData() throws Exception {
        mvc.perform(get(ApiConstants.GET_TASK_STATUS_API,  "1"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        UUID taskId = UUID.randomUUID();
        Mockito.when(taskService.getTaskData(taskId)).thenReturn(null);
        mvc.perform(get(ApiConstants.GET_TASK_STATUS_API, taskId.toString()))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void getCorrectStatus() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskStatusData expectedStatus = new TaskStatusData(TaskStatus.FINISHED, 100);
        Mockito.when(taskService.getTaskData(taskId)).thenReturn(new TaskData(expectedStatus));
        MvcResult result = mvc.perform(get(ApiConstants.GET_TASK_STATUS_API, taskId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        ObjectMapper mapper = new ObjectMapper();
        TaskStatusData fetchedStatus = mapper.readValue(result.getResponse().getContentAsString(), TaskStatusData.class);
        Assertions.assertEquals(expectedStatus, fetchedStatus);
    }

    @Test
    void invalidAttemptToGetTaskProcessingResult() throws Exception {
        mvc.perform(get(ApiConstants.GET_TASK_RESULT_API,  "1"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        UUID taskId = UUID.randomUUID();
        Mockito.when(taskService.getTaskData(taskId)).thenReturn(null);
        mvc.perform(get(ApiConstants.GET_TASK_RESULT_API, taskId.toString()))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        taskId = UUID.randomUUID();
        Mockito.when(taskService.getTaskData(taskId)).thenReturn(new TaskData(new TaskStatusData(TaskStatus.IN_PROGRESS)));
        mvc.perform(get(ApiConstants.GET_TASK_RESULT_API, taskId.toString()))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        taskId = UUID.randomUUID();
        Mockito.when(taskService.getTaskData(taskId)).thenReturn(new TaskData(new TaskStatusData(TaskStatus.CANCELLED)));
        mvc.perform(get(ApiConstants.GET_TASK_RESULT_API, taskId.toString()))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void getCorrectResult() throws Exception {
        UUID taskId = UUID.randomUUID();
        ProcessingResult expectedResult = new ProcessingResult(1, 0);
        Mockito.when(taskService.getTaskData(taskId)).thenReturn(new TaskData(new TaskStatusData(TaskStatus.FINISHED, 100), expectedResult));
        MvcResult result = mvc.perform(get(ApiConstants.GET_TASK_RESULT_API, taskId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        ProcessingResult fetchedStatus = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ProcessingResult.class);
        Assertions.assertEquals(expectedResult, fetchedStatus);
    }

}
