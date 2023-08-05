import java.awt.Point;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

public class Roads implements Runnable{

	private final static String newline = System.getProperty("line.separator");
	public static String character = " o' ";
	public static String goal = " @  ";
	public static final String enemy = "><";
	//COOOOOOOONSTAAAAAAAANTSSSSSSS
		private final static Integer 
			//to reference walls
			WALL=0, PATH=1, OBJ=2,
			UP_LEFT=0, 	UP_MID=1, 	UP_RIGHT=2,
			MID_LEFT=3,	MID_MID=4,	MID_RIGHT=5,
			LOW_LEFT=6,	LOW_MID=7,	LOW_RIGHT=8,
		
			UP_LEFT_CORNER_WALL = 27,
			UP_RIGHT_CORNER_WALL = 54,
			UP_MID_WALL = 63,
			NILL = 0;		
	public static final int TILE_SIZE = 16;
	public static final int ROOM_HEIGHT = 1024+512;
	public static final int ROOM_WIDTH = 1024+512;
	public static final int ROAD_LENGTH = 3;
	public static final int ROAD_WIDTH = 1;
	public int roomHeight;
	public int roomWidth;
	boolean paused = true;
	int maxX = 0;
	int maxY = 0;
	int maxDir = 0;
	//public static final int ROAD_LENGTH = 2;
	public static String currentFrame ="";
	public int roadLength;
	public int roadWidth;
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
	
	

	
	public static final Random rnd = new Random();
	public int roomMap[][];
	public int objectMap[][];
	int keyFrame;
	static String[] wallSprites = new String[512];//{};
	static String[] pathSprites = new String[512];//{"  ","  ","  ","  ","  ","  ","  ","  ","  "};
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
	public boolean building = true;
	public boolean useSprites = true;
	public boolean lookAtPaths = true;
	boolean lookAtWalls;

	public ArrayList<String> buildRoad(int x, int y){
		
		assignShit();
		
//		for(int i=0; i < roomMap.length; i++){
//			//generate right wall
//			roomMap[i][0] = 1;
//			
//			//generate left wall
//			roomMap[i][roomMap[0].length-1] = 1;
//		}
//		
//		for(int i=0; i < roomMap[0].length; i++){
//			
//			//generate top wall
//			roomMap[0][i] = 1;
//			
//			//generate bottom wall
//			roomMap[roomMap.length-1][i] = 1;
//		}
		
		
		
		
		ArrayList<String> buildProcess = new ArrayList<String>();
		int dir = 0;
		boolean run = true;
		boolean can = true;
		boolean start = true;
		int pathMax = 0;
		while(run){
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
				building = start = false;
				//initialize a random list of directions
				directions = 
						new ArrayList<Integer>(
								Arrays.asList(new Integer[]{MID_LEFT,LOW_MID,MID_RIGHT,UP_MID}));
				Collections.shuffle(directions);
				//check each direction to find a free direction
				//if it is free, build the road and store the direction
				//otherwise remove the direction and keep searching
				can = false;
				while(!directions.isEmpty() && can == false){
					if(can = checkFree(x,y,dir=directions.remove(0))){
						for(int i = 0; i < roadLength; i++){
							//for(int j=0; j <roadWidth; j++){
								getFrame();
								roomMap[x][y]=1;
								x += DIRECTION[dir][0];
								y += DIRECTION[dir][1];
							//}
						}
					}
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
				}
			}
			run = can;
		}
		
//		for(int i=0; i < roomMap.length; i++){
//			//generate right wall
//			roomMap[i][0] = 0;
//			
//			//generate left wall
//			roomMap[i][roomMap[0].length-1] = 0;
//		}
//		
//		for(int i=0; i < roomMap[0].length; i++){
//			
//			//generate top wall
//			roomMap[0][i] = 0;
//			
//			//generate bottom wall
//			roomMap[roomMap.length-1][i] = 0;
//		}
		
		paused = false;
		return buildProcess;
	}
	
	private boolean checkFree(int x, int y, int dir){	
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
		paused = true;
		gameReplay = buildRoad(roomWidth/TILE_SIZE/2 ,roomHeight/TILE_SIZE/2 );
		keyFrame = gameReplay.size();
	}
	
	public Roads (){
		roadLength=ROAD_LENGTH;
		roadWidth=ROAD_WIDTH;
		
		this.roomWidth = ROOM_WIDTH;
		this.roomHeight = ROOM_HEIGHT;
		
		this.roadLength=3;
		this.roadWidth=1;
		
		this.roomWidth = 30*TILE_SIZE;
		this.roomHeight = 30*TILE_SIZE;
		
		player = new Point(this.roomWidth/TILE_SIZE/2 ,this.roomHeight/TILE_SIZE/2 );
		roomMap = new int[this.roomWidth/TILE_SIZE ][this.roomHeight/TILE_SIZE ];
		path = new Stack<Integer>();
		gameReplay = new ArrayList<String>();
		
		//getFrame();
	}
	
	public Roads (int roadLength, int roadWidth,int roomWidth, int roomHeight){
		this.roadLength=roadLength;
		this.roadWidth=roadWidth;
		
		this.roomWidth = roomWidth*TILE_SIZE;
		this.roomHeight = roomHeight*TILE_SIZE;
		
		player = new Point(this.roomWidth/TILE_SIZE/2 + 1,this.roomHeight/TILE_SIZE/2 + 1);
		roomMap = new int[this.roomWidth/TILE_SIZE + 2][this.roomHeight/TILE_SIZE + 2];
		path = new Stack<Integer>();
		gameReplay = new ArrayList<String>();
		
		//getFrame();
	}
	
	public void rebuild(int roadLength, int roadWidth,int roomWidth, int roomHeight){
		
		this.roadLength=roadLength;
		this.roadWidth=roadWidth;
		
		this.roomWidth = roomWidth*TILE_SIZE;
		this.roomHeight = roomHeight*TILE_SIZE;
		
		player = new Point(this.roomWidth/TILE_SIZE/2 ,this.roomHeight/TILE_SIZE/2 );
		roomMap = new int[this.roomWidth/TILE_SIZE][this.roomHeight/TILE_SIZE ];
		path = new Stack<Integer>();
		gameReplay = new ArrayList<String>();
		
		build();
	}
	
//	public Roads (int roadLength, int roadWidth){
//		this.roadLength=roadLength;
//		this.roadWidth=roadWidth;
//		
//		this.roomWidth = roomWidth*TILE_SIZE;
//		this.roomHeight = roomHeight*TILE_SIZE;
//		
//		player = new Point(roomWidth/TILE_SIZE/2,roomHeight/TILE_SIZE/2);
//		roomMap = new int[roomWidth/TILE_SIZE][roomHeight/TILE_SIZE];
//		path = new Stack<Integer>();
//		gameReplay = new ArrayList<String>();
//	}
	static boolean printOnce = false;
	
	public String getFrame(){
//		String result ="X ";
//		
//		for(int i=0;i<roomHeight/TILE_SIZE + 1;i++)
//			result+="X ";
//		result+=newline+"X ";
		String result="";
		for(int i=0;i<roomWidth/TILE_SIZE;i++){
			for(int j=0;j<roomHeight/TILE_SIZE;j++){
				//find out what type of block this is
				boolean[] l = new boolean[9]; 
				for(int dir=0;dir<DIRECTION.length;dir++){
					int x = i + DIRECTION[dir][0];
					int y = j + DIRECTION[dir][1];
					if(inBounds(x,y))
						l[dir]=isWall(x,y);
					else
						l[dir]=true;
				}
				int spriteType = PATH;
				int spriteIndex = 0;

				for (int k = 0; k < l.length; k++) {
				    spriteIndex = (spriteIndex << 1) + (l[k] ? 1 : 0);
				}
				
				if(l[MID_MID]){
					spriteType=0;
				}else{
					spriteType=1;
				}
				
				if(player.x == i 
						&& player.y == j)
					result+=character;
				else if(maxX-DIRECTION[maxDir][0] == i && maxY-DIRECTION[maxDir][1] == j)
					result+=goal;
				else
					result+=gameSprites[spriteType][spriteIndex];
					//result+=roomMap[i][j];
					//result+=(roomMap[i][j] > 0)? " " : "X";
					//result+=(roomMap[i][j] > 0)? roomMap[i][j]-1 + " ": "  ";
					//result+=(roomMap[i][j] > 10)? roomMap[i][j]-1 : ((roomMap[i][j]>0)? roomMap[i][j]-1+" ":"  ");
				
			}
//			result+="X" + newline+"X ";
			result+= newline;
		}
//		for(int i=0;i<roomHeight/TILE_SIZE +  1;i++)
//			result+="X ";
//		result+=newline;
		//System.out.println(result);
		gameReplay.add(result);
		return result + newline;
	}
	
	private boolean isWall(int x, int y) {
		if(inBounds(x,y))
			return roomMap[x][y]==0;
		else
			return true;
	}

//	public boolean[][] buildLocalityArray(boolean[] linearArray){
//		boolean[][] localityArray = new boolean[3][3];
//		for(int i = 0; i < 3; i++)
//			for(int j = 0; j< 3; j++)
//				localityArray[i][j]=linearArray[i+j];
//		return localityArray;
//	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("Roads Instance");
		clear();
		//gameReplay = buildRoad(roomWidth/TILE_SIZE/2,roomHeight/TILE_SIZE/2);
		build();
	}
	
	
	
	public boolean isWall(Point p){
		return roomMap[p.x][p.y]==0;
	}
	
	public boolean checkWin(){
		if(player.x==maxX-DIRECTION[maxDir][0]&&player.y==maxY-DIRECTION[maxDir][1]){
			return paused = true;
		}else 
			return false;
	}
	

	public void movePlayer(int keyCode){
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
		if(!paused)
			player = moveWalk(player,dir);
//		gameReplay.add(currentFrame = getFrame());
//		if(player.x==maxX-DIRECTION[maxDir][0]&&player.y==maxY-DIRECTION[maxDir][1]){
//			return paused = building = true;
//		}else 
//			return false;
	}
	private Point moveWalk(int x, int y, int dir) {
		return moveWalk(new Point(x,y),dir);
	}

	public Point moveWalk(Point p, int dir){
		return moveWalk(p,dir,1);
	}
	
	public Point moveWalk(Point p, int dir, int mag){
		Point point = move(p,dir,mag);
		if(isWall(point.x,point.y))
				return p;
		else
			return point;
	}
	
	public Point move(Point p, int dir, int mag){
		Point point = new Point(p);
		//System.out.println("New 1: " +point);
		//System.out.println("Old 1: " +p);
		int x = (p.x + mag*DIRECTION[dir][0]);
		int y = (p.y + mag*DIRECTION[dir][1]);
		//System.out.println("New x: "+ x+ ", y: " +y);
		if(inBounds(x,y)){
			point = new Point(x,y);
		}
		//System.out.print("Final :" +point);
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
		
	public void assignShit(){
		NumberFormat f = new DecimalFormat("000 ");
		
		for(int i = 0; i < wallSprites.length; i++){
			
			if(useSprites){
				wallSprites[i] = "  ";
				pathSprites[i] = "  ";
			}else{
				if(lookAtPaths){
					wallSprites[i] = "    ";
					pathSprites[i] = f.format(i);
				}else{
					pathSprites[i] = "    ";
					wallSprites[i] = f.format(i);
				}
			}
		}
		
		if(useSprites){
			character = "o'";
			goal = "@ ";
		}else{
			character = " o' ";
			goal = " @  ";
		}
		
		if(useSprites){
			
		//PATHS
			if(lookAtPaths){
			//horizontal path
			pathSprites[231] =
			pathSprites[399] =
			pathSprites[454] =
			pathSprites[455] = 
			pathSprites[199] =
			pathSprites[227] = 
			pathSprites[391] = 
			pathSprites[451] = 
			pathSprites[390] = 
			pathSprites[195] = 
			pathSprites[398] = 
			pathSprites[333] = 
			pathSprites[462] =
			pathSprites[483] =
			pathSprites[487] =
			pathSprites[463] = "══";
			
			//vertical path
			pathSprites[429] =
			pathSprites[237] =
			pathSprites[493] =
			pathSprites[301] =
			pathSprites[109] =
			pathSprites[173] =
			pathSprites[45]  =
			pathSprites[360] =
			pathSprites[361] =
			pathSprites[363] =
			pathSprites[362] =
			pathSprites[364] =
			pathSprites[365] =
			pathSprites[366] = 
			pathSprites[367] = "║ ";
			
			//path intersections
			pathSprites[333] = "╣ ";
			pathSprites[453] = "╦═";
			pathSprites[357] = "╠═";
			pathSprites[327] = "╩═";	
			pathSprites[325] = "╬═";
			
			//path corners ╚ ╔ ╗ ╝
			pathSprites[485] = "╔═";
			pathSprites[461] = "╗ ";
			pathSprites[359] = "╚═";
			pathSprites[335] = "╝ ";
			}
			
		//WALLS
			if(lookAtWalls){
			//surrounded wall
			wallSprites[511]="┼ ";
			
			//corners
			wallSprites[27] ="┌ ";
			wallSprites[54] ="┐ ";
			wallSprites[432]="┘ ";
			wallSprites[216]="└ ";
			
			//WALL ROCK
			
			wallSprites[176] =
			wallSprites[26] =
			wallSprites[434] =
			wallSprites[498] =
			wallSprites[315] =
			wallSprites[474] =
			wallSprites[249] =
			wallSprites[177]  =
			wallSprites[347]  =
			wallSprites[23]  =
			wallSprites[252] =
			wallSprites[90] =
			wallSprites[182]  =
			wallSprites[221]=
			wallSprites[30] =
			wallSprites[316] =
			wallSprites[183] =
			wallSprites[22] =
			wallSprites[59] =
			wallSprites[89] =
			wallSprites[217] =
			wallSprites[496] =
			wallSprites[95] =
			wallSprites[500] =
			wallSprites[31] =
			wallSprites[473] =
			wallSprites[311] =
			wallSprites[472] =
			wallSprites[444] =
			wallSprites[304] =
			wallSprites[112] =
			wallSprites[275] =
			wallSprites[151] =
			wallSprites[436] =
			wallSprites[310] =
			wallSprites[55]  =
			wallSprites[248]  =
			wallSprites[440]  =			
			wallSprites[62]  =			
			wallSprites[318]  =
			wallSprites[159]  =
			wallSprites[441]  =
			wallSprites[306]  =
			wallSprites[148] =
			wallSprites[240] =
			wallSprites[222] =	
			wallSprites[123] =	
			wallSprites[91]  = "# ";
			
			//ROCK BRIDGE
			//DIAGONAL
			wallSprites[20] =
			wallSprites[80] =
			wallSprites[17] =
			wallSprites[274] =
			wallSprites[153] =
			wallSprites[272] =
			wallSprites[273]  = "# ";
			//STRAIGHT
			wallSprites[120]  = "# ";		//EAST WEST
			wallSprites[403] =
			wallSprites[402]  =
			wallSprites[313]  =
			wallSprites[120]  = "# ";
			//ROCKBRIDGE JOINT
			wallSprites[120]  = "# ";
			
			//ROCK BRIDGE CAP
			//NORTH
			wallSprites[22]  =
			wallSprites[18]  =
			//EAST
			wallSprites[25] =
			wallSprites[30] =
			wallSprites[52]  =
			//SOUTH
			wallSprites[400] =
			wallSprites[464] =
			wallSprites[144]  =
			wallSprites[208] =
			//WEST
					wallSprites[28] =
			wallSprites[88]  =
			//UNKNOWN
			wallSprites[19]  =
			wallSprites[24]  = "# ";
			
			
			//ISOLATED ROCK
			wallSprites[16]  = "# ";
			
			
			//ROADBLOCK
			wallSprites[477] =
			wallSprites[351] =
			wallSprites[343] =
			wallSprites[469] = 
			wallSprites[341] =	
			wallSprites[349] =	
			wallSprites[373] = 	
			wallSprites[501] =  	
			wallSprites[375] ="X ";	
			
			//edge ├
			wallSprites[219] =
			wallSprites[475] =
			wallSprites[223] =
			wallSprites[479] = "├ ";
			
			//edge ┤
			wallSprites[210] =
			wallSprites[146] =
			wallSprites[147] =
			wallSprites[155] = 
			wallSprites[438] =
			wallSprites[503] =
			wallSprites[439] =
			wallSprites[502] = "┤ ";
			
			//edge ┬
			wallSprites[383] =
			wallSprites[63]  =
			wallSprites[191] = 
			wallSprites[319] =
			wallSprites[127] = "┬ ";
			
			//edge ┴
			wallSprites[504] =
			wallSprites[505] =
			wallSprites[508] =
			wallSprites[509] = "┴ ";
			
			//inside corners ┼
			wallSprites[218] =
			wallSprites[446] =
			wallSprites[506] = 
			wallSprites[251] =
			wallSprites[447] =
			wallSprites[510] = 
			wallSprites[255] =
			wallSprites[507] = "┼ ";
			
			//corners next to roadblock
			wallSprites[433] =
			wallSprites[283] =
			wallSprites[118] =
			wallSprites[220] = "┼ ";
			}
		}
	}
	
	public void clear() {

	}
}
