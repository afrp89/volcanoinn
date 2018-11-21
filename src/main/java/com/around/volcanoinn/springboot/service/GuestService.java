package com.around.volcanoinn.springboot.service;

import java.util.List;
import java.util.UUID;
import com.around.volcanoinn.springboot.entity.Guest;

public interface GuestService {
    List<Guest> getAllGuests();

    boolean existsGuest(Long guestId);

    Guest getGuest(Long guestId);

    void deleteGuest(Long guestId);

    void updateGuest(Guest guest, Long guestId);
}
