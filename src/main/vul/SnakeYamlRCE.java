package src.main.vul;

public class SnakeYamlRCE {
    public String address;
    public String command;
    public SnakeYamlRCE(String address, String command){
        this.address = address;
        this.command= command;
    }

    public void RcePoc(){
        String api = "/env";
        String api1 = "/refresh";
        String llib = "spring-cloud-starter";

    }
}
