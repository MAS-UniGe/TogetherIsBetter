import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

public class ChatGUI {
	
	private final int CHAT_HEIGHT = 600;
	private final int CHAT_WIDTH = 490;
	private JFrame frame;
	private JPanel panel;
	private LinkedList<JLabel> labels = new LinkedList<JLabel>();
	private Map<String,String> playerPadding = new LinkedHashMap<String,String>();

	
	public ChatGUI () {
		playerPadding.put("red","           ");
		playerPadding.put("blue","             ");
		playerPadding.put("green","               ");
		playerPadding.put("yellow","                ");
		playerPadding.put("pink","             ");
		playerPadding.put("purple","                ");
		playerPadding.put("brown","                ");
		playerPadding.put("lime","             ");
		playerPadding.put("cyan","             ");
		playerPadding.put("black","              ");
		playerPadding.put("white","               ");
		playerPadding.put("orange","                 ");
		playerPadding.put("","");
	  
		frame = new JFrame();
		panel = new JPanel();
		
		panel.setBorder(BorderFactory.createEmptyBorder(0,0,CHAT_HEIGHT,CHAT_WIDTH));
		panel.setLayout(new GridLayout(0,1));
		panel.setBackground(Color.BLACK);
		frame.add(panel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Among Us - Chat");
		frame.setResizable(false);
		
		frame.pack();
		frame.setVisible(true);
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				frame.getContentPane().removeAll();
				frame.getContentPane().invalidate();
				
				if (labels.size() > 0) {
				int margin = 30;
				for (JLabel label : labels) {
					if (label == null)
						break;
					label.setForeground(Color.WHITE);
					label.setBounds(0,0,480,margin);
					label.setFont(new Font("Verdana",1,12));
					frame.add(label);
					margin += 60;
				}
				}
				frame.add(panel,BorderLayout.CENTER);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				
				frame.pack();
				frame.getContentPane().revalidate();
				frame.setVisible(true);
			}
		}, 0, 1000);
	}
	
	public synchronized void addMessage(String player, String message, boolean padding) {
		synchronized(labels) {
			if (labels.size() >= 20)
				labels.remove(0);
			
			if(padding)
				labels.add(new JLabel(playerPadding.get(player)+message));
			else
				labels.add(new JLabel((player.equals("") ? "" : "["+player+"]: ") + message));
		}
	}
}
