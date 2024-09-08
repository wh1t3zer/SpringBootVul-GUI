package src.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import src.main.Exp.ExpImp.ExpImp;
import src.main.impl.ResultCallback;
import src.main.module.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class springboot_vul extends Application {
        public int Vulvalue;
        public int clsValue;
        public static TextFlow consoleOutput;
        public static ExecutorService executorService;
        public static ScrollPane scrollPane; // 滚动
        public static ProgressBar progressBar; // 进度条

        @Override
        public void start(Stage stage) throws UnsupportedEncodingException {
                // 地址标签
                Label addlabel = new Label("地址: ");
                addlabel.setMaxSize(50, 30);
                Label explabel = new Label("漏洞列表: ");
                explabel.setMaxSize(60, 30);
                Label argslabel = new Label("脱敏参数: ");
                argslabel.setMaxSize(60, 30);
                Label cmdlabel = new Label("命令参数: ");
                cmdlabel.setMaxSize(60, 30);
                Label clslabel = new Label("调用类名: ");
                clslabel.setMaxSize(60,30);
                Label curstatuslabel = new Label("当前状态： ");
                curstatuslabel.setMaxSize(70,30);

                // 漏洞类型  后续优化修改
                // 定义选项的显示文本和对应的值
                String[] VulList = {
                        "全部",
                        "端点泄露扫描",
                        "密码脱敏漏洞1",
                        "密码脱敏漏洞2",
                        "密码脱敏漏洞3",
                        "Spring Cloud SnakeYaml RCE漏洞",
                        "Spring Cloud Gateway RCE漏洞",
                        "SpEl注入 RCE漏洞",
                        "Eureka Xstream Serialize RCE漏洞",
                        "Jolokia Logback JNDI RCE漏洞",
                        "Jolokia Realm JNDI RCE漏洞",
                        "H2 Database Query属性 RCE漏洞",
                        "H2 Database JNDI RCE漏洞",
                        "Mysql Jdbc Serialize RCE漏洞",
                        "Logging属性 Logback JNDI RCE漏洞",
                        "Logging属性 Groovy RCE漏洞",
                        "MainSource Groovy RCE漏洞",
                        "H2 Database Datasource RCE漏洞",
                        "Druid连接池密码爆破",
                };

                int[] VulListValue = {
                        1,  // 全部
                        2,  // 端点泄露扫描
                        3,  // 密码脱敏漏洞1
                        4,  // 密码脱敏漏洞2
                        5,  // 密码脱敏漏洞3
                        6,  // Spring Cloud SnakeYaml RCE漏洞
                        7,  // Spring Cloud Gateway RCE漏洞
                        8,  // SpEl注入 RCE漏洞
                        9,  // Eureka Xstream Serialize RCE漏洞
                        10,  // Jolokia Logback JNDI RCE漏洞
                        11, // Jolokia Realm JNDI RCE漏洞
                        12, // H2 Database Query属性 RCE漏洞
                        13, // H2 Database JNDI RCE漏洞
                        14, // Mysql Jdbc Serialize RCE漏洞
                        15, // Logging属性 Logback JNDI RCE漏洞
                        16, // Logging属性 Groovy RCE漏洞
                        17, // MainSource Groovy RCE漏洞
                        18, // H2 Database Datasource RCE漏洞
                        19, // Druid连接池密码爆破
                };

                String[] ClassNameList = {
                        "SpringApplicationAdminMXBeanRegistrar类",
                        "EnvironmentManager类",
                };

                int[] ClassNameListValue = {
                        1, //SpringApplicationAdminMXBeanRegistrar类
                        2  //EnvironmentManager类
                };

                // 类方法调用下拉框
                ComboBox<String> clsComboBox = new ComboBox<>();
                clsComboBox.getItems().addAll(ClassNameList);
                clsComboBox.setOnAction(event -> {
                        String optionsValue = clsComboBox.getValue();
                        // 查找选项对应的值
                        for (int i = 0; i < ClassNameList.length; i++) {
                                if (ClassNameList[i].equals(optionsValue)) {
                                        clsValue = ClassNameListValue[i];
                                        break;
                                }
                        }
                });
                clsComboBox.setPrefWidth(200);
                clsComboBox.setValue(null); // 默认无

                //设置VPS内容
                Label vpsaddrlabel = new Label("IP：");
                vpsaddrlabel.setMaxSize(60, 30);
                TextField VpsAddrtf = new TextField();
                VpsAddrtf.setPrefWidth(140);
                Label vpsportlabel = new Label("port：");
                vpsportlabel.setMaxSize(60,30);
                TextField VpsPorttf = new TextField();
                VpsPorttf.setPrefWidth(60);

                // 调用类输入框容器
                HBox clsBox = new HBox(10);
                clsBox.setAlignment(Pos.TOP_LEFT);
                clsBox.setPrefHeight(40);
                HBox vpsbox = new HBox(10);
                vpsbox.getChildren().addAll(vpsaddrlabel,VpsAddrtf,vpsportlabel,VpsPorttf);
                clsBox.getChildren().addAll(clslabel,clsComboBox,vpsbox);
                clsBox.setPadding(new Insets(0, 0, 0, 20));

                // 下拉框
                ComboBox<String> comboBox = new ComboBox<>();
                comboBox.getItems().addAll(VulList);
                comboBox.setOnAction(event -> {
                        String optionsValue = comboBox.getValue();
                        Vulvalue = -1;
                        // 查找选项对应的值
                        for (int i = 0; i < VulList.length; i++) {
                                if (VulList[i].equals(optionsValue)) {
                                        Vulvalue = VulListValue[i];
                                        break;
                                }
                        }
                });
                comboBox.setPrefWidth(200);
                comboBox.setValue(null); // 默认选中“无”

                // 输入框
                TextField Addrtf = new TextField();
                Addrtf.setPromptText("请输入地址");
                Addrtf.setPrefWidth(200);
                TextField Argstf = new TextField();
                Argstf.setPromptText("请输入参数");
                Argstf.setPrefWidth(200);
                TextField Cmdtf = new TextField();
                Cmdtf.setPromptText("请输入命令");
                Cmdtf.setPrefWidth(200);
                TextField Clstf = new TextField();
                Clstf.setPrefWidth(200);


                // 按钮
                Button Expbtn = new Button("开干");
                Expbtn.setOnAction(event -> {
                        try {
                                handlerExp(Addrtf,Argstf,Cmdtf,VpsAddrtf,VpsPorttf);
                        } catch (IOException e) {
                                throw new RuntimeException(e);
                        }
                });
                Expbtn.setPrefSize(80,60);
                Button Shellbtn = new Button("Getshell");
                Shellbtn.setOnAction(event -> {
                        try {
                                handlerGetshell(Addrtf);
                        } catch (IOException e) {
                                throw new RuntimeException(e);
                        }
                });
                Shellbtn.setPrefSize(80,60);
                HBox btnBox = new HBox(10);
                btnBox.getChildren().addAll(Expbtn,Shellbtn);
                HBox.setMargin(Expbtn,new Insets(0,0,0,20));

                // 地址框和漏洞列表框
                HBox addrBox = new HBox(10);
                addrBox.getChildren().addAll(addlabel, Addrtf, explabel, comboBox);
                addrBox.setAlignment(Pos.TOP_LEFT);
                addrBox.setPadding(new Insets(0, 0, 0, 20));
                HBox.setMargin(Addrtf, new Insets(0, 0, 0, 25));  // 向右移动地址框

                // 漏洞利用框
                HBox menuBox = new HBox(10);
                menuBox.setAlignment(Pos.TOP_LEFT);
                menuBox.setPrefHeight(40);
                menuBox.getChildren().addAll(argslabel, Argstf, cmdlabel, Cmdtf);
                menuBox.setPadding(new Insets(0, 0, 0, 20));

                // 按钮容器
                VBox buttonBox = new VBox(10);
                buttonBox.getChildren().addAll(addrBox, menuBox, clsBox);

                HBox boxContainer = new HBox();
                // 左右两边各为一个容器
                boxContainer.getChildren().addAll(buttonBox,btnBox);

                // 添加间隔区域
                Region topSpacer = new Region();
                VBox.setVgrow(topSpacer, Priority.ALWAYS);

                // 文本框 (改为 TextFlow)
                consoleOutput = new TextFlow();
                consoleOutput.setPadding(new Insets(10, 20, 10, 20)); // 设置边距
                scrollPane = new ScrollPane(consoleOutput);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefHeight(200);  // 调整高度
                Platform.runLater(()->{
                        consoleOutput.requestFocus();
                        scrollPane.setVvalue(1.0);
                });

                // 为 Text 添加右键菜单，允许复制
                ContextMenu contextMenu = new ContextMenu();
                MenuItem copyItem = new MenuItem("复制");
                contextMenu.getItems().add(copyItem);

                // 状态栏
                HBox statusBar = new HBox();
                // 创建容器，将“当前状态”标签和进度条放在里面
                HBox statusContainer = new HBox();
                Label statusLabel = new Label("禁止用于未授权测试！");
                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
                progressBar = new ProgressBar(0);  // 初始化进度条，默认值为0
                progressBar.setPrefWidth(200);     // 设置进度条的宽度
                // 创建一个空的区域占位符，推动右边的内容到最右侧
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // 将标签和进度条放到状态容器中
                statusBar.getChildren().addAll(statusLabel, spacer, curstatuslabel, progressBar);
                statusContainer.setAlignment(Pos.CENTER_RIGHT);
                statusBar.getChildren().add(statusContainer);
                // 设置状态栏的样式
                statusBar.setStyle("-fx-background-color: #f0f0f0;");
                statusBar.setPadding(new Insets(5, 10, 5, 10));

                // 创建主界面布局
                BorderPane mainLayout = new BorderPane();
                mainLayout.setCenter(scrollPane);         // ScrollPane (包裹TextFlow) 放在中间
                mainLayout.setBottom(statusBar);          // 状态栏放在底部

                VBox topLayout = new VBox(10);
                topLayout.getChildren().addAll(topSpacer, boxContainer); // Add spacer before buttonBox

                mainLayout.setTop(topLayout);

                // 创建场景并设置到舞台
                Scene scene = new Scene(mainLayout, 800, 600);
                stage.setTitle("SpringBootGUI by wh1t3zer");
                stage.setScene(scene);
                stage.show();

                // 初始化线程池
                executorService = Executors.newSingleThreadExecutor();
        }

        public void handlerAllScan(String address){

        }
        public void handlerScanVul(String address) throws IOException {
                final AtomicReference<Double> curlines = new AtomicReference<>(0.0);
                final AtomicReference<Double> totalLines = new AtomicReference<>(0.0);
                // 暂无证书模块，待设置
                if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                }
                if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                }
                consoleOutput.getChildren().clear();
                consoleOutput.getChildren().addAll(new Text("端点扫描进行中，请勿关闭！\n"));
                ScanVul sv = new ScanVul(address);  // 执行具体扫描操作
                executorService.submit(() -> {
                        try {
                                sv.scanVul(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        if (result.contains("curlines")) {
                                                                int delimiterIndex = result.indexOf(":");
                                                                if (delimiterIndex != -1) {
                                                                        curlines.set(Double.parseDouble(result.substring(delimiterIndex + 1).trim()));
                                                                }
                                                        } else if (result.contains("totalLines")) {
                                                                int delimiterIndex = result.indexOf(":");
                                                                if (delimiterIndex != -1) {
                                                                        totalLines.set(Double.parseDouble(result.substring(delimiterIndex + 1).trim()));
                                                                }
                                                        } else {
                                                                // 输出其他内容到控制台
                                                                Text text = new Text(result + "\n");
                                                                consoleOutput.getChildren().add(text);
                                                                // 自动滚动到最新内容
                                                                scrollPane.setVvalue(1.0);
                                                        }
                                                        // 更新进度条
                                                        if ((curlines.get() > 0)&&totalLines.get()>0) {
                                                                double progress = curlines.get() / totalLines.get();
                                                                progressBar.setProgress(progress);
                                                        }
                                                });

                                        }
                                        @Override
                                        public void onComplete() {
                                                Platform.runLater(() -> {
                                                        Text completeText = new Text("扫描完成！\n");
                                                        consoleOutput.getChildren().add(completeText);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }
                                });
                        } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                        }
                });

        }

        public void handlerGetSp_1(String address,String args) throws IOException {
                consoleOutput.getChildren().clear();
                if (args.isEmpty()){
                        showAlertEmpty("脱敏参数为空！");
                }else{
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        GetSpPassWord_I gsp = new GetSpPassWord_I(address,args,clsValue);
                        gsp.Exp(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                        });
                                }

                                @Override
                                public void onComplete() {

                                }
                        });
                }
        }
        public void handlerGetSp_2(String address,String args,String vpsIP,String vpsPORT) throws IOException {
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty()) {
                                showAlertEmpty("反弹IP为空！");
                        }
                        GetSpPassWord_II gsp = new GetSpPassWord_II(address,vpsIP,vpsPORT,args);
                        gsp.Exp(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                // 输出其他内容到控制台
                                                Text text = new Text(result + "\n");
                                                consoleOutput.getChildren().add(text);
                                                // 自动滚动到最新内容
                                                scrollPane.setVvalue(1.0);
                                        });
                                }
                                @Override
                                public void onComplete() {
                                }
                        });

                }
        }
        public void handlerGetSp_3(String address,String args,String vpsIP,String vpsPORT) throws IOException {
                consoleOutput.getChildren().clear();
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty()) {
                                showAlertEmpty("反弹IP为空！");
                        }
                        GetSpPassWord_III gsp = new GetSpPassWord_III(address,vpsIP,vpsPORT,args);
                        gsp.Exp(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                // 输出其他内容到控制台
                                                Text text = new Text(result + "\n");
                                                consoleOutput.getChildren().add(text);
                                                // 自动滚动到最新内容
                                                scrollPane.setVvalue(1.0);
                                        });
                                }
                                @Override
                                public void onComplete() {
                                }
                        });
                }
        }
        public void handlerSnakeYamlRce(String address,String command){

        }
        public void handlerSpgRCE(String address, String command) throws IOException {
                // 清空控制台输出
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        SpringGawRCE spg = command.isEmpty() ? new SpringGawRCE(address) : new SpringGawRCE(address, command);
                        executorService.submit(() -> {
                                try {
                                        Stream<String> result = spg.GawExp();
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
        public void handlerSpElRCE(String address,String command){

        }
        public void handlerEurekaXstreamRCE(String address,String command){

        }
        public void handlerJolokiaLogbackRCE(String address,String command){

        }
        public void handlerJolokiaRealmRCE(String address,String command){

        }

        public void handlerH2DatabaseQueryRCE(String address,String command){

        }
        public void handlerH2DatabaseJNDIRCE(String address,String command){

        }
        public void handlerMysqlJdbcRCE(String address,String command){

        }
        public void handlerLoggingLogbackRCE(String address,String command){

        }
        public void handlerLoggingGroovyRCE(String address,String command){

        }
        public void handlerMainSourceGroovyRCE(String address,String command){

        }
        public void handlerH2DatabaseDatasourceRCE(String address,String command){

        }
        public void handlerDruidBruteForce(String address){
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        consoleOutput.getChildren().addAll(new Text("Druid爆破中，请勿关闭！\n"));
                        DruidBruteForce bf = new DruidBruteForce(address);
                        executorService.submit(() -> {
                                try {
                                        Stream<String> result = bf.BruteDruid();
                                        result.forEach(line -> {
                                                Platform.runLater(() -> {
                                                        Text text = new Text(line + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        });
                                } catch (IOException | InterruptedException e) {
                                        e.printStackTrace();
                                        Platform.runLater(() -> {
                                                Text errorText = new Text("发生错误，请检查与服务器的连接！\n");
                                                consoleOutput.getChildren().add(errorText);
                                        });
                                }
                        });
                }
        }
        public void handleAllVulnerabilities(String address,String command){

        }
        public void handlerGetshell(TextField addr) throws IOException {
                consoleOutput.getChildren().clear();
                // 一键上内存马
                String address = addr.getText();
                String command = "";
                ExpImp exp = new ExpImp(address);
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else{
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        switch (Vulvalue){
                                case -1:
                                        showAlertEmpty("你踏马还没选择漏洞类型呢！");
                                case 5:
                                        handlerSnakeYamlRce(address,command);
                                        break;
                                case 6:
                                        exp.handlerSpgRCE(address);
                                        break;
                                case 7:
                                        handlerSpElRCE(address, command);
                                        break;
                                case 8:
                                        handlerEurekaXstreamRCE(address, command);
                                        break;
                                case 9:
                                        handlerJolokiaLogbackRCE(address, command);
                                        break;
                                case 10:
                                        handlerJolokiaRealmRCE(address, command);
                                        break;
                                case 11:
                                        handlerH2DatabaseQueryRCE(address, command);
                                        break;
                                case 12:
                                        handlerH2DatabaseJNDIRCE(address, command);
                                        break;
                                case 13:
                                        handlerMysqlJdbcRCE(address, command);
                                        break;
                                case 14:
                                        handlerLoggingLogbackRCE(address, command);
                                        break;
                                case 15:
                                        handlerLoggingGroovyRCE(address, command);
                                        break;
                                case 16:
                                        handlerMainSourceGroovyRCE(address, command);
                                        break;
                                case 17:
                                        handlerH2DatabaseDatasourceRCE(address, command);
                                        break;
                                case 18:
                                        handlerDruidBruteForce(address);
                                        break;
                                case 19:
                                        handleAllVulnerabilities(address, command);
                                        break;
                                default:
                                        showAlertEmpty("你踏马还没选择漏洞类型呢！");
                        }
                }

        }
        public void handlerExp(TextField addr,TextField args,TextField cmdobj,TextField vpsobj,TextField portobj) throws IOException {
                String address = addr.getText();
                String arg = args.getText();
                String command = cmdobj.getText();
                String vpsIP = vpsobj.getText();
                String vpsPort = portobj.getText();
                        switch (Vulvalue) {
                                case -1:
                                        showAlertEmpty("你踏马还没选择漏洞类型呢！");
                                case 1:
                                        handlerAllScan(address);
                                        break;
                                case 2:
                                        handlerScanVul(address);
                                        break;
                                case 3:
                                        handlerGetSp_1(address,arg);
                                        break;
                                case 4:
                                        handlerGetSp_2(address,arg,vpsIP,vpsPort);
                                        break;
                                case 5:
                                        handlerGetSp_3(address,arg,vpsIP,vpsPort);
                                        break;
                                case 6:
                                        handlerSnakeYamlRce(address,command);
                                        break;
                                case 7:
                                        handlerSpgRCE(address,command);
                                        break;
                                case 8:
                                        handlerSpElRCE(address, command);
                                        break;
                                case 9:
                                        handlerEurekaXstreamRCE(address, command);
                                        break;
                                case 10:
                                        handlerJolokiaLogbackRCE(address, command);
                                        break;
                                case 11:
                                        handlerJolokiaRealmRCE(address, command);
                                        break;
                                case 12:
                                        handlerH2DatabaseQueryRCE(address, command);
                                        break;
                                case 13:
                                        handlerH2DatabaseJNDIRCE(address, command);
                                        break;
                                case 14:
                                        handlerMysqlJdbcRCE(address, command);
                                        break;
                                case 15:
                                        handlerLoggingLogbackRCE(address, command);
                                        break;
                                case 16:
                                        handlerLoggingGroovyRCE(address, command);
                                        break;
                                case 17:
                                        handlerMainSourceGroovyRCE(address, command);
                                        break;
                                case 18:
                                        handlerH2DatabaseDatasourceRCE(address, command);
                                        break;
                                case 19:
                                        handlerDruidBruteForce(address);
                                        break;
                                default:
                                        showAlertEmpty("你踏马还没选择漏洞类型呢！");
                        }
        }

        public void showAlertEmpty(String text) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initStyle(StageStyle.UNDECORATED);
                alert.setContentText(text);
                alert.showAndWait();
        }

        public static void main(String[] args) {
                launch(args);
        }
        @Override
        public void stop() {
                if (executorService != null) {
                        executorService.shutdownNow();
                }
                Platform.exit();
        }
}