package pl.dopierala.wirepickapi.model;

import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.User;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class BookEvent implements Comparable<BookEvent> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime bookStart;
    private LocalDateTime bookEnd;
    private String notes;
    @ManyToOne
    @JoinColumn
    private DeviceItem itemBooked;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "bookEvent", fetch = FetchType.EAGER)
    private List<BorrowEvent> borrows;

    public BookEvent() {
    }

    public BookEvent(DeviceItem itemBooked, LocalDateTime bookStart, LocalDateTime bookEnd, User user) {
        this.bookStart = bookStart;
        this.bookEnd = bookEnd;
        this.user = user;
        this.itemBooked = itemBooked;
    }

    public BookEvent(DeviceItem itemBooked, LocalDateTime bookStart, Duration duration, User user) {
        this.bookStart = bookStart;
        this.bookEnd = bookStart.plus(duration);
        this.user = user;
        this.itemBooked = itemBooked;
    }

    public boolean isInBookedPeriod() {
        LocalDateTime now = LocalDateTime.now();
        return isInBookedPeriod(now);
    }

    public boolean isInBookedPeriod(LocalDateTime testDate) {
        return (testDate.isAfter(bookStart) && testDate.isBefore(bookEnd)) || testDate.equals(bookStart) || testDate.equals(bookEnd);
    }

    public boolean isInBookedPeriod(LocalDateTime testHireStart, Duration period) {
        return isInBookedPeriod(testHireStart, testHireStart.plus(period));
    }

    public boolean isInBookedPeriod(LocalDateTime testHireStart, LocalDateTime testHireStop) {
        return isInBookedPeriod(testHireStart) ||
                isInBookedPeriod(testHireStop) ||
                testHireStart.isBefore(bookStart) && testHireStop.isAfter(bookEnd);
    }

    /**
     * Checks if BookEvent has any common period.
     *
     * @param test BookEvent to test
     * @return True if smallest part ot test is overlapping
     */
    public boolean isOverlapping(BookEvent test) {
        return isInBookedPeriod(test.getBookStart(), test.getBookEnd());
    }

    /**
     * Checks if test BookEvent is totally included
     *
     * @param test BookEvent to test
     * @return true if test BookEvent is totally included
     */
    public boolean isContaining(BookEvent test) {
        return this.getBookStart().isBefore(test.getBookStart()) && this.getBookEnd().isAfter(test.getBookEnd());
    }

    public LocalDateTime getBookStart() {
        return bookStart;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBookStart(LocalDateTime bookStart) {
        this.bookStart = bookStart;
    }

    public LocalDateTime getBookEnd() {
        return bookEnd;
    }

    public void setBookEnd(LocalDateTime bookEnd) {
        this.bookEnd = bookEnd;
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

    public DeviceItem getItemBooked() {
        return itemBooked;
    }

    public void setItemBooked(DeviceItem itemBooked) {
        this.itemBooked = itemBooked;
    }

    public List<BorrowEvent> getBorrows() {
        return borrows;
    }

    public void setBorrows(List<BorrowEvent> borrows) {
        this.borrows = borrows;
    }

    /**
     * Compare two book events by dates.
     *
     * @param other Other book event
     * @return -2 if this is before other AND periods are not overlapping <br>
     *         -1 if this is before other AND periods are overlapping <br>
     *         0  if start dates of this and other are the same <br>
     *         1  if this is after other AND periods are overlapping <br>
     *         2  if this is after other AND periods are not overlapping <br>
     */
    @Override
    public int compareTo(BookEvent other) {

        if (this.getBookStart().equals(other.getBookStart())) {
            return 0;
        }

        if (this.isOverlapping(other)) {
            if (this.getBookStart().isBefore(other.getBookStart())) {
                return -1;
            }
            if (this.getBookStart().isAfter(other.getBookStart())) {
                return 1;
            }
        } else {
            if (this.getBookEnd().isBefore(other.getBookStart())) {
                return -2;
            }
            if (this.getBookStart().isAfter(other.getBookEnd())) {
                return 2;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookEvent bookEvent = (BookEvent) o;
        return bookStart.equals(bookEvent.bookStart) &&
                bookEnd.equals(bookEvent.bookEnd) &&
                user.equals(user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookStart, bookEnd, user);
    }
}
