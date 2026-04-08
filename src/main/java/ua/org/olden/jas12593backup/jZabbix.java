/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

/**
 * Based on
 * https://github.com/hengyunabc/zabbix-api/blob/master/src/main/java/io/github/hengyunabc/zabbix/api/DefaultZabbixApi.java
 *
 * @author olden
 */
public class jZabbix {

    public jZabbix(Properties prop) {
        this.prop = prop;
    }

    public void init() throws MalformedURLException, IOException {
        this.auth = null;
        jZabbixRequest request = jZabbixRequestBuilder.newBuilder().
                paramEntry("user", this.prop.getProperty("zabbix.username")).
                paramEntry("password", this.prop.getProperty("zabbix.password")).
                method("user.login").auth(this.auth).
                build();
        call(request, jZabbixAuthReply.class);
        if (this.responseObject != null) {
            this.auth = ((jZabbixAuthReply) responseObject).result;
        }
    }

    public ArrayList<jZabbixInventory> getInventory() throws IOException {
        if (this.auth == null) {
            return null;
        }

        ArrayList<String> host = new ArrayList<>();
        host.add("host");
        ArrayList<String> hardware = new ArrayList<>();
        hardware.add("hardware");

        jZabbixRequest request = jZabbixRequestBuilder.newBuilder().
                paramEntry("output", host).
                paramEntry("selectInventory", hardware).
                method("host.get").auth(this.auth).
                build();
        call(request, jZabbixHostsInventory.class);
        if (this.responseObject != null) {
            this.zabbixHostInventory = ((jZabbixHostsInventory) responseObject).result;
            return zabbixHostInventory;
        }

        return null;
    }

    public void call(jZabbixRequest request, Class aClass) throws IOException {
        this.responseObject = null;
        /*
        System.out.println(request.toString()); 
         */
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            ClassicHttpRequest httpPost = ClassicRequestBuilder.
                    post().
                    setUri(this.prop.getProperty("zabbix.api").trim()).
                    addHeader("Content-Type", "application/json").
                    setEntity(new StringEntity(request.toString(), ContentType.APPLICATION_JSON)).build();
            // System.out.println(new String(httpPost.getEntity().getContent().readAllBytes()));
            httpclient.execute(httpPost, (ClassicHttpResponse response) -> {
                /*
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                 */
                final HttpEntity entity2 = response.getEntity();
                final byte[] data = EntityUtils.toByteArray(entity2);
                /*
                System.out.println(new String(data));
                 */
                this.responseObject = new Gson().fromJson(
                        new String(data),
                        aClass
                );
                return null;
            });

        }

    }

    private final Properties prop;
    private Object responseObject = null;
    private volatile String auth;
    private ArrayList<jZabbixInventory> zabbixHostInventory = null;
}
