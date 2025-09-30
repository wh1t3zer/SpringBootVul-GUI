package src.main.finger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Finger {
    public String address;

    public Finger(String address) {
        this.address = address.replaceAll("/$", "");
    }

    public Map<String, Boolean> checkSpringBoot() {
        Map<String, Boolean> results = new HashMap<>();

        try {
            results.put("whitelabel_error_page", checkWhitelabelErrorPage());
            results.put("actuator_endpoints", checkActuatorEndpoints());
            results.put("spring_favicon", checkSpringFavicon());
            results.put("spring_headers", checkSpringHeaders());
            results.put("spring_static_resources", checkStaticResources());
        } catch (Exception e) {
            System.err.println("检测过程中发生错误: " + e.getMessage());
        }
        return results;
    }

    private boolean checkWhitelabelErrorPage() {
        try {
            String url = address + "/nonexistent-path-" + System.currentTimeMillis();
            HttpURLConnection conn = createConnection(url);
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            String content = readResponseContent(conn);

            return content.contains("Whitelabel Error Page") ||
                    content.contains("There was an unexpected error") ||
                    (status == 404 && content.contains("type=Not Found")) ||
                    content.contains("DefaultServlet");

        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkActuatorEndpoints() {
        String[] actuatorPaths = {
                "/actuator", "/actuator/health", "/actuator/info",
                "/metrics", "/env", "/beans", "/mappings",
                "/health", "/info"  // Spring Boot 1.x 路径
        };

        for (String path : actuatorPaths) {
            if (checkEndpoint(path)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSpringFavicon() {
        try {
            String url = address + "/favicon.ico";
            HttpURLConnection conn = createConnection(url);
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            if (status == 200) {
                String contentType = conn.getContentType();
                return "image/x-icon".equals(contentType) ||
                        "image/vnd.microsoft.icon".equals(contentType);
            }
        } catch (Exception e) {
           // 不处理
        }
        return false;
    }

    private boolean checkSpringHeaders() {
        try {
            String url = address + "/";
            HttpURLConnection conn = createConnection(url);
            conn.setRequestMethod("HEAD");

            Map<String, String> headers = getResponseHeaders(conn);

            return headers.entrySet().stream()
                    .anyMatch(entry ->
                            entry.getKey().toLowerCase().contains("x-application-context") ||
                                    entry.getKey().toLowerCase().contains("x-powered-by") &&
                                            entry.getValue().toLowerCase().contains("spring") ||
                                    entry.getValue().toLowerCase().contains("spring")
                    );

        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkStaticResources() {
        String[] staticPaths = {
                "/error", "/webjars/", "/css/", "/js/",
                "/images/", "/static/", "/public/"
        };

        for (String path : staticPaths) {
            if (checkEndpoint(path)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkEndpoint(String path) {
        try {
            String url = address + path;
            HttpURLConnection conn = createConnection(url);
            conn.setRequestMethod("GET");
            int status = conn.getResponseCode();
            if (status >= 200 && status < 500) {
                String contentType = conn.getContentType();
                String content = readResponseContent(conn);
                return
                        content.contains("status") ||
                                content.contains("beans") ||
                                content.contains("mappings") ||
                                content.contains("environment") ||
                                content.contains("health") ||
                                content.contains("info") ||
                                content.contains("actuator") ||
                                (contentType != null && contentType.contains("application/json")) ||
                                contentType != null && contentType.contains("application/vnd.spring-boot") ||
                                content.contains("/actuator") ||
                                (contentType != null && contentType.contains("text/html") &&
                                        (content.contains("Spring") || content.contains("spring")));
            }
        } catch (Exception e) {
            // 不处理
        }
        return false;
    }

    private HttpURLConnection createConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (compatible; SpringBootScanner/1.0)");
        return conn;
    }

    private String readResponseContent(HttpURLConnection conn) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                reader.close();
                return content.toString();
            } catch (Exception ex) {
                return "";
            }
        }
    }

    private Map<String, String> getResponseHeaders(HttpURLConnection conn) {
        Map<String, String> headers = new HashMap<>();
        for (int i = 0; ; i++) {
            String key = conn.getHeaderFieldKey(i);
            String value = conn.getHeaderField(i);
            if (key == null && value == null) break;
            if (key != null) {
                headers.put(key.toLowerCase(), value);
            }
        }
        return headers;
    }

    public boolean isSpringBootApplication() {
        Map<String, Boolean> results = checkSpringBoot();

        int score = 0;
        if (results.get("whitelabel_error_page")) score += 1;
        if (results.get("actuator_endpoints")) score += 1;
        if (results.get("spring_headers")) score += 1;
        if (results.get("spring_favicon")) score += 1;
        if (results.get("spring_static_resources")) score += 1;
        return score >= 1;
    }

    public boolean CheckedSpringBoot() {
        return isSpringBootApplication();
    }
}
