package fer.ior;

public class Parser {

    public static Packet parse(String message){
        Packet p = new Packet();
        String arr[] = message.split("#");

        p.setWaspmote_id(arr[2]);
        if(arr[arr.length - 2].contains("WT")){
            p.setTemperature(Float.parseFloat(arr[arr.length - 2].split("WT:")[1]));
        }
        else if(arr[arr.length - 3].contains("WT")){
            p.setTemperature(Float.parseFloat(arr[arr.length - 3].split("WT:")[1]));
        }
        if(arr[arr.length - 2].contains("DICA:")){
            p.setHumidty(Integer.parseInt(arr[arr.length - 2].split("DICA:")[1]));
        }
        else if(arr[arr.length - 3].contains("DICA:")){
            p.setHumidty(Integer.parseInt(arr[arr.length - 3].split("DICA:")[1]));
        }
        return p;
    }
}
