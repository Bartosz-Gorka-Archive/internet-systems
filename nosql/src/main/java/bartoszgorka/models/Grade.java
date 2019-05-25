package bartoszgorka.models;

import bartoszgorka.rest.CourseREST;
import bartoszgorka.rest.GradeREST;
import bartoszgorka.rest.GradesREST;
import bartoszgorka.rest.StudentREST;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.*;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@XmlRootElement
@Indexes(
        @Index(fields = @Field("ID"), options = @IndexOptions(unique = true))
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Grade {
    @InjectLinks({
            @InjectLink(
                    style = InjectLink.Style.ABSOLUTE,
                    resource = GradeREST.class,
                    bindings = {
                            @Binding(name = "index", value = "${instance.studentIndex}"),
                            @Binding(name = "ID", value = "${instance.ID}")
                    },
                    rel = "self"),
            @InjectLink(
                    style = InjectLink.Style.ABSOLUTE,
                    resource = StudentREST.class,
                    bindings = {@Binding(name = "index", value = "${instance.studentIndex}")},
                    rel = "student"),
            @InjectLink(
                    style = InjectLink.Style.ABSOLUTE,
                    resource = GradesREST.class,
                    bindings = {@Binding(name = "index", value = "${instance.studentIndex}")},
                    rel = "parent"),
            @InjectLink(
                    style = InjectLink.Style.ABSOLUTE,
                    resource = CourseREST.class,
                    bindings = {@Binding(name = "ID", value = "${instance.courseID}")},
                    rel = "course")
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @Transient
    List<Link> links;
    @XmlTransient
    @Id
    ObjectId _id;
    @XmlElement(name = "course")
    @Reference
    Course course;
    @XmlTransient
    @JsonIgnore
    @Reference
    Student student;
    private int ID;
    @XmlTransient
    private int studentIndex;
    private int courseID;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private Date createdAt;
    private GradeValue grade;

    public Grade() {
    }

    public Grade(int ID, int studentIndex, int courseID, Date createdAt, GradeValue grade, Student student, Course course) {
        this.ID = ID;
        this.studentIndex = studentIndex;
        this.courseID = courseID;
        this.createdAt = createdAt;
        this.grade = grade;
        this.student = student;
        this.course = course;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @XmlTransient
    public int getStudentIndex() {
        return studentIndex;
    }

    public void setStudentIndex(int studentIndex) {
        this.studentIndex = studentIndex;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public GradeValue getGrade() {
        return grade;
    }

    public void setGrade(GradeValue grade) {
        this.grade = grade;
    }

    public enum GradeValue {
        NIEDOSTATECZNY,
        DOSTATECZNY,
        DOSTATECZNY_PLUS,
        DOBRY,
        DOBRY_PLUS,
        BARDZO_DOBRY;

        public static List<GradeValue> greaterThan(GradeValue val) {
            List<GradeValue> list = new ArrayList<>();
            switch (val) {
                case NIEDOSTATECZNY: {
                    list.add(DOSTATECZNY);
                }
                case DOSTATECZNY: {
                    list.add(DOSTATECZNY_PLUS);
                }
                case DOSTATECZNY_PLUS: {
                    list.add(DOBRY);
                }
                case DOBRY: {
                    list.add(DOBRY_PLUS);
                }
                case DOBRY_PLUS: {
                    list.add(BARDZO_DOBRY);
                }

            }
            return list;
        }

        public static List<GradeValue> lessThan(GradeValue val) {
            List<GradeValue> list = new ArrayList<>();
            switch (val) {
                case BARDZO_DOBRY: {
                    list.add(DOBRY_PLUS);
                }
                case DOBRY_PLUS: {
                    list.add(DOBRY);
                }
                case DOBRY: {
                    list.add(DOSTATECZNY_PLUS);
                }
                case DOSTATECZNY_PLUS: {
                    list.add(DOSTATECZNY);
                }
                case DOSTATECZNY: {
                    list.add(NIEDOSTATECZNY);
                }
            }
            return list;
        }
    }
}
