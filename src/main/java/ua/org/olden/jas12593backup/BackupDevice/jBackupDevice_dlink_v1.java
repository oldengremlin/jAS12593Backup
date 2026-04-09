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
public class jBackupDevice_dlink_v1 extends jBackupDevice_ciscoios implements ConfigBackupInterface {

    @Override
    protected void login() throws IOException {
        telnetReadUntil("ame:");
        telnetWrite(getConfigDeviceEvent().getUsername());
        telnetReadUntil("ord:");
        telnetWrite(getConfigDeviceEvent().getPassword());
        telnetReadUntil(getPrompt());
        telnetWrite("enable admin");
        telnetReadUntil("ord:");
        telnetWrite(getConfigDeviceEvent().getEnable());
        telnetReadUntil(getPrompt());
        telnetWrite("disable clipaging");
        telnetReadUntil(getPrompt());
    }

    @Override
    protected String shRun() throws IOException {
        telnetWrite("show config current_config");
        telnetReadUntil("Command: show config current_config");
        // String[] original = telnetReadUntil(getConfigDeviceEvent().getHost().concat(":([0-9]+|admin)").concat(getPrompt())).split("\n");
        String[] original = telnetReadUntil(getConfigDeviceEvent().getHost().concat(":[^".concat(getPrompt()).concat("]+")).concat(getPrompt())).split("\n");
        String[] prepared = new String[original.length - 1];
        System.arraycopy(original, 0, prepared, 0, original.length - 1);
        return String.join("\n", prepared);
    }

    @Override
    public String telnetReadUntil(String pattern) throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = (char) this.in.read();
        while (true) {
            sb.append(ch);
            if (sb.toString().matches("(?s)^.*".concat(pattern.concat("$")))) {
                return sb.toString();
            }
            ch = (char) this.in.read();
        }
    }

}
