/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup.BackupDevice;

import ua.org.olden.jas12593backup.ConfigBackupAbstract;
import ua.org.olden.jas12593backup.ConfigBackupInterface;
import ua.org.olden.jas12593backup.jConfigDevice;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import ua.org.olden.jas12593backup.JAS12593Backup;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

/**
 *
 * @author olden
 */
public class jBackupDevice_mikrotik_old extends ConfigBackupAbstract implements ConfigBackupInterface {

    public jBackupDevice_mikrotik_old() {
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
            boolean status = inet.isReachable(5000); //Timeout = 5000 milli seconds
            if (status) {
                try {
                    deviceLogin();
                    doGetConfigCommand("/export compact");
                } catch (JSchException | IOException ex) {
                    JAS12593Backup.LOGGER.error("ERROR WITH HOST " + getConfigDeviceEvent().getHost(), ex);
                } finally {
                    this.channel.disconnect();
                    this.session.disconnect();
                }
            } else {
                JAS12593Backup.LOGGER.warn("Host {} is not reachable", this.fqdn);
            }
        } catch (UnknownHostException ex) {
            JAS12593Backup.LOGGER.warn("Host {} does not exist", this.fqdn);
        } catch (IOException ex) {
            JAS12593Backup.LOGGER.error("Error reaching host {}", this.fqdn);
        }

        JAS12593Backup.LOGGER.info("{}	{}", getConfigDeviceEvent().getType(), getConfigDeviceEvent().getHost());

        StringBuilder sb = new StringBuilder();
        for (String s : this.config) {
            sb.append(s).append("\n");
        }

        return sb.toString();
    }

    private void deviceLogin() throws JSchException {
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

    private void doGetConfigCommand(String command) throws JSchException, IOException {
        this.config.addAll(
                Arrays.asList(
                        this.doCommand(command)
                                .split("\n")
                )
        );
    }

    private String doCommand(String command) throws JSchException, IOException {
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

    private final ArrayList<String> config;
    private final Properties config_ssh;
    private String fqdn;
    private Session session;
    private Channel channel;

}
