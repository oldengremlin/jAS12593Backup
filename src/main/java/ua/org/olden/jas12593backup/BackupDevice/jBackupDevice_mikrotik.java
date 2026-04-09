/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup.BackupDevice;

/**
 *
 * @author olden
 */
public class jBackupDevice_mikrotik extends jBackupDevice_junos {

    @Override
    protected String getConfigCommand() {
        return "/export compact";
    }

}
