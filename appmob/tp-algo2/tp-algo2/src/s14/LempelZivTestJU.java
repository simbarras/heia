package s14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.BeforeClass;
import org.junit.Test;
public class LempelZivTestJU {

  /** Temporary files names */
  static final String TMP_FOLDER="tmp";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    //create temp files from strings stored in the code
    File tempDir = new File(TMP_FOLDER);
    tempDir.mkdir();
  }

  /** special case where no string is "running" when end-of-file happens */
  @Test public void testLastWordComplete() {
    final String CONTENT = "aababcabcd"; // a ab abc abcd
    testWithContent(CONTENT);
  }
  
  /** normal case where a string is "running" when end-of-file happens */
  @Test public void testLastWordIncomplete() {
    final String CONTENT = "aababcabcdab"; // a ab abc abcd ab
    testWithContent(CONTENT);
  }

  /** message with zeros (contains some \n) */
  @Test public void testZeroContent() {
    final String CONTENT_ZEROS = "000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000";
    int zipLen = testWithContent(CONTENT_ZEROS);
    String msg = "poor compresion level: "+ zipLen+" bytes instead of ~441...";
    assertTrue(msg, zipLen<500);
    // prevent the bug where each char c is coded as 0-c (aaa -> 0a 0a 0a)... 
  }

  /** message with random content (contains some \n) */
  @Test public void testRandomContent() {
    final String CONTENT_RANDOM = "vz2sns9QSE5J5Fk3pJ52\noBSOiCNF6cCCX5WC4oM4\nSOWJ6cUp3Puy4yrelvzR\n8t6qEJ9bUceXgS0MD9mN\n9NbqLqFd8uR21LsP1wSS\nZw9zuDd9Smk1ThkLmBaO\nDUOEMVh3t53iRM1d6kRC\nO4ty6KQBGrIaOgC7Fal7\nDn6PHm51s4VKttkBZ1Q9\nTYKEIMOiZAfTpSlhbkSA\nYtjzAo24N1b0nBHCXhAr\nO08p668GzshuOTdpguah\noNTRLZpnU2lQ4XFL1vzd\n0Ux3AELcWPcPi5oB6xld\n218tZr4TVswnXNQ1nGrZ\nvi2FhLGigrjIDpbAANoi\nRgK4A7gvCSc7aDI2ga8L\nboEKuXnhoamlUNkRUiXM\n8c1FNojPJrjW2hiA5LCx\ntR5tOJ9ahSbzJt1kQJxA\nygCVNYX3d9glokA6IQgv\nRqGSfo99RlCTWFOsAd9r\nSgnoNz27I6IEGzZbeC6w\nKXAhISFK5iykZ1Fs9VFf\nRe3F2JxzJIxkics4GtKz\nYi2zkMBkN3W84IWSlZVm\nrRkcy3SGFlqQ69PsuPCf\nJS1QHo8GSSCTZW0GK66a\nMn1WcUBXzGf0MW2n3bEW\n7606RJpUG016JTYRIIEE\nChiF4O6DwOE6WEplZ6FN\n2TXm8W7Q0khxMtONIz8p\nGohYFaDzmFHuWJmkwAXs\nobkw8gyRZwNcEjRkYKJK\nnxMRC3Go0oIxIWKfDmM2\n6pOVZRBHB9pKYdq0FkkB\noCj83lrekejUJMBjhgvH\n2R7lBUnO34JloS48TOiB\np6wxtJ9GfVqf52HV79Qm\ni8KxNCHRNeXEK0L4DKaP\nqUW2wpgUbeAUT6iLLSN8\nCWyNDh7H5iEIzf4l0KSg\nQYRDzQZM0DMbWv8JhcWM\ni4ghGAh2TBmxbmUk79ny\nHYIn9BQHrbauJeK6xqI6\n3Q4X6cu0dBagPHCu6YfM\nff73qSdpe5YbVHwX31h1\nv0HBPLFEA0MFSbnw2xc8\n65saxX2Y1hxoBQvMuz2X\nbx8kZvJVTL1D1VT7P0Op\nlnVnqbpOW6r3SfCAXaok\nrNM5GsRt9g1wfYLeW4M9\nkFTXIKc430IE8rVyNG0B\n9k6kDDaqGDrbGQjnceka\ntNeIiN5S8iZYJP6L0jxR\nHDXvOUt1zvw0uci1MKLM\nfLsvHcgTxuXjXUrcYOWo\nJKVeF6cdXeFYvEBf15rA\nSM5bAMzTcdiZYXAtrWYl\nReoJfwKohjdXBQT4x5at\nGYKWlOGS1tN50mek7Y9k\nE0P9ZDIuTGJO1FUm1TF6\nAK0nL7qNu8N0f86Xvso0\nFfIZikwjfQjploBeKK0q\ndJjTxDUpkDoAfpjBlNar\nawi2y4Er3M7SeZO9X4U6\nZT6L8j7jrx2JRSXGUWi9\nOqp1uokFtpvbkmhURMqK\n0RvKlSz1uELAvTqP9e4n\nOb5vUsz2smGXoNMN3uJa\n6QIIkLyRBQjGFkMtlsfF\nNMsEOzRhG0c6SHK2aUNI\n9mxjFkI14z6sht4Hjfsp\nmJcUmkWn3WPbe1l7Spsy\nHrGa68AlxpawTXJGyBMz\nVTBnMXV41AwRAAIoDglo\nrkGRypo4uUCtn9o84BQY\nYjCRcGdk0QuLQbyXEJGx\nO9A14Kfmr7Sgv8TKLA8w\nTROjrm7oVgKLZzmb5mrc\nhiRSWvJU4WZOya56t7YP\nW9AErWQ5ZprmhL2cbDJ3\nmi4b1MYWVGwMc4M0yBf6\n0Kbp6G0PejDAAphRRqHI\nPecPWpLEB3EBUyOz7y35\n1wjmvk5JFHU5K7ikphFL\nNEnKYol3xwYTjlywtz74\n9VM1O0zapnyjzeownTzo\nvBGPvmTljUfpDca2z9NN\nnNLMsF5apzzKEM3KOFvL\nJCZ9IcU0b6vF3QMvHeFv\nQ0KYCVbewMJckvBSKGHh\nYXKWeTiLZlqbdwHHiM1F\nl3gF9T4CI4IpoLeJhyKq\nNOWj5j6fqFs5PZr936VI\nsng7Sngk7jCP0DVngZy7\nvQHJQSgjkbjLChrvHxuS\n1NrZ8vEMPfLUwz49hmWb\nPThyrZUNOtnjRmHmT20R\n0gs9KLlXq28KM6vv6M8H\nhx5nBBYJ90LBPutegtvK\nPn68lDdWIi2Exjdv1WwX\nzGDEv3Kv8hdl3Qoqvigi\nLPX8HoPMa0DABWN1XuPN\nSqyYEFThO8sFBapWIEqe\n2aXLmjyT4sXMA610XH1A\nLVTDaZ1gWReOpErs4k1z\nYkVUvHf5f2xizCIvaGdI\nYL8jHAB5nmRh1qKN6fES\nPNMhnmQkVcWkntD0maWs\ntGH6kroihkYVAdgdnh94\ngs4wmrRDjWhc3Q5aBMPU\nAqdjZH60wY0HHfr0CWpx\nkypud6EB5cUxTS0obb5K\nDlhEFONk0sGPzdpO3dVU\npZnjqln6fzfq7CkEs8ct\nj4RBMnFwSTo8kv0KdFfJ\n7ehXPNHhlt2oVyxFmsDS\nnrw6axHLwmy0hRk9moAU\nWSOiRcXpRAn8EVBor0nv\nr4BlI2D8OlyeD3QUJmCg\nDf9L9cFfkLMQpRQygL6d\nMGuPz0tqHF9HSdaSlvyH\nAcHw7UeVMFXQBP0mf5hZ\nAt5eoDG65dQr2S86K2Ao\njBHBoRIUIl1VHNI0ffxz\nyH6i4L6GCkrMISQsyizx\nxId31qFApB14E4iXe1Jv\njUCIZvLL5VvrTnTqHWlF\n2uK2GdPKKNsXv4I3uB7Q\nCG2nNmYCUirpHEfLAiMN\nc2khWyMssYZ5cxjQflqh\nTI7ff84dlEMOdbO0kDM1\nQatDCClJO0UbZ8xWNq3Z\nZvx3zHkmQ5D619tXH06p\nmtxaVJ7Ta3LpakSXFfl0\n8q7NYb0tXKDbT6ax1JS0\nULT9fGFpdSe1xCcy9Cad\nKktuB5C5NDQdr4R95Dhz\nY1GeezIkvZddC08hvDQx\npOJERcqcp9p1e1C1FlJQ\nu13U1kEChXWp7LKU15Yj\nvpfp1JX8V2bVwA1OCmRU\nseyXqQQz8pTdPfG2xwIw\ntPfjdebPxaQyWhCH4hAU\nJIQ1vMSpd23yrFiCdXUX\nsv5MOx58YOKoz3flT8q3\nxNYbZzlac8n6s328U\nT1d9GbzpxdUrJ2gO2NsO\nwtg5Gex6vNC3lKbokP2X\nGnqqpoelpGkSieZSqDME\n9kzkCXEBddHds0OmfkYd\nBR1Xk4MGnsGJ50AFtrA7\nw7edltdLevEm6K9qDTcU\n5iQMotVIC7eacVEwCM1f\n5GDdGegVACw6AbIcR4Wi\n5C5ToWAsVnXcBWtyUjxY\nCGzk8cy0F2ePkLOSoTEk\npITYFkKiwc54y8NNoZA0\nKzBg1WfdDJVfnm3Xd7v1\nUatUIC0R3a0Cdikv5cnp\n4bLBhm34B8Lb3GCennxU\nECvf12WKdaxDqTRBAYhF\nRE4vZ7UaLpRYAn5mv21d\nKNMYK1gfIQ9BZb7FBG4t\nfyKMLlLKqEuj7b999ZON\nlgbqYEof0x13AwmxqXQm\nJcE5KkQsmXEqMtFS3Pr2\nFeCkAWtBsW9B3l6mt0UN\niHgSSVGps3ftlCgde7TF\niJq7SvCR93xWDlk4BRHa\nxKc8pQi6NCPJQdc6oAm3\n9cOB084X609jTWvuMFY3\nho93jzMmKWGVTPnnEgul\nSVpGjvlAgZyfZ1twGCi6\nCu1hyr0hyA8xm20j3RKt\n0ls8x8LGkGP2vVUWRMRi\nCEzGLMl2Hldo8FH7LsEQ\nNE2nSlVxIqpJJAPK8J8l\nNIymrlR6t8PxtSqQR4eB\nc5SAROxSnsqxujnLMIY5\nBGROve4MLlwRdqNpyimF\nDPVQ7cCZ5HzwdIL2qvli\neXq0EyJSbEaLIpZLlGss\nzb2UQFlbp0EUUsbaJz6m\nSrBCwTeG5uxmgNN3NqCU\nCXnKRmHWeMLG95w4KFTQ\nPVSpxNs3oMgSRoEHZTxo\nTtBcbQucphpFG8Fd02cC\nS9ILMyxKAps9DDcMre7g\nQcznp0ujymxSYagITcke\nX7mPr58wWKE2odyFIWTK\nTvVRUwFUk1BEsQ33r4yM\nyhhUbPGSJWLGWJasdzLm\nevO1XDd228PSjrdRRFoD\nkeKGxw8p1HbrHRdu3YXS\n4sjmdxRFXovo2zBbpi6X\nw4qYzo4Kp3qflTppdQvt\ni4FGxDcTjqzo2tIXeLg0\nVVN6TO5RWoxEjRp3l27C";
    testWithContent(CONTENT_RANDOM);
  }
  
  /** message with text (contains some \n) */
  @Test public void testTextContent() {
    final String CONTENT_TEXT = "The Elder Scrolls V: Skyrim Review, Say goodbye to real life.\n\nI was stacking books on a shelf in my house in Whiterun, one of Skyrim's major cities, when I noticed a weapon rack right beside it. I set a sacrificial dagger in one slot, an Orcish mace in the other. They were on display for nobody but me and my computer controlled housecarl, Lydia, who sat at a table patiently waiting for me to ask her to go questing. The chest upstairs was reserved for excess weapons and armor, the bedside table for smithing ingots and ores, the one next to the Alchemy table for ingredients. I'd meticulously organized my owned virtual property not because I had to, but because tending to the minutia of domestic life is a comforting break from dealing with screaming frost trolls, dragons, a civil war, and job assignments that never seem to go as planned. It's even a sensible thing to do; a seemingly natural component of every day existence in Skyrim, one of the most fully realized, easily enjoyable, and utterly engrossing role playing games ever made. \n\nPart of what makes it so enjoyable has to do with how legacy Elder Scrolls clutter has been condensed and in some cases eliminated. In Skyrim, there's no more moon hopping between hilltops with a maxed out Acrobatics skill. That's gone, so is Athletics. The Elder Scrolls V pares down the amount of skills and cuts out attributes like Endurance and Intelligence altogether. There's no time wasted on the character creation screen agonizing over which skills to assign as major. You don't assign major and minor skills at all, but instead pick one of ten races, each with a specific bonus. High Elves can once a day regenerate magicka quickly, Orcs can enter a berserk rage for more effective close range combat. These abilities are best paired with certain character builds the High Elf regeneration is useful for a magic user but don't represent a rigid class choice. Major decisions don't need to be made until you're already out in the world and can try out magic, sneaking and weapon combat, emphasizing first hand experience over instruction manual study, letting you specialize only when you're ready. \n\nIt contributes to the thrilling sense of freedom associated with life in Skyrim. Do a quest, kill a dragon, snatch torchbugs from the air, munch on butterfly wings or simply wander while listening to one of the best game soundtracks in recent memory. Despite the enormity of the world and the colossal amount of content contained within, little feels random and useless. Even chewing on a butterfly wing has purpose, as it reveals one of several alchemical parameters later useful in potion making at an alchemy table. Mined ore and scraps of metal from Dwemer ruins can be smelted into ingots and fashioned into armor sets, pelts lifted from slain wildlife can be turned into leather armor sets, and random books plucked from ancient ruins can trigger hidden quest lines that lead to valuable rewards. Skyrim's land mass is absolutely stuffed with content and curiosities, making every step you take, even if it's through what seems like total wilderness, an exciting one, as something unexpected often lies just over the next ridge. \n\nMany times the unexpected takes the form of a dragon. Sometimes they're purposefully placed to guard relics, sometimes they swoop over cities and attack at seemingly random times. In the middle of a fight against a camp of bandits a dragon might strike, screaming through the sky and searing foe and friendly alike with frost or flame. Momentarily all on the battlefield unite, directing arrows and magic blasts upward to knock down the creature, creating impromptu moments of camaraderie, a surprising change from what may have been yet another by the numbers bandit camp sweep. Dragons show up often, their presence announced by an ominous flap of broad wings or an otherworldly scream from high above. The scale and startling detail built into each creature's appearance and animations as it circles, stops to attack, circles again and slams to the ground makes encounters thrilling, though their predictable attack patterns lessen the excitement after a few battles. In the long run they're far less irritating than the Oblivion gate equivalent from The Elder Scrolls IV, can be completed in a few minutes, and always offer a useful reward. \n\nKilling a dragon yields a soul, which powers Skyrim's new Shout system. These are magical abilities any character can use, you don't have to specialize in spell casting to slow time, throw your voice, change the weather, call in allies, blast out ice and fire, or knock back enemies with a rolling wave of pure force. Even if you favor sword, shield and heavy armor and ignore magic entirely, you'll still be able to take full advantage of these abilities provided you find the proper words each Shout has three hidden on Skyrim's high snowy peaks and in the depths of forgotten dungeons, serving as another reason to continue exploring long after you've exhausted the main quest story, joined with the Thieves Guild, fought alongside the Dark Brotherhood, or thrown your support behind one of the factions vying for control of Skyrim. \n\nNot only is this land under assault by dragons, long thought to be dead, it's also ripped in two by civil war. You can choose one side or the other, but so much of the allure of Skyrim is how, even outside of the confines of quest lines, the embattled state of the world is evident, and steeped in a rich fictional legacy. Lord of the Rings this is not, but with the release of every Elder Scrolls game, the fiction becomes denser, and the cross referencing for long time fans all the more rewarding. \n\nSkyrim's residents are all aware of current events. They'll comment on the civil war, some sympathizing with the rebels, others thinking the establishment sold its soul. The peasants complain about the Jarls who control each settlement, the Jarls complain about the rebels or foreign policy, the overprotective College librarian complains when I drop dragon scales all over his floor; many characters feel like whole, distinct personalities instead of vacuous nothings that hand out quests like a downtown greeter hands out flyers for discount jeans. Characters stereotype based on race, they double cross at even the slightest hint it might be profitable, and they react to your evolving stature within the world. It makes a ridiculous realm, filled with computer controlled cat people and humanoid reptiles, demon gods and dragons, feel authentic, like a world that existed long before you showed up and will continue to exist long after you leave. ";
    testWithContent(CONTENT_TEXT);
  }

  // returns: nb of bytes in the compressed file
  int testWithContent(String s) {
    final String filenameOrig = TMP_FOLDER+"/sample1";
    final String filenameZipped = filenameOrig + "_compressed";
    final String filenameUnzipped = filenameOrig + "_decompressed";
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(filenameOrig));
      out.write(s);
      out.close();

      LempelZiv.code(filenameOrig, filenameZipped);
      LempelZiv.decode(filenameZipped, filenameUnzipped);
      
      String hash1=Utils.hashFile(filenameOrig);
      String hash2=Utils.hashFile(filenameUnzipped);
      assertEquals("Bad checksum of decompressed file.", hash1, hash2);

      byte[] zipped = Utils.readBytesFromFile(filenameZipped);
      return zipped.length;
    } catch (FileNotFoundException e){
      fail("Some required files are missing.... "+ e.getMessage());
    } catch (IOException e) {
      fail("Internal test failure, unable to create or access temp files: " + e.getMessage());
    }
    return 0;
  }
  
  /**
   * Some utility methods used by the tests.
   * @author alexandr.peclat
   */
  public static class Utils{
    
    /**
     * Read bytes of a given file.
     * @param file            Path to the file to read.
     * @return                Contained data as an array of bytes.
     * @throws IOException    If the file cannot be found or opened.
     */
    public static byte[] readBytesFromFile(String file) throws IOException{
      
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      InputStream is = new FileInputStream(file);
      byte[] temp = new byte[1024];
      int read;

      while((read = is.read(temp)) > 0){
         buffer.write(temp, 0, read);
      }

      is.close();
      return buffer.toByteArray();
    }
      
    /**
     * Computes the SHA1 hash of a file, give the hexadecimal form.
     * @param file    the path to the file
     * @return        the SHA1 hash as a string on hexadecimal form
     * @throws IOException 
     */
    public static String hashFile(String file) throws IOException {
      return hash(readBytesFromFile(file));
    }
    
    public static String hash(byte[] data) {
      
      MessageDigest md;
      byte[] digest = new byte[20];
      
      try {
        md = MessageDigest.getInstance("SHA1");
        md.reset();
        md.update(data, 0, data.length);
        digest = md.digest();
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }      
      return convertToHex(digest);
    }
    
    /**
     * Convert an array of bytes to a human readable hexadecimal string.
     * @param data    the array of bytes to process
     * @return        the hexadecimal representation of data
     */
    public static String convertToHex(byte[] data) { 
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < data.length; i++) { 
          int halfbyte = (data[i] >>> 4) & 0x0F;
          int two_halfs = 0;
          do { 
              if ((0 <= halfbyte) && (halfbyte <= 9)) 
                  buf.append((char) ('0' + halfbyte));
              else 
                  buf.append((char) ('a' + (halfbyte - 10)));
              halfbyte = data[i] & 0x0F;
          } while(two_halfs++ < 1);
      } 
      return buf.toString();
    }
  }

}
