package bartoszgorka.models;

import java.util.*;
import java.util.stream.Collectors;

public class DB {
    private static DB instance = null;
    private static Set<Student> students;
    private static Set<Course> courses;
    private static int lastStudentIndex = 0;
    private static int lastCourseID = 0;

    // TODO required in this form?
    public synchronized static DB getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new DB();
            loadData();

            return instance;
        }
    }

    private static void loadData() {
        // TODO this is only for debug
        courses = new HashSet<>();
        Course c = new Course();
        c.setID(1);
        c.setName("SINT");
        c.setSupervisor("Super");
        courses.add(c);
        lastCourseID++;

        students = new HashSet<>();
        Student student = new Student();
        student.setIndex(1);
        student.setDateOfBirth(new Date());
        student.setFirstName("AMAZING");
        student.setLastName("STUDENT");

        Grade grade = new Grade();
        grade.setID(student.getNextGradeID());
        grade.setCourseID(1);
        grade.setStudentIndex(1);
        grade.setCreatedAt(new Date());
        grade.setGrade(Grade.GradeValue.DOBRY);
        HashSet<Grade> grades = new HashSet<>();
        grades.add(grade);
        student.setGrades(grades);
        students.add(student);
        lastStudentIndex++;
    }

    public static Set<Student> getStudents() {
        DB.getInstance();
        return students;
    }

    public static Set<Course> getCourses() {
        DB.getInstance();
        return courses;
    }

    public static Student addNewStudent(Student rawStudent) {
        if (rawStudent.getFirstName() != null && rawStudent.getLastName() != null && rawStudent.getDateOfBirth() != null) {
            Student student = new Student();
            lastStudentIndex++;
            student.setIndex(lastStudentIndex);
            student.setFirstName(rawStudent.getFirstName());
            student.setLastName(rawStudent.getLastName());
            student.setDateOfBirth(rawStudent.getDateOfBirth());
            student.setGrades(new HashSet<>());
            DB.getInstance();
            students.add(student);
            return student;
        }

        return null;
    }

    public static Course registerNewCourse(Course rawCourse) {
        if (rawCourse.getName() != null && rawCourse.getSupervisor() != null) {
            Course course = new Course();
            lastCourseID++;
            course.setID(lastCourseID);
            course.setSupervisor(rawCourse.getSupervisor());
            course.setName(rawCourse.getName());
            DB.getInstance();
            courses.add(course);
            return course;
        }

        return null;
    }

    public static Course updateCourse(int courseID, Course rawCourseBody) {
        DB.getInstance();
        Optional<Course> possibleCourse = courses.stream().filter(course -> course.getID() == courseID).findFirst();
        if (possibleCourse.isPresent()) {
            Course course = possibleCourse.get();
            if (rawCourseBody.getSupervisor() != null) {
                course.setSupervisor(rawCourseBody.getSupervisor());
            }
            if (rawCourseBody.getName() != null) {
                course.setName(rawCourseBody.getName());
            }

            return course;
        }

        return null;
    }

    public static void removeCourse(int courseID) {
        DB.getInstance();
        courses.stream().filter(course -> course.getID() == courseID).findFirst().ifPresent(course -> courses.remove(course));
        for(Student s : students) {
            for (Grade grade : s.getGrades()) {
                if (grade.getCourseID() == courseID) {
                    s.getGrades().remove(grade);
                }
            }
        }
    }

    public static Student updateStudent(int index, Student rawStudentBody) {
        DB.getInstance();
        Optional<Student> possibleStudent = students.stream().filter(student -> student.getIndex() == index).findFirst();
        if (possibleStudent.isPresent()) {
            Student student = possibleStudent.get();
            if (rawStudentBody.getDateOfBirth() != null) {
                student.setDateOfBirth(rawStudentBody.getDateOfBirth());
            }
            if (rawStudentBody.getLastName() != null) {
                student.setLastName(rawStudentBody.getLastName());
            }
            if (rawStudentBody.getFirstName() != null) {
                student.setFirstName(rawStudentBody.getFirstName());
            }

            return student;
        }

        return null;
    }

    public static void removeStudent(int index) {
        DB.getInstance();
        students.stream().filter(student -> student.getIndex() == index).findFirst().ifPresent(student -> students.remove(student));
    }

    public static Set<Grade> getGrades(int index) {
        DB.getInstance();
        return students.stream().filter(student -> student.getIndex() == index).findFirst().map(Student::getGrades).orElse(null);
    }

    public static Grade registerNewGrade(int index, Grade rawGradeBody) {
        List<Integer> courseIDs = courses.stream().map(Course::getID).collect(Collectors.toList());
        if (courseIDs.contains(rawGradeBody.getCourseID()) && rawGradeBody.getCreatedAt() != null && rawGradeBody.getGrade() != null) {
            DB.getInstance();
            Student student = students.stream().filter(s -> s.getIndex() == index).findFirst().orElse(null);
            if (student != null) {
                Grade grade = new Grade();
                grade.setGrade(rawGradeBody.getGrade());
                grade.setCreatedAt(rawGradeBody.getCreatedAt());
                grade.setStudentIndex(student.getIndex());
                grade.setCourseID(rawGradeBody.getCourseID());
                grade.setID(student.getNextGradeID());

                student.getGrades().add(grade);
                return grade;
            }
        }

        return null;
    }

    public static Grade updateGrade(int index, int gradeID, Grade rawGradeBody) {
        DB.getInstance();
        Grade grade = getGrades(index).stream().filter(g -> g.getID() == gradeID).findFirst().orElse(null);
        if (grade != null) {
            List<Integer> courseIDs = courses.stream().map(Course::getID).collect(Collectors.toList());
            if (courseIDs.contains(rawGradeBody.getCourseID())) {
                grade.setCourseID(rawGradeBody.getCourseID());
            }

            if (rawGradeBody.getGrade() != null) {
                grade.setGrade(rawGradeBody.getGrade());
            }

            if (rawGradeBody.getCreatedAt() != null) {
                grade.setCreatedAt(rawGradeBody.getCreatedAt());
            }

            return grade;
        }

        return null;
    }

    public static void removeGrade(int index, int gradeID) {
        DB.getInstance();
        getGrades(index).stream().filter(g -> g.getID() == gradeID).findFirst().ifPresent(g -> getGrades(index).remove(g));
    }
}
