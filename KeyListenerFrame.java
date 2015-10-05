import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class KeyListenerFrame extends JFrame implements KeyListener{

		/**
		 * 
		 */
	
	//GUI fields
	private JTextArea typingArea;
	private JTextArea map;
	private JTextArea infoPanel;
	private JPanel console;
	private JTextArea info;
	
		int style = 0;
		private boolean playReplay = false;
		static long initialtime = System.currentTimeMillis();
		static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
		private static final long serialVersionUID = 1L;
		private static final String INFO = 
				"\n"
				+ "Commands: "
				+ "\n"
				+ "\n"
				+ "new [1-n] [1-w] [1-h]"
				+ "\n"
				+ "creates a new maze"
				+ "\nn - road length (default 3)" 
				+ "\nw - maze width (default 48)"
				+ "\nh - maze height (default 48)"
				+ "\n"
				+ "\n"
				+ "style [0-"+(Roads.styles.length-1)+"]"
				+ "\n"
				+ "changes style"
				+ "\n"
				+ "\n"
				+ "color [0-11] and colorbg [0-11]"
				+ "\n"
				+ "changes color"
				+ "\n"
				+ "\n"
				+ "replay [1-100]"
				+ "\n"
				+ "adjusts replay speed"
				+ "\n"
				+ "\n"
				+ "game [1-100]"
				+ "\n"
				+ "adjusts game speed"
				+ "\n"
				+ "\n"
				+ "Hints: "
				+ "\nTry using the commands "
				+ "\nwithout parameters!"
				+ "\nTry style 3!"
				+ "\nTry dimensions that are"
				+ "\nmultiples of your road"
				+ "\nlength!";
		
		public static final int DEFAULT_REPLAY_SPEED = 20;
		public static final int DEFAULT_GAME_SPEED = 25;
		int replaySpeedInMilli = 1000/DEFAULT_REPLAY_SPEED;
		int gameSpeedInMilli = 1000/DEFAULT_GAME_SPEED;
		public ArrayList<Integer> keysDown = new ArrayList<Integer>();
		
		
		
		boolean keyPress = false;
		int keyDown = 0;
		static int nextFrame = 0;
	    final String newline = System.getProperty("line.separator");
	    Roads game;
		
	    static int buildSpeed = 8;
	    static int defaultRoadLength= Roads.ROAD_LENGTH;
	    static KeyListenerFrame frame;
	    static Timer showBuild;
	    static Thread gameInstance;
	    static Color[] colorArray = {
	    		Color.GREEN,
	    		Color.BLACK,
	    		Color.WHITE, 
	    		Color.BLUE,
	    		Color.LIGHT_GRAY,
	    		Color.RED,
	    		Color.YELLOW,
	    		Color.GRAY,
	    		Color.PINK,
	    		Color.CYAN,
	    		Color.ORANGE,
	    		Color.MAGENTA,
	    };
	    int currentForeColorIndex = 0;
	    int currentBackColorIndex = 1;
	    Timer updateGraphicsTimer;
	    private ArrayList<Component> componentList;
	    
		public KeyListenerFrame() {
			componentList = new ArrayList<Component>();
		}

		private static void createAndShowGUI() {
			//identify thread
			Thread.currentThread().setName("Graphic Instance");
			
			//Create and set up the window.
			frame = new KeyListenerFrame();
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	         
	        //Set up the content pane.
	        frame.addComponentsToPane();
	         
	        //Display the window.
	        frame.pack();
	        frame.setSize(Roads.ROOM_WIDTH, Roads.ROOM_HEIGHT);
	        frame.setBackground(Color.BLACK);
	        frame.setVisible(true);   
	        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	        
	        //create game
	        (gameInstance = new Thread(frame.game = new Roads())).start();
	        gameInstance.setName("Game Instance");
	        
	        Timer gameClock = new Timer();
	        gameClock.scheduleAtFixedRate(new TimerTask() {
	        	  @Override
	        	  public void run() {
	        		//Thread.currentThread().setName("Game Clock");
	        	    try {
	        	    	if(frame.game!=null){
	        	    		if(frame.keyPress)
	        	    			frame.game.didPlayerWin(frame.keyDown);
	        	    		frame.typingArea.requestFocusInWindow();
	        	    		frame.updateGraphics();
	        	    		TimeUnit.MILLISECONDS.sleep(frame.gameSpeedInMilli);
	        	    		updateInfo();
	        	    	}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	  }
	        	}, 1, 1);
		}
		
		protected void updateGraphics() throws InterruptedException {
			if(!playReplay)
				frame.map.setText(game.getFrame());
			else{
				if(nextFrame<game.gameReplay.size()){
					if(nextFrame>game.keyFrame)
						TimeUnit.MILLISECONDS.sleep(replaySpeedInMilli);
					frame.map.setText(game.gameReplay.get(nextFrame));
					nextFrame++;
				}
				else{
					playReplay=false;
					nextFrame=0;
				}
			}
		}

		protected static void updateInfo() {
			//get date and offset by 7500
			Date date = new Date(System.currentTimeMillis()-initialtime-7500*60*60);
			frame.info.setText("Time: " + sdf.format(date) + INFO);
		}

		private void addComponentsToPane() {
			infoPanel = new JTextArea();
			infoPanel.setText("WELCOME! ENTER COMMAND OR PRESS ARROW KEYS TO PLAY!");
			
			typingArea = new JTextArea();
	        typingArea.addKeyListener(this);
	        
	        console = new JPanel();
	        console.setLayout(new BorderLayout());
	        console.add(infoPanel,BorderLayout.PAGE_START);
	        console.add(typingArea,BorderLayout.PAGE_END);
	        
	        map = new JTextArea();
	        map.setBounds(0, 0, Roads.ROOM_WIDTH/Roads.TILE_SIZE, Roads.ROOM_HEIGHT/Roads.TILE_SIZE);

	        info = new JTextArea(); 
	       
	        getContentPane().add(map, BorderLayout.CENTER);
	        getContentPane().add(info, BorderLayout.EAST);
	        getContentPane().add(console, BorderLayout.SOUTH);
	        
	        frame.componentList = getAllComponents(frame);
	        for(Component component:componentList){
		        component.setFont(new Font("monospaced", Font.PLAIN, 18));
		        component.setForeground(Color.GREEN);
		        component.setBackground(Color.BLACK);
	        }
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			//if(keyPress)
			//	return;
			keyDown = keyCode;
			//keysDown.add(keyCode);
			keyPress=true;
		    switch( keyCode ) { 
		        case KeyEvent.VK_UP:
		            break;
		        case KeyEvent.VK_DOWN:
		            break;
		        case KeyEvent.VK_LEFT:
		            break;
		        case KeyEvent.VK_RIGHT :
		            break;
		        case KeyEvent.VK_ENTER :
		        	StringTokenizer command = new StringTokenizer(typingArea.getText());
		        	typingArea.setText(null);
		        	switch(command.nextToken()){
		        	case "speed":{
		        		infoPanel.setText("CHANGING GAME SPEED");
	        			int speed = DEFAULT_GAME_SPEED;
	        			if(command.hasMoreTokens())
	        				speed = Integer.parseInt(command.nextToken()); 
	        			if(speed>0&&speed<=100)
	        				gameSpeedInMilli=1000/speed;
	        			else
	        				replaySpeedInMilli=1000/DEFAULT_REPLAY_SPEED;
	        			typingArea.setText(null);
	        			break;
		        	}
		        	case "replay":{
		        		infoPanel.setText("CHANGING REPLAY SPEED");
	        			int speed = DEFAULT_REPLAY_SPEED;
	        			if(command.hasMoreTokens())
	        				speed = Integer.parseInt(command.nextToken()); 
	        			if(speed>0&&speed<=100)
	        				replaySpeedInMilli=1000/speed;
	        			else
	        				replaySpeedInMilli=1000/DEFAULT_REPLAY_SPEED;
	        			typingArea.setText(null);
	        			break;
		        	}
		        	case "new":{
		        		infoPanel.setText("GENERATING NEW MAP");
	        			int roadLength = Roads.ROAD_LENGTH;
	        			int width = Roads.ROOM_HEIGHT;
	        			int height = Roads.ROOM_WIDTH;
	        			if(command.hasMoreTokens())
	        				roadLength = Integer.parseInt(command.nextToken()); 
	        			if(command.hasMoreTokens())
	        				width = Integer.parseInt(command.nextToken()); 
	        			if(command.hasMoreTokens())	
	        				height = Integer.parseInt(command.nextToken());
	        			
	        			if(roadLength<1)
	        				roadLength=1;
	        			if(width<1)
	        				width=1;
	        			if(height<1)
	        				height=1;
	        			
	        			frame.game.clear();
	        			(gameInstance = new Thread(frame.game = new Roads(roadLength,width,height))).start();
	        			break;
		        	}
		        	case "style":{
		        		infoPanel.setText("CHANGING STYLE");
	        			if(command.hasMoreTokens())
	        				style = Integer.parseInt(command.nextToken()); 
	        			else
	        				style=(style+1)%Roads.styles.length;
	        			Roads.sprites=Roads.styles[style];
	        			typingArea.setText("");
	        			break;
		        	}
		        	case "color":{
		        		infoPanel.setText("CHANGING FOREGROUND COLOR");
    					Color changeForeColor = colorArray[currentForeColorIndex];
	        			if(command.hasMoreTokens()){
	        				int input = Integer.parseInt(command.nextToken())%colorArray.length;
	        				if(input>=0)
	        					currentForeColorIndex=input;
	        			}else{
	        				currentForeColorIndex = (currentForeColorIndex+1) % colorArray.length;
	        			}
	        			
	        			while(currentForeColorIndex==currentBackColorIndex)
	        				currentForeColorIndex=(currentForeColorIndex+1)%colorArray.length;
	        			
	        			changeForeColor = colorArray[currentForeColorIndex];
	        			
	        			map.setForeground(changeForeColor);
	        			infoPanel.setForeground(changeForeColor);
	        			typingArea.setForeground(changeForeColor);
	        			info.setForeground(changeForeColor);
	        			typingArea.setText("");
	        			break;
		        	}
		        	case "colorbg":{
		        		infoPanel.setText("CHANGING BACKROUND COLOR");
	        			Color changeBackColor = colorArray[currentBackColorIndex];
	        			if(command.hasMoreTokens()){
	        				int input = Integer.parseInt(command.nextToken())%colorArray.length;
	        				if(input>=0)
	        					currentBackColorIndex=input;
	        			}else{
	        				currentBackColorIndex = (currentBackColorIndex+1) % colorArray.length;
	        			}
	        			
	        			while(currentForeColorIndex==currentBackColorIndex)
	        				currentBackColorIndex=(currentBackColorIndex+1)%colorArray.length;
	        			
	        			changeBackColor = colorArray[currentBackColorIndex];
	        			
	        			map.setBackground(changeBackColor);
	        			infoPanel.setBackground(changeBackColor);
	        			typingArea.setBackground(changeBackColor);
	        			info.setBackground(changeBackColor);
	        			break;
		        	}
		        	default:{
		        			infoPanel.setText("COMMAND INVALID");
		        			break;
		        	}
		        	}
				break;
		        default:
		     }
//		    if(update){
//		    	if(game.didPlayerWin(keyCode)){
//		    		frame.game.clear();
//        			infoPanel.setText("CONGRATULATIONS, YOU WON!");
//        			playReplay=true;
//		    	}
//		    }
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if(!keyPress)
				return;
			else if(keyCode!=keyDown)
				return;
			keyPress=false;
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		//helper method from
		//http://stackoverflow.com/questions/6495769/how-to-get-all-elements-inside-a-jframe
		public static ArrayList<Component> getAllComponents(final Container c) {
		    Component[] comps = c.getComponents();
		    ArrayList<Component> compList = new ArrayList<Component>();
		    for (Component comp : comps) {
		        compList.add(comp);
		        if (comp instanceof Container)
		            compList.addAll(getAllComponents((Container) comp));
		    }
		    return compList;
		}
		public static void main(String[] args) throws IOException{
			//Schedule a job for event dispatch thread:
	        //creating and showing this application's GUI.
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
	        
	        
		}
		

}