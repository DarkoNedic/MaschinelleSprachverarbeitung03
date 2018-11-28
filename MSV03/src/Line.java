import java.util.ArrayList;

public class Line {

	static ArrayList<String> line = new ArrayList<>();
	public Line (ArrayList<String> line) {
		for (String g : line) {
			this.line.add(g);
		}
	}
	
	public static String get(int index) {
		return line.get(index);
	}
	
	public static int size() {
		return line.size();
	}
}
