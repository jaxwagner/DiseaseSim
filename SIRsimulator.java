import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.ArrayList;


public class SIRsim extends JFrame implements ActionListener, MouseListener
{
	final int DELAY_IN_MILLISEC = 100;
	final static int MAX_PERIOD = 20;
	static int PERIOD_S = 10;
	final static int MAX_WIDTH = 1500;
	final static int MAX_HEIGHT = 800;
	public final static int NUM_CELLS = 200;
	static int NUM_NODES = 3;
	static int NUM_CONNECTIONS = 2;
	final static int NUM_VACCINATORS = 3;
	final static int NUM_BUTTONS = 7;
	final static int NUM_SLIDERS = 2;
	final static double PROB_TRANS = .5;
	int numS = 0;
	int numI = 0;
	int numR = 0;

	int count = 0;

	//start and stop the simulation
	boolean running = false;

	boolean bostonT = false;

	public static Cell [] cellList = new Cell [NUM_CELLS];
	public static Vaccinator [] vList = new Vaccinator [NUM_VACCINATORS];
	Node [] nodeList = new Node [NUM_NODES];
	Connection [] connectionList = new Connection [NUM_CONNECTIONS];
	Button [] buttonList = new Button [NUM_BUTTONS];
	Slider [] sliderList = new Slider [NUM_SLIDERS];

	//for new connecitons
	int [] pointSaver = {0,0};

	Timer clock = new Timer(DELAY_IN_MILLISEC, this);

	// buttons
	Button playButton = new Button(200, 50, 100, 22, "play/pause");
	Button resetButton = new Button(300, 50, 100, 22, "reset");
	Button newSmallNodeButton = new Button(400,50,120,22,"new small node");
	Button newMediumNodeButton = new Button(520,50,120,22,"new medium node");
	Button newLargeNodeButton = new Button(640,50,120,22,"new large node");
	Button newConnectionButton = new Button(760, 50, 120, 22, "new connection");
	Button bostonTPreset = new Button(880, 50, 120, 22, "Boston T");
	
	//sliders
	Slider frequencySlider = new Slider(1020, 50, 200, 22, .5, "Vaccine Frequency");
	Slider lifetimeSlider = new Slider(1230, 50, 200, 22, .5, "Vaccine Lifetime");
	
	//graph
	Graph graph = new Graph(640, 500, 1000, 200);
	
	//stat lists
	ArrayList<Integer> numSList = new ArrayList<Integer>();
	ArrayList<Integer> numIList = new ArrayList<Integer>();
	ArrayList<Integer> numRList = new ArrayList<Integer>();
	ArrayList<Integer> P1 = new ArrayList<Integer>();
	ArrayList<Integer> P2 = new ArrayList<Integer>();
	ArrayList<Integer> P3 = new ArrayList<Integer>();
	ArrayList<Integer> S1 = new ArrayList<Integer>();
	ArrayList<Integer> S2 = new ArrayList<Integer>();
	ArrayList<Integer> S3 = new ArrayList<Integer>();
	ArrayList<Integer> I1 = new ArrayList<Integer>();
	ArrayList<Integer> I2 = new ArrayList<Integer>();
	ArrayList<Integer> I3 = new ArrayList<Integer>();
	
	public static void main (String [] args)
	{
		SIRsim mg = new SIRsim();

		// Register listeners
		mg.addMouseListener(mg);
	}

	public SIRsim()
	{
		// buttons
		buttonList[0] = playButton;
		buttonList[1] = resetButton;
		buttonList[2] = newSmallNodeButton;
		buttonList[3] = newMediumNodeButton;
		buttonList[4] = newLargeNodeButton;
		buttonList[5] = newConnectionButton;
		buttonList[6] = bostonTPreset;

		//initialize nodes


		nodeList[0] = new Node(100, 100, 200, 200);
		nodeList[1] = new Node(500, 100, 346, 346);
		nodeList[2] = new Node(900, 100, 282, 282);
		//initialize connections

		connectionList[0] = new Connection(nodeList[0], nodeList[1]);
		connectionList[1] = new Connection(nodeList[1], nodeList[2]);

		//sliders
		sliderList[0] = frequencySlider;
		sliderList[1] = lifetimeSlider;



		//initialize vaccinators
		for(int i = 0; i < NUM_VACCINATORS; i++)
		{
			int nodeNum = (int)((NUM_NODES)*Math.random());
			int x = (int)((nodeList[nodeNum].nodeWidth-25)*Math.random()+nodeList[nodeNum].nodeX + 10);
			int y = (int)((nodeList[nodeNum].nodeHeight - 35)*Math.random()+nodeList[nodeNum].nodeY + 20);
			int lifetime = 10;
			vList[i] = new Vaccinator(x, y, nodeList[nodeNum], lifetime);
		}

		//initialize cells
		for(int i = 0; i < NUM_CELLS; i ++)
		{
			int nodeNum = 0;
			//int nodeNum = (int)((NUM_NODES)*Math.random());
			if(i < 25)
			{
				nodeNum = 0;
			}
			else if(25 <= i && i < 50)
			{
				nodeNum = 2;
			}
			else
			{
				nodeNum = 1;
			}
			
			int x = (int)((nodeList[nodeNum].nodeWidth-25)*Math.random()+nodeList[nodeNum].nodeX + 10);
			int y = (int)((nodeList[nodeNum].nodeHeight - 35)*Math.random()+nodeList[nodeNum].nodeY + 20);
			int sir = (int)((90)*Math.random());
			//			if(sir > 50)
			//			{
			//				sir = 1;
			//			}
			//			else
			//			{
			//				sir = 0;
			//			}
			if(nodeNum == 0)
			{
				sir = 1;
			}
			else
			{
				sir = 0;
			}
			cellList[i] = new Cell(x, y, sir, nodeList[nodeNum]);
		}


		clock.start();
		setSize(MAX_WIDTH, MAX_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void reset()
	{

		cellList = new Cell [NUM_CELLS];
		vList = new Vaccinator [NUM_VACCINATORS];
		nodeList = new Node [NUM_NODES];
		connectionList = new Connection [NUM_CONNECTIONS];
		buttonList = new Button [NUM_BUTTONS];
		
		numSList.clear();
		numIList.clear();
		numRList.clear();

		// buttons
		buttonList[0] = playButton;
		buttonList[1] = resetButton;
		buttonList[2] = newSmallNodeButton;
		buttonList[3] = newMediumNodeButton;
		buttonList[4] = newLargeNodeButton;
		buttonList[5] = newConnectionButton;
		buttonList[6] = bostonTPreset;

		//initialize nodes
		if(!bostonT)
		{
			NUM_NODES = 3;
			nodeList[0] = new Node(100, 100, 200, 200);
			nodeList[1] = new Node(500, 100, 300, 300);
			nodeList[2] = new Node(200, 350, 200, 200);
			//initialize connections
			NUM_CONNECTIONS = 2;
			connectionList[0] = new Connection(nodeList[0], nodeList[1]);
			connectionList[1] = new Connection(nodeList[1], nodeList[2]);
		}
		else
		{
			NUM_NODES = 4;
			nodeList = new Node [NUM_NODES];
			nodeList[0] = new Node(100, 100, 200, 200);
			nodeList[1] = new Node(350, 100, 200, 200);
			nodeList[2] = new Node(600, 150, 200, 200);
			nodeList[3] = new Node(900, 175, 200, 200);

			NUM_CONNECTIONS = 3;
			connectionList = new Connection [NUM_CONNECTIONS];
			connectionList[0] = new Connection(nodeList[0], nodeList[1]);
			connectionList[1] = new Connection(nodeList[1], nodeList[2]);
			connectionList[2] = new Connection(nodeList[2], nodeList[3]);
		}


		//initialize vaccinators
		for(int i = 0; i < NUM_VACCINATORS; i++)
		{
			int nodeNum = (int)((NUM_NODES)*Math.random());
			int x = (int)((nodeList[nodeNum].nodeWidth-25)*Math.random()+nodeList[nodeNum].nodeX + 10);
			int y = (int)((nodeList[nodeNum].nodeHeight - 35)*Math.random()+nodeList[nodeNum].nodeY + 20);
			int lifetime = 10;
			vList[i] = new Vaccinator(x, y, nodeList[nodeNum], lifetime);
		}

		//initialize cells
		for(int i = 0; i < NUM_CELLS; i ++)
		{
			int nodeNum = (int)((NUM_NODES)*Math.random());
			int x = (int)((nodeList[nodeNum].nodeWidth-25)*Math.random()+nodeList[nodeNum].nodeX + 10);
			int y = (int)((nodeList[nodeNum].nodeHeight - 35)*Math.random()+nodeList[nodeNum].nodeY + 20);
			int sir = (int)((90)*Math.random());
			//					if(sir > 50)
			//					{
			//						sir = 1;
			//					}
			//					else
			//					{
			//						sir = 0;
			//					}
			if(nodeNum == 0)
			{
				sir = 1;
			}
			else
			{
				sir = 0;
			}
			cellList[i] = new Cell(x, y, sir, nodeList[nodeNum]);
		}


		clock.restart();
		setSize(MAX_WIDTH, MAX_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void makeNewNode(int x, int y, int width, int height)
	{
		NUM_NODES++;
		Node [] newNodeList = new Node[NUM_NODES];
		for(int i = 0; i < nodeList.length; i++)
		{
			newNodeList[i] = nodeList[i];
		}
		newNodeList[NUM_NODES - 1] = new Node(x,y,width,height);
		nodeList = newNodeList;
	}

	public void makeNewConnection(int x, int y)
	{
		if(pointSaver[0] == 0 && pointSaver[1] == 0)
		{
			pointSaver[0] = x;
			pointSaver[1] = y;
		}
		else
		{
			Node node1 = getNode(x,y);
			Node node2 = getNode(pointSaver[0], pointSaver[1]);

			if(node1 != null && node2 != null && node1 != node2)
			{
				NUM_CONNECTIONS++;
				Connection [] newConnectionList = new Connection[NUM_CONNECTIONS];
				for(int i = 0; i < connectionList.length; i++)
				{
					newConnectionList[i] = connectionList[i];
				}
				newConnectionList[NUM_CONNECTIONS-1] = new Connection(node1, node2);
				connectionList = newConnectionList;
			}
			pointSaver[0] = 0;
			pointSaver[1] = 0;
		}
	}

	public Node getNode(int x, int y)
	{
		for(int i = 0; i < NUM_NODES; i++)
		{
			if(x > nodeList[i].nodeX && x < nodeList[i].nodeX + nodeList[i].nodeWidth
					&& y > nodeList[i].nodeY && y < nodeList[i].nodeHeight + nodeList[i].nodeY)
			{
				return nodeList[i];
			}
		}
		return null;
	}

	/**
	 * updates stuff each clock tick
	 */
	public void actionPerformed(ActionEvent arg0) 
	{
		if(playButton.getClicked())
		{
			count ++;
			for(int i = 0; i < NUM_NODES; i++)
			{
				nodeList[i].getNodeSIR();
			}
			for(int i = 0; i < NUM_CONNECTIONS; i++)
			{
				for(int j = 0; j < NUM_CELLS; j++)
				{
					connectionList[i].grabCells(cellList[j]);
					if(cellList[j].connecting == true)
					{
						cellList[j].inNewNode();
					}
				}
				for(int j = 0; j < NUM_VACCINATORS; j++)
				{
					connectionList[i].grabVaccinators(vList[j]);
					if(vList[j].connecting == true)
					{
						vList[j].inNewNode();
					}
				}
			}
			for(int i = 0; i < NUM_VACCINATORS; i++)
			{
				vList[i].die();
				if(vList[i].alive == true)
				{
					vList[i].move();
					for(int j = 0; j < NUM_CELLS; j++)
					{
						vList[i].touchingCell(cellList[j]);
					}
				}
			}
			if(count % (1000*PERIOD_S/DELAY_IN_MILLISEC) == 0)
			{
				for(int i = 0; i < NUM_VACCINATORS; i++)
				{
					vList[i].alive = true;
				}
			}
			for(int i = 0; i < NUM_CELLS; i++)
			{
				cellList[i].move();
				if(cellList[i].SIR == 0)
				{
					for(int j = 0; j < NUM_CELLS; j ++)
					{
						if(cellList[j].SIR == 1)
						{
							cellList[i].touchingInfected(cellList[j]);
						}
					}
				}
				if(cellList[i].SIR == 1)
				{
					cellList[i].iToR();
				}
			}
			numS = 0;
			numI = 0;
			numR = 0;
			for(int i = 0; i < NUM_CELLS; i++)
			{
				if(cellList[i].SIR == 0)
				{
					numS++;
				}
				else if(cellList[i].SIR == 1)
				{
					numI++;
				}
				else if(cellList[i].SIR == 2)
				{
					numR++;
				}
			}
			numSList.add(numS);
			numIList.add(numI);
			numRList.add(numR);
			
			S1.add(nodeList[0].nodeNumS);
			I1.add(nodeList[0].nodeNumI);
			S2.add(nodeList[1].nodeNumS);
			I2.add(nodeList[1].nodeNumI);
			S3.add(nodeList[2].nodeNumS);
			I3.add(nodeList[2].nodeNumI);
			
			P1.add(nodeList[0].nodeNumS + nodeList[0].nodeNumI);
			P2.add(nodeList[1].nodeNumS + nodeList[1].nodeNumI);
			P3.add(nodeList[2].nodeNumS + nodeList[2].nodeNumI);
			int p1 = nodeList[0].nodeNumS + nodeList[0].nodeNumI;
			int p2 = nodeList[1].nodeNumS + nodeList[1].nodeNumI;
			int p3 = nodeList[2].nodeNumS + nodeList[2].nodeNumI;
			System.out.println( nodeList[0].nodeNumS + "," + nodeList[0].nodeNumI + "," +nodeList[1].nodeNumS
					+ "," + nodeList[1].nodeNumI + "," + nodeList[2].nodeNumS + "," + nodeList[2].nodeNumI);
			
		}

		repaint();
	}

	public void mouseClicked(MouseEvent e) 
	{
		int x = e.getX();
		int y = e.getY();

		if(x > playButton.x && x < playButton.x + playButton.width && y > playButton.y && y < playButton.y + playButton.height)
		{
			running = !running;
			//print lists 
//			for(int i = 0; i < numSList.size(); i ++)
//			{
//				System.out.println(numSList.get(i) + ", " + numIList.get(i) + ", " + numRList.get(i));
//			}
			
//			for(int i = 0; i < P1.size(); i++)
//			{
//				System.out.println(1 + ", " + P1.get(i) + ", " + P2.get(i) + ", " + P3.get(i));
//			}
			
			playButton.setClicked(running);
		}

		else if(x > resetButton.x && x < resetButton.x + resetButton.width && y > resetButton.y && y < resetButton.y + resetButton.height)
		{
			reset();
		}
		else if(x > bostonTPreset.x && x < bostonTPreset.x + bostonTPreset.width && y > bostonTPreset.y && y < bostonTPreset.y + bostonTPreset.height)
		{
			bostonT = !bostonT;
			bostonTPreset.setClicked(!bostonTPreset.getClicked());
			reset();
		}
		//sliders
		else if(x > sliderList[0].x && x < sliderList[0].x + sliderList[0].width && y > sliderList[0].y && y < sliderList[0].y + sliderList[0].height)
		{
			sliderList[0].setBallX(x);
		}
		else if(x > sliderList[1].x && x < sliderList[1].x + sliderList[1].width && y > sliderList[1].y && y < sliderList[1].y + sliderList[1].height)
		{
			sliderList[1].setBallX(x);
		}

		//new node and connection making
		else if( newSmallNodeButton.getClicked() && y > newSmallNodeButton.height + newSmallNodeButton.y)
		{
			makeNewNode(x,y,150, 150);
		}
		else if(newMediumNodeButton.getClicked() && y > newMediumNodeButton.height + newMediumNodeButton.y)
		{
			makeNewNode(x,y,220, 220);
		}
		else if(newLargeNodeButton.getClicked() && y > newLargeNodeButton.height + newLargeNodeButton.y)
		{
			makeNewNode(x,y,300, 300);
		}
		else if(newConnectionButton.getClicked() && y > newConnectionButton.height + newConnectionButton.y)
		{
			makeNewConnection(x,y);
		}

		// new node and connection clikced
		else if(!bostonT && x > newSmallNodeButton.x && x < newSmallNodeButton.x + newSmallNodeButton.width && y > newSmallNodeButton.y && y < newSmallNodeButton.y + newSmallNodeButton.height)
		{
			newSmallNodeButton.setClicked(!newSmallNodeButton.getClicked());
		}
		else if(!bostonT && x > newMediumNodeButton.x && x < newMediumNodeButton.x + newMediumNodeButton.width && y > newMediumNodeButton.y && y < newMediumNodeButton.y + newMediumNodeButton.height)
		{
			newMediumNodeButton.setClicked(!newMediumNodeButton.getClicked());
		}
		else if(!bostonT && x > newLargeNodeButton.x && x < newLargeNodeButton.x + newLargeNodeButton.width && y > newLargeNodeButton.y && y < newLargeNodeButton.y + newLargeNodeButton.height)
		{
			newLargeNodeButton.setClicked(!newLargeNodeButton.getClicked());
		}
		else if(!bostonT && x > newConnectionButton.x && x < newConnectionButton.x + newConnectionButton.width && y > newConnectionButton.y && y < newConnectionButton.y + newConnectionButton.height)
		{
			newConnectionButton.setClicked(!newConnectionButton.getClicked());
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{

	}

	public void paint (Graphics g)
	{
		g.setColor(Color.white);
		g.fillRect(0,0,MAX_WIDTH*3,MAX_HEIGHT*3);
		g.setColor(Color.blue);
		g.drawString("Infectious Disease Simulator", (int)(MAX_WIDTH/2.2), 35);
		//draw buttons
		for(int i = 0; i < NUM_BUTTONS; i++)
		{
			boolean raised = true;
			if(buttonList[i].getClicked() == true)
			{
				g.setColor(buttonList[i].clickedColor);
				raised = false;  
			}
			else
			{
				g.setColor(buttonList[i].normalColor);
			}
			g.draw3DRect(buttonList[i].x, buttonList[i].y, buttonList[i].width, buttonList[i].height, raised);
			String s = buttonList[i].text;
			g.drawString(s, buttonList[i].x + 5, buttonList[i].y + 15);
		}
		
		for(int i = 0; i < NUM_SLIDERS; i++)
		{
			g.setColor(Color.black);
			g.drawString(sliderList[i].name, sliderList[i].x, sliderList[i].y - 10);
			g.setColor(Color.gray);
			g.drawRect(sliderList[i].x, sliderList[i].y, sliderList[i].width, sliderList[i].height);
			g.setColor(Color.blue);
			g.fillOval(sliderList[i].getBallX(), sliderList[i].y, sliderList[i].height, sliderList[i].height);
		}

		g.setColor(Color.black);
		g.drawString("Susceptible: " + numS, 10, 35);
		g.drawString("Infected: " + numI, 10, 47);
		g.drawString("Recovered: " + numR, 10, 59);
		
		// draw graph
		g.drawLine(graph.x, graph.y, graph.x, graph.y+graph.height);
		g.drawLine(graph.x, graph.y + graph.height, graph.x + graph.width, graph.y + graph.height);
		g.drawString("Percent of Population", graph.x - 150, graph.y + graph.height/2);
		g.drawString("Time", graph.x + 200, graph.y + graph.height + 15);
		
		g.setColor(Color.gray);
		for(int i = 0; i < numSList.size(); i++)
		{
			g.drawOval(graph.x + i, graph.getPointHeight(numSList.get(i)), 2, 2);
		}
		g.setColor(Color.green);
		for(int i = 0; i < numIList.size(); i++)
		{
			g.drawOval(graph.x + i, graph.getPointHeight(numIList.get(i)), 2, 2);
		}
		g.setColor(Color.blue);
		for(int i = 0; i < numRList.size(); i++)
		{
			g.drawOval(graph.x + i, graph.getPointHeight(numRList.get(i)), 2, 2);
		}
		
		g.setColor(Color.black);
		for(int i = 0; i < NUM_NODES; i++)
		{
			g.drawRect(nodeList[i].nodeX, nodeList[i].nodeY, nodeList[i].nodeWidth, nodeList[i].nodeHeight);
			g.drawString("Susceptible: " + nodeList[i].nodeNumS, nodeList[i].nodeX + 10, nodeList[i].nodeY + 15);
			g.drawString("Infected: " + nodeList[i].nodeNumI, nodeList[i].nodeX + 10, nodeList[i].nodeY + 27);
			g.drawString("Recovered: " + nodeList[i].nodeNumR, nodeList[i].nodeX + 10, nodeList[i].nodeY + 39);
		}
		for(int i = 0; i < NUM_CONNECTIONS; i++)
		{
			g.drawLine(connectionList[i].x1, connectionList[i].y1, connectionList[i].x2, connectionList[i].y2);
		}
		for(int i = 0; i < NUM_CELLS; i++)
		{
			if(cellList[i].SIR == 0)
			{
				g.setColor(Color.gray);
			}
			else if(cellList[i].SIR == 1)
			{
				g.setColor(Color.green);
			}
			else
			{
				g.setColor(Color.blue);
			}
			g.fillOval(cellList[i].cellX, cellList[i].cellY, 2*cellList[i].radius, 2*cellList[i].radius);
		}
		g.setColor(Color.red);
		for(int i = 0; i < NUM_VACCINATORS; i++)
		{
			if(vList[i].alive == true)
			{
				g.fillOval(vList[i].vX, vList[i].vY, 2*vList[i].radius, 2*vList[i].radius);
			}
		}
	}
}

class Vaccinator
{
	public int vX;
	public int vY;
	public Node vNode;
	Node oldNode;
	double theta = 2*Math.PI*Math.random();
	int radius = 10;
	double dTheta;
	int count = 0;
	double MAX_LIFETIME = 80;
	
	static int lifetime = 10;
	public boolean connecting = false;
	int connectionDX;
	int connectionDY;

	public static boolean alive = true;

	public Vaccinator(int xIn, int yIn, Node nodeIn, int lifetimeIn)
	{
		vX = xIn;
		vY = yIn;
		vNode = nodeIn;
		lifetime = lifetimeIn;
	}
	
	public void setLifetime(double value)
	{
		double placeholder = value * MAX_LIFETIME;
		lifetime = (int)(placeholder);
	}

	public void move()
	{
		if(connecting == false)
		{
			count ++;
			if(count%5 == 0)
			{
				dTheta = 0.85*(2*Math.random() - 1);
			}
			int dx = (int)(20*Math.cos(dTheta + theta));
			int dy = (int)(20*Math.sin(dTheta + theta));

			if(vX + dx <= vNode.nodeX || vX + 2*radius + dx >= vNode.nodeX + vNode.nodeWidth)
			{
				dx = 0 - dx;
				theta = theta - Math.PI;
			}
			if(vY- 2*radius + dy <= vNode.nodeY || vY + 2*radius + dy >= vNode.nodeY + vNode.nodeHeight)
			{
				dy = 0 - dy;
				theta = -theta;
			}
			vX = vX + dx;
			vY = vY + dy;
		}
		else
		{
			vX = vX + connectionDX;
			vY = vY + connectionDY;
		}

	}

	public void die()
	{
		if(count >= lifetime && alive == true)
		{
			alive = false;
		}
		if(alive == false)
		{
			count = 0;
		}
	}

	public void touchingCell(Cell c)
	{
		if(2*radius >= Math.sqrt((c.cellX - vX)*(c.cellX - vX) + (c.cellY - vY)*(c.cellY - vY)))
		{
			//c.SIR = 2;
		}
	}

	public void inNewNode()
	{
		if(oldNode != vNode)
		{
			if(vX > vNode.nodeX + 2*radius && vX < vNode.nodeX + vNode.nodeWidth - 2*radius
					&& vY > vNode.nodeY + 2*radius && vY < vNode.nodeY + vNode.nodeHeight - 2*radius)
			{
				oldNode = vNode;
				connecting = false;
			}
		}
	}
}

class Cell
{
	public int cellX;
	public int cellY;

	public Node cellNode;
	Node oldNode;
	double theta = 2*Math.PI*Math.random();

	// 0 - succeptible, 1 - infected, 2- recovered
	int SIR;
	double probInfected = 0.3;
	double probRecovery = 0.01;
	int radius = 10;
	double dTheta;
	int count = 0;

	public boolean connecting = false;
	int connectionDX;
	int connectionDY;

	public Cell(int cellXin, int cellYin, int SIRin, Node nodeIn)
	{
		cellX = cellXin;
		cellY = cellYin;
		SIR = SIRin;
		cellNode = nodeIn;
		dTheta = 0.5*(2*Math.random() - 1);
	}

	public void move()
	{
		if(connecting == false)
		{
			count ++;
			if(count%5 == 0)
			{
				dTheta = 0.85*(2*Math.random() - 1);
			}
			int dx = (int)(20*Math.cos(dTheta + theta));
			int dy = (int)(20*Math.sin(dTheta + theta));

			if(cellX + dx <= cellNode.nodeX || cellX + 2*radius + dx >= cellNode.nodeX + cellNode.nodeWidth)
			{
				dx = 0 - dx;
				theta = theta - Math.PI;
			}
			if(cellY- 2*radius + dy <= cellNode.nodeY || cellY + 2*radius + dy >= cellNode.nodeY + cellNode.nodeHeight)
			{
				dy = 0 - dy;
				theta = -theta;
			}
			cellX = cellX + dx;
			cellY = cellY + dy;
		}
		else
		{
			cellX = cellX + connectionDX;
			cellY = cellY + connectionDY;
		}
	}

	public void iToR()
	{
		if(SIR == 1)
		{
			double prob = Math.random();
			if(prob <= probRecovery)
			{
				SIR = 2;
				//switch to 0 to have IR model instead of SIR
				//SIR = 0;
			}
		}
	}

	public void touchingInfected(Cell c)
	{
		if(2*radius >= Math.sqrt((c.cellX - cellX)*(c.cellX - cellX) + (c.cellY - cellY)*(c.cellY - cellY)) && SIR == 0 && c.SIR == 1)
		{
			double prob = Math.random();
			if(prob <= probInfected)
			{
				SIR = 1;
			}
		}
	}

	public void inNewNode()
	{
		if(oldNode != cellNode)
		{
			if(cellX > cellNode.nodeX && cellX < cellNode.nodeX + cellNode.nodeWidth - 2*radius
					&& cellY > cellNode.nodeY && cellY < cellNode.nodeY + cellNode.nodeHeight - 2*radius)
			{
				oldNode = cellNode;
				connecting = false;
			}
		}
	}
}

class Node
{
	public int nodeX;
	public int nodeY;
	public int nodeWidth;
	public int nodeHeight;
	public int nodeNumS = 0;
	public int nodeNumI = 0;
	public int nodeNumR = 0;

	public Node(int xIn, int yIn, int widthIn, int heightIn)
	{
		nodeX = xIn;
		nodeY = yIn;
		nodeWidth = widthIn;
		nodeHeight = heightIn;
	}

	public void getNodeSIR()
	{
		nodeNumS = 0;
		nodeNumI = 0;
		nodeNumR = 0;
		for(int i = 0; i < SIRsim.NUM_CELLS; i ++)
		{
			if(SIRsim.cellList[i].cellNode == this)
			{
				if(SIRsim.cellList[i].SIR == 0)
				{
					nodeNumS++;
				}
				else if(SIRsim.cellList[i].SIR == 1)
				{
					nodeNumI++;
				}
				else if(SIRsim.cellList[i].SIR == 2)
				{
					nodeNumR++;
				}
			}
		}
	}
}

class Connection
{
	Node node1;
	Node node2;
	int x1;
	int y1;
	int x2;
	int y2;

	public Connection(Node oneIn, Node twoIn)
	{
		node1 = oneIn;
		node2 = twoIn;
		y1 = node1.nodeY + node1.nodeHeight/2;
		y2 = node2.nodeY + node2.nodeHeight/2;
		if(node1.nodeX < node2.nodeX)
		{
			x1 = node1.nodeX + node1.nodeWidth;
			x2 = node2.nodeX;
		}
		else
		{
			x2 = node2.nodeX + node2.nodeWidth;
			x1 = node1.nodeX;
		}
	}

	public void grabCells(Cell c)
	{
		int distance1 = (int)Math.sqrt((c.cellX + c.radius - x1)*(c.cellX + c.radius - x1) + (c.cellY + c.radius - y1)*(c.cellY + c.radius - y1));
		int distance2 = (int)Math.sqrt((c.cellX + c.radius - x2)*(c.cellX + c.radius - x2) + (c.cellY + c.radius - y2)*(c.cellY + c.radius - y2));
		//int length = (int)(Math.sqrt((x1-x2)*(x1-x2) + (y1 - y2)*(y1 - y2)));
		int length = 1000;
		if(distance1 > 2*c.radius && distance2 > 2*c.radius)
		{
			return;
		}
		if(c.connecting == false && Math.random() < SIRsim.PROB_TRANS)
		{
			c.connecting = true;
			if(distance1 <= 2*c.radius)
			{
				c.connectionDX = 2*c.radius*(x2 - x1)/length;
				c.connectionDY = 2*c.radius*(y2 - y1)/length;
				c.cellNode = node2;
				c.oldNode = node1;
			}
			else if(distance2 <= 2*c.radius)
			{
				c.connectionDX = 2*c.radius*(x1 - x2)/length;
				c.connectionDY = 2*c.radius*(y1 - y2)/length;
				c.cellNode = node1;
				c.oldNode = node2;
			}
		}
	}

	public void grabVaccinators(Vaccinator v)
	{
		int distance1 = (int)Math.sqrt((v.vX + v.radius - x1)*(v.vX + v.radius - x1) + (v.vY + v.radius - y1)*(v.vY + v.radius - y1));
		int distance2 = (int)Math.sqrt((v.vX + v.radius - x2)*(v.vX + v.radius - x2) + (v.vY + v.radius - y2)*(v.vY + v.radius - y2));
		//int length = (int)(Math.sqrt((x1-x2)*(x1-x2) + (y1 - y2)*(y1 - y2)));
		int length = 1000;
		if(distance1 > 2*v.radius && distance2 > 2*v.radius)
		{
			return;
		}
		if(v.connecting == false && Math.random() < SIRsim.PROB_TRANS)
		{
			v.connecting = true;
			if(distance1 <= 2*v.radius)
			{
				v.connectionDX = 2*v.radius*(x2 - x1)/length;
				v.connectionDY = 2*v.radius*(y2 - y1)/length;
				v.vNode = node2;
				v.oldNode = node1;
			}
			else if(distance2 <= 2*v.radius)
			{
				v.connectionDX = 2*v.radius*(x1 - x2)/length;
				v.connectionDY = 2*v.radius*(y1 - y2)/length;
				v.vNode = node1;
				v.oldNode = node2;
			}
		}
	}

}

class Button
{
	public int x;
	public int y;
	public int width;
	public int height;
	boolean clicked = false;
	String text;
	public Color normalColor = Color.gray;
	public Color clickedColor = Color.blue;

	public Button(int xIn, int yIn, int widthIn, int heightIn, String textIn)
	{
		x = xIn;
		y = yIn;
		width = widthIn;
		height = heightIn;
		text = textIn;
	}

	public void setClicked(boolean cIn)
	{
		clicked = cIn;
	}

	public boolean getClicked()
	{
		return clicked;
	}
}

class Slider
{
	public int x;
	public int y;
	public int width;
	public int height;
	public double value;
	public String name;
	
	public Slider(int xIn, int yIn, int widthIn, int heightIn, double valueIn, String nameIn)
	{
		x = xIn;
		y = yIn;
		width = widthIn;
		height = heightIn;
		value = valueIn;
		name = nameIn;
	}
	
	public int getBallX()
	{
		double radius = height/2;
		double w = width;
		double xPlace = x;
		int pos = (int)((value*w) - radius + xPlace);
		return pos;
	}
	
	public void setBallX(int xIn)
	{
		if(xIn > x && xIn < x + width)
		{
			double place1 = x;
			double place2 = xIn;
			double w = width;
			value = ((place2-place1)/w);
			if(name.equals("Vaccine Lifetime"))
			{
				SIRsim.vList[0].setLifetime(value);
			}
			else
			{
				double holder = SIRsim.MAX_PERIOD;
				SIRsim.PERIOD_S = (int)(holder*(1-value));
			}
			
		}
	}
	
}

class Graph
{
	public int x;
	public int y;
	public int width;
	public int height;
	
	
	public Graph(int xIn, int yIn, int widthIn, int heightIn)
	{
		x = xIn;
		y = yIn;
		width = widthIn;
		height = heightIn;
	}
	
	public int getPointHeight(int num)
	{
		double dnum = num;
		double value = dnum/SIRsim.NUM_CELLS;
		return y + height -(int)(value*height); 
	}
	
}
