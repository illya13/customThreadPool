package com.github.illya13.customThreadPool;

public class Request {
    private long id;
    private long executionTime;

    public Request(long id, long executionTime) {
        this.id = id;
        this.executionTime = executionTime;
    }

    public long getId() {
        return id;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", executionTime=" + executionTime +
                '}';
    }
}
