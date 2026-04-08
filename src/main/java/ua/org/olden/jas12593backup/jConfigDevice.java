/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup;

import java.util.Properties;

/**
 * Клас, який зберігає поточний стан та властивості пристрою в обробці.
 * Проходить "білою ниткою" скрізь всі "зацікавлені" класи. Кінцева мета -
 * отримати текстову конфігурацію в body.
 *
 * @author olden
 */
class jConfigDevice {

    public jConfigDevice() {
        setType();
        setHost();
        setBody();
    }

    public jConfigDevice(String t, String h, Properties p) {
        setType(t);
        setHost(h);
        setBody();
        setProp(p);
    }

    public final void setType() {
        this.type = new String();
    }

    public final void setType(String t) {
        this.type = t;
    }

    public String getType() {
        return this.type;
    }

    public final void setHost() {
        this.host = new String();
    }

    public final void setHost(String h) {
        this.host = h;
    }

    public String getHost() {
        return this.host;
    }

    public final void setBody() {
        this.body = new StringBuilder();
    }

    public void setBody(String b) {
        this.body = new StringBuilder(b);
    }

    public String getBody() {
        return this.body.toString();
    }

    public void addBody(String b) {
        this.body.append(b);
    }

    public final void setProp(Properties p) {
        this.prop = p;
    }

    public Properties getProp() {
        return this.prop;
    }

    public String getUsername() {
        String username = getProp().getProperty(getHost() + ".username");
        if (username == null) {
            username = getProp().getProperty("default.username");
        }
        return username;
    }

    public String getPassword() {
        String password = getProp().getProperty(getHost() + ".password");
        if (password == null) {
            password = getProp().getProperty("default.password");
        }
        return password;
    }

    public String getEnable() {
        String password = getProp().getProperty(getHost() + ".enable");
        if (password == null) {
            password = getProp().getProperty("default.enable");
        }
        return password;
    }

    public String getFTPUsername() {
        String username = getProp().getProperty(getHost() + ".ftpusername");
        if (username == null) {
            username = getProp().getProperty("default.ftpusername");
        }
        return username;
    }

    public String getFTPPassword() {
        String password = getProp().getProperty(getHost() + ".ftppassword");
        if (password == null) {
            password = getProp().getProperty("default.ftppassword");
        }
        return password;
    }

    private String type;
    private String host;
    private StringBuilder body;
    private Properties prop;

}
