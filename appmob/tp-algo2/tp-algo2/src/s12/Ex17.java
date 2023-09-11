package s12;

import java.util.Random;

public class Ex17 {
    public static class Loto {
        private long[] nums;
        private int pointer;
        private Random rnd;

        public Loto(long[] elts, Random r) {
            nums = elts;
            rnd = r;
            pointer = nums.length;
        }

        public long nextElement() {
            if (pointer == 0) {
                pointer = nums.length;
            }
            int num = rnd.nextInt(pointer);
            long result = nums[num];
            if (num != --pointer) swap(num, pointer);
            return result;
        }

        private void swap(int a, int b) {
            long tmp = nums[a];
            nums[a] = nums[b];
            nums[b] = tmp;
        }
    }

    public static void main(String[] args) {


    }
}
