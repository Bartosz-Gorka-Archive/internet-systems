package bartoszgorka.models;

import bartoszgorka.rest.Courses;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@XmlRootElement
public class Course {
    private int ID;
    private String supervisor;
    private String name;
    @InjectLinks({
        @InjectLink(
            style = InjectLink.Style.ABSOLUTE,
            resource = bartoszgorka.rest.Course.class,
            bindings = {@Binding(name="ID", value="${instance.ID}")},
            rel = "self"),
        @InjectLink(
            style = InjectLink.Style.ABSOLUTE,
            resource = Courses.class,
            rel = "parent")
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
