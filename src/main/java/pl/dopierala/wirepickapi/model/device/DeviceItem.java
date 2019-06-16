package pl.dopierala.wirepickapi.model.device;

import pl.dopierala.wirepickapi.model.ReservationEvent;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DeviceItem implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private DeviceDefinition deviceDefinition;
    private LocalDateTime dateAddedToLibrary;
    private String localization;
    @OneToMany(mappedBy = "itemReserved", cascade = {CascadeType.ALL})
    private List<ReservationEvent> reservations;

    public DeviceItem() {
        reservations = new ArrayList<>();
    }

    public DeviceItem(List<ReservationEvent> reservations) {
        this();
        this.reservations = reservations;
    }

    public DeviceItem(DeviceDefinition deviceDefinition) {
        this();
        this.deviceDefinition = deviceDefinition;
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

    public void setReservations(List<ReservationEvent> reservations) {
        this.reservations = reservations;
    }

    public List<ReservationEvent> getReservations() {
        return reservations;
    }

    @Override
    public DeviceItem clone(){
        DeviceItem clonedObject = new DeviceItem();
        clonedObject.setReservations(this.getReservations());
        clonedObject.setId(this.getId());
        clonedObject.setDateAddedToLibrary(this.getDateAddedToLibrary());
        clonedObject.setDeviceDefinition(this.getDeviceDefinition());
        clonedObject.setLocalization(this.getLocalization());
        return clonedObject;
    }
}
