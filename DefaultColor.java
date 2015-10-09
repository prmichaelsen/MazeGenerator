import java.awt.Color;

public enum DefaultColor {
	
	GREEN 		(Color.GREEN),
	BLACK 		(Color.BLACK),
	WHITE 		(Color.WHITE),
	BLUE 		(Color.BLUE),
	LIGHT_GRAY 	(Color.LIGHT_GRAY),
	RED			(Color.RED),
	YELLOW 		(Color.YELLOW),
	GRAY 		(Color.GRAY),
	PINK 		(Color.PINK),
	CYAN 		(Color.CYAN),
	ORANGE 		(Color.ORANGE),
	MAGENTA 	(Color.MAGENTA);
	
	private final Color color;
	
	DefaultColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
}