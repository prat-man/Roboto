package tk.pratanumandal.roboto;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class AppFrame extends JFrame {
	
	private static final long serialVersionUID = -1748889400784668366L;
	
	private ScheduledFuture<?> mouseFuture;
	private ScheduledFuture<?> keyboardFuture;
	private ScheduledFuture<?> mouseStopFuture;
	private ScheduledFuture<?> keyboardStopFuture;
	private ScheduledFuture<?> shutdownFuture;
	
	private TrayIcon trayIcon;
	private Point mousePoint;
	
	private JButton mouseStartButton;
	private JButton mouseStopButton;
	private JComboBox<String> mouseStopCombo;
	
	private JButton keyboardStartButton;
	private JButton keyboardStopButton;
	private JComboBox<String> keyboardStopCombo;
	
	private JComboBox<String> shutdownCombo;
	
	private String currentMouseStop;
	private String currentKeyboardStop;
	
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
		container.setLayout(new GridLayout2(3, 1, 0, 10));
		Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		container.setBorder(padding);
		
		this.add(container, BorderLayout.CENTER);
		
		
		// initialize mouse panel
		JPanel outerMousePanel = new JPanel();
		outerMousePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		outerMousePanel.setBorder(new TitledBorder("<html><span style=\"font-size: 1.1em; font-weight: bold;\">Mouse</span></html>"));
		
		JPanel mousePanel = new JPanel();
		mousePanel.setLayout(new BoxLayout(mousePanel, BoxLayout.Y_AXIS));
		outerMousePanel.add(mousePanel);
		
		Box mouseBox1 = Box.createHorizontalBox();
		
		this.mouseStartButton = new JButton("Start");
		mouseStartButton.setMargin(new Insets(3, 35, 3, 35));
		mouseStartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mouseBox1.add(mouseStartButton);
		
		mouseBox1.add(Box.createRigidArea(new Dimension(5, 5)));
		
		this.mouseStopButton = new JButton("Stop");
		mouseStopButton.setMargin(new Insets(3, 35, 3, 35));
		mouseStopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mouseStopButton.setEnabled(false);
		mouseBox1.add(mouseStopButton);
		
		mouseBox1.add(Box.createHorizontalGlue());
		
		mousePanel.add(mouseBox1);
		
		mousePanel.add(Box.createRigidArea(new Dimension(10, 10)));
		
		Box mouseBox2 = Box.createHorizontalBox();
		
		mouseBox2.add(Box.createRigidArea(new Dimension(2, 2)));
		
		JLabel preMouseLabel = new JLabel("Move mouse at intervals of every");
		mouseBox2.add(preMouseLabel);
		
		mouseBox2.add(Box.createRigidArea(new Dimension(5, 5)));
		
		SpinnerNumberModel mouseSpinnerModel = new SpinnerNumberModel(1, 1, 60, 1);
		JSpinner mouseSpinner = new JSpinner(mouseSpinnerModel);
		((JSpinner.DefaultEditor) mouseSpinner.getEditor()).getTextField().setEditable(false);
		((JSpinner.DefaultEditor) mouseSpinner.getEditor()).getTextField().setBackground(new JTextField().getBackground());
		mouseBox2.add(mouseSpinner);
		
		mouseBox2.add(Box.createRigidArea(new Dimension(5, 5)));
		
		JLabel postMouseLabel = new JLabel("second(s)");
		mouseBox2.add(postMouseLabel);
		
		mouseBox2.add(Box.createRigidArea(new Dimension(2, 2)));
		
		mouseBox2.add(Box.createHorizontalGlue());
		
		mousePanel.add(mouseBox2);
		
		mousePanel.add(Box.createRigidArea(new Dimension(10, 10)));
		
		Box mouseBox3 = Box.createHorizontalBox();
		
		JLabel preMouseStopLabel = new JLabel("Automatically stop after");
		mouseBox3.add(preMouseStopLabel);
		
		mouseBox3.add(Box.createRigidArea(new Dimension(5, 5)));
		
		this.mouseStopCombo = new JComboBox<>();
		mouseStopCombo.addItem("Never");
		mouseStopCombo.addItem("15 minutes");
		mouseStopCombo.addItem("30 minutes");
		mouseStopCombo.addItem("1 hour");
		mouseStopCombo.addItem("3 hours");
		mouseStopCombo.addItem("5 hours");
		mouseBox3.add(mouseStopCombo);
		
		mouseBox3.add(Box.createHorizontalGlue());
		
		mousePanel.add(mouseBox3);
		
		container.add(outerMousePanel);
		
		
		// initialize keyboard panel
		JPanel outerKeyboardPanel = new JPanel();
		outerKeyboardPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		outerKeyboardPanel.setBorder(new TitledBorder("<html><span style=\"font-size: 1.1em; font-weight: bold;\">Keyboard</span></html>"));
		
		JPanel keyboardPanel = new JPanel();
		keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS));
		outerKeyboardPanel.add(keyboardPanel);
		
		Box keyboardBox1 = Box.createHorizontalBox();
		
		this.keyboardStartButton = new JButton("Start");
		keyboardStartButton.setMargin(new Insets(3, 35, 3, 35));
		keyboardStartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		keyboardBox1.add(keyboardStartButton);
		
		keyboardBox1.add(Box.createRigidArea(new Dimension(5, 5)));
		
		this.keyboardStopButton = new JButton("Stop");
		keyboardStopButton.setMargin(new Insets(3, 35, 3, 35));
		keyboardStopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		keyboardStopButton.setEnabled(false);
		keyboardBox1.add(keyboardStopButton);
		
		keyboardBox1.add(Box.createHorizontalGlue());
		
		keyboardPanel.add(keyboardBox1);
		
		keyboardPanel.add(Box.createRigidArea(new Dimension(10, 10)));
		
		Box keyboardBox2 = Box.createHorizontalBox();
		
		keyboardBox2.add(Box.createRigidArea(new Dimension(2, 2)));
		
		JLabel preKeyboardLabel = new JLabel("Press key at intervals of every");
		keyboardBox2.add(preKeyboardLabel);
		
		keyboardBox2.add(Box.createRigidArea(new Dimension(5, 5)));
		
		SpinnerNumberModel keyboardSpinnerModel = new SpinnerNumberModel(1, 1, 60, 1);
		JSpinner keyboardSpinner = new JSpinner(keyboardSpinnerModel);
		((JSpinner.DefaultEditor) keyboardSpinner.getEditor()).getTextField().setEditable(false);
		((JSpinner.DefaultEditor) keyboardSpinner.getEditor()).getTextField().setBackground(new JTextField().getBackground());
		keyboardBox2.add(keyboardSpinner);
		
		keyboardBox2.add(Box.createRigidArea(new Dimension(5, 5)));
		
		JLabel postKeyboardLabel = new JLabel("second(s)");
		keyboardBox2.add(postKeyboardLabel);
		
		keyboardBox2.add(Box.createRigidArea(new Dimension(2, 2)));
		
		keyboardBox2.add(Box.createHorizontalGlue());
		
		keyboardPanel.add(keyboardBox2);
		
		keyboardPanel.add(Box.createRigidArea(new Dimension(10, 10)));
		
		Box keyboardBox3 = Box.createHorizontalBox();
		
		JLabel preKeyboardStopLabel = new JLabel("Automatically stop after");
		keyboardBox3.add(preKeyboardStopLabel);
		
		keyboardBox3.add(Box.createRigidArea(new Dimension(5, 5)));
		
		this.keyboardStopCombo = new JComboBox<>();
		keyboardStopCombo.addItem("Never");
		keyboardStopCombo.addItem("15 minutes");
		keyboardStopCombo.addItem("30 minutes");
		keyboardStopCombo.addItem("1 hour");
		keyboardStopCombo.addItem("3 hours");
		keyboardStopCombo.addItem("5 hours");
		keyboardBox3.add(keyboardStopCombo);
		
		keyboardBox3.add(Box.createHorizontalGlue());
		
		keyboardPanel.add(keyboardBox3);
		
		container.add(outerKeyboardPanel);
		
		
		// initialize control panel
		JPanel outerControlPanel = new JPanel();
		outerControlPanel.setBackground(Color.CYAN);
		outerControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		outerControlPanel.setBorder(new TitledBorder("<html><span style=\"font-size: 1.1em; font-weight: bold;\">Control</span></html>"));
		
		JPanel controlPanel = new JPanel2(outerControlPanel);
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		outerControlPanel.add(controlPanel);
		
		Box controlBox1 = Box.createHorizontalBox();
		
		JLabel preShutdownLabel = new JLabel("Auto shutdown system after");
		controlBox1.add(preShutdownLabel);
		
		controlBox1.add(Box.createRigidArea(new Dimension(5, 5)));
		
		this.shutdownCombo = new JComboBox<>();
		shutdownCombo.addItem("Never");
		shutdownCombo.addItem("15 minutes");
		shutdownCombo.addItem("30 minutes");
		shutdownCombo.addItem("1 hour");
		shutdownCombo.addItem("3 hours");
		shutdownCombo.addItem("5 hours");
		controlBox1.add(shutdownCombo);
		
		controlBox1.add(Box.createHorizontalGlue());
		
		controlPanel.add(controlBox1);
		
		container.add(outerControlPanel);
		
		
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
			int value = (int) mouseSpinner.getValue();
			
			if (startMouse(value)) {
				System.out.println("Mouse Scheduler Started");
				displayTray("Roboto", "Mouse Scheduler Started");
				
				if (autoStopMouse()) {
					System.out.println("Mouse Automatic Stop Started");
				} else {
					System.out.println("Failed to start Mouse Automatic Stop");
				}
				
				mouseStartButton.setEnabled(false);
				mouseStopButton.setEnabled(true);
			}
		});
		
		// stop action for mouse
		mouseStopButton.addActionListener((event) -> {
			if (stopMouse()) {
				System.out.println("Mouse Scheduler Stopped");
				displayTray("Roboto", "Mouse Scheduler Stopped");
				
				if (cancelAutoStopMouse()) {
					System.out.println("Mouse Automatic Stop Cancelled");
				} else {
					System.out.println("Failed to cancel Mouse Automatic Stop");
				}
				
				mouseStartButton.setEnabled(true);
				mouseStopButton.setEnabled(false);
			}
		});
		
		// handle mouse spinner value change
		mouseSpinner.addChangeListener((event) -> {
			int value = (int) mouseSpinner.getValue();
			if (stopMouse()) {
				startMouse(value);
			}
		});
		
		mouseStopCombo.addActionListener((event) -> {
			if (autoStopMouse()) {
				System.out.println("Mouse Automatic Stop Changed");
			} else if (mouseFuture != null && !mouseFuture.isCancelled() && !mouseFuture.isDone()) {
				System.out.println("Mouse Automatic Stop Cancelled");
			} else {
				System.out.println("Mouse Automatic Stop Changed");
			}
		});
		
		// start action for keyboard
		keyboardStartButton.addActionListener((event) -> {
			int value = (int) keyboardSpinner.getValue();
			
			if (startKeyboard(value)) {
				System.out.println("Keyboard Scheduler Started");
				displayTray("Roboto", "Keyboard Scheduler Started");
				
				if (autoStopKeyboard()) {
					System.out.println("Keyboard Automatic Stop Started");
				} else {
					System.out.println("Failed to start Keyboard Automatic Stop");
				}
				
				keyboardStartButton.setEnabled(false);
				keyboardStopButton.setEnabled(true);
			}
		});
		
		// stop action for keyboard
		keyboardStopButton.addActionListener((event) -> {
			if (stopKeyboard()) {
				System.out.println("Keyboard Scheduler Stopped");
				displayTray("Roboto", "Keyboard Scheduler Stopped");
				
				if (cancelAutoStopKeyboard()) {
					System.out.println("Keyboard Automatic Stop Cancelled");
				} else {
					System.out.println("Failed to cancel Keyboard Automatic Stop");
				}
				
				keyboardStartButton.setEnabled(true);
				keyboardStopButton.setEnabled(false);
			}
		});
		
		// handle keyboard spinner value change
		keyboardSpinner.addChangeListener((event) -> {
			int value = (int) keyboardSpinner.getValue();
			if (stopKeyboard()) {
				startKeyboard(value);
			}
		});
		
		keyboardStopCombo.addActionListener((event) -> {
			if (autoStopKeyboard()) {
				System.out.println("Keyboard Automatic Stop Changed");
			} else if (keyboardFuture != null && !keyboardFuture.isCancelled() && !keyboardFuture.isDone()) {
				System.out.println("Keyboard Automatic Stop Cancelled");
			} else {
				System.out.println("Keyboard Automatic Stop Changed");
			}
		});
		
		// handle shutdown scheduler
		shutdownCombo.addItemListener((event) -> {
			autoShutdown();
		});
		
		// information label action
		infoLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					AppUtils.openWebpage(new URI("https://github.com/prat-man/Roboto"));
				} catch (URISyntaxException exc) {
					exc.printStackTrace();
				}
				super.mouseClicked(e);
			}
		});
	}
	
	public boolean startMouse(int value) {
		try {
			// initialize robot
			Robot robot = new Robot();
			
			// initialize screen size
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int MAX_X = (int) screenSize.getWidth();
			int MAX_Y = (int) screenSize.getHeight();
			
			// start scheduler to move mouse at specified intervals
			ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
			mouseFuture = exec.scheduleAtFixedRate(() -> {
				Point point = MouseInfo.getPointerInfo().getLocation();
				if (mousePoint == null || point.equals(mousePoint)) {
					// initialize random
					Random random = new Random();
					// generate new mouse point
					mousePoint = new Point(random.nextInt(MAX_X), random.nextInt(MAX_Y));
					// move mouse
					robot.mouseMove((int) mousePoint.getX(), (int) mousePoint.getY());
				}
				else {
					mousePoint = point;
				}
			}, value, value, TimeUnit.SECONDS);
			
			return true;
		}
		catch (AWTException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean stopMouse() {
		if (mouseFuture == null || mouseFuture.isCancelled() || mouseFuture.isDone()) {
			return false;
		}
		mouseFuture.cancel(true);
		mouseFuture = null;
		return true;
	}
	
	public boolean autoStopMouse() {
		if (mouseFuture != null && !mouseFuture.isCancelled() && !mouseFuture.isDone()) {
			String item = (String) mouseStopCombo.getSelectedItem();
			if (item.equals(currentMouseStop)) {
				return true;
			} else {
				currentMouseStop = item;
			}
			int time;
			switch (item) {
				default:
				case "Never":		time = 0; 		break;
				case "15 minutes":	time = 15; 		break;
				case "30 minutes":	time = 30; 		break;
				case "1 hour":		time = 60; 		break;
				case "3 hours":		time = 3 * 60; 	break;
				case "5 hours":		time = 5 * 60; 	break;
			}
			if (time == 0) {
				if (mouseStopFuture != null && !mouseStopFuture.isCancelled() && !mouseStopFuture.isDone()) {
					mouseStopFuture.cancel(true);
					mouseStopFuture = null;
					return false;
				}
			}
			else {
				// start scheduler to stop movements after specified time
				ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
				mouseStopFuture = exec.schedule(() -> {
					if (stopMouse()) {
						System.out.println("Mouse Scheduler Stopped");
						displayTray("Roboto", "Mouse Scheduler Stopped");
						
						mouseStartButton.setEnabled(true);
						mouseStopButton.setEnabled(false);
					}
				}, time, TimeUnit.MINUTES);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean cancelAutoStopMouse() {
		if (mouseStopFuture == null || mouseStopFuture.isCancelled() || mouseStopFuture.isDone()) {
			return false;
		}
		mouseStopFuture.cancel(true);
		mouseStopFuture = null;
		return true;
	}
	
	public boolean startKeyboard(int value) {
		try {
			// initialize robot
			Robot robot = new Robot();
			
			// start scheduler to press keyboard keys at specified intervals
			ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
			keyboardFuture = exec.scheduleAtFixedRate(() -> {
				// initialize random
				Random random = new Random();
				// initialize random keycode
				int keycode = AppUtils.generateRandomKeyCode();
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
			}, value, value, TimeUnit.SECONDS);
			
			return true;
		}
		catch (AWTException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean stopKeyboard() {
		if (keyboardFuture == null || keyboardFuture.isCancelled() || keyboardFuture.isDone()) {
			return false;
		}
		keyboardFuture.cancel(true);
		keyboardFuture = null;
		return true;
	}
	
	public boolean autoStopKeyboard() {
		if (keyboardFuture != null && !keyboardFuture.isCancelled() && !keyboardFuture.isDone()) {
			String item = (String) keyboardStopCombo.getSelectedItem();
			if (item.equals(currentKeyboardStop)) {
				return true;
			} else {
				currentKeyboardStop = item;
			}
			int time;
			switch (item) {
				default:
				case "Never":		time = 0; 		break;
				case "15 minutes":	time = 15; 		break;
				case "30 minutes":	time = 30; 		break;
				case "1 hour":		time = 60; 		break;
				case "3 hours":		time = 3 * 60; 	break;
				case "5 hours":		time = 5 * 60; 	break;
			}
			if (time == 0) {
				if (keyboardStopFuture != null && !keyboardStopFuture.isCancelled() && !keyboardStopFuture.isDone()) {
					keyboardStopFuture.cancel(true);
					keyboardStopFuture = null;
					return false;
				}
			}
			else {
				// start scheduler to stop movements after specified time
				ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
				keyboardStopFuture = exec.schedule(() -> {
					if (stopKeyboard()) {
						System.out.println("Keyboard Scheduler Stopped");
						displayTray("Roboto", "Keyboard Scheduler Stopped");
						
						keyboardStartButton.setEnabled(true);
						keyboardStopButton.setEnabled(false);
					}
				}, time, TimeUnit.MINUTES);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean cancelAutoStopKeyboard() {
		if (keyboardStopFuture == null || keyboardStopFuture.isCancelled() || keyboardStopFuture.isDone()) {
			return false;
		}
		keyboardStopFuture.cancel(true);
		keyboardStopFuture = null;
		return true;
	}
	
	public void autoShutdown() {
		String item = (String) shutdownCombo.getSelectedItem();
		int time;
		switch (item) {
			default:
			case "Never":		time = 0; 		break;
			case "15 minutes":	time = 15; 		break;
			case "30 minutes":	time = 30; 		break;
			case "1 hour":		time = 60; 		break;
			case "3 hours":		time = 3 * 60; 	break;
			case "5 hours":		time = 5 * 60; 	break;
		}
		if (time == 0) {
			if (shutdownFuture != null && !shutdownFuture.isCancelled() && !shutdownFuture.isDone()) {
				shutdownFuture.cancel(true);
				shutdownFuture = null;
				displayTray("Roboto", "Automatic Shutdown Cancelled");
			}
		}
		else {
			// start scheduler to stop movements after specified time
			ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
			shutdownFuture = exec.schedule(() -> {
				try {
					// allow 2 minutes of wait time
					AppUtils.shutdown(120);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}, time, TimeUnit.MINUTES);
			displayTray("Roboto", "Automatic Shutdown Scheduled");
		}
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
