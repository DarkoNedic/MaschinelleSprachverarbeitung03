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
	public static Map<String, Integer> emissions_tag_count = new HashMap<>();
	public static Map<String, Double> emissions_matrix = new HashMap<>();
	public static Map<String,Map<String, Double>> transitionMatrix;


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
                	Viterbi.all_tags1.put(tag, new Node(null));
                	Viterbi.all_tags2.put(tag, new Node(null));
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
    	build_emissions_matrix();
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
    	if (emissions_tag_count.containsKey(tag)) {
    		emissions_tag_count.put(tag, emissions_tag_count.get(tag)+1);
    	} else {
    		emissions_tag_count.put(tag, 1);
    	}
    }

    private static void build_emissions_matrix() {
    	double val;
    	for (Entry<String, HashMap<String, Integer>> e : emissions_map.entrySet()) {
    		for (Entry<String, Integer> f : e.getValue().entrySet()) {
    			val = (double) f.getValue()/(double) emissions_tag_count.get(f.getKey());
    			HashMap<String, Double> hmp = new HashMap<>();
    			hmp.put(f.getKey(), val);
    			emissions_matrix.put(e.getKey()+f.getKey(), val);
    		}
    	}
	}

    public static double get_emission_entry(String word, String tag) {
    	if (emissions_matrix.containsKey(word+tag)) {
    		return emissions_matrix.get(word+tag);
    	} else {
    		return (double) 0;
    	}
    }

	public static void main(String[] args) throws IOException {
		String path = "./brown_training";

				
		File[] files = read_folder(path);

		for (File file : files) {
			load_file(file);
		}

        emissions();
        transitions();

    }

    private static void transitions()
    {
        Map<String, Map<String, Integer>> states = new HashMap<>();

        for (ArrayList<String> element : lines)
        {

            for (int pos = 1; pos < element.size()- 2; pos = pos +2)
            {

                if(states.containsKey(element.get(pos))){
                    if(states.get(element.get(pos)).containsKey(element.get(pos+2))){
                        states.get(element.get(pos)).put(element.get(pos+2),states.get(element.get(pos)).get(element.get(pos+2))+1);
                    }else{
                        states.get(element.get(pos)).put(element.get(pos+2),1);
                    }
                }else{
                    Map<String, Integer> temp = new HashMap<>();
                    temp.put(element.get(pos+2), 1);
                    states.put(element.get(pos), temp);
                }
            }
        }
//        System.out.println("bla");

        transitionMatrix = new HashMap<>();
        for(Map.Entry<String, Map<String, Integer>> entry : states.entrySet()) {
            String key = entry.getKey();
            Map<String, Integer> map = entry.getValue();


            double nennerCounter = 0;
            for (Map.Entry<String, Integer> innerEntry : map.entrySet()) {
                nennerCounter = nennerCounter + innerEntry.getValue();
            }

            Map<String, Double> temp = new HashMap<>();
            for (Map.Entry<String, Integer> innerEntry : map.entrySet()) {
                String innerKey = innerEntry.getKey();
                double innerCount = (double) innerEntry.getValue();

                double result = innerCount / nennerCounter;

                temp.put(innerKey, result);
            }
            transitionMatrix.put(key, temp);
        }
        System.out.println("bla");

    }

}
