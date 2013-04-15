package biz.bclife.jvnc;

// Import jvnc packages
import biz.bclife.jvnc.core.Block;

// Import java packages
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.image.BufferStrategy;
import java.awt.Graphics;
import java.awt.Color;

import java.nio.ByteBuffer;

import java.net.*;
import java.io.*;

import javax.imageio.ImageIO;


public class Test extends Thread{
	private Robot          robot;
	private Dimension      screenSize;
	private double         blockWidth, blockHeight;
	private int            blockColumns, blockRows;
	private Block[][]      blocks;
	private DatagramSocket socket;
	private boolean        run = true;

	public Test() throws Exception{
		// Initalize all of our global objects
		socket       = new DatagramSocket(4445);
		robot        = new Robot();
		screenSize   = Toolkit.getDefaultToolkit().getScreenSize();
		
		blockWidth   = 100;
		blockHeight  = 100;

		blockColumns = (int)Math.round(screenSize.getWidth()  / blockWidth);
		blockRows    = (int)Math.round(screenSize.getHeight() / blockHeight);

		blocks       = new Block[blockColumns][blockRows];

		// Chunk the screen up into blocks
		for (int y = 0; y < blockRows; y++)
			for (int x = 0; x < blockColumns; x++){
				double bWidth  = blockWidth;
				double bHeight = blockHeight;

				// Make sure last block in row is not larger than need be.
				if( x * bWidth + bWidth > screenSize.getWidth() )
					bWidth = screenSize.getWidth() - x * bWidth;

				// Make sure last block in column is not larger than need be.
				if( y * bHeight + bHeight > screenSize.getHeight() )
					bHeight = screenSize.getWidth() - x * bHeight;

				blocks[x][y] = new Block( x * blockWidth, y * blockHeight, bWidth, bHeight );
			} // end of x for loop

		System.out.println( "Columns: " + blockColumns + " Rows: " + blockRows);

		captureScreen();
	} // end of constructor
	
	public void run() {
		while(run){
			try{
				// Get list of possible formats to write to and print them to the screen.
				String[] formats = ImageIO.getWriterFormatNames();

				for(int i = 0; i < formats.length; i++){
					System.out.println(formats[i]);
				}

				byte[] buf = new byte[8];

				

				// receive request
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				// Convert block to byte array
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write( blocks[0][0].getImage(), "png", baos );
				baos.flush();
				byte[] imageData = baos.toByteArray();
				baos.close();


				System.out.println("Image Data Length: " + imageData.length);		

				// Create byte array for packet (image size + 3 ints [1 int = 4 bytes])
				byte[] data = new byte[imageData.length + 12];
				
				System.out.println("Packet Length: " + data.length);

				// Convert ints to byte arrays
				byte[] xBytes = ByteBuffer.allocate(4).putInt(0).array();
				byte[] yBytes = ByteBuffer.allocate(4).putInt(0).array();
				byte[] lBytes = ByteBuffer.allocate(4).putInt(imageData.length).array();


				// Pointer to current byte in byte array
				int loc = 0;

				// Begin packet with x position
				for( int i = 0; i < xBytes.length; i++ )
					data[loc++] = xBytes[i];

				// Add y position to packet
				for( int i = 0; i < yBytes.length; i++ )
					data[loc++] = yBytes[i];

				// Add image size to packet
				for( int i = 0; i < lBytes.length; i++ )
					data[loc++] = lBytes[i];

				// Add image to packet
				for( int i = 0; i < imageData.length; i++ )
					data[loc++] = imageData[i];


				// Get the return address and port for the outgoing packet
				InetAddress address        = packet.getAddress();
				int port                   = packet.getPort();

				// Create and send the packet
				packet                     = new DatagramPacket(data, data.length, address, port);
				socket.send(packet);


				
			} catch(Exception e){
				System.err.print("Error!!! ");
				e.printStackTrace();
			}
		}

		socket.close();
	}

	public void captureScreen(){
		for (int y = 0; y < blockRows; y++) {
			for (int x = 0; x < blockColumns; x++) {
				captureBlock( x, y );
			} // end of x for loop
		} // end of y for loop
	} // end of captureScreen


	public void captureBlock(int x, int y){
		blocks[x][y].setImage( robot.createScreenCapture(blocks[x][y]) );
	} // end of captureBlock


	public static void main(String[] args) throws Exception {
		(new Test()).start();		
	} // end of main
} // end of class Test