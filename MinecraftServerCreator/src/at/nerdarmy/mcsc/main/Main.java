package at.nerdarmy.mcsc.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
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
                f.delete();
            }
        }
    }

    public static void main(String[] args)
    {
        // Arguments
        String path = "F:\\NewMinecraftServer";
        String version = "1.15.2";

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
            if(folder.listFiles() != null)
            {
                System.out.println("The folder you want to create the server in isn't empty!");
                System.out.print("Delete files [y/N]: ");
                Scanner scan = new Scanner(System.in);
                String in = scan.next();
                if(in.equalsIgnoreCase("y"))
                {
                    System.out.print("Deleting files...");
                    clearFolder(folder);
                }
            }
        }

        // Download file
        File sourceServerJar = new File("F:\\MCSC\\SpigotJARs\\spigot-"+version+".jar");


        // Move file into folder
        File ServerJar = new File(folder, sourceServerJar.getName());
        try
        {
            Files.copy(sourceServerJar.toPath(), ServerJar.toPath(), REPLACE_EXISTING);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // Create start file
        if(System.getProperty("os.name").toLowerCase().contains("win"))
        {
            try
            {
                File startbat = new File(folder, "start.bat");
                startbat.createNewFile();
                String content = "java -Xmx4G -jar spigot-" + version + ".jar nogui\npause";
                Files.write(startbat.toPath(), content.getBytes(), WRITE);

            } catch (Exception e)
            {

                e.printStackTrace();

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

                startsh.createNewFile();
                Files.write(startsh.toPath(), content.getBytes(), WRITE);
                startsh.setExecutable(true);

                startex.createNewFile();
                Files.write(startex.toPath(), content.getBytes(), WRITE);
                startex.setExecutable(true);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }else
        {
            System.out.println("Error: Couldn't identify your os!");
            return;
        }

        // Create the eula.txt file
        try
        {
            File eula = new File(folder, "eula.txt");
            String content = "eula=true";
            eula.createNewFile();
            Files.write(eula.toPath(), content.getBytes(), WRITE);
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

        // Todo:
        // Install Essentials
        // Disable default commands
        // Set right server properties
        // Really download spigot jar

        File pluginFolder = new File(folder, "plugins");
        File serverproperties = new File(folder, "server.properties");
        File commandsyml = new File(folder, "commands.yml");

        // End Output
        System.out.println("End");
    }
}
