package minegunaydin.nesneler;

import minegunaydin.KarincaOptimizasyonu;

import java.util.ArrayList;
import java.util.List;

public class Yol {

    private List<Sehir> sehiler;
    private double mesafe;

    public Yol(List<Sehir> sehiler, double mesafe) {
        this.sehiler = sehiler;
        this.mesafe = mesafe;
    }

    public List<Sehir> getSehiler() {
        return sehiler;
    }

    public void setSehiler(List<Sehir> sehiler) {
        this.sehiler = sehiler;
    }

    public double getMesafe() {
        return mesafe;
    }

    public void setMesafe(double mesafe) {
        this.mesafe = mesafe;
    }
}
