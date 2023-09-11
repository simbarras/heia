
package s29;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Here, money is not in "dollars", but in "bolivars".

// The BolivarBank is very generous/silly: every new account mentioned in a 
// transfer is automatically created with a certain amount of money (a 
// welcome present). But in our bank it is forbidden to have a negative balance.

public class BolivarBank {
    public final long INITIAL_WELCOME_GIFT = 100;

    /**
     * Account -> remainingBolivars
     */
    private final Map<String, Long> balance = new HashMap<>();

    /**
     * Account -> number of transactions already done by the owner
     */
    private final Map<String, Long> nTransactions = new HashMap<>();

    private void createIfUnknownAccount(String account) {
        if (balance.containsKey(account)) return;
        balance.put(account, INITIAL_WELCOME_GIFT);
        nTransactions.put(account, 0L);
    }

    public long moneyOf(String account) {
        createIfUnknownAccount(account);
        return balance.get(account);
    }

    public long nbOfTransactionsDone(String account) {
        createIfUnknownAccount(account);
        return nTransactions.get(account);
    }

    /**
     * Applies the transaction or else throws an exception if that
     * transaction is not reasonable
     */
    private void applyTransaction(Transaction t) throws BadTransactionException {
        t.checkSignature();
        long amount = t.movement.amount;
        String sender = t.movement.senderAccount;
        String receiver = t.movement.receiverAccount;
        long moneySender = moneyOf(sender);
        long moneyReceiver = moneyOf(receiver);


        if (amount < 0) throw new BadTransactionException("Negative amount");
        if (amount > moneySender) throw new BadTransactionException("Insufficient funds");

        if (moneySender < 0) throw new BadTransactionException("Negative balance");

        if (!balance.containsKey(receiver)) throw new BadTransactionException("Non-existent receiver");
        if (!balance.containsKey(sender)) throw new BadTransactionException("Non-existent sender");

        if (t.senderTransactionIndex != nTransactions.get(sender))
            throw new BadTransactionException("Invalid sender transaction index");

        // Update founds of sender / receiver
        balance.put(sender, (moneySender - amount));
        balance.put(receiver, (moneyReceiver + amount));

        // Update number of transactions done by the sender / receiver
        nTransactions.put(sender, nTransactions.get(sender) + 1);
        nTransactions.put(receiver, nTransactions.get(receiver));
    }

    public void runFullStory(List<Transaction> ts) throws BadTransactionException {
        for (Transaction t : ts)
            applyTransaction(t);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String account : balance.keySet()) {
            sb.append("Account: ").append(account).append("\n");
            sb.append("        balance: ").append(moneyOf(account)).append("\n");
            sb.append("  #transactions: ").append(nTransactions.get(account)).append("\n");
        }
        return sb.toString();
    }

}
