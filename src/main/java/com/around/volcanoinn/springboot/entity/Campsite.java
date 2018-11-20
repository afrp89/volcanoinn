package com.around.volcanoinn.springboot.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class Campsite implements Serializable {

    private static final long serialVersionUID = 6802986412258921333L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Transient
    private Set<Long> availableDays;
    
    @OneToMany(mappedBy="campsite", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Booking> Bookings;

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

    public Set<Long> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(Set<Long> availableDays) {
        this.availableDays = availableDays;
    }
}
