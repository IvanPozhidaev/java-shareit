package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingMapperTest {

    public static final long ID = 1L;
    public static final LocalDateTime DATE = LocalDateTime.now();

    private User user;
    private UserDto userDto;
    private Item item;
    private Booking booking;
    private BookingPostDto bookingPostDto;

    @BeforeEach
    public void beforeEach() {
        userDto = new UserDto(
                ID,
                "User",
                "email@mail.ru"
        );
        bookingPostDto = new BookingPostDto(ID, ID, DATE, DATE.plusDays(7));
        user = new User(ID, "name", "user@emali.com");
        item = new Item(
                ID,
                "name",
                "description",
                true,
                User.builder().id(ID + 1).build(),
                Request.builder().id(ID + 1).build());
        booking = new Booking(ID,
                DATE,
                DATE.plusDays(7),
                item,
                user,
                BookingStatus.APPROVED);
    }

    @Test
    public void toModelTest() {
        Booking result = BookingMapper.toModel(bookingPostDto, item, user);

        assertNotNull(result);
        assertEquals(bookingPostDto.getStart(), result.getStart());
        assertEquals(bookingPostDto.getEnd(), result.getEnd());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(user.getId(), result.getBooker().getId());
    }

    @Test
    public void toPostResponseDtoTest() {
        BookingPostResponseDto result = BookingMapper.toPostResponseDto(booking, item);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }

    @Test
    public void toResponseDtoTest() {
        BookingResponseDto result = BookingMapper.toResponseDto(booking, userDto, item);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(userDto.getId(), result.getBooker().getId());
        assertEquals(item.getName(), result.getName());
    }

    @Test
    public void toDetailedDtoTest() {
        BookingDetailedDto result = BookingMapper.toDetailedDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getBooker().getId(), result.getBooker().getId());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(booking.getItem().getName(), result.getName());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }

    @Test
    public void bookingInItemDtoTest() {
        BookingInItemDto result = BookingMapper.bookingInItemDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getBooker().getId(), result.getBookerId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }

    @Test
    public void toListDetailedDtoTest() {
        List<BookingDetailedDto> result = BookingMapper.toListDetailedDto(List.of(booking));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(result.get(0).getId(), booking.getId());
    }
}