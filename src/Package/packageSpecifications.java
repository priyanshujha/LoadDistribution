/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Package;

/**
 *
 * @author Pk
 */
public class PackageSpecifications {
    private int id;
    private int wt;
    private int length;
    private int breadth;
    private int height;
    private int safetyFactor;

    public int getBreadth() {
        return breadth;
    }

    public void setBreadth(int breadth) {
        this.breadth = breadth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSafetyFactor() {
        return safetyFactor;
    }

    public void setSafetyFactor(int safetyFactor) {
        this.safetyFactor = safetyFactor;
    }

    public int getWt() {
        return wt;
    }

    public void setWt(int wt) {
        this.wt = wt;
    }
}
