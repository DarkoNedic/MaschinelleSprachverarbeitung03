import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// delete temporary files
				File f = new File("./10foldCV/");
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
	}

}
