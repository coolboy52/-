
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>学生列表</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f4f4f4;
      margin: 0;
      padding: 0;
    }
    h1 {
      text-align: center;
      color: #333;
    }
    a {
      text-decoration: none;
      color: #007BFF;
    }
    a:hover {
      text-decoration: underline;
    }
    .container {
      width: 80%;
      margin: 0 auto;
      padding: 20px;
      background-color: #fff;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
    }
    th, td {
      padding: 12px;
      text-align: left;
      border-bottom: 1px solid #ddd;
    }
    th {
      background-color: #f2f2f2;
      color: #333;
    }
    tr:nth-child(even) {
      background-color: #f9f9f9;
    }
    tr:hover {
      background-color: #f1f1f1;
    }
    .pagination {
      margin-top: 20px;
      text-align: center;
    }
    .pagination a {
      display: inline-block;
      padding: 8px 16px;
      margin: 0 5px;
      border: 1px solid #ddd;
      border-radius: 4px;
      background-color: #fff;
      color: #333;
    }
    .pagination a:hover {
      background-color: #007BFF;
      color: #fff;
    }
  </style>
</head>
<body>
<div class="container">
  <h1>学生列表</h1>
  <a href="/student?action=edit">新增学生</a>

  <table>
    <tr>
      <th>姓名</th>
      <th>出生日期</th>
      <th>备注</th>
      <th>平均分</th>
      <th>操作</th>
    </tr>
    <c:forEach items="${students}" var="student">
      <tr>
        <td>${student.name}</td>
        <td>${student.birthday}</td>
        <td>${student.description}</td>
        <td>${student.avgScore}</td>
        <td>
          <a href="/student?action=edit&id=${student.id}">修改</a>
          <a href="/student?action=delete&id=${student.id}">删除</a>
        </td>
      </tr>
    </c:forEach>
  </table>
  <div class="pagination">
    <c:forEach var="i" begin="1" end="${totalPages}">
      <a href="/student?action=list&page=${i}">${i}</a>
    </c:forEach>
  </div>
</div>
</body>
</html>