package com.example.startendo;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Segment {
    private int color;
    private List<Point> points = new ArrayList();
    private String user;

    private Segment() {
    }

    public Segment(int i, String str) {
        this.color = i;
        this.user = str;
    }

    public void addPoint(int i, int i2) {
        this.points.add(new Point(i, i2));
    }

    public List<Point> getPoints() {
        return this.points;
    }

    public int getColor() {
        return this.color;
    }

    public String getUser() {
        return this.user;
    }
}
