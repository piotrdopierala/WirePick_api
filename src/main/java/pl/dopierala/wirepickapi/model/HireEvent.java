package pl.dopierala.wirepickapi.model;

import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.User;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class HireEvent implements Comparable<HireEvent> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime hireStart;
    private LocalDateTime hireEnd;
    private String notes;
    @ManyToOne
    @JoinColumn
    private DeviceItem itemHired;
    @ManyToOne
    private User user;

    public HireEvent() {
    }

    public HireEvent(DeviceItem itemHired, LocalDateTime hireStart, LocalDateTime hireEnd, User user) {
        this.hireStart = hireStart;
        this.hireEnd = hireEnd;
        this.user = user;
        this.itemHired = itemHired;
    }

    public HireEvent(DeviceItem itemHired, LocalDateTime hireStart, Duration duration, User user) {
        this.hireStart = hireStart;
        this.hireEnd = hireStart.plus(duration);
        this.user = user;
        this.itemHired = itemHired;
    }

    public boolean isInHirePeriod() {
        LocalDateTime now = LocalDateTime.now();
        return isInHirePeriod(now);
    }

    public boolean isInHirePeriod(LocalDateTime testDate) {
        return (testDate.isAfter(hireStart) && testDate.isBefore(hireEnd)) || testDate.equals(hireStart) || testDate.equals(hireEnd);
    }

    public boolean isInHirePeriod(LocalDateTime testHireStart, Duration period) {
        return isInHirePeriod(testHireStart, testHireStart.plus(period));
    }

    public boolean isInHirePeriod(LocalDateTime testHireStart, LocalDateTime testHireStop) {
        return isInHirePeriod(testHireStart) ||
                isInHirePeriod(testHireStop) ||
                testHireStart.isBefore(hireStart) && testHireStop.isAfter(hireEnd);
    }

    /**
     * Checks if HireEvent has any common period.
     *
     * @param test HireEvent to test
     * @return True if smallest part ot test is overlapping
     */
    public boolean isOverlapping(HireEvent test) {
        return isInHirePeriod(test.getHireStart(), test.getHireEnd());
    }

    /**
     * Checks if test HireEvent is totally included
     *
     * @param test HireEvent to test
     * @return true if test HireEvent is totally included
     */
    public boolean isContaining(HireEvent test) {
        return this.getHireStart().isBefore(test.getHireStart()) && this.getHireEnd().isAfter(test.getHireEnd());
    }

    public LocalDateTime getHireStart() {
        return hireStart;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setHireStart(LocalDateTime hireStart) {
        this.hireStart = hireStart;
    }

    public LocalDateTime getHireEnd() {
        return hireEnd;
    }

    public void setHireEnd(LocalDateTime hireEnd) {
        this.hireEnd = hireEnd;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Compare two hire events by dates.
     *
     * @param other Other hire event
     * @return -2 if this is before other AND periods are not overlapping <br>
     *         -1 if this is before other AND periods are overlapping <br>
     *         0  if start dates of this and other are the same <br>
     *         1  if this is after other AND periods are overlapping <br>
     *         2  if this is after other AND periods are not overlapping <br>
     */
    @Override
    public int compareTo(HireEvent other) {

//        should return -2 or 2 if ranges are NOT overlapping
//        should return -1 or 1 if ranges are overlapping(then consider start date, witch is before, witch after)
//        should return 0 only if start date are THE SAME

        if (this.getHireStart().equals(other.getHireStart())) {
            return 0;
        }

        if (this.isOverlapping(other)) {
            if (this.getHireStart().isBefore(other.getHireStart())) {
                return -1;
            }
            if (this.getHireStart().isAfter(other.getHireStart())) {
                return 1;
            }
        } else {
            if (this.getHireEnd().isBefore(other.getHireStart())) {
                return -2;
            }
            if (this.getHireStart().isAfter(other.getHireEnd())) {
                return 2;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HireEvent hireEvent = (HireEvent) o;
        return hireStart.equals(hireEvent.hireStart) &&
                hireEnd.equals(hireEvent.hireEnd) &&
                user.equals(user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hireStart, hireEnd, user);
    }
}
