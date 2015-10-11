import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mapper {

    public static final int MULTIPLIER = 10;
    public static final int WIDTH = 360 * MULTIPLIER;
    public static final int HEIGHT = 180 * MULTIPLIER;
    public static final int ROUND_TO = 20;
    public static final Color ANDROID_COLOR = new Color(0, 203, 0);
    public static final Color STRING_BACKGROUND_COLOR = new Color(188, 201, 255, 144);
    public static final BasicStroke STROKE = new BasicStroke(3);
    public static final BasicStroke ZERO_STROKE = new BasicStroke(0);

    public void start() throws IOException {
        System.out.println("Start Mapper");
        java.util.List<Coordinate> coordinates = getCoordinates("locations.csv");
        java.util.List<Coordinate> coordinatesAndroid = getCoordinates("android_users.csv");

        BufferedImage map = ImageIO.read(new File("map.png"));

        BufferedImage outputImage =
                new BufferedImage(WIDTH, HEIGHT,
                        BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = outputImage.createGraphics();

        g2.drawImage(map, 0, 0, WIDTH, HEIGHT, (img, f, x1, y1, width, height) -> true);

        drawCoordinates(coordinates, g2, Color.red);
        drawCoordinates(coordinatesAndroid, g2, ANDROID_COLOR);

        g2.setStroke(new BasicStroke(3));

        drawBlocks(coordinates, g2, false);
        drawBlocks(coordinatesAndroid, g2, true);

        File outputFile = new File("output.png");
        ImageIO.write(outputImage, "png", outputFile);
        System.out.println("saved output outputImage " + outputFile);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void drawBlocks(List<Coordinate> coordinates, Graphics2D g2, boolean isAndroid) {
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

        Color color = isAndroid ? ANDROID_COLOR : Color.blue;
        g2.setColor(color);
        g2.setStroke(STROKE);
        int fontSize = 5 * MULTIPLIER;
        g2.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

        block.entrySet().forEach(entry -> {
            Coordinate c = entry.getKey();
            int width = ROUND_TO * MULTIPLIER;
            int x = (int) ((c.getLat()) * MULTIPLIER);
            int y;
            if (isAndroid) {
                y = HEIGHT - (int) ((c.getLong()) * MULTIPLIER);
            } else {
                y = HEIGHT - (int) ((c.getLong()) * MULTIPLIER) - width;
            }
            if (!isAndroid)
                g2.drawRect(x, y, width, width);

            int stringX = (int) (x + fontSize * 0.2f);
            int stringY = (int) (y + fontSize * (!isAndroid ? 0.8f : -0.2f));

            String string = entry.getValue() + "";
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D stringBounds = fm.getStringBounds(string, g2);
            stringBounds.setRect(0, 0, stringBounds.getWidth(), stringBounds.getHeight() * 0.6f);
            g2.setColor(STRING_BACKGROUND_COLOR);
            if (!string.isEmpty())
                g2.fillRect(stringX, stringY - (int) stringBounds.getHeight(), (int) stringBounds.getWidth(), (int) stringBounds.getHeight());

            g2.setColor(color);
            g2.drawString(string, stringX, stringY);
        });
    }

    private void drawCoordinates(List<Coordinate> coordinates, Graphics2D g2, Color color) {
        g2.setColor(color);

        int fontSize = 15;
        g2.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

        coordinates.forEach(coordinate -> {
            int x = (int) ((coordinate.getLat()) * MULTIPLIER);
            int y = HEIGHT - (int) ((coordinate.getLong()) * MULTIPLIER);
            g2.drawRect(x, y, 5, 5);
            if (coordinate.deviceName != null) {
                g2.setStroke(STROKE);
                g2.drawString(coordinate.deviceName, x, y);
                g2.setStroke(ZERO_STROKE);
                g2.setColor(color);
            }
        });
    }

    private List<Coordinate> getCoordinates(String fileName) throws IOException {
        File file = new File(fileName);
        boolean fileExist = file.exists();
        if (!fileExist) {
            throw new RuntimeException("File " + file + " not found!");
        }
        Stream<Coordinate> coordinateStream = Files.lines(file.toPath()).map(Coordinate::new);
        return coordinateStream.collect(Collectors.toList());
    }
}
