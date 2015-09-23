package xmltest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/** Non-validating parse of the XHTML files under "src/main/webapp";
 * faster than deploying and failing for missing ">" or "/"...
 * @author Ian Darwin, http://www.darwinsys.com/
 */
@RunWith(Parameterized.class)
public class WellFormedXmlTest {

	/** Where to look for pages */
	final static String VIEW_DIR = "src/main/webapp/";	// "view" for old Seam2 layout...

	/** Only parse files that end like so */
	final static String FACELETS_EXT = ".xhtml";

	static boolean verbose = false;

	final File xmlFile;
	
	public WellFormedXmlTest(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	/**
	 * Give up the list of files to test.
	 * @Return a List of Object[] the first of each of which is a File[] for a page to test
	 */
	@Parameters
	public static List<Object[]> findFiles() {
		List<File> allFiles = new ArrayList<>(100);
		doDir(new File(VIEW_DIR), allFiles);

		List<Object[]> params = new ArrayList<>(allFiles.size());
		for (File f : allFiles) {
			Object[] a = new Object[]{f};
			params.add(a);
		}
		return params;
	}

	/** doDir - recurse through one filesystem object */
	private static void doDir(File f, List<File> allFiles) {
		if (!f.exists()) {
			throw new IllegalArgumentException(f + " does not exist");
		}
		if (f.isFile()) {
			if (f.getName().endsWith(FACELETS_EXT)) {
				allFiles.add(f);
			}
		} else if (f.isDirectory()) {
			File objects[] = f.listFiles();

			for (int i=0; i<objects.length; i++)
				doDir(objects[i], allFiles);
		} else
			System.err.println("Unknown filesystem object: " + f);
	}

	/**
	 * Parse the one file named in "XmlFile"; will be called many
	 * times by the Parameterized runner, once per file
	 */
	@Test
	public void parseOneFile() throws Exception {
			if (verbose) {
				System.err.println( "Parsing " + xmlFile.getAbsolutePath() + "...");
			}
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			// Disable loading of DTDs; we just want a quick syntax parse
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			DocumentBuilder parser = dbFactory.newDocumentBuilder();
			parser.parse(xmlFile);
			// If we get here, it parsed OK
	}
}
