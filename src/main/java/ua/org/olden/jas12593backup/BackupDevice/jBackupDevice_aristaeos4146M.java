/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup.BackupDevice;

import java.io.IOException;

/**
 *
 * @author olden
 */
public class jBackupDevice_aristaeos4146M extends jBackupDevice_ciscoios {

    @Override
    protected void login() throws IOException {
        telnetReadUntil("sername:");
        telnetWrite(getConfigDeviceEvent().getUsername());
        telnetReadUntil("assword:");
        telnetWrite(getConfigDeviceEvent().getPassword());
        telnetReadUntil(">");
        telnetWrite("enable");
        telnetReadUntil("assword:");
        telnetWrite(getConfigDeviceEvent().getEnable());
        telnetReadUntil(getPrompt());
        telnetWrite("terminal length 0");
        telnetReadUntil(getPrompt());
    }

    @Override
    protected String shRun() throws IOException {
        telnetWrite("show running-config");
        telnetReadUntil("show running-config");
        return telnetReadUntil(getConfigDeviceEvent().getHost().concat(getPrompt()));
    }

}
