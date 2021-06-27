package com.secure.util;

public class Path {
    private String pathEncrypt;
    private String pathDecrypt;
    private int id;
    private String decrypt;

    public Path(String pathEncrypt, String pathDecrypt, String decrypt) {
        this.pathEncrypt = pathEncrypt;
        this.pathDecrypt = pathDecrypt;
        this.decrypt = decrypt;
        this.id = id;
    }

    public String getDecrypt() {
        return decrypt;
    }

    public void setDecrypt(String decrypt) {
        this.decrypt = decrypt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPathEncrypt() {
        return pathEncrypt;
    }

    public void setPathEncrypt(String pathEncrypt) {
        this.pathEncrypt = pathEncrypt;
    }

    public String getPathDecrypt() {
        return pathDecrypt;
    }

    public void setPathDecrypt(String pathDecrypt) {
        this.pathDecrypt = pathDecrypt;
    }
}

