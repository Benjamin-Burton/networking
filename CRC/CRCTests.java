package CRC;

import java.util.concurrent.ThreadLocalRandom;

public class CRCTests {
    /**
     * Tests for the CRC and CRCREverse classes.
     * @param args - first argument, polynomial e.g. 101101
     * second argument - maxProb, which means that 1/maxProb
     * bits will be permuted during the error detection tests.
     * If no args, default to polynomial 1101, 1/100 probability of
     * permutation.
     */
    public static void main(String[] args) {

        String poly;
        int maxProb = 100;

        if (args.length != 2) {
            System.out.println("Arguments not valid. Using default polynomial 1101 and default maxProb of 100");
            poly = "1101";    
        } else {
            // check polynomial string is valid
            for (int i = 0; i < args[0].length(); i++) {
                if (args[0].charAt(i) != '0' && args[0].charAt(i) != '1') {
                    System.err.println("Invalid polynomial string");
                    System.exit(-1);
                }
            }
            poly = args[0];
            // check maxProb is valid
            try {
                maxProb = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid maxProb.");
                System.exit(-1);
            }
            if (maxProb < 1) {
                System.err.println("Invalid maxProb.");
                System.exit(-1);
            }
        }

        String generator = "1101";

        // general correctness tests
        // test 1 - encode and decode with remainder 0
        CRC crc = new CRC("1001001110", generator);
        System.out.println(crc.getMessage());
        CRCReverse reverseCRC = new CRCReverse(crc.getMessage(), generator);
        if (reverseCRC.isNoRemainder()) { 
            // test passed
            System.out.println("test case 1: passed.");
        } else {
            System.out.println("test case 1: failed.");
        }

        // test 2 - ensure remainder is calculated differently with similar data
        reverseCRC = new CRCReverse("1001001010101", generator);
        if (reverseCRC.getRemainder() != "101") {
            // test passed
            System.out.println("test case 2: passed.");
        } else {
            System.out.println("test case 2: failed.");
        }

        // test 3 - ensure remainder is calculated differently with similar data
        reverseCRC = new CRCReverse("1001001011101", generator);
        if (reverseCRC.getRemainder() != "101") {
            // test passed
            System.out.println("test case 3: passed.");
        } else {
            System.out.println("test case 3: failed.");
        }

        // new generator - x^4 + x^2 + 1 = 10101
        generator = "10101";

        crc = new CRC("11111111", generator);
        reverseCRC = new CRCReverse(crc.getMessage(), generator);
        if (reverseCRC.noRemainder) {
            // test passed
            System.out.println("test case 4: passed.");
        } else {
            System.out.println("test case 4: failed.");
        }

        // new generator - x^4 + x^3 + x + 1 = 11011
        generator = "11011";
        
        crc = new CRC("0000", generator);
        crc.printMessage();

        crc = new CRC("1111", generator);
        crc.printMessage();

        crc = new CRC("0101", generator);
        crc.printMessage();

        crc = new CRC("1010", generator);
        crc.printMessage();

        System.out.println("----------------------");
        // new generator - 1011
        generator = "1011";

        crc = new CRC("11001001", generator);
        reverseCRC = new CRCReverse(crc.getMessage(), generator);
        if (reverseCRC.isNoRemainder()) {
            // test passed
            System.out.println("test case for assignment: passed.");
        } else {
            System.out.println("test case for assignemnt: failed.");
        }

        // should produce a remainder i.e. an error
        reverseCRC = new CRCReverse("11101001001", generator);
        
        if (!reverseCRC.isNoRemainder()) {
            // test passed
            System.out.println("test case for assignment: passed.");
            System.out.println("Remainder: " + reverseCRC.getRemainder());
        } else {
            System.out.println("test case for assignemnt: failed.");
        }

        randomTests(poly, maxProb);
    }

    public static void randomTests(String poly, int maxProb) {
        // create 1000000 n-length (8 < n < 1500) strings and check they are correctly calculated
        // for each string, create a random number of permutations and check error is detected

        CRC crc;
        CRCReverse crcRev;
        int missedErrorCount = 0;
        for (int i = 0; i < 1000000; i++) {

            int n = ThreadLocalRandom.current().nextInt(8, 1500 + 1);
            // create the string
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < n; j++) {
                s.append(ThreadLocalRandom.current().nextInt(0,2));
            }
            try {
                crc = new CRC(s.toString(), poly);
                crcRev = new CRCReverse(crc.getMessage(), poly);
                if (!crcRev.noRemainder) {
                    System.out.println("Test failed.");
                    System.out.println(s.toString());
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            // permute the string and see about a remainder
            for (int k = 0; k < s.length(); k++) {
                if (ThreadLocalRandom.current().nextInt(0, maxProb) == 0) {
                    if (s.charAt(k) == '0') {
                        s.setCharAt(k, '1');
                    } else {
                        s.setCharAt(k, '0');
                    }
                }
            }
            crcRev = new CRCReverse(s.toString(), poly);
            if (crcRev.isNoRemainder()) {
                missedErrorCount += 1;
            }
        }
        System.out.println("Polynomial " + poly);
        System.out.println("Probability of bitflip: 1/" + maxProb);
        System.out.println("Missed Errors: " + missedErrorCount + "/1000000");

        // notes - with 1101 polynomial
        // bear in mind: our polynomial is not particularly good
        // consider testing with the IEEE standard polnomial
        // 1/8 bits permuted - missed 125097 errors
        // 1/16 bits permuted - missed 125636 errors
        // 1/100 bits permuted - missed 124955 errors
    }
}
