package src.main.FileCommon;

import com.google.gson.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class File {
    public Map<Integer, JsonArray> parseVulList(String filePath) throws IOException {
        Map<Integer,JsonArray> totalList = new HashMap<>();
        try (Reader reader = new FileReader(System.getProperty("user.dir") + "/src/main/" + filePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            JsonArray vulnList = jsonObject.getAsJsonArray("VuLnList");
//            for (JsonElement elem : vulnList) {
//                JsonObject vuln = elem.getAsJsonObject();
//                String label = vuln.get("label").getAsString();
//                int value = vuln.get("value").getAsInt();
//                System.out.println("Label: " + label + ", Value: " + value);
//            }

            JsonArray clasList = jsonObject.getAsJsonArray("ClassList");
//            for (JsonElement elem : clasList) {
//                JsonObject vuln1 = elem.getAsJsonObject();
//                String label = vuln1.get("label").getAsString();
//                int value = vuln1.get("value").getAsInt();
//                System.out.println("Label: " + label + ", Value: " + value);
//            }
            totalList.put(1,vulnList);
            totalList.put(2,clasList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalList;
    }
}
