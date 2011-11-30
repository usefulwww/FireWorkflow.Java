

package org.fireflow.model.io;

@SuppressWarnings("serial")
public class FPDLSerializerException extends Exception{

    /** 
     * Construct a new FPDLSerializerException. 
     */
    public FPDLSerializerException(){
        super();
    }

    /** 
     * Construct a new FPDLSerializerException with the given error message.
     * @param message The error message
     */
    public FPDLSerializerException(String message){
        super(message);
    }

    /** 
     * Construct a new FPDLSerializerException with the given nested error.
     * @param t The nested error
     */

    public FPDLSerializerException(Throwable t){
        super(t);
    }

    /** 
     * Construct a new FPDLSerializerException with the given error message<br>
     * and nested error.        
     * @param message The error message
     * @param t The error
     */
    public FPDLSerializerException(String message, Throwable t){
        super(message, t);
    }

}
