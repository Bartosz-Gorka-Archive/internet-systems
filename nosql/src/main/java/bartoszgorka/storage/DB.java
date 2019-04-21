package bartoszgorka.storage;

import bartoszgorka.models.Course;
import bartoszgorka.models.Grade;
import bartoszgorka.models.Student;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Set;

public interface DB {
    Set<Student> getStudents();

    Set<Course> getCourses();

    Set<Grade> getGrades(int index) throws NotFoundException;

    Grade registerNewGrade(int index, Grade rawGradeBody) throws NotFoundException, BadRequestException;

    Grade updateGrade(int index, int gradeID, Grade rawGradeBody) throws NotFoundException;

    void removeGrade(int index, int gradeID) throws NotFoundException;

    Student addNewStudent(Student rawStudent) throws BadRequestException;

    Course registerNewCourse(Course rawCourse) throws BadRequestException;

    Course updateCourse(int courseID, Course rawCourseBody) throws NotFoundException;

    void removeCourse(int courseID) throws NotFoundException;

    Student updateStudent(int index, Student rawStudentBody) throws NotFoundException;

    void removeStudent(int index) throws NotFoundException;

}
