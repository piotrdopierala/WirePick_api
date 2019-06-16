package pl.dopierala.wirepickapi.repositories.devices;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dopierala.wirepickapi.model.ReservationEvent;
import pl.dopierala.wirepickapi.model.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReservationRepository extends CrudRepository<ReservationEvent, Long> {
    Optional<ReservationEvent> findByItemReserved_Id(Long id);

    Iterable<ReservationEvent> findAllByUser(User user);


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
    @Query(value = "SELECT count(1) FROM wirepick.reservation_event he " +
            "WHERE (:from <= hire_end AND :to >= hire_start) AND he.item_hired_id=:itemId"
            , nativeQuery = true)
    Integer numberOfOverlappingReservPeriods(@Param("itemId") Long itemId,
                                             @Param("from") LocalDateTime from,
                                             @Param("to") LocalDateTime to);
}
