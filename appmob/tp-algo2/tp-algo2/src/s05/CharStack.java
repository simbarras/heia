package s05;

public class CharStack {
    private int topIndex;
    private int backIndex;
    private char[] buffer;
    //-------------------------------------
    private static final int DEFAULT_SIZE = 10;

    //-------------------------------------
    public CharStack() {
        this(DEFAULT_SIZE);
    }

    //-------------------------------------
    public CharStack(int estimatedSize) {
        // --- constructor of CharStack
        // --- estimatedSize must be greater than 0
        // --- if the estimatedSize is smaller or equal to 0, the default value will be assigned
        if (estimatedSize <= 0) estimatedSize = DEFAULT_SIZE;
        this.buffer = new char[estimatedSize];
        this.topIndex = -1;
        this.backIndex = 0;
    }

    //-------------------------------------
    public boolean isEmpty() {
        // --- check if the buffer is empty
        return topIndex == backIndex - 1;
    }

    //-------------------------------------
    public char top() {
        // -- gives the most recent char
        if (!isEmpty()) {
            return buffer[topIndex];
        } else {
            return Character.MIN_VALUE;
        }
    }

    //-------------------------------------
    public char pop() {
        // --- gives and removes the most recent element
        char deletedChar = buffer[backIndex++];
        backIndex %= buffer.length;
        return deletedChar;
    }

    //-------------------------------------
    public void push(char x) {
        // --- adds x as the most recent char
        topIndex = ++topIndex % buffer.length;
        if (topIndex == backIndex - 1) {
            resizeBuffer();
        }
        buffer[topIndex] = x;
    }
    //-------------------------------------

    public void resizeBuffer() {
        // --- doubles the size of the "buffer" array when it is full
        // --- create a new table twice as big as the old one
        char[] newBuffer = new char[buffer.length * 2];
        // --- copies the contents of the old buffer into the new one
        for (int i = 0; i < buffer.length; i++) {
            newBuffer[i] = buffer[(i + backIndex) % buffer.length];
        }
        backIndex = 0;
        topIndex = buffer.length - 1;
        this.buffer = newBuffer;
    }

    public static void main(String[] args) {
        // --- main method to carry out the checks of our methods
        CharStack charStack1 = new CharStack(1);
        charStack1.push('q');
        charStack1.push('w');
        charStack1.push('e');
        charStack1.push('r');
        charStack1.push('t');
        charStack1.push('z');
        System.out.println("TOP: " + charStack1.top());
        System.out.println("POP: " + charStack1.pop());
        System.out.println("TOP: " + charStack1.top());
        CharStack charStack2 = new CharStack(4);
        System.out.println("IS EMPTY? " + charStack2.isEmpty());
    }
}
