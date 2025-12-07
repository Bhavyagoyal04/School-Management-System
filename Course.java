import java.sql.*;

public class Course
{
    private String courseCode;
    private Subject subject;
    private int credits;
    private double fee;
    
    public Course(String courseCode, Subject subject, int credits, double fee)
    {
        this.courseCode = courseCode;
        this.subject = subject;
        this.credits = credits;
        this.fee = fee;
    }
    
    public String getCourseCode()
    {
        return courseCode;
    }
    
    public Subject getSubject()
    {
        return subject;
    }
    
    public int getCredits()
    {
        return credits;
    }
    
    public double getFee()
    {
        return fee;
    }
    
    public void displayInfo()
    {
        System.out.println("Course Information:");
        System.out.println("Course Code: " + courseCode);
        System.out.println("Subject: " + subject.getDisplayName());
        System.out.println("Credits: " + credits);
        System.out.println("Fee: " + fee);
    }
    
    public void saveToDB()
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try
        {
            conn = DatabaseConnection.getConnection();
            
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM courses WHERE course_code = ?"
            );
            checkStmt.setString(1, courseCode);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();
            
            if (count > 0)
            {
                pstmt = conn.prepareStatement(
                    "UPDATE courses SET subject = ?, credits = ?, fee = ? WHERE course_code = ?"
                );
                pstmt.setString(1, subject.getDisplayName());
                pstmt.setInt(2, credits);
                pstmt.setDouble(3, fee);
                pstmt.setString(4, courseCode);
            }
            else
            {
                pstmt = conn.prepareStatement(
                    "INSERT INTO courses (course_code, subject, credits, fee) VALUES (?, ?, ?, ?)"
                );
                pstmt.setString(1, courseCode);
                pstmt.setString(2, subject.getDisplayName());
                pstmt.setInt(3, credits);
                pstmt.setDouble(4, fee);
            }
            
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Error saving course data: " + e.getMessage());
        }
        finally
        {
            DatabaseConnection.closeResources(null, pstmt, conn);
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
        
        Course course = (Course) obj;
        return courseCode.equals(course.courseCode);
    }
    
    public int hashCode()
    {
        return courseCode.hashCode();
    }
}