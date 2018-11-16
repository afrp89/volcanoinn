package entity;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Booking implements Serializable {

    private static final long serialVersionUID = 1081384719778639745L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column
    private String email;

    @Column
    private String fullName;

    @Column
    private Long arrivalDate;

    @Column
    private Long departureDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campsite_id")
    @JsonIgnore
    private Campsite campsite;

    public Booking() {
    }

    public Booking(String email, String fullName, Long fromDate, Long toDate) {
        this.email = email;
        this.fullName = fullName;
        this.arrivalDate = fromDate;
        this.departureDate = toDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Long arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Long getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Long departureDate) {
        this.departureDate = departureDate;
    }

    public Campsite getCampsite() {
        return campsite;
    }

    public void setCampsite(Campsite campsite) {
        this.campsite = campsite;
    }
}
