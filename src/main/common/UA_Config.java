package src.main.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class UA_Config {

    public  List<String> loadUserAgents() throws IOException {
        return Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/resources/UA.txt"));
    }
    public  String getRandomUserAgent(List<String> userAgents) {
        Random random = new Random();
        int index = random.nextInt(userAgents.size());
        return userAgents.get(index);
    }

}
