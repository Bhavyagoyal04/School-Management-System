import java.sql.*;

public class DatabaseConnection
{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/school_management";
    private static final String USER = "root";
    private static final String PASS = "Bhavya@123";
    
    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    
    public static void closeResources(ResultSet rs, Statement stmt, Connection conn)
    {
        try
        {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
        catch (SQLException e)
        {
            System.err.println("Error closing database resources: " + e.getMessage());
        }
    }
    
    public static void initializeDatabase()
    {
        Connection conn = null;
        Statement stmt = null;
        
        try
        {
            conn = getConnection();
            stmt = conn.createStatement();
            
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                      "roll_number INTEGER PRIMARY KEY," +
                      "name TEXT NOT NULL," +
                      "age INTEGER," +
                      "email TEXT," +
                      "phone TEXT)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS teachers (" +
                      "name TEXT PRIMARY KEY," +
                      "age INTEGER," +
                      "specialization TEXT," +
                      "email TEXT," +
                      "phone TEXT," +
                      "salary REAL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS courses (" +
                      "course_code TEXT PRIMARY KEY," +
                      "subject TEXT NOT NULL," +
                      "credits INTEGER," +
                      "fee REAL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS enrollments (" +
                      "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                      "student_roll INTEGER," +
                      "course_code TEXT," +
                      "FOREIGN KEY (student_roll) REFERENCES students(roll_number)," +
                      "FOREIGN KEY (course_code) REFERENCES courses(course_code)," +
                      "UNIQUE(student_roll, course_code))");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS teaching_assignments (" +
                      "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                      "teacher_name TEXT," +
                      "course_code TEXT," +
                      "FOREIGN KEY (teacher_name) REFERENCES teachers(name)," +
                      "FOREIGN KEY (course_code) REFERENCES courses(course_code)," +
                      "UNIQUE(teacher_name, course_code))");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS marks (" +
                      "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                      "student_roll INTEGER," +
                      "subject TEXT," +
                      "marks REAL," +
                      "FOREIGN KEY (student_roll) REFERENCES students(roll_number)," +
                      "UNIQUE(student_roll, subject))");
            
            System.out.println("Database initialized successfully");
            
        }
        catch (SQLException e)
        {
            System.err.println("Error initializing database: " + e.getMessage());
        }
        finally
        {
            closeResources(null, stmt, conn);
        }
    }
}