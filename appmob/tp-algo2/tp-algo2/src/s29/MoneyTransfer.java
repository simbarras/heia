
package s29;

public class MoneyTransfer {
  public final long amount;
  public final String senderAccount;
  public final String receiverAccount;
  
  static final String SEPARATOR = " ";
  
  public MoneyTransfer(long amount, String sender, String receiver) {
    this.senderAccount = sender;
    this.receiverAccount = receiver;
    this.amount = amount;
  }
  
  @Override public String toString() {
    return amount + SEPARATOR + senderAccount + SEPARATOR + receiverAccount;
  }
  
  public static MoneyTransfer fromString(String s) {
    String[] parts = s.split(SEPARATOR); 
    long amount = Long.parseLong(parts[0]);
    return new MoneyTransfer(amount, parts[1], parts[2]);
  }

}
