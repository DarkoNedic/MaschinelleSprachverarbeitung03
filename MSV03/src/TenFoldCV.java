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
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.plaf.synth.SynthSpinnerUI;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
//import com.google.common.io.Files;

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
			Files.write(Paths.get("/tmp/10foldCV/" + n + "/train"), g.getBytes(), StandardOpenOption.APPEND);
		} else {
			Files.write(Paths.get("/tmp/10foldCV/" + n + "/valid"), g.getBytes(), StandardOpenOption.APPEND);
		}
		
		/*if (train) {
			//Files.write(Paths.get("./10foldCV/" + n + "/train"), g.getBytes(), StandardOpenOption.APPEND);
			File file = new File("./10foldCV/" + n + "/train");
			CharSink chs = com.google.common.io.Files.asCharSink(
				      file, Charsets.UTF_8, FileWriteMode.APPEND);
				    chs.write(g);
		} else {
			//Files.write(Paths.get("./10foldCV/" + n + "/valid"), g.getBytes(), StandardOpenOption.APPEND);
			File file = new File("./10foldCV/" + n + "/valid");
			CharSink chs = com.google.common.io.Files.asCharSink(
				      file, Charsets.UTF_8, FileWriteMode.APPEND);
				    chs.write(g);
		}*/
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
			
			if(!main.annotate) {
				//File f = new File(file.getParent() + "/notags");
				File f = new File(file.getParent() + "/notags");
				f.createNewFile();
				f = new File(file.getParent() + "/nowords");
				f.createNewFile();

				for (String sentence : sentences) {
					sentence += "\n";
					Files.write(Paths.get(file.getParent() + "/notags"), sentence.getBytes(),
							StandardOpenOption.APPEND);
				}

				for (String tag_sequence : tag_sequences) {
					tag_sequence += "\n";
					Files.write(Paths.get(file.getParent() + "/nowords"), tag_sequence.getBytes(),
							StandardOpenOption.APPEND);
				}
			
			} else {
				File f = new File(file.getParent() + "/notags-" + file.getName());
				f.createNewFile();
				
				for (String sentence : sentences) {
					sentence += "\n";
					Files.write(Paths.get(file.getParent() + "/notags-" + file.getName()), sentence.getBytes(),
							StandardOpenOption.APPEND);
				}
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
	

	public static void main(String args) throws IOException {
		// TODO Auto-generated method stub
		// wenn einmal ausgeführt, werden die daten
		// auf der platte gespeichert. Ab dann kann
		// ab folgender Zeile kommentiert werden bis 241
		int n = 10;
		
		//String path = "./brown_training";
		String path = args;

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
		File f = new File("/tmp/10foldCV");
		f.mkdir();
		/*f = new File("./10foldCV/");
		f.mkdir();*/
		for (int i = 1; i < n+1; i++) {
			fold = folds.get(i-1);
			valid = validations.get(i-1);
			f = new File("/tmp/10foldCV/" + i);
			f.mkdir();
			f = new File("/tmp/10foldCV/" + i + "/train");
			f.createNewFile();
			f = new File("/tmp/10foldCV/" + i + "/valid");
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
		for (int i = 1; i < n+1; i++) {
			files = read_folder("/tmp/10foldCV/" + i);
			train_files.add(files[1].toString());
			valid_files.add(files[0].toString());
		}
		
		for (String valid_file : valid_files) {
			File ff = new File(valid_file);
			clean_valid(ff, StandardCharsets.ISO_8859_1);
		}
		//Files.move(Paths.get("/tmp/10foldCV/"), Paths.get("./"), StandardCopyOption.REPLACE_EXISTING);
		//Files.move(new File("/tmp/10foldCV/").toPath(), new File("./10foldCV/").toPath(), StandardCopyOption.REPLACE_EXISTING);
		CopyDir.copyFolder(new File("/tmp/10foldCV/"), new File("./10foldCV/"));
		// bis hierher kommentieren
		
		
		// dann das hier wieder weg-wegkommentieren
		
		//File[] files;
		files = null;
		//ArrayList<String> train_files = new ArrayList<>();
		//ArrayList<String> valid_files = new ArrayList<>();
		train_files = new ArrayList<>();
		valid_files = new ArrayList<>();
		ArrayList<String> nowords = new ArrayList<>();
		ArrayList<String> notags = new ArrayList<>();
		for (int i = 1; i < n+1; i++) {
			files = read_folder("./10foldCV/" + i);
			if (files[0].toString().endsWith("train")) train_files.add(files[0].toString());
			if (files[1].toString().endsWith("train")) train_files.add(files[1].toString());
			if (files[2].toString().endsWith("train")) train_files.add(files[2].toString());
			if (files[3].toString().endsWith("train")) train_files.add(files[3].toString());
			if (files[0].toString().endsWith("valid")) valid_files.add(files[0].toString());
			if (files[1].toString().endsWith("valid")) valid_files.add(files[1].toString());
			if (files[2].toString().endsWith("valid")) valid_files.add(files[2].toString());
			if (files[3].toString().endsWith("valid")) valid_files.add(files[3].toString());
			if (files[0].toString().endsWith("nowords")) nowords.add(files[0].toString());
			if (files[1].toString().endsWith("nowords")) nowords.add(files[1].toString());
			if (files[2].toString().endsWith("nowords")) nowords.add(files[2].toString());
			if (files[3].toString().endsWith("nowords")) nowords.add(files[3].toString());
			if (files[0].toString().endsWith("notags")) notags.add(files[0].toString());
			if (files[1].toString().endsWith("notags")) notags.add(files[1].toString());
			if (files[2].toString().endsWith("notags")) notags.add(files[2].toString());
			if (files[3].toString().endsWith("notags")) notags.add(files[3].toString());
			//train_files.add(files[2].toString());
			//valid_files.add(files[0].toString());
			//nowords.add(files[1].toString());
			//notags.add(files[3].toString());
		}
		
		System.out.println("Prepared validation data for cross-validation.\n");
		
		System.out.println("Starting 10-fold cross-validation...");
		
		int fc = 0;
		ArrayList<Double> total_precision = new ArrayList<>();
		for (String train_file : train_files) {
			String[] argss = {"10cv", train_file};
			main.main(argss);
			int fc_ = 0;
			double precision_sum = 0.0;
			//ArrayList<String> path = new ArrayList<>();
			ArrayList<String> path2 = new ArrayList<>();
			for (String validd : read_lines2arraylist(new File(notags.get(fc)))) {
				//System.out.println(valid);
				path2.clear();
				//System.out.println("start sed");
				path2 = Viterbi_Sed.main(validd);
				//System.out.println("end sed");
				String[] truth = read_lines2arraylist(new File(nowords.get(fc))).get(fc_).split(" ");
				int matches = 0;
				for (int i = 0; i < truth.length; i++) {
					if (path2.size() == 0) {
						System.out.println(validd);
					}
					//System.out.println(validd);
					if (path2.get(i).equals(truth[i])) {
						matches++;
					}
				}
				precision_sum += (float) ((float) matches / (float) truth.length);
				//System.out.print("Precision: " + (float) ((float) matches / (float) truth.length) + ", " + (float) matches + "/" + (float) truth.length);
				//System.out.println(" | Total: " + precision_sum/(fc_+1));
				fc_++;
				/*ViterbiDN.clear_map(Viterbi_Sed.all_tags1);
				ViterbiDN.clear_map(Viterbi_Sed.all_tags2);*/
				Viterbi_Sed.all_tags1.remove("");
				Viterbi_Sed.all_tags2.remove("");
			}
			System.out.println("Fold " + (fc+1) + " precision: " + precision_sum/(fc_));
			total_precision.add(precision_sum/(fc_));
			fc++;
			main.emissions_map.clear();
			main.transitionMatrix.clear();
			main.emissions_matrix.clear();
			main.lines.clear();
			main.words.clear();
			main.lines_test.clear();
			main.emissions_tag_count.clear();
			main.lines_test.clear();
		}
		
		double total = 0.0;
		for (double prec : total_precision) {
			total += prec;
		}
		
		// delete temporary files
		f = new File("./10foldCV/");
		String[] entries = f.list();
		for (String s : entries) {
			File currentFolder = new File("./10foldCV/" + s);
			if (s.startsWith(".")) {
				currentFolder.delete();
				continue;
			}
			String[] entries2 = currentFolder.list();
			for (String g : entries2) {
				File currentFile = new File("./10foldCV/" + s, g);
				currentFile.delete();
			}
			currentFolder.delete();
		}
		f.delete();
		
		System.out.println("avg. Precision: " + total/n);
		
	}

}
