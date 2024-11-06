<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>修改学生信息</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f4f4f4;
      margin: 0;
      padding: 0;
    }
    .container {
      max-width: 600px;
      margin: 50px auto;
      background: #fff;
      padding: 20px;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }
    h1 {
      text-align: center;
      color: #333;
    }
    form {
      display: flex;
      flex-direction: column;
    }
    label {
      margin-top: 10px;
      font-weight: bold;
    }
    input[type="text"] {
      padding: 10px;
      margin-top: 5px;
      border: 1px solid #ccc;
      border-radius: 4px;
    }
    input[type="submit"] {
      margin-top: 20px;
      padding: 10px;
      background-color: #007BFF;
      color: #fff;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    input[type="submit"]:hover {
      background-color: #0056b3;
    }
  </style>
</head>
<body>
<div class="container">
  <h1>修改学生信息</h1>
  <form action="/student?action=save" method="post">
    <input type="hidden" name="id" value="${student.id}" />
    <label for="name">姓名:</label>
    <input type="text" id="name" name="name" value="${student.name}" required pattern="^[A-Za-z\s]+$" title="姓名只能包含字母和空格" />

    <label for="birthday">出生日期:</label>
    <input type="date" id="birthday" name="birthday" value="${student.birthday}" required />

    <label for="description">备注:</label>
    <input type="text" id="description" name="description" value="${student.description}" />

    <label for="avgScore">平均分:</label>
    <input type="number" id="avgScore" name="avgScore" value="${student.avgScore}" required min="0" max="100" step="0.01" />

    <input type="submit" value="保存" />
  </form>
</div>
</body>
</html>