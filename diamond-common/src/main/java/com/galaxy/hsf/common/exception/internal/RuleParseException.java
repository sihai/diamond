/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common.exception.internal;

public class RuleParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8249618008835652655L;


	/*
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * 
	 * Constructors.
	 * 
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */
	public RuleParseException() {
        super();
    }

    public RuleParseException(String msg) {
        super(msg);
    }

    public RuleParseException(Throwable cause) {
        super(cause);
    }

    public RuleParseException(String msg, Throwable cause) {
        super(msg, cause);
    }


    
    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * <p>
     * Return the exception that is the underlying cause of this exception.
     * </p>
     * 
     * <p>
     * This may be used to find more detail about the cause of the error.
     * </p>
     * 
     * @return the underlying exception, or <code>null</code> if there is not
     *         one.
     */
    public Throwable getUnderlyingException() {
        return super.getCause();
    }


    public String toString() {
        Throwable cause = getUnderlyingException(); 
        if (cause == null || cause == this) {
            return super.toString();
        } else {
            return super.toString() + " [See nested exception: " + cause + "]";
        }
    }
}
