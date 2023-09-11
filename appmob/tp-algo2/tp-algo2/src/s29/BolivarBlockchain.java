
package s29;

import java.util.ArrayList;
import java.util.List;

public class BolivarBlockchain {
    // This can be changed to simulate harder Proof-of-Work!
    public static int DIFFICULTY = 3;
    public static int N_TRANSACTIONS_PER_BLOCK = 2;  // ~100 would be more reasonable

    private final List<Block> chainOfBlocks = new ArrayList<>();
    private final List<Transaction> committedTransactions = new ArrayList<>();
    private final List<Transaction> pendingTransactions = new ArrayList<>();

    public BolivarBlockchain() {
        Block genesis = new Block(0, "", "Genesis");
        genesis.mineBlock(DIFFICULTY);
        chainOfBlocks.add(genesis);
    }

    private void addNewStuff(Block newBlock, List<Transaction> newTransactions) {
        chainOfBlocks.add(newBlock);
        committedTransactions.addAll(newTransactions);
        System.out.println("Added a new block!");
        System.out.println(newBlock.toReadableString());
    }

    public void handleNewTransaction(Transaction t) throws BadTransactionException, BadBlockchainException {
        t.checkSignature();
        checkSubmission(t);
        pendingTransactions.add(t);
        // Now t would be broadcasted to every participant in the network...
        if (pendingTransactions.size() < N_TRANSACTIONS_PER_BLOCK) return;
        // Ok, there are enough transactions to forge a block. Let's try!
        Transaction[] tt = new Transaction[N_TRANSACTIONS_PER_BLOCK];
        tt = pendingTransactions.toArray(tt);
        Block b = newBlockForTransactions(tt);
        b.mineBlock(DIFFICULTY);
        // Now, provided that we "won" the competition and that our block b has been
        // elected by consensus, b would be broadcasted to every participant in
        // the network...
        addNewStuff(b, pendingTransactions);
        pendingTransactions.clear();
    }

    public void checkSubmission(Transaction t) throws BadTransactionException {
        List<Transaction> all = new ArrayList<>(committedTransactions);
        all.addAll(pendingTransactions);
        all.add(t);
        BolivarBlockchain.checkFullStory(all);
    }

    private Block newBlockForTransactions(Transaction[] ts) {
        if (ts.length != N_TRANSACTIONS_PER_BLOCK)
            throw new IllegalArgumentException("bad number of transactions");
        String digest = Transaction.combinedDigest(ts);
        int nBlocks = chainOfBlocks.size();
        Block lastBlock = chainOfBlocks.get(nBlocks - 1);
        Block newBlock = new Block(nBlocks, lastBlock.sha256(), digest);
        return newBlock;
    }

    private void checkBlocksAgainstTransactions() throws BadBlockchainException {
        int nb = chainOfBlocks.size() - 1; // the genesis block is special
        if (committedTransactions.size() != nb * N_TRANSACTIONS_PER_BLOCK)
            throw new BadBlockchainException("number of commited transactions is not a multiple of " + N_TRANSACTIONS_PER_BLOCK);
        for (int j = 1; j <= nb; j++) {
            Block b = chainOfBlocks.get(j);
            int from = (j - 1) * N_TRANSACTIONS_PER_BLOCK, to = from + N_TRANSACTIONS_PER_BLOCK;
            List<Transaction> tl = committedTransactions.subList(from, to);
            Transaction[] tt = new Transaction[N_TRANSACTIONS_PER_BLOCK];
            tt = tl.toArray(tt);
            if (!b.data.equals(Transaction.combinedDigest(tt)))
                throw new BadBlockchainException("The data digest in a block doesn't match the transactions" + b);
        }
    }

    private void checkBlockchain() throws BadBlockchainException {
        Block prev = null;
        for (Block crt : chainOfBlocks) {
            crt.checkValidity(DIFFICULTY, prev);
            prev = crt;
        }
    }

    public static void checkFullStory(List<Transaction> ts) throws BadTransactionException {
        BolivarBank tb = new BolivarBank();
        tb.runFullStory(ts);
    }

    /**
     * Returns the current state of the "machine" or
     * throws an exception if anything is invalid
     */
    public BolivarBank bank() throws BadTransactionException, BadBlockchainException {
        checkBlockchain();
        checkBlocksAgainstTransactions();
        BolivarBank bank = new BolivarBank();
        bank.runFullStory(committedTransactions);
        return bank;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block b : chainOfBlocks) {
            sb.append("Block: \n").append(b.toReadableString()).append("\n");
        }
        return sb.toString();
    }
}
