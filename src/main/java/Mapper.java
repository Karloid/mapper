import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mapper {

    public static final int MULTIPLIER = 10;
    public static final int WIDTH = 360 * MULTIPLIER;
    public static final int HEIGHT = 180 * MULTIPLIER;
    public static final int ROUND_TO = 20;

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

        g2.drawImage(map, 0, 0, WIDTH, HEIGHT, (img, f, x1, y1, width, height) -> true);

        g2.setColor(Color.red);
        Stream<Coordinate> coordinateStream = Files.lines(file.toPath()).map(Coordinate::new);
        java.util.List<Coordinate> coordinates = coordinateStream.collect(Collectors.toList());
        coordinates.forEach(coordinate -> {
            int x = (int) ((coordinate.getLat()) * MULTIPLIER);
            int y = (int) ((coordinate.getLong()) * MULTIPLIER);
            g2.drawRect(x, HEIGHT - y, 5, 5);
        });

        g2.setColor(Color.green);
        g2.setStroke(new BasicStroke(3));

        HashMap<Coordinate, Integer> block = new HashMap<>();

        coordinates.forEach(c -> {
            c = Coordinate.roundTo(c, ROUND_TO);

            Integer value = block.get(c);
            if (value == null) {
                block.put(c, 1);
            } else {
                block.put(c, 1 + value);
            }
        });

        g2.setColor(Color.blue);
        BasicStroke stroke = new BasicStroke(3);
        g2.setStroke(stroke);
        int fontSize = 5 * MULTIPLIER;
        g2.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

        block.entrySet().forEach(entry -> {
            Coordinate c = entry.getKey();
            int width = ROUND_TO * MULTIPLIER;
            int x = (int) ((c.getLat()) * MULTIPLIER);
            int y = HEIGHT - (int) ((c.getLong()) * MULTIPLIER) - width;
            g2.drawRect(x, y, width, width);
            g2.drawString(entry.getValue() + "", x + fontSize * 0.2f, y + fontSize * 0.8f);
        });

        File outputFile = new File("output.png");
        ImageIO.write(outputImage, "png", outputFile);
        System.out.println("saved output outputImage" + outputFile);
    }
}
