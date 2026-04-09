/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ua.org.olden.jas12593backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author olden
 */
public class JAS12593Backup {

    public static final Logger LOGGER = LoggerFactory.getLogger(JAS12593Backup.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOGGER.debug("Execute JAS12593Backup");
        ExecutorService pool = Executors.newCachedThreadPool();
        jBackup jBackup = new jBackup(pool);
        jBackup.run();
    }

}
