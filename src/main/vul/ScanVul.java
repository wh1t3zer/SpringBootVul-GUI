package src.main.vul;

import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScanVul {
    private final String address;

    public ScanVul(String address) {
        this.address = address;
    }

    public CompletableFuture<String> downloadHPAsync() {
        return CompletableFuture.supplyAsync(() -> {
            String fileName = null;
            try {
                String fileURL = address + "/heapdump";
                String saveDir = "./HPFile"; // 替换为保存文件的目录

                // 去http
                fileName = address.replaceAll("^(http://|https://)", "");

                // 去掉端口号
                int portIndex = fileName.indexOf(":");
                if (portIndex != -1) {
                    fileName = fileName.substring(0, portIndex);
                }

                // 去掉路径和查询参数
                int pathIndex = fileName.indexOf("/");
                if (pathIndex != -1) {
                    fileName = fileName.substring(0, pathIndex);
                }
                fileName = fileName + "_" + "heapdump";

                // 创建目录，如果不存在
                Path dirPath = Paths.get(saveDir);
                if (Files.notExists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                // 下载文件
                URL url = new URL(fileURL);
                try (InputStream inputStream = url.openStream();
                     OutputStream outputStream = Files.newOutputStream(Paths.get(saveDir + File.separator + fileName))) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return fileName;
        });
    }

    public void scanVul(ResultCallback callback) throws InterruptedException {
        String filepath = "./resources/dict.txt";
        List<String> resultList = new ArrayList<>();
        String poc;
        String exp = "发现端点泄露";
        String err1 = "发现端点泄露但无法返回内容";

        // 读总行数
        int totalLines = 0;
        int curlines = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            // 先计算总行数
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
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    poc = site + " " + exp;
                    resultList.add(site + " " + exp);
                    curlines++;
                    System.out.println(poc);
                    callback.onResult(poc);
                    callback.onResult("curlines: "+curlines);
                } else if (responseCode == HttpURLConnection.HTTP_BAD_METHOD) {
                    poc = site + " " + err1;
                    curlines++;
                    System.out.println(poc);
                    callback.onResult(poc);
                    callback.onResult("curlines: "+curlines);
                    callback.onResult("totalLines: "+totalLines);
                } else {
                    poc = "";
                    curlines++;
                    callback.onResult("curlines: "+curlines);
                    callback.onResult("totalLines: "+totalLines);
                }
                Thread.sleep(500);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 在这里你可以进行额外的处理，例如检查是否包含特定内容，并异步下载文件
        CompletableFuture<String> downloadFuture = CompletableFuture.completedFuture(null);

        // 检查结果中是否包含 "/heapdump" 关键字
        boolean containsHeapdump = resultList.stream().anyMatch(line -> line.contains("/heapdump"));
        if (containsHeapdump) {
            callback.onResult("发现heapdump文件！正在下载");
            Thread.sleep(3000);
            downloadFuture = downloadHPAsync();
        } else {
            callback.onResult("未找到heapdump文件");
        }

        // 等待下载完成并获取文件名
        downloadFuture.thenApply(fileName -> {
            if (fileName != null && !fileName.isEmpty()) {
                callback.onResult(String.format("文件下载完成，文件名为: %s", fileName));
            } else {
                callback.onResult("文件下载失败！");
                callback.onResult("服务端访问频繁造成429，也可能端点存在而文件不存在");
            }
            callback.onComplete();
            return null;
        }).join();
    }
}