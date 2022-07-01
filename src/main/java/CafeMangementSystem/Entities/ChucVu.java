package CafeMangementSystem.Entities;

public enum ChucVu {
    ChuQuan("ChuQuan"), NVBanHang("NVBanHang");

    private String name;

    ChucVu(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ChucVu fromName(String name) {
        switch (name) {
            case "ChuQuan":
                return ChucVu.ChuQuan;

            case "NVBanHang":
                return ChucVu.NVBanHang;

            default:
                throw new IllegalArgumentException("ShortName [" + name
                        + "] not supported.");
        }
    }

}
