package src.main.common;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HTTPConfig {
    public static HttpURLConnection createConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (Header_Config.hasAuthHeader()) {
            String headerName = Header_Config.getAuthHeaderName();
            String headerValue = Header_Config.getAuthHeaderValue();
            conn.setRequestProperty(headerName, headerValue);
            String ua = "";
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
        }
        return conn;
    }
}
