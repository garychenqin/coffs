package cn.edu.bjut.coffs.enums;

/**
 * Created by chenshouqin on 2016-07-07 18:04.
 */
public enum URLTypeEnum {

    FILE(10, "file"),
    JAR(20, "jar");


    private int code;
    private String name;

    private URLTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public  int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
