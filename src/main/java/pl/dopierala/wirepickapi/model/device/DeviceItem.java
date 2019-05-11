package pl.dopierala.wirepickapi.model.device;

import pl.dopierala.wirepickapi.exceptions.definitions.DeviceNotAvailableAlreadyHiredException;
import pl.dopierala.wirepickapi.model.HireEvent;
import pl.dopierala.wirepickapi.model.user.User;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class DeviceItem implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private DeviceDefinition deviceDefinition;
    private LocalDateTime dateAddedToLibrary;
    private String localization;
    @OneToMany(cascade = {CascadeType.ALL})
    private List<HireEvent> hires; //todo consider change to Map<LocalDate,HireEvent> and use TreeMap implementation. Key is hire start date, automatic sorting

    public DeviceItem() {
        hires = new ArrayList<>();
    }

    public DeviceItem(List<HireEvent> hires) {
        this();
        this.hires = hires;
    }

    public DeviceItem(DeviceDefinition deviceDefinition) {
        this();
        this.deviceDefinition = deviceDefinition;
    }

    /**
     * Rents device for given user from suplied date for given Duration
     *
     * @param start DateTime start of rent period
     * @param duration Duration of rent period
     * @param user User witch rents device
     * @return 0 - rent succeeded
     */
    public int rent(LocalDateTime start, Duration duration, User user) throws DeviceNotAvailableAlreadyHiredException {
        if (isAvailable(start, duration)) {
            hires.add(new HireEvent(start, duration,user));
            return 0;
        } else {
            throw new DeviceNotAvailableAlreadyHiredException();
        }
    }

    /**
     * Rents device for given user between suplied dates
     *
     * @param start DateTime start of rent period
     * @param end DateTime end of rent period
     * @param user User witch rents device
     * @return 0 - rent succeeded
     *
     */
    public int rent(LocalDateTime start, LocalDateTime end, User user) throws DeviceNotAvailableAlreadyHiredException {
        return rent(start, Duration.between(start, end),user);
    }

    /**
     * Checks whether deviceDefinition is available to borrow given start date and period od hire.
     *
     * @param when    start date of hire period
     * @param howLong period of hire
     * @return if deviceDefinition is available or not
     */
    public boolean isAvailable(LocalDateTime when, Duration howLong) {
        if (Objects.isNull(when) || Objects.isNull(howLong)) {
            return false;
        }
        if(hires.isEmpty() || hires.stream().noneMatch(hire->hire.isInHirePeriod(when,howLong))){
            return true;
        }
        return false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DeviceDefinition getDeviceDefinition() {
        return deviceDefinition;
    }

    public void setDeviceDefinition(DeviceDefinition deviceDefinition) {
        this.deviceDefinition = deviceDefinition;
    }

    public LocalDateTime getDateAddedToLibrary() {
        return dateAddedToLibrary;
    }

    public void setDateAddedToLibrary(LocalDateTime dateAddedToLibrary) {
        this.dateAddedToLibrary = dateAddedToLibrary;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public void setHires(List<HireEvent> hires) {
        this.hires = hires;
    }

    public List<HireEvent> getHires() {
        return hires;
    }

    @Override
    public DeviceItem clone(){
        DeviceItem clonedObject = new DeviceItem();
        clonedObject.setHires(this.getHires());
        clonedObject.setId(this.getId());
        clonedObject.setDateAddedToLibrary(this.getDateAddedToLibrary());
        clonedObject.setDeviceDefinition(this.getDeviceDefinition());
        clonedObject.setLocalization(this.getLocalization());
        return clonedObject;
    }
}
