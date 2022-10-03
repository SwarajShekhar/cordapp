package com.modeln.exceptions;

public class InvalidStateException extends RuntimeException{

    public InvalidStateException() {
        super();
    }

    public InvalidStateException(String msg){
        super(msg);
    }

    public InvalidStateException(String msg, Throwable tx){
        super(msg, tx);
    }

}
