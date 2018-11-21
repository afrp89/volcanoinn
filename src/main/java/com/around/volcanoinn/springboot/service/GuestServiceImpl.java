package com.around.volcanoinn.springboot.service;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.around.volcanoinn.springboot.entity.Guest;
import com.around.volcanoinn.springboot.repository.GuestRepository;

@Service("guestService")
public class GuestServiceImpl implements GuestService {

    private static final Logger logger = LoggerFactory.getLogger(GuestServiceImpl.class);
    @Autowired
    GuestRepository guestRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public boolean existsGuest(Long guestId) {
        return guestRepository.findById(guestId).isPresent();
    }

    @Override
    public void deleteGuest(Long guestId) {
        guestRepository.deleteById(guestId);
    }

    @Override
    public void updateGuest(Guest guest, Long guestId) {
        Guest oldGuest = guestRepository.findById(guestId).get();
        oldGuest.setName(guest.getName());
        oldGuest.setEmail(guest.getEmail());
        oldGuest.setBookings(guest.getBookings());
        guestRepository.save(oldGuest);
    }

    @Override
    public Guest getGuest(Long guestId) {
        return guestRepository.findById(guestId).get();
    }

    @Override
    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<Guest>();
        for (Guest r : guestRepository.findAll()) {
            guests.add(r);
        }
        return guests;
    }

}