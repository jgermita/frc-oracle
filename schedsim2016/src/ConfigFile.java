import java.io.BufferedReader;
import java.io.FileReader;

public class ConfigFile {

	private String fileName = "";



	private int it = 10000;
	private int oprMode = 0;

	public ConfigFile(String fileName) {
		this.fileName = fileName;
		
		try { 
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			
			String line = "";
			
			while((line = br.readLine()) != null) {
				if (line.startsWith("iterations = ")) {
					it = Integer.parseInt(line.split(" = ")[1]);
				} else if (line.startsWith("oprMode = ")) {
					oprMode = Integer.parseInt(line.split(" = ")[1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public int getIterations() {
		return it;
	}

	public int getOprMode() {
		// 0 - most recent
		// 1 - best
		// 2 - average
		// 3 - recent weighted average
		return oprMode;
	}

}
