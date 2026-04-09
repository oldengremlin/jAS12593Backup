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
public class jBackupDevice_es3510 extends jBackupDevice_ciscoios implements ConfigBackupInterface {
    
    public jBackupDevice_es3510() {
        super();
        this.more = "---More---";
    }
    
    @Override
    protected void login() throws IOException {
        telnetReadUntil("sername: ");
        telnetWrite(getConfigDeviceEvent().getUsername());
        telnetReadUntil("assword: ");
        telnetWrite(getConfigDeviceEvent().getPassword());
        telnetReadUntil(getPrompt());
    }
    
    @Override
    protected String shRun() throws IOException {
        telnetWrite("show running-config");
        telnetReadUntil("building running-config, please wait...");
        StringBuilder retConf = new StringBuilder();
        while (true) {
            String s = telnetReadUntil(getPrompt());
            if (s.matches("(?s)^.*".concat(this.more).concat("$"))) {
                String[] original = s.replaceAll("\\x08+", "\n").split("\n");
                String[] prepared = new String[original.length - 1];
                System.arraycopy(original, 0, prepared, 0, original.length - 1);
                retConf.append(String.join("\n", prepared).replaceAll("(?m)^\s+$", ""));
                telnetWrite(" ");
                telnetReadUntil("\\W");
            } else if (s.matches("(?s)^.*".concat(getConfigDeviceEvent().getHost()).concat("-0").concat(this.prompt).concat("$"))) {
                return retConf.toString();
            } else {
                return retConf.toString().concat(s);
            }
            
        }
    }
    
    @Override
    protected void logout() {
        telnetWrite("exit");
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
    
    @Override
    public String getPrompt() {
        return "(".concat(this.prompt).concat("|").concat(this.more).concat(")");
    }
    
    private final String more;
    
}
