package sockets.tcp;

public class MyMessage implements java.io.Serializable {
    private int counter;
    private String message;

    public MyMessage(int counter, String message) {
        this.counter = counter;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCounter() {
        return counter;
    }

}
