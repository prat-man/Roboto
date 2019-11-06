package tk.pratanumandal.roboto;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class AppFrame extends JFrame {
	
	private static final long serialVersionUID = -1748889400784668366L;
	
	private ScheduledFuture<?> mouseFuture;
	private ScheduledFuture<?> keyboardFuture;
	
	private TrayIcon trayIcon;
	
	public AppFrame() {
		// call super constructor and set frame title
		super("Roboto 1.1");
		
		// set frame icon
		try {
			String imagePath = "robot.png";
			InputStream imgStream = this.getClass().getClassLoader().getResourceAsStream(imagePath);
			BufferedImage image = ImageIO.read(imgStream);
			this.setIconImage(image);
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		
		// initialize tray icon
		initializeTray();
		
		// initialize container which acts as a wrapper for mousePanel and keyboardPanel
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(2, 1, 0, 10));
		Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		container.setBorder(padding);
		
		this.add(container, BorderLayout.CENTER);
		
		// initialize mouse panel
		JPanel mousePanel = new JPanel();
		mousePanel.setBorder(new TitledBorder("<html><span style=\"font-size: 1.1em; font-weight: bold;\">Mouse</span></html>"));
		
		JButton mouseStartButton = new JButton("Start");
		mouseStartButton.setMargin(new Insets(3, 35, 3, 35));
		mouseStartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mousePanel.add(mouseStartButton);
		
		JButton mouseStopButton = new JButton("Stop");
		mouseStopButton.setMargin(new Insets(3, 35, 3, 35));
		mouseStopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mouseStopButton.setEnabled(false);
		mousePanel.add(mouseStopButton);
		
		container.add(mousePanel);
		
		// initialize keyboard panel
		JPanel keyboardPanel = new JPanel();
		keyboardPanel.setBorder(new TitledBorder("<html><span style=\"font-size: 1.1em; font-weight: bold;\">Keyboard</span></html>"));
		
		JButton keyboardStartButton = new JButton("Start");
		keyboardStartButton.setMargin(new Insets(3, 35, 3, 35));
		keyboardStartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		keyboardPanel.add(keyboardStartButton);
		
		JButton keyboardStopButton = new JButton("Stop");
		keyboardStopButton.setMargin(new Insets(3, 35, 3, 35));
		keyboardStopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		keyboardStopButton.setEnabled(false);
		keyboardPanel.add(keyboardStopButton);
		
		container.add(keyboardPanel);
		
		
		// initialize information panel
		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
		
		this.add(infoPanel, BorderLayout.SOUTH);
		
		// initialize information label
		JLabel infoLabel = new JLabel("<html>Developed by <span style=\"color: blue;\">Pratanu Mandal</span></html>");
		infoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		infoPanel.add(infoLabel);
		
		
		// start action for mouse
		mouseStartButton.addActionListener((event) -> {
			try {
				// initialize robot
				Robot robot = new Robot();
				
				// initialize screen size
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				int MAX_X = (int) screenSize.getWidth();
				int MAX_Y = (int) screenSize.getHeight();
				
				// start scheduler to move mouse every 5 seconds
				ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
				mouseFuture = exec.scheduleAtFixedRate(() -> {
					// initialize random
					Random random = new Random();
					// move mouse
					robot.mouseMove(random.nextInt(MAX_X), random.nextInt(MAX_Y));
				}, 0, 5, TimeUnit.SECONDS);
				
				System.out.println("Mouse Scheduler Started");
				displayTray("Roboto", "Mouse Scheduler Started");
				
				mouseStartButton.setEnabled(false);
				mouseStopButton.setEnabled(true);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		});
		
		// stop action for mouse
		mouseStopButton.addActionListener((event) -> {
			mouseFuture.cancel(true);
			
			System.out.println("Mouse Scheduler Stopped");
			displayTray("Roboto", "Mouse Scheduler Stopped");
			
			mouseStartButton.setEnabled(true);
			mouseStopButton.setEnabled(false);
		});
		
		// start action for keyboard
		keyboardStartButton.addActionListener((event) -> {
			try {
				// initialize robot
				Robot robot = new Robot();
				
				// start scheduler to move keyboard every 5 seconds
				ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
				keyboardFuture = exec.scheduleAtFixedRate(() -> {
					// initialize random
					Random random = new Random();
					// initialize random keycode
					int keycode = generateRandomKeyCode();
					// if alphabet, chance based lower case or upper case
					// if number, chance based number or symbol
					boolean shift = false;
					if (random.nextInt(2) == 0) {
						shift = true;
					}
					// press shift key if required
					if (shift) {
						robot.keyPress(16);
					}
					// press key
					robot.keyPress(keycode);
					// release key
					robot.keyRelease(keycode);
					// release shift key if required
					if (shift) {
						robot.keyRelease(16);
					}
				}, 0, 1, TimeUnit.SECONDS);
				
				System.out.println("Keyboard Scheduler Started");
				displayTray("Roboto", "Keyboard Scheduler Started");
				
				keyboardStartButton.setEnabled(false);
				keyboardStopButton.setEnabled(true);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		});
		
		// stop action for keyboard
		keyboardStopButton.addActionListener((event) -> {
			keyboardFuture.cancel(true);
			
			System.out.println("Keyboard Scheduler Stopped");
			displayTray("Roboto", "Keyboard Scheduler Stopped");
			
			keyboardStartButton.setEnabled(true);
			keyboardStopButton.setEnabled(false);
		});
		
		// information label action
		infoLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					openWebpage(new URI("https://github.com/prat-man/Roboto"));
				} catch (URISyntaxException exc) {
					exc.printStackTrace();
				}
				super.mouseClicked(e);
			}
		});
	}
	
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
	
	public void initializeTray() {
		if (trayIcon == null) {
	        // obtain only one instance of the SystemTray object
	        SystemTray tray = SystemTray.getSystemTray();
	        
	        // obtain image
			Image image = Toolkit.getDefaultToolkit().createImage(getClass().getClassLoader().getResource("robot.png"));
	
			// create tray icon
	        trayIcon = new TrayIcon(image, "Roboto");
	        
	        // let the system resize the image if needed
	        trayIcon.setImageAutoSize(true);
	        
	        // set tooltip text for the tray icon
	        trayIcon.setToolTip("Roboto");
	        
	        trayIcon.addMouseListener(new MouseAdapter() {
	        	@Override
	        	public void mouseClicked(MouseEvent e) {
	        		if (AppFrame.this.getState() == JFrame.ICONIFIED) {
		        		AppFrame.this.setState(JFrame.NORMAL);
		        		AppFrame.this.toFront();
		        		super.mouseClicked(e);
	        		}
	        		else {
	        			AppFrame.this.setState(JFrame.ICONIFIED);
		        		super.mouseClicked(e);
	        		}
	        	}
			});
	        
	        // add trayicon to system tray
	        try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void displayTray(String title, String message) {
		initializeTray();
        // display message
        trayIcon.displayMessage(title, message, MessageType.INFO);
    }
	
}
