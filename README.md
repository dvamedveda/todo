[![Build Status](https://travis-ci.com/dvamedveda/todo.svg?branch=master)](https://travis-ci.com/dvamedveda/todo)
[![codecov](https://codecov.io/gh/dvamedveda/todo/branch/master/graph/badge.svg?token=6LLK4RI2RZ)](https://codecov.io/gh/dvamedveda/todo)

# Todo
Simple MVC Java web application realizing todo-list.
This application developed with layered architecture:
- persistance-layer
- service-layer
- controller-layer
- view-layer

This application have tests for each layer, including controller layer.
It used DAO/DTO, Singleton templates.

### Application features:

- you can register in application as user and use register credentials to authenticate;
- user can add task that need to do, by specifying task's description and hit "Submit" button;
- application can show to user undone tasks or all tasks, including done tasks from other users;
- user can mark own tasks as done/undone, and that shown immediately in list;
- user can predefine some categories for tasks through db;
- user can set few categories for task while creating;
- application validates creating task for not empty description AND at least one task category;
- user cannot done/undone other user's task, but can view it;
- application supports automatic refreshing state of todo list;  
- web app automatically update/check database state on startup.

### Application screenshots:
- register page shows as
![reg](https://github.com/dvamedveda/screenshots/blob/main/todo/reg_page.png?raw=true)  
- auth page shows as
![auth](https://github.com/dvamedveda/screenshots/blob/main/todo/auth_page.png?raw=true)  
- tasks shows as
![index](https://github.com/dvamedveda/screenshots/blob/main/todo/categorized_index_page.png?raw=true)  

### Used frameworks, libs, technologies:
- Java, Servlets, Maven, JUnit, JSP, JSTL, Log4j, Mock, PowerMock, Jackson
- JS, jQuery, Twitter Bootstrap
- Hibernate/JPA, Postgresql, JDBC, Flyway