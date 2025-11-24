import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

// Main class - started this on Nov 20, 2024
// TODO: maybe add more features later?
public class StudentAnalyticsSystem extends JFrame {
    private StudentManager studentManager;
    private JTabbedPane tabbedPane;
    private JComboBox<String> marksStudentCombo;
    private JComboBox<String> reportsStudentCombo;
    
    // tried different colors - these look good with dark theme
    private static final Color DARK_BG = new Color(30, 30, 30);
    private static final Color DARKER_BG = new Color(20, 20, 20);
    private static final Color CARD_BG = new Color(45, 45, 45);
    private static final Color ACCENT_COLOR = new Color(0, 150, 200); 
    private static final Color ACCENT_HOVER = new Color(0, 170, 220);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color WARNING_COLOR = new Color(255, 152, 0);
    private static final Color ERROR_COLOR = new Color(244, 67, 54);
    
    public StudentAnalyticsSystem() {
        studentManager = new StudentManager();
        setupLookAndFeel();
        initializeUI();
        setTitle("Student Performance Analytics System");
        setSize(1200, 800);  // tested different sizes, this works best
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    // setupLookAndFeel 
    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
            // fallback to default if system L&F fails
        }
        
        // custom dark theme - spent hours getting these colors right!
        UIManager.put("Panel.background", DARK_BG);
        UIManager.put("OptionPane.background", DARK_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
    }
    // initialize ui 
    private void initializeUI() {
        getContentPane().setBackground(DARKER_BG);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(DARK_BG);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // adding tabs with emojis - looks cool!
        tabbedPane.addTab("📚 Students", createStudentPanel());
        tabbedPane.addTab("✏️ Add Marks", createMarksPanel());
        tabbedPane.addTab("📊 Analytics", createAnalyticsPanel());
        tabbedPane.addTab("📄 Reports", createReportsPanel());
        
        // This listener refreshes combos when switching tabs
        // had a bug here before where combos weren't updating
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 1 && marksStudentCombo != null) {
                refreshStudentCombo(marksStudentCombo);
            } else if (selectedIndex == 3 && reportsStudentCombo != null) {
                refreshStudentCombo(reportsStudentCombo);
            }
        });
        
        add(tabbedPane);
    }
    
    // Student tab - where users add/remove students
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(DARK_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARK_BG);
        JLabel titleLabel = new JLabel("Student Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Input card with nice border
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(CARD_BG);
        
        JTextField nameField = createStyledTextField();
        JTextField rollField = createStyledTextField();
        JTextField classField = createStyledTextField();
        
        inputPanel.add(createLabel("Student Name:"));
        inputPanel.add(nameField);
        inputPanel.add(createLabel("Roll Number:"));
        inputPanel.add(rollField);
        inputPanel.add(createLabel("Class:"));
        inputPanel.add(classField);
        
        cardPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(CARD_BG);
        
        JButton addButton = createStyledButton("➕ Add Student", SUCCESS_COLOR);
        JButton removeButton = createStyledButton("🗑️ Remove Student", ERROR_COLOR);
        JButton refreshButton = createStyledButton("🔄 Refresh", ACCENT_COLOR);
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(refreshButton);
        
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        headerPanel.add(cardPanel, BorderLayout.CENTER);
        
        // Table to display students
        String[] columns = {"Roll No", "Name", "Class", "Avg Score", "Grade"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // don't let users edit table directly
            }
        };
        JTable studentTable = createStyledTable(tableModel);
        JScrollPane scrollPane = createStyledScrollPane(studentTable);
        
        // Add button logic
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String roll = rollField.getText().trim();
            String className = classField.getText().trim();
            
            // validation - make sure all fields filled
            if (name.isEmpty() || roll.isEmpty() || className.isEmpty()) {
                showStyledMessage(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Student student = new Student(roll, name, className);
            if (studentManager.addStudent(student)) {
                refreshStudentTable(tableModel);
                // clear fields after adding
                nameField.setText("");
                rollField.setText("");
                classField.setText("");
                showStyledMessage(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showStyledMessage(this, "Student with this roll number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Remove button - with confirmation dialog
        removeButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                String rollNo = (String) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to remove this student?", 
                    "Confirm Deletion", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    studentManager.removeStudent(rollNo);
                    refreshStudentTable(tableModel);
                    showStyledMessage(this, "Student removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                showStyledMessage(this, "Please select a student to remove!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        refreshButton.addActionListener(e -> refreshStudentTable(tableModel));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        refreshStudentTable(tableModel);  // load initial data
        return panel;
    }
    
    // Marks panel - for entering subject marks
    private JPanel createMarksPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(DARK_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Add Student Marks", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(CARD_BG);
        
        marksStudentCombo = createStyledComboBox();
        JTextField subjectField = createStyledTextField();
        JTextField marksField = createStyledTextField();
        JTextField maxMarksField = createStyledTextField();
        maxMarksField.setText("100");  // default max marks
        
        inputPanel.add(createLabel("Select Student:"));
        inputPanel.add(marksStudentCombo);
        inputPanel.add(createLabel("Subject:"));
        inputPanel.add(subjectField);
        inputPanel.add(createLabel("Marks Obtained:"));
        inputPanel.add(marksField);
        inputPanel.add(createLabel("Maximum Marks:"));
        inputPanel.add(maxMarksField);
        
        cardPanel.add(inputPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(CARD_BG);
        JButton addMarksButton = createStyledButton("➕ Add Marks", SUCCESS_COLOR);
        JButton refreshButton = createStyledButton("🔄 Refresh List", ACCENT_COLOR);
        buttonPanel.add(addMarksButton);
        buttonPanel.add(refreshButton);
        
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK_BG);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(cardPanel, BorderLayout.CENTER);
        
        // Table showing all marks entries
        String[] columns = {"Roll No", "Name", "Subject", "Marks", "Max Marks", "Percentage"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable marksTable = createStyledTable(tableModel);
        JScrollPane scrollPane = createStyledScrollPane(marksTable);
        
        refreshStudentCombo(marksStudentCombo);
        
        // Add marks logic with validation
        addMarksButton.addActionListener(e -> {
            String selected = (String) marksStudentCombo.getSelectedItem();
            if (selected == null) {
                showStyledMessage(this, "No students available! Please add students first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String rollNo = selected.split(" - ")[0];
            String subject = subjectField.getText().trim();
            
            if (subject.isEmpty()) {
                showStyledMessage(this, "Subject name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double marks = Double.parseDouble(marksField.getText().trim());
                double maxMarks = Double.parseDouble(maxMarksField.getText().trim());
                
                // check if marks are valid
                if (marks < 0 || maxMarks <= 0 || marks > maxMarks) {
                    showStyledMessage(this, "Invalid marks! Ensure 0 ≤ marks ≤ max marks.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Student student = studentManager.getStudent(rollNo);
                if (student != null) {
                    student.addMarks(subject, marks, maxMarks);
                    studentManager.saveData();
                    refreshMarksTable(tableModel);
                    subjectField.setText("");
                    marksField.setText("");
                    showStyledMessage(this, "Marks added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                showStyledMessage(this, "Please enter valid numbers for marks!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        refreshButton.addActionListener(e -> {
            refreshStudentCombo(marksStudentCombo);
            refreshMarksTable(tableModel);
        });
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        refreshMarksTable(tableModel);
        return panel;
    }
    
    // Analytics dashboard - shows statistics
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(DARK_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Performance Analytics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JTextArea analyticsArea = new JTextArea();
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        analyticsArea.setBackground(CARD_BG);
        analyticsArea.setForeground(TEXT_COLOR);
        analyticsArea.setCaretColor(TEXT_COLOR);
        analyticsArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = createStyledScrollPane(analyticsArea);
        
        JButton refreshButton = createStyledButton("🔄 Refresh Analytics", ACCENT_COLOR);
        refreshButton.addActionListener(e -> {
            analyticsArea.setText(generateAnalytics());
        });
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK_BG);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(DARK_BG);
        buttonPanel.add(refreshButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        analyticsArea.setText(generateAnalytics());
        return panel;
    }
    
    // Reports panel - individual student reports
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(DARK_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Student Reports", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(DARK_BG);
        
        reportsStudentCombo = createStyledComboBox();
        refreshStudentCombo(reportsStudentCombo);
        
        JButton generateButton = createStyledButton("📄 Generate Report", ACCENT_COLOR);
        JButton refreshButton = createStyledButton("🔄 Refresh List", SUCCESS_COLOR);
        
        controlPanel.add(createLabel("Select Student:"));
        controlPanel.add(reportsStudentCombo);
        controlPanel.add(generateButton);
        controlPanel.add(refreshButton);
        
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        reportArea.setBackground(CARD_BG);
        reportArea.setForeground(TEXT_COLOR);
        reportArea.setCaretColor(TEXT_COLOR);
        reportArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = createStyledScrollPane(reportArea);
        
        generateButton.addActionListener(e -> {
            String selected = (String) reportsStudentCombo.getSelectedItem();
            if (selected != null) {
                String rollNo = selected.split(" - ")[0];
                Student student = studentManager.getStudent(rollNo);
                if (student != null) {
                    reportArea.setText(generateStudentReport(student));
                }
            } else {
                showStyledMessage(this, "No students available!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        refreshButton.addActionListener(e -> refreshStudentCombo(reportsStudentCombo));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK_BG);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Helper method to create labels with consistent styling
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(DARKER_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setBackground(DARKER_BG);
        combo.setForeground(TEXT_COLOR);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return combo;
    }
    
    // Custom button with hover effect
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_COLOR);
        table.setGridColor(new Color(60, 60, 60));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(DARKER_BG);
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // center align text in cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBackground(CARD_BG);
        centerRenderer.setForeground(TEXT_COLOR);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        return table;
    }
    
    private JScrollPane createStyledScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBackground(CARD_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        scrollPane.getViewport().setBackground(CARD_BG);
        return scrollPane;
    }
    
    private void showStyledMessage(Component parent, String message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.getContentPane().setBackground(DARK_BG);
        dialog.setVisible(true);
    }
    
    // Update table with current student data
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
    
    // Generate analytics text - calculates various stats
    private String generateAnalytics() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════════╗\n");
        sb.append("║        STUDENT PERFORMANCE ANALYTICS DASHBOARD                ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════╝\n\n");
        
        List<Student> students = studentManager.getAllStudents();
        if (students.isEmpty()) {
            sb.append("📊 No student data available.\n");
            sb.append("\nℹ️  Add students and their marks to view analytics.\n");
            return sb.toString();
        }
        
        // Calculate overall stats
        double totalAvg = students.stream()
            .mapToDouble(Student::getAverageScore)
            .average()
            .orElse(0.0);
        
        double highestAvg = students.stream()
            .mapToDouble(Student::getAverageScore)
            .max()
            .orElse(0.0);
        
        double lowestAvg = students.stream()
            .mapToDouble(Student::getAverageScore)
            .min()
            .orElse(0.0);
        
        sb.append("📈 OVERALL STATISTICS\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append(String.format("   Total Students: %d\n", students.size()));
        sb.append(String.format("   Class Average: %.2f%%\n", totalAvg));
        sb.append(String.format("   Highest Average: %.2f%%\n", highestAvg));
        sb.append(String.format("   Lowest Average: %.2f%%\n\n", lowestAvg));
        
        // Top performers list
        sb.append("🏆 TOP 5 PERFORMERS\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        students.stream()
            .sorted((s1, s2) -> Double.compare(s2.getAverageScore(), s1.getAverageScore()))
            .limit(5)
            .forEach(s -> sb.append(String.format("   %-20s (%s) - %.2f%% [%s]\n", 
                s.getName(), s.getRollNumber(), s.getAverageScore(), s.getGrade())));
        
        // Students who need help
        sb.append("\n⚠️  STUDENTS NEEDING ATTENTION (Below 50%)\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        List<Student> needingAttention = students.stream()
            .filter(s -> s.getAverageScore() < 50)
            .sorted((s1, s2) -> Double.compare(s1.getAverageScore(), s2.getAverageScore()))
            .toList();
        
        if (needingAttention.isEmpty()) {
            sb.append("   ✓ All students are performing well!\n");
        } else {
            needingAttention.forEach(s -> sb.append(String.format("   %-20s (%s) - %.2f%%\n", 
                s.getName(), s.getRollNumber(), s.getAverageScore())));
        }
        
        // Grade distribution chart
        sb.append("\n📊 GRADE DISTRIBUTION\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        Map<String, Long> gradeCount = new TreeMap<>();
        for (Student s : students) {
            String grade = s.getGrade();
            gradeCount.put(grade, gradeCount.getOrDefault(grade, 0L) + 1);
        }
        
        String[] gradeOrder = {"A+", "A", "B", "C", "D", "F"};
        for (String grade : gradeOrder) {
            long count = gradeCount.getOrDefault(grade, 0L);
            if (count > 0) {
                double percentage = (count * 100.0) / students.size();
                String bar = "█".repeat((int)(percentage / 5));  // visual bar chart
                sb.append(String.format("   %s: %2d students (%5.1f%%) %s\n", 
                    grade, count, percentage, bar));
            }
        }
        
        return sb.toString();
    }
    
    // Generate detailed report for a single student
    private String generateStudentReport(Student student) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════════╗\n");
        sb.append("║            STUDENT PERFORMANCE REPORT                          ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════╝\n\n");
        
        sb.append("👤 STUDENT INFORMATION\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append(String.format("   Name: %s\n", student.getName()));
        sb.append(String.format("   Roll Number: %s\n", student.getRollNumber()));
        sb.append(String.format("   Class: %s\n\n", student.getClassName()));
        
        if (student.getMarks().isEmpty()) {
            sb.append("📝 No marks data available for this student.\n");
            return sb.toString();
        }
        
        sb.append("📚 SUBJECT-WISE PERFORMANCE\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append(String.format("   %-20s %10s %10s %12s\n", 
            "Subject", "Obtained", "Maximum", "Percentage"));
        sb.append("   " + "─".repeat(60) + "\n");
        
        for (Mark mark : student.getMarks()) {
            sb.append(String.format("   %-20s %10.2f %10.2f %11.2f%%\n", 
                mark.getSubject(), 
                mark.getMarksObtained(), 
                mark.getMaxMarks(), 
                mark.getPercentage()));
        }
        
        sb.append("\n📊 OVERALL PERFORMANCE\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append(String.format("   Average Score: %.2f%%\n", student.getAverageScore()));
        sb.append(String.format("   Grade: %s\n", student.getGrade()));
        
        // Add personalized remark based on performance
        String performance;
        double avg = student.getAverageScore();
        if (avg >= 90) performance = "Excellent! Outstanding performance! 🌟";
        else if (avg >= 80) performance = "Very Good! Keep it up! 👏";
        else if (avg >= 70) performance = "Good! Room for improvement. 💪";
        else if (avg >= 60) performance = "Satisfactory. More effort needed. 📖";
        else if (avg >= 50) performance = "Needs improvement. Keep working! 💡";
        else performance = "Needs significant improvement. ⚠️";
        
        sb.append(String.format("   Remark: %s\n", performance));
        
        return sb.toString();
    }
    
    public static void main(String[] args) {
        // Start the app on EDT thread
        SwingUtilities.invokeLater(() -> {
            try {
                StudentAnalyticsSystem system = new StudentAnalyticsSystem();
                system.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error starting application: " + e.getMessage());
            }
        });
    }
}

// Student data class - represents one student
class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private String rollNumber;
    private String name;
    private String className;
    private List<Mark> marks;  // all marks for this student
    
    public Student(String rollNumber, String name, String className) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.className = className;
        this.marks = new ArrayList<>();
    }
    
    public void addMarks(String subject, double obtained, double max) {
        marks.add(new Mark(subject, obtained, max));
    }
    
    // Calculate average percentage across all subjects
    public double getAverageScore() {
        if (marks.isEmpty()) return 0.0;
        return marks.stream()
            .mapToDouble(Mark::getPercentage)
            .average()
            .orElse(0.0);
    }
    
    // Determine grade based on average
    public String getGrade() {
        double avg = getAverageScore();
        if (avg >= 95) return "A+";
        if (avg >= 85) return "A";
        if (avg >= 75) return "B";
        if (avg >= 65) return "C";
        if (avg >= 55) return "D";
        return "F";
    }
    
    // Getters
    public String getRollNumber() { return rollNumber; }
    public String getName() { return name; }
    public String getClassName() { return className; }
    public List<Mark> getMarks() { return marks; }
}

// Mark class - represents marks for one subject
class Mark implements Serializable {
    private static final long serialVersionUID = 1L;
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
    
    public String getSubject() { return subject; }
    public double getMarksObtained() { return marksObtained; }
    public double getMaxMarks() { return maxMarks; }
}

// Manager class - handles all student operations and data persistence
class StudentManager {
    private Map<String, Student> students;  // using map for fast lookup by roll number
    private static final String DATA_FILE = "student_data.ser";
    
    public StudentManager() {
        students = new HashMap<>();
        loadData();  // load saved data on startup
    }
    
    public boolean addStudent(Student student) {
        // check if student already exists
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
    
    // Save to file using serialization
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    
    // Load from file
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;  // no saved data yet
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            students = (Map<String, Student>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }
}
