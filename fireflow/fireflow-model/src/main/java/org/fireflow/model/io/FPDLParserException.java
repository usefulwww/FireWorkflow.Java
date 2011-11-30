

package org.fireflow.model.io;

/**
 * 
 * @author chennieyun
 *
 */
@SuppressWarnings("serial")
public class FPDLParserException extends Exception{

    /** 
     * Construct a new FPDLParserException. 
     */
    public FPDLParserException(){
        super();
    }

    /** 
     * Construct a new FPDLParserException with the specified message.
     * @param message The error message
     */
    public FPDLParserException(String message){
        super(message);
    }

    /** 
     * Construct a new FPDLParserException with the specified nested error.
     * @param t The nested error
     */
    public FPDLParserException(Throwable t){
        super(t);
    }

    /** 
     * Construct a new FPDLParserException with the specified error message<br>
     * and nested exception.
     * @param message The error message
     * @param t The nested error
     */
    public FPDLParserException(String message, Throwable t){
        super(message, t);
    }

}
