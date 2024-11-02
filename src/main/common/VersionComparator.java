package src.main.common;

import java.util.Arrays;

public class VersionComparator {
    /**
     * 比较当前 JDK 版本是否满足指定的版本条件
     *
     * @param leftVersion 当前 JDK 版本号（如 "1.8.0_181" 或 "11.0.1"）
     * @param rightVersion  目标版本号（如 "8u182"）
     * @return 如果当前版本 >= 目标版本，则返回 true；否则返回 false
     */
    public static boolean isVersionAtLeast(String leftVersion, String rightVersion) {
        String[] currentParts = parseVersion(leftVersion);
        String[] targetParts = parseVersion(rightVersion);
        for (int i = 0; i < currentParts.length; i++) {
            int left = Integer.parseInt(currentParts[i]);
            int right = Integer.parseInt(targetParts[i]);
            if (left > right) {
                return false;
            } else if (left < right) {
                return true;
            }
        }
        return true;
    }

    /**
     * 解析版本号字符串，将其转换为可以比较的版本数字数组
     *
     * @param version 版本号字符串
     * @return 版本号各部分的数组
     */
    private static String[] parseVersion(String version) {
        if (version.startsWith("1.")) {
            version = version.substring(2); // 去掉前面的 "1."
        }
        version = version.replace("_", ".").replace("u", ".");
        return version.split("\\.");
    }
}