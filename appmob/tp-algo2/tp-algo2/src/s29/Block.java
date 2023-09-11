
package s29;

import java.util.Arrays;
import java.util.Random;

// In this simplified model, the "difficulty" parameter for the mining task has
// a very coarse granularity: it tells how many '0' characters must be present  
// at the beginning of the sha256(). The 8x finer granularity would be to count
// the number of the leading 0 bits (and not bytes).

public class Block {
    // mining-independent fields:
    public final long index;
    public final String previousHash;
    public final String data;

    // mining-dependent field:
    private long miningNonce;

    public Block(int index, String prevHash, String data) {
        this.index = index;
        this.previousHash = prevHash;
        this.data = data;
        this.miningNonce = 0;
    }

    public String sha256() {
        String allContents = toString();
        return CryptoUtils.sha256(allContents);
    }

    private static String targetPrefix(int difficulty) {
        return "0".repeat(difficulty);
        // Another way to code that:
        //   char[] zeros = new char[difficulty];
        //   Arrays.fill(zeros, '0');
        //   return new String(zeros);
    }

    /**
     * Changes miningNonce so that this.sha256() starts with k=difficulty '0's
     */
    public void mineBlock(int difficulty) {
        Random rnd = new Random();
        do {
            miningNonce = rnd.nextLong();
        } while (!isMined(difficulty));
    }

    public boolean isMined(int difficulty) {
        return sha256().startsWith(targetPrefix(difficulty));
    }

    public void checkValidity(int supposedDifficulty, Block supposedPreviousBlock)
            throws BadBlockchainException {
        String supposedPreviousHash = "";
        if (supposedPreviousBlock != null) supposedPreviousHash = supposedPreviousBlock.sha256();
        Block b = new Block((int) index, supposedPreviousHash, data);
        b.miningNonce = miningNonce;
        if (!b.isMined(supposedDifficulty)) {
            throw new BadBlockchainException("Invalid block");
        }
    }

    @Override
    public String toString() {
        return String.format("%d %s %s %d", index, previousHash, data, miningNonce);
    }

    public String toReadableString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(" BlockIndex: %d \n", index));
        sb.append(String.format("   PrevHash: %s \n", previousHash));
        sb.append(String.format("       Data: %s \n", data));
        sb.append(String.format("      Nonce: %d \n", miningNonce));
        sb.append(String.format("CurrentHash: %s \n", sha256()));
        return sb.toString();
    }

}
