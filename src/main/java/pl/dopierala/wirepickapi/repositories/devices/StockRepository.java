package pl.dopierala.wirepickapi.repositories.devices;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dopierala.wirepickapi.model.device.DeviceItem;

import java.time.LocalDateTime;

@Repository
public interface StockRepository extends CrudRepository<DeviceItem, Long> {
    Iterable<DeviceItem> findByDeviceDefinition_Id(Long id);


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
    @Query(value = "SELECT count(1) FROM wirepick.hire_event he " +
            "WHERE (:from <= hire_end AND :to >= hire_start) AND he.item_hired_id=:itemId"
            , nativeQuery = true)
    Integer numberOfOverlappingHirePeriods(@Param("itemId") Long itemId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);
}
