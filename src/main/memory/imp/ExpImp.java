package src.main.memory.imp;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import src.main.memory.core.ExpCore;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;
import src.main.springboot_vul;

public class ExpImp {
    String address;
    ExecutorService executorService;
    TextFlow consoleOutput;
    ScrollPane scrollPane;
    public ExpImp(String address){
        this.address = address;
        executorService = springboot_vul.executorService;
        consoleOutput = springboot_vul.consoleOutput;
        scrollPane = springboot_vul.scrollPane;
    }

    public void handlerSpgRCE(String address, int type){
        // 清空控制台输出
        consoleOutput.getChildren().clear();
        if (address.endsWith("/")) {
            address = address.replaceAll("/$", "");
        }
        if (!address.startsWith("http://")) {
            address = "http://" + address;
        }
        ExpCore ec = new ExpCore(address);
        executorService.submit(() -> {
            try {
                Stream<String> result =ec.ShellGaw(type);
                result.forEach(line -> {
                    Platform.runLater(() -> {
                        Text text = new Text(line + "\n");
                        consoleOutput.getChildren().add(text);
                        // 自动滚动到最新内容
                        scrollPane.setVvalue(1.0);
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Text errorText = new Text("发生错误，请检查与服务器的连接！\n");
                    consoleOutput.getChildren().add(errorText);
                });
            }
        });
    }

}
