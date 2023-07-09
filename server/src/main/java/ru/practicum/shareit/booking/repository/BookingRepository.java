package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

        Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime before);

        Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime before);

        List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

        List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

        List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

        List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

        @Query(" select b " +
                "from Booking b " +
                "where b.booker.id = ?1 " +
                " and ?2 between b.start and b.end " +
                "order by b.start desc ")
        List<Booking> findCurrentForDate(Long userId, LocalDateTime now, Pageable pageable);

        @Query(" select b " +
                "from Booking b " +
                "where b.booker.id = ?1 " +
                "  and b.status = 'WAITING' " +
                "order by b.start desc ")
        List<Booking> findPending(Long userId, Pageable pageable);

        @Query(" select b " +
                "from Booking b " +
                "where b.booker.id = ?1 " +
                "  and b.status = 'REJECTED' " +
                "order by b.start desc ")
        List<Booking> findCanceled(Long userId, Pageable pageable);

        List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId, Pageable pageable);

        List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

        List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

        @Query(" select b " +
                "from Booking b " +
                "where b.item.owner.id = ?1 " +
                " and ?2 between b.start and b.end " +
                "order by b.start desc ")
        List<Booking> findOwnerCurrentForDate(Long userId, LocalDateTime now, Pageable pageable);

        @Query(" select b " +
                "from Booking b " +
                "where b.item.owner.id = ?1 " +
                "  and b.status = 'WAITING' " +
                "order by b.start desc ")
        List<Booking> findOwnerPending(Long userId, Pageable pageable);

        @Query(" select b " +
                "from Booking b " +
                "where b.item.owner.id = ?1 " +
                "  and b.status = 'REJECTED' " +
                "order by b.start desc ")
        List<Booking> findOwnerCanceled(Long userId, Pageable pageable);
}
