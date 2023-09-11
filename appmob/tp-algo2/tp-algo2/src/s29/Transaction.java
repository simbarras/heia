
package s29;

import java.security.PrivateKey;

public class Transaction {
    public final MoneyTransfer movement;
    public final long senderTransactionIndex;
    public final String signature;

    static final String SEPARATOR = "/";

    Transaction(MoneyTransfer movement, long senderTransactionIndex, String signature) {
        this.movement = movement;
        this.senderTransactionIndex = senderTransactionIndex;
        this.signature = signature;
    }

    public static Transaction signedTransaction(MoneyTransfer movement,
                                                long senderTransactionIndex,
                                                PrivateKey kPriv) {
        String dataToSign = dataToBeSigned(movement, senderTransactionIndex);
        String signature = CryptoUtils.signatureFor(dataToSign, kPriv);
        return new Transaction(movement, senderTransactionIndex, signature);
    }

    static String dataToBeSigned(MoneyTransfer movement, long senderTransactionIndex) {
        return movement.toString() + SEPARATOR + senderTransactionIndex;
    }

    // small quirk: the format has a dependency to MoneyTransfer.toString()...
    @Override
    public String toString() {
        return movement.toString()
                + SEPARATOR + senderTransactionIndex
                + SEPARATOR + signature;
    }

    public static Transaction fromString(String s) {
        String[] parts = s.split(SEPARATOR);
        MoneyTransfer mt = MoneyTransfer.fromString(parts[0]);
        long index = Long.parseLong(parts[1]);
        return new Transaction(mt, index, parts[2]);
    }

    public static String combinedDigest(Transaction[] tt) {
        String s = CryptoUtils.sha256("");
        for (Transaction t : tt)
            s = CryptoUtils.sha256(s + t);
        return s;
    }

    public void checkSignature() throws BadTransactionException {
        if (!CryptoUtils.isSignatureValid(signature, dataToBeSigned(movement, senderTransactionIndex), CryptoUtils.publicKeyFromString(movement.senderAccount))) {
            throw new BadTransactionException("Invalid transaction");
        }
    }
}