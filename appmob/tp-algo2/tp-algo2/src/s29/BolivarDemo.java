
package s29;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

public class BolivarDemo {

    static KeyPair john = CryptoUtils.newRsaKeyPair();
    static KeyPair kurt = CryptoUtils.newRsaKeyPair();
    static KeyPair mary = CryptoUtils.newRsaKeyPair();
    static String johnPub = CryptoUtils.stringFromPublicKey(john.getPublic());
    static String kurtPub = CryptoUtils.stringFromPublicKey(kurt.getPublic());
    static String maryPub = CryptoUtils.stringFromPublicKey(mary.getPublic());

    static List<Transaction> someTransactionsForDemo() {
        MoneyTransfer johnToKurt = new MoneyTransfer(7, johnPub, kurtPub);
        MoneyTransfer johnToMary1 = new MoneyTransfer(8, johnPub, maryPub);
        MoneyTransfer maryToKurt = new MoneyTransfer(3, maryPub, kurtPub);
        MoneyTransfer johnToMary2 = new MoneyTransfer(6, johnPub, maryPub);
        MoneyTransfer kurtToMary = new MoneyTransfer(5, kurtPub, maryPub);
        MoneyTransfer johnToMary3 = new MoneyTransfer(4, johnPub, maryPub);

        List<Transaction> tl = new ArrayList<>();
        tl.add(Transaction.signedTransaction(johnToKurt, 0, john.getPrivate()));
        tl.add(Transaction.signedTransaction(johnToMary1, 1, john.getPrivate()));
        tl.add(Transaction.signedTransaction(maryToKurt, 0, mary.getPrivate()));
        tl.add(Transaction.signedTransaction(johnToMary2, 2, john.getPrivate()));
        tl.add(Transaction.signedTransaction(kurtToMary, 0, kurt.getPrivate()));
        tl.add(Transaction.signedTransaction(johnToMary3, 3, john.getPrivate()));

        return tl;
    }

    static void checkBoolean(boolean b, String msg) {
        if (!b) throw new IllegalStateException(msg);
    }


    public static void main(String[] args) throws BadTransactionException, BadBlockchainException {
        System.out.println("John's account: " + johnPub);
        System.out.println("Kurt's account: " + kurtPub);
        System.out.println("Mary's account: " + maryPub);
        System.out.println();

        BolivarBlockchain bbc = new BolivarBlockchain();
        System.out.println(bbc);
        bbc.bank(); // brand new Bank, no accounts for the moment
        for (Transaction t : someTransactionsForDemo()) {
            bbc.handleNewTransaction(t);
        }
        System.out.println(bbc);
        BolivarBank bank = bbc.bank();
        System.out.println(bank);
        long johnMoney = bank.moneyOf(johnPub);
        long kurtMoney = bank.moneyOf(kurtPub);
        long maryMoney = bank.moneyOf(maryPub);
        checkBoolean(johnMoney == 75, "Wrong balance for John (" + johnMoney + ")...");
        checkBoolean(kurtMoney == 105, "Wrong balance for Kurt (" + kurtMoney + ")...");
        checkBoolean(maryMoney == 120, "Wrong balance for Mary (" + maryMoney + ")...");

        // ------------------------------------------------------------

        // Negative amount
        try {
            bbc.handleNewTransaction(Transaction.signedTransaction(new MoneyTransfer(-7, johnPub, kurtPub), 4, john.getPrivate()));
        } catch (BadTransactionException e) {
            if (!e.getMessage().equals("Negative amount")) throw e;
        }
        johnMoney = bbc.bank().moneyOf(johnPub);
        checkBoolean(johnMoney == 75, "Wrong balance for John on negative amount (" + johnMoney + ")...");


        // Negative index
        try {
            bbc.handleNewTransaction(Transaction.signedTransaction(new MoneyTransfer(7, johnPub, kurtPub), -1, john.getPrivate()));
        } catch (BadTransactionException e) {
            if (!e.getMessage().equals("Invalid sender transaction index")) throw e;
        }
        johnMoney = bbc.bank().moneyOf(johnPub);
        checkBoolean(johnMoney == 75, "Wrong balance for John on negative index (" + johnMoney + ")...");

        // Too big amount
        try {
            bbc.handleNewTransaction(Transaction.signedTransaction(new MoneyTransfer(500, johnPub, kurtPub), 4, john.getPrivate()));
        } catch (BadTransactionException e) {
            if (!e.getMessage().equals("Insufficient funds")) throw e;
        }
        johnMoney = bbc.bank().moneyOf(johnPub);
        checkBoolean(johnMoney == 75, "Wrong balance for John on too big amount (" + johnMoney + ")...");


        // OK
        bbc.handleNewTransaction(Transaction.signedTransaction(new MoneyTransfer(10, johnPub, kurtPub), 4, john.getPrivate()));
        bbc.handleNewTransaction(Transaction.signedTransaction(new MoneyTransfer(5, johnPub, kurtPub), 5, john.getPrivate()));
        bbc.handleNewTransaction(Transaction.signedTransaction(new MoneyTransfer(3, johnPub, kurtPub), 6, john.getPrivate()));
        bbc.handleNewTransaction(Transaction.signedTransaction(new MoneyTransfer(2, johnPub, kurtPub), 7, john.getPrivate()));
        johnMoney = bbc.bank().moneyOf(johnPub);
        checkBoolean(johnMoney == 55, "Wrong balance for John on a legit transaction (" + johnMoney + ")...");
        // ------------------------------------------------------------

        System.out.println("Everything seems Ok!");
    }

}
