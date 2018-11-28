import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class main {
	
	public static ArrayList<ArrayList<String>> lines = new ArrayList<>();
	
	public static Map<String, HashMap<String, Integer>> emissions_map = new HashMap<>();
	
	public static File[] read_folder(String path) {
		File[] files = new File(path).listFiles();
		return files;
	}
	
	public static void load_file(File file) throws IOException {
    	//String str = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
    	readAllLines(file, StandardCharsets.UTF_8);
    	
    }
    
    public static void readAllLines(File file, Charset cs) throws IOException {
    	String line = null;
    	String word = null;
    	String tag = null;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), cs)) {
            for (;;) {
            	line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.equals("")) {
                    continue;
                }
                ArrayList<String> lineAL = new ArrayList<>();
                for (String g1 : line.split(" ")) {
                	if (!g1.contains("/")) continue;
                	g1 = g1.replace("	", "");
                	word = g1.split("/\\w+$")[0];
                	tag = g1.split(".+/")[1];
                	//if (g1.equals("")) continue;
                	lineAL.add(word);
                	lineAL.add(tag);
                }
                lines.add(lineAL);
            }
        }
    }
    
    public static void emissions() {
    	iterate_emissions();
    }
    
    public static void iterate_emissions() {
    	String word = null;
    	String tag = null;
    	boolean add = false;
    	for (ArrayList<String> line : lines) {
    		for (String g : line) {
    			if (add) {
    				tag = g;
    				add_emissions(word, tag);
    				add = false;
    				continue;
    			}
    			word = g;
    			add = true;
    		}
    	}
    }
    
    public static void add_emissions(String word, String tag) {
    	int val;
    	if (!emissions_map.containsKey(word)) {
    		HashMap<String, Integer> tag_map = new HashMap<>();
    		tag_map.put(tag, 1);
    		emissions_map.put(word, tag_map);
    	} else {
    		if (!emissions_map.get(word).containsKey(tag)) {
    			emissions_map.get(word).put(tag, 1);
    		} else {
    			val = emissions_map.get(word).get(tag);
        		emissions_map.get(word).put(tag, val+1);
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
		
		emissions();
	}

}
