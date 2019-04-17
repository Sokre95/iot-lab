package fer.ior;

public class Packet {
    private float temperature;
    private int humidty;

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setHumidty(int humidty) {
        this.humidty = humidty;
    }

    public void setWaspmote_id(String waspmote_id) {
        this.waspmote_id = waspmote_id;
    }

    private String waspmote_id;

    public float getTemperature() {
        return temperature;
    }

    public int getHumidty() {
        return humidty;
    }

    public String getWaspmote_id() {
        return waspmote_id;
    }
}
