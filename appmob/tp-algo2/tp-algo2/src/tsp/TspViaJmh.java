package tsp;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;

public class TspViaJmh {
  public static void main(String... args) throws RunnerException, IOException {
    if (args.length>0) {
      // Use standard JHM Main.
      // Command line options: try "-h" for a description
      org.openjdk.jmh.Main.main(args);
      return;
    }
    
    // "-bm avgt -f 2 -wi 3 -i 4 -p algoId=120,177,0 -p nbOfCities=100000"
    new Runner(config(true)).run();  // normal (with JIT)
    
    // idem but: "-p nbOfCities=20000 -jvmArgsAppend -Xint"
    // new Runner(config(false)).run(); // without JIT (far slower)
  }

  static Options config(boolean withJit) {
    int nbOfCities = withJit ? 50_000 : 10_000;
    ChainedOptionsBuilder ob = new OptionsBuilder()
    .warmupIterations(3)
    .measurementIterations(4)
    .forks(2)
    .mode(Mode.AverageTime)
    .param("algoId", "0", "101")
    .param("nbOfCities", ""+nbOfCities);
    if (!withJit) ob=ob.jvmArgsAppend("-Xint");
    return ob.build();
  }
}
