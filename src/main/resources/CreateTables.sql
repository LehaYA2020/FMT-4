DROP TABLE IF EXISTS students_courses;
DROP TABLE IF EXISTS students_groups;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS groups;

CREATE TABLE groups
(
  id SERIAL PRIMARY KEY,
  name VARCHAR(30) NOT NULL UNIQUE CHECK (name != '')
);

CREATE TABLE students
(
  id SERIAL PRIMARY KEY,
  first_name VARCHAR(30) NOT NULL CHECK (first_name != ''),
  last_name VARCHAR(30) NOT NULL CHECK (last_name != '')
);

CREATE TABLE courses
(
  id SERIAL PRIMARY KEY,
  name VARCHAR(30) NOT NULL CHECK (name != ''),
  description VARCHAR(200) NOT NULL CHECK (description != '')
);

CREATE TABLE students_courses
(
  student_id INTEGER,
  course_id INTEGER,
  UNIQUE (student_id , course_id),
  FOREIGN KEY (student_id) REFERENCES students(id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (course_id) REFERENCES courses(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE students_groups
(
  student_id INTEGER UNIQUE,
  group_id INTEGER,
  FOREIGN KEY (student_id) REFERENCES students(id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (group_id) REFERENCES groups(id) ON UPDATE CASCADE ON DELETE CASCADE
);