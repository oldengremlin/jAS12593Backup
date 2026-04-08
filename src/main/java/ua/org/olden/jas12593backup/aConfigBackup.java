/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup;

/**
 * Абстрактний клас для інтерфейсу iConfigBackup, який реалізує деякі допоміжні
 * функції, що можуть бути задіяні у всіх реалізаціях інтерфейсу. При цьому клас
 * не реалізує метод prepare(), який об'явлено в інтерфейсі. Цей метод має
 * реалізувати клас під конкретний тип пристрою.
 *
 * @author olden
 */
abstract public class aConfigBackup implements iConfigBackup {

    /**
     * Отримуємо домен для імені пристрою. Наприклад, якщо прийстрій зветься
     * test то спочатку шукаємо у Property запис test.domain, якщо такого немає
     * то беремо значення з default.domain, якщо і такого немає то повертаємо
     * просто пусту строку.
     *
     * @return
     */
    public String getDomain() {

        String domain = getConfigDeviceEvent()
                .getProp().getProperty(
                        getConfigDeviceEvent().getHost() + ".domain"
                );
        if (domain == null) {
            domain = getConfigDeviceEvent()
                    .getProp().getProperty(
                            "default.domain"
                    );
            if (domain == null) {
                domain = "";
            }
        }
        if (domain.length() > 0) {
            domain = "." + domain;
        }
        return domain;
    }

    /**
     * Встановлюємо значення для екземпляру класа jConfigDevice, який є
     * "зліпком" поточного стану пристрою і зберігає всі його властивості, що
     * нас цікавлять в процесі роботи.
     *
     * @param e
     */
    public final void setConfigDeviceEvent(jConfigDevice e) {
        this.event = e;
    }

    /**
     * Повертає значення для екземпляру класа jConfigDevice, який є "зліпком"
     * поточного стану пристрою і зберігає всі його властивості, що нас
     * цікавлять в процесі роботи.
     *
     * @return
     */
    public final jConfigDevice getConfigDeviceEvent() {
        return this.event;
    }

    private jConfigDevice event;

}
