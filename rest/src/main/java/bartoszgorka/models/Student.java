package bartoszgorka.models;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

// TODO - add links in Student (single), Grade (single) and Course (single)
// TODO - return course structure instead of reference - grades
@XmlRootElement
public class Student {
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links = new ArrayList<>();
    private int index;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    @XmlTransient
    private Set<Grade> grades = new HashSet<>();
    @XmlTransient
    private int lastGradeID = 0;

    @XmlTransient
    public int getNextGradeID() {
        this.lastGradeID++;
        return this.lastGradeID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @XmlTransient
    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
