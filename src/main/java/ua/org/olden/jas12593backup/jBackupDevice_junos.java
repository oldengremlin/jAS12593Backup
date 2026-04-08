/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.telnet.TelnetClient;

/**
 *
 * @author olden
 */
public class jBackupDevice_junos extends aConfigBackup implements iConfigBackup {

    public jBackupDevice_junos() {
        this.config_ssh = new java.util.Properties();
        this.config_ssh.put("StrictHostKeyChecking", "no");
        this.config = new ArrayList<>();
    }

    /**
     * Реалізація метода інтерфейсу iConfigBackup.
     *
     * @param e
     * @return
     */
    @Override
    public String prepare(jConfigDevice e) {

        setConfigDeviceEvent(e);
        this.fqdn = getConfigDeviceEvent().getHost() + getDomain();
        try {
            InetAddress inet = InetAddress.getByName(this.fqdn);
            boolean status = inet.isReachable(5000) || isReachable();;
            if (status) {
                try {
                    deviceLogin();
                    // System.out.println(this.fqdn.concat("\t").concat(getConfigCommand()));
                    doGetConfigCommand(getConfigCommand());
                } catch (JSchException | IOException ex) {
                    System.err.println("ERROR WITH HOST " + getConfigDeviceEvent().getHost());
                    Logger.getLogger(jBackupDevice_junos.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (this.channel.isConnected()) {
                        this.channel.disconnect();
                    }
                    if (this.session.isConnected()) {
                        this.session.disconnect();
                    }
                }
            }
        } catch (UnknownHostException ex) {
            System.err.println("Host " + this.fqdn + "does not exists");
        } catch (IOException ex) {
            System.err.println("Error in reaching the Host " + this.fqdn);
        }

        System.out.println(
                getConfigDeviceEvent().getType()
                        .concat("\t")
                        .concat(getConfigDeviceEvent().getHost())
        );

        StringBuilder sb = new StringBuilder();
        for (String s : this.config) {
            sb.append(s).append("\n");
        }

        return sb.toString();
    }

    protected String getConfigCommand() {
        return "show configuration | display omit | no-more";
    }

    protected void deviceLogin() throws JSchException {
        JSch jsch = new JSch();
        this.session = jsch.getSession(
                getConfigDeviceEvent().getUsername(),
                getConfigDeviceEvent().getHost() + getDomain(),
                22
        );
        this.session.setConfig(config_ssh);
        this.session.setPassword(
                getConfigDeviceEvent().getPassword()
        );
        this.session.connect();

    }

    protected void doGetConfigCommand(String command) throws JSchException, IOException {
        this.config.addAll(
                Arrays.asList(
                        this.doCommand(command)
                                .split("\n")
                )
        );
    }

    protected String doCommand(String command) throws JSchException, IOException {
        channel = this.session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        //OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        String text = new String();
        try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
    }

    protected boolean isReachable() throws IOException {
        TelnetClient telnet = new TelnetClient();
        telnet.setDefaultPort(22);
        telnet.setConnectTimeout(5000);
        telnet.connect(this.fqdn);
        boolean status = telnet.isConnected() && telnet.isAvailable();
        telnet.disconnect();
        return status;
    }

    protected final ArrayList<String> config;
    private final Properties config_ssh;
    protected String fqdn;
    protected Session session;
    protected Channel channel;

}
