/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common.exception;

/**
 * 
 * @author sihai
 *
 */
public class HSFException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -857726789855312267L;

	/*
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * 
	 * Constructors.
	 * 
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */
	public HSFException() {
        super();
    }

    public HSFException(String msg) {
        super(msg);
    }

    public HSFException(Throwable cause) {
        super(cause);
    }

    public HSFException(String msg, Throwable cause) {
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
