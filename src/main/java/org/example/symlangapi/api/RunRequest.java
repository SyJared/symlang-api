package org.example.symlangapi.api;

public class RunRequest {

    private String code;
    private String lang = "en";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }
}
