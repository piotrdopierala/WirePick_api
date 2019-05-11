package pl.dopierala.wirepickapi.model.device;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DeviceDefinition implements Cloneable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;

    public DeviceDefinition() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    protected DeviceDefinition clone() throws CloneNotSupportedException {
        DeviceDefinition clone = new DeviceDefinition();
        clone.name=this.name;
        clone.id=this.id;
        clone.description=this.description;
        return clone;
    }
}