package minegunaydin;

import minegunaydin.nesneler.App;
import minegunaydin.nesneler.Sehir;
import minegunaydin.nesneler.Yol;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Ekran extends JFrame implements UIEvents{
    private JPanel panel1;
    private JScrollPane scrollPane;
    private JTable table1;
    private JTextField txt_karinca_sayisi;
    private JTextField txt_ıterasyon_sayisi;
    private JTextField txt_durdurma_cozum_degeri;
    private JButton başlatButton;
    private JLabel lbl_siralama;
    private JLabel lbl_kisa_mesafe;
    private JLabel lbl_enuzun_mesafe;
    private JPanel canvas;
    private JTextField txt_alfa;
    private JTextField txt_beta;
    private JTextField txt_rho;

    DefaultTableModel daDefaultTableModel;



    public Ekran() {
        Driver driver = new Driver(this);
        setResizable(false);
        add(panel1);
        setSize(App.CERCEVE_GENISLIK,App.CERCEVE_YUKSEKLIK);
        setLocationRelativeTo(null);
        canvas.add(driver);
        setVisible(true);
        setTitle("Mine Günaydın Karınca Kolonisi Algoritması");
        tabloyuDuzenle();


        başlatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                   if(konfigurasyonuAyarla(driver)){
                       tabloyuDuzenle();
                       iterasyon = 1;
                        driver.optimizasyonuBaslat();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    boolean konfigurasyonuAyarla(Driver driver){
        int karincaSayisi = getInt(txt_karinca_sayisi, "Karınca Sayısı");
        if(karincaSayisi == -1){
            return false;
        }

        double alfa = getDouble(txt_alfa, "Alfa");
        if(alfa == -1){
            return false;
        }

        double beta = getDouble(txt_beta, "Beta");
        if(alfa == -1){
            return false;
        }

        double rho = getDouble(txt_rho, "RHO");
        if(alfa == -1){
            return false;
        }

        int maxIterasyonSayisi = getInt(txt_ıterasyon_sayisi, "Max Iterasyon Sayisi ");
        if(maxIterasyonSayisi == -1){
            return false;
        }

        double durdurma_degeri = getDouble(txt_durdurma_cozum_degeri, "Durdurma");
        if(durdurma_degeri == -1){
            return false;
        }

        driver.setKonfigurasyon(karincaSayisi, alfa, beta, rho, maxIterasyonSayisi, durdurma_degeri);

        return true;

    }

    double getDouble(JTextField field, String alanAdi){
        double deger = -1;
        if(field.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null, "Lütfen " + alanAdi + " değerini boş bırakmayın", "Uyarı", JOptionPane.ERROR_MESSAGE);
            return deger;
        }

        try{
            deger =  Double.parseDouble(field.getText().trim());
            if(deger < 0){
                return  -1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return deger;
    }

    int getInt(JTextField field, String alanAdi){
        int deger = -1;
        if(field.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null, "Lütfen " + alanAdi + " değerini boş bırakmayın", "Uyarı", JOptionPane.ERROR_MESSAGE);
            return deger;
        }

        try{
            deger =  Integer.parseInt(field.getText().trim());
            if(deger <= 0){
                JOptionPane.showMessageDialog(null, "Lütfen " + alanAdi + " değerini 0 dan büyük girin.", "Uyarı", JOptionPane.ERROR_MESSAGE);
                return  -1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return deger;
    }


    public static void main(String[] args) {
        new Ekran();
    }


    @Override
    public void rotaCiz(Yol yol) {
        iterasyonEkle(yol);
    }

    @Override
    public void enKısaYoluCiz(Yol yol) {
        enIyıDegeriGoster(yol);
    }

    @Override
    public void enUzunMesafeCiz(Yol yol) {
        enKotuDegeriGoster(yol);
    }

    public void enIyıDegeriGoster(Yol yol){
        lbl_kisa_mesafe.setText(String.format("%.2f", yol.getMesafe()));
        String siralama = "";
        for(Sehir sehir : yol.getSehiler()){
            siralama += sehir.getSehirAdi() + " => \n";
        }

        lbl_siralama.setText(siralama);
    }

    public void enKotuDegeriGoster(Yol yol){
        lbl_enuzun_mesafe.setText(String.format("%.2f", yol.getMesafe()));
    }


    void tabloyuDuzenle(){
        String[] basliklar= {"Iterasyon", "1.Sehir"," 2.Sehir", " 3.Sehir", " 4.Sehir", "5.Şehir", "6.Sehir", "7.Şehir", "8.Şehir", "9.Şehir", "10.Şehir", "Mesafe"};
        daDefaultTableModel = new DefaultTableModel(0, 0);
        daDefaultTableModel.setColumnIdentifiers(basliklar);
        table1.setModel(daDefaultTableModel);

    }
    int iterasyon = 1;
    public void iterasyonEkle(Yol yol){

        daDefaultTableModel.addRow(new Object[] {iterasyon++,
                yol.getSehiler().get(0).getSehirAdi(),
                yol.getSehiler().get(1).getSehirAdi(),
                yol.getSehiler().get(2).getSehirAdi(),
                yol.getSehiler().get(3).getSehirAdi(),
                yol.getSehiler().get(4).getSehirAdi(),
                yol.getSehiler().get(5).getSehirAdi(),
                yol.getSehiler().get(6).getSehirAdi(),
                yol.getSehiler().get(7).getSehirAdi(),
                yol.getSehiler().get(8).getSehirAdi(),
                yol.getSehiler().get(9).getSehirAdi(),
                String.format("%.2f", yol.getMesafe())
        });
    }

}
