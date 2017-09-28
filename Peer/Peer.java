import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Peer extends javax.swing.JFrame implements Runnable{
    
    private Socket socket1;
    private InputStream in1;
    private Socket socket2;
    private OutputStream out2;
    private Socket socket3;
    private ObjectOutputStream out3;
    private ObjectInputStream in3;
    private Socket socket4;
    private ObjectOutputStream out4;
    private ObjectInputStream in4;
    private String serverIP;
    private int port;
    private int var;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;

    public Peer(String host,int port) {
        socket1 = null;
        in1 = null;
        socket2 = null;
        out2 = null;
        socket3 = null;
        out3 = null;
        in3 = null;
        socket4 = null;
        out4 = null;
        in4 = null;
        serverIP = host;
        this.port = port;
        var = 0;
        initComponents();
    }
    
    public void startRunning(){
        establishConnection();
        setupStreams();
        shareFiles();
        Thread thread = new Thread(this);
        thread.start();
    }
    
    private void establishConnection(){
        try{
            socket1 = new Socket(serverIP,port);
            socket2 = new Socket(serverIP,port);
            socket3 = new Socket(serverIP,port);
            socket4 = new Socket(serverIP,port);
        }catch(Exception e){
            System.out.println("Unable to establish connection with the server");
        }
    }

    private void setupStreams(){
        try{
            in1 = socket1.getInputStream();
            out2 = socket2.getOutputStream();
            out2.flush();
            out3 = new ObjectOutputStream(socket3.getOutputStream());
            out3.flush();
            in3 = new ObjectInputStream(socket3.getInputStream());
            out4 = new ObjectOutputStream(socket4.getOutputStream());
            out4.flush();
            in4 = new ObjectInputStream(socket4.getInputStream());
        }catch(IOException e){
            System.out.println("Unable to setup the streams");
        }
    }
        
    private void shareFiles(){
        File[] files = new File(".").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                try{
                    out3.writeObject(file.getName());
                    out3.flush();
                }catch(IOException e){
                    System.out.println("File " + file.getName() + " could not be shared");
                }
            }
        }
        try{
            String str = "DONE SHARING FILES";
            out3.writeObject(str);
            out3.flush();
        }catch(IOException e){
            
        }
    }

    private void requestFile(){
        try{
            jTextArea1.setEditable(true);
            jTextArea1.setText(null);
            String str = (String)in3.readObject();
            int x = 0;
            while(!str.equals("DONE SENDING RESULTS")){
                x++;
                jTextArea1.append(x + " " + str + "\n");
                str = (String)in3.readObject();
            }
            var = x;
            jTextArea1.append("\n");
            jTextArea1.append("Choose appropriate option\nIf you don't want to download then enter 0\n");
            jTextArea1.setEditable(false);
        }catch(Exception e){
            System.out.println("Unable to process your request");
        }
    }

    private void receiveFile(){
        try{
            String fname = (String)in3.readObject();
            long len = (Long)in3.readObject();
            File file = new File(fname);
            OutputStream out = new FileOutputStream(file);
            int count;
            byte[] bytes = new byte[16384];
            long sum = 0;
            while ((count = in1.read(bytes)) > 0) {
                sum += count;
                out.write(bytes, 0, count);
                out.flush();
                if(sum==len)
                    break;
            }
        }catch(Exception e){
            System.out.println("Unable to receive file");
        }
    }
    
    public void run(){
        try{
            while(true){
                String fname = (String)in4.readObject();
                sendFile(fname);
            }
        }catch(Exception e){
            System.out.println("Will not be able to send file now");
        }
    }
    
    private void sendFile(String fname){
        try{
            byte[] bytes = new byte[16384];
            File file = new File(fname);
            out4.writeObject(file.length());
            out4.flush();
            InputStream in = new FileInputStream(file);
            int count;
            while ((count = in.read(bytes)) > 0) {
                out2.write(bytes, 0, count);
                out2.flush();
            }
        }catch(Exception e){
            System.out.println("Unsuccessful transfer of " + fname);
        }
    }

    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jTextArea1.setEditable(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton2.setText("Select Option");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String str = this.jTextField1.getText();
        try{
            out3.writeObject(str);
            out3.flush();
            requestFile();
        }catch(IOException e){
            System.out.println("Unable to process your request");
       }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        int num = Integer.parseInt(this.jTextField2.getText());
        if(num > var || num < 0){
            JOptionPane.showMessageDialog(this, "Enter valid number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try{
            out3.writeObject(num);
            out3.flush();
        }catch(IOException e){
            System.out.println("Unable to process your request");
        }
        if(num!=0){
            receiveFile();
        }
    }
}