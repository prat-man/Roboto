/*
 * Roboto - Random mouse movement and keyboard key press simulator
 * 
 * Copyright (C) 2019  Pratanu Mandal
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 */

package tk.pratanumandal.roboto;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Random;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.SystemUtils;

import dorkbox.notify.Pos;
import dorkbox.notify.Notify;

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
	
	public static Notify notify(String title, String message) {
		return notify(title, message, 3000, Notification.NONE);
	}
	
	public static Notify notify(String title, String message, Notification notification) {
		return notify(title, message, 3000, notification);
	}
	
	public static Notify notify(String title, String message, int time) {
		return notify(title, message, time, Notification.NONE);
	}
	
	public static Notify notify(String title, String message, int time, Notification notification) {
		Notify notify = Notify.create()
							  .title(title)
							  .text(message)
							  .position(Pos.TOP_RIGHT)
							  .hideAfter(time)
							  .darkStyle();
		
		SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
				if (notification == Notification.NONE) {
					notify.show();
				}
				else if (notification == Notification.INFORMATION) {
					notify.showInformation();
				}
				else if (notification == Notification.QUESTION) {
					notify.showConfirm();
				}
				else if (notification == Notification.WARNING) {
					notify.showWarning();
				}
				else if (notification == Notification.ERROR) {
					notify.showError();
				}
	        }
	    });
		
		return notify;
	}
	
	public static enum Notification {
		NONE, INFORMATION, QUESTION, WARNING, ERROR
	}
	
}
