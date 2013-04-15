import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class BlockCapture {
	
	public static void main(String[] args) throws Exception{
		Robot robot = new Robot();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int blockColumns = 5;
		int blockRows    = 5;

		int blockWidth   = (int)Math.round(screenSize.getWidth() / blockColumns);
		int blockHeight  = (int)Math.round(screenSize.getHeight() / blockRows);

		Rectangle[][] blocks = new Rectangle[blockColumns][blockRows];


		for (int y = 0; y < blockRows; y++) {
			for (int x = 0; x < blockColumns; x++) {
				blocks[x][y] = new Rectangle(x * blockWidth, y * blockHeight, blockWidth, blockHeight);
			}
		}

		for (int i = 0; i < blockRows; i++) {
			for (int j = 0; j < blockColumns; j++) {
				BufferedImage block = robot.createScreenCapture(blocks[j][i]);
				ImageIO.write(block, "JPG", new File("Block[" + j + "][" + i + "].jpg"));
			}
		}
	}
}