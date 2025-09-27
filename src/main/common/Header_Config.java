package src.main.common;

// 全局鉴权管理器
public class Header_Config {
    private static final String AUTH_HEADER_KEY = "spring.auth.header";
    private static final String AUTH_HEADER_VALUE = "spring.auth.value";

    public static void setAuthHeader(String headerName, String headerValue) {
        System.setProperty(AUTH_HEADER_KEY, headerName);
        System.setProperty(AUTH_HEADER_VALUE, headerValue);
    }

    public static String getAuthHeaderName() {
        return System.getProperty(AUTH_HEADER_KEY, "");
    }

    public static String getAuthHeaderValue() {
        return System.getProperty(AUTH_HEADER_VALUE, "");
    }

    public static boolean hasAuthHeader() {
        String name = getAuthHeaderName();
        return name != null && !name.trim().isEmpty();
    }

    public static void clearAuthHeader() {
        System.clearProperty(AUTH_HEADER_KEY);
        System.clearProperty(AUTH_HEADER_VALUE);
    }
}
