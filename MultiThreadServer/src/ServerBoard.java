package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.SwingConstants;
import javax.swing.JSplitPane;

public class ServerBoard extends JFrame {
	private MultiThreds server;
	private JButton btnStart;
	private JButton btnClearDevices;
	private JButton btnClearBalls;
	private JButton btnAddBalls;
	private JTextField serverName;
	private JPanel panel_1;
	private JLabel lblNewLabel;
	private JLabel lblBug;
	private JPanel panel_2;
	private JLabel lblInfo;
	private JPanel panel_3;
	private JLabel lblNumberOfSlots;
	private JTextField nSlots;
	private JLabel lblMin;
	private JPanel panel_4;
	private JTextField min;
	private JLabel lblSec;
	private JTextField sec;
	private JPanel panel_6;

	public ServerBoard() {
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		
		this.setTitle("SERVER");
		
		btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	System.out.println("Server started");
            	btnStart.setEnabled(false);
            	
               	server = new MultiThreds(serverName.getText(), nSlots.getText(), min.getText(), sec.getText());
               	serverName.setEditable(false);
               	nSlots.setEditable(false);
               	min.setEditable(false);
               	sec.setEditable(false);
               	
   				server.update.start();
   				server.deviceUpdate.start();
            }
        });
        
        
        btnClearDevices = new JButton("Clear Devices");
        btnClearDevices.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.out.println("All devices cleared");
				//server.clearBalls();
				server.stopClientThreads();
				server.clearDeviceManeger();
			}
        });
        
        btnClearBalls = new JButton("Clear Balls");
        btnClearBalls.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.out.println("Balls cleared");
				server.clearBalls();
			}
        });
        
        btnAddBalls = new JButton("Add balls");
        btnAddBalls.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				server.addBalls();
			}
        });
        
        btnAddBalls.setEnabled(false);
        
        panel.add(btnStart);
        panel.add(btnClearDevices);
        panel.add(btnClearBalls);
        panel.add(btnAddBalls);
        panel2.setLayout(new GridLayout(0, 1, 0, 0));
        
        panel_1 = new JPanel();
        panel2.add(panel_1);
        panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        lblNewLabel = new JLabel("Server name:");
        panel_1.add(lblNewLabel);
        
        serverName = new JTextField();
        panel_1.add(serverName);
        serverName.setColumns(20);
        
        getContentPane().add(panel);
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        getContentPane().add(panel2);
        
        panel_3 = new JPanel();
        panel2.add(panel_3);
        panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));
        
        panel_6 = new JPanel();
        panel_3.add(panel_6);
        panel_6.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        lblNumberOfSlots = new JLabel("Number of slots:");
        panel_6.add(lblNumberOfSlots);
        
        nSlots = new JTextField();
        panel_6.add(nSlots);
        nSlots.setColumns(2);
        
        panel_4 = new JPanel();
        panel_3.add(panel_4);
        panel_4.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        lblMin = new JLabel("Min:");
        panel_4.add(lblMin);
        
        min = new JTextField();
        min.setColumns(2);
        panel_4.add(min);
        
        lblSec = new JLabel("Sec:");
        panel_4.add(lblSec);
        
        sec = new JTextField();
        sec.setColumns(2);
        panel_4.add(sec);
        
        panel_2 = new JPanel();
        panel2.add(panel_2);
        panel_2.setLayout(new GridLayout(2, 1, 0, 0));
        
        lblInfo = new JLabel(" Info: ");
        panel_2.add(lblInfo);
        
        lblBug = new JLabel(" Bug: 'Clear balls' f\u00F6re 'Clear devices'");
        panel_2.add(lblBug);
        setSize(370,160);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        
	}
	
	   public static void main(String[] args) {
	        //Schedule a job for the event-dispatching thread:
	        //creating and showing this application's GUI.
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                new ServerBoard();
	            }
	        });
	    }
}