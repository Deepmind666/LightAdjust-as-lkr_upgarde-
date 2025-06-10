package cn.com.zonesion.lightadjust.bean;

/**
 * IdKeyBean类用来描述用户设备的ID、KEY和使用的服务器的地址SERVER。一个IdKeyBean对象可以看作一个用户的设备对象
 */

public class IdKeyBean {
	 /**
     * 用户设备的ID
     */
    private String id;
    /**
     * 用户设备的KEY
     */
    private String key;
    /**
     * 用户设备的服务器地址
     */
    private String server;

    /**
     * 空参构造器
     */
    public IdKeyBean() {
    }

    /**
     * 全参构造器
     * @param id 外部传入的ID
     * @param key 外部传入的KEY
     * @param server 外部传入的服务器地址SERVER
     */
    public IdKeyBean(String id, String key, String server) {
        this.id = id;
        this.key = key;
        this.server = server;
    }

    /**
     * 该方法用来获取用户设备的ID
     * @return 用户设备的ID
     */
    public String getId() {
        return id;
    }

    /**
     * 该方法用来设置用户设备的ID
     * @param id 外部指定的ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 该方法用来获取用户设备的KEY
     * @return 用户设备的KEY
     */
    public String getKey() {
        return key;
    }

    /**
     * 该方法用来设置用户设备的KEY
     * @param key 外部指定的KEY
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 该方法用来获取用户设备的服务器地址SERVER
     * @return 用户设备的服务器地址
     */
    public String getServer() {
        return server;
    }

    /**
     * 该方法用来设置用户设备的服务器地址
     * @param server 外部指定的服务器地址
     */
    public void setServer(String server) {
        this.server = server;
    }
}
