package bartoszgorka.models;

import bartoszgorka.rest.GradesREST;
import bartoszgorka.rest.StudentREST;
import bartoszgorka.rest.StudentsREST;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement
public class Student {
    @InjectLinks({
            @InjectLink(
                    style = InjectLink.Style.ABSOLUTE,
                    resource = StudentREST.class,
                    bindings = {@Binding(name = "index", value = "${instance.index}")},
                    rel = "self"),
            @InjectLink(
                    style = InjectLink.Style.ABSOLUTE,
                    resource = StudentsREST.class,
                    rel = "parent"),
            @InjectLink(
                    style = InjectLink.Style.ABSOLUTE,
                    resource = GradesREST.class,
                    bindings = {@Binding(name = "index", value = "${instance.index}")},
                    rel = "grades")
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;
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

    public void clearLinks() {
        this.links = null;
    }
}
