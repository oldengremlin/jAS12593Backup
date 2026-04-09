/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup;

import ua.org.olden.jas12593backup.Zabbix.jZabbixInventory;
import ua.org.olden.jas12593backup.Zabbix.jZabbix;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Клас, що реалізує головну гілку програми.
 *
 * @author olden
 */
public class jBackup {

    public jBackup(ExecutorService p) {
        this.pool = p;
        this.prop = new Properties();
        this.jZabbix = new jZabbix(this.prop);

        try (InputStream input = findPropertiesStream()) {
            this.prop.load(input);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(jBackup.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(jBackup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Головна частина програми
     */
    public void run() {
        ArrayList<Future<jConfigDevice>> results = new ArrayList<>();
        ExecutorService poolLocal = Executors.newCachedThreadPool();
        try {
            jZabbix.init();

            for (jZabbixInventory host : jZabbix.getInventory()) {
                if (waitPool(poolLocal, results, false)) {
                    results = new ArrayList<>();
                    poolLocal = Executors.newCachedThreadPool();
                    System.out.println("== NEXT POOL ==============");
                }
                setHost(host.host);
                setInventory(host.inventory.hardware);
                setCurrentInventoryType();
                if (getType().length() > 0) {
                    if (getProp().getProperty(getHost().concat(".backup")) != null
                            && getProp().getProperty(getHost().concat(".backup")).trim().equalsIgnoreCase("ignore")) {
                        continue;
                    }
                    System.out.print(results.size() + ": ");
                    System.out.println(getHost());

                    if (getProp().getProperty("queue.debug") != null
                            && getProp().getProperty("queue.debug", "off").trim().equalsIgnoreCase("on")
                            && getType().trim().equalsIgnoreCase("dummy")) {
                        continue;
                    }

                    results.add(
                            poolLocal.submit(
                                    new jConfigBackup(
                                            new jConfigDevice(
                                                    getType(),
                                                    getHost(),
                                                    getProp()
                                            )
                                    )
                            )
                    );

                }
            }
            System.out.println("-- finish -----------------");
            waitPool(poolLocal, results, true);

        } catch (MalformedURLException ex) {
            Logger.getLogger(jBackup.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InterruptedException | ExecutionException ex) {
            Logger.getLogger(jBackup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Отримуємо результати. get() повертає екземпляр класу jConfigDevice. Тут
     * ми з ним нічого не робимо, тому просто викликаємо get(), не зберігаючи
     * навіть локальних копій екземпляра класа.
     */
    private boolean waitPool(ExecutorService p, ArrayList<Future<jConfigDevice>> r, boolean finish) throws InterruptedException, ExecutionException {
        if (finish || r.size() >= Integer.parseInt(getProp().getProperty("queue.max_size", "10").trim())) {
            System.out.println("-- get pool ---------------");
            for (Future<jConfigDevice> result : r) {
                try {
                    result.get(
                            Long.parseLong(getProp().getProperty("queue.timeout_sec", "30").trim()),
                            TimeUnit.SECONDS
                    ).getBody();
                } catch (TimeoutException ex) {
                }
            }
            p.shutdown();
            int lps = ((ThreadPoolExecutor) this.pool).getLargestPoolSize();
            System.out.println("Largest Pool Size " + lps);
            if (finish) {
                System.out.println("== THE END ================");
                System.exit(0);
            }
            return true;
        }
        return false;
    }

    /**
     * Аналізуємо значення Inventory і на його підставі визначаємо тип
     * обладнання. Цей тип буде використовуватися в імені реалізації класу, що
     * буде збирати конфігурацію з конкретного пристрою.
     */
    private void setCurrentInventoryType() {
        setType(new String());
        if (getInventory().length() > 0) {
            // setInventory(getInventory().replaceAll("[\\s|\\t|\\r\\n|\\r|\\n]+", " ").trim());
/*
            if (getInventory().matches("^(D-Link\s+)?DES-3200-[0-9]+\s+.*") && getHost().equalsIgnoreCase("ssks-4")) {
                System.err.println(getHost() + " v4");
                setType("dlink_v4");
            } else {
                setType("dummy");
                return;
            }
             */
            boolean noop;
            /*
                https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
            
                Pattern.DOTALL or (?s) tells Java to allow the dot to match 
                    newline characters, too.
                Pattern.MULTILINE or (?m) tells Java to accept the anchors ^ 
                    and $ to match at the start and end of each line (otherwise they
                    only match at the start/end of the entire string).

                Equivalent expressions:

                Pattern.
                    compile("^Cisco\\s+(IOS|Internetwork Operating System).*", Pattern.DOTALL).
                    matcher(getInventory()).
                    find()

                getInventory().
                    matches("(?s)^Cisco\\s+(IOS|Internetwork Operating System).*")
             */

            if (getInventory().matches("^(D-Link\s+)?DES-3200-[0-9]+/C1\s+.*")) {
                setType("dlink_v4");
            } else if (getInventory().matches("^(D-Link\s+)?DES-3200-[0-9]+\s+.*") && getHost().equalsIgnoreCase("ssks-4")) {
                setType("dlink_v4");
            } else if (getInventory().matches("^(D-Link\s+)?DES-3200-[0-9]+\s+.*")) {
                setType("dlink_v1");
            } else if (getInventory().matches("^(D-Link\s+)?DES-3526\s+.*")) {
                setType("dlink_3526");
            } else if (getInventory().matches("^(D-Link\s+)?DES-3010.\s+.*")) {
                setType("dlink_3010");
            } else if (getInventory().matches("^(D-Link\s+)?DGS-1210-[0-9]+P/ME\s+.*")) {
                setType("dlink_dgs_1210_me");
            } else if (getInventory().matches("^(D-Link\s+)?DGS-1510-[0-9]+L/ME\s+.*")) {
                setType("dlink_dgs_1510_me");
            } else if (getInventory().matches("^(D-Link\s+)?DGS-1510-[0-9]+\s+.*")) {
                setType("dlink_dgs");
            } else if (getInventory().matches("^RouterOS\s+.*")) {
                setType("mikrotik");
//            } else if (getInventory().matches("^CSS.*SwOS\s+.*")) {
//                setType("mikrotik_swos");
            } else if (getInventory().matches("^Juniper\s+.*([Ee][Xx]|[Mm][Xx])[0-9]+.*")) {
                setType("junos");
            } else if (getInventory().matches("^.*\s+[Ee][Ss][0-9]+$")) {
                setType("es3510");
            } else if (getHost().matches("^rcore[12]") && getInventory().matches("(?s)^Cisco\\s+(IOS|Internetwork Operating System).*")) {
                setType("ciscoios_rcore");
            } else if (getInventory().matches("(?s)^Cisco\\s+(IOS|Internetwork Operating System).*")) {
                setType("ciscoios");
            } else if (getInventory().matches("^Arista\s+Networks\s+EOS\s+version 4\\.14\\.6M.*")) {
                setType("aristaeos4146M");
            } else if (getInventory().matches("^Arista\s+Networks\s+EOS.*")) {
                setType("aristaeos");
            } else if (getInventory().matches("^ExtremeXOS.*")) {
                setType("extremexos");
            } else if (getInventory().matches("^ATI\s+(AT-)?8000S$")) {
                setType("at8000");
            } else if (getInventory().matches("^ROS$")) {
                setType("iscom");
            } else if (getInventory().matches("^7324 RU.*")) {
                setType("dslam_7324");
            } else if (getInventory().matches("(?s)^ZyXEL IES-1000.*")) {
                setType("dslam_ies1000");
            } else {
                System.err.println(getHost().concat(" :: ").concat(getInventory()));
            }

        }
    }

    /**
     * Встановлює властивість типу обладнання для поточного пристрою в обробці.
     *
     * @param s
     */
    public void setType(String s) {
        this.cType = s;
    }

    /**
     * Повертає властивість типу обладнання для поточного пристрою в обробці.
     *
     * @return
     */
    public String getType() {
        return this.cType;
    }

    /**
     * Встановлює властивість типу імені хоста для поточного пристрою в обробці.
     *
     * @param s
     */
    public void setHost(String s) {
        this.cHost = s;
    }

    /**
     * Повертає властивість типу імені хоста для поточного пристрою в обробці.
     *
     * @return
     */
    public String getHost() {
        return this.cHost;
    }

    /**
     * Встановлює властивість Inventory хоста для поточного пристрою в обробці.
     *
     * @param s
     */
    public void setInventory(String s) {
        this.cInventory = s;
    }

    /**
     * Повертає властивість Inventory хоста для поточного пристрою в обробці.
     *
     * @return
     */
    public String getInventory() {
        return this.cInventory;
    }

    /**
     * Повертає клас з властивостями програми.
     *
     * @return
     */
    public Properties getProp() {
        return this.prop;
    }

    private InputStream findPropertiesStream() throws FileNotFoundException {
        final String filename = "JAS12593Backup.properties";
        File f;

        f = new File(filename);
        if (f.isFile()) {
            return new FileInputStream(f);
        }

        f = new File(System.getProperty("user.home"), ".config/jAS12593Backup/" + filename);
        if (f.isFile()) {
            return new FileInputStream(f);
        }

        f = new File("/etc/jAS12593Backup/" + filename);
        if (f.isFile()) {
            return new FileInputStream(f);
        }

        throw new FileNotFoundException(
                filename + " not found in: ./, ~/.config/jAS12593Backup/, /etc/jAS12593Backup/"
        );
    }

    private String cHost;
    private String cInventory;
    private String cType;
    private Properties prop;
    private jZabbix jZabbix;
    private ExecutorService pool;
}
