package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.validationmarkers.Create;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {

    public static final String DEFAULT_STATE_VALUE = "ALL";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private BookingService bookingService;

    @PostMapping
    public BookingPostResponseDto createBooking(@RequestBody @Validated(Create.class) BookingPostDto dto,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto patchBooking(@PathVariable Long bookingId,
                                           @RequestParam Boolean approved,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.patchBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDetailedDto findById(@PathVariable Long bookingId,
                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDetailedDto> findAllBookings(@RequestParam(defaultValue = DEFAULT_STATE_VALUE) String state,
                                                    @RequestHeader(USER_ID_HEADER) Long userId,
                                                    @RequestParam(defaultValue = "0")
                                                    @Min(0) int from,
                                                    @RequestParam(defaultValue = "20")
                                                    @PositiveOrZero int size) {
        return bookingService.findAllByBooker(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDetailedDto> findAll(@RequestParam(defaultValue = DEFAULT_STATE_VALUE) String state,
                                            @RequestHeader(USER_ID_HEADER) Long userId,
                                            @RequestParam(defaultValue = "0")
                                            @Min(0) int from,
                                            @RequestParam(defaultValue = "20")
                                            @PositiveOrZero int size) {
        return bookingService.findAllByItemOwner(state, userId, from, size);
    }
}