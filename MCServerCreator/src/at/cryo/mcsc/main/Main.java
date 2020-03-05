package at.cryo.mcsc.main;

import at.cryo.mcscapi.mcserver.MCServer;

public class Main
{
	private static boolean setBaseParameters()
	{
		return false;
	}

	public static void main(String[] args)
	{
		MCServer mcs = new MCServer();
		if(args[0].equalsIgnoreCase("-c"))
		{

		}else if(args[0].equalsIgnoreCase("-e"))
		{

		}else if(args[0].equalsIgnoreCase("-sc"))
		{
			setBaseParameters();
			if(!mcs.load())
			{
				System.out.println("Error: Server couldn't be loaded!");
				return;
			}
			if(!mcs.showConfig())
			{
				System.out.println("Error: Something went wrong while reading the config!");
				return;
			}
		}else if(args[0].equalsIgnoreCase("-d"))
		{
			setBaseParameters();
			if(!mcs.load())
			{
				System.out.println("Error: Server couldn't be loaded!");
				return;
			}
			if(!mcs.delete())
			{
				System.out.println("Error: Server couldn't be deleted!");
			}
			return;
		}else
		{
			System.out.println("Error: Wrong arguments!");
			return;
		}
	}
}
