package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

        List<Booking> findByBooker_Id(Long userId, Sort newestFirst);

        List<Booking> findByBooker_IdAndEndIsBefore(Long userId, LocalDateTime now, Sort newestFirst);

        List<Booking> findByBooker_IdAndStartIsAfter(Long userId, LocalDateTime now, Sort newestFirst);

        @Query(" select b " +
                "from Booking b " +
                "where b.booker.id = ?1 " +
                " and ?2 between b.start and b.end")
        List<Booking> findCurrentForDate(Long userId, LocalDateTime now, Sort newestFirst);

        @Query(" select b " +
                "from Booking b " +
                "where b.booker.id = ?1 " +
                "  and b.status = 'WAITING'")
        List<Booking> findPending(Long userId, Sort newestFirst);

        @Query(" select b " +
                "from Booking b " +
                "where b.booker.id = ?1 " +
                "  and b.status = 'REJECTED'")
        List<Booking> findCanceled(Long userId, Sort newestFirst);

        List<Booking> findByItem_Owner_Id(Long userId, Sort newestFirst);

        List<Booking> findByItem_Owner_IdAndEndIsBefore(Long userId, LocalDateTime now, Sort newestFirst);

        List<Booking> findByItem_Owner_IdAndStartIsAfter(Long userId, LocalDateTime now, Sort newestFirst);

        @Query(" select b " +
                "from Booking b " +
                "where b.item.owner.id = ?1 " +
                " and ?2 between b.start and b.end")
        List<Booking> findOwnerCurrentForDate(Long userId, LocalDateTime now, Sort newestFirst);

        @Query(" select b " +
                "from Booking b " +
                "where b.item.owner.id = ?1 " +
                "  and b.status = 'WAITING'")
        List<Booking> findOwnerPending(Long userId, Sort newestFirst);

        @Query(" select b " +
                "from Booking b " +
                "where b.item.owner.id = ?1 " +
                "  and b.status = 'REJECTED'")
        List<Booking> findOwnerCanceled(Long userId, Sort newestFirst);
}
