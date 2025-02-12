import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.awt.Color;
import java.awt.Font;

public class AmongUsGUI {
	
	private final int BACKGROUND_WIDTH = 863;
	private final int BACKGROUND_HEIGHT = 500;
	private final int CHARACTER_WIDTH = 26;
	private final int CHARACTER_HEIGHT = 28;
	private final int MARGIN_CHUNK = 27;
	private final String IMAGE_DIRECTORY = "images/";
	private Map<String,ImageIcon> playerIcons = new LinkedHashMap<String,ImageIcon>();
	private Map<String,JLabel> players = new LinkedHashMap<String,JLabel>();
	private Map<String,String> playerPositions = new LinkedHashMap<String,String>();
	private Map<String,String> spaceshipPositions = new LinkedHashMap<String,String>();
	private LinkedList<JLabel> labels = new LinkedList<JLabel>();
	private JLabel background;
	private JLabel crewmateCount;
	private JLabel impostorCount;
	private JLabel tasksCount;
	private JFrame frame;
	private JPanel panel;
	private int crewmates;
	private int impostors;
	private int tasks;
	
	public AmongUsGUI (int crewmates, int impostors, int tasks) {
		this.crewmates = crewmates;
		this.impostors = impostors;
		this.tasks = tasks;
		frame = new JFrame();
		panel = new JPanel();
		//loading the icon of each player
		playerIcons.put("black",resize(new ImageIcon(IMAGE_DIRECTORY+"black.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("blue",resize(new ImageIcon(IMAGE_DIRECTORY+"blue.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("brown",resize(new ImageIcon(IMAGE_DIRECTORY+"brown.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("cyan",resize(new ImageIcon(IMAGE_DIRECTORY+"cyan.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("green",resize(new ImageIcon(IMAGE_DIRECTORY+"green.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("lime",resize(new ImageIcon(IMAGE_DIRECTORY+"lime.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("orange",resize(new ImageIcon(IMAGE_DIRECTORY+"orange.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("pink",resize(new ImageIcon(IMAGE_DIRECTORY+"pink.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("purple",resize(new ImageIcon(IMAGE_DIRECTORY+"purple.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("red",resize(new ImageIcon(IMAGE_DIRECTORY+"red.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("white",resize(new ImageIcon(IMAGE_DIRECTORY+"white.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		playerIcons.put("yellow",resize(new ImageIcon(IMAGE_DIRECTORY+"yellow.png"),CHARACTER_WIDTH,CHARACTER_HEIGHT));
		//loading the positions for each section
		spaceshipPositions.put("1_350_300","available");	//room 1 - upper engine
		spaceshipPositions.put("1_530_390","available");	//room 3 - security
		spaceshipPositions.put("1_270_500","available");	//room 2 - reactor
		spaceshipPositions.put("1_360_170","available");	//room 1 - upper engine
		spaceshipPositions.put("1_540_450","available");	//room 3 - security
		spaceshipPositions.put("1_430_290","available");	//room 1 - upper engine
		spaceshipPositions.put("1_220_370","available");	//room 2 - reactor
		spaceshipPositions.put("1_530_510","available");	//room 3 - security
		spaceshipPositions.put("1_220_550","available");	//room 2 - reactor
		spaceshipPositions.put("1_430_180","available");	//room 1 - upper engine
		spaceshipPositions.put("1_270_430","available");	//room 2 - reactor
		spaceshipPositions.put("1_480_470","available");	//room 3 - security
		spaceshipPositions.put("2_740_450","available");	//room 4 - medbay
		spaceshipPositions.put("2_950_170","available");	//room 5 - cafeteria
		spaceshipPositions.put("2_680_430","available");	//room 4 - medbay
		spaceshipPositions.put("2_1000_100","available");	//room 5 - cafeteria
		spaceshipPositions.put("2_970_310","available");	//room 5 - cafeteria
		spaceshipPositions.put("2_850_250","available");	//room 5 - cafeteria
		spaceshipPositions.put("2_680_350","available");	//room 4 - medbay
		spaceshipPositions.put("2_1100_210","available");	//room 5 - cafeteria
		spaceshipPositions.put("2_1150_300","available");	//room 5 - cafeteria
		spaceshipPositions.put("2_1050_400","available");	//room 5 - cafeteria
		spaceshipPositions.put("2_950_400","available");	//room 5 - cafeteria
		spaceshipPositions.put("2_690_230","available");	//room 4 - medbay
		spaceshipPositions.put("3_1350_250","available");	//room 6 - weapons
		spaceshipPositions.put("3_1210_400","available");	//room 7 - O2
		spaceshipPositions.put("3_1590_480","available");	//room 8 - navigation
		spaceshipPositions.put("3_1590_400","available");	//room 8 - navigation
		spaceshipPositions.put("3_1320_160","available");	//room 6 - weapons
		spaceshipPositions.put("3_1640_450","available");	//room 8 - navigation
		spaceshipPositions.put("3_1280_400","available");	//tunnel among rooms 6-7-8
		spaceshipPositions.put("3_1500_440","available");	//tunnel among rooms 6-7-8
		spaceshipPositions.put("3_1350_400","available");	//tunnel among rooms 6-7-8
		spaceshipPositions.put("3_1430_400","available");	//tunnel among rooms 6-7-8
		spaceshipPositions.put("3_1430_500","available");	//tunnel among rooms 6-7-8
		spaceshipPositions.put("3_1340_310","available");	//tunnel among rooms 6-7-8
		spaceshipPositions.put("4_1100_840","available");	//rrom 10 - communications
		spaceshipPositions.put("4_1340_700","available");	//rrom 9 - shields
		spaceshipPositions.put("4_1210_620","available");	//rrom 11 - admin
		spaceshipPositions.put("4_1100_550","available");	//rrom 11 - admin
		spaceshipPositions.put("4_1200_710","available");	//tunnel among rooms 9-10
		spaceshipPositions.put("4_1170_860","available");	//rrom 10 - communications
		spaceshipPositions.put("4_1340_770","available");	//rrom 9 - shields
		spaceshipPositions.put("4_1100_620","available");	//rrom 11 - admin
		spaceshipPositions.put("4_1200_800","available");	//rrom 10 - communications
		spaceshipPositions.put("4_1220_540","available");	//rrom 11 - admin
		spaceshipPositions.put("4_1100_710","available");	//tunnel among rooms 9-10
		spaceshipPositions.put("4_1340_600","available");	//tunnel above room 9
		spaceshipPositions.put("5_900_650","available");	//room 12 - storage
		spaceshipPositions.put("5_700_680","available");	//room 13 - electrical
		spaceshipPositions.put("5_430_750","available");	//room 14 - lower engine
		spaceshipPositions.put("5_900_850","available");	//room 12 - storage
		spaceshipPositions.put("5_750_580","available");	//room 13 - electrical
		spaceshipPositions.put("5_1000_840","available");	//room 12 - storage
		spaceshipPositions.put("5_370_750","available");	//room 14 - lower engine
		spaceshipPositions.put("5_1000_620","available");	//room 12 - storage
		spaceshipPositions.put("5_680_570","available");	//room 13 - electrical
		spaceshipPositions.put("5_430_620","available");	//room 14 - lower engine
		spaceshipPositions.put("5_750_790","available");	//tunnel among rooms 12-13-14
		spaceshipPositions.put("5_620_790","available");	//tunnel among rooms 12-13-14
		//setting background and other global settings
		background = new JLabel("",resize(new ImageIcon(IMAGE_DIRECTORY+"spaceship.png"),BACKGROUND_WIDTH,BACKGROUND_HEIGHT),JLabel.CENTER);
		background.setBounds(0,0,BACKGROUND_WIDTH,BACKGROUND_HEIGHT);
		panel.setBorder(BorderFactory.createEmptyBorder(0,0,BACKGROUND_HEIGHT,BACKGROUND_WIDTH));
		panel.setLayout(new GridLayout(0,1));
		frame.add(panel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Among Us");
		frame.setResizable(false);
		
		crewmateCount = new JLabel("crewmates: "+crewmates);
		crewmateCount.setForeground(Color.WHITE);
		crewmateCount.setBounds(770,0,500,MARGIN_CHUNK);
		crewmateCount.setFont(new Font("Verdana",1,12));
		frame.add(crewmateCount);
		impostorCount = new JLabel("impostors:   " +impostors);
		impostorCount.setForeground(Color.WHITE);
		impostorCount.setBounds(770,0,500,MARGIN_CHUNK+MARGIN_CHUNK);
		impostorCount.setFont(new Font("Verdana",1,12));
		frame.add(impostorCount);
		tasksCount = new JLabel("tasks: "+tasks);
		tasksCount.setForeground(Color.WHITE);
		tasksCount.setBounds(800,0,500,980);
		tasksCount.setFont(new Font("Verdana",1,12));
		frame.add(tasksCount);
		
		frame.add(background);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void addPlayer(String color, String section) {
		//retrieving the icon for the player
		JLabel player = new JLabel("",playerIcons.get(color),JLabel.CENTER);
		//retrieving an available slot for the player
		String position = "";
		for (Map.Entry<String,String> entry : spaceshipPositions.entrySet()) {
			if (entry.getKey().startsWith(section) && entry.getValue().equals("available")) {
				position = entry.getKey();
				break;
			}
		}
		if (position.equals("") || position == null)
			return;
		playerPositions.put(color,position);
		spaceshipPositions.replace(position,"unavailable");
		//adding the player
		String[] positionVariables = position.split("_");
		player.setBounds(0,0,Integer.parseInt(positionVariables[1]),Integer.parseInt(positionVariables[2]));
		players.put(color,player);
		addLabel(color+" joined the game");
		refresh();
	}
	
	public void removePlayer(String color,String cause,String playerType) {
		if (!playerPositions.containsKey(color) || !players.containsKey(color))
			return;
		spaceshipPositions.replace(playerPositions.get(color),"available");
		playerPositions.remove(color);
		players.remove(color);
		addLabel(color+" was "+cause);
		if (playerType.equals("impostor")) {
			impostors--;
			impostorCount.setText("impostors:   "+impostors);
		}
		else {
			crewmates--;
			crewmateCount.setText("crewmates: "+crewmates);
		}
		refresh();
	}
	
	public void movePlayer(String color, String section, String newSection) {
		//retrieving the informations about the current position of the player
		JLabel player = players.get(color);
		String position = playerPositions.get(color);
		if (position == null || player == null)
			return;
		spaceshipPositions.replace(position,"available");
		//retrieving an available slot for the player in the new section they will move to
		String newPosition = "";
		for (Map.Entry<String,String> entry : spaceshipPositions.entrySet()) {
			if (entry.getKey().startsWith(newSection) && entry.getValue().equals("available")) {
				newPosition = entry.getKey();
				break;
			}
		}
		if (newPosition.equals("") || newPosition == null)
			return;
		playerPositions.replace(color,newPosition);
		spaceshipPositions.replace(newPosition,"unavailable");
		//moving the player
		String[] positionVariables = newPosition.split("_");
		player.setBounds(0,0,Integer.parseInt(positionVariables[1]),Integer.parseInt(positionVariables[2]));
		players.replace(color,player);
		refresh();
	}
	
	private void refresh() {
		frame.getContentPane().removeAll();
		frame.getContentPane().invalidate();
		
		frame.add(panel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		for (Map.Entry<String,JLabel> entry : players.entrySet()) {
			frame.add(entry.getValue());
		}
		
		int margin = MARGIN_CHUNK;
		for (JLabel label : labels) {
			label.setForeground(Color.WHITE);
			label.setBounds(0,0,500,margin);
			label.setFont(new Font("Verdana",1,12));
			frame.add(label);
			margin += MARGIN_CHUNK;
		}
		
		frame.add(crewmateCount);
		frame.add(impostorCount);
		frame.add(tasksCount);
		frame.add(background);
		
		frame.pack();
		frame.getContentPane().revalidate();
		frame.setVisible(true);
	}
	
	public synchronized void updateTasks() {
		if (tasks == 0)
			return;
		tasks--;
		tasksCount.setText("tasks: "+tasks);
	}
	
	private void addLabel(String label) {
		if (labels.size() >= 5)
			labels.remove(0);
		labels.add(new JLabel(label));
	}
	
	private ImageIcon resize(ImageIcon img, int newWidth, int newHeight) {
		Image image = img.getImage();
		Image newImage = image.getScaledInstance(newWidth,newHeight,Image.SCALE_DEFAULT);
		return new ImageIcon(newImage);
	}
}
