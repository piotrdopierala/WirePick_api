package pl.dopierala.wirepickapi.repositories.devices;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dopierala.wirepickapi.model.BookEvent;
import pl.dopierala.wirepickapi.model.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BookingsRepository extends CrudRepository<BookEvent, Long> {
    Optional<BookEvent> findByItemBooked_Id(Long id);

    Iterable<BookEvent> findAllByUser(User user);

    Iterable<BookEvent> findAllByItemBooked_Id(Long ItemId);

    Iterable<BookEvent> findAllByUserAndItemBooked_Id(User user, Long ItemId);

    BookEvent findBookEventByUserAndItemBooked_IdAndBookStartLessThanEqualAndBookEndGreaterThanEqual(User user, Long ItemId, LocalDateTime start, LocalDateTime end);

    /**
     * Return number of overlapping period.
     * 0 if its available.
     *
     * @param itemId ID of item to check
     * @param from   start period to check
     * @param to     end period to check
     * @return 0 if is available, otherwise number of overlapping periods.
     */
    @Query(value = "SELECT count(1) FROM wirepick.book_event be " +
            "WHERE (:from <= book_end AND :to >= book_start) AND be.item_booked_id=:itemId"
            , nativeQuery = true)
    Integer numberOfOverlappingBookPeriods(@Param("itemId") Long itemId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);

    /**
     * Return number of overlapping periods
     * 0 if available
     *
     * @param bookId ID of booking to check
     * @param from   start period to check
     * @param to     end of period to check
     * @return 0 if is available, otherwise number of overlapping periods.
     */
    @Query(value = "SELECT count(1) FROM wirepick.book_event bke " +
            "JOIN wirepick.borrow_event bre " +
            "ON bke.id=bre.book_event_id " +
            "WHERE bke.id=:bookId AND " +
            " :from <=bre.borrow_end AND " +
            " :to >=bre.borrow_start", nativeQuery = true)
    Integer numberOfOverlappingRentPeriods(@Param("bookId") Long bookId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);
}
