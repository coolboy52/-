package wen.servlet;


import com.sun.deploy.util.StringUtils;
import redis.clients.jedis.Jedis;

import wen.pojo.Student;
import wen.utils.RedisPoolUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


@WebServlet("/student")
public class StudentServlet extends HttpServlet {
    // Redis连接池
    private Jedis jedis = RedisPoolUtils.getJedis();

    /**
     * 处理 GET 请求
     * @param request 请求对象
     * @param response 响应对象
     * @throws ServletException 抛出异常
     * @throws IOException  抛出异常
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置请求和响应的字符编码
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if ("list".equals(action)) {
            listStudents(request, response);
        } else if ("edit".equals(action)) {
            editStudent(request, response);
        } else if ("delete".equals(action)) {
            deleteStudent(request, response);
        }
    }

    // 处理 POST 请求
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("save".equals(action)) {
            saveStudent(request, response);
        }
    }

    // 保存学生信息（新增或修改）
    private void saveStudent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置请求和响应的字符编码
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        // 获取表单数据
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String birthday = request.getParameter("birthday");
        String description = request.getParameter("description");
        int avgScore = Integer.parseInt(request.getParameter("avgScore"));


            // 如果 id 为空，说明是新增学生
            if (id == null || id.isEmpty()) {
                id = UUID.randomUUID().toString();  // 生成唯一的学生 ID
            }

            // 保存学生信息到 Redis 哈希表
            Map<String, String> studentData = new HashMap<>();
            studentData.put("name", name);
            studentData.put("birthday", birthday);
            studentData.put("description", description);
            studentData.put("avgScore", String.valueOf(avgScore));



            jedis.hmset("H:student:" + id, studentData);

            // 将学生信息按平均分存入排序集合 ZSET 中
            jedis.zadd("Z:students_avgScore", avgScore, id);


        // 重定向到学生列表页面
        response.sendRedirect("/student?action=list&page=1");
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            // 获取当前页码
            // 从请求中获取当前页码
            int page = Integer.parseInt(request.getParameter("page"));
            // 设置每页显示的学生人数
            int pageSize = 10;
            // 使用Redis命令获取集合中学生的总数
            // 这里的集合键为"Z:students_avgScore"，用于存储学生的平均分数
            long totalStudents = jedis.zcard("Z:students_avgScore");

            // 计算分页查询的起始索引和结束索引
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize - 1, (int) totalStudents - 1);

            // 从Redis中按分数降序获取指定范围内的学生ID列表
            List<String> studentIds = jedis.zrevrange("Z:students_avgScore", start, end);

            // 创建一个列表用于存储学生对象
            List<Student> students = new ArrayList<>();
            // 遍历学生ID列表，为每个学生创建一个Student对象，并添加到学生列表中
            for (String studentId : studentIds) {
                // 从Redis中获取学生的所有信息
                Map<String, String> studentData = jedis.hgetAll("H:student:" + studentId);
                Student student = new Student();
                student.setId(studentId);
                student.setName(studentData.get("name"));
                student.setBirthday(studentData.get("birthday"));
                student.setDescription(studentData.get("description"));
                student.setAvgScore(Integer.parseInt(studentData.get("avgScore")));
                students.add(student);
            }
            for (Student s :students) {
                System.out.println(s.getName());
                System.out.println(s.getId());
            }
            request.setAttribute("students", students);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", (totalStudents / pageSize) + (totalStudents % pageSize == 0 ? 0 : 1));
            RequestDispatcher dispatcher = request.getRequestDispatcher("/student_list.jsp");
            dispatcher.forward(request, response);

    }

    /**
     * 编辑学生信息的方法
     *
     * 该方法从HTTP请求中提取学生ID，然后从Redis数据库中获取该学生的所有信息，
     * 并将这些信息设置到请求属性中，最后转发请求到学生信息编辑页面
     *
     * @param request HTTP请求对象，用于获取请求参数和设置请求属性
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException 如果Servlet操作失败
     * @throws IOException 如果输入输出操作失败
     */
    private void editStudent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 从请求参数中获取学生ID
        String studentId = request.getParameter("id");

        // 从Redis中获取指定学生的所有信息
        Map<String, String> studentData = jedis.hgetAll("H:student:" + studentId);
        //判空
        if (!(studentId == null || studentId.isEmpty())){
            studentData.put("id", studentId);
        }

        // 将学生信息设置到请求属性中
        request.setAttribute("student", studentData);
        // 获取请求分发器
        RequestDispatcher dispatcher = request.getRequestDispatcher("/student_edit.jsp");
        // 将请求转发到学生信息编辑页面
        dispatcher.forward(request, response);
    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String studentId = request.getParameter("id");


            Set<String> hkeys = jedis.hkeys("H:student:" +studentId);
            for (String field : hkeys) {
                jedis.hdel("H:student:" + studentId,field);
                jedis.zrem("Z:students_avgScore", studentId);
            }


        response.sendRedirect("/student?action=list&page=1");
    }

    // Create student method, etc.
}
