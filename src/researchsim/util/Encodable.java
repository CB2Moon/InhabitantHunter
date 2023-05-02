package researchsim.util;

/**
 * Denotes a class whose state can be encoded and represented as a String.
 *
 * @ass2
 */
public interface Encodable {
    /**
     * Returns the machine-readable encoded representation of this object.
     *
     * @return encoded String representation
     */
    String encode();
}
