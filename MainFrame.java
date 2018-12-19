package p1;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class MainFrame extends JFrame implements Runnable, KeyListener
{
	private static final long serialVersionUID = 1L;

	/*********************************************************************/
	/* Window settings                                                   */
	/*********************************************************************/
	
	private final static String FRAME_TITLE = "3D Engine Java";
	private final static int WIDTH = 1600;
	private final static int HEIGHT = 900;	
	
	/*********************************************************************/
	/* Graphics                                                          */
	/*********************************************************************/
	
	private Canvas canvas = new Canvas();
	private BufferStrategy bs;
	private Graphics g;
	
	/*********************************************************************/
	/* Game Loop variables                                               */
	/*********************************************************************/
	
	private boolean running = true;
	private short frameCounter = 0;
	private short FPS = 60;
	private final static double PREFERRED_FPS = 60;

	/*********************************************************************/
	/* Game variables                                                    */
	/*********************************************************************/
	
	private Mesh cube;
	
	/*********************************************************************/
	/* Engine variables                                                  */
	/*********************************************************************/
	
	private final static double NEAR = 0.1;
	private final static double FAR = 1000.0;
	private final static double FOV = 90.0;
	private final static double ASPECT_RATIO = HEIGHT * 1.0 / WIDTH;
	private final static double FOV_RAD = 1.0 / Math.tan(FOV * 0.5 / 180.0 * Math.PI);
	
	private final static double[][] toScreenMat = new double[4][4];
	
	private final static double[][] rotMatX = new double[4][4];
	private final static double[][] rotMatY = new double[4][4];
	private final static double[][] rotMatZ = new double[4][4];
	
	private final static Point camera = new Point();
	private final static Point light = new Point(0.0, 0.0, -1.0);
	
	private double theta = 0;
	
	/*********************************************************************/
	/* Boolean Keys                                                      */
	/*********************************************************************/
	
	private boolean escape = false;	
	
	/*********************************************************************/
	/* Constructor                                                       */
	/*********************************************************************/
	
	public MainFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		canvas.setSize(WIDTH, HEIGHT);
		add(canvas);
		
		pack();
		setLocationRelativeTo(null);
		
		setVisible(true);
		
		canvas.createBufferStrategy(3);
		
		addKeyListener(this);
		
		new Thread(this).start();
	}
	
	/*********************************************************************/
	/* Game methods                                                      */
	/*********************************************************************/
	
	private void init()
	{
		Point[] cubePoints = new Point[8];
		Triangle[] cubeTriangles = new Triangle[12];
		
		cubePoints[0] = new Point(0, 0, 0);
		cubePoints[1] = new Point(0, 1, 0);
		cubePoints[2] = new Point(1, 1, 0);
		cubePoints[3] = new Point(1, 0, 0);
		cubePoints[4] = new Point(0, 0, 1);
		cubePoints[5] = new Point(0, 1, 1);
		cubePoints[6] = new Point(1, 1, 1);
		cubePoints[7] = new Point(1, 0, 1);
		
		cubeTriangles[0]  = new Triangle(cubePoints[0], cubePoints[1], cubePoints[2]); // SOUTH
		cubeTriangles[1]  = new Triangle(cubePoints[0], cubePoints[2], cubePoints[3]);
		
		cubeTriangles[2]  = new Triangle(cubePoints[3], cubePoints[2], cubePoints[6]); // EAST
		cubeTriangles[3]  = new Triangle(cubePoints[3], cubePoints[6], cubePoints[7]);
		
		cubeTriangles[4]  = new Triangle(cubePoints[7], cubePoints[6], cubePoints[5]); // NORTH
		cubeTriangles[5]  = new Triangle(cubePoints[7], cubePoints[5], cubePoints[4]);
		
		cubeTriangles[6]  = new Triangle(cubePoints[4], cubePoints[5], cubePoints[1]); // WEST
		cubeTriangles[7]  = new Triangle(cubePoints[4], cubePoints[1], cubePoints[0]);
		
		cubeTriangles[8]  = new Triangle(cubePoints[1], cubePoints[5], cubePoints[6]); // TOP
		cubeTriangles[9]  = new Triangle(cubePoints[1], cubePoints[6], cubePoints[2]);
		
		cubeTriangles[10] = new Triangle(cubePoints[0], cubePoints[4], cubePoints[7]); // BOT
		cubeTriangles[11] = new Triangle(cubePoints[0], cubePoints[7], cubePoints[3]);
		
		cube = new Mesh(cubeTriangles);
		
		toScreenMat[0][0] = ASPECT_RATIO * FOV_RAD;
		toScreenMat[1][1] = FOV_RAD;
		toScreenMat[2][2] = FAR / (FAR - NEAR);
		toScreenMat[3][2] = (-FAR * NEAR) / (FAR - NEAR);
		toScreenMat[2][3] = 1.0;
		toScreenMat[3][3] = 0.0;
	}
	
	private void update()
	{
		if(!escape)
		{
			setTitle(FRAME_TITLE + " - FPS: " + FPS);
			
			theta += 0.03;
		}
		else
			running = false;
	}
	
	private void render()
	{
		bs = canvas.getBufferStrategy();
		g = bs.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		
		/*g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);*/
		
		/*********************************************************************/
		
		g.setColor(Color.RED);
		g.fillArc(WIDTH - 120, 40, 80, 80, frameCounter * 6 % 360, 90);
		
		rotMatX[0][0] = 1.0;
		rotMatX[1][1] = Math.cos(theta);
		rotMatX[1][2] = Math.sin(theta);
		rotMatX[2][1] = - Math.sin(theta);
		rotMatX[2][2] = Math.cos(theta);
		rotMatX[3][3] = 1.0;
		
		rotMatY[0][0] = Math.cos(theta);
		rotMatY[1][1] = 1.0;
		rotMatY[0][2] = -  Math.sin(theta);
		rotMatY[2][0] = Math.sin(theta);
		rotMatY[2][2] = Math.cos(theta);
		rotMatY[3][3] = 1.0;
		
		rotMatZ[0][0] = Math.cos(theta);
		rotMatZ[0][1] = Math.sin(theta);
		rotMatZ[1][0] = - Math.sin(theta);
		rotMatZ[1][1] = Math.cos(theta);
		rotMatZ[2][2] = 1.0;
		rotMatZ[3][3] = 1.0;
		
		for(int i = 0; i < cube.getNTriangle(); i++)
		{			
			Triangle inBuffer = cube.getTriangle(i);
			Triangle outBuffer = new Triangle(new Point(), new Point(), new Point());
			
			multiplyMatrixVector(inBuffer.getPoint(0), outBuffer.getPoint(0), rotMatX);
			multiplyMatrixVector(inBuffer.getPoint(1), outBuffer.getPoint(1), rotMatX);
			multiplyMatrixVector(inBuffer.getPoint(2), outBuffer.getPoint(2), rotMatX);
			inBuffer = outBuffer;
			outBuffer = new Triangle(new Point(), new Point(), new Point());
			
		/*	multiplyMatrixVector(inBuffer.getPoint(0), outBuffer.getPoint(0), rotMatY);
			multiplyMatrixVector(inBuffer.getPoint(1), outBuffer.getPoint(1), rotMatY);
			multiplyMatrixVector(inBuffer.getPoint(2), outBuffer.getPoint(2), rotMatY);
			inBuffer = outBuffer;
			outBuffer = new Triangle(new Point(), new Point(), new Point());
			
			multiplyMatrixVector(inBuffer.getPoint(0), outBuffer.getPoint(0), rotMatZ);
			multiplyMatrixVector(inBuffer.getPoint(1), outBuffer.getPoint(1), rotMatZ);
			multiplyMatrixVector(inBuffer.getPoint(2), outBuffer.getPoint(2), rotMatZ);
			inBuffer = outBuffer;
			outBuffer = new Triangle(new Point(), new Point(), new Point());*/
			
			// offset into screen
			Triangle triTranslated = inBuffer.clone();
			triTranslated.getPoint(0).z += 3.0;
			triTranslated.getPoint(1).z += 3.0;
			triTranslated.getPoint(2).z += 3.0;				
			
			Point line1 = new Point(triTranslated.getPoint(1).x - triTranslated.getPoint(0).x,
									triTranslated.getPoint(1).y - triTranslated.getPoint(0).y,
									triTranslated.getPoint(1).z - triTranslated.getPoint(0).z);
			
			Point line2 = new Point(triTranslated.getPoint(2).x - triTranslated.getPoint(0).x,
									triTranslated.getPoint(2).y - triTranslated.getPoint(0).y,
									triTranslated.getPoint(2).z - triTranslated.getPoint(0).z);
			
			Point normal = new Point(line1.y * line2.z - line1.z * line2.y,
									 line1.z * line2.x - line1.x * line2.z,
									 line1.x * line2.y - line1.y * line2.x);
			
			double l = Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z);
			normal.x /= l;
			normal.y /= l;
			normal.z /= l;
		
			//if(normal.z < 0)
			if(normal.x * (triTranslated.getPoint(0).x - camera.x) +
			   normal.y * (triTranslated.getPoint(0).y - camera.y) +
			   normal.z * (triTranslated.getPoint(0).z - camera.z) < 0)
			{
				
				double l2 = Math.sqrt(light.x * light.x + light.y * light.y + light.z * light.z);
				light.x /= l2;
				light.y /= l2;
				light.z /= l2;
				
				double dotProduct = normal.x * light.x + normal.y * light.y + normal.z * light.z;
				
				System.out.println(dotProduct);
				
				Triangle triProjected = new Triangle(new Point(), new Point(), new Point());
				
				// Project triangles from 3D to 2D
				multiplyMatrixVector(triTranslated.getPoint(0), triProjected.getPoint(0), toScreenMat);
				multiplyMatrixVector(triTranslated.getPoint(1), triProjected.getPoint(1), toScreenMat);
				multiplyMatrixVector(triTranslated.getPoint(2), triProjected.getPoint(2), toScreenMat);
				
				// scale into view
				triProjected.getPoint(0).x += 1.0; triProjected.getPoint(0).y += 1.0;
				triProjected.getPoint(1).x += 1.0; triProjected.getPoint(1).y += 1.0;
				triProjected.getPoint(2).x += 1.0; triProjected.getPoint(2).y += 1.0;
				
				triProjected.getPoint(0).x *= 0.5 * WIDTH;
				triProjected.getPoint(0).y *= 0.5 * HEIGHT;
				triProjected.getPoint(1).x *= 0.5 * WIDTH;
				triProjected.getPoint(1).y *= 0.5 * HEIGHT;
				triProjected.getPoint(2).x *= 0.5 * WIDTH;
				triProjected.getPoint(2).y *= 0.5 * HEIGHT;
				
				// draw triangle
				int[] xPoints = new int[3];
				int[] yPoints = new int[3];
				
				xPoints[0] = (int)triProjected.getPoint(0).x;
				xPoints[1] = (int)triProjected.getPoint(1).x;
				xPoints[2] = (int)triProjected.getPoint(2).x;
				
				yPoints[0] = (int)triProjected.getPoint(0).y;
				yPoints[1] = (int)triProjected.getPoint(1).y;
				yPoints[2] = (int)triProjected.getPoint(2).y;
				
				g.setColor(new Color((int)(dotProduct * 200), (int)(dotProduct * 200), (int)(dotProduct * 200) + 55));
				g.fillPolygon(xPoints, yPoints,3);
				
			/*	g.setColor(Color.BLACK);
				g.drawLine((int)triProjected.getPoint(0).x, (int)triProjected.getPoint(0).y, (int)triProjected.getPoint(1).x, (int)triProjected.getPoint(1).y);
				g.drawLine((int)triProjected.getPoint(1).x, (int)triProjected.getPoint(1).y, (int)triProjected.getPoint(2).x, (int)triProjected.getPoint(2).y);
				g.drawLine((int)triProjected.getPoint(2).x, (int)triProjected.getPoint(2).y, (int)triProjected.getPoint(0).x, (int)triProjected.getPoint(0).y);*/
			}
		}
		
		
		/*********************************************************************/
		
		bs.show();		
		g.dispose();
	}
	
	private void multiplyMatrixVector(Point i, Point o, double[][] m)
	{		
		double w;
		o.x = i.x * m[0][0] + i.y * m[1][0] + i.z * m[2][0] + m[3][0];
		o.y = i.x * m[0][1] + i.y * m[1][1] + i.z * m[2][1] + m[3][1];
		o.z = i.x * m[0][2] + i.y * m[1][2] + i.z * m[2][2] + m[3][2];
		w   = i.x * m[0][3] + i.y * m[1][3] + i.z * m[2][3] + m[3][3];
		
		if(w != 0.0)
		{
			o.x /= w;
			o.y /= w;
			o.z /= w;
		}
	}

	/*********************************************************************/
	/* Game loop                                                         */
	/*********************************************************************/

	public void run()
	{
		init();
		
		long t0 = System.nanoTime();
		long t1 = t0;
		
		while(running)
		{
			update();
			render();
			
			if(t1 >= t0 + 1000000000)
			{			
				t0 = t1;
				FPS = frameCounter;
				frameCounter = 0;
			}
		
			frameCounter++;
			
			while(System.nanoTime() < t1 + (1 / PREFERRED_FPS) * 1000000000);
			t1 = System.nanoTime();
		}
		
		System.exit(0);
	}
	
	/*********************************************************************/
	/* Events                                                            */
	/*********************************************************************/

	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			escape = true;
	}

	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			escape = false;
	}
	
	public void keyTyped(KeyEvent e){}
	
}
