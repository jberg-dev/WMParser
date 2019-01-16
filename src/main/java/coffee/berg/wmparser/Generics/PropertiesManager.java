package coffee.berg.wmparser.Generics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by Bergerking on 2019-01-16.
 */
public class PropertiesManager
{

	final static Path pathOfPropfile = Paths.get(System.getProperty("user.home") + java.io.File.separator +
			"WMParser" + java.io.File.separator + "config.properties");

	private static File propFile;
	private static Properties prop;

	public final static String lastDirPropName = "lastDir";




	public PropertiesManager ()
	{
		if(!Files.exists(pathOfPropfile.getParent()))
		{
			try
			{
				Files.createDirectories(pathOfPropfile.getParent());
			}catch(Exception e)
			{
				//ignored
			}
		}

		FileReader fileReader;
		propFile = new File(pathOfPropfile.toString());
		prop = new Properties();

		//attempt to read the file, if it does not exist, create it.
		try
		{
			fileReader = new FileReader(propFile);
		} catch (FileNotFoundException e)
		{
			try
			{
				Files.createFile(pathOfPropfile);
			} catch (IOException e1)
			{
				e1.printStackTrace();
				return;
			}

			try
			{
				fileReader = new FileReader(propFile);
			} catch (FileNotFoundException e1)
			{
				e1.printStackTrace();
				return;
			}
		}

		//attempt to read the properties
		try
		{
			prop.load(fileReader);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void setProp (final String _property, final String _value)
	{
		prop.setProperty(_property, _value);
		PrintWriter printWriter = null;

		try
		{
			printWriter = new PrintWriter(pathOfPropfile.toString());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			prop.store(printWriter, "");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static Optional<String> getProperty (final String _property)
	{
		return Optional.ofNullable(prop.getProperty(_property));
	}
}
