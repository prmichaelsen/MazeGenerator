import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
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
				+ "new [1-r] [1-t] [1-w] [1-h]"
				+ "\n"
				+ "creates a new maze"
				+ "\nr - road length (default 3)"
				+ "\nt - road thickness (default 1)" 
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
				+ "replay [s]"
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
		
		public static final int DEFAULT_REPLAY_SPEED = 100;
		public static final int DEFAULT_GAME_SPEED = 25;
		int replaySpeed = DEFAULT_REPLAY_SPEED;
		int gameSpeedInMilli = 1000/DEFAULT_GAME_SPEED;
		public ArrayList<Integer> keysDown = new ArrayList<Integer>();
		
		
		boolean delay = false;
		boolean keyPress = false;
		int keyDown = 0;
		static double nextFrame = 0;
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
		private static String[] fonts = {
				"consolas",
//				"courier",
				"courier new",
//				"dejavu sans mono",
				"lucida console",
				"lettergothicstd",
				"lucida sans typewriter",
				"oratorstd",
				"ocrastd",
				"prestigeelitestd"
//				"monaco",
//				"lucida sans unicode",
//				"fixedsys excelsior",
//				"everson mono",
		} ;
		private int fontIndex = 0;
		private String lastCommand;
	    
		public KeyListenerFrame() {
			componentList = new ArrayList<Component>();
		}

		private static void createAndShowGUI() {
			
			Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
			ArrayList<String> monospacedFonts = new ArrayList<String>();
			
			for(int i = 0; i < allFonts.length;i++){
				for(int j = 0; j < fonts.length;j++){
					Font f = Font.decode(fonts[j]);
					String fam = f.getFamily();
					System.out.println(fam);
					if(allFonts[i].getFamily().equals(fam)
							&& !allFonts[i].getFamily().equals(Font.DIALOG)
							&& !allFonts[i].getName().equals("Lucida Sans Typewriter Bold Oblique")
							&& !allFonts[i].getName().equals("Lucida Sans Typewriter Oblique"))
						monospacedFonts.add(allFonts[i].getFontName());
				}
			}
			
			System.out.println(monospacedFonts);
			
			fonts = monospacedFonts.toArray(fonts);
			
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
	        
	        Timer graphicsClock = new Timer();
	        graphicsClock.scheduleAtFixedRate(new TimerTask() {
	        	  @Override
	        	  public void run() {
	        		Thread.currentThread().setName("Graphics Clock");
	        	    try {
	        	    	if(frame.game!=null){
	        	    		TimeUnit.MILLISECONDS.sleep(frame.gameSpeedInMilli);
	        	    		frame.typingArea.requestFocusInWindow();
	        	    		if(!frame.game.building)
	        	    			frame.updateGraphics();
	        	    		updateInfo();
	        	    	}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	  }
	        	}, 1000/60, 1000/60); //60 fps
	        
	        Timer logicClock = new Timer();
	        logicClock.scheduleAtFixedRate(
	        	new TimerTask() {
	        		@Override
	        		public void run() {
	        			Thread.currentThread().setName("Logic Clock");
	        			if(frame.game!=null){
	        				ArrayList<Integer> tempKeysDown = 
	        						new ArrayList<Integer>(frame.keysDown);
	        				for(int keyDown:tempKeysDown){
	        					frame.game.movePlayer(keyDown);
	        				}
	        				if(frame.playReplay = frame.game.checkWin()){
	        					frame.infoPanel.setText("CONGRATULATIONS, YOU WON!");
	        				}
	        			}
	        		}
	        	}, 1000/10, 1000/10); //10 cps
			}
		
		protected void updateGraphics() throws InterruptedException {
			if(!playReplay){
				frame.map.setText(game.getFrame());
			}else{
				if(nextFrame <0)
					nextFrame = game.gameReplay.size() - 1;
				if(nextFrame<game.gameReplay.size() ){
					frame.map.setText(game.gameReplay.get((int)nextFrame));
					nextFrame+=1*replaySpeed/100.0;
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
		        component.setFont(new Font(fonts[fontIndex], Font.PLAIN, 18));
		        component.setForeground(Color.GREEN);
		        component.setBackground(Color.BLACK);
	        }
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
//			if(keyPress)
//				return;
//			keyDown = keyCode;
//			keyPress=true;
		    switch( keyCode ) { 
		        case KeyEvent.VK_UP:
		        case KeyEvent.VK_DOWN:
		        case KeyEvent.VK_LEFT:
		        case KeyEvent.VK_RIGHT :
		        	if(!keysDown.contains(keyCode))
						keysDown.add(keyCode);
		            break;
		        case KeyEvent.VK_ENTER :
		        	StringTokenizer command;
		        	if(typingArea.getText().equals("\n")){
		        		command = new StringTokenizer(lastCommand);
		        	}
		        	else{
		        		command = new StringTokenizer(typingArea.getText());
		        		lastCommand = typingArea.getText();
		        	}
		        	
		        	typingArea.setText(null);
		        	switch(command.nextToken()){
		        	case "font":{
		        		String font = fonts[(++fontIndex)%fonts.length];
		        		for(Component component:componentList){
		    		        component.setFont(new Font(font, Font.PLAIN, 18));
		    	        }
		        		
		        		//System.out.println(frame.getFont());
		        		infoPanel.setText("CHANGED FONT TO " + font + " " + fontIndex%fonts.length + "/" + fonts.length);
		        		typingArea.setText(null);
		        		break;
		        	}
		        	case "fontb":{
		        		String font = fonts[(--fontIndex)%fonts.length];
		        		for(Component component:componentList){
		    		        component.setFont(new Font(font, Font.PLAIN, 18));
		    	        }
		        		
		        		//System.out.println(frame.getFont());
		        		infoPanel.setText("CHANGED FONT TO " + font + " " + fontIndex%fonts.length + "/" + fonts.length);
		        		typingArea.setText(null);
		        		break;
		        	}
		        	case "pause":{
		        		frame.game.paused=!frame.game.paused;
		        		typingArea.setText(null);
		        		break;
		        	}
		        	case "t":{
		        		frame.game.useSprites=!frame.game.useSprites;
		        		frame.game.assignShit();
		        		typingArea.setText(null);
		        		break;
		        	}
		        	case "p":{
		        		frame.game.lookAtPaths=!frame.game.lookAtPaths;
		        		frame.game.assignShit();
		        		typingArea.setText(null);
		        		break;
		        	}
		        	case "speed":{
		        		infoPanel.setText("CHANGING GAME SPEED");
	        			int speed = DEFAULT_GAME_SPEED;
	        			if(command.hasMoreTokens())
	        				speed = Integer.parseInt(command.nextToken()); 
	        			if(speed>0&&speed<=100)
	        				gameSpeedInMilli=1000/speed;
	        			else
	        				replaySpeed=DEFAULT_REPLAY_SPEED;
	        			typingArea.setText(null);
	        			break;
		        	}
		        	case "replay":{
		        		infoPanel.setText("CHANGING REPLAY SPEED");
	        			int speed = DEFAULT_REPLAY_SPEED;
	        			if(command.hasMoreTokens())
	        				speed = Integer.parseInt(command.nextToken()); 
	        			//if(speed>0&&speed<=100)
	        				replaySpeed=speed;
	        			//else
	        				//replaySpeed=DEFAULT_REPLAY_SPEED;
	        			typingArea.setText(null);
	        			break;
		        	}
		        	case "new":{
		        		infoPanel.setText("GENERATING NEW MAP");
		        		typingArea.setText("");
	        			int roadLength = 0, roadWidth = 0,
	        					width = 0, height = 0;
	        			if(command.hasMoreTokens())
	        				roadLength = Integer.parseInt(command.nextToken()); 
	        			if(command.hasMoreTokens())
	        				roadWidth = Integer.parseInt(command.nextToken()); 
	        			if(command.hasMoreTokens())
	        				width = Integer.parseInt(command.nextToken()); 
	        			if(command.hasMoreTokens())	
	        				height = Integer.parseInt(command.nextToken());
	        			
	        			if(roadLength<1)
	        				roadLength= Roads.ROAD_LENGTH;
	        			if(roadWidth<1)
	        				roadWidth=Roads.ROAD_WIDTH;
	        			if(width<1)
	        				width=Roads.ROAD_WIDTH;;
	        			if(height<1)
	        				height=Roads.ROOM_HEIGHT;
	        			//(new Thread (frame.game = new Roads(roadLength,roadWidth,width,height))).start();
	        			frame.game.rebuild(roadLength,roadWidth,width,height);
	        			break;
		        	}
		        	case "style":{
		        		infoPanel.setText("CHANGING STYLE");
		        		typingArea.setText("");
	        			if(command.hasMoreTokens())
	        				style = Integer.parseInt(command.nextToken()); 
	        			else
	        				style=(style+1)%Roads.styles.length;
	        			Roads.sprites=Roads.styles[style];
	        			break;
		        	}
		        	case "color":{
		        		infoPanel.setText("CHANGING FOREGROUND COLOR");
		        		typingArea.setText("");
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
	        			
	        			for(Component component:componentList){
	        		        component.setForeground(changeForeColor);
	        	        }

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
	        			
	        			for(Component component:componentList){
	        		        component.setBackground(changeBackColor);
	        	        }
	        			
	        			typingArea.setText("");
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
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
//			asynch stop
//			keyPress = false;
			keysDown.remove(new Integer(keyCode));
//			if(keysDown.isEmpty())
//				frame.game.didPlayerWin(0);
//			if(!keyPress)
//				return;
//			else if(keyCode!=keyDown)
//				return;
//			keyPress=false;
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