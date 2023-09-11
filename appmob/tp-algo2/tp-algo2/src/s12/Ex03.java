package s12;

import java.util.ArrayList;
import java.util.List;

//BetterStack
public class Ex03 {
    private int indexMin = 0;
    private int indexMax = 0;
    private int topIndex = -1;
    private List<Float> buffer;
    boolean flagChange = false;

    public Ex03() {
        buffer = new ArrayList<>();
    }

    public void push(float e) {
        buffer.add(e);
        topIndex++;
        if (e > buffer.get(indexMax)) indexMax = topIndex;
        if (e < buffer.get(indexMin)) indexMin = topIndex;
        return;
    }

    public float pop() {
        if (isEmpty()) return Float.MIN_VALUE;

        float res = buffer.remove(buffer.size() - 1);
        topIndex--;
        if (indexMax == topIndex + 1) {
            indexMax = 0;
            flagChange = true;
        }
        if (indexMin == topIndex + 1) {
            indexMin = 0;
            flagChange = true;
        }
        if (flagChange) {
            for (int i = 1; i < buffer.size() -1; i++) {
                if (buffer.get(i) > buffer.get(indexMax)) indexMax = i;
                if (buffer.get(i) < buffer.get(indexMin)) indexMin = i;
            }
            flagChange = false;
        }
        return res;
    }

    public float consultMin() {
        if(buffer.isEmpty()) return Float.MAX_VALUE;
        return buffer.get(indexMin);
    }

    public float consultMax() {
        if(buffer.isEmpty()) return Float.MIN_VALUE;
        return buffer.get(indexMax);
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

}
