package com.axreng.backend.model;

public enum Status {

    ACTIVE("active"),
    DONE("done");
 
    private String status;
    
    Status(String status) {
        this.status = status;
    }
    
    public String getValue() {
        return status;
    }
}
