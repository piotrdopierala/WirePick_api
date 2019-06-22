package pl.dopierala.wirepickapi.model.device;

import pl.dopierala.wirepickapi.model.BookEvent;

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
    @OneToMany(mappedBy = "itemBooked", cascade = {CascadeType.ALL})
    private List<BookEvent> bookings;

    public DeviceItem() {
        bookings = new ArrayList<>();
    }

    public DeviceItem(List<BookEvent> bookings) {
        this();
        this.bookings = bookings;
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

    public void setBookings(List<BookEvent> bookings) {
        this.bookings = bookings;
    }

    public List<BookEvent> getBookings() {
        return bookings;
    }

    @Override
    public DeviceItem clone(){
        DeviceItem clonedObject = new DeviceItem();
        clonedObject.setBookings(this.getBookings());
        clonedObject.setId(this.getId());
        clonedObject.setDateAddedToLibrary(this.getDateAddedToLibrary());
        clonedObject.setDeviceDefinition(this.getDeviceDefinition());
        clonedObject.setLocalization(this.getLocalization());
        return clonedObject;
    }
}
