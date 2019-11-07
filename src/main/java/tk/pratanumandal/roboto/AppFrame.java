package tk.pratanumandal.roboto;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class AppFrame extends JFrame {
	
	private static final long serialVersionUID = -1748889400784668366L;
	
	private ScheduledFuture<?> mouseFuture;
	private ScheduledFuture<?> keyboardFuture;
	
	private TrayIcon trayIcon;
	private Point mousePoint;
	
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
		JPanel outerMousePanel = new JPanel();
		outerMousePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		outerMousePanel.setBorder(new TitledBorder("<html><span style=\"font-size: 1.1em; font-weight: bold;\">Mouse</span></html>"));
		
		JPanel mousePanel = new JPanel();
		mousePanel.setLayout(new BoxLayout(mousePanel, BoxLayout.Y_AXIS));
		outerMousePanel.add(mousePanel);
		
		Box mouseBox1 = Box.createHorizontalBox();
		
		JButton mouseStartButton = new JButton("Start");
		mouseStartButton.setMargin(new Insets(3, 35, 3, 35));
		mouseStartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mouseBox1.add(mouseStartButton);
		
		mouseBox1.add(Box.createRigidArea(new Dimension(5, 5)));
		
		JButton mouseStopButton = new JButton("Stop");
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
		mouseBox2.add(mouseSpinner);
		
		mouseBox2.add(Box.createRigidArea(new Dimension(5, 5)));
		
		JLabel postMouseLabel = new JLabel("<html>second &nbsp;</html>");
		mouseBox2.add(postMouseLabel);
		
		mouseBox2.add(Box.createHorizontalGlue());
		
		mousePanel.add(mouseBox2);
		
		container.add(outerMousePanel);
		
		// initialize keyboard panel
		JPanel outerKeyboardPanel = new JPanel();
		outerKeyboardPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		outerKeyboardPanel.setBorder(new TitledBorder("<html><span style=\"font-size: 1.1em; font-weight: bold;\">Keyboard</span></html>"));
		
		JPanel keyboardPanel = new JPanel();
		keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS));
		outerKeyboardPanel.add(keyboardPanel);
		
		Box keyboardBox1 = Box.createHorizontalBox();
		
		JButton keyboardStartButton = new JButton("Start");
		keyboardStartButton.setMargin(new Insets(3, 35, 3, 35));
		keyboardStartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		keyboardBox1.add(keyboardStartButton);
		
		keyboardBox1.add(Box.createRigidArea(new Dimension(5, 5)));
		
		JButton keyboardStopButton = new JButton("Stop");
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
		keyboardBox2.add(keyboardSpinner);
		
		keyboardBox2.add(Box.createRigidArea(new Dimension(5, 5)));
		
		JLabel postKeyboardLabel = new JLabel("<html>second &nbsp;</html>");
		keyboardBox2.add(postKeyboardLabel);
		
		keyboardBox2.add(Box.createHorizontalGlue());
		
		keyboardPanel.add(keyboardBox2);
		
		container.add(outerKeyboardPanel);
		
		
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
				
				mouseStartButton.setEnabled(false);
				mouseStopButton.setEnabled(true);
			}
		});
		
		// stop action for mouse
		mouseStopButton.addActionListener((event) -> {
			if (stopMouse()) {
				System.out.println("Mouse Scheduler Stopped");
				displayTray("Roboto", "Mouse Scheduler Stopped");
				
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
			if (value == 1) {
				postMouseLabel.setText("second");
			} else {
				postMouseLabel.setText("seconds");
			}
		});
		
		// start action for keyboard
		keyboardStartButton.addActionListener((event) -> {
			int value = (int) keyboardSpinner.getValue();
			
			if (startKeyboard(value)) {
				System.out.println("Keyboard Scheduler Started");
				displayTray("Roboto", "Keyboard Scheduler Started");
				
				keyboardStartButton.setEnabled(false);
				keyboardStopButton.setEnabled(true);
			}
		});
		
		// stop action for keyboard
		keyboardStopButton.addActionListener((event) -> {
			if (stopKeyboard()) {
				System.out.println("Keyboard Scheduler Stopped");
				displayTray("Roboto", "Keyboard Scheduler Stopped");
				
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
			if (value == 1) {
				postKeyboardLabel.setText("second");
			} else {
				postKeyboardLabel.setText("seconds");
			}
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
	
	public boolean startMouse(int value) {
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
		if (mouseFuture == null || mouseFuture.isCancelled()) {
			return false;
		}
		mouseFuture.cancel(true);
		mouseFuture = null;
		return true;
	}
	
	public boolean startKeyboard(int value) {
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
			}, value, value, TimeUnit.SECONDS);
			
			return true;
		}
		catch (AWTException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean stopKeyboard() {
		if (keyboardFuture == null || keyboardFuture.isCancelled()) {
			return false;
		}
		keyboardFuture.cancel(true);
		keyboardFuture = null;
		return true;
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
