---

# 📚 Student Directory Management System

A Java Swing-based desktop application that manages **Students**, **Programs**, and **Colleges** using **CSV files only**.

---

## 🚀 Features

### 👨‍🎓 Student Management

* Add, Edit, Delete students
* Student ID format validation (`YYYY-NNNN`)
* Program selection filtered by selected College
* Pagination (10 records per page)
* Live search with keyword highlighting
* Duplicate ID prevention

### 🏫 College Management

* Add, Edit, Delete colleges
* Duplicate college code prevention
* Cascade delete:

  * Deletes related programs
  * Sets affected students’ program to `NULL`

### 🎓 Program Management

* Add, Edit, Delete programs
* Program selection based on College
* Duplicate program code prevention
* Cascade update:

  * Deleting program sets affected students to `NULL`

---

## 🗂 Data Storage (CSV-Based Only)

This system strictly uses CSV files stored in:

```
data/
```

### 📄 students.csv

```
ID,First Name,Last Name,Program,Year,Gender
```

### 📄 program.csv

```
Program Code,Program Name,College Code
```

### 📄 colleges.csv

```
College Name,College Code
```

---

## 🔗 Data Relationships

The system follows relational structure rules:

* `Student.program` → references `Program.code`
* `Program.college` → references `College.code`

Foreign key behavior is handled manually via Java logic (no database).

---

## 📊 Sorting System

* Custom sorting implemented (not JTable default sorter)
* Sort works across all pages
* Click column header to toggle:

  * ▲ Ascending
  * ▼ Descending
* "Actions" column is excluded from sorting

---

## 📄 Pagination

* 10 rows per page
* Page navigation:

  * `<< Prev`
  * `Next >>`
* Sorting and search apply globally before pagination

---

## 🔎 Search

* Real-time filtering
* Case-insensitive
* Keyword highlighting inside table cells

---

## 🛡 Data Integrity Rules

| Rule                          | Enforcement              |
| ----------------------------- | ------------------------ |
| Student ID format `YYYY-NNNN` | Regex validation         |
| No duplicate Student ID       | Checked before save      |
| No duplicate Program Code     | Checked before save      |
| No duplicate College Code     | Checked before save      |
| Cannot edit primary keys      | Disabled in edit mode    |
| Cascade delete                | Handled manually in code |

---

## 🧠 Architecture

* Java Swing UI
* No external libraries
* Nimbus Look and Feel

---

## ▶ How to Run

1. Open project in IDE
2. Run `Main.java`
3. CSV files will auto-create inside `/data` folder if not existing

---

## 💡 Design Decisions

* No database used
* CSV parsing handled manually
* Sorting implemented before pagination to maintain consistency
* Referential integrity manually enforced

---

## 📌 Author Notes

This project demonstrates:

* File-based relational data handling
* Manual foreign key management
* Pagination with sorting and search integration

---
