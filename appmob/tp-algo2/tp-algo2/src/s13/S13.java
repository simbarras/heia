
package s13;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.*;

public class S13 {

    // ------ Ex. 1 -----------
    public static void printSomePowersOfTwo() {
        IntStream.rangeClosed(0, 10)
                .map(e -> (int) Math.pow(2, e))
                .forEach(System.out::println);
    }

    // ------ Ex. 2 -----------
    public static String macAddress(int[] t) {
        String result = Arrays.stream(t)
                .mapToObj(n -> String.format("%02X", n))
                .reduce("", (partialString, element) -> {
                    if (partialString.equals("")) {
                        return element;
                    } else {
                        return partialString + ":" + element;
                    }
                });
        return result;
    }

    // ------ Ex. 3 -----------
    public static long[] divisors(long n) {
        return LongStream.range(1, n).filter(e -> (n % e) == 0).toArray();
    }

    // ------ Ex. 4a -----------
    public static long sumOfDivisors(long n) {
        /*long[] t = divisors(n);
        long result = Arrays.stream(t).reduce(0, (sum, e) -> sum + e);
        return result;*/
        return Arrays.stream(divisors(n)).reduce(0, (sum, e) -> sum + e);
    }


    // ------ Ex. 4b -----------
    public static long[] perfectNumbers(long max) {
        return LongStream.range(1, max).filter(val -> val == sumOfDivisors(val)).toArray();
    }

    public static long nextPerfectNumbers(long start) {
        while (++start != sumOfDivisors(start)) {
            System.out.println("\tTry: " + start);
            if (start == Integer.MAX_VALUE) return -1;
        }
        return start;
    }

    // ------ Ex. 5 -----------
    public static void printMagicWord() throws IOException {
        Path path = FileSystems.getDefault().getPath("wordlist.txt");
        Files.lines(path)
                .filter(x -> x.length() == 11)
                .filter(x -> x.charAt(2) == 't')
                .filter(x -> x.charAt(4) == 'l')
                .filter(x -> x.chars().distinct().count() == 6)
                .forEach(System.out::println);
    }

    public static boolean isPalindrome(String str) {
        return str.equals(new StringBuilder(str).reverse().toString());
    }

    public static void printPalindromes() throws IOException {
        Path path = FileSystems.getDefault().getPath("wordlist.txt");
        Files.lines(path)
                .filter(x -> x.length() >= 4)
                .filter(S13::isPalindrome)
                .forEach(System.out::println);
    }

    // ------ Ex. 6 -----------
    public static void averages() {
        double[][] results = {
                {9, 10, 8, 5, 9},
                {5, 9, 9, 8, 8},
                {4, 8, 10, 9, 5},
                {8, 10, 8, 10, 7},
                {8, 9, 7, 10, 6},
        };
        double[] grades = new double[results.length];
        for (int i = 0; i < results.length; i++) {
            double sum = 0;
            for (int j = 0; j < results[i].length; j++) {
                sum += results[i][j];
            }
            grades[i] = 1.0 + sum / 10.0;
        }
        double sum = 0;
        for (int i = 0; i < grades.length; i++) {
            sum += grades[i];
        }
        double average = sum / grades.length;

        System.out.printf("Grades : %s%n", Arrays.toString(grades));
        System.out.printf("Average: %.2f%n", average);
    }

    public static void averagesWithStreams() {
        double[][] results = {
                {9, 10, 8, 5, 9},
                {5, 9, 9, 8, 8},
                {4, 8, 10, 9, 5},
                {8, 10, 8, 10, 7},
                {8, 9, 7, 10, 6},};
        double[] grades = Arrays.stream(results)
                .mapToDouble(elt -> Arrays.stream(elt).reduce(0, (sum, e) -> sum + e))
                .map(elt -> elt / 10.0 + 1)
                .toArray();

        double average = Arrays.stream(grades).average().getAsDouble();

        System.out.printf("Grades : %s%n", Arrays.toString(grades));
        System.out.printf("Average: %.2f%n", average);
    }

    // ------ Ex. 8 -----------

    static DoubleStream sampling(double from, double to, int nSubSamples) {
        Random rnd = new Random();
        return rnd.doubles(nSubSamples, from, to);
    }

    public static void ex8() {
        double[] product = {1.2, 3.4, 5.6};
        System.out.println("... product of:  " + Arrays.toString(product));
        System.out.println(Arrays.stream(product)
                .reduce(1, (prod, elt) -> prod * elt));

        int start2 = 4;
        int end2 = 30;
        System.out.println("... math series:  i/2^i for i between " + start2 + " and " + end2);
        System.out.println(Arrays.toString(IntStream.rangeClosed(4, 30).mapToDouble(elt -> elt / Math.pow(2, elt)).toArray()));

        String[] t = {"Hello", "World", "!"};
        System.out.println("... concatenation of: " + Arrays.toString(t));
        String joined = Arrays.stream(t).reduce("", (phrase, word) -> phrase + word);
        System.out.println(joined);

        double start4 = Math.PI / 4.0;
        double end4 = Math.PI / 3.0;
        int nbSample = 2002;
        System.out.println("... max of: sin^2*cos in [" + start4 + ".." + end4 + "], " + nbSample + " samples");
        System.out.println(sampling(start4, end4, nbSample).max());
    }


    // ------ Ex. 9 -----------
    public static void nipas(int n) {
        System.out.println(
                IntStream.range(0, n)
                        .mapToObj(
                                i -> IntStream.range(i, i + 4)
                                        .mapToObj(j ->
                                                new String(new char[n + 2 - j]).replace("\0", " ") +
                                                        new String(new char[1 + 2 * j]).replace("\0", "*"))
                                        .collect(Collectors.joining("\n")))
                        .collect(Collectors.joining("\n"))
        );
    }

    public static void nipasEnVerlan(int n) {
        System.out.println("nipas à l'envers ça fait sapin");

        String sapin = "";
        for (int i = 0; i < n; i++) {
            for (int j = i; j < i + 4; j++) {
                for (int k = 0; k < n + 2 - j; k++) {
                    sapin += " ";
                }
                for (int k = 0; k < 1 + 2 * j; k++) {
                    sapin += "*";
                }
                sapin += "\n";
            }
        }
        System.out.println(sapin);
    }

    //-------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("Ex1 --------------------");
        printSomePowersOfTwo();

        System.out.println("Ex2 --------------------");
        System.out.println(macAddress(new int[]{78, 26, 253, 6, 240, 13}));

        System.out.println("Ex3 --------------------");
        System.out.println(Arrays.toString(divisors(496L)));

        System.out.println("Ex4a --------------------");
        System.out.println(sumOfDivisors(496L));

        System.out.println("Ex4b --------------------");
        System.out.println(Arrays.toString(perfectNumbers(10_000)));

//        System.out.println("Ex4c --------------------");
//        System.out.println(nextPerfectNumbers(10_000));

        try {
            System.out.println("Ex5ab --------------------");
            printMagicWord();

            System.out.println("Ex5c --------------------");
            printPalindromes();
        } catch (IOException e) {
            System.out.println("!! Problem when reading file... " + e.getMessage());
        }

        System.out.println("Ex6 --------------------");
        averages();
        averagesWithStreams();

        System.out.println("Ex8 --------------------");
        ex8();

        System.out.println("Ex9 --------------------");
        nipas(4); // read/analyze the code first!...
        nipasEnVerlan(4);
    }

}
