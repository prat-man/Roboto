package tk.pratanumandal.roboto;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class AppFrame extends JFrame {
	
	private static final long serialVersionUID = -1748889400784668366L;
	
	private ScheduledFuture<?> mouseFuture;
	private ScheduledFuture<?> keyboardFuture;
	
	public AppFrame() {
		super("Roboto");
		
		
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(2, 1));
		
		
		JPanel mousePanel = new JPanel();
		mousePanel.setBorder(new TitledBorder("Mouse"));
		
		JButton mouseStartButton = new JButton("Start");
		mouseStartButton.setPreferredSize(new Dimension(100, 25));
		mousePanel.add(mouseStartButton);
		
		JButton mouseStopButton = new JButton("Stop");
		mouseStopButton.setPreferredSize(new Dimension(100, 25));
		mouseStopButton.setEnabled(false);
		mousePanel.add(mouseStopButton);
		
		container.add(mousePanel);
		
		
		JPanel keyboardPanel = new JPanel();
		keyboardPanel.setBorder(new TitledBorder("Keyboard"));
		
		JButton keyboardStartButton = new JButton("Start");
		keyboardStartButton.setPreferredSize(new Dimension(100, 25));
		keyboardPanel.add(keyboardStartButton);
		
		JButton keyboardStopButton = new JButton("Stop");
		keyboardStopButton.setPreferredSize(new Dimension(100, 25));
		keyboardStopButton.setEnabled(false);
		keyboardPanel.add(keyboardStopButton);
		
		container.add(keyboardPanel);
		
		
		this.add(container, BorderLayout.CENTER);
		
		
		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(new EmptyBorder(0, 0, 3, 0));
		
		JLabel infoLabel = new JLabel("<html><span style=\"color: blue;\">Pratanu Mandal & Jerry Jose</span></html>");
		infoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		infoPanel.add(infoLabel);
		
		this.add(infoPanel, BorderLayout.SOUTH);
		
		
		mouseStartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
					
					mouseStartButton.setEnabled(false);
					mouseStopButton.setEnabled(true);
				} catch (AWTException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		mouseStopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mouseFuture.cancel(true);
				
				System.out.println("Mouse Scheduler Stopped");
				
				mouseStartButton.setEnabled(true);
				mouseStopButton.setEnabled(false);
			}
		});
		
		
		keyboardStartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// initialize robot
					Robot robot = new Robot();
					
					// start scheduler to move keyboard every 5 seconds
					ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
					keyboardFuture = exec.scheduleAtFixedRate(() -> {
						// initialize random
						Random random = new Random();
						// initialize random keycode
						int keycode = generateRandomAlphanumericKeyCode();
						// if alphabet, chance based lower case or upper case
						boolean shift = false;
						if (keycode >= 65 && keycode <= 90 && random.nextInt(2) == 0) {
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
					
					keyboardStartButton.setEnabled(false);
					keyboardStopButton.setEnabled(true);
				} catch (AWTException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		keyboardStopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keyboardFuture.cancel(true);
				
				System.out.println("Keyboard Scheduler Stopped");
				
				keyboardStartButton.setEnabled(true);
				keyboardStopButton.setEnabled(false);
			}
		});
		
		
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
	
	public static int generateRandomAlphanumericKeyCode() {
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
	
}
