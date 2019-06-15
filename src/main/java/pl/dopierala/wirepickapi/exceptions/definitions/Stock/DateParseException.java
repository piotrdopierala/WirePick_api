package pl.dopierala.wirepickapi.exceptions.definitions.Stock;

public class DateParseException extends RuntimeException {
    private String dateTriedToParse;

    public DateParseException(String dateTriedToParseAsDate) {
        super("Error parsing '"+dateTriedToParseAsDate+"' to Date type.");
        this.dateTriedToParse=dateTriedToParseAsDate;

    }

    public String getDateTriedToParse() {
        return dateTriedToParse;
    }
}
