import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SchoolManagementSwing extends JFrame
{
    private static final String SCHOOL_NAME = "Java Academy";
    
    private JTabbedPane tabbedPane;
    private Admin admin;
    
    public SchoolManagementSwing()
    {
        super(SCHOOL_NAME + " Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        admin = new Admin();
        admin.loadData();
        
        initUI();
    }
    
    private void initUI()
    {
        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Students", createStudentsPanel());
        tabbedPane.addTab("Teachers", createTeachersPanel());
        tabbedPane.addTab("Courses", createCoursesPanel());
        tabbedPane.addTab("Enrollment", createEnrollmentPanel());
        tabbedPane.addTab("Marks", createMarksPanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        add(tabbedPane);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save Data");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        saveItem.addActionListener(e -> Admin.saveData(admin));
        exitItem.addActionListener(e -> {
            Admin.saveData(admin);
            System.exit(0);
        });
        
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    
    private JPanel createStudentsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        
        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Roll No", "Name", "Age", "Email", "Phone"}
        );
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Student");
        JButton removeButton = new JButton("Remove Student");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> showAddStudentDialog());
        removeButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0)
            {
                int rollNo = Integer.parseInt(table.getValueAt(row, 0).toString());
                Admin.removeStudent(rollNo);
                refreshStudentTable(model);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select a student to remove");
            }
        });
        
        refreshButton.addActionListener(e -> refreshStudentTable(model));
        
        refreshStudentTable(model);
        return panel;
    }
    
    private void refreshStudentTable(DefaultTableModel model)
    {
        model.setRowCount(0);
        for (Student student : Admin.getAllStudents())
        {
            model.addRow(new Object[] {
                student.getRollNumber(),
                student.getName(),
                student.age,
                student.email,
                student.phoneNumber
            });
        }
    }
    
    private void showAddStudentDialog()
    {
        JTextField nameField = new JTextField(20);
        JTextField ageField = new JTextField(5);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(15);
        JTextField rollNoField = new JTextField(5);
        
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Roll Number:"));
        panel.add(rollNoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION)
        {
            try
            {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                int rollNo = Integer.parseInt(rollNoField.getText().trim());
                
                Student student = new Student(name, age, email, phone, rollNo);
                Admin.addStudent(student);
                
                DefaultTableModel model = (DefaultTableModel) ((JTable)((JScrollPane)((JPanel)tabbedPane.getComponentAt(0)).getComponent(0)).getViewport().getView()).getModel();
                refreshStudentTable(model);
            }
            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for age and roll number");
            }
        }
    }
    
    private JPanel createTeachersPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        
        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Name", "Age", "Email", "Phone", "Specialization", "Salary"}
        );
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Teacher");
        JButton payrollButton = new JButton("Process Payroll");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(payrollButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> showAddTeacherDialog());
        
        payrollButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0)
            {
                String name = table.getValueAt(row, 0).toString();
                for (Teacher teacher : Admin.getAllTeachers()) {
                    if (teacher.getName().equals(name)) {
                        teacher.processPayment();
                        JOptionPane.showMessageDialog(this, "Processed payroll for " + name);
                        break;
                    }
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select a teacher");
            }
        });
        
        refreshButton.addActionListener(e -> refreshTeacherTable(model));
        
        refreshTeacherTable(model);
        return panel;
    }
    
    private void refreshTeacherTable(DefaultTableModel model)
    {
        model.setRowCount(0);
        for (Teacher teacher : Admin.getAllTeachers())
        {
            model.addRow(new Object[]
            {
                teacher.getName(),
                teacher.age,
                teacher.email,
                teacher.phoneNumber,
                teacher.getSpecialization().getDisplayName(),
                teacher.salary
            });
        }
    }
    
    private void showAddTeacherDialog()
    {
        JTextField nameField = new JTextField(20);
        JTextField ageField = new JTextField(5);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(15);
        JComboBox<String> subjectCombo = new JComboBox<>();
        JTextField salaryField = new JTextField(10);
        
        for (Subject subject : Subject.values())
        {
            subjectCombo.addItem(subject.getDisplayName());
        }
        
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Specialization:"));
        panel.add(subjectCombo);
        panel.add(new JLabel("Salary:"));
        panel.add(salaryField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Teacher", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION)
        {
            try
            {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                Subject specialization = Subject.values()[subjectCombo.getSelectedIndex()];
                double salary = Double.parseDouble(salaryField.getText().trim());
                
                Teacher teacher = new Teacher(name, age, email, phone, specialization, salary);
                Admin.addTeacher(teacher);
                
                DefaultTableModel model = (DefaultTableModel) ((JTable)((JScrollPane)((JPanel)tabbedPane.getComponentAt(1)).getComponent(0)).getViewport().getView()).getModel();
                refreshTeacherTable(model);
            }
            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for age and salary");
            }
        }
    }
    
    private JPanel createCoursesPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        
        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Course Code", "Subject", "Credits", "Fee"}
        );
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Course");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> showAddCourseDialog());
        refreshButton.addActionListener(e -> refreshCourseTable(model));
        
        refreshCourseTable(model);
        return panel;
    }
    
    private void refreshCourseTable(DefaultTableModel model)
    {
        model.setRowCount(0);
        for (Course course : Admin.getAllCourses())
        {
            model.addRow(new Object[] {
                course.getCourseCode(),
                course.getSubject().getDisplayName(),
                course.getCredits(),
                course.getFee()
            });
        }
    }
    
    private void showAddCourseDialog()
    {
        JTextField codeField = new JTextField(10);
        JComboBox<String> subjectCombo = new JComboBox<>();
        JTextField creditsField = new JTextField(5);
        JTextField feeField = new JTextField(10);
        
        for (Subject subject : Subject.values())
        {
            subjectCombo.addItem(subject.getDisplayName());
        }
        
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Course Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Subject:"));
        panel.add(subjectCombo);
        panel.add(new JLabel("Credits:"));
        panel.add(creditsField);
        panel.add(new JLabel("Fee:"));
        panel.add(feeField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION)
        {
            try
            {
                String code = codeField.getText().trim();
                Subject subject = Subject.values()[subjectCombo.getSelectedIndex()];
                int credits = Integer.parseInt(creditsField.getText().trim());
                double fee = Double.parseDouble(feeField.getText().trim());
                
                Course course = new Course(code, subject, credits, fee);
                Admin.addCourse(course);
                
                DefaultTableModel model = (DefaultTableModel) ((JTable)((JScrollPane)((JPanel)tabbedPane.getComponentAt(2)).getComponent(0)).getViewport().getView()).getModel();
                refreshCourseTable(model);
            }
            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for credits and fee");
            }
        }
    }
    
    private JPanel createEnrollmentPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel selectionPanel = new JPanel(new GridLayout(2, 2));
        
        JComboBox<String> studentCombo = new JComboBox<>();
        JComboBox<String> courseCombo = new JComboBox<>();
        
        selectionPanel.add(new JLabel("Select Student:"));
        selectionPanel.add(studentCombo);
        selectionPanel.add(new JLabel("Select Course:"));
        selectionPanel.add(courseCombo);
        
        JButton enrollButton = new JButton("Enroll Student in Course");
        JButton assignButton = new JButton("Assign Teacher to Course");
        JButton refreshButton = new JButton("Refresh Lists");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(enrollButton);
        buttonPanel.add(assignButton);
        buttonPanel.add(refreshButton);
        
        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        
        refreshComboBoxes(studentCombo, courseCombo);
        
        enrollButton.addActionListener(e -> {
            if (studentCombo.getSelectedIndex() != -1 && courseCombo.getSelectedIndex() != -1)
            {
                String studentInfo = studentCombo.getSelectedItem().toString();
                String courseInfo = courseCombo.getSelectedItem().toString();
                
                int rollNo = Integer.parseInt(studentInfo.split(" - ")[0]);
                String courseCode = courseInfo.split(" - ")[0];
                
                Student selectedStudent = null;
                Course selectedCourse = null;
                
                for (Student s : Admin.getAllStudents())
                {
                    if (s.getRollNumber() == rollNo)
                    {
                        selectedStudent = s;
                        break;
                    }
                }
                
                for (Course c : Admin.getAllCourses())
                {
                    if (c.getCourseCode().equals(courseCode))
                    {
                        selectedCourse = c;
                        break;
                    }
                }
                
                if (selectedStudent != null && selectedCourse != null)
                {
                    selectedStudent.enrollCourse(selectedCourse);
                    infoArea.append("Student " + selectedStudent.getName() + " enrolled in " + selectedCourse.getSubject().getDisplayName() + "\n");
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select both a student and a course");
            }
        });
        
        assignButton.addActionListener(e -> {
            JComboBox<String> teacherCombo = new JComboBox<>();
            for (Teacher t : Admin.getAllTeachers())
            {
                teacherCombo.addItem(t.getName() + " - " + t.getSpecialization().getDisplayName());
            }
            
            JPanel assignPanel = new JPanel(new GridLayout(0, 2));
            assignPanel.add(new JLabel("Select Teacher:"));
            assignPanel.add(teacherCombo);
            assignPanel.add(new JLabel("Select Course:"));
            JComboBox<String> assignCourseCombo = new JComboBox<>();
            for (Course c : Admin.getAllCourses())
            {
                assignCourseCombo.addItem(c.getCourseCode() + " - " + c.getSubject().getDisplayName());
            }
            assignPanel.add(assignCourseCombo);
            
            int result = JOptionPane.showConfirmDialog(this, assignPanel, "Assign Teacher to Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
            if (result == JOptionPane.OK_OPTION && teacherCombo.getSelectedIndex() != -1 && assignCourseCombo.getSelectedIndex() != -1)
            {
                String teacherName = teacherCombo.getSelectedItem().toString().split(" - ")[0];
                String courseCode = assignCourseCombo.getSelectedItem().toString().split(" - ")[0];
                
                Teacher selectedTeacher = null;
                Course selectedCourse = null;
                
                for (Teacher t : Admin.getAllTeachers())
                {
                    if (t.getName().equals(teacherName))
                    {
                        selectedTeacher = t;
                        break;
                    }
                }
                
                for (Course c : Admin.getAllCourses())
                {
                    if (c.getCourseCode().equals(courseCode))
                    {
                        selectedCourse = c;
                        break;
                    }
                }
                
                if (selectedTeacher != null && selectedCourse != null)
                {
                    try
                    {
                        if (!selectedTeacher.canTeach(selectedCourse))
                        {
                            JOptionPane.showMessageDialog(this, 
                                "Teacher " + selectedTeacher.getName() + " cannot teach " + 
                                selectedCourse.getSubject().getDisplayName() + " because their specialization is " + 
                                selectedTeacher.getSpecialization().getDisplayName(),
                                "Specialization Mismatch", JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {
                            selectedTeacher.assignCourse(selectedCourse);
                            infoArea.append("Teacher " + selectedTeacher.getName() + " assigned to " + selectedCourse.getSubject().getDisplayName() + "\n");
                        }
                    }
                    catch (Exception ex)
                    {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        refreshButton.addActionListener(e -> refreshComboBoxes(studentCombo, courseCombo));
        
        return panel;
    }
    
    private void refreshComboBoxes(JComboBox<String> studentCombo, JComboBox<String> courseCombo)
    {
        studentCombo.removeAllItems();
        courseCombo.removeAllItems();
        
        for (Student s : Admin.getAllStudents())
        {
            studentCombo.addItem(s.getRollNumber() + " - " + s.getName());
        }
        
        for (Course c : Admin.getAllCourses())
        {
            courseCombo.addItem(c.getCourseCode() + " - " + c.getSubject().getDisplayName());
        }
    }
    
    private JPanel createMarksPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        
        JComboBox<String> studentCombo = new JComboBox<>();
        
        for (Student s : Admin.getAllStudents())
        {
            studentCombo.addItem(s.getRollNumber() + " - " + s.getName());
        }
        
        JButton selectButton = new JButton("Select Student");
        JButton refreshButton = new JButton("Refresh");
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Student:"));
        topPanel.add(studentCombo);
        topPanel.add(selectButton);
        topPanel.add(refreshButton);
        
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        
        selectButton.addActionListener(e -> {
            if (studentCombo.getSelectedIndex() != -1)
            {
                String selectedItem = studentCombo.getSelectedItem().toString();
                int rollNo = Integer.parseInt(selectedItem.split(" - ")[0]);
                
                Student selectedStudent = null;
                for (Student s : Admin.getAllStudents())
                {
                    if (s.getRollNumber() == rollNo)
                    {
                        selectedStudent = s;
                        break;
                    }
                }
                
                if (selectedStudent != null)
                {
                    List<Course> enrolledCourses = selectedStudent.getEnrolledCourses();
                    
                    if (enrolledCourses.isEmpty())
                    {
                        JOptionPane.showMessageDialog(this, "This student is not enrolled in any courses");
                    }
                    else
                    {
                        String[] courseOptions = new String[enrolledCourses.size()];
                        for (int i = 0; i < enrolledCourses.size(); i++)
                        {
                            Course course = enrolledCourses.get(i);
                            courseOptions[i] = course.getCourseCode() + " - " + course.getSubject().getDisplayName();
                        }
                        
                        String selectedCourseStr = (String) JOptionPane.showInputDialog(this, "Select Course:", "Add Marks", JOptionPane.QUESTION_MESSAGE, null, courseOptions, courseOptions[0]);
                            
                        if (selectedCourseStr != null)
                        {
                            String courseCode = selectedCourseStr.split(" - ")[0];
                            Course selectedCourse = null;
                            
                            for (Course c : enrolledCourses)
                            {
                                if (c.getCourseCode().equals(courseCode))
                                {
                                    selectedCourse = c;
                                    break;
                                }
                            }
                            
                            if (selectedCourse != null)
                            {
                                String marksStr = JOptionPane.showInputDialog(this, "Enter marks (0-100):", "Add Marks", JOptionPane.QUESTION_MESSAGE);
                                    
                                if (marksStr != null)
                                {
                                    try
                                    {
                                        double marks = Double.parseDouble(marksStr);
                                        selectedStudent.addMarksByCourse(selectedCourse, marks);
                                        infoArea.append("Added marks " + marks + " for " + 
                                            selectedStudent.getName() + " in " + 
                                            selectedCourse.getSubject().getDisplayName() + "\n");
                                    }
                                    catch (NumberFormatException ex)
                                    {
                                        JOptionPane.showMessageDialog(this, "Please enter a valid number");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            studentCombo.removeAllItems();
            for (Student s : Admin.getAllStudents())
            {
                studentCombo.addItem(s.getRollNumber() + " - " + s.getName());
            }
        });
        
        return panel;
    }
    
    private JPanel createReportsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        
        JComboBox<String> studentCombo = new JComboBox<>();
        
        for (Student s : Admin.getAllStudents())
        {
            studentCombo.addItem(s.getRollNumber() + " - " + s.getName());
        }
        
        JButton generateButton = new JButton("Generate Report");
        JButton refreshButton = new JButton("Refresh");
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Student:"));
        topPanel.add(studentCombo);
        topPanel.add(generateButton);
        topPanel.add(refreshButton);
        
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        
        generateButton.addActionListener(e -> {
            if (studentCombo.getSelectedIndex() != -1)
            {
                String selectedItem = studentCombo.getSelectedItem().toString();
                int rollNo = Integer.parseInt(selectedItem.split(" - ")[0]);
                
                Student selectedStudent = null;
                for (Student s : Admin.getAllStudents())
                {
                    if (s.getRollNumber() == rollNo)
                    {
                        selectedStudent = s;
                        break;
                    }
                }
                
                if (selectedStudent != null)
                {
                    reportArea.setText("");
                    
                    PrintStream originalOut = System.out;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PrintStream captureOut = new PrintStream(baos);
                    System.setOut(captureOut);
                    
                    selectedStudent.displayInfo();
                    System.out.println();
                    selectedStudent.generateReport();
                    
                    double totalFees = selectedStudent.calculateTotalFees();
                    System.out.println("\nTotal Course Fees: " + totalFees);
                    
                    System.setOut(originalOut);
                    reportArea.setText(baos.toString());
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            studentCombo.removeAllItems();
            for (Student s : Admin.getAllStudents())
            {
                studentCombo.addItem(s.getRollNumber() + " - " + s.getName());
            }
        });
        
        return panel;
    }
    
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        DatabaseConnection.initializeDatabase();
        
        SwingUtilities.invokeLater(() -> {
            SchoolManagementSwing app = new SchoolManagementSwing();
            app.setVisible(true);
        });
    }
}