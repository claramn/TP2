package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.DefaultRegionBuilder;
import simulator.factories.DynamicSupplyRegionBuilder;
import simulator.factories.Factory;
import simulator.factories.SelectClosestBuilder;
import simulator.factories.SelectFirstBuilder;
import simulator.factories.SelectYoungestBuilder;
import simulator.factories.SheepBuilder;
import simulator.factories.WolfBuilder;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;
import simulator.view.MainWindow;

public class Main {

	private static Simulator _sim;
	private static Controller _cont;
	private static Factory<SelectionStrategy> selection_strategy_factory;
	public static Factory<Region> region_factory;	
	public static Factory<Animal> animal_factory;

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	// default values for some parameters
	private final static Double _default_time = 10.0;
	private final static Double _default_dt = 0.03;
	private final static int _default_width = 600;
	private final static int _default_height = 800;
	private final static int _default_cols = 20;
	private final static int _default_rows = 15;
	
	// some attributes to stores values corresponding to command-line parameters
	private static Double _time = 10.0;
	private static Double _dt = 0.03;
	private static String _in_file = null;
	private static String _out_file = null;
	private static ExecMode _mode = ExecMode.GUI;
	private static boolean _sv = false;

	private static void parse_args(String[] args) {

		// define the valid command line options
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		CommandLineParser parser = new DefaultParser();
		
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_time_option(line);
			parse_dt_option(line);
			parse_sv_option(line);
			parse_GUI_option(line);
			parse_out_file_option(line);
			
			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}
		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());
		// dt
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in." + _default_dt + ".").build());
		// sv
		cmdLineOptions.addOption(
				Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());
		// output file
		cmdLineOptions.addOption(
				Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());
		//m
		cmdLineOptions.addOption(
				Option.builder("m").longOpt("mode").hasArg().desc("Execution Mode: batch | gui. Default value: gui.")
                        .build());
		
		return cmdLineOptions;
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}
	
	private static void parse_GUI_option(CommandLine line) throws ParseException {
		if (line.hasOption("m")) {
			String m = line.getOptionValue("m");
			if (m.equals("batch"))
				_mode = ExecMode.BATCH;
			else if (m.equals("gui"))
				_mode = ExecMode.GUI;
			else 
				throw new ParseException("Invalid value for mode: " + m);
		}
	}

	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) 
			throw new ParseException("In batch mode an input configuration file is required");
	}

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} 
		catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}

	private static void parse_dt_option(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _default_dt.toString());
		try {
			_dt = Double.parseDouble(dt);
			assert (_dt >= 0);
		} 
		catch (Exception e) {
			throw new ParseException("Invalid value for time: " + dt);
		}
	}

	private static void parse_sv_option(CommandLine line) throws ParseException {
		if (line.hasOption("sv")) 
			_sv = true;
	}

	private static void parse_out_file_option(CommandLine line) throws ParseException {
		_out_file = line.getOptionValue("o");
		if (_mode == ExecMode.BATCH && _out_file == null) 
			_out_file ="resources/examples/outer.json";
	}

	public static void init_factories() {
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
		selection_strategy_builders.add(new SelectFirstBuilder());
		selection_strategy_builders.add(new SelectClosestBuilder());
		selection_strategy_builders.add(new SelectYoungestBuilder());
		selection_strategy_factory = new BuilderBasedFactory<SelectionStrategy>(selection_strategy_builders);

		// initialize the animals factory
		List<Builder<Animal>> animal_builder = new ArrayList<>();
		animal_builder.add(new SheepBuilder(selection_strategy_factory));
		animal_builder.add(new WolfBuilder(selection_strategy_factory));
		animal_factory = new BuilderBasedFactory<Animal>(animal_builder);

		// initializes regions factory
		List<Builder<Region>> region_builder = new ArrayList<>();
		region_builder.add(new DynamicSupplyRegionBuilder("dynamic", "dynamic"));
		region_builder.add(new DefaultRegionBuilder("default", "default"));
		region_factory = new BuilderBasedFactory<Region>(region_builder);
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}

	private static void start_batch_mode() throws Exception {
		InputStream is = new FileInputStream(new File(_in_file));
		JSONObject file = load_JSON_file(is);
		OutputStream os = new FileOutputStream(new File(_out_file));
		_sim = new Simulator(file.getInt("cols"), file.getInt("rows"), file.getInt("width"), file.getInt("height"),
				animal_factory, region_factory);
		_cont = new Controller(_sim);
		_cont.load_data(file);
		_cont.run(_time, _dt, _sv, os);
		os.close();
	}

	private static void start_GUI_mode() throws Exception {
		try {
			JSONObject file = null;
			if(_in_file != null && !_in_file.isEmpty()) {
				InputStream is = new FileInputStream(new File(_in_file));
				file = load_JSON_file(is);
				_sim = new Simulator(file.getInt("cols"), file.getInt("rows"), file.getInt("width"), file.getInt("height"),
						animal_factory, region_factory);
			}
			else 
				_sim = new Simulator(_default_cols, _default_rows, _default_width, _default_height,
						animal_factory, region_factory);
			
			_cont = new Controller(_sim);
			
			if (file != null) 
				_cont.load_data(file);

			SwingUtilities.invokeAndWait(() -> new MainWindow(_cont));
			
		} 
		catch (Exception e) {
        	throw new UnsupportedOperationException("GUI mode is not ready yet ...");
        }
	}

	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (_mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l);
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}

