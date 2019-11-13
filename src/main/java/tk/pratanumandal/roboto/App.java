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

import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import dorkbox.notify.Notify;
import tk.pratanumandal.roboto.unique.Unique;

public class App {
	
	// port over which application instances will communicate
	public static final int APP_PORT = 31267;
	
	// application frame object
	public static AppFrame frame;
	
	// main entry point for application
	public static void main(String[] args) {
		// use system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// create unique instance
		Unique unique = new Unique(APP_PORT) {
			@Override
			public void receiveMessage(String message) {
				// print received message (timestamp)
				System.out.println(message);
				// bring first instance to front
				frame.setState(JFrame.NORMAL);
				frame.toFront();
				frame.requestFocus();
			}
			
			@Override
			public String sendMessage() {
				// show error message for subsequent instances
				JOptionPane.showMessageDialog(null, "Another instance is already running!", "Roboto", JOptionPane.ERROR_MESSAGE);
				// send timestamp as message
				Timestamp ts = new Timestamp(new Date().getTime());
				return "Another instance launch attempted: " + ts.toString();
			}
		};
		
		// try to lock
		unique.lock();
		
		// pre-initialize the notification framework
		Notify.create();
		
		// create frame
		frame = new AppFrame();
		
		// customize frame exit operation
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame, 
		            "Are you sure you want to exit?", "Roboto", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
		        	// free the unique lock
		        	unique.free();
		        	// exit program gracefully
		            System.exit(0);
		        }
		    }
		});
		
		// customize frame properties
		frame.setResizable(false);
		frame.pack();
		frame.setLocationByPlatform(true);
		
		// display the frame
		frame.setVisible(true);
	}

}
