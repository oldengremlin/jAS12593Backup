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
public class jBackupDevice_dlink_3010 extends jBackupDevice_dlink_v4 {

    @Override
    protected String shRun() throws IOException {
        telnetWrite("show config current_config");
        telnetReadUntil("Command: show config current_config");
        String[] original = telnetReadUntil("DES-3010G:4".concat(getPrompt())).split("\n");
        String[] prepared = new String[original.length - 1];
        System.arraycopy(original, 0, prepared, 0, original.length - 1);
        return String.join("\n", prepared);
    }

}
