package ch.ehi.gbdbs4bfs;

import java.io.File;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;

/** Main program and commandline interface of gbdbs4bfs.
 */
public class Main {
	
	/** name of application as shown to user.
	 */
	public static final String APP_NAME="gbdbs4bfs";
	/** name of jar file.
	 */
	public static final String APP_JAR="gbdbs4bfs.jar";
	/** version of application.
	 */
	private static String version=null;
	
	/** main program entry.
	 * @param args command line arguments.
	 */
	static public void main(String args[]){
		Settings settings=new Settings();
		int argi=0;
		for(;argi<args.length;argi++){
			String arg=args[argi];
			if(arg.equals("--trace")){
				EhiLogger.getInstance().setTraceFilter(false);
			}else if(arg.equals("--log")) {
			    argi++;
			    settings.setValue(Gbdbs4bfs.SETTING_LOGFILE, args[argi]);
			}else if(arg.equals("--version")){
				printVersion();
				return;
			}else if(arg.equals("--help")){
					printVersion ();
					System.err.println();
					printDescription ();
					System.err.println();
					printUsage ();
					System.err.println();
					System.err.println("OPTIONS");
					System.err.println();
				    System.err.println("--log file            text file, that receives conversion results.");
					System.err.println("--trace               enable trace messages.");
					System.err.println("--help                Display this help text.");
					System.err.println("--version             Display the version of "+APP_NAME+".");
					System.err.println();
					return;
				
			}else if(arg.startsWith("-")){
				EhiLogger.logAdaption(arg+": unknown option; ignored");
			}else{
				break;
			}
		}
		int dataFileCount=args.length-argi;
		if(dataFileCount!=2) {
	        if (dataFileCount == 0) {
	            EhiLogger.logError(APP_NAME+": wrong number of arguments");
	            System.exit(2);                     
	        }
		}
        File fullFile = new File(args[argi++]);
        File reducedFile = new File(args[argi++]);
        
        boolean ok = Gbdbs4bfs.convert(fullFile,reducedFile,settings);
        System.exit(ok ? 0 : 1);
	}
	/** Name of file with program settings. Only used by GUI, not used by commandline version.
	 */
	private final static String SETTINGS_FILE = System.getProperty("user.home") + "/.gbdbs4bfs";
	/** Reads program settings.
	 * @param settings Program configuration as read from file.
	 */
	public static void readSettings(Settings settings)
	{
		java.io.File file=new java.io.File(SETTINGS_FILE);
		try{
			if(file.exists()){
				settings.load(file);
			}
		}catch(java.io.IOException ex){
			EhiLogger.logError("failed to load settings from file "+SETTINGS_FILE,ex);
		}
	}
	/** Writes program settings.
	 * @param settings Program configuration to write.
	 */
	public static void writeSettings(Settings settings)
	{
		java.io.File file=new java.io.File(SETTINGS_FILE);
		try{
			settings.store(file,APP_NAME+" settings");
		}catch(java.io.IOException ex){
			EhiLogger.logError("failed to settings settings to file "+SETTINGS_FILE,ex);
		}
	}
	
	/** Prints program version.
	 */
	protected static void printVersion ()
	{
	  System.err.println(APP_NAME+", Version "+getVersion());
	  System.err.println("  Developed by Eisenhut Informatik AG, CH-3400 Burgdorf");
	}

	/** Prints program description.
	 */
	protected static void printDescription ()
	{
	  System.err.println("DESCRIPTION");
	  System.err.println("  Reduces a GBDBS file to BfS requirements.");
	}

	/** Prints program usage.
	 */
	protected static void printUsage()
	{
	  System.err.println ("USAGE");
	  System.err.println("  java -jar "+APP_JAR+" [Options] full-gbdbs.xml reduced-gbdbs.xml");
	}
	/** Gets version of program.
	 * @return version e.g. "1.0.0"
	 */
	public static String getVersion() {
		  if(version==null){
		java.util.ResourceBundle resVersion = java.util.ResourceBundle.getBundle(ch.ehi.basics.i18n.ResourceBundle.class2qpackageName(Main.class)+".Version");
			StringBuffer ret=new StringBuffer(20);
		ret.append(resVersion.getString("version"));
			ret.append('-');
		ret.append(resVersion.getString("versionCommit"));
			version=ret.toString();
		  }
		  return version;
	}
	
	/** Gets main folder of program.
	 * 
	 * @return folder Main folder of program.
	 */
	static public String getAppHome()
	{
	  String[] classpaths = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
	  for(String classpath:classpaths) {
		  if(classpath.toLowerCase().endsWith(".jar")) {
			  File file = new File(classpath);
			  String jarName=file.getName();
			  if(jarName.toLowerCase().startsWith(APP_NAME)) {
				  file=new File(file.getAbsolutePath());
				  if(file.exists()) {
					  return file.getParent();
				  }
			  }
		  }
	  }
	  return null;
	}
	
}
