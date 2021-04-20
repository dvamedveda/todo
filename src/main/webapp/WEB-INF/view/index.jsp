<!doctype html>
<html lang="en">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
          integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <title>Todo List</title>
</head>

<body>
<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
        integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
        crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
        integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
        crossorigin="anonymous"></script>
<script type="text/javascript" src='<c:url value="/scripts/index.js"/>' defer></script>

<div class="container">
    <div class="row pt-3 mx-2 px-2 my-1 py-1">
        <div class="col-8">
        </div>
        <div class="col align-self-end">
            <div class="row">
                <div class="col align-self-start">
                    <p class="text-right">
                        Вы вошли:
                    </p>
                </div>
                <div class="col align-self-start">
                    <div id="user">
                        <p class="text-left">
                            <c:out value="${sessionScope.user.name}"/>
                        </p>
                    </div>
                </div>
                <div class="col align-self-start">
                    <form action="<c:url value="/logout.do"/>" method="post">
                        <button id="logout" type="submit" class="btn btn-success mx-auto">Выйти</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div id="head_row" class="row pt-3 mx-2 px-2 my-1 py-1">
        <div class="col-8">
            <h4>
                Добавить новую задачу:
            </h4>
        </div>
    </div>

    <div class="row pt-3 mx-2 px-2 my-1 py-1">
        <div class="col">
            <div id="task_form">
                <label for="task_description">Описание:</label>
                <input type="text" class="form-control" id="task_description" name="description">
            </div>
        </div>
    </div>

    <div class="row mx-2 px-2 my-1 py-1">
        <div class="col">
        </div>
        <div class="col">
        </div>
        <div class="col">
        </div>
        <div class="col m-1 p-1">
            <button id="submit_task" type="button" class="btn btn-success mx-auto">Создать задачу</button>
        </div>
    </div>

    <div class="row pt-3 mx-2 px-2 my-1 py-1">
        <div class="col align-self-end m-1">
            <h4>
                Список задач:
            </h4>
        </div>
        <div class="col">
        </div>
        <div class="col">
        </div>
        <div class="col align-self-end m-2">
            <input id="completed" type="checkbox">Показать выполненные</button>
        </div>
    </div>
    <div id="list_row" class="row pt-3 mx-2 px-2 my-1 py-1">
        <table class="table table-bordered mx-2 px-2 my-1 py-1">
            <thead>
            <tr id="table_head_row">
                <th>Описание задачи</th>
                <th style="width: 20%;">Создана</th>
                <th style="width: 10%;">Выполнена</th>
                <th style="width: 10%;">Автор</th>
            </tr>
            </thead>
            <tbody id="table_body">
            </tbody>
        </table>
    </div>
</div>
</body>
</html>