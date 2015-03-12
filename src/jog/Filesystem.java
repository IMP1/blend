package jog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * <h1>jog.filesystem</h1>
 * <p>Used for accessing files within the project.</p>
 * @author IMP1
 */
public abstract class Filesystem {

	/**
	 * Holds locations to be searched for files.
	 */
	private static ArrayList<String> locations = new ArrayList<String>();
	
	static {
		addLocation(".", false);
	}
	
	/**
	 * Adds a location to be tracked.
	 * @param filepath the path to the directory to be added.
	 */
	public static void addLocation(String filepath) { addLocation(filepath, true); }
	/**
	 * Adds a location to be tracked.
	 * @param filepath the path to the directory to be added.
	 * @param subFolders whether to recursively add sub-folders.
	 */
	public static void addLocation(String filepath, boolean subFolders) { addLocation(filepath, subFolders, 0); }
	private static void addLocation(String filepath, boolean subFolders, int depth) {
		// Create file
		File newLocation = new File(filepath);
		String path = newLocation.getAbsolutePath();
		// Warn if pre-existing.
		if (locations.contains(path)) {
			System.err.println("[Filesystem] \"" + filepath + "\" is already a project location.");
			return;
		}
		// Add location
		locations.add(path);
		System.out.println("[Filesystem] \"" + path + "\" added to project locations.");
		// ~~~~~~
		// If we don't want to look any further
		if (!subFolders || !newLocation.isDirectory()) {			
			return;
		}
		// If we _do_ want to look further, create array of files
		for (File child : newLocation.listFiles()) {
			// Make sure they're directories
			if (child.isDirectory()) {
				// And add them too (searching through recursively)
				addLocation(child.getAbsolutePath(), true, depth+1);
			}
		}
	}
	
	/**
	 * Returns the first file in the project found with the filename.
	 * Used privately for other methods.
	 * @param filename the filename of the file to search for.
	 * @return the file object.
	 */
	private static File getFile(String filename) {
		String path = "";
		File f;
		for (String loc : locations) {
			path = loc + File.separator + filename;
			f = (new File(path)).getAbsoluteFile();
			if (f.exists()) {
				return f;
			}
		}
		throw new RuntimeException("No path to file: " + filename);
	}
	
	/**
	 * Gets a URL for the file, if it exists within the project.
	 * @param filename the name of the file.
	 * @return the URL for the file.
	 */
	public static URL getURL(String filename) {
		File f = getFile(filename);
		try {
			return f.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Path exists to, but no URL can be created for \"" + filename + "\".");
		}
	}
	
	/**
	 * Searched through all tracked locations for the file.
	 * @param filename the file to search for.
	 * @return the path to the file.
	 */
	public static String getPath(String filename) {
		return getFile(filename).getAbsolutePath();
	}
	
	/**
	 * Gets all the direct children files in a directory. 
	 * @param filename the directory of which to list the contents.
	 * @return the filenames of the files and folders in the directory.
	 */
	public static String[] enumerate(String filename) {
		File dir = getFile(filename);
		return dir.list();
	}
	
	/**
	 * Returns the contents of a file in a String object.
	 * @param filename the filename of the file to read.
	 * @return the contents of the file, with lines separated by newlines.
	 */
	public static String readFile(String filename) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(new File(getPath(filename))));
			String contents = "";
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				contents += line + "\n";
			}
			r.close();
			return contents;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

}
