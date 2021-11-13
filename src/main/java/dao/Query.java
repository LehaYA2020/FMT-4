package dao;

public enum Query {
    GET_ALL_STUDENTS ("SELECT students.id, students.first_name, students.last_name, students_groups.group_id FROM students LEFT JOIN students_groups on students_groups.student_id = students.id;"),
    GET_STUDENT_BY_ID ("SELECT students.id, students.first_name, students.last_name, students_groups.group_id FROM students LEFT JOIN students_groups on students_groups.student_id = students.id WHERE students.id = ?;"),
    DELETE_STUDENT ("DELETE FROM students WHERE id = ?;"),
    DELETE_STUDENT_FROM_COURSE ("delete FROM students_courses where student_id = ? AND course_id = ?;"),
    ASSIGN_TO_COURSE ("INSERT INTO students_courses(student_id, course_id) VALUES(?, ?);"),
    GET_STUDENT_ASSIGNMENTS ("SELECT course_id FROM students_courses WHERE student_id = ?;"),
    GET_STUDENTS_BY_COURSE_NAME ("SELECT students.id, students.first_name, students.last_name, students_groups.group_id FROM students INNER JOIN students_courses ON students.id = students_courses.student_id LEFT JOIN students_groups ON students_groups.student_id = students.id INNER JOIN courses ON courses.id = students_courses.course_id WHERE courses.name = ?;"),
    INSERT_STUDENT ("INSERT INTO students(first_name, last_name) VALUES(?, ?);"),
    INSERT_STUDENT_TO_COURSE ("INSERT INTO students_groups(student_id, group_id) VALUES(?, ?);"),

    GET_ALL_GROUPS ("SELECT * FROM groups;"),
    GET_GROUPS_BY_COUNTER ("SELECT groups.id, groups.name, COUNT(*) FROM groups LEFT JOIN students_groups on students_groups.group_id = groups.id GROUP BY groups.id HAVING COUNT(*) <= ?;"),
    INSERT_GROUP ("INSERT INTO groups(name) VALUES (?);"),

    GET_ALL_COURSES ("SELECT * FROM courses;"),
    GET_COURSE_BY_ID ("SELECT courses.id, courses.name, courses.description FROM courses WHERE id = ?;"),
    INSERT_COURSE ("INSERT INTO courses(name, description) VALUES(?, ?);");

    private final String text;
    Query(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
