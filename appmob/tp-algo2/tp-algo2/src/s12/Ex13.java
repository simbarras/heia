package s12;

public class Ex13 {
    private class Node {
        int value;
        Node next;
        Node(int value) {
            this.value = value;
        }
    }

    private int TORTOISE = 1;
    private int HARE = 2;


    public boolean isCyclic(Node head) {
        Node tortoiseNode = head;
        Node hareNode = head;
        do {
            for (int i = 0; i < HARE; i++) {
                hareNode = hareNode.next;
                if (hareNode == null) { return false; }
            }
            for (int i = 0; i < TORTOISE; i++) {
                tortoiseNode = tortoiseNode.next;
            }
        } while (tortoiseNode != hareNode);
        return true;
    }

    public static void main(String[] args) {

    }

}
