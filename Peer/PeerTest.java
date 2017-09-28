/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mohit
 */
public class PeerTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Peer peer = new Peer("127.0.0.1",9000);
        peer.startRunning();
        peer.setVisible(true);
    }
    
}
