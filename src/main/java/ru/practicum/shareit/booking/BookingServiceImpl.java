package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.InvalidBookingException;
import ru.practicum.shareit.exception.UnavailableBookingException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static ru.practicum.shareit.booking.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    public static final String ILLEGAL_STATE_MESSAGE = "  state: ";
    public static final String INVALID_BOOKING = "нельзя забронировать свою же вещь";
    public static final String STATE_ALREADY_SET_MESSAGE = "статус уже выставлен state: ";
    public static final String BOOKING_INVALID_MESSAGE = "недопустимые значения времени бронирования: ";
    public static final String UNAVAILABLE_BOOKING_MESSAGE = "в данный момент невозможно забронировать item: ";
    public static final String DENIED_PATCH_ACCESS_MESSAGE = "пользователь не является владельцем вещи userId: ";
    public static final String DENIED_ACCESS_MESSAGE = "пользователь не является владельцем вещи или брони userId: ";

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingPostResponseDto createBooking(BookingPostDto dto, Long userId) {
        if (!isStartBeforeEnd(dto)) {
            throw new IllegalArgumentException(BOOKING_INVALID_MESSAGE +
                    "start: " + dto.getStart() + " end: " + dto.getEnd() + " now: ");
        }

        User user = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow();

        if (Objects.equals(userId, item.getOwner().getId())) {
            throw new InvalidBookingException(INVALID_BOOKING);
        }

        if (!item.getAvailable()) {
            throw new UnavailableBookingException(UNAVAILABLE_BOOKING_MESSAGE + item.getId());
        }

        Booking booking = BookingMapper.toModel(dto, item, user);
        booking = bookingRepository.save(booking);
        return BookingMapper.toPostResponseDto(booking, item);
    }

    @Override
    @Transactional
    public BookingResponseDto patchBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow();

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NoSuchElementException(DENIED_PATCH_ACCESS_MESSAGE + userId + " itemId: " + item.getId());
        }
        BookingStatus status = convertToStatus(approved);

        if (booking.getStatus().equals(status)) {
            throw new IllegalArgumentException(STATE_ALREADY_SET_MESSAGE + status);
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        return BookingMapper.toResponseDto(booking, UserMapper.toDto(booking.getBooker()), item);
    }

    @Override
    public BookingDetailedDto findById(Long bookingId, Long userId) {
        checkIfUserExists(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Long itemOwner = booking.getItem().getOwner().getId();
        Long bookingOwner = booking.getBooker().getId();
        boolean itemOrBookingOwner = userId.equals(bookingOwner) || userId.equals(itemOwner);

        if (!itemOrBookingOwner) {
            throw new NoSuchElementException(DENIED_ACCESS_MESSAGE + userId);
        }
        return BookingMapper.toDetailedDto(booking);
    }

    @Override
    public List<BookingDetailedDto> findAllByBooker(String state, Long userId, int from, int size) {
        State status = State.parseState(state);
        checkIfUserExists(userId);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());

        switch (status) {
            case REJECTED:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findByBookerIdAndStatus(userId, REJECTED, pageable)
                        .toList());
            case WAITING:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findByBookerIdAndStatus(userId, WAITING, pageable)
                        .toList());
            case CURRENT:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartAsc(userId, start, end, pageable)
                        .toList());
            case FUTURE:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findByBookerIdAndStartIsAfter(userId, start, pageable)
                        .toList());
            case PAST:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findByBookerIdAndEndIsBefore(userId, start, pageable)
                        .toList());
            case ALL:
                return BookingMapper.toListDetailedDto(bookingRepository.findByBookerId(userId, pageable)
                        .toList());
            default:
                throw new IllegalArgumentException(ILLEGAL_STATE_MESSAGE);
        }
    }

    @Override
    public List<BookingDetailedDto> findAllByItemOwner(String stateValue, Long userId, int from, int size) {
        State state = State.parseState(stateValue);
        checkIfUserExists(userId);
        LocalDateTime now = LocalDateTime.now();

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());

        switch (state) {
            case REJECTED:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findBookingByItemOwnerIdAndStatus(userId, REJECTED, pageable)
                        .toList());
            case WAITING:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findBookingByItemOwnerIdAndStatus(userId, WAITING, pageable)
                        .toList());
            case CURRENT:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findBookingsByItemOwnerIdCurrent(userId, now, pageable)
                        .toList());
            case FUTURE:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findBookingByItemOwnerIdAndStartIsAfter(userId, now, pageable)
                        .toList());
            case PAST:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findBookingByItemOwnerIdAndEndIsBefore(userId, now, pageable)
                        .toList());
            case ALL:
                return BookingMapper.toListDetailedDto(bookingRepository
                        .findBookingByItemOwnerId(userId, pageable)
                        .toList());
            default:
                throw new IllegalArgumentException(ILLEGAL_STATE_MESSAGE);
        }
    }

    private void checkIfUserExists(Long userId) {
        userRepository.findById(userId).orElseThrow();
    }

    private boolean isStartBeforeEnd(BookingPostDto dto) {
        return dto.getStart().isBefore(dto.getEnd());
    }

    private BookingStatus convertToStatus(Boolean approved) {
        return approved ? BookingStatus.APPROVED : REJECTED;
    }
}