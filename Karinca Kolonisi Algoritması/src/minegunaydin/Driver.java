package minegunaydin;

import minegunaydin.nesneler.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Driver extends JPanel implements Runnable{

    private SIMULASYON_DURUMU simulasyon_durumu = SIMULASYON_DURUMU.DURDU;

    private double alfa, beta, rho, durdurma_degeri;
    private int max_iterasyon_sayisi;

    int karincaSayisi;
    double PCR = 0.8;
    public Yol enKısaYol, enUzunYol;
    ExecutorService executorService;
    ExecutorCompletionService<Karinca> executorCompletionService;

    private int activeAnts = 0;

    private UIEvents events;

    public Driver(UIEvents events){
        setLayout(null);
        setBackground(new Color(0, 50, 80));
        this.events = events;
        new Thread(this).start();
    }

    public void setKonfigurasyon(int karincaSayisi, double alfa, double beta, double rho, int max_iterasyon_sayisi, double durdurma_degeri){
        this.karincaSayisi = karincaSayisi;
        this.alfa = alfa;
        this.beta = beta;
        this.rho = rho;
        this.max_iterasyon_sayisi = max_iterasyon_sayisi;
        this.durdurma_degeri = durdurma_degeri;
    }

    public void degerleriSifirla(){
        enKısaYol = null;
        enUzunYol = null;
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executorCompletionService = new ExecutorCompletionService<Karinca>(executorService);
    }


    public void optimizasyonuBaslat() {
        degerleriSifirla();
        simulasyon_durumu = SIMULASYON_DURUMU.BASLADI;
        IntStream.range(0, max_iterasyon_sayisi).forEach(x -> { iterasyonIslemi(); });

        executorService.shutdownNow();
        simulasyon_durumu = SIMULASYON_DURUMU.DURDU;
    }

    private void iterasyonIslemi(){
        try {
            KarincaOptimizasyonu karincaOptimizasyonu = new KarincaOptimizasyonu();

            IntStream.range(0, karincaSayisi).forEach(i -> {
                executorCompletionService.submit(new Karinca(karincaOptimizasyonu, i, alfa, beta, rho));
                activeAnts++;
                if (Math.random() > PCR) processAnt();

                if(enKısaYol.getMesafe() <= durdurma_degeri){
                    executorService.shutdownNow();
                    simulasyon_durumu = SIMULASYON_DURUMU.DURDU;
                    events.rotaCiz(enKısaYol);
                    return;
                }else{
                    events.rotaCiz(enKısaYol);
                }

            });
            processAnt();
        }catch (Exception e){

        }



    }


    private void processAnt(){
        while (activeAnts > 0 ){
            try {
                Karinca karinca = executorCompletionService.take().get();
                Yol yol = karinca.getYol();
                if(enKısaYol == null || yol.getMesafe() < enKısaYol.getMesafe()){
                    enKısaYol = yol;
                    events.enKısaYoluCiz(yol);

                }else if(enUzunYol == null || yol.getMesafe() > enUzunYol.getMesafe()){
                    enUzunYol = yol;
                    events.enUzunMesafeCiz(enUzunYol);
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            activeAnts--;
        }

    }

    @Override
    public void paintComponent(Graphics gfx) {
        super.paintComponent(gfx);
        if(enKısaYol == null) return;
        gfx.setColor(new Color(0, 50, 80));
        gfx.fillRect(0, 0, App.CANVAS_GENISLIK, App.CANVAS_YUKSEKLIK);


        for(Sehir sehir : enKısaYol.getSehiler()){
            sehir.goster(gfx);
        }

        for(int i = 0; i < enKısaYol.getSehiler().size() -1; i++){
            gfx.setColor(Color.WHITE);
            Sehir sehir = enKısaYol.getSehiler().get(i);
            Sehir sonrakiSehir = enKısaYol.getSehiler().get(i +1);
            gfx.drawLine((int) (sehir.getX() + sehir.getDaireBoyutu() / 2), (int)(sehir.getY() + sehir.getDaireBoyutu() / 2),
                    (int) (sonrakiSehir.getX() + sehir.getDaireBoyutu() /2), (int) (sonrakiSehir.getY() + sehir.getDaireBoyutu() / 2));
        }

        Sehir sehir = enKısaYol.getSehiler().get(0);
        Sehir sonrakiSehir = enKısaYol.getSehiler().get(enKısaYol.getSehiler().size() -1);
        gfx.drawLine((int) (sehir.getX() + sehir.getDaireBoyutu() / 2), (int)(sehir.getY() + sehir.getDaireBoyutu() / 2),
                (int) (sonrakiSehir.getX() + sehir.getDaireBoyutu() /2), (int) (sonrakiSehir.getY() + sehir.getDaireBoyutu() / 2));




    }



    @Override
    public void run() {
        while (true) {
            if(simulasyon_durumu == SIMULASYON_DURUMU.BASLADI){
                repaint();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public enum SIMULASYON_DURUMU{
        DURDU, BASLADI
    }
}
