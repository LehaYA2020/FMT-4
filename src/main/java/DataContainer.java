import DAO.Models.Course;
import DAO.Models.Group;
import DAO.Models.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataContainer {
    private Map<Student, Set<Course>> students;
    private List<Course> courses;
    private List<Group> groups;

    public Map<Student, Set<Course>> getStudents() {
        return students;
    }

    public void setStudents(Map<Student, Set<Course>> students) {
        this.students = students;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
