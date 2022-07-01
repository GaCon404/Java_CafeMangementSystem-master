package CafeMangementSystem.Utils;


import CafeMangementSystem.Entities.Nhanvien;

public final class SessionUser {
    private Nhanvien nhanvien;

    private static SessionUser instance;
    private SessionUser() {};
    public static SessionUser getInstance() {
        if (instance == null) {
            instance = new SessionUser();
            return instance;
        }
        return instance;
    }

    public void cleanSession() {
        nhanvien =null;
    }

    public Nhanvien getNhanvien() {
        return nhanvien;
    }

    public void setNhanvien(Nhanvien nhanvien) {
        this.nhanvien = nhanvien;
    }
}