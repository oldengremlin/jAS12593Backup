/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup.BackupDevice;

import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olden
 */
public class jBackupDevice_aristaeos extends jBackupDevice_junos {

    @Override
    protected String getConfigCommand() {
        return "show running-config";
    }

}
