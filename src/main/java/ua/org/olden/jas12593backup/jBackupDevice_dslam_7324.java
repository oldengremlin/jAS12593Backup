/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author olden
 */
public class jBackupDevice_dslam_7324 extends aConfigBackup implements iConfigBackup {

    public jBackupDevice_dslam_7324() {
        // https://medium.com/bliblidotcom-techblog/java-ftp-integration-using-apache-commons-net-5efb3d300829
        this.ftpClient = new FTPClient();
        this.config = "";
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
        this.fqdn = getConfigDeviceEvent().getHost().concat(getDomain());

        try {
            if (isReachable()) {
                FTPConnect();
                FTPGetConfig();
                FTPDisconnect();
            } else {
                System.err.println("Host " + this.fqdn + " is not reachable");
            }
        } catch (IOException ex) {
            //Logger.getLogger(jBackupDevice_dslam_7324.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(jBackupDevice_dslam_7324.class.getName()).log(Level.SEVERE, this.fqdn, ex);
        } finally {
            return this.config;
        }
    }

    protected void FTPConnect() throws IOException {
        //this.ftpClient.connect(this.fqdn);
        this.ftpClient.connect(InetAddress.getByName(this.fqdn));
        this.ftpClient.login(
                getConfigDeviceEvent().getFTPUsername(),
                getConfigDeviceEvent().getFTPPassword()
        );
    }

    protected void FTPDisconnect() throws IOException {
        ftpClient.disconnect();
    }

    protected void FTPGetConfig() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.ftpClient.retrieveFile("config-0", byteArrayOutputStream);
        this.config = byteArrayOutputStream.toString();
    }

    protected boolean isReachable() throws IOException {
        InetAddress inet = InetAddress.getByName(this.fqdn);
        boolean status = inet.isReachable(5000); //Timeout = 5000 milli seconds
        return status;
    }

    protected final FTPClient ftpClient;
    protected String fqdn;
    protected String config;
}
