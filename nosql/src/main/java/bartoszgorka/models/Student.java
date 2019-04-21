package bartoszgorka.models;

import bartoszgorka.rest.GradesREST;
import bartoszgorka.rest.StudentREST;
import bartoszgorka.rest.StudentsREST;
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
import java.util.Date;
import java.util.List;

@Entity
@XmlRootElement
@Indexes(
        @Index(fields = {@Field("index")}, options = @IndexOptions(unique = true))
)
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
    @Transient
    List<Link> links;
    @XmlTransient
    @Id
    ObjectId _id;
    private int index;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;

    public Student() {
    }

    public Student(int index, String firstName, String lastName, Date dateOfBirth) {
        this.index = index;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
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
}
