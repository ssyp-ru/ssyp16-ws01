package org.ssyp.theforceawakens.game;

public class Position {
    private double latitude;
    private double longitude;

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void moveTo(Position where) {
        this.latitude = where.latitude;
        this.longitude = where.longitude;
    }

    public float distanceTo(Position another) {
        double dx = this.latitude - another.latitude;
        double dy = this.longitude - another.longitude;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public String toString() {
        return "(" + this.latitude + "," + this.longitude + ")";
    }
}
