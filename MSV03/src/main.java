import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class main {
	
	public static ArrayList<ArrayList<String>> lines = new ArrayList<>();
	public static Set<String> words = new HashSet<>();
	public static ArrayList<ArrayList<String>> lines_copy = lines;
	public static ArrayList<ArrayList<String>> lines_test = new ArrayList<>();
	public static ArrayList<String> test_set = new ArrayList<>();

	public static Map<String, HashMap<String, Integer>> emissions_map = new HashMap<>();
	public static Map<String, Integer> emissions_tag_count = new HashMap<>();
	public static Map<String, Double> emissions_matrix = new HashMap<>();
	public static Map<String,Map<String, Double>> transitionMatrix;


	public static File[] read_folder(String path) {
		File[] files = new File(path).listFiles();
		return files;
	}
	
	public static void load_file(File file) throws IOException {
    	readAllLines(file, StandardCharsets.ISO_8859_1);
    	
    }

	static int count = 0;
	static int count_max = 0;

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
                	word = g1.split("/(\\w+\\$*|\\W+\\$*)$")[0];
                	tag = g1.split(".+/")[1];
//                	ViterbiDN.all_tags1.put(tag, new Node(null));
//                	ViterbiDN.all_tags2.put(tag, new Node(null));
                    Viterbi_Sed.all_tags1.put(tag, new Node(null));
                    Viterbi_Sed.all_tags2.put(tag, new Node(null));
                	count++;
                	lineAL.add(word);
                	words.add(word);
                	lineAL.add(tag);
                }
                lines.add(lineAL);
                count=0;
            }
        }
    }

    public static void emissions() {
    	emissions_map.clear();
    	emissions_matrix.clear();
    	if (transitionMatrix != null) {
    		transitionMatrix.clear();
    	}
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
    			emissions_matrix.put(e.getKey()+"##"+f.getKey(), val);
    		}
    	}
	}

    public static double get_emission_entry(String word, String tag) {
    	if (emissions_matrix.containsKey(word+"##"+tag)) {
    		return emissions_matrix.get(word+"##"+tag);
    	} else {
    		return 0.000001;
    	}
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

    }

    
    
    private static void export() throws IOException {
    	File f = new File("/tmp/emissions");
    	f.createNewFile();
    	f.setExecutable(true, false);
        f.setReadable(true, false);
        f.setWritable(true, false);
    	f = new File("/tmp/transitions");
    	f.createNewFile();
    	//String write = "";
    	for (Entry<String, Double> e : emissions_matrix.entrySet()) {
    		//write += e.getKey()+"\n" + e.getValue()+"\n";
    		Files.write(Paths.get("/tmp/emissions"), (e.getKey()+"\n").getBytes(), StandardOpenOption.APPEND);
    		Files.write(Paths.get("/tmp/emissions"), (e.getValue()+"\n").getBytes(), StandardOpenOption.APPEND);
    	}
    	//Files.write(Paths.get("./emissions"), (write).getBytes(), StandardOpenOption.APPEND);
    	//BufferedWriter writer = new BufferedWriter(new FileWriter("./emissions"), 32768);
        //writer.write(write);
        //writer.close();
    	System.out.println("Emission matrix exported.");
    	
    	//write = "";
    	for (Entry<String,Map<String, Double>> e : transitionMatrix.entrySet()) {
    		//write += e.getKey()+"\n";
    		Files.write(Paths.get("/tmp/transitions"), (e.getKey()+"\n").getBytes(), StandardOpenOption.APPEND);
    		for (Entry<String, Double> i : e.getValue().entrySet()) {
    			//write += i.getKey()+"\n" + i.getValue()+"\n";
    			Files.write(Paths.get("/tmp/transitions"), (i.getKey()+"\n").getBytes(), StandardOpenOption.APPEND);
    			Files.write(Paths.get("/tmp/transitions"), (i.getValue()+"\n").getBytes(), StandardOpenOption.APPEND);
    		}
    		//write += "####\n";
    		Files.write(Paths.get("/tmp/transitions"), ("####\n").getBytes(), StandardOpenOption.APPEND);
    	}
    	//Files.write(Paths.get("./transitions"), (write).getBytes(), StandardOpenOption.APPEND);
    	//writer = new BufferedWriter(new FileWriter("./transitions"), 32768);
        //writer.write(write);
        //writer.close();
    	
    	Files.move(Paths.get("/tmp/emissions"), Paths.get("./emissions"), StandardCopyOption.REPLACE_EXISTING);
    	Files.move(Paths.get("/tmp/transitions"), Paths.get("./transitions"), StandardCopyOption.REPLACE_EXISTING);
    	//new File("/tmp/emissions").renameTo(new File("./emissions"));
    	//new File("/tmp/transitions").renameTo(new File("./transitions"));
    	System.out.println("Transition matrix exported.");
	}
    
    private static void import_matrices() throws IOException {
    	//File file = new File("./emissions");
    	File file = new File("./emissions");
    	String line;
    	boolean isKey = true;
    	String word = null;
    	String tag = null;
    	String key = null;
    	try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.ISO_8859_1)) {
    		for (;;) {
            	line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.equals("")) {
                    continue;
                }
                if (isKey) {
                	isKey = false;
                	word = line.split("##")[0];
                	tag = line.split("##")[1];
                	key = line;
                	words.add(word);
                	//ViterbiDN.all_tags1.put(tag, new Node(null));
                	//ViterbiDN.all_tags2.put(tag, new Node(null));
                	Viterbi_Sed.all_tags1.put(tag, new Node(null));
                	Viterbi_Sed.all_tags2.put(tag, new Node(null));
                	continue;
                }
                isKey = true;
                //System.out.println(key + " : " + Double.parseDouble(line));
                emissions_matrix.put(key, Double.parseDouble(line));
                //add_emissions(word, tag);
    		}
    		//build_emissions_matrix();
    	}
    	
    	file = new File("./transitions");
    	//file = new File("/var/tmp/transitions");
    	boolean first = true;
    	boolean val = false;
    	isKey = true;
    	Map<String, Double> value = null;
    	transitionMatrix = new HashMap<>();
    	try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.ISO_8859_1)) {
    		for (;;) {
            	line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.equals("")) {
                    continue;
                }
                if (first) {
                	first = false;
                	key = line;
                	value = new HashMap<>();
                	continue;
                }
                if (line.equals("####")) {
                	first = true;
                	transitionMatrix.put(key, value);
                	continue;
                }
                if (val) {
                	val = false;
                	value.put(tag, Double.parseDouble(line));
                } else {
                	val = true;
                	tag = line;
                }
    		}
    	}
	}
    
    public static void readValidationFile(File file) throws IOException {
		String line = null;
		ArrayList<String> lineAL_ = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.ISO_8859_1)) {
			for (;;) {
				line = reader.readLine();
				if (line == null) {
					break;
				}
				if (line.equals("")) {
					continue;
				}
				lineAL_.add(line);
			}
			lines.add(lineAL_);
		}
	}

    static boolean annotate = false;
	public static void main(String[] args) throws IOException {
        
        
        //export();
        
        
		if (args.length == 2 && args[0].equals("cv")) {
			System.out.println("Mode = Cross Validation");
			//TenFoldCV.main("./brown_training");
			TenFoldCV.main(args[1]);
		} else if (args.length == 2 && args[0].equals("10cv")) {
			String path = args[1];
			load_file(new File(path));
			emissions();
	        transitions();
		} else if (args.length == 2 && args[0].equals("train")) {
			System.out.println("Mode = Train");
			String path = args[1];
			File[] files = read_folder(path);
			for (File file : files) {
				load_file(file);
			}
			emissions();
	        transitions();
	        export();
		} else if (args.length == 3 && args[0].equals("annotate")) {
			System.out.println("Mode = Annotate");
			annotate = true;
			String input_dir = args[1];
			String output_dir = args[2];
			File[] files = read_folder(input_dir);
			for (File file : files) {
				if (file.getName().startsWith(".")) continue;
				TenFoldCV.clean_valid(file, StandardCharsets.ISO_8859_1);
			}
			import_matrices();
			files = read_folder(input_dir);
			for (File file : files) {
				ArrayList<ArrayList<String>> results = new ArrayList<>();
				if (file.getName().startsWith(".") || !file.getName().startsWith("notags-")) continue;
				ArrayList<String> fileAL = TenFoldCV.read_lines2arraylist(file);
				for (String g : fileAL) {
					results.add(Viterbi_Sed.main(g));
					ViterbiDN.clear_map(ViterbiDN.all_tags1);
					ViterbiDN.clear_map(ViterbiDN.all_tags2);
				}
				//File f = new File(output_dir + "/" + file.getName().substring(7));
				File f = new File("/tmp/" + file.getName().substring(7));
				f.createNewFile();
				String[] g;
				ArrayList<String> res;
				String wrt = null;
				for (int i = 0; i < results.size(); i++) {
					wrt = "";
					g = fileAL.get(i).split(" ");
					res = results.get(i);
					for (int j = 0; j < g.length; j++) wrt += " " + g[j] + "/" + res.get(j);
					Files.write(new File("/tmp/" + f.getName()).toPath(), (wrt.substring(1) + "\n").getBytes(), StandardOpenOption.APPEND);
					//Files.write(Paths.get(f.getPath()), (wrt.substring(1) + "\n").getBytes(), StandardOpenOption.APPEND);
				}
				Files.move(new File(f.getAbsolutePath()).toPath(), Paths.get(output_dir + "/" + file.getName().substring(7)), StandardCopyOption.REPLACE_EXISTING);
				file.delete();
			}
			System.out.println("Annotation finished.");
		} else {
			String path2 = "./brown_training";
			path2 = "/Users/darko/Documents/GitHub/MaschinelleSprachverarbeitung03/MSV03/10foldCV/8/tri";
			File[] filess = read_folder(path2);
			for (File file : filess) {
				load_file(file);
			}
			emissions();
	        transitions();
		}
        

    }

}
