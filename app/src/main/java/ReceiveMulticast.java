import android.util.Log;

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

import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastPackage;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Message;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;


//Only used for testing purposes

public class ReceiveMulticast {
    private MulticastSocket s = null;
    InetAddress group = null;
    String ipAdress = "224.1.1.1";
    int port = 9879;
    Map<String, TableInfo> tables;
    Map<String, TableInfo> oldTables;
    private TableInfo hostTable;
    private MulticastPackage greeting = new MulticastPackage(Message.Target.ALL_DEVICES,
            Message.Type.REQUEST_ALL_TABLES);

    public ReceiveMulticast(){
        initMulticast();
        tables = new HashMap<>();
        oldTables = new HashMap<>();

        try {
            TimeUnit.SECONDS.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        send(greeting);

        receiveGreeting();

        PlayerInfo player = new PlayerInfo("Alex", "test_address");
        MulticastPackage joinRequest = new MulticastPackage(hostTable.getHost().getDeviceAddress(),
                Message.Type.PLAYER_JOIN_REQUEST, player);

        try {
            for(int i = 0; i < 3; i++){
                send(joinRequest);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MulticastPackage joinSuccessful = new MulticastPackage(hostTable.getHost().getDeviceAddress(),
                Message.Response.PLAYER_JOIN_SUCCESS, player);
/*
        try {
            for(int i = 0; i < 3; i++){
                send(joinSuccessful);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

    }

    private void send(MulticastPackage mPackage){
        byte[] msg = Serializer.serialize(mPackage);
        DatagramPacket dp = new DatagramPacket(msg, msg.length, group, port);
        try {
            s.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveGreeting() {
        byte[] buf = new byte[10000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        boolean keepGoing = true;
        int counter = 1;
        int marginOfError = 3;

        try {
            s.setSoTimeout(100);
        } catch (SocketException e) {
            counter++;
        }

        while(keepGoing) {
            Object msg = null;
            try {
                s.receive(recv);
                byte[] data = recv.getData();
                if(data != null) {
                    msg = Serializer.deserialize(data);
                }
            } catch (IOException e) {
                msg = null;
                if(oldTables.equals(tables) && counter < marginOfError){
                    send(greeting);
                }

                System.out.println("Trying to receive datagram again (try " + counter + ")");
                counter++;
            }


            if(counter > marginOfError) {
                keepGoing = false;
                System.out.println("Done");
            }

            if (msg instanceof MulticastPackage) {
                String target = ((MulticastPackage) msg).getTarget();
                String type = ((MulticastPackage) msg).getPackageType();
                Object packageObject = ((MulticastPackage) msg).getObject();

                if (target.equals(Message.Target.ALL_DEVICES)) {
                    if(type.equals(Message.Response.HOST_TABLE)){
                        hostTable = (TableInfo) packageObject;
                        tables.put(hostTable.getName(), hostTable);
                        System.out.println("ReceiveMulticast: Received a " + type);
                    }
                }

                if (hostTable !=  null && target.equals(hostTable.getHost().getDeviceAddress())){
                    if(type.equals(Message.Response.PLAYER_JOIN_ACCEPTED)){
                        //TODO: Send confirmation
                    }
                }
            }
        }
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

    public static void main(String[] args){
        ReceiveMulticast rm = new ReceiveMulticast();
    }
}
