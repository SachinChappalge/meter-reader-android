package com.extentia.imagetotext.api;

public class NetworkState {
    public enum Status{
        RUNNING,
        SUCCESS,
        SUCCESS_800,
        FAILED
    }


    private final Status status;
    private final String msg;

    public static final NetworkState LOADED;
    public static final NetworkState LOADED_800;
    public static final NetworkState LOADING;

    public NetworkState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    static {
        LOADED=new NetworkState(Status.SUCCESS,"Success");
        LOADED_800=new NetworkState(Status.SUCCESS_800,"Success");
        LOADING=new NetworkState(Status.RUNNING,"Running");
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
