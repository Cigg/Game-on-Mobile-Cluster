import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ServerBoard extends JFrame {
	private MultiThreds server;
	private JButton btnStart;
	private JButton btnClearDevices;
	private JButton btnClearBalls;
	private JButton btnAddBalls;
	private JTextField textBug;
	
	public ServerBoard() {
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel2.setLayout(new BoxLayout(panel2,BoxLayout.X_AXIS));
		
		btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println("Server started");
            	btnStart.setEnabled(false);
               	server = new MultiThreds();
   				server.update.start();
   				server.deviceUpdate.start();
            }
        });
        
        
        btnClearDevices = new JButton("Clear Devices");
        btnClearDevices.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("All devices cleared");
				//server.clearBalls();
				server.stopClientThreads();
				server.clearDeviceManeger();
			}
        });
        
        btnClearBalls = new JButton("Clear Balls");
        btnClearBalls.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Balls cleared");
				server.clearBalls();
			}
        });
        
        btnAddBalls = new JButton("Add balls");
        btnAddBalls.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				server.addBalls();
			}
        });
        
        textBug = new JTextField("Bug: 'Clear balls' före 'Clear devices'");
        
        btnAddBalls.setEnabled(false);
        
        panel.add(btnStart);
        panel.add(btnClearDevices);
        panel.add(btnClearBalls);
        panel.add(btnAddBalls);
        panel2.add(textBug);
        
        getContentPane().add(panel);
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        getContentPane().add(panel2);
        setSize(400,170);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	   public static void main(String[] args) {
	        //Schedule a job for the event-dispatching thread:
	        //creating and showing this application's GUI.
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                ServerBoard serverBoard = new ServerBoard();
	            }
	        });
	    }
}