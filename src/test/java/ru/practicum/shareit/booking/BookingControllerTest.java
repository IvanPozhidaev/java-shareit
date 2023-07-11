package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    public static final Long ID = 1L;
    public static final String FROM_VALUE = "0";
    public static final String SIZE_VALUE = "20";
    public static final String FROM_PARAM = "from";
    public static final String SIZE_PARAM = "size";
    public static final String STATE_VALUE = "ALL";
    public static final String STATE_PARAM = "state";
    public static final String APPROVED_VALUE = "true";
    public static final String APPROVED_PARAM = "approved";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final LocalDateTime START_DATE = LocalDateTime.now();
    public static final LocalDateTime END_DATE = START_DATE.plusDays(7);

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Test
    public void createBookingTest() throws Exception {
        BookingPostDto inputDto = generateInputDto();
        BookingPostResponseDto responseDto = generatePostResponseDto(ID, inputDto);

        when(bookingService.createBooking(any(BookingPostDto.class), any(Long.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(inputDto))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(responseDto.getItem().getName()), String.class));

        verify(bookingService, times(1))
                .createBooking(any(BookingPostDto.class), any(Long.class));
    }

    @Test
    public void createBookingStatusNotFoundUserDoesNotExistTest() throws Exception {
        BookingPostDto inputDto = generateInputDto();

        doThrow(NoSuchElementException.class)
                .when(bookingService)
                .createBooking(any(BookingPostDto.class), any(Long.class));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(inputDto))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1))
                .createBooking(any(BookingPostDto.class), any(Long.class));
    }

    @Test
    public void patchBookingTest() throws Exception {
        BookingResponseDto responseDto = generateResponseDto(ID);

        when(bookingService.patchBooking(any(Long.class), any(Boolean.class), any(Long.class)))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/1")
                        .param(APPROVED_PARAM, APPROVED_VALUE)
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class));

        verify(bookingService, times(1))
                .patchBooking(any(Long.class), any(Boolean.class), any(Long.class));
    }

    @Test
    public void findByIdTest() throws Exception {
        BookingDetailedDto responseDto = generateDetailedDto(ID);

        when(bookingService.findById(any(Long.class), any(Long.class)))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class));

        verify(bookingService, times(1))
                .findById(any(Long.class), any(Long.class));
    }

    @Test
    public void findAllBookingsTest() throws Exception {
        when(bookingService.findAllByBooker(any(String.class), any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, ID)
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE)
                        .param(STATE_PARAM, STATE_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1))
                .findAllByBooker(any(String.class), any(Long.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void findAllTest() throws Exception {
        when(bookingService
                .findAllByItemOwner(any(String.class), any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ID)
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1))
                .findAllByItemOwner(any(String.class), any(Long.class), any(Integer.class), any(Integer.class));
    }

    private BookingPostDto generateInputDto() {
        BookingPostDto dto = new BookingPostDto();
        dto.setId(ID);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(10));
        return dto;
    }

    private BookingDetailedDto generateDetailedDto(Long id) {
        BookingDetailedDto result = new BookingDetailedDto();
        result.setId(id);
        result.setName("name");
        result.setBooker(new UserDto());
        result.setItem(new ItemDto());
        result.setStart(START_DATE);
        result.setEnd(END_DATE);
        return result;
    }

    private BookingPostResponseDto generatePostResponseDto(Long id, BookingPostDto inputDto) {
        BookingPostResponseDto result = new BookingPostResponseDto();
        Item item = new Item();
        item.setId(ID);
        item.setName("item");
        item.setDescription("item description");
        item.setAvailable(true);

        result.setId(id);
        result.setItem(item);
        result.setStart(START_DATE);
        result.setEnd(END_DATE);

        return result;
    }

    private BookingResponseDto generateResponseDto(Long id) {
        BookingResponseDto result = new BookingResponseDto();
        result.setId(id);
        result.setBooker(new UserDto());
        result.setItem(new ItemDto());
        result.setName("Item name");
        return result;
    }
}