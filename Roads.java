import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

public class Roads implements Runnable{

	private final static String newline = System.getProperty("line.separator");
	public static final String character = "o'";
	public static final String drawer = "  ";
	public static final String enemy = "><";
	public static final int TILE_SIZE = 32;
	public static final int ROOM_HEIGHT = 1024+512;
	public static final int ROOM_WIDTH = 1024+512;
	public int roomHeight;
	public int roomWidth;
	boolean paused = true;
	int maxX = 0;
	int maxY = 0;
	int maxDir = 0;
	//public static final int ROAD_LENGTH = 2;
	public static String currentFrame ="";
	public int roadLength=3;
	public ArrayList<String> gameReplay;
	public Stack<Integer> path;
	public static final int[][] DIRECTION = {
			{-1, -1}, //UP_LEFT
			{-1,  0}, //UP_MID
			{-1,  1}, //UP_RIGHT
			
			{ 0, -1}, //MID_LEFT
			{ 0,  0}, //MID_MID
			{ 0,  1}, //MID_RIGHT
			
			
			{ 1, -1}, //LOW_LEFT
			{ 1,  0}, //LOW_MID
			{ 1,  1}, //LOW_RIGHT
	};
	
	//COOOOOOOONSTAAAAAAAANTSSSSSSS
	private final static Integer 
		//to reference directions
//		LEFT=0,DOWN=1,RIGHT=2,
//		UP=3,DOWN_LEFT=4,DOWN_RIGHT=5,
//		UP_RIGHT=6,UP_LEFT=7,MID=8,
		
		//to reference walls
		WALL=0, PATH=1, OBJ=2,
		UP_LEFT=0, 	UP_MID=1, 	UP_RIGHT=2,
		MID_LEFT=3,	MID_MID=4,	MID_RIGHT=5,
		LOW_LEFT=6,	LOW_MID=7,	LOW_RIGHT=8;
		
//		//to reference paths
//		PATH=1,
//		UP_LEFT_PATH=0, 	UP_MID_PATH=1, 	UP_RIGHT_PATH=2,
//		MID_LEFT_PATH=3,	MID_MID_PATH=4, MID_RIGHT_PATH=5,
//		DOWN_LEFT_PATH=6,	DOWN_MID_PATH=7,DOWN_RIGHT_PATH=8;
	
	public static final Random rnd = new Random();
	public static final int ROAD_LENGTH = 3;
	public int roomMap[][];
	public int objectMap[][];
	int keyFrame;
	static String[] wallSprites = new String[]{"X ","X ","X ","X ","X ","X ","X ","X ","X "};
	static String[] pathSprites = new String[]{"  ","  ","  ","  ","  ","  ","  ","  ","  "};
	static String[][] gameSprites = new String[][]{
		wallSprites,
		pathSprites,
	};
	static String[] sprites = new String[]{"X ","  ","  ","  ","  ","X ","X ","@ "};
	static String[][] styles = new String[][]{
		{"X ","  ","  ","  ","  ","X ","X ","@ "},
		{"X ","  ","  ","  ","  ",". ",". ","@ "},
		{"  ","__","||","__","||","  ","X ","@ "},
		{"  ","  ","  ","  ","  ","  ","  ","@ "}
		};
	public static int tiles[] = {1,0}; //ROAD, WALL
	private Point player;
	
	public ArrayList<String> buildRoad(int x, int y, int size, int dir){
		ArrayList<String> buildProcess = new ArrayList<String>();
		boolean run = true;
		boolean can = true;
		boolean start = true;
		int pathMax = 0;
		//path.push(dir);
		while(run && size > 0){
			//print(x,y);
			//buildProcess.add(this.print(x, x,"  "));
			//goforward
			ArrayList<Integer> directions;
			//if you can still go backwards
			while(!path.isEmpty()||start){
				if(pathMax<path.size()){
					pathMax = path.size();
					maxX=x;
					maxY=y;
					maxDir=dir;
				}
				//System.out.println("x "+maxX+", y "+ maxY + ", maxSize " + pathMax+", size "+ path.size());
				start = false;
				//print(x,y);;
				currentFrame = this.getFrame();
				//initialize a random list of directions
				directions = 
						new ArrayList<Integer>(
								Arrays.asList(new Integer[]{MID_LEFT,LOW_MID,MID_RIGHT,UP_MID}));
				Collections.shuffle(directions);
				//check each direction to find a free direction
				//if it is free, build the road and store the direction
				//otherwise remove the direction and keep searching
				can = false;
//				System.out.println();
				while(!directions.isEmpty() && can == false){
					//print(x,y);
					if(can = checkFree(x,y,size,dir=directions.remove(0),roomMap)){
						for(int i = 0; i < roadLength; i++){
							//System.out.println(this.print(x,y));
							//buildProcess.add(this.print(x,y,drawer));
							roomMap[x][y]=1;
//							roomMap[x][y]=path.size()+1;
							x += DIRECTION[dir][0];
							y += DIRECTION[dir][1];
						}
						size--;
					}
//					System.out.println(can);
//					System.out.println(x +  ", " + y);
//					System.out.println(dir);
//					System.out.println(path.size());
				}
				//if there was nowhere to go
				if(can){
					path.push(dir);
				}else{
					//get the last traveled direction 
					//go that direction
					dir=path.pop();
					x -= (roadLength)*DIRECTION[dir][0];
					y -= (roadLength)*DIRECTION[dir][1];
					//buildProcess.add(this.print(x,y,drawer));
//					dir=path.peek();
//					x -= DIRECTION[dir][0];
//					y -= DIRECTION[dir][1];
				}
			}
			run = can;
		}
//		for(int i=1;i<roomWidth/TILE_SIZE-1;i++){
//			for(int j=1;j<roomHeight/TILE_SIZE-1;j++){
//				boolean surroundedByRoad = true;
//				boolean touchAtLeastOneRoad = false;
//				boolean touchAtLeastOneWall = false;
//				boolean isWall = false;
//				if(roomMap[i][j]==0||roomMap[i][j]==5||roomMap[i][j]==6)
//					isWall=true;
//				for(int k=0;k<4;k++){
//					int check = roomMap[i+DIRECTION[k][0]][j+DIRECTION[k][1]];
//					//check if all adjacent blocks are road
//					//and if this block is wall
//					if(check>0&&check<5&&isWall){
//						roomMap[i][j]=5;
//						touchAtLeastOneRoad=true;
//					}
//					if(check==0||check==5||check==6){
//						touchAtLeastOneWall=true;
//					}
//
//				}
//				if(!touchAtLeastOneWall&&isWall)
//					roomMap[i][j]=6;
//			}
//		}
		paused = false;
		return buildProcess;
	}
	
	private boolean checkFree(int x, int y, int size, int dir, int[][] map){	
		boolean can = true;
		for(int i = 0; i < roadLength+1; i++){
			if(inBounds(x,y)){
				if(i>0){
					if(roomMap[x][y]>0)
						can= false;
				}else if(roadLength==1)
					if(roomMap[x][y]>0)
						can= false;
			}else if(i < roadLength)
				can= false;
				
			x += DIRECTION[dir][0];
			y += DIRECTION[dir][1];
		}
		return can;
	}
	
	private boolean inBounds(int x, int y){
		if(x<0 || x>=roomWidth/TILE_SIZE ||
				y<0 || y>=roomHeight/TILE_SIZE)
			return false;
		return true;
	}
	
	private boolean inBounds(Point p){
		return inBounds(p.x,p.y);
	}
	
	public void build(){
		gameReplay = buildRoad(roomWidth/TILE_SIZE/2,roomHeight/TILE_SIZE/2,2000,0);
		keyFrame = gameReplay.size();
	}
	
//	public void reset(){
//		maxX=maxY=0;
//		Px = roomWidth/TILE_SIZE/2;
//		Py = roomHeight/TILE_SIZE/2;
//		roomMap = new int[roomWidth/TILE_SIZE][roomHeight/TILE_SIZE];
//		path = new Stack<Integer>();
//		print(Px,Py,character);
//		build();
//	}
	
	public Roads (){
		roadLength=ROAD_LENGTH;
		roomWidth = ROOM_WIDTH;
		roomHeight = ROOM_HEIGHT;
		player = new Point(roomWidth/TILE_SIZE/2,roomHeight/TILE_SIZE/2);
		roomMap = new int[roomWidth/TILE_SIZE][roomHeight/TILE_SIZE];
		path = new Stack<Integer>();
		gameReplay = new ArrayList<String>();
	}
	
	public Roads (int length, int width, int height){
		this(length);
		roomWidth = width*TILE_SIZE;
		roomHeight = height*TILE_SIZE;
	}
	
	public Roads (int length){
		this();
		roadLength=length;
	}
	
//	public void rebuild (int length){
//		Px = roomWidth/TILE_SIZE/2;
//		Py = roomHeight/TILE_SIZE/2;
//		roomMap = new int[roomWidth/TILE_SIZE][roomHeight/TILE_SIZE];
//		path = new Stack<Integer>();
//		roadLength=length;
//		build();
//	}
	
	public String getFrame(){
		String result ="X ";
		for(int i=0;i<roomHeight/TILE_SIZE +  1;i++)
			result+="X ";
		result+=newline+"X ";
		for(int i=0;i<roomWidth/TILE_SIZE;i++){
			for(int j=0;j<roomHeight/TILE_SIZE;j++){
				int toPrint = 0;
				Point p = new Point(i,j);
				boolean touchAtLeastOneRoad = false;
				boolean touchAtLeastOneWall = false;
				boolean isWall = false;
				//locality array 
				//(describes the immediate 
				//surroundings of current block
				boolean[] l = new boolean[9]; 
				for(int dir=0;dir<DIRECTION.length;dir++){
					l[dir]=isWall(move(p,dir));
				}
				int spriteType;
				if(l[MID_MID])
					spriteType = 0;
				else
					spriteType = 1;
				//if the center is a wall
//				if(l[MID]){
//					type=0;
//					if(l[UP_MID_WALL])
//				}else{
//					type=1;
//				}
				
				if(player.x == i && player.y == j)
					result+=character;
				else if(maxX-DIRECTION[maxDir][0] == i && maxY-DIRECTION[maxDir][1] == j)
					result+=sprites[7];
				else
					result+=gameSprites[spriteType][0];
					//result+=(roomMap[i][j] > 0)? "  " : "X ";
					//result+=(roomMap[i][j] > 0)? roomMap[i][j]-1 + " ": "  ";
					//result+=(roomMap[i][j] > 10)? roomMap[i][j]-1 : ((roomMap[i][j]>0)? roomMap[i][j]-1+" ":"  ");
				
			}
			result+="X" + newline+"X ";
		}
		for(int i=0;i<roomHeight/TILE_SIZE +  1;i++)
			result+="X ";
		result+=newline;
		//System.out.println(result);
		return result;
	}
	
	private boolean isWall(int x, int y) {
		return isWall(new Point(x,y));
	}

	public boolean[][] buildLocalityArray(boolean[] linearArray){
		boolean[][] localityArray = new boolean[3][3];
		for(int i = 0; i < 3; i++)
			for(int j = 0; j< 3; j++)
				localityArray[i][j]=linearArray[i+j];
		return localityArray;
	}
	
	@Override
	public void run() {
		
		gameReplay = buildRoad(roomWidth/TILE_SIZE/2,roomHeight/TILE_SIZE/2,2000,0);
	}
	
	public boolean isWall(Point p){
		return roomMap[p.x][p.y]==0;
	}
	
	public boolean didPlayerWin(int keyCode){
		int dir=MID_MID;
		switch( keyCode ) { 
        case KeyEvent.VK_UP:
        	dir = UP_MID;
            break;
        case KeyEvent.VK_DOWN:
        	dir = LOW_MID;
            break;
        case KeyEvent.VK_LEFT:
        	dir = MID_LEFT;
            break;
        case KeyEvent.VK_RIGHT :
        	dir = MID_RIGHT;
            break;
        default:
        	break;
		}
		System.out.println(paused);
		System.out.println(dir);
		if(!paused)
			player = moveWalk(player,dir);
		gameReplay.add(currentFrame = getFrame());
		if(player.x==maxX-DIRECTION[maxDir][0]&&player.y==maxY-DIRECTION[maxDir][1]){
			return true;
		}else 
			return false;
			
		//System.out.println(this.print(x, y,drawer));
	}
	public Point moveWalk(Point p, int dir){
		return moveWalk(p,dir,1);
	}
	
	public Point moveWalk(Point p, int dir, int mag){
		Point point = move(p,dir,mag);
		if(isWall(point))
				return p;
		else
			return point;
	}
	
	public Point move(Point p, int dir, int mag){
		Point point = new Point(p);
		int x = p.x + mag*DIRECTION[dir][0];
		int y = p.y + mag*DIRECTION[dir][1];
		if(inBounds(x,y)){
			point = new Point(x,y);
		}
		return point;
	}
	
	public Point move(Point p, int dir){
		return move(p,dir,1);
	}
	
	public Point move(int x, int y, int dir, int mag){
		return move(new Point(x,y),dir,mag);
	}
	
	public Point move(int x, int y, int dir){
		return move(new Point(x,y),dir,1);
	}
	
//	public boolean update(){
//		//int dir = keyCode-37;
//		int x = Px;// + DIRECTION[dir][0];
//		int y = Py;// + DIRECTION[dir][1];
//		if(inBounds(x,y)&&!paused)
//			if(roomMap[x][y]>0&&roomMap[x][y]<5){
//				Px = x;
//				Py = y;
//				print(Px,Py,character);
//			}
//		//gameReplay.add(this.print(Px, Py,character));
//		if(Px==maxX-DIRECTION[maxDir][0]&&Py==maxY-DIRECTION[maxDir][1]){
//			return true;
//		}else 
//			return false;
//			
//		//System.out.println(this.print(x, y,drawer));
//	}

	
	public void clear() {

	}
}
