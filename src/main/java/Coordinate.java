public class Coordinate {
    public final double latitude;
    public final double longitude;

    public Coordinate(String s) {
        String[] split = s.split(",");
        latitude = Double.parseDouble(split[1]);
        longitude = Double.parseDouble(split[0]);
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    public double getLat() {
        return latitude + 180;
    }

    public double getLong() {
        return longitude + 90;
    }
}
