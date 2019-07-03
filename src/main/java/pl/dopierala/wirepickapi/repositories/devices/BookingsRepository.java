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

    Iterable<BookEvent> findAllByUserAndItemBooked_Id(User user, Long ItemId);

    //TODO finish test, sql from code below
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
            "WHERE (:from <= book_end AND :to >= book_start) AND be.item_hired_id=:itemId"
            , nativeQuery = true)
    Integer numberOfOverlappingBookPeriods(@Param("itemId") Long itemId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);
}
