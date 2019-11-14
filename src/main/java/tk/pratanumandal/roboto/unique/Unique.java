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

package tk.pratanumandal.roboto.unique;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public abstract class Unique {
	
	// application ID
	private final String APP_ID;
	
	// lock server port
	private int port;
	
	// starting position of port check
	public static final int PORT_START = 3000;
	
	// system temporary directory path
	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
	
	// lock server socket
	private ServerSocket server;

	// parameterized constructor
	public Unique(String APP_ID) {
		this.APP_ID = APP_ID;
	}
	
	// try to obtain lock
	// if not possible, send data to first instance
	public void lock() {
		port = lockFile();
		
		if (port == -1) {
			startServer();
		}
		else {
			doClient();
		}
	}
	
	// start the server
	public void startServer() {
		// try to create server
		port = PORT_START;
		while (true) {
			try {
				server = new ServerSocket(port);
				break;
			} catch (IOException e) {
				port++;
			}
		}
		
		if (lockFile(port)) {
			// server created successfully; this is the first instance
			// keep listening for data from other instances
			Thread thread = new Thread(() -> {
				while (!server.isClosed()) {
					try {
						// establish connection
						Socket socket = server.accept();
						
						// open writer
						OutputStream os = socket.getOutputStream();
						PrintWriter pw = new PrintWriter(os, true);
						
						// open reader
						InputStream is = socket.getInputStream();
		                InputStreamReader isr = new InputStreamReader(is);
		                BufferedReader br = new BufferedReader(isr);
		                
		                // read message from client
		                String message = br.readLine();
		                if (message == null) message = new String();
		                
		                // write response to client
		                pw.write(APP_ID + "\r\n");
		                pw.flush();
		                
		                // close writer and reader
		                pw.close();
		    	        br.close();
						
		                // perform user action on message
						receiveMessage(message);
						
						// close socket
						socket.close();
					} catch (SocketException e1) {
						// do nothing
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}
			});
			
			thread.start();
		}
		else {
			System.err.println("Failed to created lock file!");
		}
	}
	
	// do client tasks
	public void doClient() {
		// establish connection
		InetAddress address = null;
		try {
			address = InetAddress.getByName(null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		Socket socket = null;
		try {
			// try to establish connection to server
			socket = new Socket(address, port);
			
			// get message to be sent to first instance
			String message = sendMessage();
			if (message == null) message = new String();
			
			// open writer
			OutputStream os = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(os, true);
			
			// open reader
			InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
	        
            // write message to server
	        pw.write(message + "\r\n");
	        pw.flush();
	        
	        // read response from server
	        String response = br.readLine();
	        if (response == null) response = new String();
	        
	        // close writer and reader
	        pw.close();
	        br.close();
	        
	        if (response.equals(APP_ID)) {
	        	// validation successful, exit this instance
				System.exit(0);
	        }
	        else {
	        	// validation failed, this is the first instance
	        	startServer();
	        }
		} catch (IOException e) {
			startServer();
		} finally {
			// close socket
	     	try {
	     		if (socket != null) socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// try to get port from lock file
	public int lockFile() {
		// lock file path
		String filePath = TEMP_DIR + "/" + APP_ID + ".lock";
		File file = new File(filePath);
		
		// try to get port from lock file
		if (file.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				return Integer.parseInt(br.readLine());
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null) br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return -1;
	}
	
	// try to write port to lock file
	public boolean lockFile(int port) {
		// lock file path
		String filePath = TEMP_DIR + "/" + APP_ID + ".lock";
		File file = new File(filePath);
		
		// try to write port to lock file
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			bw.write(String.valueOf(port));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (bw != null) bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// free the lock if possible
	public void free() {
		try {
			if (server != null) {
				server.close();
			}
			
			String filePath = TEMP_DIR + "/" + APP_ID + ".lock";
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// message received by first instance
	public abstract void receiveMessage(String message);
	
	// message sent by subsequent instances
	public abstract String sendMessage();
	
}
