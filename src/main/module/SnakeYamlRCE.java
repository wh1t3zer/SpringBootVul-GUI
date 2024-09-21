package src.main.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class SnakeYamlRCE {
    private String address;
    private String vpsIP;
    private String vpsPORT;
    private String args;
    public String text;
    public String pocData = "";
    public String expData = "";
    public SnakeYamlRCE(String address, String vpsIP, String vpsPORT, String args){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPORT = vpsPORT;
        this.args = args;
    }
    public SnakeYamlRCE(String address){
        this.address = address;
    }


    public void Result1(){
        String site = address + "/env";
        String llib = "spring-boot-starter-actuator";
        disableSSLVerification();
        try{
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains(llib)) {
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if (matcher.find()) {
                    text = String.format("spring-boot-starter-actuator 依赖为: %s", matcher.group(1));
                    System.out.println(text);
//                    callback.onResult(text);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void Result2(){

    }
    public void Exp() throws IOException {
        String api ="/actuator/env";
        String site = address + api;
        try{
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 存在路径是springboot2，否则是springboot1
                text = "当前版本为springboot2";
//                callback.onResult("当前版本为springboot2");
                System.out.println(text);
                Result2();
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                //                callback.onResult("当前版本为springboot1");
                System.out.println(text);
                Result1();
            }else{
//                callback.onResult("未识别springboot版本");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        SnakeYamlRCE s = new SnakeYamlRCE("http://127.0.0.1:9092");
        s.Exp();
    }
}
