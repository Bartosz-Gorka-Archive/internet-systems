package bartoszgorka.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Course {
    private int ID;
    private String supervisor;
    private String name;

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
}
