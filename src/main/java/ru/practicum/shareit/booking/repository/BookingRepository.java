package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

        List<Booking> findByStatusInOrderByStartDesc(List<BookingState> status);

        List<Booking> findByEndBeforeOrderByStartDesc(LocalDateTime before);

        List<Booking> findByStartBeforeAndEndAfterOrderByStartDesc(LocalDateTime before, LocalDateTime after);

        List<Booking> findByStartAfterOrderByStartDesc(LocalDateTime after);

        Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime before);

        Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime before);

        List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);
}
