package bartoszgorka.storage;

import bartoszgorka.models.Course;
import bartoszgorka.models.Grade;
import bartoszgorka.models.Student;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;

public interface DB {
    List<Student> getStudents();

    List<Course> getCourses();

    List<Grade> getGrades(int index) throws NotFoundException;

    Grade registerNewGrade(Student student, Course course, Grade body) throws NotFoundException, BadRequestException;

    Grade updateGrade(Student student, Grade grade, Grade body) throws NotFoundException;

    void removeGrade(Student student, Grade grade) throws NotFoundException;

    Student addNewStudent(Student student) throws BadRequestException;

    Course registerNewCourse(Course body) throws BadRequestException;

    Course updateCourse(Course course, Course body) throws NotFoundException;

    void removeCourse(Course course) throws NotFoundException;

    Student updateStudent(Student student, Student body) throws NotFoundException;

    void removeStudent(Student student) throws NotFoundException;

    Course getCourseByID(int courseID) throws NotFoundException;

    Student getStudentByID(int index) throws NotFoundException;

    Grade getGradeByID(Student student, int gradeID) throws NotFoundException;
}
