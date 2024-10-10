package src.main.LoadLib.JARLoad;

import src.main.impl.ResultCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class LoadJarLib {
    private String filename = "";

    public LoadJarLib(String filename) {
        this.filename = filename;
    }

    public void loadJar(ResultCallback callback) {
        try {
            // 1. 定义需要打包的Java文件路径和类名
            String jarFileName = System.getProperty("user.dir") + "/resources/" + filename + ".jar";
            boolean jarCreated = createJarFile(jarFileName, System.getProperty("user.dir") + "/resources/SnakeYamlPayload/src/");

            if (jarCreated) {
                callback.onResult("JAR包打包完成");
            } else {
                callback.onResult("JAR包打包失败");
            }
        } catch (Exception e) {
            callback.onResult("jar包打包异常");
            e.printStackTrace();
        }
    }


    // 创建JAR文件并返回是否成功
    private static boolean createJarFile(String jarFileName, String sourceDir) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(
                "jar", "cvf", jarFileName, "-C", sourceDir,  "."
        );

        // 启动进程
        Process process = builder.start();
        int exitCode = process.waitFor(); // 等待打包完成
        // 返回打包是否成功
        return exitCode == 0;
    }
}