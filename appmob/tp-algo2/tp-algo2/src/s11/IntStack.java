package s11;

public class IntStack { // BUGGY !!
  private int[] buf;
  private int   top;

  public IntStack() {
    this(10);
  }

  /** PRE: initialCapacity > 0 */
  public IntStack(int initialCapacity) {
    buf=new int[initialCapacity];
    top=-1;
  }

  public boolean isEmpty() {
    return top == -1;
  }

  /** PRE: !isEmpty() */
  public int top() {
    return buf[top];
  }

  /** PRE: !isEmpty() */
  public int pop() {
    int a=buf[top];
    top--;
    return a;
  }

  public void push(int x) {
    checkSize();
    top++;
    buf[top]=x;
  }

  private void checkSize() {
    if (top < buf.length-1) return;
    int[] t=new int[2*buf.length];
    for (int i=0; i++ < buf.length-1;)
      t[i]=buf[i];
    buf=t;
  }
}
