package org.unclesniper.choreo.clirun;

import java.net.URL;
import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import java.util.LinkedList;
import org.unclesniper.choreo.Doom;
import org.unclesniper.choreo.CLIRunner;
import java.nio.file.NoSuchFileException;
import org.unclesniper.choreo.BuildContext;
import org.unclesniper.choreo.PropertyUtils;
import org.unclesniper.choreo.ChoreoException;
import org.unclesniper.choreo.PropertyTypeMapper;
import org.unclesniper.choreo.resource.Resources;
import org.unclesniper.choreo.parseopt.OptionSpec;
import org.unclesniper.choreo.parseopt.OptionLogic;
import org.unclesniper.choreo.ChoreoEntityResolver;
import org.unclesniper.choreo.parseopt.OptionPrinter;
import org.unclesniper.choreo.parseopt.UsageWordAction;

public class CLIOptions implements PropertyUtils.EntitySink {

	public enum GraphSourceType {
		FILE,
		URL,
		PREDEF
	}

	public static final String DEFAULT_COMMAND_PREFIX = "java " + CLIRunner.class.getName();

	public static final String DEFAULT_CHOREOGRAPHY_FILE = "choreography.xml";

	public static final String DEFAULT_COMMON_PROPERTIES_FILE = "common.choreo.properties";

	public static final String DEFAULT_SITE_PROPERTIES_FILE = "site.choreo.properties";

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private String commandPrefix;

	private String graphSource = CLIOptions.DEFAULT_CHOREOGRAPHY_FILE;

	private GraphSourceType graphSourceType = GraphSourceType.FILE;

	private String commonProperties = CLIOptions.DEFAULT_COMMON_PROPERTIES_FILE;

	private String siteProperties = CLIOptions.DEFAULT_SITE_PROPERTIES_FILE;

	private boolean searchUp = true;

	private boolean miscTypes = true;

	private boolean refreshModules;

	private final Map<String, ChoreoEntityResolver> entities = new HashMap<String, ChoreoEntityResolver>();

	private final Map<String, URL> mappedServices = new HashMap<String, URL>();

	private final List<URL> unmappedServices = new LinkedList<URL>();

	public CLIOptions() {
		this(null);
	}

	public CLIOptions(String commandPrefix) {
		setCommandPrefix(commandPrefix);
	}

	public String getCommandPrefix() {
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix) {
		this.commandPrefix = commandPrefix == null ? CLIOptions.DEFAULT_COMMAND_PREFIX : commandPrefix;
	}

	public String getGraphSource() {
		return graphSource;
	}

	public void setGraphSource(String graphSource) {
		this.graphSource = graphSource;
	}

	public GraphSourceType getGraphSourceType() {
		return graphSourceType;
	}

	public void setGraphSourceType(GraphSourceType graphSourceType) {
		this.graphSourceType = graphSourceType;
	}

	public String getCommonProperties() {
		return commonProperties;
	}

	public void setCommonProperties(String commonProperties) {
		this.commonProperties = commonProperties;
	}

	public String getSiteProperties() {
		return siteProperties;
	}

	public void setSiteProperties(String siteProperties) {
		this.siteProperties = siteProperties;
	}

	public boolean isSearchUp() {
		return searchUp;
	}

	public void setSearchUp(boolean searchUp) {
		this.searchUp = searchUp;
	}

	public boolean isMiscTypes() {
		return miscTypes;
	}

	public void setMiscTypes(boolean miscTypes) {
		this.miscTypes = miscTypes;
	}

	public boolean isRefreshModules() {
		return refreshModules;
	}

	public void setRefreshModules(boolean refreshModules) {
		this.refreshModules = refreshModules;
	}

	public Iterable<String> getEntityKeys() {
		return entities.keySet();
	}

	public Iterable<Map.Entry<String, ChoreoEntityResolver>> getEntities() {
		return entities.entrySet();
	}

	public ChoreoEntityResolver getEntity(String key) {
		return entities.get(key);
	}

	public void addEntityResolver(String key, ChoreoEntityResolver resolver) {
		key.length();
		if(resolver == null)
			entities.remove(key);
		else
			entities.put(key, resolver);
	}

	public Iterable<String> getMappedServiceKeys() {
		return mappedServices.keySet();
	}

	public Iterable<Map.Entry<String, URL>> getMappedServices() {
		return mappedServices.entrySet();
	}

	public URL getMappedService(String key) {
		return mappedServices.get(key);
	}

	public void addMappedService(String key, URL url) {
		key.length();
		if(url == null)
			mappedServices.remove(key);
		else
			mappedServices.put(key, url);
	}

	public Iterable<URL> getUnmappedServices() {
		return unmappedServices;
	}

	public void addUnmappedService(URL url) {
		if(url != null)
			unmappedServices.add(url);
	}

	public OptionLogic createOptionLogic() {
		OptionLogic logic = new OptionLogic(
			OptionLogic.FL_SHORT_OPTIONS
			| OptionLogic.FL_DOUBLE_LONG_OPTIONS
			| OptionLogic.FL_WORD_SEPARATORS
			| OptionLogic.FL_SYMBOL_SEPARATORS
			| OptionLogic.FL_INLINE_ARGUMENTS
			| OptionLogic.FL_OPTION_TERMINATOR
			| OptionLogic.FL_BAREWORD_TERMINATES
			| OptionLogic.FL_UNRECOGNIZED_TERMINATES
		);
		OptionPrinter printer = new OptionPrinter();
		OptionSpec spec = new OptionSpec(logic, printer)
			.option("choreography", "=FILE", 'c', " FILE",
					new ChoreographyAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Read task object graph from given FILE. Note that only the last among "
					+ "all --choreography, --choreo-url and --predef takes effect."
					+ "|||Defaults to '" + CLIOptions.DEFAULT_CHOREOGRAPHY_FILE + "'.")
			.option("choreo-url", "=URL", 'r', " URL",
					new ChoreoURLAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Read task object graph from given URL. See --choreography for notes.")
			.option("predef", "=NAME", 'p', " NAME",
					new PredefAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Read task object graph from predefined (builtin) file by the given NAME. "
					+ "See --choreography for notes.")
			.option("common-properties", "=FILE",
					new CommonPropertiesAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Read project entity definitions from given FILE. If --choreography "
					+ "(or none of --choreography, --choreo-url or --predef) is used, "
					+ "and the FILE name does not contain any file separator ('" + File.separator
					+ "') characters, the FILE is expected to be in the same directory "
					+ "as the choreography file. Otherwise, it is expected to be in the "
					+ "JVM working directory."
					+ "|||Defaults to '" + CLIOptions.DEFAULT_SITE_PROPERTIES_FILE + "'.")
			.option("site-properties", "=FILE",
					new SitePropertiesAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Read site entity definitions from given FILE. The path name is "
					+ "resolved in the same manner as --common-properties."
					+ "|||Defaults to '" + CLIOptions.DEFAULT_SITE_PROPERTIES_FILE + "'.")
			.option("search-up", "=true|false",
					new SearchUpAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Whether to search all directories from the JVM working directory "
					+ "to the filesystem root for the file given by --choreography, as "
					+ "opposed to only searching the current working directory. Note that "
					+ "this is always inherently 'false' if the name of that file contains "
					+ "a file separator ('" + File.separator + "') character or if "
					+ "--choreo-url or --predef is used."
					+ "|||Defaults to 'true'.")
			.option("misc-types", "=true|false",
					new MiscTypesAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Whether to use builtin non-primitive property type mappers. "
					+ "At present, this includes:" + CLIOptions.getMiscTypeMappers()
					+ "|||Defaults to 'true'.")
			.option("refresh-modules", "[=true|false]",
					new RefreshModulesAction(this), OptionLogic.Arity.OPTIONAL_ARGUMENT,
					"Whether to retrieve modules even if they are already cached."
					+ "|||Defaults to 'false' if the option is not used, and to "
					+ "'true' if the argument is omitted.")
			.option('R', null, new RefreshModulesAction(this), "same as --refresh-modules")
			.option("entity", " KEY=VALUE", 'e', " KEY=VALUE",
					new StringEntityAction(this, false), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Set choreo entity by given KEY to resolve to the string VALUE.")
			.option("esc-entity", " KEY=VALUE", 'E', " KEY=VALUE",
					new StringEntityAction(this, true), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Set choreo entity by given KEY to resolve to the literal string VALUE. "
					+ "In other words, this is equivalent to handing the result of escaping "
					+ "the VALUE to --entity.")
			.option("entity-file", " KEY=PATH", 'f', " KEY=PATH",
					new FileEntityAction(this, false), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Set choreo entity by given KEY to resolve to the contents of the file "
					+ "by the given PATH name.")
			.option("esc-entity-file", " KEY=PATH", 'F', " KEY=PATH",
					new FileEntityAction(this, true), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Set choreo entity by given KEY to resolve to the literal contents of "
					+ "the file by the given PATH name. The contents of the file are run "
					+ "through an escaping filter before reaching the XML parser.")
			.option("entity-url", " KEY=URL", 'u', " KEY=URL",
					new URLEntityAction(this, false), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Set choreo entity by given KEY to resolve as the given URL. In other "
					+ "words, the contents of the document retrieved from the URL "
					+ "constitute the entity.")
			.option("esc-entity-url", " KEY=URL", 'U', " KEY=URL",
					new URLEntityAction(this, true), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Set choreo entity by given KEY to resolve to the literal contants of "
					+ "the document retrieved from the given URL. In other words, the "
					+ "result of escaping the document contents constitutes the entity.")
			.option("properties", " FILE", 'i', " FILE",
					new PropertiesAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Read choreo entity definitions from given property FILE. This option "
					+ "can be used any number of times to import any number of files.")
			.option("property", " KEY=VALUE", 'P', " KEY=VALUE",
					new PropertyAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Set choreo entity by given property, as though KEY=VALUE was a "
					+ "property definition in a file read with --properties.")
			.option("service", " KEY=FILE", 's', " KEY=FILE",
					new FileServiceAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Before reading the main task object graph, read a supplementary "
					+ "object graph from the given FILE. If the given KEY is not empty, "
					+ "bind the root object of that graph to that service key.")
			.option("service-url", " KEY=URL", 'S', " KEY=URL",
					new URLServiceAction(this), OptionLogic.Arity.REQUIRED_ARGUMENT,
					"Like --service, but read graph from given URL instead of a file.")
			.option("help", new UsageWordAction(printer,
					"Usage: " + commandPrefix + " [choreo-options...] [script-arguments...]",
					"Options:"),
					"Print this helpful message and quit.");
		return logic;
	}

	private static String getMiscTypeMappers() {
		BuildContext ctx = new BuildContext();
		StringBuilder builder = new StringBuilder();
		ctx.addMiscTypeMappers();
		for(PropertyTypeMapper mapper : ctx.getTypeMappers()) {
			builder.append("|||~~");
			builder.append(mapper.getClass().getName());
		}
		return builder.toString();
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(CLIOptions.class.getName());
		builder.append(" {");
		builder.append(CLIOptions.LINE_SEPARATOR);
		CLIOptions.dumpStringField("commandPrefix", commandPrefix, builder);
		CLIOptions.dumpStringField("graphSource", graphSource, builder);
		CLIOptions.dumpPlainField("graphSourceType", graphSourceType.name(), builder);
		CLIOptions.dumpStringField("commonProperties", commonProperties, builder);
		CLIOptions.dumpStringField("siteProperties", siteProperties, builder);
		CLIOptions.dumpPlainField("searchUp", searchUp, builder);
		CLIOptions.dumpPlainField("miscTypes", miscTypes, builder);
		CLIOptions.dumpPlainField("refreshModules", refreshModules, builder);
		// begin entities
		builder.append("    entities = {");
		boolean first = true;
		for(Map.Entry<String, ChoreoEntityResolver> entry : entities.entrySet()) {
			if(first)
				first = false;
			else
				builder.append(',');
			builder.append(CLIOptions.LINE_SEPARATOR);
			builder.append("        \"");
			builder.append(entry.getKey());
			builder.append("\" -> ");
			builder.append(entry.getValue().toString());
		}
		builder.append(CLIOptions.LINE_SEPARATOR);
		builder.append("    },");
		builder.append(CLIOptions.LINE_SEPARATOR);
		// end entities
		// begin mappedServices
		builder.append("    mappedServices = {");
		first = true;
		for(Map.Entry<String, URL> entry : mappedServices.entrySet()) {
			if(first)
				first = false;
			else
				builder.append(',');
			builder.append(CLIOptions.LINE_SEPARATOR);
			builder.append("        \"");
			builder.append(entry.getKey());
			builder.append("\" -> \"");
			builder.append(entry.getValue().toString());
			builder.append('"');
		}
		builder.append(CLIOptions.LINE_SEPARATOR);
		builder.append("    },");
		builder.append(CLIOptions.LINE_SEPARATOR);
		// end mappedServices
		// begin unmappedServices
		builder.append("    unmappedServices = [");
		first = true;
		for(URL service : unmappedServices) {
			if(first)
				first = false;
			else
				builder.append(',');
			builder.append(CLIOptions.LINE_SEPARATOR);
			builder.append("        \"");
			builder.append(service.toString());
			builder.append('"');
		}
		builder.append(CLIOptions.LINE_SEPARATOR);
		builder.append("    ]");
		builder.append(CLIOptions.LINE_SEPARATOR);
		// end unmappedServices
		builder.append('}');
		return builder.toString();
	}

	private static void dumpStringField(String name, String value, StringBuilder builder) {
		builder.append("    ");
		builder.append(name);
		builder.append(" = \"");
		builder.append(value);
		builder.append("\",");
		builder.append(CLIOptions.LINE_SEPARATOR);
	}

	private static void dumpPlainField(String name, Object value, StringBuilder builder) {
		builder.append("    ");
		builder.append(name);
		builder.append(" = ");
		builder.append(value.toString());
		builder.append(',');
		builder.append(CLIOptions.LINE_SEPARATOR);
	}

	public BuildContext toBuildContext(String[] argv) throws ChoreoException, IOException {
		File workdir = new File(".").getAbsoluteFile();
		// first, find the choreography file
		File basedir, graphdir;
		URL choreography;
		switch(graphSourceType) {
			case FILE:
				{
					boolean hasfsep = graphSource.indexOf(File.separatorChar) >= 0
							|| graphSource.indexOf('/') >= 0;
					if(searchUp && graphSourceType == GraphSourceType.FILE && !hasfsep) {
						File searchDir = workdir;
						choreography = null;
						basedir = workdir = null;
						do {
							File gfile = new File(searchDir, graphSource);
							if(gfile.isFile()) {
								basedir = workdir = gfile.getParentFile();
								choreography = gfile.toURI().toURL();
								break;
							}
							searchDir = searchDir.getParentFile();
						} while(searchDir != null);
						if(choreography == null)
							throw new NoSuchFileException("No choreography file: " + graphSource);
					}
					else {
						File ab = new File(graphSource);
						File gfile = ab.isAbsolute() ? ab : new File(workdir, graphSource);
						basedir = workdir;
						graphdir = gfile.getParentFile();
						choreography = gfile.exists() ? gfile.toURI().toURL() : null;
					}
				}
			case URL:
				basedir = graphdir = workdir;
				choreography = new URL(graphSource);
				break;
			case PREDEF:
				basedir = graphdir = workdir;
				choreography = Resources.class.getResource("predef/" + graphSource + ".xml");
				if(choreography == null)
					throw new IOException("No such predef: " + graphSource);
				break;
			default:
				throw new Doom("Unrecognized GraphSourceType: " + graphSourceType.name());
		}
		// now find the other files
		File cpropf, spropf;
		if(commonProperties.indexOf(File.separatorChar) < 0 && commonProperties.indexOf('/') < 0
				&& graphSourceType == GraphSourceType.FILE)
			cpropf = new File(graphdir, commonProperties);
		else
			cpropf = new File(commonProperties).getAbsoluteFile();
		if(siteProperties.indexOf(File.separatorChar) < 0 && siteProperties.indexOf('/') < 0
				&& graphSourceType == GraphSourceType.FILE)
			spropf = new File(graphdir, siteProperties);
		else
			spropf = new File(siteProperties).getAbsoluteFile();
		// create skeleton context
		BuildContext ctx = new BuildContext();
		ctx.addDecodePrimitiveTypeMappers();
		if(miscTypes)
			ctx.addMiscTypeMappers();
		ctx.setRefreshModules(refreshModules);
		for(Map.Entry<String, ChoreoEntityResolver> entry : entities.entrySet())
			ctx.addEntityResolver(entry.getKey(), entry.getValue());
		// add initial services
		final String svcpfx = BuildContext.class.getName();
		ctx.putServiceObject(svcpfx + ".baseDir", basedir);
		ctx.putServiceObject(svcpfx + ".basePath", basedir.getPath());
		ctx.putServiceObject(svcpfx + ".argv", argv == null ? new String[0] : argv);
		// parse service resources
		for(Map.Entry<String, URL> entry : mappedServices.entrySet())
			CLIOptions.loadServiceResource(ctx, entry.getKey(), entry.getValue());
		for(URL surl : unmappedServices)
			CLIOptions.loadServiceResource(ctx, null, surl);
		// finish up
		ctx.parseURL(choreography);
		ctx.propagateError();
		return ctx;
	}

	private static void loadServiceResource(BuildContext ctx, String key, URL url)
			throws ChoreoException, IOException {
		ctx.parseURL(url);
		ctx.propagateError();
		if(key != null)
			ctx.putServiceObject(key, ctx.getRootObject());
	}

}
