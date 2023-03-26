package GetData;

public class PreProcessTimeData {
    String cloud;
    String trans;
    String see;
    Double alumMag;
    Double moonAlum;
    String smoke;
    String wind;
    String humidity;
    String temp;

    @Override
    public String toString() {
        return String.format("Cloud:%45s, Trans:%45s, See:%45s, AlumMag:%45f, Moon Alum:%45f, Smoke:%45s, Wind:%45s, Humidity:%45s, Temp:%45s", cloud,trans,see, alumMag, moonAlum, smoke,wind,humidity, temp);
    }

    public String getCloud() {
        return cloud;
    }

    public String getTrans() {
        return trans;
    }

    public String getSee() {
        return see;
    }

    public Double getAlumMag() {
        return alumMag;
    }

    public Double getMoonAlum() {
        return moonAlum;
    }

    public String getSmoke() {
        return smoke;
    }

    public String getWind() {
        return wind;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTemp() {
        return temp;
    }
}
