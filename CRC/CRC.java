package CRC;

/**
 * CRC - Cyclic Redundency Check
 * Software implementation of CRC, which is an efficient data link level 
 * error detection protocol. Normally implemented in hardware.
 * Takes a bit string and a generator polynomial, 
 * calculates the remainder when dividing the bit string by the generator 
 * by that polynomial.
 * Outputs the original bit string with the remainder attached, which can be 
 * checked for errors by running it through another instance of CRC with 
 * the same generator polynomial. If the transmission is error-free, this
 * will produce a remainder of 0. 
 * 
 *
 */
public class CRC {
    private int[] bits; // the output message after CRC applied
    private int bitsLength; // the length of the output message after CRC applied
    private int[] remainder; // the remainder as calculated by the CRC
    private int[] polynomial; // the binary representation of the polynomial
    private String message; // the output message after CRC calculated

    /**
     * Constructor to calculate the message that should be sent across
     * the network when transmitting bitString, using a cyclic redundency check
     * with the polynomial polynomialBitString.
     * To output the prepared message, call the printMessage() function. 
     * @param bitString - a string of the form [01]* e.g. 01010101 - the message to transmit. 
     * @param polynomialBitString - a string of the form [01]* where each 
     * digit corresponds to the coefficient of a polynomial. 
     * E.g. x^4 + x^2 + 1 = 1*x^4 + 0*x^3 + 1*x^2 + 0*x^1 + 1*x^0 = 10101
     * @throws IllegalArgumentException - if the input strings are null or contain
     * anything other than 0 and 1 
     */
    public CRC(String bitString, String polynomialBitString) {
        if (bitString == null || polynomialBitString == null) { throw new IllegalArgumentException(); }
        if (bitString.length() == 0 || polynomialBitString.length() == 0) { throw new IllegalArgumentException(); };

        bitsLength = bitString.length() + polynomialBitString.length() - 1; // output string length 
        bits = new int[bitsLength];
        polynomial = new int[polynomialBitString.length()];
        
        // convert data and polynomial into integer arrays
        try {
            for (int i = 0; i < bitString.length(); i++) {
                bits[i] = Integer.parseInt(bitString.substring(i, i+1));
                if (bits[i] < 0 || bits[i] > 1) {
                    throw new NumberFormatException();
                }
                
            }
            for (int i = 0; i < polynomial.length; i++) {
                polynomial[i] = Integer.parseInt(polynomialBitString.substring(i, i+1));
                if (polynomial[i] < 0 || polynomial[i] > 1) {
                    throw new NumberFormatException();
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }

        remainder = new int[polynomial.length - 1];
        calculateRemainder();

        // construct and cache outupt message
        StringBuilder s = new StringBuilder();
        for (int bit : bits) {
            s.append(bit);
        }
        this.message = s.toString();
    }

    /**
     * Calculates the remainder by repeatedly dividing the 
     * bit string by the polynomial. 
     */
    private void calculateRemainder() {
        // copy bits
        int[] temp = new int[bitsLength];
        for (int i = 0; i < bitsLength; i++) {
            temp[i] = bits[i];
        }

        // we iterate length - polynomial length + 1 times
        for (int i = 0; i < bitsLength - polynomial.length + 1; i++) {
            int firstBit = temp[i];
            if (firstBit == 1) {
                for (int j = 0; j < polynomial.length; j++) {
                    // XOR
                    temp[i+j] = Math.abs(temp[i+j] - polynomial[j]);
                }
            } else {
                // subtract all zeroes (do nothing)
            }
        }

        for (int i = 0; i < polynomial.length - 1; i++) {
            remainder[i] = temp[bitsLength - polynomial.length + 1 + i];
            bits[bitsLength - polynomial.length + 1 + i] = remainder[i];
        }
    }

    /**
     * Shifts all bits of the bit string left, putting a 
     * new bit in the right-most location.
     * Note: not used in current implementation.
     * @param newBit - the bit to add into the rightmost location.
     */
    public void shiftLeft(int newBit) {
        for (int i = 1; i < bitsLength; i++) {
            bits[i-1] = bits[i];
        }
        bits[bitsLength - 1] = newBit;
    }

    /**
     * Prints the output message to standard out.
     */
    public void printMessage() {
        for (int bit : bits) {
            System.out.print(bit);
        }
        System.out.println();
    }

    /**
     * Returns the message to send across the network as a bit string. 
     */
    public String getMessage() {
        return message;
    }

    /**
     * Prints the calculated remainder to standard out.
     */
    public void printRemainder() {
        for (int bit : remainder) {
            System.out.print(bit);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java CRC <message> <polynomial>");
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

        CRC crc = new CRC(message, polynomial);
        crc.printMessage();
    }
}