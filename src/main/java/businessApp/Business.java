package businessApp;

import javax.persistence.*;

@Entity
public class Business {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long business_id;

    private String name;
    private String location;

    @ManyToOne
    @JoinColumn(name = "usermodel_id")
    private Usermodel usermodel;

    public Long getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(Long business_id) {
        this.business_id = business_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Usermodel getUsermodel() {
        return usermodel;
    }

    public void setUsermodel(Usermodel usermodel) {
        this.usermodel = usermodel;
    }
}
