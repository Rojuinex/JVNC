package biz.bclife.jvnc.core;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Block extends Rectangle {
	private BufferedImage image         = null;

	public Block()                                    {	super(0,0,0,0); }             // end of constructor
	public Block(Rectangle rect)                      { super(rect); }                // end of constructor
	public Block(int x, int y, int width, int height) {	super(x, y, width, height);}  // end of constructor

	public Block(double x, double y, double width, double height) {
		super((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(width), (int)Math.floor(height));
	}  // end of constructor

	public void setImage(BufferedImage _image)        {	image = _image; }             // end of setImage
	public BufferedImage getImage()                   { return image; }       // end of getImage
} // end of class Block