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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import at.nerdarmy.mcsc.mcserver.McServer;

import static java.nio.file.StandardOpenOption.WRITE;

public class Main {

    private static String[] disableCommands = {"bukkit:help", "bukkit:plugins", "bukkit:pl", "bukkit:ver",
            "bukkit:about", "bukkit:reload", "bukkit:rl", "bukkit:timings", "bukkit:version", "bukkit:?",
            "help", "plugins", "pl", "ver", "about", "reload", "rl", "timings", "version", "'?'"};

    public static void main(String[] args)
    {
        McServer mcs = new McServer();

        mcs.setPath("F:\\NewMinecraftServer");
        mcs.setVersion("1.15.2");
        /*mcs.setSeed("-3093782458931373062");
        mcs.setDifficulty("normal");
        mcs.setGamemode("survival");
        mcs.setMOTD("The ULTIMATE minecraft server");
        mcs.setMaxPlayers(100);
        mcs.setSpawnProtection(0);*/

        // mcs.createServer();
        // mcs.loadServer();
        // mcs.installPlugin("Essentials");
        // mcs.removePlugin("Essentials");
        /*for(String s : disableCommands)
        {
            mcs.disableCommand(s);
        }

        mcs.aliasCommand("reload", "Essentials:reload");
        mcs.aliasCommand("rl", "Essentials:rl");
        mcs.aliasCommand("plugins", "Essentials:plugins");
        mcs.aliasCommand("pl", "Essentials:pl");

        */

        /*Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);*/
    }
}
