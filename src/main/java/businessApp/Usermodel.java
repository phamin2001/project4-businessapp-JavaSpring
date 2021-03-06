package businessApp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.Set;

@Entity
public class Usermodel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usermodel_id;

    private String username;
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usermodel")
    @JsonIgnore
    private Set<Business> businesses;

    public Long getUsermodel_id() {
        return usermodel_id;
    }

    public void setUsermodel_id(Long usermodel_id) {
        this.usermodel_id = usermodel_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(Set<Business> businesses) {
        this.businesses = businesses;
    }
}
