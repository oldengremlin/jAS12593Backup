/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup.BackupDevice;

import ua.org.olden.jas12593backup.ConfigBackupAbstract;
import ua.org.olden.jas12593backup.ConfigBackupInterface;
import ua.org.olden.jas12593backup.jConfigDevice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author olden
 */
public class jBackupDevice_dslam_ies1000 extends ConfigBackupAbstract implements ConfigBackupInterface {

    public jBackupDevice_dslam_ies1000() {
        this.config = new String();
        this.fqdn = new String();
    }

    /**
     * Реалізація метода інтерфейсу iConfigBackup.
     *
     * @param e
     * @return
     */
    @Override
    public String prepare(jConfigDevice e) {
        // https://www.baeldung.com/java-ftp-client
        // https://mkyong.com/java/how-to-convert-inputstream-to-string-in-java/
        //// git clone https://github.com/mkyong/core-java
        //// 

        setConfigDeviceEvent(e);
        this.fqdn = getConfigDeviceEvent().getHost().concat(getDomain());
        String ftpUrl = "ftp://"
                .concat(getConfigDeviceEvent().getFTPUsername())
                .concat(":")
                .concat(getConfigDeviceEvent().getFTPPassword())
                .concat("@")
                .concat(this.fqdn)
                .concat("/init");

        try {
            if (InetAddress.getByName(this.fqdn).isReachable(5000)) {
                // URLConnection urlConnection = new URL(ftpUrl).openConnection();
                URLConnection urlConnection = URI.create(ftpUrl)
                        .toURL().openConnection();
                try (Stream<String> lines = new BufferedReader(
                        new InputStreamReader(
                                urlConnection.getInputStream()))
                        .lines()) {
                    this.config = lines.collect(Collectors.joining("\n"));
                }
            } else {
                System.err.println("Host " + this.fqdn + " is not reachable");
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(jBackupDevice_dslam_ies1000.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(jBackupDevice_dslam_ies1000.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.config;
    }

    public static final int DEFAULT_BUFFER_SIZE = 8192;

    protected String fqdn;
    protected String config;

}
