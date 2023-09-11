package s05;

import java.util.ArrayList;

public class Stack<T> {

    private int front = 0;
    private int back = 0;
    private ArrayList<T> queue;

    public Stack(ArrayList<T> queue) {
        this.queue = queue;
    }

    public T pop() {
        T value = queue.get(front);
        queue.remove(front);
        front++;
        return value;
    }

    public void push(T value) {
        queue.add(value);
    }
}
