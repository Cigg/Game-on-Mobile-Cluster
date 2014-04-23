import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

public class ServerGraphics {

    private JFrame mainMap;
    private Polygon poly;
    private JPanel p;
    private PhysicsWorld pw;
    private int xPoly[] = {150, 250, 325, 375, 450, 275, 100};
    private int yPoly[] = {150, 100, 125, 225, 250, 375, 300};
    private World world;
    private DeviceManager deviceManager;
    private int offsetX = 0;
    private int offsetY = 0;
    private boolean gotOffset = false;
    private float converter = (1200.0f/(104.0f/2.54f));

    public ServerGraphics(DeviceManager deviceManager) {
    	this.deviceManager = deviceManager;
        initComponents();

    }

    private void initComponents() {

        mainMap = new JFrame();
        mainMap.setResizable(false);
        mainMap.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        pw = MultiThreds.getPhysicsWorld();
        world = pw.getWorld();
        System.out.print(world.getBodyList());
        
        
        poly = new Polygon(xPoly, yPoly, xPoly.length);
        p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.RED);
                
                g.fillOval(600, 600, 10, 10);
                
                g.setColor(Color.BLUE);
                if(!gotOffset) {
                    if(deviceManager.getMiddleIp() != null ){
                    	deviceManager.getDeviceThread(deviceManager.getMiddleIp());
                    	offsetX = (int) (deviceManager.getWidthInch(deviceManager.getMiddleIp())*converter/2);
                    	offsetY = (int) (deviceManager.getHeightInch(deviceManager.getMiddleIp())*converter/2);
                    	gotOffset = true;
                    	//System.out.println("offsetX: " + offsetX  + ", OffsetY: " + offsetY);
                    }
                }
                
                Body body = world.getBodyList();
                while(body != null){
                	Fixture fixture = body.getFixtureList();
                	while(fixture != null){
                		ShapeType type = fixture.getType();
                		Vec2 pos = body.getPosition();
                		if(type == ShapeType.POLYGON) {
                			PolygonShape shape = (PolygonShape)fixture.getShape();
                			
                			poly = new Polygon();
                			Vec2[] vertices = shape.getVertices();
                			float x =0;
                			float y =0;
                			for(int i = 0; i < shape.m_vertexCount; i++){
                				x = pos.x/2; 
                				y = pos.y/2;
                				poly.addPoint((int) ((x+vertices[i].x*1.3)*converter)+600 - offsetX, (600+offsetY)-(int) ((y+vertices[i].y*1.3)*converter));
                			}
                			
                			float centerX = 600 - offsetX;
                			float centerY = (600+offsetY);
                			AffineTransform rotateTransform = new AffineTransform();
                			rotateTransform.rotate(body.getAngle(),centerX,centerY);
                			
                			Graphics2D g2d = (Graphics2D) g;
                			g2d.setTransform(rotateTransform);
                			g2d.draw(poly);
                			rotateTransform.rotate(-body.getAngle(),centerX,centerY);
                			g2d.setTransform(rotateTransform);
                			//g2d.rotate(-body.getAngle());
                		} else if (type == ShapeType.CIRCLE){
                			CircleShape shape = (CircleShape)fixture.getShape();
                			//System.out.println(pos.x + ", " + pos.y);
                			int r = 20;
                			int x = (int) ((pos.x) * converter) + 600 - offsetX;
                			int y = (600+offsetY) - ((int) ((pos.y) * converter));
                			
                			x = x - (r/2);
                			y = y + (r/2);
                			g.fillOval(x, y, r, r);
                		}
                		fixture = fixture.getNext();
                	}
                	body = body.getNext();
                }
                
                for(int i=0; i < deviceManager.devices.size(); i++){
                	int x = (int) (deviceManager.getPosX(deviceManager.getIp(i))*converter);
                	int y = (int) (deviceManager.getPosY(deviceManager.getIp(i))*converter);
                	int width = (int) (deviceManager.getWidthInch(deviceManager.getIp(i))*converter);
                	int height = (int) (deviceManager.getHeightInch(deviceManager.getIp(i))*converter);
                	x = x + 600 - offsetX;
                	y = (600 + offsetY) - y - height;
                	float rot = deviceManager.getRotZ(deviceManager.getIp(i));
                	Rectangle rect = new Rectangle(x,y,width,height);
                	Graphics2D g2d = (Graphics2D)g;
                	g2d.rotate(rot);
                	g2d.draw(rect);
                	//g.drawRect(x, y, width, height);
                	
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(1200, 1200);
            }
        };
        mainMap.add(p);
        mainMap.pack();
        mainMap.setVisible(true);

    }
    
    public void update() {
    	Graphics g = p.getGraphics();
    	mainMap.update(g);
    }
}