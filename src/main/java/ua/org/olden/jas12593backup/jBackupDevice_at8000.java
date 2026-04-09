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
public class jBackupDevice_at8000 extends jBackupDevice_ciscoios implements ConfigBackupInterface {

    public jBackupDevice_at8000() {
        super();
        this.more = "More: <space>,  Quit: q, One line: <return> ";
    }

    @Override
    protected void login() throws IOException {
        telnetReadUntil("ame:");
        telnetWrite(getConfigDeviceEvent().getUsername());
        telnetReadUntil("assword:");
        telnetWrite(getConfigDeviceEvent().getPassword());
        telnetReadUntil(getPrompt());
    }

    @Override
    protected String shRun() throws IOException {
        telnetWrite("show running-config");
        telnetReadUntil(" show running-config");
        StringBuilder retConf = new StringBuilder();
        while (true) {
            String s = telnetReadUntil(getPrompt());
            if (s.matches("(?s)^.*".concat(this.more).concat("$"))) {
                String[] original = s.split("\n");
                String[] prepared = new String[original.length - 1];
                System.arraycopy(original, 0, prepared, 0, original.length - 1);
                retConf.append("\n");
                retConf.append(String.join("\n", prepared).replaceAll("(?m)^\s+$", ""));
                retConf.append("\n");
                telnetWrite(" ");
            } else if (s.matches("(?s)^.*".concat(getConfigDeviceEvent().getHost()).concat(this.prompt).concat("$"))) {
                retConf.append(s);
                String[] original = retConf.toString().split("\n");
                String[] prepared = new String[original.length - 1];
                System.arraycopy(original, 0, prepared, 0, original.length - 1);
                return String.join("\n", prepared);
            } else {
                return retConf.append("\n").toString().concat(s);
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
