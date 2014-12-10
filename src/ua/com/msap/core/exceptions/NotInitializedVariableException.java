package ua.com.msap.core.exceptions;

/**
 * Класс исключения возникающего при попытке изменить не инициализированную 
 * переменную
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class NotInitializedVariableException extends Exception{
    public NotInitializedVariableException(){
        super("Attempt to use an uninitialized variable...");
    };
    public NotInitializedVariableException(String variableName){
        super("Attempt to use an uninitialized variable '"
                + variableName + "'...");
    }
}
