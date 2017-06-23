package onenet.edp;

/**
 * 固件信息
 * Created by yonghua on 2017/6/23.
 */
public class SoftInfo {
    private String name;
    private String version;
    private String url;
    private String md5;
    private int nameVersionlength;
    public static final int MD5_SIZE = 32;

    /**
     * soft info init
     * @param name 固件名称
     * @param version   固件版本
     * @throws IllegalArgumentException if name or version invalid
     */
    public SoftInfo(String name, String version) throws IllegalArgumentException {
        if (name == null || version == null || name.length() > Short.MAX_VALUE || version.length() > Short.MAX_VALUE
                || name.length() == 0 || version.length() == 0) {
            throw new IllegalArgumentException("name or version invalid");
        }
        this.name = name;
        this.version = version;
        nameVersionlength = name.length() + version.length();
    }

    /**
     * completely soft info init
     * @param name  固件名称
     * @param version   固件版本
     * @param url   下载地址
     * @param md5   MD5
     * @throws IllegalArgumentException if arguments invalid
     */
    public SoftInfo(String name, String version, String url, String md5) throws IllegalArgumentException {
        if (name == null || version == null || name.length() > Short.MAX_VALUE || version.length() > Short.MAX_VALUE
                || name.length() == 0 || version.length() == 0) {
            throw new IllegalArgumentException("name or version invalid");
        }
        this.name = name;
        this.version = version;
        nameVersionlength = name.length() + version.length();

        if (url == null || md5 == null || url.length() > Short.MAX_VALUE || url.length() == 0 || md5.length() != MD5_SIZE) {
            throw new IllegalArgumentException("url or md5 invalid");
        }
        this.url = url;
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public int getNameVersionlength() {
        return nameVersionlength;
    }

    public String getUrl() {
        return url;
    }

    public String getMd5() {
        return md5;
    }
}
