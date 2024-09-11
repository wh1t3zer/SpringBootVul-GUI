package src.main.LoadLib.MemshellLoad;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Base64;

public class MemshellLoad {
    public static byte[] load(String path) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;

        try {
            fis = new FileInputStream(path);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
                baos.flush();
            }
            return baos.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fis != null) {
                try { fis.close(); }
                catch (Exception e) { e.printStackTrace(); }
            }
            if (baos != null) {
                try { baos.close(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
        return null;
    }

    public String run(){
        byte[] code = MemshellLoad.load(System.getProperty("user.dir") + "/src/main/MemshellLoad/GMemShell.class");
        String b64exp = Base64.getEncoder().encodeToString(code);
        return b64exp;
    }
}