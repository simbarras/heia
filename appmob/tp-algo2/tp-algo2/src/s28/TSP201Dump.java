package s28;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.FileOutputStream;

import static org.objectweb.asm.Opcodes.*;

public class TSP201Dump {
    static final String DUMPED_CLASSNAME = "TSP201";

    //------------------------------------------------------------
    public static byte[] dump() throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        MethodVisitor mv;

        cw.visit(V1_8, ACC_SUPER, "tsp/" + DUMPED_CLASSNAME, null,
                "java/lang/Object", new String[]{"tsp/TSP"});

        cw.visitSource(DUMPED_CLASSNAME + ".java", null);
        //----- default constructor
        {
            mv = cw.visitMethod(0, "<init>", "()V", null, null);
            mv.visitCode();
            Label line02 = new Label();
            ;
            mv.visitLabel(line02);
            mv.visitLineNumber(2, line02);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            ;
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "Ltsp/" + DUMPED_CLASSNAME + ";", null, line02, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        //----- the saleseman() method
        {
            Label line08 = new Label();
            Label line09 = new Label();
            Label line10 = new Label();
            Label line11 = new Label();
            Label line12 = new Label();
            Label line14 = new Label();
            Label line17 = new Label();
            Label l7 = new Label();
            Label l8 = new Label();
            Label line18 = new Label();
            Label line19 = new Label();
            Label line20 = new Label();
            Label line17a = new Label();
            Label line22 = new Label();
            Label line23 = new Label();
            Label line24 = new Label();
            Label line24b = new Label();
            Label line25 = new Label();
            Label line26 = new Label();
            Label l19 = new Label();
            Label line27 = new Label();
            Label line28 = new Label();
            Label line29 = new Label();
            Label l30 = new Label();
            Label line30 = new Label();
            Label line27b = new Label();
            Label line31 = new Label();
            Label line32 = new Label();
            Label line33 = new Label();
            Label line36 = new Label();
            Label line34 = new Label();
            Label line37 = new Label();
            Label line38 = new Label();
            Label l33 = new Label();
            Label line39 = new Label();
            Label line40 = new Label();
            Label l36 = new Label();
            Label line41 = new Label();
            Label line43 = new Label();
            Label l39 = new Label();

            mv = cw.visitMethod(ACC_PUBLIC, "salesman", "([Ltsp/TSPPoint;[I)V", null, null);
            mv.visitCode();
            ;
            mv.visitLabel(line08);
            mv.visitLineNumber(8, line08);
            mv.visitVarInsn(ALOAD, var_t0);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitVarInsn(ISTORE, var_n);
            ;
            mv.visitLabel(line09);
            mv.visitLineNumber(9, line09);
            mv.visitVarInsn(ILOAD, var_n);
            mv.visitIntInsn(NEWARRAY, T_DOUBLE);
            mv.visitVarInsn(ASTORE, var_tx);
            ;
            mv.visitLabel(line10);
            mv.visitLineNumber(10, line10);
            mv.visitVarInsn(ILOAD, var_n);
            mv.visitIntInsn(NEWARRAY, T_DOUBLE);
            mv.visitVarInsn(ASTORE, var_ty);
            ;
            mv.visitLabel(line11);
            mv.visitLineNumber(11, line11);
            mv.visitVarInsn(ILOAD, var_n);
            mv.visitIntInsn(NEWARRAY, T_INT);
            mv.visitVarInsn(ASTORE, var_ti);
            ;
            mv.visitLabel(line12);
            mv.visitLineNumber(12, line12);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, var_closestPt);
            ;
            mv.visitLabel(line14);
            mv.visitLineNumber(14, line14);
            mv.visitVarInsn(ILOAD, var_n);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(ISUB);
            mv.visitVarInsn(ISTORE, var_highPt);
            ;
            mv.visitLabel(line17);
            mv.visitLineNumber(17, line17);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, var_i);
            ;
            mv.visitLabel(l7);
            mv.visitJumpInsn(GOTO, l8);
            ;
            mv.visitLabel(line18);
            mv.visitLineNumber(18, line18);
            mv.visitVarInsn(ALOAD, var_tx);
            mv.visitVarInsn(ILOAD, var_i);
            mv.visitVarInsn(ALOAD, var_t0);
            mv.visitVarInsn(ILOAD, var_i);
            mv.visitInsn(AALOAD);
            mv.visitFieldInsn(GETFIELD, "tsp/TSPPoint", "x", "D");
            mv.visitInsn(DASTORE);
            ;
            mv.visitLabel(line19);
            mv.visitLineNumber(19, line19);
            mv.visitVarInsn(ALOAD, var_ty);
            mv.visitVarInsn(ILOAD, var_i);
            mv.visitVarInsn(ALOAD, var_t0);
            mv.visitVarInsn(ILOAD, var_i);
            mv.visitInsn(AALOAD);
            mv.visitFieldInsn(GETFIELD, "tsp/TSPPoint", "y", "D");
            mv.visitInsn(DASTORE);
            ;
            mv.visitLabel(line20);
            mv.visitLineNumber(20, line20);
            mv.visitVarInsn(ALOAD, var_ti);
            mv.visitVarInsn(ILOAD, var_i);
            mv.visitVarInsn(ILOAD, var_i);
            mv.visitInsn(IASTORE);
            ;
            mv.visitLabel(line17a);
            mv.visitLineNumber(17, line17a);
            mv.visitIincInsn(var_i, 1);
            ;
            mv.visitLabel(l8);
            mv.visitVarInsn(ILOAD, var_i);
            mv.visitVarInsn(ILOAD, var_n);
            mv.visitJumpInsn(IF_ICMPLT, line18);
            ;
            mv.visitLabel(line22);
            mv.visitLineNumber(22, line22);
            mv.visitVarInsn(ILOAD, var_n);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(ISUB);
            mv.visitVarInsn(ISTORE, var_thisPt);
            ;
            mv.visitLabel(line23);
            mv.visitLineNumber(23, line23);
            mv.visitVarInsn(ALOAD, var_path);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ILOAD, var_n);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(ISUB);
            mv.visitInsn(IASTORE);
            ;
            mv.visitLabel(line24);
            mv.visitLineNumber(24, line24);
            mv.visitJumpInsn(GOTO, line24b);
            ;
            mv.visitLabel(line25);
            mv.visitLineNumber(25, line25);
            mv.visitLdcInsn(Double.valueOf(Double.MAX_VALUE));
            mv.visitVarInsn(DSTORE, var_shortestDist);
            ;
            mv.visitLabel(line26);
            mv.visitLineNumber(26, line26);
            mv.visitVarInsn(ALOAD, var_tx);
            mv.visitVarInsn(ILOAD, var_thisPt);
            mv.visitInsn(DALOAD);
            mv.visitVarInsn(DSTORE, var_thisX);
            ;
            mv.visitLabel(l19);
            mv.visitVarInsn(ALOAD, var_ty);
            mv.visitVarInsn(ILOAD, var_thisPt);
            mv.visitInsn(DALOAD);
            mv.visitVarInsn(DSTORE, var_thisY);
            ;
            mv.visitLabel(line27);
            mv.visitLineNumber(27, line27);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, var_j);
            ;
            mv.visitLabel(line28);
            mv.visitLineNumber(28, line28);
            mv.visitVarInsn(DLOAD, var_thisX);
            mv.visitVarInsn(ALOAD, var_tx);
            mv.visitVarInsn(ILOAD, var_j);
            mv.visitInsn(DALOAD);
            mv.visitInsn(DSUB);
            /*mv.visitVarInsn(DLOAD, var_thisX);
            mv.visitVarInsn(ALOAD, var_tx);
            mv.visitVarInsn(ILOAD, var_j);
            mv.visitInsn(DALOAD);
            mv.visitInsn(DSUB);*/
            mv.visitInsn(DUP2);
            mv.visitInsn(DMUL);
            mv.visitVarInsn(DSTORE, var_d);
            ;
            mv.visitLabel(line29);
            mv.visitLineNumber(29, line29);
            mv.visitVarInsn(DLOAD, var_d);
            mv.visitVarInsn(DLOAD, var_shortestDist);
            mv.visitInsn(DCMPL);
            mv.visitJumpInsn(IFLE, line30);
            mv.visitJumpInsn(GOTO, line27b);
            ;
            mv.visitLabel(line30);
            mv.visitLineNumber(30, line30);
            mv.visitVarInsn(DLOAD, var_d);
            mv.visitVarInsn(DLOAD, var_thisY);
            mv.visitVarInsn(ALOAD, var_ty);
            mv.visitVarInsn(ILOAD, var_j);
            mv.visitInsn(DALOAD);
            mv.visitInsn(DSUB);
            /*mv.visitVarInsn(DLOAD, var_thisY);
            mv.visitVarInsn(ALOAD, var_ty);
            mv.visitVarInsn(ILOAD, var_j);
            mv.visitInsn(DALOAD);
            mv.visitInsn(DSUB);*/
            mv.visitInsn(DUP2);
            mv.visitInsn(DMUL);
            mv.visitInsn(DADD);
            mv.visitVarInsn(DSTORE, var_d);
            ;
            mv.visitLabel(line31);
            mv.visitLineNumber(31, line31);
            mv.visitVarInsn(DLOAD, var_d);
            mv.visitVarInsn(DLOAD, var_shortestDist);
            mv.visitInsn(DCMPL);
            mv.visitJumpInsn(IFLE, line32);
            mv.visitJumpInsn(GOTO, line27b);
            ;
            mv.visitLabel(line32);
            mv.visitLineNumber(32, line32);
            mv.visitVarInsn(ILOAD, var_j);
            mv.visitVarInsn(ILOAD, var_highPt);
            mv.visitJumpInsn(IF_ICMPNE, line33);
            mv.visitJumpInsn(GOTO, line36);
            ;
            mv.visitLabel(line33);
            mv.visitLineNumber(33, line33);
            mv.visitVarInsn(DLOAD, var_d);
            mv.visitVarInsn(DSTORE, var_shortestDist);
            ;
            mv.visitLabel(line34);
            mv.visitLineNumber(34, line34);
            mv.visitVarInsn(ILOAD, var_j);
            mv.visitVarInsn(ISTORE, var_closestPt);
            ;
            mv.visitLabel(line27b);
            mv.visitLineNumber(27, line27b);
            mv.visitIincInsn(var_j, 1);
            ;
            mv.visitLabel(l30);
            mv.visitJumpInsn(GOTO, line28);
            ;
            mv.visitLabel(line36);
            mv.visitLineNumber(36, line36);
            mv.visitVarInsn(ALOAD, var_path);
            mv.visitVarInsn(ILOAD, var_n);
            mv.visitVarInsn(ILOAD, var_highPt);
            mv.visitInsn(ISUB);
            mv.visitVarInsn(ALOAD, var_ti);
            mv.visitVarInsn(ILOAD, var_closestPt);
            mv.visitInsn(IALOAD);
            mv.visitInsn(IASTORE);
            ;
            mv.visitLabel(line37);
            mv.visitLineNumber(37, line37);
            mv.visitIincInsn(var_highPt, -1);
            ;
            mv.visitLabel(line38);
            mv.visitLineNumber(38, line38);
            mv.visitVarInsn(ALOAD, var_tx);
            mv.visitVarInsn(ILOAD, var_highPt);
            mv.visitInsn(DALOAD);
            mv.visitVarInsn(DSTORE, var_aux);
            ;
            mv.visitLabel(l33);
            mv.visitVarInsn(ALOAD, var_tx);
            mv.visitVarInsn(ILOAD, var_highPt);
            mv.visitVarInsn(ALOAD, var_tx);
            mv.visitVarInsn(ILOAD, var_closestPt);
            mv.visitInsn(DALOAD);
            mv.visitInsn(DASTORE);
            mv.visitVarInsn(ALOAD, var_tx);
            mv.visitVarInsn(ILOAD, var_closestPt);
            mv.visitVarInsn(DLOAD, var_aux);
            mv.visitInsn(DASTORE);
            ;
            mv.visitLabel(line39);
            mv.visitLineNumber(39, line39);
            mv.visitVarInsn(ALOAD, var_ty);
            mv.visitVarInsn(ILOAD, var_highPt);
            mv.visitInsn(DALOAD);
            mv.visitVarInsn(DSTORE, var_aux);
            mv.visitVarInsn(ALOAD, var_ty);
            mv.visitVarInsn(ILOAD, var_highPt);
            mv.visitVarInsn(ALOAD, var_ty);
            mv.visitVarInsn(ILOAD, var_closestPt);
            mv.visitInsn(DALOAD);
            mv.visitInsn(DASTORE);
            mv.visitVarInsn(ALOAD, var_ty);
            mv.visitVarInsn(ILOAD, var_closestPt);
            mv.visitVarInsn(DLOAD, var_aux);
            mv.visitInsn(DASTORE);
            ;
            mv.visitLabel(line40);
            mv.visitLineNumber(40, line40);
            mv.visitVarInsn(ALOAD, var_ti);
            mv.visitVarInsn(ILOAD, var_highPt);
            mv.visitInsn(IALOAD);
            mv.visitVarInsn(ISTORE, var_auxi);
            ;
            mv.visitLabel(l36);
            mv.visitVarInsn(ALOAD, var_ti);
            mv.visitVarInsn(ILOAD, var_highPt);
            mv.visitVarInsn(ALOAD, var_ti);
            mv.visitVarInsn(ILOAD, var_closestPt);
            mv.visitInsn(IALOAD);
            mv.visitInsn(IASTORE);
            mv.visitVarInsn(ALOAD, var_ti);
            mv.visitVarInsn(ILOAD, var_closestPt);
            mv.visitVarInsn(ILOAD, var_auxi);
            mv.visitInsn(IASTORE);
            ;
            mv.visitLabel(line41);
            mv.visitLineNumber(41, line41);
            mv.visitVarInsn(ILOAD, var_highPt);
            mv.visitVarInsn(ISTORE, var_thisPt);
            ;
            mv.visitLabel(line24b);
            mv.visitLineNumber(24, line24b);
            mv.visitVarInsn(ILOAD, var_highPt);
            mv.visitJumpInsn(IFGT, line25);
            ;
            mv.visitLabel(line43);
            mv.visitLineNumber(43, line43);
            mv.visitInsn(RETURN);
            ;
            mv.visitLabel(l39);
            mv.visitLocalVariable("this", "Ltsp/" + DUMPED_CLASSNAME + ";", null, line08, l39, 0);
            mv.visitLocalVariable("t0", "[Ltsp/TSPPoint;", null, line08, l39, 1);
            mv.visitLocalVariable("path", "[I", null, line08, l39, 2);
            mv.visitLocalVariable("j", "I", null, line28, line24b, 3);
            mv.visitLocalVariable("d", "D", null, line29, l30, 4);
            mv.visitLocalVariable("d", "D", null, line36, line24b, 4);
            mv.visitLocalVariable("thisX", "D", null, l19, line24b, 6);
            mv.visitLocalVariable("thisY", "D", null, line27, line24b, 8);
            mv.visitLocalVariable("i", "I", null, l7, l39, 10);
            mv.visitLocalVariable("n", "I", null, line09, l39, 11);
            mv.visitLocalVariable("tx", "[D", null, line10, l39, 12);
            mv.visitLocalVariable("ty", "[D", null, line11, l39, 13);
            mv.visitLocalVariable("ti", "[I", null, line12, l39, 14);
            mv.visitLocalVariable("thisPt", "I", null, line23, l39, 15);
            mv.visitLocalVariable("closestPt", "I", null, line14, l39, 16);
            mv.visitLocalVariable("shortestDist", "D", null, line26, line24b, 17);
            mv.visitLocalVariable("highPt", "I", null, line17, l39, 19);
            mv.visitLocalVariable("aux", "D", null, l33, line24b, 20);
            mv.visitLocalVariable("auxi", "I", null, l36, line24b, 22);
            mv.visitMaxs(8, 23);
            mv.visitEnd();
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    //------------------------------------------------------------------
    // mv.visitVarInsn(ALOAD, var_ti);     is more readable than :
    // mv.visitVarInsn(ALOAD, 14);         , no ?
    static int var_this = 0;
    static int var_t0 = 1;
    static int var_path = 2;
    static int var_j = 3;
    static int var_d = 4;
    static int var_thisX = 6;
    static int var_thisY = 8;
    static int var_i = 10;
    static int var_n = 11;
    static int var_tx = 12;
    static int var_ty = 13;
    static int var_ti = 14;
    static int var_thisPt = 15;
    static int var_closestPt = 16;
    static int var_shortestDist = 17;
    static int var_highPt = 19;
    static int var_aux = 20;
    static int var_auxi = 22;

    //------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        FileOutputStream fos = new FileOutputStream("out/production/tp-algo2/tsp/" + DUMPED_CLASSNAME + ".class");
        // This is a verification that can detect some incoherences in the bytecode
        // but you'll need to include other libraries:
        //       asm-analysis-*.jar, asm-tree-*.jar, asm-util-*.jar
        //    org.objectweb.asm.util.CheckClassAdapter.verify(
        //        new ClassReader(dump()), false, new PrintWriter(System.err));
        fos.write(dump());
        fos.close();
        System.err.println("done - written: " + DUMPED_CLASSNAME + ".class");
    }
}