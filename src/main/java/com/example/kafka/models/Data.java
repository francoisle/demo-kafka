package com.example.kafka.models;

public class Data {

    private String content;
    private boolean valid;

    public Data() {}

    public Data(String content, boolean valid) {
        this.content = content;
        this.valid = valid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "Message: {" +
                "  content='" + content + '\'' +
                ", valid='" + valid + '\'' +
                '}';
    }
}
