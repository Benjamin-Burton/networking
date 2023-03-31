package CRC;
/**
 * CRC - Cyclic Redundency Check
 * Software implementation of CRC, which is an efficient data link level 
 * error detection protocol. Normally implemented in hardware.
 * Takes a bit string and a generator polynomial, 
 * calculates the remainder when dividing the bit string by the generator 
 * by that polynomial.
 * Outputs the original bit string with the remainder attached, which can be 
 * checked for errors with CRCReverse.java.
 */
public class CRCReverse {
    int[] bits;
    int bitsLength;
    int[] remainder;
    int[] polynomial;
    boolean noRemainder;
    

    public CRCReverse(String bitString, String polynomialBitString) {
        bitsLength = bitString.length();
        bits = new int[bitsLength];
        for (int i = 0; i < bitString.length(); i++) {
            bits[i] = Integer.parseInt(bitString.substring(i, i+1));
        }

        polynomial = new int[polynomialBitString.length()];
        for (int i = 0; i < polynomial.length; i++) {
            polynomial[i] = Integer.parseInt(polynomialBitString.substring(i, i + 1));
        }

        remainder = new int[polynomial.length - 1];
        calculateRemainder();
    }

    public CRCReverse(int[] bits, String polynomialBitString) {
        this.bits = new int[bits.length];
        for (int i = 0; i < bits.length; i++) {
            this.bits[i] = bits[i];
        }
        this.bitsLength = bits.length;

        polynomial = new int[polynomialBitString.length()];
        for (int i = 0; i < polynomial.length; i++) {
            polynomial[i] = Integer.parseInt(polynomialBitString.substring(i, i + 1));
        }

        remainder = new int[polynomial.length - 1];
        calculateRemainder();
    }

    public void calculateRemainder() {
        // we iterate length - 3 times
        for (int i = 0; i < bitsLength - polynomial.length + 1; i++) {
            int firstBit = bits[i];
            if (firstBit == 1) {
                for (int j = 0; j < polynomial.length; j++) {
                    bits[i+j] = Math.abs(bits[i+j] - polynomial[j]);
                }
            } else {
                // subtract all zeroes (do nothing)
            }
        }
        noRemainder = true;
        for (int i = 0; i < 3; i++) {
            remainder[i] = bits[bitsLength - 3 + i];
            if (remainder[i] == 1) { noRemainder = false; }
        }
    }

    public void printRemainder() {
        for (int bit : remainder) {
            System.out.print(bit);
        }
        System.out.println();
    }

    public void shiftLeft(int newBit) {
        for (int i = 1; i < bitsLength; i++) {
            bits[i-1] = bits[i];
        }
        bits[bitsLength - 1] = newBit;
    }

    public void printMessage() {
        for (int bit : bits) {
            System.out.print(bit);
        }
        System.out.println();
    }

    public Boolean isNoRemainder() {
        return noRemainder;
    }

    public String getRemainder() {
        StringBuilder s = new StringBuilder();
        for (int bit : remainder) {
            s.append(bit);
        }
        return s.toString();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java CRCReverse <message> <polynomial>");
            System.exit(-1);
        }

        String message = args[0];
        String polynomial = args[1];

        // check input strings are valid
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) != '0' && message.charAt(i) != '1') {
                System.out.println("Error: message format incorrect.");
                System.exit(-1);
            }
        }

        for (int i = 0; i < polynomial.length(); i++) {
            if (message.charAt(i) != '0' && message.charAt(i) != '1') {
                System.out.println("Error: polynomial format incorrect.");
                System.exit(-1);
            }
        }

        CRCReverse crc = new CRCReverse(message, polynomial);
        if (crc.isNoRemainder()) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }
}