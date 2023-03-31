# CRC - Cyclic Redundancy Check

Software implementation of CRC, which is an efficient data link level 
error detection protocol. Normally implemented in hardware.
Takes a bit string and a generator polynomial, 
calculates the remainder when dividing the bit string by the generator 
by that polynomial.
Outputs the original bit string with the remainder attached, which can be 
checked for errors by running it through another instance of CRC with 
the same generator polynomial. If the transmission is error-free, this
will produce a remainder of 0. 

## Usage
Use the CRC object to calculate the CRC, which is the remainder when
dividing the message by the polynomial. 
CRC will then output the message to be sent through the network.

Use the CRCReverse object to check a message output by CRC with the same
polynomial. If the remainder is 0, it is likely that there was no error. 
If the remainder is not zero, an error has been detected.

Use the CRCTests to run tests showing correctness.
CRCTests can also be used to see how well different polynomials are
able to detect errors. CRCTests will produce 1000000 bitstrings of 
length 10 < n < 1500 and check that they get processed correctly. 

It will then randomly flip bits based on a given probability, which can
be used to represent the noisiness of the channel. 

### CRC
java CRC <bitstring> <polynomial>

e.g. java CRCReverse 10010010010 1011

### CRCReverse
java CRCReverse <bitstring> <polynomial>

e.g. java CRCReverse 10010100110100 1011

### CRCTests
java CRCTests <polynomial> <maxProb>
where the probability of a particular bit being flipped in a message
is 1/maxProb.

