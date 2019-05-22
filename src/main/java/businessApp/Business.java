package businessApp;

import javax.persistence.*;

@Entity
public class Business {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usermodel usermodel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
