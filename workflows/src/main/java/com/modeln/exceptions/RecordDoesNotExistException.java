package com.modeln.exceptions;

public class RecordDoesNotExistException extends RuntimeException{

    public RecordDoesNotExistException() {
        super();
    }

    public RecordDoesNotExistException(String msg){
        super(msg);
    }

    public RecordDoesNotExistException(String msg, Throwable tx){
        super(msg, tx);
    }

}
