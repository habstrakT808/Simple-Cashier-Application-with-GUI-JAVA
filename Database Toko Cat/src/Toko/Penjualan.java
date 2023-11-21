/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Toko;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;


/**
 *
 * @author ACER
 */
public class Penjualan extends javax.swing.JFrame {

    String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    String url = "jdbc:sqlserver://localhost\\DESKTOP-CPA7IS4:1433;databaseName=penjualan;encrypt=true;trustServerCertificate=true";
    String user = "sa";
    String password = "Fansspongebobno2";
    String Tanggal;
    private DefaultTableModel model;
    
    public void totalBiaya(){
        int jumlahBaris = jTable1.getRowCount();
        int totalBiaya = 0;
        int jumlahBarang, hargaBarang;
        for (int i = 0; i < jumlahBaris; i++) {
            jumlahBarang = Integer.parseInt(jTable1.getValueAt(i, 3).toString());
            hargaBarang = Integer.parseInt(jTable1.getValueAt(i, 4).toString());
            totalBiaya = totalBiaya + (jumlahBarang * hargaBarang);
        }
        txTotalBayar.setText(String.valueOf(totalBiaya));
        txTampil.setText("Rp "+ totalBiaya +",00");
    }
    
        private void autonumber(){
        try {
            Connection c = koneksi.getKoneksi();
            Statement s = c.createStatement();
            String sql = "SELECT * FROM Penjualan ORDER BY nomorTransaksi DESC";
            ResultSet r = s.executeQuery(sql);
            if (r.next()) {
                String NoFaktur = r.getString("nomorTransaksi").substring(2);
                String TR = "" +(Integer.parseInt(NoFaktur)+1);
                String Nol = "";
                
                if(TR.length()==1)
                {Nol = "000";}
                else if(TR.length()==2)
                {Nol = "00";}
                else if(TR.length()==3)
                {Nol = "0";}
                else if(TR.length()==4)
                {Nol = "";}
                txnomorTransaksi.setText("TR" + Nol + TR);
            } else {
                txnomorTransaksi.setText("TR0001");
            }
            r.close();
            s.close();
        } catch (Exception e) {
            System.out.println("autonumber error");
        }
    }
        
        public void loadData(){
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.addRow(new Object[]{
            txnomorTransaksi.getText(),
            txidBarang.getText(),
            txnamaBarang.getText(),
            txJumlah.getText(),
            txharga.getText(),
            txTotalBayar.getText()
        });
    }
        
        public void kosong(){
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        
        while (model.getRowCount()>0) {
            model.removeRow(0);
        }
    }
        
        public void utama(){
        txnomorTransaksi.setText("");
        txidBarang.setText("");
        txnamaBarang.setText("");
        txharga.setText("");
        txJumlah.setText("");
        autonumber();
    }
        
        public void clear(){
        txidPelanggan.setText("");
        txnamaPelanggan.setText("");
        txTotalBayar.setText("0");
        txBayar.setText("");
        txKembalian.setText("0");
        txTampil.setText("0");
        }
        
         public void clear2(){
        txidBarang.setText("");
        txnamaBarang.setText("");
        txharga.setText("");
        txJumlah.setText("");
    }
         
          public void tambahTransaksi() {
    try {
        String idBarang = txidBarang.getText();
        String namaBarang = txnamaBarang.getText();
        int jumlah = Integer.parseInt(txJumlah.getText());
        int harga = Integer.parseInt(txharga.getText());
        int total = jumlah * harga;

        txTotalBayar.setText(String.valueOf(total));

        loadData();
        totalBiaya();
        txidBarang.requestFocus();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Jumlah dan Harga harus berupa angka");
    }
}
              private void cariData(String kriteria) {
    try {
        Connection c = koneksi.getKoneksi();
        String sql = "SELECT Penjualan.nomorTransaksi, Pelanggan.idPelanggan, Penjualan.tanggal, Penjualan.total, " +
                     "PenjualanRinci.idBarang, PenjualanRinci.jumlah, Barang.namaBarang, Barang.harga " +
                     "FROM Penjualan " +
                     "JOIN PenjualanRinci ON Penjualan.nomorTransaksi = PenjualanRinci.nomorTransaksi " +
                     "JOIN Barang ON PenjualanRinci.idBarang = Barang.idBarang " +
                     "JOIN Pelanggan ON Penjualan.idPelanggan = Pelanggan.idPelanggan " +
                     "WHERE Penjualan.nomorTransaksi LIKE ? OR PenjualanRinci.idBarang LIKE ? OR Pelanggan.idPelanggan LIKE ?";

        PreparedStatement p = c.prepareStatement(sql);

        p.setString(1, "%" + kriteria + "%");
        p.setString(2, "%" + kriteria + "%");
        p.setString(3, "%" + kriteria + "%");

        ResultSet r = p.executeQuery();

        // Bersihkan data pada tabel sebelum menampilkan hasil pencarian
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        while (r.next()) {
            // Tambahkan hasil pencarian ke dalam tabel
            model.addRow(new Object[]{
                    r.getString("nomorTransaksi"),
                    r.getString("idBarang"),
                    r.getString("namaBarang"),
                    r.getString("jumlah"),
                    r.getString("harga"),
                    r.getString("total")
            });
        }

        r.close();
        p.close();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mencari data.");
    }
}
              
        private void deleteData(String nomorTransaksi, String idBarang, String idPelanggan) {
    Connection c = null;
    try {
        c = koneksi.getKoneksi();
        String sqlPenjualan = "DELETE FROM Penjualan WHERE nomorTransaksi=?";
        String sqlPenjualanRinci = "DELETE FROM PenjualanRinci WHERE nomorTransaksi=?";
        String sqlBarang = "DELETE FROM Barang WHERE idBarang=?";
        String sqlPelanggan = "DELETE FROM Pelanggan WHERE idPelanggan=?";

        // Hapus data dari tabel Penjualan
        try (PreparedStatement psPenjualan = c.prepareStatement(sqlPenjualan)) {
            psPenjualan.setString(1, nomorTransaksi);
            psPenjualan.executeUpdate();
        }

        // Hapus data dari tabel PenjualanRinci
        try (PreparedStatement psPenjualanRinci = c.prepareStatement(sqlPenjualanRinci)) {
            psPenjualanRinci.setString(1, nomorTransaksi);
            psPenjualanRinci.executeUpdate();
        }

        // Hapus data dari tabel Barang
        try (PreparedStatement psBarang = c.prepareStatement(sqlBarang)) {
            psBarang.setString(1, idBarang);
            psBarang.executeUpdate();
        }
        System.out.println(idPelanggan);
        // Hapus data dari tabel Pelanggan
        try (PreparedStatement psPelanggan = c.prepareStatement(sqlPelanggan)) {
            psPelanggan.setString(1, idPelanggan);
            psPelanggan.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menghapus data: " + e.getMessage());
    } finally {
        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

      
    public void simpan(){
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        String noTransaksi = txnomorTransaksi.getText();
        String tanggal = txTanggal.getText();
        String idCustomer = txidPelanggan.getText();
        String total = txTotalBayar.getText();
        String IDBarang = txidBarang.getText();
        String jumlah = txJumlah.getText();
        String harga = txharga.getText();
        String namaBarang = txnamaBarang.getText();
        String namaPelanggan = txnamaPelanggan.getText();


try {
    Connection c = koneksi.getKoneksi();
    String sql = "INSERT INTO Barang (idBarang, namaBarang, harga) VALUES (?, ?, ?)";
    PreparedStatement p = c.prepareStatement(sql);
    p.setString(1, IDBarang);  
    p.setString(2, namaBarang);
    p.setString(3, harga);
    p.executeUpdate();
    p.close();
} catch (Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "ID Barang sudah ada di database.");
}

try {
    Connection c = koneksi.getKoneksi();
    String sql = "INSERT INTO Pelanggan (idPelanggan, namaPelanggan) VALUES (?, ?)";
    PreparedStatement p = c.prepareStatement(sql);
    p.setString(1, idCustomer);  
    p.setString(2, namaPelanggan);
    p.executeUpdate();
    p.close();
} catch (Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "ID Customer sudah ada di database.");
}

try {
    Connection c = koneksi.getKoneksi();

    // Mengambil idPelanggan dari baris ke-1 di jTable1
    String idBarangBaru = IDBarang;
    String idPelangganBaru = idCustomer;

    String sql = "INSERT INTO PenjualanRinci (nomorTransaksi, idBarang, idPelanggan, jumlah) VALUES (?, ?, ?, ?)";
    PreparedStatement p = c.prepareStatement(sql);
    p.setString(1, noTransaksi);
    p.setString(2, idBarangBaru);  
    p.setString(3, idPelangganBaru);
    p.setString(4, jumlah);
    p.executeUpdate();
    p.close();

} catch (Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "BERHASIL");
}


    try {
    Connection c = koneksi.getKoneksi();
    String IDPelangganBaru = idCustomer;
    
    String sql = "INSERT INTO Penjualan (nomorTransaksi, idPelanggan, tanggal, total) VALUES (?, ?, ?, ?)";
    PreparedStatement p = c.prepareStatement(sql);
    p.setString(1, noTransaksi);
    p.setString(2, IDPelangganBaru);
    p.setString(3, tanggal);
    p.setString(4, total);
    
    p.executeUpdate();
    p.close();
} catch (Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "BERHASIL");
}
        utama();
        autonumber();
        kosong();
        txTampil.setText("Rp. 0");
    }

         
        
    public Penjualan() {
        initComponents();
    
    
     //Create Table
        model = new DefaultTableModel();
        
        jTable1.setModel(model);
        
        model.addColumn("No Transaksi");
        model.addColumn("ID Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Jumlah");
        model.addColumn("Harga");
        model.addColumn("Total");
        
        utama();
        Date date = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        txTanggal.setText(s.format(date));
        txTotalBayar.setText("0");
        txBayar.setText("");
        txKembalian.setText("0");
        txidPelanggan.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txnomorTransaksi = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txidPelanggan = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txnamaPelanggan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txidBarang = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txnamaBarang = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txJumlah = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txharga = new javax.swing.JTextField();
        txTanggal = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnTambah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        txTampil = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txTotalBayar = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txBayar = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txKembalian = new javax.swing.JTextField();
        btnCariData = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("FORM PENJUALAN - BASIS DATA");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(242, 242, 242))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jLabel2.setText("No Transaksi");

        txnomorTransaksi.setEnabled(false);

        jLabel3.setText("ID Customer");

        txidPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txidPelangganActionPerformed(evt);
            }
        });

        jLabel4.setText("Nama Customer");

        jLabel5.setText("Tanggal");

        jLabel6.setText("ID Barang");

        txidBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txidBarangActionPerformed(evt);
            }
        });

        jLabel7.setText("Nama Barang");

        jLabel8.setText("Harga");

        txJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txJumlahActionPerformed(evt);
            }
        });

        jLabel9.setText("Jumlah");

        txTanggal.setEnabled(false);
        txTanggal.setFocusCycleRoot(true);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        txTampil.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N

        jLabel10.setText("Total Bayar");

        txTotalBayar.setEnabled(false);

        jLabel11.setText("Bayar");

        txBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txBayarActionPerformed(evt);
            }
        });

        jLabel12.setText("Kembalian");

        txKembalian.setEnabled(false);
        txKembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txKembalianActionPerformed(evt);
            }
        });
        
        btnCariData.setText("Cari Data");
        btnCariData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariDataActionPerformed(evt);
            }
        });
        
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

       javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txidBarang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(35, 35, 35)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel7)
                                .addComponent(txnamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(35, 35, 35)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel8)
                                .addComponent(txharga, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(35, 35, 35))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                            .addGap(27, 27, 27)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txidPelanggan)
                                .addComponent(txnomorTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txnamaPelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
                            .addGap(275, 275, 275)
                            .addComponent(jLabel5)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSimpan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txTampil, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel12)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(txBayar, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                                            .addComponent(txTotalBayar))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(txKembalian, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)))
                            .addComponent(jScrollPane1))
                        .addGap(35, 35, 35)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txTanggal)
                    .addComponent(jLabel9)
                    .addComponent(txJumlah, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCariData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txnomorTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txidPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txnamaPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(58, 58, 58)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txidBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txnamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txharga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnTambah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnHapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCariData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnUpdate)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(txBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txTampil, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(txKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 24, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>                        

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
        tambahTransaksi();
        
    }
    
    private void btnCariDataActionPerformed(java.awt.event.ActionEvent evt) {                                            
    // Ambil nilai kriteria pencarian dari user
    String kriteriaPencarian = JOptionPane.showInputDialog(this, "Masukkan kriteria pencarian:");

    if (kriteriaPencarian != null && !kriteriaPencarian.isEmpty()) {
        cariData(kriteriaPencarian);
    } else {
        JOptionPane.showMessageDialog(this, "Kriteria pencarian tidak boleh kosong.");
    }
}



  private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {
    // Minta input idPelanggan dari pengguna
    String idPelanggan = JOptionPane.showInputDialog("Masukkan ID Pelanggan:");

    // Pastikan input tidak kosong atau null
    if (idPelanggan == null || idPelanggan.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "ID Pelanggan tidak boleh kosong");
        return;
    }

    // Mendapatkan nomor transaksi dan idBarang dari tabel
    int row = jTable1.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(null, "Pilih baris yang akan dihapus");
        return;
    }

    String nomorTransaksi = jTable1.getValueAt(row, 0).toString();
    String idBarang = jTable1.getValueAt(row, 1).toString();

    // Menghapus data dari tabel
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.removeRow(row);

    // Menghapus data dari database
    deleteData(nomorTransaksi, idBarang, idPelanggan);

    // Menghitung total biaya
    totalBiaya();
    txBayar.setText("0");
    txKembalian.setText("0");
}




private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
    int selectedRow = jTable1.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih baris yang akan diupdate.");
        return;
    }

    String nomorTransaksi = jTable1.getValueAt(selectedRow, 0).toString();
    String idBarang = jTable1.getValueAt(selectedRow, 1).toString();
    String idPelanggan = jTable1.getValueAt(selectedRow, 2).toString();

    String newIdBarang = txidBarang.getText();
    String newNamaBarang = txnamaBarang.getText();
    String newJumlah = txJumlah.getText();
    String newHarga = txharga.getText();
    String newidPelanggan = txidPelanggan.getText();
    String newnamaPelanggan = txnamaPelanggan.getText();
    String newnomorTransaksi = txnomorTransaksi.getText();
    String newTotal = String.valueOf(Integer.parseInt(newJumlah) * Integer.parseInt(newHarga));

    try {
        Connection c = koneksi.getKoneksi();

        // Periksa apakah newIdBarang ada di tabel PenjualanRinci
        String checkIdBarangSql = "SELECT COUNT(*) FROM PenjualanRinci WHERE idBarang=?";
        String checkIdPelangganSql = "SELECT COUNT(*) FROM Pelanggan WHERE idPelanggan=?";
//        String checknomorTransaksiSql = "SELECT COUNT(*) FROM Penjualan WHERE nomorTransaksi=?";


        try (PreparedStatement psCheckIdBarang = c.prepareStatement(checkIdBarangSql)) {
            psCheckIdBarang.setString(1, newIdBarang);
            
            ResultSet rsCheckIdBarang = psCheckIdBarang.executeQuery();

            if (!rsCheckIdBarang.next()) {
                JOptionPane.showMessageDialog(this, "Tolong Masukan ID Barang");
                return;
            }

            int count = rsCheckIdBarang.getInt(1);

            if (count == 0) {
                JOptionPane.showMessageDialog(this, "ID Barang baru tidak valid.");
                return;
            }
        }
        
        try (PreparedStatement psCheckIdPelanggan = c.prepareStatement(checkIdPelangganSql)) {
            psCheckIdPelanggan.setString(1, newidPelanggan);
            
            ResultSet rsCheckIdPelanggan = psCheckIdPelanggan.executeQuery();

            if (!rsCheckIdPelanggan.next()) {
                JOptionPane.showMessageDialog(this, "ID Customer tidak ada");
                return;
            }

            int count = rsCheckIdPelanggan.getInt(1);

            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Tolong Masukan ID Customer");
                return;
            }
        }
//        try (PreparedStatement psChecknomorTransaksi = c.prepareStatement(checknomorTransaksiSql)) {
//            psChecknomorTransaksi.setString(1, newnomorTransaksi);
//            
//            ResultSet rsChecknomorTransaksi = psChecknomorTransaksi.executeQuery();
//
//            if (!rsChecknomorTransaksi.next()) {
//                JOptionPane.showMessageDialog(this, "Gagal memeriksa Nomor Transaksi baru.");
//                return;
//            }
//
//            int count = rsChecknomorTransaksi.getInt(1);
//
//            if (count == 0) {
//                JOptionPane.showMessageDialog(this, "Nomor Transaksi baru tidak valid.");
//                return;
//            }
//        }
        
        // Update data di tabel Pelanggan jika ID pelanggan berubah
        
        String updatePelangganSql = "UPDATE Pelanggan SET idPelanggan=?, namaPelanggan=? WHERE idPelanggan=?";
            try (PreparedStatement psPelanggan = c.prepareStatement(updatePelangganSql)) {
                psPelanggan.setString(1, newidPelanggan);
                psPelanggan.setString(2, newnamaPelanggan);
                psPelanggan.setString(3, idPelanggan);
                psPelanggan.executeUpdate();
            }
            
        String updateBarangSql = "UPDATE Barang SET idBarang=?, namaBarang=?,harga=? WHERE idBarang=?";
            try (PreparedStatement psBarang = c.prepareStatement(updateBarangSql)) {
                psBarang.setString(1, newIdBarang);
                psBarang.setString(2, newNamaBarang);
                psBarang.setString(3,newHarga);
                psBarang.setString(4, idBarang);
                psBarang.executeUpdate();
            }

        // Update data di tabel Penjualan
        String updatePenjualanSql = "UPDATE Penjualan SET idPelanggan=?, total=? WHERE nomorTransaksi=?";
        try (PreparedStatement psPenjualan = c.prepareStatement(updatePenjualanSql)) {
            psPenjualan.setString(1, newidPelanggan);
            psPenjualan.setString(2, newTotal);
            psPenjualan.setString(3, nomorTransaksi);
            psPenjualan.executeUpdate();
        }

        // Update data di tabel PenjualanRinci
        String updatePenjualanRinciSql = "UPDATE PenjualanRinci SET idBarang=?, idPelanggan=?, jumlah=? WHERE idBarang=?";
        try (PreparedStatement psPenjualanRinci = c.prepareStatement(updatePenjualanRinciSql)) {
            psPenjualanRinci.setString(1, newIdBarang);
            psPenjualanRinci.setString(2, newidPelanggan);
            psPenjualanRinci.setString(3, newJumlah);
            psPenjualanRinci.setString(4, idBarang);
            psPenjualanRinci.executeUpdate();
        }


        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setValueAt(newIdBarang, selectedRow, 1);
        model.setValueAt(newNamaBarang, selectedRow, 2);
        model.setValueAt(newJumlah, selectedRow, 3);
        model.setValueAt(newHarga, selectedRow, 4);
        model.setValueAt(newTotal, selectedRow, 5);

        totalBiaya();

        JOptionPane.showMessageDialog(this, "Data berhasil diupdate.");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengupdate data:\n" + e.getMessage());
    }
}






// Fungsi untuk mendapatkan harga dari PenjualanRinci berdasarkan idBarang
private int getHargaByIdBarang(String idBarang) {
    try {
        Connection c = koneksi.getKoneksi();
        String sql = "SELECT harga FROM PenjualanRinci WHERE idBarang = ?";
        PreparedStatement p = c.prepareStatement(sql);
        p.setString(1, idBarang);
        ResultSet r = p.executeQuery();

        if (r.next()) {
            return r.getInt("harga");
        }

        r.close();
        p.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

    return -1; // Mengembalikan -1 jika data tidak ditemukan
}
    private void txBayarActionPerformed(java.awt.event.ActionEvent evt) {                                        
        // TODO add your handling code here:
        int total, bayar, kembalian;
        
        total = Integer.valueOf(txTotalBayar.getText());
        bayar = Integer.valueOf(txBayar.getText());
        
        if (total > bayar) {
            JOptionPane.showMessageDialog(null, "Uang tidak cukup untuk melakukan pembayaran");
        } else {
            kembalian = bayar - total;
            txKembalian.setText(String.valueOf(kembalian));
        }
    }    
    
    

    private void txKembalianActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void txidPelangganActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void txidBarangActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void txJumlahActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
        tambahTransaksi();
    }                                        

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {                                          
        simpan();
    }
         

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
   

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Penjualan().setVisible(true);
            }
        });
    }
    
    

    // Variables declaration - do not modify  
    private javax.swing.JButton btnCariData;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txBayar;
    private javax.swing.JTextField txharga;
    private javax.swing.JTextField txidBarang;
    private javax.swing.JTextField txidPelanggan;
    private javax.swing.JTextField txJumlah;
    private javax.swing.JTextField txKembalian;
    private javax.swing.JTextField txnamaBarang;
    private javax.swing.JTextField txnamaPelanggan;
    private javax.swing.JTextField txnomorTransaksi;
    private javax.swing.JTextField txTampil;
    private javax.swing.JTextField txTanggal;
    private javax.swing.JTextField txTotalBayar;
    // End of variables declaration                   
}

