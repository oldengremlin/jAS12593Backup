/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup.Zabbix;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author olden
 */
public class jZabbixRequestBuilder {

    private static final AtomicInteger nextId = new AtomicInteger(1);

    private final jZabbixRequest request = new jZabbixRequest();

    private jZabbixRequestBuilder() {

    }

    static public jZabbixRequestBuilder newBuilder() {
        return new jZabbixRequestBuilder();
    }

    public jZabbixRequest build() {
        if (request.getId() == null) {
            request.setId(nextId.getAndIncrement());
        }
        return request;
    }

    public jZabbixRequestBuilder version(String version) {
        request.setJsonrpc(version);
        return this;
    }

    public jZabbixRequestBuilder paramEntry(String key, Object value) {
        request.putParam(key, value);
        return this;
    }

    public jZabbixRequestBuilder setParamEntry(String key, Object value) {
        Map<String, Object> p = new HashMap<>();
        p.put(key, value);
        request.setParams(p);
        return this;
    }

    /**
     * Do not necessary to call this method.If don not set id, ZabbixApi will
     * auto set request auth..
     *
     * @param auth
     * @return
     */
    public jZabbixRequestBuilder auth(String auth) {
        request.setAuth(auth);
        return this;
    }

    public jZabbixRequestBuilder method(String method) {
        request.setMethod(method);
        return this;
    }

    /**
     * Do not necessary to call this method.If don not set id,
     * jZabbixRequestBuilder will auto generate.
     *
     * @param id
     * @return
     */
    public jZabbixRequestBuilder id(Integer id) {
        request.setId(id);
        return this;
    }
}
