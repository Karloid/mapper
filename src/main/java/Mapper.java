import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Mapper {

    public static final int MULTIPLIER = 30;
    public static final int WIDTH = 360 * MULTIPLIER;
    public static final int HEIGHT = 180 * MULTIPLIER;

    public void start() throws IOException {
        System.out.println("Start Mapper");
        File file = new File("locations.csv");
        boolean fileExist = file.exists();
        if (!fileExist) {
            throw new RuntimeException("File " + file + " not found!");
        }

        BufferedImage map = ImageIO.read(new File("map.png"));

        BufferedImage outputImage =
                new BufferedImage(WIDTH, HEIGHT,
                        BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = outputImage.createGraphics();
        g2.setColor(Color.red);

        g2.drawImage(map, 0, 0, WIDTH, HEIGHT, (img, f, x1, y1, width, height) -> true);

        Files.lines(file.toPath()).map(Coordinate::new).forEach(coordinate -> {
            int x = (int) ((coordinate.getLat()) * MULTIPLIER);
            int y = (int) ((coordinate.getLong()) * MULTIPLIER);
            g2.drawRect(x, HEIGHT - y, 5, 5);
        });

        File outputFile = new File("output.png");
        ImageIO.write(outputImage, "png", outputFile);
        System.out.println("saved output outputImage" + outputFile);
    }
}
