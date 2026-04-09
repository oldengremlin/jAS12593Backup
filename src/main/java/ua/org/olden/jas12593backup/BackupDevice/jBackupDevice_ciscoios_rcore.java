/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup.BackupDevice;

import ua.org.olden.jas12593backup.ConfigBackupInterface;

import java.io.IOException;

/**
 *
 * @author olden
 */
public class jBackupDevice_ciscoios_rcore extends jBackupDevice_ciscoios implements ConfigBackupInterface {

    @Override
    protected void login() throws IOException {
        telnetReadUntil("assword:");
        telnetWrite(getConfigDeviceEvent().getPassword());
        telnetReadUntil(">");
        telnetWrite("enable");
        telnetReadUntil("assword:");
        telnetWrite(getConfigDeviceEvent().getProp().getProperty(getConfigDeviceEvent().getHost().concat(".enable")));
        telnetReadUntil(getPrompt());
        telnetWrite("terminal length 0");
        telnetReadUntil(getPrompt());
    }

    @Override
    protected String shRun() throws IOException {
        telnetWrite("show running-config");
        telnetReadUntil(" bytes");
        String[] original = telnetReadUntil(getPrompt()).split("\n");
        String[] prepared = new String[original.length - 1];
        System.arraycopy(original, 0, prepared, 0, original.length - 1);
        return String.join("\n", prepared);
    }

}
