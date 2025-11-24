import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

// Main Application Class
public class StudentAnalyticsSystem extends JFrame {
    private StudentManager studentManager;
    private JTabbedPane tabbedPane;
    
    public StudentAnalyticsSystem() {
        studentManager = new StudentManager();
        initializeUI();
        setTitle("Student Performance Analytics System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void initializeUI() {
        tabbedPane = new JTabbedPane();
        
        // Add tabs
        tabbedPane.addTab("Students", createStudentPanel());
        tabbedPane.addTab("Add Marks", createMarksPanel());
        tabbedPane.addTab("Analytics", createAnalyticsPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        
        add(tabbedPane);
    }
    
    // Student Management Panel
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField rollField = new JTextField();
        JTextField classField = new JTextField();
        
        inputPanel.add(new JLabel("Student Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Roll Number:"));
        inputPanel.add(rollField);
        inputPanel.add(new JLabel("Class:"));
        inputPanel.add(classField);
        
        JButton addButton = new JButton("Add Student");
        JButton removeButton = new JButton("Remove Student");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        
        inputPanel.add(buttonPanel);
        
        // Table Panel
        String[] columns = {"Roll No", "Name", "Class", "Avg Score", "Grade"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        
        // Add Student Action
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String roll = rollField.getText().trim();
            String className = classField.getText().trim();
            
            if (name.isEmpty() || roll.isEmpty() || className.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }
            
            Student student = new Student(roll, name, className);
            if (studentManager.addStudent(student)) {
                refreshStudentTable(tableModel);
                nameField.setText("");
                rollField.setText("");
                classField.setText("");
                JOptionPane.showMessageDialog(this, "Student added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Student with this roll number already exists!");
            }
        });
        
        // Remove Student Action
        removeButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                String rollNo = (String) tableModel.getValueAt(selectedRow, 0);
                studentManager.removeStudent(rollNo);
                refreshStudentTable(tableModel);
                JOptionPane.showMessageDialog(this, "Student removed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to remove!");
            }
        });
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        refreshStudentTable(tableModel);
        return panel;
    }
    
    // Marks Entry Panel
    private JPanel createMarksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        
        JComboBox<String> studentCombo = new JComboBox<>();
        JTextField subjectField = new JTextField();
        JTextField marksField = new JTextField();
        JTextField maxMarksField = new JTextField("100");
        
        inputPanel.add(new JLabel("Select Student:"));
        inputPanel.add(studentCombo);
        inputPanel.add(new JLabel("Subject:"));
        inputPanel.add(subjectField);
        inputPanel.add(new JLabel("Marks Obtained:"));
        inputPanel.add(marksField);
        inputPanel.add(new JLabel("Maximum Marks:"));
        inputPanel.add(maxMarksField);
        
        JButton addMarksButton = new JButton("Add Marks");
        inputPanel.add(addMarksButton);
        
        // Marks Table
        String[] columns = {"Roll No", "Name", "Subject", "Marks", "Max Marks", "Percentage"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable marksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(marksTable);
        
        // Populate student combo
        refreshStudentCombo(studentCombo);
        
        addMarksButton.addActionListener(e -> {
            String selected = (String) studentCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "No students available!");
                return;
            }
            
            String rollNo = selected.split(" - ")[0];
            String subject = subjectField.getText().trim();
            
            try {
                double marks = Double.parseDouble(marksField.getText().trim());
                double maxMarks = Double.parseDouble(maxMarksField.getText().trim());
                
                if (marks < 0 || maxMarks <= 0 || marks > maxMarks) {
                    JOptionPane.showMessageDialog(this, "Invalid marks!");
                    return;
                }
                
                Student student = studentManager.getStudent(rollNo);
                if (student != null) {
                    student.addMarks(subject, marks, maxMarks);
                    studentManager.saveData();
                    refreshMarksTable(tableModel);
                    subjectField.setText("");
                    marksField.setText("");
                    JOptionPane.showMessageDialog(this, "Marks added successfully!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for marks!");
            }
        });
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Analytics Panel
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea analyticsArea = new JTextArea();
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(analyticsArea);
        
        JButton refreshButton = new JButton("Refresh Analytics");
        refreshButton.addActionListener(e -> {
            analyticsArea.setText(generateAnalytics());
        });
        
        panel.add(refreshButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        analyticsArea.setText(generateAnalytics());
        return panel;
    }
    
    // Reports Panel
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel controlPanel = new JPanel();
        JComboBox<String> studentCombo = new JComboBox<>();
        refreshStudentCombo(studentCombo);
        
        JButton generateButton = new JButton("Generate Report");
        controlPanel.add(new JLabel("Select Student:"));
        controlPanel.add(studentCombo);
        controlPanel.add(generateButton);
        
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        
        generateButton.addActionListener(e -> {
            String selected = (String) studentCombo.getSelectedItem();
            if (selected != null) {
                String rollNo = selected.split(" - ")[0];
                Student student = studentManager.getStudent(rollNo);
                if (student != null) {
                    reportArea.setText(generateStudentReport(student));
                }
            }
        });
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Helper Methods
    private void refreshStudentTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Student student : studentManager.getAllStudents()) {
            model.addRow(new Object[]{
                student.getRollNumber(),
                student.getName(),
                student.getClassName(),
                String.format("%.2f", student.getAverageScore()),
                student.getGrade()
            });
        }
    }
    
    private void refreshMarksTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Student student : studentManager.getAllStudents()) {
            for (Mark mark : student.getMarks()) {
                model.addRow(new Object[]{
                    student.getRollNumber(),
                    student.getName(),
                    mark.getSubject(),
                    mark.getMarksObtained(),
                    mark.getMaxMarks(),
                    String.format("%.2f%%", mark.getPercentage())
                });
            }
        }
    }
    
    private void refreshStudentCombo(JComboBox<String> combo) {
        combo.removeAllItems();
        for (Student student : studentManager.getAllStudents()) {
            combo.addItem(student.getRollNumber() + " - " + student.getName());
        }
    }
    
    private String generateAnalytics() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== STUDENT PERFORMANCE ANALYTICS ===\n\n");
        
        List<Student> students = studentManager.getAllStudents();
        if (students.isEmpty()) {
            sb.append("No student data available.\n");
            return sb.toString();
        }
        
        // Overall Statistics
        double totalAvg = students.stream()
            .mapToDouble(Student::getAverageScore)
            .average()
            .orElse(0.0);
        
        sb.append("Total Students: ").append(students.size()).append("\n");
        sb.append("Class Average: ").append(String.format("%.2f", totalAvg)).append("\n\n");
        
        // Top Performers
        sb.append("--- TOP 5 PERFORMERS ---\n");
        students.stream()
            .sorted((s1, s2) -> Double.compare(s2.getAverageScore(), s1.getAverageScore()))
            .limit(5)
            .forEach(s -> sb.append(String.format("%s (%s): %.2f - %s\n", 
                s.getName(), s.getRollNumber(), s.getAverageScore(), s.getGrade())));
        
        sb.append("\n--- STUDENTS NEEDING ATTENTION ---\n");
        students.stream()
            .filter(s -> s.getAverageScore() < 50)
            .forEach(s -> sb.append(String.format("%s (%s): %.2f\n", 
                s.getName(), s.getRollNumber(), s.getAverageScore())));
        
        // Grade Distribution
        sb.append("\n--- GRADE DISTRIBUTION ---\n");
        Map<String, Long> gradeCount = new HashMap<>();
        for (Student s : students) {
            String grade = s.getGrade();
            gradeCount.put(grade, gradeCount.getOrDefault(grade, 0L) + 1);
        }
        gradeCount.forEach((grade, count) -> 
            sb.append(String.format("%s: %d students\n", grade, count)));
        
        return sb.toString();
    }
    
    private String generateStudentReport(Student student) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== STUDENT PERFORMANCE REPORT ===\n\n");
        sb.append("Name: ").append(student.getName()).append("\n");
        sb.append("Roll Number: ").append(student.getRollNumber()).append("\n");
        sb.append("Class: ").append(student.getClassName()).append("\n\n");
        
        sb.append("--- SUBJECT WISE PERFORMANCE ---\n");
        for (Mark mark : student.getMarks()) {
            sb.append(String.format("%-15s: %6.2f / %.2f (%.2f%%)\n", 
                mark.getSubject(), 
                mark.getMarksObtained(), 
                mark.getMaxMarks(), 
                mark.getPercentage()));
        }
        
        sb.append("\n--- OVERALL PERFORMANCE ---\n");
        sb.append(String.format("Average Score: %.2f\n", student.getAverageScore()));
        sb.append(String.format("Grade: %s\n", student.getGrade()));
        
        return sb.toString();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentAnalyticsSystem().setVisible(true);
        });
    }
}

// Student Class
class Student implements Serializable {
    private String rollNumber;
    private String name;
    private String className;
    private List<Mark> marks;
    
    public Student(String rollNumber, String name, String className) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.className = className;
        this.marks = new ArrayList<>();
    }
    
    public void addMarks(String subject, double obtained, double max) {
        marks.add(new Mark(subject, obtained, max));
    }
    
    public double getAverageScore() {
        if (marks.isEmpty()) return 0.0;
        return marks.stream()
            .mapToDouble(Mark::getPercentage)
            .average()
            .orElse(0.0);
    }
    
    public String getGrade() {
        double avg = getAverageScore();
        if (avg >= 90) return "A+";
        if (avg >= 80) return "A";
        if (avg >= 70) return "B";
        if (avg >= 60) return "C";
        if (avg >= 50) return "D";
        return "F";
    }
    
    // Getters
    public String getRollNumber() { return rollNumber; }
    public String getName() { return name; }
    public String getClassName() { return className; }
    public List<Mark> getMarks() { return marks; }
}

// Mark Class
class Mark implements Serializable {
    private String subject;
    private double marksObtained;
    private double maxMarks;
    
    public Mark(String subject, double marksObtained, double maxMarks) {
        this.subject = subject;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
    }
    
    public double getPercentage() {
        return (marksObtained / maxMarks) * 100;
    }
    
    // Getters
    public String getSubject() { return subject; }
    public double getMarksObtained() { return marksObtained; }
    public double getMaxMarks() { return maxMarks; }
}

// Student Manager Class
class StudentManager {
    private Map<String, Student> students;
    private static final String DATA_FILE = "student_data.ser";
    
    public StudentManager() {
        students = new HashMap<>();
        loadData();
    }
    
    public boolean addStudent(Student student) {
        if (students.containsKey(student.getRollNumber())) {
            return false;
        }
        students.put(student.getRollNumber(), student);
        saveData();
        return true;
    }
    
    public void removeStudent(String rollNumber) {
        students.remove(rollNumber);
        saveData();
    }
    
    public Student getStudent(String rollNumber) {
        return students.get(rollNumber);
    }
    
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }
    
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            students = (Map<String, Student>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }
}