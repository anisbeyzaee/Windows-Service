import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
/*Program that takes a txt file of network data about
 * the ANN's weights and uses it to classify a given 
 * image as a fire or no fire by resizing the image
 * and storing its pixel values
 */
public class ReadImage {
	private static int sideLength;
	private static int hiddenUnitCount;
	private static int hiddenLayerCount;
	private static int outputUnitCount;
	private static int fireValue;
	private static int inputUnitCount;
	private static double[][] hiddenInputWeights;
	private static double[][] outputHiddenWeights;
	
	public static void main(String[] args) throws Exception {
		File file = new File("networkInfo.txt");
		readNetworkVariables(file);
		BufferedReader reader=new BufferedReader(new FileReader(file));
		double[] pixelValues = new double[inputUnitCount];
		//String imagePathName = "C:\\Users\\Meg\\Documents\\FireImages\\fire1.png";
		String imagePathName =  args[0];//"C:\\eclipse-workspace\\LocalTestImages\\test1.jpg";
		//String imagePathName = "C:\\Users\\Meg\\Documents\\FireImages\\black-forest-fire.jpg";
		//System.out.println(imagePathName);
		
		File imagePath = new File(imagePathName);
		//String resizedImagePathName = "C:\\Users\\Meg\\Documents\\FireResized\\fire1.png";
		String resizedImagePathName = "C:\\eclipse-workspace\\LocalTestImages\\R\\test1.jpg";
		//String resizedImagePathName = "C:\\Users\\Meg\\Documents\\FireResized\\black-forest-fire.jpg";
		resizeImage(imagePath, imagePathName, resizedImagePathName, sideLength);
		calcPixel(resizedImagePathName, inputUnitCount, pixelValues);
		double[] hiddenUnits = new double[hiddenUnitCount];
		double[] outputUnits = new double[outputUnitCount];
		calcHiddenLayer(hiddenInputWeights, outputHiddenWeights, pixelValues, inputUnitCount, hiddenUnitCount, hiddenUnits, outputUnits);
		calcOutputLayer(hiddenUnits, outputUnits, outputUnitCount, hiddenUnitCount, outputHiddenWeights);
		System.out.printf(" %s has been detected in image : %s",determineFire(outputUnits), imagePathName );
	}
	
	public static void readNetworkVariables(File file) throws IOException {
		BufferedReader reader=new BufferedReader(new FileReader(file));
		String line;
		for (int i = 0; i < 5; i++) {
			line = reader.readLine();
			if (i == 0) {
				sideLength = Integer.parseInt(line);
			} else if(i == 1) {
				hiddenUnitCount = Integer.parseInt(line);
			} else if(i == 2) {
				hiddenLayerCount = Integer.parseInt(line);
			} else if(i == 3) {
				outputUnitCount = Integer.parseInt(line);
			} else {
				fireValue = Integer.parseInt(line);
			}
		}
		inputUnitCount = ((sideLength * sideLength) * 3) + 1;
		hiddenInputWeights = new double[hiddenUnitCount][inputUnitCount];
		for (int i = 0; i < hiddenUnitCount; i++) {
			line = reader.readLine();
			List<String> inputList = Arrays.asList(line.split(","));
			for (int j = 0; j < inputUnitCount; j++) {
				double number = Double.parseDouble(inputList.get(j));
				hiddenInputWeights[i][j] = number;
			}
		}
		outputHiddenWeights = new double[outputUnitCount][hiddenUnitCount];
		for (int i = 0; i < outputUnitCount; i++) {
			line = reader.readLine();
			List<String> outputList = Arrays.asList(line.split(","));
			for (int j = 0; j < hiddenUnitCount; j++) {
				double number = Double.parseDouble(outputList.get(j));
				outputHiddenWeights[i][j] = number;
			}
		}
	}
	
	// Pixel values are put through hidden layer to calculate the hidden units
	public static void calcHiddenLayer(double[][] hiddenInputWeights, double[][] outputHiddenWeights,
            double[] pixelValues, int inputUnitCount, int hiddenUnitCount, double[] hiddenUnits, double[] outputUnits) {
		double sum = 0.0;
        String word = "hidden";
        hiddenUnits[0] = 1.0;
        for (int i = 1; i < hiddenUnitCount; i++) {
            for (int j = 0; j < inputUnitCount; j++) {
                sum += ((double) pixelValues[j] * hiddenInputWeights[i][j]);
            }
            sigmoid(sum, word, hiddenUnits, outputUnits, i);
            sum = 0.0;
        }
	}
	
	// Hidden units are put through output layer to calculate fire/no fire classification
	 public static void calcOutputLayer(double[] hiddenUnits, double[] outputUnits, int outputCount, int hiddenCount,
        double[][] outputHiddenWeights) {
		double sum = 0.0;
		String word = "output";
		for (int i = 0; i < outputCount; i++) { // 2
			for (int j = 0; j < hiddenCount; j++) { // 32
				sum += (hiddenUnits[j] * outputHiddenWeights[i][j]);
			}
			sigmoid(sum, word, hiddenUnits, outputUnits, i);
			sum = 0.0;
		}
	 }
		
 	// Function to calculate the sigmoid value for the given pixel/hidden unit
	public static void sigmoid(double number, String word, double[] hiddenUnits, double[] outputUnits, int index) {
        double e = Math.exp(number * -1);
        double result = 1 / (1 + e);
        if (word.equals("hidden")) {
            hiddenUnits[index] = result;
        } else {
            outputUnits[index] = result;
        }
    }
	
	// Determines classification of image given its output units
	public static String determineFire(double[] outputUnits) {
		//System.out.println(outputUnits[0] + " " + outputUnits[1]);
		if (outputUnits[1] >= outputUnits[0]) {
			return "Fire";
		} else {
			return "No fire";
		}
	}

	// Resizes image to a given uniform size
	private static BufferedImage resizeImageHelper(BufferedImage originalImage, int type, int sideLength) {
        BufferedImage resizedImage = new BufferedImage(sideLength, sideLength, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, sideLength, sideLength, null);
        g.dispose();
        return resizedImage;
    }
	
	// Takes a single image and resizes it a given size 
	public static void resizeImage(File imagePath, String fileName, String newFileName, int sideLength) throws IOException {
		try {
            BufferedImage originalImage = ImageIO.read(new File(fileName));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            /*if (fileName.endsWith("jpg")) {
                BufferedImage resizeImageJpg = resizeImageHelper(originalImage, type, sideLength);
                ImageIO.write(resizeImageJpg, "jpg", new File(newFileName));
            } else if (fileName.endsWith("jpeg")) {
                BufferedImage resizeImageJpeg = resizeImageHelper(originalImage, type, sideLength);
                ImageIO.write(resizeImageJpeg, "jpeg", new File(newFileName));
            } else {
                BufferedImage resizeImagePng = resizeImageHelper(originalImage, type, sideLength);
                ImageIO.write(resizeImagePng, "png", new File(newFileName));
            }*/
		BufferedImage resizeImage = resizeImageHelper(originalImage, type, sideLength);
		ImageIO.write(resizeImage, "jpeg", new File(newFileName));
        } catch (IOException e) {
            System.out.println("line 164 + " + e.getMessage());
        }
   }
	
	// Determines the pixel values from the resized image and stores it in an array
	public static void calcPixel(String resizedImagePathName, int inputUnitCount, double[] pixelValues) throws IOException {
        BufferedImage raw, processed;
        raw = ImageIO.read(new File(resizedImagePathName));
        int width = raw.getWidth();
        int height = raw.getHeight();
        int count = 1;
        pixelValues[0] = 1.0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //this is how we grab the RGBvalue of a pixel
                // at x,y coordinates in the image
                int rgb = raw.getRGB(x, y);
                //extract the red value
                int r = (rgb >> 16) & 0xFF;
                pixelValues[count] = r;
                count++;
                //extract the green value
                int g = (rgb >> 8) & 0xFF;
                pixelValues[count] = g;
                count++;
                //extract the blue value
                int b = rgb & 0xFF;
                pixelValues[count] = b;
                count++;
            }
        }
	}
}