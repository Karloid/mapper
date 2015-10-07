public class Coordinate {
    public final double latitude;
    public final double longitude;

    public Coordinate(String s) {
        String[] split = s.split(",");
        latitude = Double.parseDouble(split[1])+ 180;
        longitude = Double.parseDouble(split[0])+ 90;
    }

    public Coordinate(int latitude, int longitude) {
        this.latitude = latitude ;
        this.longitude = longitude  ;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    public double getLat() {
        return latitude ;
    }

    public double getLong() {
        return longitude;
    }

    public static Coordinate roundTo(Coordinate coordinate, int roundTo) {
        return new Coordinate((int)(coordinate.latitude / roundTo) * roundTo, (int)( coordinate.longitude / roundTo) * roundTo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        return Double.compare(that.longitude, longitude) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
