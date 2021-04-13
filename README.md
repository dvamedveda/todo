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

- user can add task that need to do, by specifying task's description and hit "Submit" button;
- user can mark own tasks as done/undone, and that shown immediately in list;
- application supports automatic refreshing state of todo list;  
- application can show undone tasks or all tasks, including done tasks;
- web app automatically update/check database state on startup.

### Application screenshots:
- undone tasks shows as
![undone](https://github.com/dvamedveda/screenshots/blob/main/todo/incompleted.png?raw=true)  
- all tasks shows as
![done](https://github.com/dvamedveda/screenshots/blob/main/todo/completed.png?raw=true)  

### Used frameworks, libs, technologies:
- Java, Maven, JUnit, JSP, JSTL, Log4j, Mock, PowerMock, Jackson
- JS, jQuery, Twitter Bootstrap
- Hibernate, Postgresql, JDBC, Flyway