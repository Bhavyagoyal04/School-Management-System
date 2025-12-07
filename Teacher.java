import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Teacher extends Person implements Displayable
{
    private Subject specialization;
    public double salary;
    private List<Course> assignedCourses;
    
    public Teacher(String name, int age, String email, String phoneNumber, Subject specialization, double salary)
    {
        super(name, age, email, phoneNumber);
        this.specialization = specialization;
        this.salary = salary;
        this.assignedCourses = new ArrayList<>();
        loadAssignedCoursesFromDB();
    }
    
    private void loadAssignedCoursesFromDB()
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                "SELECT c.course_code, c.subject, c.credits, c.fee FROM teaching_assignments tc " +
                "JOIN courses c ON tc.course_code = c.course_code " +
                "WHERE tc.teacher_name = ?"
            );
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            
            while (rs.next())
            {
                String courseCode = rs.getString("course_code");
                String subjectStr = rs.getString("subject");
                int credits = rs.getInt("credits");
                double fee = rs.getDouble("fee");
                
                Subject subject = Subject.fromDisplayName(subjectStr);
                Course course = new Course(courseCode, subject, credits, fee);
                assignedCourses.add(course);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error loading assigned courses: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
    }
    
    public boolean canTeach(Course course)
    {
        return this.specialization == course.getSubject();
    }
    
    public void assignCourse(Course course)
    {
        if (!canTeach(course))
        {
            throw new IllegalArgumentException("Teacher " + name + " cannot teach " + course.getSubject().getDisplayName() + " because their specialization is " + specialization.getDisplayName());
        }
        
        if (!assignedCourses.contains(course))
        {
            assignedCourses.add(course);
            saveAssignmentToDB(course);
        }
    }
    
    private void saveAssignmentToDB(Course course)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                "INSERT INTO teaching_assignments (teacher_name, course_code) VALUES (?, ?)"
            );
            pstmt.setString(1, name);
            pstmt.setString(2, course.getCourseCode());
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Error saving course assignment: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
    }
    
    public Subject getSpecialization()
    {
        return specialization;
    }
    
    public double getSalary()
    {
        return salary;
    }
    
    public List<Course> getAssignedCourses()
    {
        return assignedCourses;
    }
    
    public void processPayment()
    {
        System.out.println("Processing payment of " + salary + " for " + name);
    }
    
    public void displayInfo()
    {
        System.out.println("Teacher Information:");
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Specialization: " + specialization.getDisplayName());
        System.out.println("Email: " + email);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Salary: " + salary);
        
        if (!assignedCourses.isEmpty())
        {
            System.out.println("Assigned Courses:");
            for (Course course : assignedCourses)
            {
                System.out.println("- " + course.getCourseCode() + ": " + course.getSubject().getDisplayName());
            }
        }
        else
        {
            System.out.println("No courses assigned.");
        }
    }
    
    public void saveToDB()
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM teachers WHERE name = ?"
            );
            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();
            
            if (count > 0)
            {
                pstmt = conn.prepareStatement(
                    "UPDATE teachers SET age = ?, specialization = ?, email = ?, phone = ?, salary = ? WHERE name = ?"
                );
                pstmt.setInt(1, age);
                pstmt.setString(2, specialization.getDisplayName());
                pstmt.setString(3, email);
                pstmt.setString(4, phoneNumber);
                pstmt.setDouble(5, salary);
                pstmt.setString(6, name);
            }
            else
            {
                pstmt = conn.prepareStatement(
                    "INSERT INTO teachers (name, age, specialization, email, phone, salary) VALUES (?, ?, ?, ?, ?, ?)"
                );
                pstmt.setString(1, name);
                pstmt.setInt(2, age);
                pstmt.setString(3, specialization.getDisplayName());
                pstmt.setString(4, email);
                pstmt.setString(5, phoneNumber);
                pstmt.setDouble(6, salary);
            }
            
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Error saving teacher data: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
    }
    
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Teacher teacher = (Teacher) obj;
        return name.equals(teacher.name);
    }
    
    public int hashCode()
    {
        return name.hashCode();
    }
}