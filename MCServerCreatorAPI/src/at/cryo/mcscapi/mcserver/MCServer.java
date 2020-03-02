package at.cryo.mcscapi.mcserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.WRITE;

public class MCServer
{
	// +-----------------------------------------------------+
	// |                  Declare variables                  |
	// +-----------------------------------------------------+

	private String path = null;
	private String version = MCSConstants.supportedVersions[0];
	private String seed = null;
	private String spawnProtection = null;
	private String motd = null;
	private String maxPlayers = null;
	private String difficulty = null;
	private String gamemode = null;

	private File serverFolder = null;
	private File pluginFolder = null;
	private File overworldFolder = null;
	private File netherworldFolder = null;
	private File endworldFolder = null;
	private File serverJar = null;
	private File serverProperties = null;
	private File commandsYml = null;



	// +-----------------------------------------------------+
	// |                     Constructor                     |
	// +-----------------------------------------------------+

	public MCServer() {}



	// +-----------------------------------------------------+
	// |                Parameter methods                    |
	// +-----------------------------------------------------+

	public void setPath(String s)
	{
		path = s;
		serverFolder = null;
		pluginFolder = null;
		overworldFolder = null;
		serverJar = null;
		serverProperties = null;
		commandsYml = null;
	}

	public boolean setVersion(String s)
	{
		if(Arrays.stream(MCSConstants.supportedVersions).anyMatch(s::equalsIgnoreCase))
		{
			version = s;
			return true;
		}else
		{
			return false;
		}
	}

	public void setSeed(String s)
	{
		seed = s;
	}

	public boolean setSpawnProtection(int spawnProtection)
	{
		if(spawnProtection >= 0)
		{
			this.spawnProtection = String.valueOf(spawnProtection);
			return true;
		}else
		{
			return false;
		}
	}

	public void setMOTD(String MOTD)
	{
		motd = MOTD;
	}

	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = String.valueOf(maxPlayers);
	}

	public boolean setDifficulty(String difficulty)
	{
		if(Stream.of("peaceful","easy","normal","hard").anyMatch(difficulty::equalsIgnoreCase))
		{
			this.difficulty = difficulty.toLowerCase();
			return true;
		}else
		{
			return false;
		}
	}

	public boolean setGamemode(String gamemode)
	{
		if(Stream.of("survival","creative","adventure","spectator").anyMatch(gamemode::equalsIgnoreCase))
		{
			this.gamemode = gamemode.toLowerCase();
			return true;
		}else
		{
			return false;
		}
	}



	// +-----------------------------------------------------+
	// |               Configuration methods                 |
	// +-----------------------------------------------------+

	public boolean disableCommand(String command)
	{
		// Check if server is loaded.
		if(isLoaded())
		{
			if(!disabledCommands().contains(command))
			{
				if(!aliasedCommands().containsKey(command))
				{
					try
					{
						// Read all lines from commands.yml.
						List<String> lines = Files.readAllLines(commandsYml.toPath());

						// Disable the command.
						lines.add("  " + command + ":");
						lines.add("  - []");

						// Clear
						PrintWriter writer = new PrintWriter(commandsYml.getPath());
						writer.print("");
						writer.close();

						// Convert list into formated string
						String content = "";
						for(String s : lines)
						{
							content = content + s + "\n";
						}

						// Write new properties int serverProperties
						Files.write(commandsYml.toPath(), content.getBytes(), WRITE);

						return true;
					} catch (Exception e)
					{
						// Print error and return false if something went wrong.
						System.out.println("Error: Something went wrong while reading or writing commands.yml!");
						return false;
					}
				}else
				{
					try
					{
						// Read all lines from commands.yml.
						List<String> lines = Files.readAllLines(commandsYml.toPath());

						// Loop threw all lines.
						for(String line : lines)
						{
							if(line.equalsIgnoreCase("  " + command + ":"))
							{
								lines.set(lines.indexOf(line+1), "  - []");
							}
						}

						// Clear
						PrintWriter writer = new PrintWriter(commandsYml.getPath());
						writer.print("");
						writer.close();

						// Convert list into formated string
						String content = "";
						for(String s : lines)
						{
							content = content + s + "\n";
						}

						// Write new properties int serverProperties
						Files.write(commandsYml.toPath(), content.getBytes(), WRITE);

						return true;
					} catch (Exception e)
					{
						// Print error and return false if something went wrong.
						System.out.println("Error: Something went wrong while reading or writing commands.yml!");
						return false;
					}
				}
			}else
			{
				return true;
			}
		}else
		{
			// Print error and return false, because server is not loaded.
			System.out.println("Error: Server is not loaded!");
			return false;
		}
	}

	public boolean resetCommand(String command)
	{
		// Check if server is loaded.
		if(isLoaded())
		{
			// Check if command is even aliased or disabled.
			if(disabledCommands().contains(command) || aliasedCommands().containsKey(command))
			{
				try
				{
					// Read all lines from commands.yml.
					List<String> lines = Files.readAllLines(commandsYml.toPath());

					// Loop threw all lines.
					for(String line : lines)
					{
						if(line.equalsIgnoreCase("  " + command + ":"))
						{
							lines.remove(lines.indexOf(line));
							lines.remove(lines.indexOf(line)+1);
						}
					}

					// Clear
					PrintWriter writer = new PrintWriter(commandsYml.getPath());
					writer.print("");
					writer.close();

					// Convert list into formated string
					String content = "";
					for(String s : lines)
					{
						content = content + s + "\n";
					}

					// Write new properties int serverProperties
					Files.write(commandsYml.toPath(), content.getBytes(), WRITE);

					return true;
				} catch (Exception e)
				{
					// Print error and return false if something went wrong.
					System.out.println("Error: Something went wrong while reading or writing commands.yml!");
					return false;
				}
			}else
			{
				return true;
			}
		}else
		{
			// Print error and return false, because server is not loaded.
			System.out.println("Error: Server is not loaded!");
			return false;
		}
	}

	public boolean aliasCommand(String command, String aliasCommand)
	{
		// Check if server is loaded.
		if(isLoaded())
		{
			if(!aliasedCommands().containsKey(command))
			{
				if(!disabledCommands().contains(command))
				{
					try
					{
						// Read all lines from commands.yml.
						List<String> lines = Files.readAllLines(commandsYml.toPath());

						// Disable the command.
						lines.add("  " + command + ":");
						lines.add("  - " + aliasCommand);

						// Clear
						PrintWriter writer = new PrintWriter(commandsYml.getPath());
						writer.print("");
						writer.close();

						// Convert list into formated string
						String content = "";
						for(String s : lines)
						{
							content = content + s + "\n";
						}

						// Write new properties int serverProperties
						Files.write(commandsYml.toPath(), content.getBytes(), WRITE);

						return true;
					} catch (Exception e)
					{
						// Print error and return false if something went wrong.
						System.out.println("Error: Something went wrong while reading or writing commands.yml!");
						return false;
					}
				}else
				{
					try
					{
						// Read all lines from commands.yml.
						List<String> lines = Files.readAllLines(commandsYml.toPath());

						// Loop threw all lines.
						for(String line : lines)
						{
							if(line.equalsIgnoreCase("  " + command + ":"))
							{
								lines.set(lines.indexOf(line)+1, "  - " + aliasCommand);
							}
						}

						// Clear
						PrintWriter writer = new PrintWriter(commandsYml.getPath());
						writer.print("");
						writer.close();

						// Convert list into formated string
						String content = "";
						for(String s : lines)
						{
							content = content + s + "\n";
						}

						// Write new properties int serverProperties
						Files.write(commandsYml.toPath(), content.getBytes(), WRITE);

						return true;
					} catch (Exception e)
					{
						// Print error and return false if something went wrong.
						System.out.println("Error: Something went wrong while reading or writing commands.yml!");
						return false;
					}
				}
			}else
			{
				if(!aliasedCommands().get(command).equalsIgnoreCase(aliasCommand))
				{
					try
					{
						// Read all lines from commands.yml.
						List<String> lines = Files.readAllLines(commandsYml.toPath());

						// Loop threw all lines.
						for(String line : lines)
						{
							if(line.equalsIgnoreCase("  " + command + ":"))
							{
								lines.set(lines.indexOf(line+1), "  - " + aliasCommand);
							}
						}

						// Clear
						PrintWriter writer = new PrintWriter(commandsYml.getPath());
						writer.print("");
						writer.close();

						// Convert list into formated string
						String content = "";
						for(String s : lines)
						{
							content = content + s + "\n";
						}

						// Write new properties int serverProperties
						Files.write(commandsYml.toPath(), content.getBytes(), WRITE);

						return true;
					} catch (Exception e)
					{
						// Print error and return false if something went wrong.
						System.out.println("Error: Something went wrong while reading or writing commands.yml!");
						return false;
					}
				}else
				{
					return true;
				}
			}
		}else
		{
			// Print error and return false, because server is not loaded.
			System.out.println("Error: Server is not loaded!");
			return false;
		}
	}

	public boolean installPlugin(String plugin)
	{
		// Check if server is loaded.
		if(isLoaded())
		{
			// Check if the plugin is one of the supported plugins
			if(Arrays.stream(MCSConstants.supportedPlugins).anyMatch(plugin::equalsIgnoreCase))
			{
				plugin = MCSConstants.pluginLinks[Arrays.asList(MCSConstants.supportedPlugins).indexOf(plugin)];
			}

			String name = plugin.substring(plugin.lastIndexOf("/")+1, plugin.length());

			// Download pluginjar.
			System.out.println("Starting download of "+name);
			File pluginJar = new File(pluginFolder, name);
			try {
				// Check if the pluginjar exists.
				if(!pluginJar.exists())
				{
					// If not create one.
					if(!pluginJar.createNewFile())
					{
						// Print out error and return false when something goes wrong
						System.out.println("Error: Something went wrong while creating the pluginjar!");
						return false;
					}
				}

				// Download pluginjar.
				URL url = new URL(plugin);
				ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(pluginJar);
				fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			} catch (Exception e)
			{
				// Print out error and return false.
				System.out.println("Error: Download failed! Please check your internet connection!");
				return false;
			}
			System.out.println("Download has been successful, plugin has been installed!");
			return true;

		}else
		{
			// Print error and return false, because server is not loaded.
			System.out.println("Error: Server is not loaded!");
			return false;
		}
	}

	public boolean removePlugin(String plugin)
	{
		// Check if server is loaded.
		if(isLoaded())
		{
			// Check if the plugin is one of the supported plugins
			if(Arrays.stream(MCSConstants.supportedPlugins).anyMatch(plugin::equalsIgnoreCase))
			{
				plugin = MCSConstants.pluginLinks[Arrays.asList(MCSConstants.supportedPlugins).indexOf(plugin)];
				plugin = plugin.substring(plugin.lastIndexOf("/")+1, plugin.length());
			}

			if(plugin.endsWith(".jar"))
			{
				File pf = new File(pluginFolder, plugin);
				if(pf.isFile() && pf.exists())
				{
					if(!pf.delete())
					{
						// Print error and return false
						System.out.println("Error: The plugin couldn't be deleted!");
						return false;
					}
					System.out.println("The plugin has been removed!");
					return true;
				}else
				{
					// Print error and return false
					System.out.println("Error: The plugin wasn't found!");
					return false;
				}
			}else
			{
				// Print error and return false
				System.out.println("Error: The plugin wasn't found!");
				return false;
			}
		}else
		{
			// Print error and return false, because server is not loaded.
			System.out.println("Error: Server is not loaded!");
			return false;
		}
	}

	public boolean createStartFiles()
	{
		// Check if server is loaded.
		if(isLoaded())
		{
			// Check if the operatingsystem is windows or linux.
			if(System.getProperty("os.name").toLowerCase().contains("win"))
			{
				// Create start.bat
				try
				{
					File startbat = new File(serverFolder, "start.bat");
					if(!startbat.exists())
					{
						if(!startbat.createNewFile())
						{
							// Print error message and return false.
							System.out.println("Error: Couldn't create start.bat!");
							return false;
						}
					}
					String content = "java -Xmx4G -jar " + serverJar.getName() + " nogui\npause";
					Files.write(startbat.toPath(), content.getBytes(), WRITE);
					return true;
				} catch (Exception e)
				{
					// Print error message and return false.
					System.out.println("Error: Couldn't write into start.bat!");
					return false;
				}
			}else if(System.getProperty("os.name").toLowerCase().contains("nix") ||
					System.getProperty("os.name").toLowerCase().contains("nux") ||
					System.getProperty("os.name").toLowerCase().contains("aix"))
			{
				try
				{
					File startsh = new File(serverFolder, "start.sh");
					File startex = new File(serverFolder, "start");

					String content = "java -Xmx4G -jar " + serverJar.getName() + " nogui";

					if(!startsh.exists())
					{
						if(!startsh.createNewFile())
						{
							// Print error message and return false.
							System.out.println("Error: Couldn't create start.sh!");
							return false;
						}
					}
					Files.write(startsh.toPath(), content.getBytes(), WRITE);
					if(!startsh.setExecutable(true))
					{
						// Print warning message and.
						System.out.println("Warning: Couldn't make the start.sh an executable!");
					}

					if(!startex.exists())
					{
						if(!startex.createNewFile())
						{
							// Print error message and return false.
							System.out.println("Error: Couldn't create start executable!");
							return false;
						}
					}
					Files.write(startex.toPath(), content.getBytes(), WRITE);
					if(!startex.setExecutable(true))
					{
						// Print warning message and.
						System.out.println("Warning: Couldn't make the start executable an executable!");
					}
					return true;
				} catch (Exception e)
				{
					// Print error message and return false.
					System.out.println("Error: Couldn't write into start.sh or start executable!");
					return false;
				}
			}else
			{
				// Print error message and return false.
				System.out.println("Error: Couldn't identify your os!");
				return false;
			}
		}else
		{
			// Print error and return false, because server is not loaded.
			System.out.println("Error: Server is not loaded!");
			return false;
		}
	}

	public boolean updateServerProperties()
	{
		// Check if serverProperties is null.
		if(isLoaded())
		{
			try
			{
				// Read all lines from serverProperties.
				List<String> lines = Files.readAllLines(serverProperties.toPath());

				// Loop threw every line.
				for(String line : lines)
				{
					// Check if the line is the one containing a searched properties and if the property is set.
					// If both things apply, change the value of the property to the set one.
					if(line.contains("seed") && seed != null)
					{
						lines.set(lines.indexOf(line), line.substring(0, line.indexOf('=')+1) + seed);
					}else if(line.contains("protection") && spawnProtection != null)
					{
						lines.set(lines.indexOf(line), line.substring(0,line.indexOf('=')+1) + spawnProtection);
					}else if(line.contains("motd"))
					{
						if(motd == null)
						{
							lines.set(lines.indexOf(line), line.substring(0,line.indexOf('=')+1) + "Server created with MCSC!");
						}else
						{
							lines.set(lines.indexOf(line), line.substring(0,line.indexOf('=')+1) + motd);
						}
					}else if(line.contains("players") && maxPlayers != null)
					{
						lines.set(lines.indexOf(line), line.substring(0,line.indexOf('=')+1) + maxPlayers);
					}else if(line.contains("difficulty") && difficulty != null)
					{
						lines.set(lines.indexOf(line), line.substring(0,line.indexOf('=')+1) + difficulty.toLowerCase());
					}else if(line.startsWith("gamemode") && gamemode != null)
					{
						lines.set(lines.indexOf(line), line.substring(0,line.indexOf('=')+1) + gamemode.toLowerCase());
					}
				}

				// Clear server.properties
				PrintWriter writer = new PrintWriter(serverProperties.getPath());
				writer.print("");
				writer.close();

				// Convert list into formated string
				String content = "";
				for(String s : lines)
				{
					content = content + s + "\n";
				}

				// Write new properties int serverProperties
				Files.write(serverProperties.toPath(), content.getBytes(), WRITE);

			} catch (Exception e)
			{
				// Print error message and return false is something goes wrong.
				System.out.println("Error: Something went wrong while reading or writing server.properties!");
				System.out.println("To ensure that the serveproperties are not wrong, please update your server!");
				return false;
			}
		}else
		{
			// If the serverProperties haven't been loaded, print error and return false.
			System.out.println("Error: Server is not loaded!");
			return false;
		}

		return true;
	}

	public boolean loadServerProperties()
	{
		if(isLoaded())
		{
			try
			{
				// Read all lines from commands.yml.
				List<String> lines = Files.readAllLines(serverProperties.toPath());

				// Loop threw all lines.
				for(String line : lines)
				{
					// Check if the line is the one containing a searched properties
					// Save the property into the variable
					String substring = line.substring(line.indexOf('=') + 1, line.length());
					if(line.contains("seed"))
					{
						seed = substring;
					}else if(line.contains("protection"))
					{
						spawnProtection = substring;
					}else if(line.contains("motd"))
					{
						motd = substring;
					}else if(line.contains("players"))
					{
						maxPlayers = substring;
					}else if(line.contains("difficulty"))
					{
						difficulty = substring;
					}else if(line.startsWith("gamemode"))
					{
						gamemode = substring;
					}
				}

				return true;
			} catch (Exception e)
			{
				// Print error and return null if something went wrong.
				System.out.println("Error: Something went wrong while reading commands.yml!");
				return false;
			}
		}else
		{
			// If the serverProperties haven't been loaded, print error and return false.
			System.out.println("Error: Server is not loaded!");
			return false;
		}
	}

	public boolean recreateWorlds()
	{
		if(isLoaded())
		{
			if(!clearFolder(overworldFolder, true))
			{
				System.out.println("Error: Something went wrong while clearing the world folder!");
				return false;
			}
			if(!clearFolder(netherworldFolder, true))
			{
				System.out.println("Error: Something went wrong while clearing the netherworld folder!");
				return false;
			}
			if(!clearFolder(endworldFolder, true))
			{
				System.out.println("Error: Something went wrong while clearing the endworld folder!");
				return false;
			}
			if(!runServerOnce())
			{
				return false;
			}
			return true;
		}else
		{
			// If the serverProperties haven't been loaded, print error and return false.
			System.out.println("Error: Server is not loaded!");
			return false;
		}
	}


	// +-----------------------------------------------------+
	// |                   Read methods                      |
	// +-----------------------------------------------------+

	public boolean isLoaded()
	{
		// Check if all folders and files are set.
		if(serverFolder != null && pluginFolder != null && pluginFolder != null && overworldFolder != null
				&& serverJar != null && serverProperties != null && commandsYml != null && netherworldFolder != null
				&& endworldFolder != null)
		{
			// If so return true.
			return true;
		}else
		{
			// If not return false.
			return false;
		}
	}

	public List<String> disabledCommands()
	{
		// Check if server is loaded.
		if(isLoaded())
		{
			try
			{
				// Read all lines from commands.yml.
				List<String> lines = Files.readAllLines(commandsYml.toPath());

				// Create output list.
				List<String> out = new ArrayList<>();

				// Loop threw all lines.
				for(String line : lines)
				{
					// Check if line is a disabled command.
					if(line.startsWith("  ") && line.endsWith(":") && lines.get(lines.indexOf(line)+1).startsWith("  - []"))
					{
						// Add command to the out list
						out.add(line.substring(2, line.length()-1));
					}
				}

				return out;
			} catch (Exception e)
			{
				// Print error and return null if something went wrong.
				System.out.println("Error: Something went wrong while reading commands.yml!");
				return null;
			}
		}else
		{
			// Print error and return null, because server is not loaded.
			System.out.println("Error: Server is not loaded!");
			return null;
		}
	}

	public HashMap<String, String> aliasedCommands()
	{
		// Check if server is loaded.
		if(isLoaded())
		{
			try
			{
				// Read all lines from commands.yml.
				List<String> lines = Files.readAllLines(commandsYml.toPath());

				// Create output list.
				HashMap<String, String>out = new HashMap<>();

				// Loop threw all lines.
				for(String line : lines)
				{
					// Check if line is a disabled command.
					if(line.startsWith("  ") && line.endsWith(":") && (lines.get(lines.indexOf(line)+1).startsWith("  -")
							&& !lines.get(lines.indexOf(line)+1).startsWith("  - []")))
					{
						// Add command to the out list
						out.put(line.substring(2, line.length()-1),
								lines.get(lines.indexOf(line)+1).substring(4, line.length()-1));
					}
				}

				return out;
			} catch (Exception e)
			{
				// Print error and return null if something went wrong.
				System.out.println("Error: Something went wrong while reading commands.yml!");
				return null;
			}
		}else
		{
			// Print error and return null, because server is not loaded.
			System.out.println("Error: Server is not loaded!");
			return null;
		}
	}



	// +-----------------------------------------------------+
	// |          Create, update and load methods            |
	// +-----------------------------------------------------+

	public boolean createServer()
	{
		if(!pathCheck(true))
		{
			return false;
		}

		if(!serverJarCheck())
		{
			return false;
		}

		if(!EULAcheck())
		{
			return false;
		}

		if(!runServerOnce())
		{
			return false;
		}

		if(!loadFiles())
		{
			return false;
		}

		if(!updateServerProperties())
		{
			return false;
		}

		if(seed != null)
		{
			recreateWorlds();
		}

		createStartFiles();

		System.out.println("Server has been successfully created!");
		return true;
	}

	public void updateServer()
	{
		updateServerProperties();
		//updateServerJar()
	}

	public boolean loadServer()
	{
		if(!pathCheck(false))
		{
			return false;
		}

		if(!loadFiles())
		{
			return false;
		}

		if(!loadServerProperties())
		{
			return false;
		}

		System.out.println("Server has successfully been loaded!");
		return true;
	}



	// +-----------------------------------------------------+
	// |                 Important methods                   |
	// +-----------------------------------------------------+

	private boolean pathCheck(boolean askForDelete)
	{
		// Check if path is valid
		try
		{
			// Check if path is valid. Save the path in a file variable.
			serverFolder = Paths.get(path).toFile();
			// Check if the entered path is a file.
			if(serverFolder.isFile())
			{
				// If so, print out error message.
				System.out.println("Error: The path you specified is a file!");
				return false;
			}
			// Print out the path of the server folder.
			System.out.println("Server path is: " + serverFolder.getAbsolutePath());
		} catch (Exception e)
		{
			// If path is not valid, print out error message.
			System.out.println("Error: You're path is not valid!");
			return false;
		}

		// Check if folder exists.
		if(!serverFolder.exists())
		{
			// If not so, create server folder and all parent folders.
			if(!serverFolder.mkdirs())
			{
				// If folder couldn't be created, print out error message!
				System.out.println("Error: Something when wrong while creating the folder!");
				return false;
			}
		}

		// Check if folder if empty.
		if(Objects.requireNonNull(serverFolder.listFiles()).length != 0 && askForDelete)
		{
			// Todo:
			// Check if it's a server.
			// If so, print, that the server could be loaded.
			// After this is coded add it to the loadFiles() method to autodetect the version and spigot jar

			// Print that files have been detected. Let the user choose between continue, delete and abort.
			System.out.println("The folder you want to install your server in, isn't empty!");
			System.out.println("Do you want to continue without doing anything, delete the files or abort?");
			System.out.print("Continue, Delete, Abort [c/d/A]: ");

			// Read the users answer.
			Scanner scan = new Scanner(System.in);
			String in = scan.next();
			in = in.toLowerCase();

			// Switch case statement for in.
			switch(in)
			{
				case "c":
				case "continue":
					// Continue!
					System.out.println("Continuing without deleting any files.");
					break;

				case "d":
				case "delete":
					// Delete all the files in the folder.
					System.out.println("Deleting all files in the folder.");
					if(!clearFolder(serverFolder, false))
					{
						// If something went wrong, abort.
						System.out.println("Error: Something went wrong while trying to clear the folder!");
						return false;
					}
					break;

				case "a":
				case "abort":
					// Abort!
					System.out.println("Aborting!");
					return false;

				default:
					// Abort, because no valid answer has been entered.
					System.out.println("Didn't detect a valid answer. Aborting!");
					return false;
			}
		}

		// Return true, all checks have been passed.
		return true;
	}

	private boolean serverJarCheck()
	{
		// Check if any files are in the folder.
		if(Objects.requireNonNull(serverFolder.listFiles()).length != 0)
		{
			// Check if any file is a spigot.jar.
			for(File f : Objects.requireNonNull(serverFolder.listFiles()))
			{
				if(f.getName().startsWith("spigot") && f.getName().endsWith(".jar"))
				{
					// Print that a jar has been found. Let the user decide what he wants to do.
					System.out.println("The system has found an already existing serverjar: " + f.getName());
					System.out.println("Dou you want to download a new one or use the existing one?");
					System.out.print("Download, Continue [D/c]: ");

					// Read input.
					Scanner scan = new Scanner(System.in);
					String in = scan.next();
					in = in.toLowerCase();

					// Switch case statement for input.
					switch (in)
					{
						case "c":
						case "continue":
							// Continue
							System.out.println("Continuing using the existing file.");
							serverJar = f;
							return true;

						case "d":
						case "download":
							// Download
							System.out.println("spigot-"+version+".jar is going to be downloaded.");
							break;

						default:
							// Abort, because no valid answer has been entered.
							System.out.println("Didn't detect a valid answer. Aborting!");
							return false;
					}
				}
			}
		}

		// Download spigot.jar.
		System.out.println("Starting download of spigot-" + version + ".jar.");
		serverJar = new File(serverFolder, "spigot-"+version+".jar");
		try {
			// Check if the serverjar exists.
			if(!serverJar.exists())
			{
				// If not create one.
				if(!serverJar.createNewFile())
				{
					// Print out error and return false when something goes wrong
					System.out.println("Error: Something went wrong while creating the serverjar!");
					return false;
				}
			}

			// Download serverjar.
			URL url = new URL("https://github.com/NerdArmy-Laboratories/MCSC/raw/master/SpigotJARs/spigot-"+version+".jar");
			ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(serverJar);
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		} catch (Exception e)
		{
			// Print out error and return false.
			System.out.println("Error: Download failed! Please check your internet connection!");
			return false;
		}
		System.out.println("Download has been successful!");

		// Return true, all checks have benn passed.
		return true;
	}

	private boolean EULAcheck()
	{
		// Ask user if he agrees to the EULA.
		System.out.println("Do you agree to the EULA?");
		System.out.print("Agree [y/N]: ");

		// Read user input
		Scanner scan = new Scanner(System.in);
		String in = scan.next();
		in = in.toLowerCase();

		// Switch case statement for in.
		switch (in)
		{
			case "n":
			case "no":
				// No
				System.out.println("EULA has been declined!");
				return false;

			case "y":
			case "yes":
				// Yes
				System.out.println("EULA has been accepted!");
				break;

			default:
				// Abort, because no valid answer has been entered.
				System.out.println("Didn't detect a valid answer. Aborting!");
				return false;
		}

		// Create eula.txt.
		try
		{
			File eula = new File(serverFolder, "eula.txt");
			String content = "eula=true";
			// Check if the file exists.
			if(!eula.exists())
			{
				if(!eula.createNewFile())
				{
					// Print error and return false if anything went wrong.
					System.out.println("Error: Something went wrong while creating eula.txt!");
					return false;
				}
			}
			// Write content into eula.txt.
			Files.write(eula.toPath(), content.getBytes(), WRITE);
		} catch (Exception e)
		{
			// Print error if something went wrong.
			System.out.println("Error: Something went wrong while creating eula.txt!");
			return false;
		}

		// Return true, all checks have benn passed.
		return true;
	}

	private boolean runServerOnce()
	{
		// Create a new processbuilder.
		System.out.println("Starting server to create all necessary files.");
		ProcessBuilder pb = new ProcessBuilder("java", "-jar", serverJar.getName(), "nogui");
		pb.directory(serverFolder);

		try
		{
			// Start the process from the processbuilder.
			Process p = pb.start();

			// Write stop into the outputstream to stop the server after all files have been created.
			p.getOutputStream().write("stop".getBytes());
			p.getOutputStream().close();

			// For some reason this doesn't work.
            /*if(!p.waitFor(5, TimeUnit.MINUTES))
            {
                p.destroy();
            }*/

			// Wait until server has stopped.
			while(p.isAlive())
			{
				// Print server output, while he is running.
				System.out.print((char) p.getInputStream().read());
			}

			// Destroy the process, if it hasn't been destroyed until now, to save resources.
			p.destroy();
		} catch (Exception e) {
			// Print error and return false.
			System.out.println("Error: Something went wrong while running the server!");
			return false;
		}
		System.out.println("Server has stopped!");

		// Return true, everything has worked out.
		return true;
	}

	private boolean loadFiles()
	{
		pluginFolder = new File(serverFolder, "plugins");
		if(!pluginFolder.exists() || pluginFolder.isFile())
		{
			System.out.println("Error: There was a problem loading the pluginfolder!");
			return false;
		}

		overworldFolder = new File(serverFolder, "world");
		if(!overworldFolder.exists() || overworldFolder.isFile())
		{
			System.out.println("Error: There was a problem loading the worldfolder!");
			return false;
		}

		netherworldFolder = new File(serverFolder, "world_nether");
		if(!netherworldFolder.exists() || netherworldFolder.isFile())
		{
			System.out.println("Error: There was a problem loading the netherworldfolder!");
			return false;
		}


		endworldFolder = new File(serverFolder, "world_the_end");
		if(!endworldFolder.exists() || endworldFolder.isFile())
		{
			System.out.println("Error: There was a problem loading the endworldfolder!");
			return false;
		}

		serverProperties = new File(serverFolder, "server.properties");
		if(!serverProperties.exists() || !serverProperties.isFile())
		{
			System.out.println("Error: There was a problem loading the server.properties!");
			return false;
		}

		commandsYml = new File(serverFolder, "commands.yml");
		if(!commandsYml.exists() || !commandsYml.isFile())
		{
			System.out.println("Error: There was a problem loading the commands.yml!");
			return false;
		}

		if(version == null)
		{
			System.out.println("Error: The serverversion hasn't been set!");
			return false;
		}
		serverJar = new File(serverFolder, "spigot-" + version + ".jar");
		if(!serverJar.exists() || !serverJar.isFile())
		{
			System.out.println("Error: There was a problem loading the serverJar!");
			System.out.println("Please check your version!");
			return false;
		}
		return true;
	}

	// +-----------------------------------------------------+
	// |                    Side methods                     |
	// +-----------------------------------------------------+

	private boolean clearFolder(File folder, boolean deleteSourcefolder)
	{
		// Foreach file in the folder
		for(File f : Objects.requireNonNull(folder.listFiles()))
		{
			// Check if the file is a directory.
			if(f.isDirectory())
			{
				// Clear the next folder
				if(!clearFolder(f, false))
				{
					// If something went wrong return false.
					return false;
				}
			}
			if(!f.delete())
			{
				// If something went wrong return false.
				return false;
			}
		}

		// Check if the folder should delete itself
		if(deleteSourcefolder)
		{
			// Delete the source folder
			if(!folder.delete())
			{
				// If something went wrong return false.
				return false;
			}
		}

		// Return true if everything went as planed.
		return true;
	}
}
