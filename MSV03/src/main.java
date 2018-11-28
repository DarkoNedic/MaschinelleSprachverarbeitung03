import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class main {
	
	public static ArrayList<Line> lines = new ArrayList<>();
	
	public static File[] read_folder(String path) {
		File[] files = new File(path).listFiles();
		return files;
	}
	
	public static void load_file(File file) throws IOException {
    	//String str = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
    	readAllLines(file, StandardCharsets.UTF_8);
    	
    }
    
    public static void readAllLines(File file, Charset cs) throws IOException {
    	String regex = "/| ";
    	String line = null;
    	ArrayList<String> lineAL = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), cs)) {
            for (;;) {
            	line = reader.readLine();
                if (line == null)
                    break;
                for (String g : line.split(regex)) {
                	g = g.replace("	", "");
                	if (g.equals("")) continue;
                	lineAL.add(g);
                }
                lines.add(new Line(lineAL));
            }
        }
    }

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String path = "./brown_training";


				
		File[] files = read_folder(path);
		
		for (File file : files) {
			load_file(file);
		}
	}

}
