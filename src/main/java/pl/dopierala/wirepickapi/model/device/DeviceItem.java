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
    @OneToMany(mappedBy = "itemHired", cascade = {CascadeType.ALL})
    private List<ReservationEvent> hires;

    public DeviceItem() {
        hires = new ArrayList<>();
    }

    public DeviceItem(List<ReservationEvent> hires) {
        this();
        this.hires = hires;
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

    public void setHires(List<ReservationEvent> hires) {
        this.hires = hires;
    }

    public List<ReservationEvent> getHires() {
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
