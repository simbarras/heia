package ch.eiafr.concurentsystems.exceptions;

public class DoNotExistException extends RuntimeException {
    public DoNotExistException(String message) {
        super(message);
    }
}
