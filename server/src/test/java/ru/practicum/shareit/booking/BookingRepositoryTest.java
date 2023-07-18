package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private User booker;
    private User itemOwner;
    private Booking booking;
    private LocalDateTime end;
    private LocalDateTime start;
    private BookingStatus bookingStatus;

    @BeforeEach
    public void beforeEach() {
        start = LocalDateTime.now().plusDays(2);
        end = start.plusDays(7);
        bookingStatus = BookingStatus.APPROVED;

        itemOwner = userRepository.save(new User(null, "user 1", "user1@email.com"));
        booker = userRepository.save(new User(null, "user 2", "user2@email.com"));
        item = itemRepository.save(
                new Item(null,
                        "item 1",
                        "description",
                        true,
                        User.builder().id(itemOwner.getId()).build(),
                        null));
        booking = bookingRepository
                .save(new Booking(null, start, end, item, booker, bookingStatus));
    }

    @Test
    public void findByBookerIdAndEndIsBeforeTest() {
        Page<Booking> result = bookingRepository
                .findByBookerIdAndEndIsBefore(booker.getId(), LocalDateTime.now().plusDays(10), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findByBookerIdAndStartIsAfterTest() {
        Page<Booking> result = bookingRepository
                .findByBookerIdAndStartIsAfter(booker.getId(), LocalDateTime.now().plusDays(1), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findByBookerIdAndStatusTest() {
        Page<Booking> result = bookingRepository
                .findByBookerIdAndStatus(booker.getId(), bookingStatus, Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findByBookerIdTest() {
        Page<Booking> result = bookingRepository.findByBookerId(booker.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findBookingByItemOwnerAndStatusTest() {
        Page<Booking> result = bookingRepository
                .findBookingByItemOwnerIdAndStatus(itemOwner.getId(), bookingStatus, Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findBookingByItemOwnerAndEndIsBeforeTest() {
        Page<Booking> result = bookingRepository
                .findBookingByItemOwnerIdAndEndIsBefore(
                        itemOwner.getId(), LocalDateTime.now().plusDays(30), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findBookingByItemOwnerAndStartIsAfterTest() {
        Page<Booking> result = bookingRepository
                .findBookingByItemOwnerIdAndStartIsAfter(itemOwner.getId(), LocalDateTime.now(), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findBookingByItemOwnerTest() {
        Page<Booking> result = bookingRepository
                .findBookingByItemOwnerId(itemOwner.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findBookingByItemIdAndEndBeforeTest() {
        List<Booking> result = bookingRepository
                .findBookingByItemIdAndEndBefore(item.getId(), LocalDateTime.now().plusDays(30), Sort.unsorted());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    public void findBookingByItemIdAndStartAfterTest() {
        List<Booking> result = bookingRepository
                .findBookingByItemIdAndStartAfter(item.getId(), LocalDateTime.now(), Sort.unsorted());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    public void findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartAscTest() {
        Page<Booking> result = bookingRepository
                .findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartAsc(booker.getId(),
                        start.plusDays(4),
                        end.minusDays(2),
                        Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    void findBookingsByItemOwnerCurrentTest() {
        Page<Booking> result = bookingRepository
                .findBookingsByItemOwnerIdCurrent(itemOwner.getId(), end.minusDays(5), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    void findBookingsForAddCommentsTest() {
        List<Booking> result = bookingRepository
                .findBookingsForAddComments(item.getId(), booker.getId(), end.plusDays(1));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}