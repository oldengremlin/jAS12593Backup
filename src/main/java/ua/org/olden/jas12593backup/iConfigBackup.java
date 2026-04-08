/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ua.org.olden.jas12593backup;

/**
 *
 * @author olden
 */
public interface iConfigBackup {

    /**
     * Основний метод, який викликається в черзі, для реалізації класа, щоб
     * отримати поточну конфігурацію пристрою та/або виконати інші дії.
     *
     * @param e
     * @return
     */
    public String prepare(jConfigDevice e);

}
