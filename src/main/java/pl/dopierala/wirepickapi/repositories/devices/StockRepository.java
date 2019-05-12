package pl.dopierala.wirepickapi.repositories.devices;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dopierala.wirepickapi.model.device.DeviceItem;

import java.time.LocalDateTime;

@Repository
public interface StockRepository extends CrudRepository<DeviceItem, Long> {
    Iterable<DeviceItem> findByDeviceDefinition_Id(Long deviceDefinitionId);

    /**
     * Returns collection od DeviceItems, that are available to hire
     *
     * @param deviceId Device definition ID of witch to find available items
     * @param from start period to check for availability
     * @param to end period to check for availability
     * @return Collection of DeviceItems available to rent
     */
    @Query(value = "SELECT * FROM device_item di WHERE di.device_definition_id=:deviceId AND NOT EXISTS (SELECT * FROM hire_event he WHERE di.id=he.item_hired_id AND (:from <= hire_end AND :to >= hire_start))",nativeQuery = true)
    Iterable<DeviceItem> findFreeItemsByDeviceIdAndHirePeriod(@Param("deviceId") Long deviceId,
                                                              @Param("from") LocalDateTime from,
                                                              @Param("to") LocalDateTime to);
}
