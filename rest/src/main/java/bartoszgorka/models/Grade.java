package bartoszgorka.models;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

@XmlRootElement
public class Grade {
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links;
    private int ID;
    @XmlTransient
    private int studentIndex;
    private int courseID;
    private Date createdAt;
    private GradeValue grade;

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

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public enum GradeValue {
        NIEDOSTATECZNY,
        DOSTATECZNY,
        DOSTATECZNY_PLUS,
        DOBRY,
        DOBRY_PLUS,
        BARDZO_DOBRY
    }
}
