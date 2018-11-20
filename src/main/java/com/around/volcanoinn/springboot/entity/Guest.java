package com.around.volcanoinn.springboot.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Guest implements Serializable {

    private static final long serialVersionUID = -1338699261077219037L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @OneToMany(mappedBy="guest", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Booking> Bookings;

    public Guest(String name, String email) {
        this.name = name;
        this.email= email;

    }

    public Guest() {

    }

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

    public List<Booking> getBookings() {
        return Bookings;
    }

    public void setBookings(List<Booking> Bookings) {
        this.Bookings = Bookings;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
