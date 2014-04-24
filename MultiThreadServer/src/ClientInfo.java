import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;


public class ClientInfo {

	JFrame clientInfo;
	JPanel pnlIncomingPackages;
	JPanel pnlSentPackages;
	JScrollPane scrollPaneIncomingPackages;
	JScrollPane scrollPaneSentPackages;
	
	final String ip;
	
	
	public ClientInfo(final String ip) {
		this.ip = ip;
	}

	public void createWindow() {
		clientInfo = new JFrame();
		clientInfo.setTitle("Device: " + ip);
		clientInfo.setVisible(true);
		clientInfo.setSize(800,400);
		clientInfo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientInfo.getContentPane().setLayout(new BoxLayout(clientInfo.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlInfo = new JPanel();
		panel.add(pnlInfo, BorderLayout.NORTH);
		pnlInfo.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblIp = new JLabel("Ip: ");
		pnlInfo.add(lblIp);
		
		JLabel lblState = new JLabel("State:");
		pnlInfo.add(lblState);
		clientInfo.getContentPane().add(panel);
		
		JPanel pnlPackagesWrapper = new JPanel();
		panel.add(pnlPackagesWrapper, BorderLayout.CENTER);
		pnlPackagesWrapper.setLayout(new GridLayout(0, 2, 0, 0));
		
		pnlIncomingPackages = new JPanel();
		
		scrollPaneIncomingPackages = new JScrollPane(pnlIncomingPackages);
		scrollPaneIncomingPackages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		pnlPackagesWrapper.add(scrollPaneIncomingPackages);
		pnlIncomingPackages.setLayout(new BoxLayout(pnlIncomingPackages, BoxLayout.Y_AXIS));
		
		JLabel lblIncomingPackages = new JLabel("Incoming Packages: ");
		lblIncomingPackages.setForeground(new Color(255, 0, 0));
		pnlIncomingPackages.add(lblIncomingPackages);
		
		pnlSentPackages = new JPanel();
		
		scrollPaneSentPackages = new JScrollPane(pnlSentPackages);
		scrollPaneSentPackages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		pnlPackagesWrapper.add(scrollPaneSentPackages);
		pnlSentPackages.setLayout(new BoxLayout(pnlSentPackages, BoxLayout.Y_AXIS));
		
		JLabel lblSentPackages = new JLabel("Sent Packages:");
		lblSentPackages.setForeground(Color.RED);
		pnlSentPackages.add(lblSentPackages);
	}
	
	public void addIncomingPackageItem(final String item) {
		pnlIncomingPackages.add(new JLabel(item), 1);
		clientInfo.revalidate();
	}
	
	public void addSentPackageItem(final String item) {
		pnlSentPackages.add(new JLabel(item), 1);
		clientInfo.revalidate();
	}
	
	public void closeWindow() {
		clientInfo.setVisible(false);
		clientInfo.dispose();
	}
}
