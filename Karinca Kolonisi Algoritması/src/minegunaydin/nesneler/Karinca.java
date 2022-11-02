package minegunaydin.nesneler;

import minegunaydin.KarincaOptimizasyonu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Karinca implements Callable<Karinca> {

    private static final double Q = 0.0005;
    double RHO = 0.2;
    double ALFA = 0.01;
    double BETA = 9.5;
    private KarincaOptimizasyonu karincaOptimizasyonu;
    private Yol yol = null;
    private int karincaNumarasi;

    static int kullanilmayanSehirIndex = -1;
    static int sehirSayisi = SehirSaglayici.getInstance().getSehirler().size();

    public Karinca(KarincaOptimizasyonu karincaOptimizasyonu, int karincaNumarasi, double ALFA, double BETA, double RHO) {
        this.karincaOptimizasyonu = karincaOptimizasyonu;
        this.karincaNumarasi = karincaNumarasi;
        this.ALFA = ALFA;
        this.BETA = BETA;
        this.RHO = RHO;
    }

    @Override
    public Karinca call() throws Exception {
        int sehirIndex = ThreadLocalRandom.current().nextInt(sehirSayisi);
        List<Sehir> rota = new ArrayList<Sehir>(sehirSayisi);
        HashMap<String, Boolean> ziyaretEdilenSehirler = new HashMap<>(sehirSayisi);
        IntStream.range(0, sehirSayisi).forEach(i -> {
            ziyaretEdilenSehirler.put(SehirSaglayici.getInstance().getSehirler().get(i).getSehirAdi(), false);
        });
        int ziyaretEdilenSehirSayisi = 0;
        ziyaretEdilenSehirler.put(SehirSaglayici.getInstance().getSehirler().get(sehirIndex).getSehirAdi(), true);
        double mesafe = 0;
        int x = sehirIndex;
        int y = kullanilmayanSehirIndex;
        if(ziyaretEdilenSehirSayisi != sehirSayisi)
            y = getY(x, ziyaretEdilenSehirler);
        while(y != kullanilmayanSehirIndex){
            rota.add(ziyaretEdilenSehirSayisi++,  SehirSaglayici.getInstance().getSehirler().get(x));
            mesafe += karincaOptimizasyonu.getMesafeMatrix()[x][y];
            feromonSeviyesiniArttir(x, y, mesafe);
            ziyaretEdilenSehirler.put(SehirSaglayici.getInstance().getSehirler().get(y).getSehirAdi(), true);
            x = y;
            if(ziyaretEdilenSehirSayisi != sehirSayisi)
                y = getY(x, ziyaretEdilenSehirler);
            else
                y = kullanilmayanSehirIndex;
        }
        mesafe += karincaOptimizasyonu.getMesafeMatrix()[x][sehirIndex];
        rota.add(ziyaretEdilenSehirSayisi, SehirSaglayici.getInstance().getSehirler().get(x));
        yol = new Yol(rota, mesafe);
        return this;
    }

    private void feromonSeviyesiniArttir(int x, int y, double mesafe) {
        boolean bayrak = false;
        while(bayrak){
            double guncelFeromonSeviyesi = karincaOptimizasyonu.getFeromonSeviyesiMatrix()[x][y].doubleValue();
            double yeniFeromonSeviyesi = (1 - RHO) * guncelFeromonSeviyesi + Q / mesafe;
            bayrak = (yeniFeromonSeviyesi < 0.00) ? karincaOptimizasyonu.getFeromonSeviyesiMatrix()[x][y].compareAndSet(0) : karincaOptimizasyonu.getFeromonSeviyesiMatrix()[x][y].compareAndSet(yeniFeromonSeviyesi);
        }
    }

    private int getY(int x, HashMap<String, Boolean> ziyaretEdilenSehirler){
        int returnY = kullanilmayanSehirIndex;
        double random = ThreadLocalRandom.current().nextDouble();
        List<Double> rotaOlasiliği = getRotaOlasiligi(x, ziyaretEdilenSehirler);
        for(int y = 0; y < sehirSayisi; y++){
            if(rotaOlasiliği.get(y) > random){
                returnY = y;
                break;
            }else random -= rotaOlasiliği.get(y);
        }
        return returnY;
    }

    private List<Double> getRotaOlasiligi(int x, HashMap<String, Boolean> ziyaretEdilenSehirler){
        List<Double> rotaOlasiligi = new ArrayList<Double>(sehirSayisi);
        IntStream.range(0, sehirSayisi).forEach(i -> {
            rotaOlasiligi.add(0.0);
        });
        double degisim = getRotaOlasiligiDegistirici(rotaOlasiligi, x, ziyaretEdilenSehirler);
        IntStream.range(0, sehirSayisi).forEach(i -> {
            rotaOlasiligi.set(i, rotaOlasiligi.get(i) / degisim);
        });
        return rotaOlasiligi;
    }

    private double getRotaOlasiligiDegistirici(List<Double> rotaOlasiligi, int x, HashMap<String, Boolean> ziyaretEdilenSehirler){
        double degisim = 0;
        for(int y = 0; y < sehirSayisi; y++){
            if(!ziyaretEdilenSehirler.get(SehirSaglayici.getInstance().getSehirler().get(y).getSehirAdi())){
                if(x == y) rotaOlasiligi.set(y, 0.0);
                else rotaOlasiligi.set(y, getRotaOlasiligiNum(x, y));
                degisim += rotaOlasiligi.get(y);
            }
        }
        return degisim;
    }

    private Double getRotaOlasiligiNum(int x, int y) {
        double num = 0;
        double feromonSeviyesi = karincaOptimizasyonu.getFeromonSeviyesiMatrix()[y][x].doubleValue();
        if(feromonSeviyesi != 0.0) num = Math.pow(feromonSeviyesi, ALFA) * Math.pow(1 / karincaOptimizasyonu.getMesafeMatrix()[x][y], BETA);
        return num;
    }

    public Yol getYol() {
        return yol;
    }

    public int getKarincaNumarasi() {
        return karincaNumarasi;
    }

}
