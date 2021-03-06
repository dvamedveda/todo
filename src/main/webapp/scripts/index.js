/**
 * Константа, определяющая период
 * автоматического обновления страницы приложения.
 * @type {number}
 */
const REFRESH_DELAY = 30000;

$(document).ready(getTasks);
$(document).ready(getCategories);
$(document).ready(autoRefresh);

/**
 * Функция обновления списка задач
 * при помощи AJAX запроса.
 */
function getTasks() {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/todo/todo.do',
        data: {
            completed: $("#completed")[0].checked
        },
        dataType: 'json'
    }).done(function (data) {
        let response = JSON.parse(JSON.stringify(data));
        $("#table_body").empty();
        for (let task of response) {
            let id = task.id;
            let description = task.description;
            let created = formatDate(task.created);
            let done = task.done;
            let author = task.user.name;
            let checked = done ? "checked" : "";
            let disabled = $("#user").text().trim() === author ? "" : "disabled";
            let categories = '';
            for (let category of task.categoryDTOList) {
                categories += category.name + "<br>"
            }
            let row = `
        <tr>
            <td>${description}</td>
            <td>${created}</td>
            <td>
                <div class="change_state_div" id="change_state_${id}">
                    <input hidden name="id" value="${id}">
                    <input type="checkbox" name="state" ${checked} ${disabled}>
                </div>
            </td>
            <td>${author}</td>
            <td>${categories}</td>
        </tr>
        `;
            $("#table_body").append(row);
        }
    }).fail(function (error) {
        console.log("Что-то пошло не так! Запрос не выполнился!")
    })
}

/**
 * Получение списка категорий с сервера.
 */
function getCategories() {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/todo/category.do',
        dataType: 'json'
    }).done(function (data) {
        let response = JSON.parse(JSON.stringify(data));
        $("#categories").empty();
        for (let category of response) {
            let id = category.id;
            let name = category.name;
            let option = `
            <option id="${id}">${name}</option>
        `;
            $("#categories").append(option);
        }
    }).fail(function (error) {
        console.log("Что-то пошло не так! Запрос не выполнился!")
    })
}

/**
 * Функция с рекурсивным setTimeout
 * для обновления списка задача.
 */
function refresh() {
    getCategories();
    getTasks();
    setTimeout(refresh, REFRESH_DELAY);
}

/**
 * Запуск автоматического обновления списка задач.
 */
function autoRefresh() {
    setTimeout(refresh, REFRESH_DELAY);
}

/**
 * Обработка клика на создание новой задачи.
 */
$("#submit_task").click(
    function (event) {
        if (validate_task_description() === false) {
            if (!$.contains(document.querySelector('#task_form'), document.querySelector('#task_form > .description_input + .alert'))) {
                $('#task_description').after('<div class="alert alert-danger" role="alert">' +
                    'Описание задачи не может быть пустым!' +
                    '</div>');
            }
            $('#task_form > .description_input + .alert').show().fadeOut(1000);
            event.preventDefault();
        } else if (validate_selected_categories() === false) {
            if (!$.contains(document.querySelector('#task_form'), document.querySelector('#task_form > .categories_input + .alert'))) {
                $('#categories').after('<div class="alert alert-danger" role="alert">' +
                    'Нужно выбрать одну или несколько категорий!' +
                    '</div>');
            }
            $('#task_form > .categories_input + .alert').show().fadeOut(1000);
            event.preventDefault();
        } else {
            submit(event);
        }
    }
);

function validate_selected_categories() {
    let selectedIds = [];
    for (let children of $("#categories").children()) {
        if (children.selected === true) {
            selectedIds.push(children.id);
        }
    }
    if (selectedIds.length > 0) {
        return true;
    } else if (selectedIds.length === 0) {
        return false
    }
}

function validate_task_description() {
    let input = $("#task_form").children("#task_description");
    return input[0].value !== "";
}

function submit(event) {
    let input = $("#task_form").children("#task_description");
    let selectedIds = [];
    for (let children of $("#categories").children()) {
        if (children.selected === true) {
            selectedIds.push(children.id);
        }
    }
    $.ajax({
        type: 'POST',
        url: 'http://localhost:8080/todo/todo.do',
        data: {
            description: input[0].value,
            categoryIds: JSON.stringify(selectedIds)
        }
    }).done(function (data) {
        input[0].value = "";
        getTasks();
        getCategories();
    }).fail(function (error) {
        console.log("Что-то пошло не так! Запрос не выполнился!")
    })
}

/**
 * Обработка клика на флаг выполненности задачи.
 */
$(document).on("change", ".change_state_div", function () {
        let id_elem = $(this).children()[0];
        let state_elem = $(this).children()[1];
        let send_id = $(id_elem).attr("value");
        let send_state = $(state_elem)[0].checked;
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/todo/task.do',
            data: {
                id: send_id,
                state: send_state
            }
        }).done(function (data) {
            getTasks();
        }).fail(function (error) {
            console.log("Что-то пошло не так! Запрос не выполнился!")
        })
    }
);

/**
 * Обновление списка задач при изменении флага "показать выполненные".
 */
$(document).on("change", "#completed", function () {
        getTasks();
    }
);

/**
 * Просто вспомогательная функция для форматирования времени.
 * @param date время в UTC
 * @returns {string} человекочитаемое время.
 */
function formatDate(date) {
    let dateObject = new Date(date);
    let year = dateObject.getFullYear();
    let month = (dateObject.getMonth() + 1) >= 10 ? dateObject.getMonth() + 1 : "0" + (dateObject.getMonth() + 1);
    let day = dateObject.getDate() >= 10 ? dateObject.getDate() : "0" + dateObject.getDate();
    let hour = dateObject.getHours() >= 10 ? dateObject.getHours() : "0" + dateObject.getHours();
    let minute = dateObject.getMinutes() >= 10 ? dateObject.getMinutes() : "0" + dateObject.getMinutes();
    let second = dateObject.getSeconds() >= 10 ? dateObject.getSeconds() : "0" + dateObject.getSeconds();
    let timeString = hour + ":" + minute + ":" + second;
    let dateString = day + "." + month + "." + year;
    return timeString + " " + dateString;
}