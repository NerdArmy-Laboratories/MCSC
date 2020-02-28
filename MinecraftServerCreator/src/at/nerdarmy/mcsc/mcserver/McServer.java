package at.nerdarmy.mcsc.mcserver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class McServer {

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



    // +-----------------------------------------------------+
    // |                     Constructor                     |
    // +-----------------------------------------------------+

    public McServer()
    {

    }



    // +-----------------------------------------------------+
    // |                Parameter methods                    |
    // +-----------------------------------------------------+

    public void setPath(String s)
    {
        path = s;
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

    public void setSpawnProtection(String s)
    {
        spawnProtection = s;
    }

    public void setMOTD(String s)
    {
        motd = s;
    }

    public void setMaxPlayers(int i)
    {
        maxPlayers = String.valueOf(i);
    }

    public void setDifficulty(String s)
    {
        difficulty = s;
    }

    public void setGamemode(String s)
    {
        gamemode = s;
    }



    // +-----------------------------------------------------+
    // |          Create, update and load methods            |
    // +-----------------------------------------------------+

    public void createServer()
    {

    }

    public void updateServer()
    {

    }

    public void loadServer()
    {

    }
}
