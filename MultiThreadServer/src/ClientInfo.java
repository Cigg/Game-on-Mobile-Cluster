package src;

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

	JFrame winClientInfo;
	JPanel pnlIncomingPackages;
	JPanel pnlSentPackages;
	JLabel lblType;
	JLabel lblClock;
	JLabel lblXDPI;
	JLabel lblYDPI;
	JLabel lblResX;
	JLabel lblResY;
	JLabel lblPosX;
	JLabel lblPosY;
	JLabel lblAngle;
	JLabel lblMidX;
	JLabel lblMidY;

	private final String startMidX = "Position center X: ";
	private final String startMidY = "Position center Y: ";
	private final String endMidX = " [cm]";
	private final String endMidY = " [cm]";
	private final String startType = "Type: ";
	private final String startClock = "From client to server delta time: ";
	private final String startXDPI = "XDPI: ";
	private final String startYDPI = "YDPI: ";
	private final String startResX = "Resolution X: ";
	private final String startResY = "Resolution Y: ";
	private final String startPosX = "Position X: ";
	private final String endPosX = " [cm]";
	private final String endPosY = " [cm]";
	private final String endAngle = " [degrees]";
	private final String startPosY = "Position Y: ";
	private final String startAngle = "Angle: ";
	

	final String ip;
	
	
	public ClientInfo(final String ip) {
		this.ip = ip;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void createWindow() {
		winClientInfo = new JFrame();
		winClientInfo.setTitle("Device: " + ip);
		winClientInfo.setVisible(true);
		winClientInfo.setSize(800,400);
		winClientInfo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		winClientInfo.getContentPane().setLayout(new BoxLayout(winClientInfo.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlInfo = new JPanel();
		panel.add(pnlInfo, BorderLayout.NORTH);
		pnlInfo.setLayout(new GridLayout(0, 2, 0, 0));
		
		lblType = new JLabel(startType);
		pnlInfo.add(lblType);
		
		lblClock = new JLabel(startClock);
		pnlInfo.add(lblClock);
		
		lblXDPI = new JLabel(startXDPI);
		pnlInfo.add(lblXDPI);
		
		lblYDPI = new JLabel(startYDPI);
		pnlInfo.add(lblYDPI);
		
		lblResX = new JLabel(startResX);
		pnlInfo.add(lblResX);
		
		lblResY = new JLabel(startResY);
		pnlInfo.add(lblResY);
		
		lblPosX = new JLabel(startPosX + 0.0 + endPosX);
		pnlInfo.add(lblPosX);
		
		lblPosY = new JLabel(startPosY + 0.0 + endPosY);
		pnlInfo.add(lblPosY);
		
		lblMidX = new JLabel(startMidX + 0.0 + endMidX);
		pnlInfo.add(lblMidX);
		
		lblMidY = new JLabel(startMidY + 0.0 + endMidY);
		pnlInfo.add(lblMidY);
		
		lblAngle = new JLabel(startAngle + 0.0 + endAngle);
		pnlInfo.add(lblAngle);


		winClientInfo.getContentPane().add(panel);
		
		JPanel pnlPackagesWrapper = new JPanel();
		panel.add(pnlPackagesWrapper, BorderLayout.CENTER);
		pnlPackagesWrapper.setLayout(new GridLayout(0, 2, 0, 0));
		
		pnlIncomingPackages = new JPanel();
		
		JScrollPane scrollPaneIncomingPackages = new JScrollPane(pnlIncomingPackages);
		scrollPaneIncomingPackages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		pnlPackagesWrapper.add(scrollPaneIncomingPackages);
		pnlIncomingPackages.setLayout(new BoxLayout(pnlIncomingPackages, BoxLayout.Y_AXIS));
		
		JLabel lblIncomingPackages = new JLabel("Incoming Packages: ");
		lblIncomingPackages.setForeground(new Color(255, 0, 0));
		pnlIncomingPackages.add(lblIncomingPackages);
		
		pnlSentPackages = new JPanel();
		
		JScrollPane scrollPaneSentPackages = new JScrollPane(pnlSentPackages);
		scrollPaneSentPackages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		pnlPackagesWrapper.add(scrollPaneSentPackages);
		pnlSentPackages.setLayout(new BoxLayout(pnlSentPackages, BoxLayout.Y_AXIS));
		
		JLabel lblSentPackages = new JLabel("Sent Packages:");
		lblSentPackages.setForeground(Color.RED);
		pnlSentPackages.add(lblSentPackages);
	}
	
	public void addIncomingPackageItem(final String item) {
		pnlIncomingPackages.add(new JLabel(item), 1);
		winClientInfo.revalidate();
	}
	
	public void addSentPackageItem(final String item) {
		pnlSentPackages.add(new JLabel(item), 1);
		winClientInfo.revalidate();
	}
	
	public void closeWindow() {
		winClientInfo.setVisible(false);
		winClientInfo.dispose();
	}
	
	public void setClock(final float clock) {
		lblClock.setText(startClock + clock);
	}
	
	public void setXDPI(final int XDPI) {
		lblXDPI.setText(startXDPI + XDPI);
	}
	
	public void setYDPI(final int YDPI) {
		lblYDPI.setText(startYDPI + YDPI);
	}
	
	public void setResX(final int resX) {
		lblResX.setText(startResX + resX);
	}
	
	public void setResY(final int resY) {
		lblResY.setText(startResY + resY);
	}
	
	public void setPosX(final float posX) {
		lblPosX.setText(startPosX + posX + endPosX);
	}
	
	public void setPosY(final float posY) {
		lblPosY.setText(startPosY + posY + endPosY);
	}
	
	public void setAngle(final float angle) {
		lblAngle.setText(startAngle + angle + endAngle);
	}

	public void setType(final short type) {
		if(type == 0) {
			lblType.setText(startType + "Main Device (Middle)");
		} else {
			lblType.setText(startType + "Regular Device (Player)");
		}
	}

	public void setMidX(final float midX) {
		lblMidX.setText(startMidX + midX + endMidX);
	}
	
	public void setMidY(final float midY) {
		lblMidY.setText(startMidY + midY + endMidY);
	}
}
