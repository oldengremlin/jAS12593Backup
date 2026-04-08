/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Клас, який створює чергу з реалізації інтерфейсу iConfigBackup, що мають на
 * меті отримання конфігурації з пристрою.
 *
 * @author olden
 */
public class jConfigBackup implements Callable<jConfigDevice> {

    public jConfigBackup() {
        setConfigDeviceEvent(new jConfigDevice());
    }

    public jConfigBackup(jConfigDevice e) {
        setConfigDeviceEvent(e);
    }

    @Override
    public jConfigDevice call() throws Exception {
        prepareClass();
        return this.event;
    }

    private void prepareClass() {
        String className = "ua.org.olden.jas12593backup.jBackupDevice_" + getConfigDeviceEvent().getType();
        String hostName = getConfigDeviceEvent().getHost();
        try {
            iConfigBackup icb = (iConfigBackup) Class.forName(className)
                    .getDeclaredConstructor()
                    .newInstance();
            /*
            System.out.println(className + "\t" + hostName);
             */
            getConfigDeviceEvent()
                    .setBody(
                            icb.prepare(
                                    getConfigDeviceEvent()
                            )
                    );

            if (getConfigDeviceEvent().getBody().length() > 0) {
                try (BufferedWriter bw = makeBackupStructure()) {
                    bw.write(getConfigDeviceEvent().getBody());
                } catch (IOException ex) {
                    Logger.getLogger(jConfigBackup.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | SecurityException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            System.err.println(className + " for host " + hostName + " not found");
        }
    }

    public final BufferedWriter makeBackupStructure() throws IOException {

        String backupDirectory = getConfigDeviceEvent().getProp()
                .getProperty("backup.dir", "./jBackup");
        if (!backupDirectory.matches("/$")) {
            backupDirectory = backupDirectory.concat("/");
        }
        File dir = new File(backupDirectory);
        if (!dir.exists()) {
            dir.mkdir();
        }

        String backupFilename = backupDirectory
                .concat(getConfigDeviceEvent().getHost())
                .concat(".backup");
        return new BufferedWriter(
                new FileWriter(
                        new File(backupFilename).getAbsoluteFile()
                )
        );
    }

    public final void setConfigDeviceEvent(jConfigDevice e) {
        this.event = e;
    }

    public final jConfigDevice getConfigDeviceEvent() {
        return this.event;
    }

    private jConfigDevice event;
}
