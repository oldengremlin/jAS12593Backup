/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.org.olden.jas12593backup;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author olden
 */
public class jZabbixRequest {

    private String jsonrpc = "2.0";
    private Map<String, Object> params = new HashMap<>();
    private String method;
    private String auth;
    private Integer id;

    public void putParam(String key, Object value) {
        params.put(key, value);
    }

    public Object removeParam(String key) {
        return params.remove(key);
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
