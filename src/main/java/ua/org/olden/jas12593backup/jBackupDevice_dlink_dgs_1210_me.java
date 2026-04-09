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
public class jBackupDevice_dlink_dgs_1210_me extends jBackupDevice_dlink_v1 implements ConfigBackupInterface {
    
    @Override
    protected String shRun() throws IOException {
        telnetWrite("show config current_config");
        telnetReadUntil("Command: show config current_config");
        String[] original = telnetReadUntil(":5".concat(getPrompt()).concat(" ")).split("\n");
        String[] prepared = new String[original.length - 1];
        System.arraycopy(original, 0, prepared, 0, original.length - 1);
        return String.join("\n", prepared);
    }
    
}
