package pl.dopierala.wirepickapi.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BorrowEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime borrowStart;
    private LocalDateTime borrowEnd;
    private String notes;
    @ManyToOne
    @JoinColumn
    private BookEvent bookEvent;

    public BorrowEvent() {
    }

    public BorrowEvent(BookEvent bookEvent, LocalDateTime borrowStart, LocalDateTime borrowEnd) {
        this.bookEvent=bookEvent;
        this.borrowStart=borrowStart;
        this.borrowEnd=borrowEnd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getBorrowStart() {
        return borrowStart;
    }

    public void setBorrowStart(LocalDateTime borrowStart) {
        this.borrowStart = borrowStart;
    }

    public LocalDateTime getBorrowEnd() {
        return borrowEnd;
    }

    public void setBorrowEnd(LocalDateTime borrowEnd) {
        this.borrowEnd = borrowEnd;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BookEvent getReservation() {
        return bookEvent;
    }

    public void setReservation(BookEvent bookEvent) {
        this.bookEvent = bookEvent;
    }
}
