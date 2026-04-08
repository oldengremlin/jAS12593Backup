/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ua.org.olden.jas12593backup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author olden
 */
public class JAS12593Backup {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        jBackup jBackup = new jBackup(pool);
        jBackup.run();
    }

}
