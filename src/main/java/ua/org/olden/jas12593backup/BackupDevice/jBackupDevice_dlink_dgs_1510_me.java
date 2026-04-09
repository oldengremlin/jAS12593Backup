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
public class jBackupDevice_dlink_dgs_1510_me extends jBackupDevice_ciscoios implements ConfigBackupInterface {

    @Override
    protected void login() throws IOException {
        telnetReadUntil("ame:");
        telnetWrite(getConfigDeviceEvent().getUsername());
        telnetReadUntil("ord:");
        telnetWrite(getConfigDeviceEvent().getPassword());
        telnetReadUntil(getPrompt());
        telnetWrite("disable clipaging");
        telnetReadUntil(getPrompt());
    }

    @Override
    protected String shRun() throws IOException {
        telnetWrite("show config current_config");
        telnetReadUntil("Command: show config current_config");
        String[] original = telnetReadUntil(getConfigDeviceEvent().getHost().concat(":admin").concat(getPrompt())).split("\n");
        String[] prepared = new String[original.length - 1];
        System.arraycopy(original, 0, prepared, 0, original.length - 1);
        return String.join("\n", prepared);
    }

}
