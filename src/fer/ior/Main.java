package fer.ior;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.wpan.RxResponse16;
import com.rapplogic.xbee.api.wpan.RxResponse64;
import com.rapplogic.xbee.util.ByteUtils;
import com.rapplogic.xbee.api.ApiId;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Main {

    static String topic_temp       = "measurement/temperature";
    static String topic_humidity   = "measurement/humidity";
    static int qos             = 2;
    static String broker       = "tcp://10.19.4.127:1883";
    static String clientId     = "Šokre_Gateway";

    static MemoryPersistence persistence = new MemoryPersistence();

    private static void publishPacket(Packet packet) throws MqttException {
        MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();

        connOpts.setCleanSession(true);
        System.out.println("Connecting to broker: " + broker);
        sampleClient.connect(connOpts);

        System.out.println("Publishing temperature");
        MqttMessage message_temp = new MqttMessage(String.valueOf(packet.getTemperature()).getBytes());
        message_temp.setQos(qos);
        sampleClient.publish(topic_temp, message_temp);

        System.out.println("Publishing humidity");
        MqttMessage message_humidity = new MqttMessage(String.valueOf(packet.getHumidty()).getBytes());
        message_humidity.setQos(qos);
        sampleClient.publish(topic_humidity, message_humidity);
        sampleClient.disconnect();
    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    private static String decodePacket(RxResponse64 response){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < response.getRawPacketBytes().length; i++) {
            String rawByteResponse = ByteUtils.toBase16(response.getRawPacketBytes()[i]).replace("0x", "");
            result.append(hexToAscii(rawByteResponse));
        }
        return result.toString();
    }

    private static void receiver() throws Exception {
        XBee xbee = new XBee();

        System.getProperty("java.library.path");
        try {
            xbee.open("/dev/ttyUSB1", 115200);

            while (true) {

                try {
                    XBeeResponse response = xbee.getResponse();

                    if (response.getApiId() == ApiId.RX_16_RESPONSE) {
                        System.out.println("Received RX 16 packet " + ((RxResponse16)response));
                    } else if (response.getApiId() == ApiId.RX_64_RESPONSE) {
                        // DECODE PACKET
                        System.out.println("Received RX 64 packet " + ((RxResponse64)response));
                        String decoded = decodePacket((RxResponse64)response);
                        System.out.println("Decoded: " + decoded);

                        // PARSE WASPMOTE_ID, TEMP and HUMIDITY values
                        Packet packet = Parser.parse(decoded);

                        System.out.println("Waspmote_ID: " + packet.getWaspmote_id());
                        System.out.println("Temp " + packet.getTemperature() + "C");
                        System.out.println("Humidity: " + packet.getHumidty() + "%");

                        // PUBLISH DATA

                        if (packet.getWaspmote_id().equals("node_cilic")){
                            publishPacket(packet);
                        }


                    } else {
                        System.out.println("Ignoring mystery packet " + response.toString());
                    }
                } catch (Exception e) {
                    System.out.println("Error" + e.toString());
                }
            }
        }
        catch (Exception X){}
        finally {
            xbee.close();
        }
    }


    public static void main(String[] args) {
        try {
            receiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
