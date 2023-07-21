package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.validationmarkers.Create;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String DEFAULT_STATE_VALUE = "ALL";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Validated(Create.class) BookingPostDto dto,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(@PathVariable Long bookingId,
                                               @RequestParam Boolean approved,
                                               @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.patchBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@PathVariable Long bookingId,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookings(@RequestParam(defaultValue = DEFAULT_STATE_VALUE) String state,
                                                  @RequestHeader(USER_ID_HEADER) Long userId,
                                                  @RequestParam(defaultValue = "0")
                                                  @Min(0) int from,
                                                  @RequestParam(defaultValue = "20")
                                                  @PositiveOrZero int size) {
        return bookingClient.findAllByBooker(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAll(@RequestParam(defaultValue = DEFAULT_STATE_VALUE) String state,
                                          @RequestHeader(USER_ID_HEADER) Long userId,
                                          @RequestParam(defaultValue = "0")
                                          @Min(0) int from,
                                          @RequestParam(defaultValue = "20")
                                          @PositiveOrZero int size) {
        return bookingClient.findAllByItemOwner(state, userId, from, size);
    }
}