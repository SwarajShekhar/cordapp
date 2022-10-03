package com.modeln.exceptions;

public class RecordAlreadyExistsException extends RuntimeException{

    public RecordAlreadyExistsException() {
        super();
    }

    public RecordAlreadyExistsException(String msg){
        super(msg);
    }

    public RecordAlreadyExistsException(String msg, Throwable tx){
        super(msg, tx);
    }

}
