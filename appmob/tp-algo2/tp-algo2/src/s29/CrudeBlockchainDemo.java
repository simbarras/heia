
package s29;

import java.util.ArrayList;
import java.util.List;

public class CrudeBlockchainDemo {
  // This can be changed to simulate harder Proof-of-Work!
  public static int DIFFICULTY = 3;
  
  private final List<Block> chainOfBlocks = new ArrayList<>();
    
  CrudeBlockchainDemo(List<Block> chainOfBlocks) {
    this.chainOfBlocks.addAll(chainOfBlocks);
  }
  
  public void checkValidity() throws BadBlockchainException {
    Block prev = null;
    for(Block crt: chainOfBlocks) {
      crt.checkValidity(DIFFICULTY, prev);
      prev = crt;
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(Block b: chainOfBlocks) {
      sb.append("Block: \n").append(b.toReadableString()).append("\n");
    }
    return sb.toString();
  }
  
  //-------------------------------------------------------------
  
  static List<Block> someBlocksForDemo() {
    Block b0 = new Block(0, "", "initial-block-data");
    b0.mineBlock(DIFFICULTY);
    Block b1 = new Block(1, b0.sha256(), "-hello");
    b1.mineBlock(DIFFICULTY);
    Block b2 = new Block(2, b1.sha256(), "-world-of-blocks");
    b2.mineBlock(DIFFICULTY);
    List<Block> blocks = new ArrayList<>();
    blocks.add(b0);
    blocks.add(b1);
    blocks.add(b2);
    return blocks;
  }

  public static void main(String[] args) throws BadBlockchainException {
    long t0 = System.currentTimeMillis();
    List<Block> blocks = someBlocksForDemo();
    CrudeBlockchainDemo cb = new CrudeBlockchainDemo(blocks);
    System.out.println(cb);
    cb.checkValidity();
    System.out.println("All seems OK!");
    double duration = (System.currentTimeMillis()-t0)/1000.0;
    System.out.printf("done in %.2f seconds", duration);
  }

}
