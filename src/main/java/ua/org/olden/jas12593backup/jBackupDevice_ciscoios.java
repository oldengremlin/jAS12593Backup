/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import org.apache.commons.net.telnet.TelnetClient;

/**
 *
 * @author olden
 */
public class jBackupDevice_ciscoios extends aConfigBackup implements iConfigBackup {

    public jBackupDevice_ciscoios() {
        this.telnet = new TelnetClient();
        this.prompt = "#";
        this.config = "";
    }

    /**
     * Реалізація метода інтерфейсу iConfigBackup.
     *
     * @param e
     * @return
     */
    @Override
    public String prepare(jConfigDevice e) {

        setConfigDeviceEvent(e);

        /*
        System.out.println(
                getConfigDeviceEvent().getType()
                        .concat("\t")
                        .concat(getConfigDeviceEvent().getHost())
                        .concat("\t[ ")
                        .concat(getConfigDeviceEvent().getUsername())
                        .concat(" : ")
                        .concat(getConfigDeviceEvent().getPassword())
                        .concat(" ]")
        );
         */
        this.fqdn = getConfigDeviceEvent().getHost().concat(getDomain());
        try {

            if (isReachable()) {
                connect();
                login();

                this.config = shRun();

                StringBuilder sb = new StringBuilder();
                for (String s : this.config.split("\n")) {
                    s = s.replaceAll("\\r", "");
                    if (!s.endsWith(
                            getConfigDeviceEvent().getHost()
                                    .concat(getPrompt()))) {
                        sb.append(s).append("\n");
                    }
                }
                this.config = sb.toString();

                logout();
                disconnect();

                System.out.println(
                        getConfigDeviceEvent().getType()
                                .concat("\t")
                                .concat(getConfigDeviceEvent().getHost())
                );

            }

        } catch (UnknownHostException ex) {
            System.err.println("Host " + this.fqdn + "does not exists");
        } catch (IOException ex) {
            System.err.println("Error in reaching the Host " + this.fqdn);
        }

        return this.config;
    }

    protected boolean isReachable() throws IOException {
        this.telnet.setConnectTimeout(5000);
        this.telnet.connect(this.fqdn);
        boolean status = this.telnet.isConnected() && this.telnet.isAvailable();
        this.telnet.disconnect();
        return status;
    }

    protected void connect() throws IOException {
        this.telnet.setConnectTimeout(5000);
        this.telnet.connect(this.fqdn);
        this.in = this.telnet.getInputStream();
        this.out = new PrintStream(this.telnet.getOutputStream());

    }

    protected void disconnect() throws IOException {
        if (this.telnet.isConnected()) {
            this.telnet.disconnect();
        }
    }

    protected void login() throws IOException {
        telnetReadUntil("sername:");
        telnetWrite(getConfigDeviceEvent().getUsername());
        telnetReadUntil("assword:");
        telnetWrite(getConfigDeviceEvent().getPassword());
        telnetReadUntil(getPrompt());
        telnetWrite("terminal length 0");
        telnetReadUntil(getPrompt());
    }

    protected String shRun() throws IOException {
        telnetWrite("show running-config");
        telnetReadUntil(" bytes");
        return telnetReadUntil(getConfigDeviceEvent().getHost().concat(getPrompt()));
    }

    protected void logout() {
        telnetWrite("logout");
    }

    public String telnetReadUntil(String pattern) throws IOException {
        char lastChar = pattern.charAt(pattern.length() - 1);
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        char ch = (char) this.in.read();
        while (true) {
            // System.out.print(ch);
            sb.append(ch);
            if (ch == lastChar) {
                if (sb.toString().endsWith(pattern)) {
                    return sb.toString();
                }
            }
            ch = (char) this.in.read();
        }
    }

    public void telnetWrite(String value) {
        this.out.println(value);
        this.out.flush();
        // System.out.println(value);
    }

    public String getPrompt() {
        return this.prompt;
    }

    protected String fqdn;
    protected String config;
    protected final TelnetClient telnet;
    protected InputStream in;
    protected PrintStream out;
    protected String prompt;

}
