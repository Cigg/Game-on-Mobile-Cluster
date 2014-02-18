import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class ServerBoard extends JFrame {
	private JButton startServer;
	private TCPServer tcpServer;
	
	public ServerBoard() {
		super("ServerBoard");
        JPanel panelFields = new JPanel();
        panelFields.setLayout(new BoxLayout(panelFields,BoxLayout.X_AXIS));
 
        JPanel panelFields2 = new JPanel();
        panelFields2.setLayout(new BoxLayout(panelFields2,BoxLayout.X_AXIS));
        
        
        startServer = new JButton("Start");
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // disable the start button
                startServer.setEnabled(false);
 
                //creates the object OnMessageReceived asked by the TCPServer constructor
                tcpServer = new TCPServer();
                tcpServer.start();
 
            }
        });
        
        panelFields.add(startServer);
        getContentPane().add(panelFields);
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        
        setSize(300, 170);
        setVisible(true);
	}
}
