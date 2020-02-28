package at.nerdarmy.mcsc.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.WRITE;

public class Main {

    private static void clearFolder(File folder)
    {
        if(folder.listFiles() != null)
        {
            for(File f : folder.listFiles())
            {
                if(f.isDirectory())
                {
                    clearFolder(f);
                }
                if(!f.delete())
                {
                    System.out.println("Warning: Couldn't delete " + f.getName() + " folder!");
                }
            }
        }
    }

    public static void main(String[] args)
    {
        // Arguments
        String path = "D:\\2-Projekte\\MCSC\\NewMinecraftServer";
        String version = "1.15.2";

        String seed = null;
        String spawnProtection = null;
        String motd = null;
        String maxPlayers = null;
        String difficulty = null;
        String gamemode = null;

        for(String s : args)
        {
            if(s.contains("seed="))
            {
                seed = s.replace("seed=","");
            }else if(s.contains("sp="))
            {
                spawnProtection = s.replace("sp=", "");
            }else if(s.contains("motd="))
            {
                motd = s.replace("motd=", "");
            }else if(s.contains("mp="))
            {
                maxPlayers = s.replace("mp=", "");
            }else if(s.contains("dc="))
            {
                difficulty = s.replace("dc=", "");
            }else if(s.contains("gm="))
            {
                gamemode = s.replace("gm=", "");
            }else
            {
                System.out.println("Error: Wrong argument: " + s);
            }
        }

        File folder = new File(path);

        // Check if folder exists
        if(!folder.exists())
        {
            // If the folder doesn't exist, create it and check if it has been created successfully
            if(!folder.mkdirs())
            {
                System.out.println("Error: The Serverfolder couldn't be created!");
                return;
            }
        }else
        {
            if(folder.listFiles().length == 0)
            {
                System.out.println("The folder you want to create the server in isn't empty!");
                System.out.print("Delete files [y/N]: ");
                Scanner scan = new Scanner(System.in);
                String in = scan.next();
                if(in.equalsIgnoreCase("y"))
                {
                    System.out.println("Deleting files...");
                    clearFolder(folder);
                }
            }
        }

        // Download file
        // Link: https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
        // Github Link https://github.com/NerdArmy-Laboratories/MCSC/raw/master/SpigotJARs/spigot-1.15.2.jar
        System.out.println("Starting download of serverjar.");
        File ServerJar = new File(folder, "spigot-"+version+".jar");
        try {
            if(!ServerJar.exists())
            {
                ServerJar.createNewFile();
            }
            URL url = new URL("https://github.com/NerdArmy-Laboratories/MCSC/raw/master/SpigotJARs/spigot-"+version+".jar");
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(ServerJar);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: Download failed! Please check your internet connection!");
            return;
        }
        System.out.println("Finished downloading.");

        // Create start file
        if(System.getProperty("os.name").toLowerCase().contains("win"))
        {
            try
            {
                File startbat = new File(folder, "start.bat");
                if(!startbat.exists())
                {
                    startbat.createNewFile();
                }
                String content = "java -Xmx4G -jar spigot-" + version + ".jar nogui\npause";
                Files.write(startbat.toPath(), content.getBytes(), WRITE);

            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Warning: Couldn't create start.bat!");
            }
        }else if(System.getProperty("os.name").toLowerCase().contains("nix") ||
                 System.getProperty("os.name").toLowerCase().contains("nux") ||
                 System.getProperty("os.name").toLowerCase().contains("aix"))
        {
            try
            {
                File startsh = new File(folder, "start.sh");
                File startex = new File(folder, "start");

                String content = "java -Xmx4G -jar spigot-" + version + ".jar nogui";

                if(!startsh.exists())
                {
                    startsh.createNewFile();
                }
                Files.write(startsh.toPath(), content.getBytes(), WRITE);
                startsh.setExecutable(true);

                if(!startex.exists())
                {
                    startex.createNewFile();
                }
                Files.write(startex.toPath(), content.getBytes(), WRITE);
                startex.setExecutable(true);
            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Warning: Couldn't create start.sh or start executable!");
            }
        }else
        {
            System.out.println("Error: Couldn't identify your os!");
            return;
        }

        // Create the eula.txt file
        try
        {
            System.out.print("Dou you agree to the EULA? [y/N]: ");
            Scanner scan = new Scanner(System.in);
            String in = scan.next();
            if(in.equalsIgnoreCase("y"))
            {
                File eula = new File(folder, "eula.txt");
                String content = "eula=true";
                if(!eula.exists())
                {
                    eula.createNewFile();
                }
                Files.write(eula.toPath(), content.getBytes(), WRITE);
            }else
            {
                System.out.println("Error: User didn't agree to the EULA!");
                return;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        // Run server
        System.out.println("Running server to create all files!");
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", ServerJar.getName(), "nogui");
        pb.directory(folder);
        try
        {
            Process p = pb.start();
            p.getOutputStream().write("stop".getBytes());
            p.getOutputStream().close();

            // For some reason this doesn't work.
            /*if(!p.waitFor(5, TimeUnit.MINUTES))
            {
                p.destroy();
            }*/

            while(p.isAlive())
            {
                System.out.print((char) p.getInputStream().read());
            }
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server has stopped!");

        // Set right server properties
        File serverproperties = new File(folder, "server.properties");
        try {
            List<String> lines =  Files.readAllLines(serverproperties.toPath());
            for(String line : lines)
            {
                if(line.contains("seed") && seed != null)
                {
                    lines.set(lines.indexOf(line), lines.get(lines.indexOf(line)) + seed);
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
                    if(Stream.of("easy", "hard", "normal", "peaceful").anyMatch(difficulty::equalsIgnoreCase))
                    {
                        lines.set(lines.indexOf(line), line.substring(0,line.indexOf('=')+1) + difficulty.toLowerCase());
                    }else
                    {
                        System.out.println("Error: Couldn't set difficulty!");
                    }
                }else if(line.contains("gamemode") && gamemode != null)
                {
                    if(Stream.of("survival", "creative", "adventure", "spectator").anyMatch(gamemode::equalsIgnoreCase))
                    {
                        lines.set(lines.indexOf(line), line.substring(0,line.indexOf('=')+1) + gamemode.toLowerCase());
                    }else
                    {
                        System.out.println("Error: Couldn't set gamemode!");
                    }
                }
            }

            PrintWriter writer = new PrintWriter(serverproperties.getPath());
            writer.print("");
            writer.close();

            Files.write(serverproperties.toPath(), lines.toString().replace("[","")
                                                                    .replace("]","")
                                                                    .replace(", ","\n")
                                                                    .getBytes(), WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Warning: Something went wrong with the server.properties!");
        }

        // Install Essentials
        File pluginFolder = new File(folder, "plugins");
        File essentialsJar = new File(pluginFolder, "Essentials.jar");
        System.out.println("Starting download of Essentials.jar");
        try {
            if(!essentialsJar.exists())
            {
                essentialsJar.createNewFile();
            }
            URL url = new URL("https://github.com/NerdArmy-Laboratories/MCSC/raw/master/SpigotJARs/spigot-"+version+".jar");
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(ServerJar);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: Download failed! Please check your internet connection!");
            return;
        }
        System.out.println("Finished downloading.");



        // Todo:
        // Disable default commands
        File commandsyml = new File(folder, "commands.yml");

        // End Output
        System.out.println("End");
    }
}
