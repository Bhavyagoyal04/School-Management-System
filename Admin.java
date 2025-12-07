import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Admin
{
    private static List<Student> students = new ArrayList<>();
    private static List<Teacher> teachers = new ArrayList<>();
    private static List<Course> courses = new ArrayList<>();
    
    public void loadData()
    {
        loadStudentsFromDB();
        loadTeachersFromDB();
        loadCoursesFromDB();
    }
    
    private void loadStudentsFromDB()
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM students");
            
            while (rs.next())
            {
                int rollNumber = rs.getInt("roll_number");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                
                Student student = new Student(name, age, email, phone, rollNumber);
                students.add(student);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error loading students from database: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(rs, stmt, conn);
        }
    }
    
    private void loadTeachersFromDB()
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM teachers");
            
            while (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String specializationStr = rs.getString("specialization");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                double salary = rs.getDouble("salary");
                
                Subject specialization = Subject.fromDisplayName(specializationStr);
                
                Teacher teacher = new Teacher(name, age, email, phone, specialization, salary);
                teachers.add(teacher);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error loading teachers from database: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(rs, stmt, conn);
        }
    }
    
    private void loadCoursesFromDB()
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM courses");
            
            while (rs.next())
            {
                String courseCode = rs.getString("course_code");
                String subjectStr = rs.getString("subject");
                int credits = rs.getInt("credits");
                double fee = rs.getDouble("fee");
                
                Subject subject = Subject.fromDisplayName(subjectStr);
                
                Course course = new Course(courseCode, subject, credits, fee);
                courses.add(course);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error loading courses from database: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(rs, stmt, conn);
        }
    }
    
    public static void saveData(Admin admin)
    {
        for (Student student : students)
        {
            student.saveToDB();
        }
        
        for (Teacher teacher : teachers)
        {
            teacher.saveToDB();
        }
        
        for (Course course : courses)
        {
            course.saveToDB();
        }
        
        System.out.println("All data saved successfully!");
    }
    
    public static List<Student> getAllStudents()
    {
        return students;
    }
    
    public static List<Teacher> getAllTeachers()
    {
        return teachers;
    }
    
    public static List<Course> getAllCourses()
    {
        return courses;
    }
    
    public static void addStudent(Student student)
    {
        if (!students.contains(student))
        {
            students.add(student);
            student.saveToDB();
        }
    }
    
    public static void addTeacher(Teacher teacher)
    {
        if (!teachers.contains(teacher))
        {
            teachers.add(teacher);
            teacher.saveToDB();
        }
    }
    
    public static void addCourse(Course course)
    {
        if (!courses.contains(course))
        {
            courses.add(course);
            course.saveToDB();
        }
    }
    
    public static void removeStudent(int rollNumber)
    {
        Student studentToRemove = null;
        for (Student student : students)
        {
            if (student.getRollNumber() == rollNumber)
            {
                studentToRemove = student;
                break;
            }
        }
        
        if (studentToRemove != null)
        {
            students.remove(studentToRemove);
            removeStudentFromDB(rollNumber);
        }
    }
    
    private static void removeStudentFromDB(int rollNumber)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            
            pstmt = conn.prepareStatement("DELETE FROM enrollments WHERE student_roll = ?");
            pstmt.setInt(1, rollNumber);
            pstmt.executeUpdate();
            pstmt.close();
            
            pstmt = conn.prepareStatement("DELETE FROM marks WHERE student_roll = ?");
            pstmt.setInt(1, rollNumber);
            pstmt.executeUpdate();
            pstmt.close();
            
            pstmt = conn.prepareStatement("DELETE FROM students WHERE roll_number = ?");
            pstmt.setInt(1, rollNumber);
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Error removing student from database: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
    }
}