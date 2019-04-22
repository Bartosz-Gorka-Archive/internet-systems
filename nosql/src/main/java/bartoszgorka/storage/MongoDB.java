package bartoszgorka.storage;

import bartoszgorka.models.Course;
import bartoszgorka.models.Grade;
import bartoszgorka.models.Sequence;
import bartoszgorka.models.Student;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Date;
import java.util.List;

public class MongoDB implements DB {
    private static final boolean DEBUG_MODE = true;
    private static MongoDB instance = new MongoDB();
    private final Datastore datastore;

    private MongoDB() {
        final Morphia morphia = new Morphia();
        morphia.mapPackage("bartoszgorka.models");

        datastore = morphia.createDatastore(new MongoClient(), "INTERNET_SYSTEMS");
        datastore.ensureIndexes();

        loadData();
    }

    public static MongoDB getInstance() {
        return instance;
    }

    private void loadData() {
        if (DEBUG_MODE) {
            datastore.delete(datastore.createQuery(Course.class));
            datastore.delete(datastore.createQuery(Student.class));
            datastore.delete(datastore.createQuery(Grade.class));
            datastore.delete(datastore.createQuery(Sequence.class));
        }

        if (datastore.getCount(Course.class) == 0 && datastore.getCount(Student.class) == 0) {
            datastore.save(new Course(1, "InternetSystems", "SupervisorName"));
            datastore.save(new Course(2, "New", "Super"));

            datastore.save(new Student(1, "Amazing", "Student", new Date()));
            datastore.save(new Student(2, "Bad", "Boy", new Date()));

            datastore.save(new Grade(1, 1, 1, new Date(), Grade.GradeValue.BARDZO_DOBRY));
            datastore.save(new Grade(2, 1, 2, new Date(), Grade.GradeValue.DOSTATECZNY));
            datastore.save(new Grade(3, 2, 1, new Date(), Grade.GradeValue.DOBRY));

            datastore.save(new Sequence(2, 2, 3));
        }
    }

    private int nextStudentIndex() {
        Sequence index = datastore.findAndModify(datastore.createQuery(Sequence.class), datastore.createUpdateOperations(Sequence.class).inc("studentIndex", 1));
        return index.getStudentIndex();
    }

    private int nextCourseIndex() {
        Sequence index = datastore.findAndModify(datastore.createQuery(Sequence.class), datastore.createUpdateOperations(Sequence.class).inc("courseID", 1));
        return index.getCourseID();
    }

    private int nextGradeIndex() {
        Sequence index = datastore.findAndModify(datastore.createQuery(Sequence.class), datastore.createUpdateOperations(Sequence.class).inc("gradeID", 1));
        return index.getGradeID();
    }

    @Override
    public List<Student> getStudents(String firstNameFilter, String lastNameFilter) {
        Query<Student> query = datastore.createQuery(Student.class);
        if (firstNameFilter != null && !firstNameFilter.isEmpty())
            query.field("firstName").containsIgnoreCase(firstNameFilter);
        if (lastNameFilter != null && !lastNameFilter.isEmpty())
            query.field("lastName").containsIgnoreCase(lastNameFilter);
        return query.asList();
    }

    @Override
    public List<Course> getCourses(String name, String supervisor) {
        Query<Course> query = datastore.createQuery(Course.class);
        if (name != null && !name.isEmpty())
            query.field("name").containsIgnoreCase(name);
        if (supervisor != null && !supervisor.isEmpty())
            query.field("supervisor").containsIgnoreCase(supervisor);
        return query.asList();
    }

    @Override
    public Course getCourseByID(int id) throws NotFoundException {
        Course course = datastore.find(Course.class).field("ID").equal(id).get();
        if (course == null) throw new NotFoundException();
        return course;
    }

    @Override
    public Student getStudentByID(int id) throws NotFoundException {
        Student student = datastore.find(Student.class).field("index").equal(id).get();
        if (student == null) throw new NotFoundException();
        return student;
    }

    @Override
    public Student addNewStudent(Student body) throws BadRequestException {
        if (body.getFirstName() != null && body.getLastName() != null && body.getDateOfBirth() != null) {
            int index = this.nextStudentIndex();
            Student student = new Student(index, body.getFirstName(), body.getLastName(), body.getDateOfBirth());
            datastore.save(student);
            return this.getStudentByID(index);
        }

        throw new BadRequestException();
    }

    @Override
    public Course registerNewCourse(Course body) throws BadRequestException {
        if (body.getName() != null && body.getSupervisor() != null) {
            int id = this.nextCourseIndex();
            Course course = new Course(id, body.getName(), body.getSupervisor());
            datastore.save(course);
            return this.getCourseByID(id);
        }

        throw new BadRequestException();
    }

    @Override
    public Course updateCourse(Course course, Course body) throws NotFoundException {
        if (body.getSupervisor() != null)
            course.setSupervisor(body.getSupervisor());
        if (body.getName() != null)
            course.setName(body.getName());

        datastore.save(course);
        return this.getCourseByID(course.getID());
    }

    @Override
    public void removeCourse(Course course) {
        datastore.delete(datastore.find(Grade.class).field("courseID").equal(course.getID()));
        datastore.delete(course);
    }

    @Override
    public Student updateStudent(Student student, Student body) throws NotFoundException {
        if (body.getDateOfBirth() != null)
            student.setDateOfBirth(body.getDateOfBirth());
        if (body.getLastName() != null)
            student.setLastName(body.getLastName());
        if (body.getFirstName() != null)
            student.setFirstName(body.getFirstName());

        datastore.save(student);
        return this.getStudentByID(student.getIndex());
    }

    @Override
    public void removeStudent(Student student) {
        datastore.delete(datastore.find(Grade.class).field("studentIndex").equal(student.getIndex()));
        datastore.delete(student);
    }

    @Override
    public List<Grade> getGrades(int index, int courseId, Grade.GradeValue value, String order) throws NotFoundException {
        Student student = this.getStudentByID(index);
        Query<Grade> query = datastore.find(Grade.class).field("studentIndex").equal(student.getIndex());
        if (courseId > 0)
            query.field("courseID").equal(courseId);
        if (value != null) {
            if (order != null && order.equals("eq")) {
                query.field("grade").equal(value);
            } else if (order != null && order.equals("gt")) {
                query.field("grade").hasAnyOf(Grade.GradeValue.greaterThan(value));
            } else if (order != null && order.equals("lt")) {
                query.field("grade").hasAnyOf(Grade.GradeValue.lessThan(value));
            }
        }
        return query.asList();
    }

    @Override
    public Grade registerNewGrade(Student student, Course course, Grade body) throws NotFoundException, BadRequestException {
        if (body.getCreatedAt() != null && body.getGrade() != null) {
            int id = this.nextGradeIndex();
            Grade grade = new Grade(id, student.getIndex(), course.getID(), body.getCreatedAt(), body.getGrade());
            datastore.save(grade);
            return this.getGradeByID(student, id);
        }

        throw new BadRequestException();
    }

    @Override
    public Grade updateGrade(Student student, Grade grade, Grade body) throws NotFoundException {
        if (body.getGrade() != null)
            grade.setGrade(body.getGrade());
        if (body.getCreatedAt() != null)
            grade.setCreatedAt(body.getCreatedAt());

        datastore.save(grade);
        return this.getGradeByID(student, grade.getID());
    }

    @Override
    public void removeGrade(Student student, Grade grade) {
        datastore.delete(grade);
    }

    @Override
    public Grade getGradeByID(Student student, int gradeID) throws NotFoundException {
        Grade grade = datastore.find(Grade.class).field("studentIndex").equal(student.getIndex()).field("ID").equal(gradeID).get();
        if (grade == null) throw new NotFoundException();
        return grade;
    }
}
