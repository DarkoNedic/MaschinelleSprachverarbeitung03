import java.util.ArrayList;

public class Line {

	public static ArrayList<String> line;
	public Line (ArrayList<String> line) {
		this.line = line;
	}
	
	public static String get(int index) {
		return line.get(index);
	}
}
