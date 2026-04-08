/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup;

import java.io.IOException;

/**
 *
 * @author olden
 */
public class jBackupDevice_iscom extends jBackupDevice_ciscoios implements iConfigBackup {

    @Override
    protected void login() throws IOException {
        telnetReadUntil("ogin:");
        telnetWrite(getConfigDeviceEvent().getUsername());
        telnetReadUntil("assword:");
        telnetWrite(getConfigDeviceEvent().getPassword());
        telnetReadUntil(getPrompt());
        telnetWrite("terminal page-break disable");
        telnetReadUntil(getPrompt());
    }

    @Override
    protected String shRun() throws IOException {
        telnetWrite("show running-config");
        telnetReadUntil(" System current configuration:");
        return telnetReadUntil(getConfigDeviceEvent().getHost().concat(getPrompt()));
    }

}
