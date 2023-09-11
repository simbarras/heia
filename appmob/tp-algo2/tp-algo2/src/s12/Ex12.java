package s12;

import java.util.ArrayList;
import java.util.List;

public class Ex12 {
    //======================================================================
    static class BTNode {
        int elt;
        BTNode left, right, parent;

        //-------------------------
        public BTNode(int e, BTNode l, BTNode r, BTNode p) {
            elt = e;
            left = l;
            right = r;
            parent = p;
        }

        public String toString() {
            String ls = "" + ((left == null) ? "-" : left.elt);
            String rs = "" + ((right == null) ? "-" : right.elt);
            String ps = "" + ((parent == null) ? "-" : parent.elt);
            return "" + elt + "_" + ls + "_" + rs + "_" + ps;
        }
    }

    public static BTNode commonAncestor(BTNode a, BTNode b) {
        if (a == null || b == null) return null;
        return littleThumb(a, b, null, null);
    }

    /*
    private static BTNode littleThumb(BTNode a, BTNode b) {
        if (a.parent == null && b.parent == null) return a;
        BTNode aParent = a.parent == null ? a : a.parent;
        BTNode bParent = b.parent == null ? b : b.parent;
        BTNode commonParent = littleThumb(aParent, bParent);
        if (aParent.equals(bParent)) return aParent;
        return commonParent;
    }
     */

    private static BTNode littleThumb(BTNode a, BTNode b, BTNode aChild, BTNode bChild) {
        if (a == null && b == null) return a;
        BTNode aParent = a;
        BTNode aNextChild = aChild;
        BTNode bParent = b;
        BTNode bNextChild = bChild;
        if ( a != null){
            aParent = a.parent;
            aNextChild = a;
        }
        if ( b != null){
            bParent = b.parent;
            bNextChild = b;
        }
        BTNode commonParent = littleThumb(aParent, bParent, aNextChild, bNextChild);
        if(a == null || b == null) return commonParent;
        if (a.equals(aParent) || b.equals(bParent)) return commonParent;
        if (aChild == null && bChild == null) return commonParent;
        if (aChild.equals(bChild)) return a;
        return commonParent;
    }

    public static void main(String[] args) {
        BTNode root = new BTNode(1, null, null, null);
        BTNode n2 = new BTNode(2, null, null, root);
        BTNode n3 = new BTNode(3, null, null, root);
        BTNode n4 = new BTNode(4, null, null, n2);
        BTNode n5 = new BTNode(5, null, null, n2);
        BTNode n6 = new BTNode(6, null, null, n3);
        BTNode n7 = new BTNode(7, null, null, n3);
        BTNode n8 = new BTNode(8, null, null, n4);
        BTNode n9 = new BTNode(9, null, null, n4);
        BTNode n10 = new BTNode(10, null, null, n5);
        BTNode n11 = new BTNode(11, null, null, n5);
        BTNode n12 = new BTNode(12, null, null, n6);
        BTNode n13 = new BTNode(13, null, null, n6);
        BTNode n14 = new BTNode(14, null, null, n7);
        BTNode n15 = new BTNode(15, null, null, n7);
        System.out.println(commonAncestor(n9, n5));
        System.out.println(commonAncestor(n9, n10));
        System.out.println(commonAncestor(n9, n8));
        System.out.println(commonAncestor(n9, n4));
        System.out.println(commonAncestor(n9, n15));
        System.out.println(commonAncestor(n9, n7));
        System.out.println(commonAncestor(n9, root));
        System.out.println(commonAncestor(n9, n9));
    }

}
