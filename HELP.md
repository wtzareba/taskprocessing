# Documentation

### Description

Application allows to:
1. Run asynchronous task finding best match of input part for given pattern
2. Fetch status of a task
3. Fetch result of a task if task  has finished
4. Fetch all task ids

### Requires
1. Java 17
2. docker and docker-compose commands

### How to run
In order to run the application as a container one need to firstly build the application using gradle

    gradle build

Finally one should execute following command from project location:

    docker-compose up --build

This will create a task processing server on localhost working on port 8080
Detailed image description is given in Dockerfile

### API

Application allows execution of following api:

* /tasks/get_all_ids - returns set of task ids
  * method: GET
  * sample usage: http://localhost:8080/api/tasks/get_all_ids
  * sample result: **["3ce9c723-bb3d-4dd7-bb2f-592d773029bb","76152b88-fcf1-4806-8254-bd32824cc68a","a592119c-d55b-43cb-a6f3-11fee6b595eb"]**
* /task/run - executes asynchronous task for given input and pattern in order to find position and typos of best matching part of input for given pattern. Returns taskId in UUID format.
  * method: GET
  * request parameters
    * pattern - String
    * input - String - represents text on which pattern should be applied
  * sample usage: http://localhost:8080/api/task/run?input=ABCD&pattern=ABD
  * sample result: **a592119c-d55b-43cb-a6f3-11fee6b595eb**
* /task/{taskId}/get_status - for desired task, gets status of task with its progress
  * method: GET
  * sample usage: http://localhost:8080/api/task/a592119c-d55b-43cb-a6f3-11fee6b595eb/get_status
  * sample result: **{"taskStatus":"FINISHED","progress":100}**
* /task/{taskId}/get_result - for desired task, gets result of processing, result will bew returned only if task has finished his job
  * method: GET
  * sample usage: http://localhost:8080/api/task/run?input=ABCD&pattern=ABD
  * sample result: **{"position":0,"typos":1}**

### Used libraries

* **org.springframework.boot:spring-boot-starter-web** - defines libraries needed to execute a web application using SpringBoot with build in Tomcat service 
* **org.springframework.boot:spring-boot-starter-validation** - set of libraries used to facilitate data validation
* **org.springframework.boot:spring-boot-starter-test** - set of libraries required to prepare JUnit tests