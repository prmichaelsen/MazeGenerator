import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

public class Roads implements Runnable{

	final static String newline = System.getProperty("line.separator");
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
			{0, -1}, //LEFT
			{-1,  0}, //DOWN
			{0,  1}, //RIGHT
			{1, 0}};  //UP
	
	public static final Random rnd = new Random();
	public static final int ROAD_LENGTH = 3;
	public int map[][];
	int keyFrame;
	static String[] sprites = new String[]{"X ","  ","  ","  ","  ","X ","X ","@ "};
	static String[][] styles = new String[][]{
		{"X ","  ","  ","  ","  ","X ","X ","@ "},
		{"X ","  ","  ","  ","  ",". ",". ","@ "},
		{"  ","__","||","__","||","  ","X ","@ "},
		{"  ","  ","  ","  ","  ","  ","  ","@ "}
		};
	public static int tiles[] = {1,0}; //ROAD, WALL
	public static int Px;
	public static int Py;
	
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
				currentFrame= this.print(x, y, enemy);
				//initialize a random list of directions
				directions = 
						new ArrayList<Integer>(
								Arrays.asList(new Integer[]{0,1,2,3}));
				Collections.shuffle(directions);
				
				//check each direction to find a free direction
				//if it is free, build the road and store the direction
				//otherwise remove the direction and keep searching
				can = false;
//				System.out.println();
				while(!directions.isEmpty() && can == false){
					//print(x,y);
					if(can = checkFree(x,y,size,dir=directions.remove(0),map)){
						for(int i = 0; i < roadLength; i++){
							//System.out.println(this.print(x,y));
							//buildProcess.add(this.print(x,y,drawer));
							map[x][y]=dir+1;
//							map[x][y]=path.size()+1;
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
		for(int i=1;i<roomWidth/TILE_SIZE-1;i++){
			for(int j=1;j<roomHeight/TILE_SIZE-1;j++){
				boolean surroundedByRoad = true;
				boolean touchAtLeastOneRoad = false;
				boolean touchAtLeastOneWall = false;
				boolean isWall = false;
				if(map[i][j]==0||map[i][j]==5||map[i][j]==6)
					isWall=true;
				for(int k=0;k<4;k++){
					int check = map[i+DIRECTION[k][0]][j+DIRECTION[k][1]];
					//check if all adjacent blocks are road
					//and if this block is wall
					if(check>0&&check<5&&isWall){
						map[i][j]=5;
						touchAtLeastOneRoad=true;
					}
					if(check==0||check==5||check==6){
						touchAtLeastOneWall=true;
					}

				}
				if(!touchAtLeastOneWall&&isWall)
					map[i][j]=6;
			}
		}
		paused = false;
		return buildProcess;
	}
	
	private boolean checkFree(int x, int y, int size, int dir, int[][] map){	
		boolean can = true;
		for(int i = 0; i < roadLength+1; i++){
			if(inBounds(x,y)){
				if(i>0){
					if(map[x][y]>0)
						can= false;
				}else if(roadLength==1)
					if(map[x][y]>0)
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
	
	public void build(){
		gameReplay = buildRoad(roomWidth/TILE_SIZE/2,roomHeight/TILE_SIZE/2,2000,0);
		keyFrame = gameReplay.size();
	}
	
	public void reset(){
		maxX=maxY=0;
		Px = roomWidth/TILE_SIZE/2;
		Py = roomHeight/TILE_SIZE/2;
		map = new int[roomWidth/TILE_SIZE][roomHeight/TILE_SIZE];
		path = new Stack<Integer>();
		print(Px,Py,character);
		build();
	}
	
	public Roads (int length, int width, int height){
		
		roomWidth = width*TILE_SIZE;
		roomHeight = height*TILE_SIZE;
		Px = roomWidth/TILE_SIZE/2;
		Py = roomHeight/TILE_SIZE/2;
		map = new int[roomWidth/TILE_SIZE][roomHeight/TILE_SIZE];
		path = new Stack<Integer>();
		gameReplay = new ArrayList<String>();
		roadLength=length;
		print(Px,Py,character);
	}
	
	public Roads (int length){
		roomWidth = ROOM_WIDTH;
		roomHeight = ROOM_HEIGHT;
		Px = roomWidth/TILE_SIZE/2;
		Py = roomHeight/TILE_SIZE/2;
		map = new int[roomWidth/TILE_SIZE][roomHeight/TILE_SIZE];
		path = new Stack<Integer>();
		gameReplay = new ArrayList<String>();
		roadLength=length;
		print(Px,Py,character);
	}
	
	public void rebuild (int length){
		Px = roomWidth/TILE_SIZE/2;
		Py = roomHeight/TILE_SIZE/2;
		map = new int[roomWidth/TILE_SIZE][roomHeight/TILE_SIZE];
		path = new Stack<Integer>();
		roadLength=length;
		build();
	}
	
	public String print(int x, int y, String character){
		String result ="X ";
		for(int i=0;i<roomHeight/TILE_SIZE +  1;i++)
			result+="X ";
		result+=newline+"X ";
		for(int i=0;i<roomWidth/TILE_SIZE;i++){
			for(int j=0;j<roomHeight/TILE_SIZE;j++){
				if(x == i && y == j)
					result+=character;
				else if(maxX-DIRECTION[maxDir][0] == i && maxY-DIRECTION[maxDir][1] == j)
					result+=sprites[7];
				else
					result+=sprites [map[i][j]];
					//result+=(map[i][j] > 0)? "  " : "X ";
					//result+=(map[i][j] > 0)? map[i][j]-1 + " ": "  ";
					//result+=(map[i][j] > 10)? map[i][j]-1 : ((map[i][j]>0)? map[i][j]-1+" ":"  ");
				
			}
			result+="X" + newline+"X ";
		}
		for(int i=0;i<roomHeight/TILE_SIZE +  1;i++)
			result+="X ";
		result+=newline;
		//System.out.println(result);
		return result;
	}
	
	/*public static String print(int x, int y, String character, Roads road){
		String result ="X ";
		for(int i=0;i<ROOM_WIDTH/TILE_SIZE +  1;i++)
			result+="X ";
		result+=newline+"X ";
		for(int i=0;i<ROOM_WIDTH/TILE_SIZE;i++){
			for(int j=0;j<roomHeight/TILE_SIZE;j++){
				if(x == i && y == j)
					result+=character;
				else
					result+=(road.map[i][j] > 0)? "  " : "X ";
					//result+=(map[i][j] > 0)? map[i][j]-1 + " ": 0 + " ";
			}
			result+="X" + newline+"X ";
		}
		for(int i=0;i<ROOM_WIDTH/TILE_SIZE +  1;i++)
			result+="X ";
		result+=newline;
		//System.out.println(result);
		return result;
	}*/
	
	@Override
	public void run() {
		gameReplay = buildRoad(roomWidth/TILE_SIZE/2,roomHeight/TILE_SIZE/2,2000,0);
	}
	
	public boolean didPlayerWin(int keyCode){
		int dir = keyCode-37;
		int x = Px + DIRECTION[dir][0];
		int y = Py + DIRECTION[dir][1];
		if(inBounds(x,y)&&!paused)
			if(map[x][y]>0&&map[x][y]<5){
				Px = x;
				Py = y;
				print(Px,Py,character);
			}
		gameReplay.add(this.print(Px, Py,character));
		if(Px==maxX-DIRECTION[maxDir][0]&&Py==maxY-DIRECTION[maxDir][1]){
			return true;
		}else 
			return false;
			
		//System.out.println(this.print(x, y,drawer));
	}

	
	
	public void clear() {

	}
}
