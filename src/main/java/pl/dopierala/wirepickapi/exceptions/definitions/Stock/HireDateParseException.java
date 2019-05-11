package pl.dopierala.wirepickapi.exceptions.definitions.Stock;

public class HireDateParseException extends RuntimeException {
    private String dateTriedToParse;

    public HireDateParseException(String dateTriedToParseAsDate) {
        super("Error parsing '"+dateTriedToParseAsDate+"' to Date type.");
        this.dateTriedToParse=dateTriedToParseAsDate;

    }

    public String getDateTriedToParse() {
        return dateTriedToParse;
    }
}
