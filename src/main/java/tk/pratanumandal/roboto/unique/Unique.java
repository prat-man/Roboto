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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public abstract class Unique {
	
	// lock server port
	private int port;
	
	// lock server socket
	private ServerSocket server;

	// parameterized constructor
	public Unique(int port) {
		this.port = port;
	}
	
	// try to create port lock
	// if not possible, send data to first instance
	public void lock() {
		try {
			// try to create server
			server = new ServerSocket(port);
			
			// server created successfully; this is the first instance
			// keep listening for data from other instances
			Thread thread = new Thread(() -> {
				while (!server.isClosed()) {
					try {
						// establish connection
						Socket socket = server.accept();
						
						// read message
						InputStream is = socket.getInputStream();
		                InputStreamReader isr = new InputStreamReader(is);
		                BufferedReader br = new BufferedReader(isr);
		                
		                String message = br.readLine();
		                if (message == null) message = new String();
						
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
			
		} catch (IOException e1) {
			// get message to be sent to first instance
			String message = sendMessage();
			if (message == null) message = new String();
			
			// send message to first instance
			try {
				// establish connection
				InetAddress address = InetAddress.getByName(null);
				Socket socket = new Socket(address, port);
				
				// write user message
				OutputStream os = socket.getOutputStream();
	            OutputStreamWriter osw = new OutputStreamWriter(os);
	            BufferedWriter bw = new BufferedWriter(osw);
	            
	            bw.write(message);
	            bw.flush();
				
	            // close socket
				socket.close();
			} catch (UnknownHostException e2) {
				e1.printStackTrace();
			} catch (IOException e3) {
				e1.printStackTrace();
			}
			
			// exit this instance
			System.exit(0);
		}
	}
	
	// free the lock if possible
	public void free() {
		try {
			if (server != null) {
				server.close();
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
