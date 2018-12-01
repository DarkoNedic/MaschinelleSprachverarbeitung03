import java.awt.SecondaryLoop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TenFoldCV {
	
	public static ArrayList<ArrayList<String>> folds = new ArrayList<>();
	public static ArrayList<ArrayList<String>> validations = new ArrayList<>();
	public static ArrayList<ArrayList<String>> lines = new ArrayList<>();
	public static int fold_size;
	
	public static int countt = 0;
	
	public static void readAllLines(File file, Charset cs) throws IOException {
		String line = null;
		ArrayList<String> lineAL_ = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), cs)) {
			for (;;) {
				line = reader.readLine();
				if (line == null) {
					break;
				}
				if (line.equals("")) {
					continue;
				}
				lineAL_.add(line);
				countt++;
			}
			lines.add(lineAL_);

		}
	}
	
	
	static int count = 0;
	static ArrayList<String> lineAL = new ArrayList<>();
	static ArrayList<String> validAL = new ArrayList<>();
	public static void readAllLines(ArrayList<String> lines, Charset cs, int n) throws IOException {
		int start = fold_size * n;
		int end = fold_size * (n + 1);
		for (String line : lines) {
			if (count >= start && count < end) {
				count++;
				validAL.add(line);
			} else {
				count++;
				lineAL.add(line);
			}
		}

	}
	
	public static File[] read_folder(String path) {
		File[] files = new File(path).listFiles();
		return files;
	}
	
	public static void load_file(File file) throws IOException {
    	readAllLines(file, StandardCharsets.ISO_8859_1);
    	
    }
	
	public static void load_file(ArrayList<String> file, int n) throws IOException {
    	readAllLines(file, StandardCharsets.ISO_8859_1, n);
    	
    }
	
	public static void write(int n, String g, boolean train) throws IOException {
		g = g + "\n";
		if (train) {
			Files.write(Paths.get("./10foldCV/" + n + "/train"), g.getBytes(), StandardOpenOption.APPEND);
		} else {
			Files.write(Paths.get("./10foldCV/" + n + "/valid"), g.getBytes(), StandardOpenOption.APPEND);
		}
	}
	
	public static void clean_valid(File file, Charset cs) throws IOException {
    	String line = null;
    	String word = null;
    	String tag = null;
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), cs)) {
			ArrayList<String> sentences = new ArrayList<>();
			ArrayList<String> tag_sequences = new ArrayList<>();
			for (;;) {
				line = reader.readLine();
				if (line == null) {
					break;
				}
				if (line.equals("")) {
					continue;
				}
				ArrayList<String> words = new ArrayList<>();
				ArrayList<String> tags = new ArrayList<>();
				String sentence = "";
				String tag_sequence = "";
				for (String g1 : line.split(" ")) {
					if (!g1.contains("/")) continue;
					g1 = g1.replace("	", "");
					word = g1.split("/(\\w+\\$*|\\W+\\$*)$")[0];
					tag = g1.split(".+/")[1];
					words.add(word);
					tags.add(tag);
				}
				for (String wd : words) {
					sentence += " " + wd;
				}
				for (String tg : tags) {
					tag_sequence += " " + tg;
				}
				sentence = sentence.substring(1);
				tag_sequence = tag_sequence.substring(1);
				sentences.add(sentence);
				tag_sequences.add(tag_sequence);

			}
			
			File f = new File(file.getParent() + "/notags");
			f.createNewFile();
			f = new File(file.getParent() + "/nowords");
			f.createNewFile();
			
			for (String sentence : sentences) {
				sentence += "\n";
				Files.write(Paths.get(file.getParent() + "/notags"), sentence.getBytes(), StandardOpenOption.APPEND);
			}
			
			for (String tag_sequence : tag_sequences) {
				tag_sequence += "\n";
				Files.write(Paths.get(file.getParent() + "/nowords"), tag_sequence.getBytes(), StandardOpenOption.APPEND);
			}

		}
    }
	
	
	public static ArrayList<String> read_lines2arraylist(File file) throws IOException {
		Charset cs = StandardCharsets.ISO_8859_1;
		String line = null;
		ArrayList<String> lineAL_ = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), cs)) {
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

		}
		return lineAL_;
	}
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// wenn einmal ausgeführt, werden die daten
		// auf der platte gespeichert. Ab dann kann
		// ab folgender Zeile kommentiert werden bis 241
		int n = 10;
		
		String path = "./brown_training";

		File[] files = read_folder(path);

		for (File file : files) {
			load_file(file);
		}
		fold_size = countt/n;
		
		
		
		for (int i = 0; i < 10; i++) {
			for (ArrayList<String> line : lines) {
				load_file(line, i);
			}
			ArrayList<String> lineAL_cp = new ArrayList<>();
			ArrayList<String> validAL_cp = new ArrayList<>();
			lineAL_cp.addAll(lineAL);
			validAL_cp.addAll(validAL);
			lineAL.clear();
			validAL.clear();
			folds.add(lineAL_cp);
			validations.add(validAL_cp);
			count = 0;
		}
		
		
		
		ArrayList<String> fold;
		ArrayList<String> valid;
		File f = new File("./10foldCV");
		f.mkdir();
		for (int i = 1; i < 11; i++) {
			fold = folds.get(i-1);
			valid = validations.get(i-1);
			f = new File("./10foldCV/" + i);
			f.mkdir();
			f = new File("./10foldCV/" + i + "/train");
			f.createNewFile();
			f = new File("./10foldCV/" + i + "/valid");
			f.createNewFile();
			for (String g : fold) {
				write(i, g, true);
			}
			for (String g : valid) {
				write(i, g, false);
			}
			fold.clear();
			valid.clear();
		}
		
		System.out.println("Data ready for 10 Fold Cross Validation.");
		System.out.println("10 distinct validaion sets created.");
		
		
		ArrayList<String> train_files = new ArrayList<>();
		ArrayList<String> valid_files = new ArrayList<>();
		for (int i = 1; i < 11; i++) {
			files = read_folder("./10foldCV/" + i);
			train_files.add(files[1].toString());
			valid_files.add(files[0].toString());
		}
		
		for (String valid_file : valid_files) {
			File ff = new File(valid_file);
			clean_valid(ff, StandardCharsets.ISO_8859_1);
		}
		// bis hierher kommentieren
		
		
		// dann das hier wieder weg-wegkommentieren
		/*
		File[] files;
		ArrayList<String> train_files = new ArrayList<>();
		ArrayList<String> valid_files = new ArrayList<>();
		ArrayList<String> nowords = new ArrayList<>();
		ArrayList<String> notags = new ArrayList<>();
		for (int i = 1; i < 11; i++) {
			files = read_folder("./10foldCV/" + i);
			train_files.add(files[2].toString());
			valid_files.add(files[0].toString());
			nowords.add(files[1].toString());
			notags.add(files[3].toString());
		}
		
		int fc = 0;
		ArrayList<Double> total_precision = new ArrayList<>();
		for (String train_file : train_files) {
			String[] argss = {"cv", train_file};
			main.main(argss);
			int fc_ = 0;
			double precision_sum = 0.0;
			ArrayList<String> path = new ArrayList<>();
			for (String valid : read_lines2arraylist(new File(notags.get(fc)))) {
				//System.out.println(valid);
				path.clear();
				path = ViterbiDN.viterbi(valid, "");
				String[] truth = read_lines2arraylist(new File(nowords.get(fc))).get(fc_).split(" ");
				int matches = 0;
				for (int i = 0; i < truth.length; i++) {
					//System.out.println(truth[i] + " : " + path.get(i));
					if (path.get(i).equals(truth[i])) {
						matches++;
					}
				}
				precision_sum += (float) ((float) matches / (float) truth.length);
				//System.out.print("Precision: " + (float) ((float) matches / (float) truth.length) + ", " + (float) matches + "/" + (float) truth.length);
				//System.out.println(" | Total: " + precision_sum/(fc_+1));
				fc_++;
				ViterbiDN.clear_map(ViterbiDN.all_tags1);
				ViterbiDN.clear_map(ViterbiDN.all_tags2);
				ViterbiDN.all_tags1.remove("");
				ViterbiDN.all_tags2.remove("");
				//if (fc_ == 13) break;
			}
			System.out.println(precision_sum/(fc_));
			total_precision.add(precision_sum/(fc_));
			fc++;
			//break;
		}
		
		double total = 0.0;
		for (double prec : total_precision) {
			total += prec;
		}
		System.out.println("AVG: " + total/10);
		*/
	}

}
