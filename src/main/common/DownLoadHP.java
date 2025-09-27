package src.main.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DownLoadHP {

    // 每块大小：1MB
    private static final int CHUNK_SIZE = 1024 * 1024;  // 1MB 缓冲区大小

    public CompletableFuture<String> downloadHPAsync(String address, ProgressCallback progressCallback) {
        return CompletableFuture.supplyAsync(() -> {
            String fileName = null;
            try {
                // 设置下载URL
                String fileURL = address + "/heapdump";
                String saveDir = "./HPFile"; // 保存文件的目录

                // 处理文件名
                fileName = getFileNameFromURL(address);

                // 创建保存目录
                Path dirPath = Paths.get(saveDir);
                if (Files.notExists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                // 获取文件总大小
                long totalSize = getFileSize(fileURL);

                // 下载文件分块
                downloadFileInChunks(fileURL, saveDir, fileName, totalSize, progressCallback);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return fileName;
        });
    }

    // 获取文件名
    private String getFileNameFromURL(String address) {
        String fileName = address.replaceAll("^(http://|https://)", "");
        int portIndex = fileName.indexOf(":");
        if (portIndex != -1) {
            fileName = fileName.substring(0, portIndex);
        }
        int pathIndex = fileName.indexOf("/");
        if (pathIndex != -1) {
            fileName = fileName.substring(0, pathIndex);
        }
        return fileName + "_heapdump";
    }

    // 获取文件大小
    private long getFileSize(String fileURL) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("HEAD");
        long length = httpConn.getContentLengthLong();
        httpConn.disconnect();
        return length;
    }

    // 分块下载文件
    private void downloadFileInChunks(String fileURL, String saveDir, String fileName, long totalSize, ProgressCallback progressCallback) throws IOException {
        File outputFile = new File(saveDir + File.separator + fileName);

        try (RandomAccessFile raf = new RandomAccessFile(outputFile, "rw")) {
            raf.setLength(totalSize);  // 预分配文件大小

            // 下载每个块
            for (long start = 0; start < totalSize; start += CHUNK_SIZE) {
                long end = Math.min(start + CHUNK_SIZE - 1, totalSize - 1);  // 确定块的结束位置
                // 下载并写入文件的该块
                downloadChunk(fileURL, raf, start, end, totalSize, progressCallback);
            }
        }
    }

    // 下载一个块
    private void downloadChunk(String fileURL, RandomAccessFile raf, long start, long end, long totalSize, ProgressCallback progressCallback) throws IOException {
        HttpURLConnection httpConn = (HttpURLConnection) new URL(fileURL).openConnection();
        String rangeHeader = "bytes=" + start + "-" + end;
        httpConn.setRequestProperty("Range", rangeHeader);
        httpConn.connect();

        // 读取响应并写入对应的文件位置
        try (InputStream inputStream = httpConn.getInputStream()) {
            raf.seek(start);  // 定位到文件的对应位置
            byte[] buffer = new byte[8192];  // 缓冲区
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                raf.write(buffer, 0, bytesRead);
            }
        }
        httpConn.disconnect();

        // 更新进度
        if (progressCallback != null) {
            long downloadedBytes = end + 1;
            String progressText = String.format("下载进度: %s / %s",
                    formatFileSize(downloadedBytes),
                    formatFileSize(totalSize));
            progressCallback.onProgress(downloadedBytes, totalSize, progressText);
        }
    }

    // 格式化文件大小
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " 字节";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public interface ProgressCallback {
        void onProgress(long downloadedBytes, long totalBytes, String progressText);
    }
}