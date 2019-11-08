package tk.pratanumandal.roboto;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Random;

import org.apache.commons.lang3.SystemUtils;

public class AppUtils {

	public static int generateRandomKeyCode() {
		Random random = new Random();
		if (random.nextInt(3) == 0) {
			return random.nextInt((57 - 48) + 1) + 48;
		}
		else {
			return random.nextInt((90 - 65) + 1) + 65;
		}
	}
	
	public static boolean openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static boolean shutdown(int time) throws IOException {
	    String shutdownCommand = null, tStr = time == 0 ? "now" : String.valueOf(time), tNum = String.valueOf(time);

	    if(SystemUtils.IS_OS_AIX)
	        shutdownCommand = "shutdown -Fh " + tStr;
	    else if(SystemUtils.IS_OS_FREE_BSD || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC|| SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_NET_BSD || SystemUtils.IS_OS_OPEN_BSD || SystemUtils.IS_OS_UNIX)
	        shutdownCommand = "shutdown -h " + tStr;
	    else if(SystemUtils.IS_OS_HP_UX)
	        shutdownCommand = "shutdown -hy " + tStr;
	    else if(SystemUtils.IS_OS_IRIX)
	        shutdownCommand = "shutdown -y -g " + tStr;
	    else if(SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS)
	        shutdownCommand = "shutdown -y -i5 -g" + tNum;
	    else if(SystemUtils.IS_OS_WINDOWS)
	        shutdownCommand = "shutdown.exe /s /t " + tNum;
	    else
	        return false;

	    Runtime.getRuntime().exec(shutdownCommand);
	    return true;
	}
	
}