package programmingtheiot.gda.system;

import java.io.File;

import programmingtheiot.common.ConfigConst;
import static programmingtheiot.gda.system.BaseSystemUtilTask._Logger;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemDiskUtilTask extends BaseSystemUtilTask
{
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public SystemDiskUtilTask()
	{
		super(ConfigConst.NOT_SET, ConfigConst.DEFAULT_TYPE_ID);
	}
	
	
	// public methods
	
	@Override
	public float getTelemetryValue()
	{
		File root = new File("/");
		long totalSpace = root.getTotalSpace();
		long freeSpace = root.getFreeSpace();
		long usedSpace = totalSpace - freeSpace;

		_Logger.fine("Disk used: " + usedSpace + "; Disk total: " + totalSpace);

		double diskUtil = (double) usedSpace / totalSpace * 100.0d;

		return (float) diskUtil;
	}
	
}