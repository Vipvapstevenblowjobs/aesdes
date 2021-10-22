/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desrod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 *
 * @author Rod
 */
public class des extends javax.swing.JFrame {

    /**
     * Creates new form des
     */
    public des() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle(" DES");
    }
    
    File ficheroACifrar = null;
    JFileChooser ficheros = new JFileChooser();
    boolean usarDES = true;
    
    
    boolean verificarFicheroYContrasena(){
        if (ficheroACifrar == null) {
            return false;
        }
        if (!ficheroACifrar.exists()) {
            return false;
        }
        //La verificacion de contraseña solo aplica para AES, ya que DES genera una
        return true;
    }
    
 
    void DesCifrarDES(boolean estaDescifrando){
        if (verificarFicheroYContrasena()) {
            DES cifradoDES = new DES();
            if (estaDescifrando) {
                JOptionPane.showMessageDialog(null,cifradoDES.decodificar(ficheroACifrar, cont.getText()));
                //salida.setText(null);
                //salida.setText("");
                //salida.setText(salida.getText() + cifradoDES.decodificar(ficheroACifrar, cont.getText()) + "\n");
                // "La contraseña es: " + claveString + "\n";
            }
            else{
                JOptionPane.showMessageDialog(null,cifradoDES.codificar(ficheroACifrar));
            }
        }
        else{
            JOptionPane.showMessageDialog(null,"Asegurese de haber seleccionado un archivo\n"
                                            + "Asegurese que la contraseña es de 16, 24 o 32 caracteres para AES\n"
                                            + "Asegurese que la contraseña solo contiene estos caracteres (AES):\n"
                                            + "     Números, letras y .-\n");
        }
    }
    
    public class DES {
        KeyGenerator generadorDES;
        SecretKey clave;
        Cipher cifrador;

        FileOutputStream out = null;
        FileInputStream in = null;
        int bytesleidos;
        byte[] buffer = new byte[1000];
        byte[] bufferCifrado;
        byte[] bufferPlano;

        public String codificar(File fichero){
            try{
                generadorDES = KeyGenerator.getInstance("DES");
                generadorDES.init(56);
                clave = generadorDES.generateKey();
                cifrador = Cipher.getInstance("DES/ECB/PKCS5Padding");
                System.out.println("la clave es: " + clave);
                mostrarBytes(clave.getEncoded());
                System.out.println("");
                System.out.println("Clave codificada " + clave.getEncoded());
                System.out.println();
                cifrador.init(Cipher.ENCRYPT_MODE, clave);
                in = new FileInputStream(fichero);
                File cifrado = new File(fichero.getParent() + "\\" + fichero.getName() + ".codifDES");
                cifrado.createNewFile();
                out = new FileOutputStream(cifrado);
                bytesleidos = in.read(buffer, 0, 1000);
                while(bytesleidos != -1){
                    bufferCifrado  = cifrador.update(buffer, 0, bytesleidos);
                    out.write(bufferCifrado);
                    bytesleidos = in.read(buffer, 0, bytesleidos);
                }
                
                bufferCifrado = cifrador.doFinal();
                out.write(bufferCifrado);
                in.close();
                out.close();

                String claveString = Base64.getEncoder().encodeToString(clave.getEncoded());
                
                //JOptionPane.showMessageDialog(null,"El resultado esta en: " + (cifrado.getPath() + "\n")+ "La contraseña es: " + claveString + "\n");
                
                salida.setText(null);
                salida.setText("");
                salida.setText(salida.getText() + claveString);
              
                return "El resultado esta en: " + (cifrado.getPath() + "\n") + "La contraseña es: " + claveString + "\n";
                //
                //_______________________________________________________
                //
                //------------------------
                //
                //-------------------------
            }
            catch(Exception e){
                try {
                    in.close();
                    out.close();
                } catch (Exception ex1) {System.out.println("Error");}
                
                JOptionPane.showMessageDialog(null,"Error, intente de nuevo\n"
                     + "Asegurese que los archivos existen\n");
                return null;
            }
        }

        public String decodificar(File fichero, String clave){
            try {
                generadorDES = KeyGenerator.getInstance("DES");
                generadorDES.init(56);
                cifrador = Cipher.getInstance("DES/ECB/PKCS5Padding");
                
                byte[] claveDecodificada = Base64.getDecoder().decode(clave);
                SecretKey claveOriginal = new SecretKeySpec(claveDecodificada, 0, claveDecodificada.length, "DES");
                cifrador.init(Cipher.DECRYPT_MODE, claveOriginal);

                in = new FileInputStream(fichero);
            
                File descifrado = new File(fichero.getParent() + "\\" + fichero.getName() + ".decoDES");
                out = new FileOutputStream(descifrado);
            
                bytesleidos = in.read(buffer, 0, 1000);
                while(bytesleidos != -1){
                    bufferPlano  = cifrador.update(buffer, 0, bytesleidos);
                    out.write(bufferPlano);
                    bytesleidos = in.read(buffer, 0, bytesleidos);
                }
                  
                bufferPlano = cifrador.doFinal();
                out.write(bufferPlano);
                in.close();
                out.close();
                
                return "El resultado esta en: " + (descifrado.getPath() + "\n");
            } catch (Exception ex) {
                try {
                    in.close();
                    out.close();
                } catch (Exception ex1) {System.out.println("murio");}
                return "Error, intente de nuevo\n"
                     + "Asegurese que los archivos existen\n"
                     + "Aegurese que la contraseña es correcta\n";
            }
        }

        void mostrarBytes(byte[] buffer) {
            //que este metodo nos va a convertir los archivos en bytes
            System.out.write(buffer, 0, buffer.length);
        }
    }
    
    public class AES {
    
    SecretKeySpec key;
    Cipher cipher;
    FileOutputStream out = null;
    FileInputStream in = null;
    
    String codificar(File fichero, String llavesimetrica) {
        try {
            cipher = Cipher.getInstance("AES");
            key = new SecretKeySpec(llavesimetrica.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            File cifrado = new File(fichero.getParent() + "\\" + fichero.getName() + ".codiAES");
            in = new FileInputStream(fichero);
            cifrado.createNewFile();
            out = new FileOutputStream(cifrado);
            
            out.write(cipher.doFinal(/*in.readAllBytes()*/));
            in.close();
            out.close();
            
            return "El resultado esta en:" + (cifrado.getPath()) + "\n";
        } catch (Exception ex) {
            try {
                in.close();
                out.close();
            } catch (IOException ex1) {System.out.println("murio");}
            return "Error, intente de nuevo\n"
                 + "Asegurese que los archivos existen\n"
                 + "Aegurese que la contraseña es correcta\n";
        }
    }
    
    String decodificar(File fichero, String llavesimetrica){
        try {
            cipher = Cipher.getInstance("AES");
            key = new SecretKeySpec(llavesimetrica.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            File descifrado = new File(fichero.getParent() + "\\" + fichero.getName() + ".decoAES");
            in = new FileInputStream(fichero);
            descifrado.createNewFile();
            out = new FileOutputStream(descifrado);
            out.write(cipher.doFinal(/*in.readAllBytes()*/));
            in.close();
            out.close();
        
            return "El resultado esta en:" + (descifrado.getPath()) + "\n";
        } catch (Exception ex) {
            try {
                in.close();
                out.close();
            } catch (IOException ex1) {System.out.println("murio");}
            return "Error, intente de nuevo\n"
                 + "Asegurese que los archivos existen\n"
                 + "Aegurese que la contraseña es correcta\n";
        }
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fichero = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cont = new javax.swing.JTextField();
        cifrar = new javax.swing.JButton();
        descifrar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        salida = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(153, 204, 0));
        setMaximumSize(new java.awt.Dimension(300, 500));
        setMinimumSize(new java.awt.Dimension(300, 500));
        setResizable(false);

        fichero.setBackground(new java.awt.Color(255, 255, 255));
        fichero.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        fichero.setForeground(new java.awt.Color(255, 255, 255));
        fichero.setText("Seleccionar archivo");
        fichero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ficheroActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel2.setText("Introduccir Contraseña");

        cont.setForeground(new java.awt.Color(255, 255, 255));
        cont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contActionPerformed(evt);
            }
        });

        cifrar.setBackground(new java.awt.Color(255, 255, 255));
        cifrar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cifrar.setForeground(new java.awt.Color(255, 255, 255));
        cifrar.setText("Cifrar");
        cifrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cifrarActionPerformed(evt);
            }
        });

        descifrar.setBackground(new java.awt.Color(255, 255, 255));
        descifrar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        descifrar.setForeground(new java.awt.Color(255, 255, 255));
        descifrar.setText("Descifrar");
        descifrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descifrarActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Castellar", 0, 40)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Cifrado DES");

        jLabel3.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Cifrado DES");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(312, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(138, 138, 138)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel4.setText("Tu contraseña es:");

        salida.setBackground(new java.awt.Color(153, 204, 0));
        salida.setForeground(new java.awt.Color(255, 255, 255));
        salida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salidaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel5.setText("Selecciona un Archivo:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fichero, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cifrar, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                            .addComponent(salida))
                        .addGap(85, 85, 85)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cont, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                            .addComponent(jLabel2)
                            .addComponent(descifrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fichero, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cont, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(84, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cifrar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(salida, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(descifrar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27))))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ficheroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ficheroActionPerformed
        int ficheroCorrecto = ficheros.showOpenDialog(this);
        if (ficheroCorrecto == JFileChooser.APPROVE_OPTION) {
            ficheroACifrar = ficheros.getSelectedFile();
            fichero.setText("Fichero seleccionado: " + ficheroACifrar.getName());
        }
        else{
            fichero.setText("Seleccionar fichero");
        }
    }//GEN-LAST:event_ficheroActionPerformed

    private void cifrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cifrarActionPerformed
        if (usarDES) {
            DesCifrarDES(false);
        }
        else{
            //DesCifrarAES(false);
        }
    }//GEN-LAST:event_cifrarActionPerformed

    private void descifrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descifrarActionPerformed
        if (usarDES) {
            DesCifrarDES(true);
        }
        else{
            //DesCifrarAES(true);
        }
    }//GEN-LAST:event_descifrarActionPerformed

    private void contActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contActionPerformed

    private void salidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salidaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_salidaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cifrar;
    private javax.swing.JTextField cont;
    private javax.swing.JButton descifrar;
    private javax.swing.JButton fichero;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField salida;
    // End of variables declaration//GEN-END:variables
}
