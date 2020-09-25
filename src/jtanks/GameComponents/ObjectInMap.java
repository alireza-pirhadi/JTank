package jtanks.GameComponents;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Objects;

/**
 * A class containing anything that can interact in playground (map)
 */
public abstract class ObjectInMap implements Serializable {

    public String identifier;

    //Location Stuff
    public Point center;
    public Polygon polygon;
    private Polygon polygonBackup;//A copy of Polygon used for rotating
    public transient BufferedImage bodyImage;
    public double theta;//Body image rotating angle (it is calculated in subclasses if necessary

    //Logic Stuff
    public boolean isBlocking;//Whether anything can go in it or not (if something is not blocking , it is just like a background)
    public boolean isBulletBlocking;//Whether bullet can go in it or not


    ObjectInMap(@NotNull Point center) {
        //Location Stuff
        this.center = center;
        theta = 0D;

        //Logic stuff
        isBlocking = true;
        isBulletBlocking = true;
        identifier = "ObjectInMap" + Math.random();
    }

    /**
     * Sets the top left and down right  point
     * <p>
     * SHOULD be called after changing picture in subclasses
     */
    void setPoints() {
        polygon = new Polygon();
        polygon.addPoint(center.x - bodyImage.getWidth() / 2, center.y - bodyImage.getHeight() / 2);
        polygon.addPoint(center.x + bodyImage.getWidth() / 2, center.y - bodyImage.getHeight() / 2);
        polygon.addPoint(center.x + bodyImage.getWidth() / 2, center.y + bodyImage.getHeight() / 2);
        polygon.addPoint(center.x - bodyImage.getWidth() / 2, center.y + bodyImage.getHeight() / 2);
        polygonBackup = new Polygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
    }

    void move(int dx, int dy) {
        center.translate(dx, dy);
        polygon.translate(dx, dy);
        polygonBackup.translate(dx, dy);
    }

    Rectangle2D getBounds() {
        return polygon.getBounds2D();
    }

    //Adapter Methods :

    /**
     * Used When Anything with damage has hit this
     *
     * @param amount amount of damage
     */
    void hit(int amount) {

    }

    /**
     * Used when this has hit something
     */
    void hit() {
    }

    /**
     * Used When this has hit something in currentMap
     *
     * @param object what This has Hit
     */
    protected void hit(ObjectInMap object) {
    }

    void rotate(double theta) {
        AffineTransform transform = new AffineTransform();
        Point2D tmp;
        transform.rotate(theta, center.x, center.y);
        for (int i = 0; i < 4; i++) {
            tmp = transform.transform(new Point(polygonBackup.xpoints[i], polygonBackup.ypoints[i]), null);
            polygon.xpoints[i] = ((int) tmp.getX());
            polygon.ypoints[i] = ((int) tmp.getY());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectInMap)) return false;
        ObjectInMap that = (ObjectInMap) o;
        return this.identifier.equals(that.identifier);
    }

    void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public static boolean intersects(Polygon polygon1, Polygon polygon2) {
        Area area = new Area(polygon1);
        area.intersect(new Area(polygon2));
        return !area.isEmpty();
    }

    public boolean intersects(Polygon polygon) {
        Area area = new Area(polygon);
        area.intersect(new Area(this.polygon));
        return !area.isEmpty();
    }

    /**
     * set Location of {@code this} based on input
     * used to set initial location of player when going to another map
     * @param object the object which location is used by this
     */
    public void setLocation(ObjectInMap object) {
        if (object == null) {
            System.err.println("Null for Set Location");
            return;
        }
        center = ((Point) object.center.clone());
        polygonBackup = new Polygon(object.polygonBackup.xpoints, object.polygonBackup.ypoints, object.polygonBackup.npoints);
        polygon = new Polygon(object.polygon.xpoints, object.polygon.ypoints, object.polygon.npoints);
        theta = object.theta;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public String toString() {
        return ' '+identifier;
    }

    /**
     * initializes points and images and logic stuff if needed
     */
    protected abstract void init();

    /**
     * For loading images
     * since images are not transferred in co-op mode , this method invocation is essential
     */
    public abstract void loadImages();

}
