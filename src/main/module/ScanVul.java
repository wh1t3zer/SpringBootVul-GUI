package src.main.module;

import src.main.common.DownLoadHP;
import src.main.common.UA_Config;
import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class ScanVul {
    private final String address;
    public String text = "";

    public ScanVul(String address) {
        this.address = address;
    }

    public void scanVul(ResultCallback callback) throws InterruptedException {
        disableSSLVerification();
        String filepath = "./resources/dict.txt";
        List<String> resultList = new ArrayList<>();
        String poc;
        String exp = "发现端点泄露";
        String err1 = "发现端点泄露但无法返回内容";
        String ua = "";
        int totalLines = 0;
        int curlines = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            // 计算总行数
            while (br.readLine() != null) {
                totalLines++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String site = address + line;
                URL obj = new URL(site);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                UA_Config uacf = new UA_Config();
                List<String> ualist = uacf.loadUserAgents();
                ua = uacf.getRandomUserAgent(ualist);
                conn.setRequestProperty("User-Agent", ua);
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    poc = site + " " + exp;
                    resultList.add(site + " " + exp);
                    curlines++;
                    callback.onResult(poc);
                    text = "curlines: " + curlines;
                    callback.onResult(text);
                } else if (responseCode == HttpURLConnection.HTTP_BAD_METHOD) {
                    poc = site + " " + err1;
                    curlines++;
                    callback.onResult(poc);
                    text = "curlines: " + curlines;
                    callback.onResult(text);
                    text = "totalLines: " + totalLines;
                    callback.onResult(text);
                } else {
                    poc = "";
                    curlines++;
                    text = "curlines: " + curlines;
                    callback.onResult(text);
                    text = "totalLines: " + totalLines;
                    callback.onResult(text);
                }

                Thread.sleep(500); // 避免频繁请求
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 在扫描完成后，检查结果并下载文件
        if (resultList.stream().anyMatch(line -> line.contains("/heapdump") && !line.contains("/actuator/heapdump"))) {
            text = "发现heapdump文件！正在下载";
            callback.onResult(text);
            // 异步下载 heapdump 文件
            DownLoadHP downloader = new DownLoadHP();
            CompletableFuture<String> downloadFuture = downloader.downloadHPAsync(address,(downloadedBytes, totalBytes,progressText) -> {
                double progress = (double) downloadedBytes / totalBytes * 100;
                text = String.format("下载进度: %.2f%% (%.2f MB / %.2f MB)", progress,
                        downloadedBytes / (1024.0 * 1024.0),
                        totalBytes / (1024.0 * 1024.0));
                callback.onResult(text);
            });

            // 处理下载完成后的逻辑
            downloadFuture.thenAccept(fileName -> {
                if (fileName != null && !fileName.isEmpty()) {
                    text = String.format("文件下载完成，文件名为: %s", fileName);
                    callback.onResult(text);
                } else {
                    text = "文件下载失败！";
                    callback.onResult(text);
                }
            }).exceptionally(throwable -> {
                text = "文件下载失败，发生异常: " + throwable.getMessage();
                callback.onResult(text);
                return null;
            });
        }
        if (resultList.stream().anyMatch(line -> line.contains("/actuator/heapdump"))){
            text = "发现heapdump文件！正在下载";
            callback.onResult(text);

            // 异步下载 heapdump 文件
            DownLoadHP downloader = new DownLoadHP();
            CompletableFuture<String> downloadFuture = downloader.downloadHPAsync(address + "/actuator",(downloadedBytes, totalBytes,progressText) -> {
                double progress = (double) downloadedBytes / totalBytes * 100;
                text = String.format("下载进度: %.2f%% (%.2f MB / %.2f MB)", progress,
                        downloadedBytes / (1024.0 * 1024.0),
                        totalBytes / (1024.0 * 1024.0));
                callback.onResult(text);
            });

            // 处理下载完成后的逻辑
            downloadFuture.thenAccept(fileName -> {
                if (fileName != null && !fileName.isEmpty()) {
                    text = String.format("文件下载完成，文件名为: %s", fileName);
                    callback.onResult(text);
                } else {
                    text = "文件下载失败！";
                    callback.onResult(text);
                }
            }).exceptionally(throwable -> {
                text = "文件下载失败，发生异常: " + throwable.getMessage();
                callback.onResult(text);
                return null;
            });
        }else{
            text = "未找到heapdump文件";
            callback.onResult(text);
        }

        // 扫描和下载结束后回调
        callback.onComplete();
    }}