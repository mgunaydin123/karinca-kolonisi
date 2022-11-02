package minegunaydin;

import minegunaydin.nesneler.Sehir;
import minegunaydin.nesneler.SehirSaglayici;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class KarincaOptimizasyonu {

    private AtomicDouble[][] feromonSeviyesiMatrix;
    private double[][] mesafeMatrix;

    private List<Sehir> sehirler = SehirSaglayici.getInstance().getSehirler();
    private int sehirSayisi = sehirler.size();


    public KarincaOptimizasyonu() throws IOException {
        mesafeMatrisiniOlustur();
        feromonSeviyesiMatrixiniOlustur();
    }

    public void mesafeMatrisiniOlustur(){
        mesafeMatrix = new double[sehirSayisi][sehirSayisi];
        IntStream.range(0, sehirSayisi).forEach(i -> {
            Sehir sehir = sehirler.get(i);
            IntStream.range(0, sehirSayisi).forEach(x -> {
                mesafeMatrix[i][x] = SehirSaglayici.getInstance().sehirlerArasiMesafeHesapla(sehir, sehirler.get(x));
            });
        });
    }

    public void feromonSeviyesiMatrixiniOlustur(){
        feromonSeviyesiMatrix = new AtomicDouble[sehirSayisi][sehirSayisi];
        Random random = new Random();
        IntStream.range(0, sehirSayisi).forEach(x -> {
            IntStream.range(0, sehirSayisi).forEach(y -> {
                feromonSeviyesiMatrix[x][y] = new AtomicDouble(random.nextDouble());
            });
        });
    }

    public AtomicDouble[][] getFeromonSeviyesiMatrix() {
        return feromonSeviyesiMatrix;
    }

    public double[][] getMesafeMatrix() {
        return mesafeMatrix;
    }
}
