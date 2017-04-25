import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;


//Only used for testing purposes

public class ReceiveMulticast {
    private MulticastSocket s = null;
    InetAddress group = null;
    String ipAdress = "224.1.1.1";
    int port = 9879;
    Map<String, TableInfo> tables;
    Map<String, TableInfo> oldTables;

    public ReceiveMulticast(){
        initMulticast();
        tables = new HashMap<>();
        oldTables = new HashMap<>();

        try {
            TimeUnit.SECONDS.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 100; i++) {
            send();
        }
        receive();
    }

    private void send(){
        byte[] greeting = serialize("CARDS_AGAINST_HUMANITY.GREETING");
        DatagramPacket dp = new DatagramPacket(greeting, greeting.length, group, port);
        try {
            s.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receive() {
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        boolean keepGoing = true;
        int counter = 1;
        int counter2 = 0;
        int marginOfError = 3;

        try {
            s.setSoTimeout(3000);
        } catch (SocketException e) {
            counter++;
        }

        while(keepGoing) {
            Object msg;
            try {
                s.receive(recv);
                msg = deserialize(recv.getData());
                System.out.println("Message: " + msg);
            } catch (IOException e) {
                msg = null;
                if(oldTables.equals(tables) && counter < marginOfError){
                    send();
                }

                System.out.println("Trying to receive datagram again (try " + counter + ")");
                counter++;
            }


            if(counter > marginOfError) {
                keepGoing = false;
                System.out.println("Done");
            }

            if (msg instanceof TableInfo) {
                PlayerInfo hostName = ((TableInfo) msg).getHost();
                String tableName = ((TableInfo) msg).getName();
                int tableSize = ((TableInfo) msg).getSize();
                tables.put(tableName, new TableInfo(tableName, hostName, tableSize));
                counter2++;
                System.out.println("Received : " + counter2);
            }
        }
    }

    private Object deserialize(byte[] serializedObject) {
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedObject);
        ObjectInput in = null;
        Object o = null;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        return o;
    }

    private void initMulticast(){
        try {
            group = InetAddress.getByName(ipAdress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            s = new MulticastSocket(port);
            s.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] serialize(Object object){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] array = {};
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            array = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return array;
    }

    public static void main(String[] args){
        ReceiveMulticast rm = new ReceiveMulticast();
    }
}
