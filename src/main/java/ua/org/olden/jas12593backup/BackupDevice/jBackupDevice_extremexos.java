/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup.BackupDevice;

import java.io.IOException;
import java.net.InetAddress;

/**
 *
 * @author olden
 */
public class jBackupDevice_extremexos extends jBackupDevice_ciscoios {

    public jBackupDevice_extremexos() {
        super();
        this.prompt = " # ";
    }

    @Override
    protected void login() throws IOException {
        telnetReadUntil("ogin:");
        telnetWrite(getConfigDeviceEvent().getUsername());
        telnetReadUntil("assword:");
        telnetWrite(getConfigDeviceEvent().getPassword());
        telnetReadUntil(getPrompt());
        telnetWrite("disable clipaging");
        telnetReadUntil(getPrompt());
    }

    @Override
    protected String shRun() throws IOException {
        telnetWrite("show configuration");
        telnetReadUntil("show configuration");
        String[] original = telnetReadUntil(getPrompt()).split("\n");
        String[] prepared = new String[original.length - 1];
        System.arraycopy(original, 0, prepared, 0, original.length - 1);
        return String.join("\n", prepared);
    }

    @Override
    protected boolean isReachable() throws IOException {
        InetAddress inet = InetAddress.getByName(this.fqdn);
        this.telnet.setDefaultPort(23);
        this.telnet.setConnectTimeout(5000);
        this.telnet.connect(this.fqdn);
        boolean status
                = inet.isReachable(5000)
                || (this.telnet.isConnected() && this.telnet.isAvailable());
        this.telnet.disconnect();
        return status;
    }

}
