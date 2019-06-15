package pl.dopierala.wirepickapi.model;

import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.User;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class ReservationEvent implements Comparable<ReservationEvent> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime reservStart;
    private LocalDateTime reservEnd;
    private String notes;
    @ManyToOne
    @JoinColumn
    private DeviceItem itemReserved;
    @ManyToOne
    private User user;

    public ReservationEvent() {
    }

    public ReservationEvent(DeviceItem itemReserved, LocalDateTime reservStart, LocalDateTime reservEnd, User user) {
        this.reservStart = reservStart;
        this.reservEnd = reservEnd;
        this.user = user;
        this.itemReserved = itemReserved;
    }

    public ReservationEvent(DeviceItem itemReserved, LocalDateTime reservStart, Duration duration, User user) {
        this.reservStart = reservStart;
        this.reservEnd = reservStart.plus(duration);
        this.user = user;
        this.itemReserved = itemReserved;
    }

    public boolean isInReservPeriod() {
        LocalDateTime now = LocalDateTime.now();
        return isInReservPeriod(now);
    }

    public boolean isInReservPeriod(LocalDateTime testDate) {
        return (testDate.isAfter(reservStart) && testDate.isBefore(reservEnd)) || testDate.equals(reservStart) || testDate.equals(reservEnd);
    }

    public boolean isInReservPeriod(LocalDateTime testHireStart, Duration period) {
        return isInReservPeriod(testHireStart, testHireStart.plus(period));
    }

    public boolean isInReservPeriod(LocalDateTime testHireStart, LocalDateTime testHireStop) {
        return isInReservPeriod(testHireStart) ||
                isInReservPeriod(testHireStop) ||
                testHireStart.isBefore(reservStart) && testHireStop.isAfter(reservEnd);
    }

    /**
     * Checks if ReservationEvent has any common period.
     *
     * @param test ReservationEvent to test
     * @return True if smallest part ot test is overlapping
     */
    public boolean isOverlapping(ReservationEvent test) {
        return isInReservPeriod(test.getReservStart(), test.getReservEnd());
    }

    /**
     * Checks if test ReservationEvent is totally included
     *
     * @param test ReservationEvent to test
     * @return true if test ReservationEvent is totally included
     */
    public boolean isContaining(ReservationEvent test) {
        return this.getReservStart().isBefore(test.getReservStart()) && this.getReservEnd().isAfter(test.getReservEnd());
    }

    public LocalDateTime getReservStart() {
        return reservStart;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setReservStart(LocalDateTime reservStart) {
        this.reservStart = reservStart;
    }

    public LocalDateTime getReservEnd() {
        return reservEnd;
    }

    public void setReservEnd(LocalDateTime reservEnd) {
        this.reservEnd = reservEnd;
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
     * Compare two reservation events by dates.
     *
     * @param other Other reservation event
     * @return -2 if this is before other AND periods are not overlapping <br>
     *         -1 if this is before other AND periods are overlapping <br>
     *         0  if start dates of this and other are the same <br>
     *         1  if this is after other AND periods are overlapping <br>
     *         2  if this is after other AND periods are not overlapping <br>
     */
    @Override
    public int compareTo(ReservationEvent other) {

        if (this.getReservStart().equals(other.getReservStart())) {
            return 0;
        }

        if (this.isOverlapping(other)) {
            if (this.getReservStart().isBefore(other.getReservStart())) {
                return -1;
            }
            if (this.getReservStart().isAfter(other.getReservStart())) {
                return 1;
            }
        } else {
            if (this.getReservEnd().isBefore(other.getReservStart())) {
                return -2;
            }
            if (this.getReservStart().isAfter(other.getReservEnd())) {
                return 2;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationEvent reservationEvent = (ReservationEvent) o;
        return reservStart.equals(reservationEvent.reservStart) &&
                reservEnd.equals(reservationEvent.reservEnd) &&
                user.equals(user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservStart, reservEnd, user);
    }
}
