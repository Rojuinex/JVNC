import java.io.*;
import java.net.*;
import java.util.*;

import java.awt.Frame;
import java.awt.Graphics;

import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;

import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
 
public class TestClient {
    public static void main(String[] args) throws Exception {
 
        if (args.length != 1) {
             System.out.println("Usage: java TestClient <hostname>");
             return;
        }
 
            // get a datagram socket
        DatagramSocket socket = new DatagramSocket();
 
            // send request
        byte[] buf            = new byte[8];
        InetAddress address   = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
     

        buf = new byte[8000];
            // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
 
        // display response
        byte[] data = packet.getData();

        int x      = ByteBuffer.wrap(data,0,4).getInt();
        int y      = ByteBuffer.wrap(data,4,4).getInt();
        int length = ByteBuffer.wrap(data,8,4).getInt();

        InputStream in = new ByteArrayInputStream(data, 12, length);
        BufferedImage image = ImageIO.read( in );


        System.out.println( "x: " + x + " y: " + y + " length: " + length );

        Frame frame = new Frame("Remote Image");

        frame.setSize(300, 300);
        frame.setDefaultCloseOperation();
        frame.setVisible(true);

        BufferStrategy bs = frame.getBufferStrategy();
        
        while(bs == null){
            frame.createBufferStrategy(2);
            bs = frame.getBufferStrategy();
        }

        Thread.sleep(100);

        Graphics g = bs.getDrawGraphics();

        g.drawImage(image, 0, 20, null);

        g.dispose();
        bs.show();
     
        socket.close();
    }
}
