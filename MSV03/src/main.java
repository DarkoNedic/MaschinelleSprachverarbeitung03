import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class main {
	
	public static File[] read_folder(String path) {
		File[] files = new File(path).listFiles();
		return files;
	}
	
	public static void load_file(File file) throws IOException {
    	//String str = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
    	readAllLines(file, StandardCharsets.UTF_8);
    	
    }
    
    public static void readAllLines(File file, Charset cs) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), cs)) {
            for (;;) {
                String line = reader.readLine();
                if (line == null)
                    break;
                System.out.println(line);
            }
        }
    }

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String path = "./brown_training";
		System.out.println("Halo");

		System.out.println("alb");

		System.out.println("bla");

				
		File[] files = read_folder(path);
		
		for (File file : files) {
			load_file(file);
		}
	}

}
