

# **README.md**

## 🎓 **Student Performance Analytics System**

A Java Swing-based desktop application that helps manage students, record marks, generate reports, and visualize academic analytics. This project provides an easy-to-use interface for schools, teachers, and institutions to maintain student performance data in one place.

---

## 📌 **Project Description**

The **Student Performance Analytics System** is a GUI-based software built using **Java Swing**.
It allows users to:

* Add and manage student records
* Record subject-wise marks
* Automatically calculate averages, percentages, and grades
* Generate detailed analytics and insights
* Produce individual student performance reports
* Store data persistently using serialization

This project is ideal for **academic submissions, student record management systems, and GUI-based Java learning**.

---

## ⭐ **Features**

### 🔹 **1. Student Management**

* Add new students with name, roll number, and class
* View all students in a table
* Remove students easily
* Automatically calculates average and grade

### 🔹 **2. Marks Management**

* Add marks for any subject
* Auto-calculate percentage for each subject
* Stores all subject-wise performance

### 🔹 **3. Analytics Dashboard**

* Class average score
* Top 5 performers
* Students needing attention (below 50%)
* Grade distribution overview

### 🔹 **4. Report Generation**

* Generate detailed performance reports for individual students
* Subject-wise marks, max marks, and percentages
* Overall average & grade displayed neatly

### 🔹 **5. Persistent Data Storage**

* All students and marks are saved automatically using `student_data.ser`
* Data loads automatically on application startup

---

## 🛠 **Tech Stack**

| Technology                | Purpose                    |
| ------------------------- | -------------------------- |
| **Java (JDK 8+)**         | Core programming           |
| **Java Swing**            | GUI development            |
| **Java Serialization**    | Data storage               |
| **Collections Framework** | Student & marks management |

---

## ▶️ **How to Run the Project**

### **1. Prerequisites**

* Install **Java JDK 8 or above**
* Install any IDE (optional):

  * IntelliJ IDEA
  * Eclipse
  * NetBeans
  * VS Code (with Java extensions)

### **2. Steps to Run**

#### **Option A: Using IDE**

1. Create a new Java project
2. Copy all `.java` files into the `src/` folder
3. Run the file:

   ```
   StudentAnalyticsSystem.java
   ```

#### **Option B: Using Terminal / Command Prompt**

1. Save all `.java` files in a folder
2. Open terminal in that folder
3. Compile the project:

   ```
   javac *.java
   ```
4. Run the main class:

   ```
   java StudentAnalyticsSystem
   ```

---

## 📸 **Screenshots (optional)**

> Add screenshots in your GitHub repository inside a folder named **/screenshots**
> Example layout:

```
/screenshots
    ├── dashboard.png
    ├── add_student.png
    ├── analytics.png
    └── reports.png
```

To include them in the README:

```markdown
![Dashboard](screenshots/dashboard.png)
![Add Student](screenshots/add_student.png)
```

---

## 📁 **Project Structure**

```
StudentAnalyticsSystem/
│
├── StudentAnalyticsSystem.java
├── Student.java
├── Mark.java
├── StudentManager.java
├── student_data.ser       (auto created)
├── README.md
└── /screenshots           
```

---

## ℹ️ **Additional Information**

* The application automatically saves data using serialization.
* Marks are validated to avoid incorrect or impossible entries.
* Performance analytics are generated dynamically based on student data.
* You must run the application from the same directory where `student_data.ser` is created to maintain continuity.

---

