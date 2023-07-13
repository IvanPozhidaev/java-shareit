package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findBookingByItemOwnerAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findBookingByItemOwnerAndEndIsBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findBookingByItemOwnerAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findBookingByItemOwner(Long bookerId, Pageable pageable);

    List<Booking> findBookingByItemIdAndEndBefore(Long itemId, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemIdAndStartAfter(Long itemId, LocalDateTime now, Sort sort);

    Page<Booking> findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartAsc(Long userId,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end,
                                                                                 Pageable pageable);

    @Query("select b from bookings b " +
            "where b.item.owner = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start asc")
    Page<Booking> findBookingsByItemOwnerCurrent(Long userId, LocalDateTime now, Pageable pageable);

    @Query("select b from bookings b " +
            " where b.item.id = ?1 " +
            " and b.booker.id = ?2" +
            " and b.end < ?3")
    List<Booking> findBookingsForAddComments(Long itemId, Long userId, LocalDateTime now);

    List<Booking> findLastBookingsByItemIdAndEndIsBeforeAndStatusIs(
            Long itemId, LocalDateTime end, BookingStatus status, Sort sort);

    List<Booking> findNextBookingsByItemIdAndEndIsAfterAndStatusIs(
            Long itemId, LocalDateTime end, BookingStatus status, Sort sort);

    @Query(nativeQuery = true, value = "SELECT b.booking_id, b.start_time, b.end_time, b.item_id, b.booker_id, b.status " +
            "FROM bookings b " +
            "WHERE b.item_id IN (:itemIds)  " +
            "  AND b.end_time < :date OR (b.start_time < :date AND b.end_time > :date) " +
            "  AND b.status = :status " +
            "GROUP BY b.booking_id, b.start_time, b.end_time, b.item_id, b.booker_id, b.status " +
            "HAVING b.end_time = MIN(b.end_time) " +
            "ORDER BY b.end_time")
    List<Booking> findLastByItemIds(@Param("itemIds") List<Long> itemIds,
                                    @Param("date") LocalDateTime date,
                                    @Param("status") String status);

    @Query(nativeQuery = true, value = "SELECT b.booking_id, b.start_time, b.end_time, b.item_id, b.booker_id, b.status " +
            "FROM bookings b " +
            "WHERE b.item_id IN (:itemIds)  " +
            "  AND b.start_time > :date " +
            "  AND b.status = :status " +
            "GROUP BY b.booking_id, b.start_time, b.end_time, b.item_id, b.booker_id, b.status " +
            "HAVING b.end_time = MIN(b.end_time) " +
            "ORDER BY b.end_time")
    List<Booking> findNextByItemIds(@Param("itemIds") List<Long> itemIds,
                                    @Param("date") LocalDateTime date,
                                    @Param("status") String status);
}