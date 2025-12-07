import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends Person implements Academic, Displayable
{
    private int rollNumber;
    private Map<Subject, Double> subjectMarks;
    private List<Course> enrolledCourses;
    
    public Student(String name, int age, String email, String phoneNumber, int rollNumber)
    {
        super(name, age, email, phoneNumber);
        this.rollNumber = rollNumber;
        this.subjectMarks = new HashMap<>();
        this.enrolledCourses = new ArrayList<>();
        loadEnrolledCoursesFromDB();
        loadMarksFromDB();
    }
    
    private void loadEnrolledCoursesFromDB()
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                "SELECT c.course_code, c.subject, c.credits, c.fee FROM enrollments e " +
                "JOIN courses c ON e.course_code = c.course_code " +
                "WHERE e.student_roll = ?"
            );
            pstmt.setInt(1, rollNumber);
            rs = pstmt.executeQuery();
            
            while (rs.next())
            {
                String courseCode = rs.getString("course_code");
                String subjectStr = rs.getString("subject");
                int credits = rs.getInt("credits");
                double fee = rs.getDouble("fee");
                
                Subject subject = Subject.fromDisplayName(subjectStr);
                Course course = new Course(courseCode, subject, credits, fee);
                enrolledCourses.add(course);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error loading enrolled courses: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
    }
    
    private void loadMarksFromDB()
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                "SELECT m.subject, m.marks FROM marks m WHERE m.student_roll = ?"
            );
            pstmt.setInt(1, rollNumber);
            rs = pstmt.executeQuery();
            
            while (rs.next())
            {
                String subjectStr = rs.getString("subject");
                double marks = rs.getDouble("marks");
                
                Subject subject = Subject.fromDisplayName(subjectStr);
                subjectMarks.put(subject, marks);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error loading marks: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
    }
    
    public void enrollCourse(Course course)
    {
        if (!enrolledCourses.contains(course))
        {
            enrolledCourses.add(course);
            saveEnrollmentToDB(course);
        }
    }
    
    private void saveEnrollmentToDB(Course course)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                "INSERT INTO enrollments (student_roll, course_code) VALUES (?, ?)"
            );
            pstmt.setInt(1, rollNumber);
            pstmt.setString(2, course.getCourseCode());
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Error saving enrollment: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
    }
    
    public void addMarksByCourse(Course course, double marks)
    {
        Subject subject = course.getSubject();
        subjectMarks.put(subject, marks);
        saveMarksToDB(subject, marks);
    }
    
    private void saveMarksToDB(Subject subject, double marks)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM marks WHERE student_roll = ? AND subject = ?"
            );
            checkStmt.setInt(1, rollNumber);
            checkStmt.setString(2, subject.getDisplayName());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();
            
            if (count > 0)
            {
                pstmt = conn.prepareStatement(
                    "UPDATE marks SET marks = ? WHERE student_roll = ? AND subject = ?"
                );
                pstmt.setDouble(1, marks);
                pstmt.setInt(2, rollNumber);
                pstmt.setString(3, subject.getDisplayName());
            }
            else
            {
                pstmt = conn.prepareStatement(
                    "INSERT INTO marks (student_roll, subject, marks) VALUES (?, ?, ?)"
                );
                pstmt.setInt(1, rollNumber);
                pstmt.setString(2, subject.getDisplayName());
                pstmt.setDouble(3, marks);
            }
            
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Error saving marks: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
    }
    
    public int getRollNumber()
    {
        return rollNumber;
    }
    
    public List<Course> getEnrolledCourses()
    {
        return enrolledCourses;
    }
    
    public Map<Subject, Double> getSubjectMarks()
    {
        return subjectMarks;
    }
    
    public double calculateTotalFees()
    {
        double totalFees = 0.0;
        for (Course course : enrolledCourses)
        {
            totalFees += course.getFee();
        }
        return totalFees;
    }
    
    public void displayInfo()
    {
        System.out.println("Student Information:");
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Roll Number: " + rollNumber);
        System.out.println("Email: " + email);
        System.out.println("Phone Number: " + phoneNumber);
    }
    
    public void generateReport()
    {
        System.out.println("\nAcademic Report for " + name + " (Roll No: " + rollNumber + ")");
        System.out.println("===============================================");
        
        if (enrolledCourses.isEmpty())
        {
            System.out.println("No courses enrolled.");
            return;
        }
        
        System.out.println("Enrolled Courses:");
        System.out.printf("%-9s %-19s %-8s %-10s %-10s\n", "Code", "Subject", "Credits", "Marks", "Grade");
        System.out.println("--------------------------------------------------------------");
        
        double totalCredits = 0;
        double totalGradePoints = 0;
        
        for (Course course : enrolledCourses)
        {
            Subject subject = course.getSubject();
            String courseCode = course.getCourseCode();
            int credits = course.getCredits();
            totalCredits += credits;
            
            Double marks = subjectMarks.getOrDefault(subject, 0.0);
            String grade = calculateGrade(marks);
            double gradePoints = calculateGradePoints(grade);
            totalGradePoints += (gradePoints * credits);
            
            System.out.printf("%-10s %-20s %-11d %-10.2f %-12s\n", courseCode, subject.getDisplayName(), credits, marks, grade);
        }
        
        double gpa = (totalCredits > 0) ? (totalGradePoints / totalCredits) : 0.0;
        System.out.println("--------------------------------------------------------------");
        System.out.printf("Total Credits: %.1f\n", totalCredits);
        System.out.printf("GPA: %.2f\n", gpa);
    }
    
    private String calculateGrade(double marks)
    {
        if (marks >= 90) return "A+";
        else if (marks >= 80) return "A";
        else if (marks >= 70) return "B+";
        else if (marks >= 60) return "B";
        else if (marks >= 50) return "C";
        else if (marks >= 40) return "D";
        else return "F";
    }
    
    private double calculateGradePoints(String grade)
    {
        switch (grade)
        {
            case "A+": return 4.0;
            case "A": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            default: return 0.0;
        }
    }
    
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        
        Student student = (Student) obj;
        return rollNumber == student.rollNumber;
    }
    
    public int hashCode()
    {
        return Integer.hashCode(rollNumber);
    }
    
    public boolean isEnrolledIn(Course course)
    {
        return enrolledCourses.contains(course);
    }
    
    public void withdrawCourse(Course course)
    {
        if (enrolledCourses.remove(course))
        {
            removeEnrollmentFromDB(course);
        }
    }
    
    private void removeEnrollmentFromDB(Course course)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                "DELETE FROM enrollments WHERE student_roll = ? AND course_code = ?"
            );
            pstmt.setInt(1, rollNumber);
            pstmt.setString(2, course.getCourseCode());
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Error removing enrollment: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(null, pstmt, conn);
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
                "SELECT COUNT(*) FROM students WHERE roll_number = ?"
            );
            checkStmt.setInt(1, rollNumber);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();
            
            if (count > 0)
            {
                pstmt = conn.prepareStatement(
                    "UPDATE students SET name = ?, age = ?, email = ?, phone = ? WHERE roll_number = ?"
                );
                pstmt.setString(1, name);
                pstmt.setInt(2, age);
                pstmt.setString(3, email);
                pstmt.setString(4, phoneNumber);
                pstmt.setInt(5, rollNumber);
            }
            else
            {
                pstmt = conn.prepareStatement(
                    "INSERT INTO students (roll_number, name, age, email, phone) VALUES (?, ?, ?, ?, ?)"
                );
                pstmt.setInt(1, rollNumber);
                pstmt.setString(2, name);
                pstmt.setInt(3, age);
                pstmt.setString(4, email);
                pstmt.setString(5, phoneNumber);
            }
            
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Error saving student data: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
    }
}