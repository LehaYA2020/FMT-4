package dao;

public class QueriesConstants {
    private FileReader fileReader = FileReader.getInstance();

    public final String GET_ALL_STUDENTS = fileReader.getQuery("getAllStudents.sql");
    public final String GET_STUDENT_BY_ID = fileReader.getQuery("getStudentById.sql");
    public final String DELETE_STUDENT = fileReader.getQuery("deleteStudentById.sql");
    public final String DELETE_STUDENT_FROM_COURSE = fileReader.getQuery("deleteStudentFromCourse.sql");
    public final String ASSIGN_TO_COURSE = fileReader.getQuery("assignToCourse.sql");
    public final String GET_STUDENT_ASSIGNMENTS = fileReader.getQuery("getStudentAssignments.sql");
    public final String GET_STUDENTS_BY_COURSE_NAME = fileReader.getQuery("getStudentsByCourseName.sql");
    public final String INSERT_STUDENT = fileReader.getQuery("insertStudent.sql");

    public final String GET_ALL_COURSES = fileReader.getQuery("getAllCourses.sql");
    public final String GET_COURSE_BY_ID = fileReader.getQuery("getCourseById.sql");
    public final String INSERT_COURSE = fileReader.getQuery("insertCourses.sql");

    public final String GET_ALL_GROUPS = fileReader.getQuery("getAllGroups.sql");
    public final String GET_GROUPS_BY_COUNTER = fileReader.getQuery("getGroupsByCounter.sql");
    public final String INSERT_GROUP = fileReader.getQuery("insertGroup.sql");
}
