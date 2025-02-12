import jason.environment.Environment;
import jason.asSyntax.*;
import java.util.logging.*;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class SpaceshipEnv extends Environment {
	
	private Logger logger = Logger.getLogger("spaceshipEnv.mas2j."+SpaceshipEnv.class.getName());
	//map agent name -> color
	private ConcurrentHashMap<String, String> playerColor = new ConcurrentHashMap<String, String>();
	//map agent name -> current room
	private ConcurrentHashMap<String, String> playerRoom = new ConcurrentHashMap<String, String>();
	//map room -> number of machinery to fix in that room
	private ConcurrentHashMap<String, Integer> roomSituation = new ConcurrentHashMap<String, Integer>();
	//list containing strings in the form player1_player2, meaning that player1 trusts player2
	List<String> trustList = Collections.synchronizedList(new ArrayList<String>());
	//list containing strings in the form player1_player2, meaning that player1 untrusts player2
	List<String> untrustList = Collections.synchronizedList(new ArrayList<String>());
	//counters
	private int crewmateBDICount = 0;
	private int crewmateRLCount = 0;
	private int impostorBDICount = 0;
	private int impostorRLCount = 0;
	private int machineryCount = 0;
	private int count = 0;
	//status of the game
	private boolean finished = false;
	//stats file
	private static final File file = new File("stats.csv");
	//GUI
	private AmongUsGUI gui;
	private ChatGUI chat;
	
	//init function: initial setup of the environment
	@Override
	public void init(String[] args) {
		
		Config config = new Config();
		
		//reading the metrics of the game from the configuration file
		try {
			YamlReader reader = new YamlReader(new FileReader(new File(args[0])));
			Map<String, Object> map = (Map<String, Object>) reader.read();
			
			config.setPlayerList((List<String>) map.get("players"));
			config.setSkillMap((Map<String,String>) map.get("crewmate_skill"));
			config.setBehaviorMap((Map<String,String>) map.get("impostor_behavior"));
			config.setColorList((List<String>) map.get("colors"));
			config.setColorMap((Map<String,String>) map.get("player_color"));
			config.setRoomsMap((Map<String,String>) map.get("rooms"));
			config.setInitialRoomMap((Map<String,String>) map.get("initial_room"));
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (YamlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//setting the initial situation of each room
		for (Map.Entry<String,String> entry : config.getRoomsMap().entrySet()) {
			roomSituation.put(entry.getKey(), Integer.parseInt(entry.getValue()));
			machineryCount = machineryCount + Integer.parseInt(entry.getValue());
		}
		
		//setting the level of skill of each crewmate
		for (Map.Entry<String,String> entry : config.getSkillMap().entrySet()) {
			addPercept("gameManager",Literal.parseLiteral("assignLevelOfSkill("+entry.getKey()+","+entry.getValue()+")"));
			if(entry.getKey().startsWith("crewmate_BDI"))
				crewmateBDICount++;
			if(entry.getKey().startsWith("crewmate_RL"))
				crewmateRLCount++;
		}
		
		//setting the behavior of each impostor
		for (Map.Entry<String,String> entry : config.getBehaviorMap().entrySet()) {
			addPercept("gameManager",Literal.parseLiteral("assignBehavior("+entry.getKey()+","+entry.getValue()+")"));
			if(entry.getKey().startsWith("impostor_BDI"))
				impostorBDICount++;
			if(entry.getKey().startsWith("impostor_RL"))
				impostorRLCount++;
		}
		
		logger.info("There are "+machineryCount+" machines to repair and "+(impostorBDICount+impostorRLCount)+" impostors on board");

		//launching the GUI
		gui = new AmongUsGUI(crewmateBDICount+crewmateRLCount,impostorBDICount+impostorRLCount,machineryCount);
		chat = new ChatGUI();
		delay();
		                                         
		//setting the color of each player, case when the player color is specified in the configuration file
		for (Map.Entry<String,String> entry : config.getColorMap().entrySet()) {
			if (!entry.getValue().equals("random")) {
				playerColor.put(entry.getKey(), entry.getValue());
				config.removeColor(entry.getValue());
				addPercept("gameManager",Literal.parseLiteral("assignColor("+entry.getKey()+","+entry.getValue()+")"));
			}
		}
		
		//setting the color of each player, case when the player color has to be assigned randomly
		for (String player : config.getPlayerList()) {
			if (!playerColor.containsKey(player)) {
				Random rand = new Random();
				int color = rand.nextInt(config.getColorList().size());
				playerColor.put(player, config.getColorList().get(color));
				addPercept("gameManager",Literal.parseLiteral("assignColor("+player+","+config.getColorList().get(color)+")"));
				config.removeColor(config.getColorList().get(color));
			}
		}
		
		delay();
		
		//setting the initial room of each player
		for (Map.Entry<String,String> entry : config.getInitialRoomMap().entrySet()) {
			if (!entry.getValue().equals("random")) {
				playerRoom.put(entry.getKey(), entry.getValue());
				addPercept("gameManager",Literal.parseLiteral("assignInitialRoom("+entry.getKey()+","+entry.getValue()+")"));
				updateGUI("add",playerColor.get(entry.getKey()),entry.getValue().substring(entry.getValue().length() - 1));
			} else {
				Random rand = new Random();
				int room = rand.nextInt(config.getRoomsMap().size());
				List<String> rooms = new ArrayList<String>(config.getRoomsMap().keySet());
				playerRoom.put(entry.getKey(), rooms.get(room));
				addPercept("gameManager",Literal.parseLiteral("assignInitialRoom("+entry.getKey()+","+rooms.get(room)+")"));
				updateGUI("add",playerColor.get(entry.getKey()),rooms.get(room).substring(rooms.get(room).length() - 1));
			}
		}
		
		//writing the initial stats of the game, with heading
		writeStats(true);
	}
	
	 @Override
	 public void stop() {
		 super.stop();
	 }

	//handles all of the possible actions the players may execute on the environment
	@Override
	public synchronized boolean executeAction(String player, Structure action){
		if (finished && !player.equals("gameManager"))
			return true;
		if (action.getFunctor().equals("moveToNextRoom")) {
			//updates the environment internal informations with the room the player will move to
			String newRoom = action.getTerm(0).toString();
			if(playerColor.containsKey(player) && playerRoom.containsKey(player))
				updateGUI("move",playerColor.get(player),playerRoom.get(player).substring(playerRoom.get(player).length()-1),newRoom.substring(newRoom.length()-1));
			playerRoom.replace(player,newRoom);
			if (player.contains("_RL_")) {
				clearPerceptsForPlayer(player);
				addPercept(player, Literal.parseLiteral("newState(standing)"));
			}
			return true;
		} else if (action.getFunctor().equals("checkRoomSituation")) {
			//action performed by crewmates only, checks if there are broken machinery to fix in the crewmate's current room
			clearPerceptsForPlayer(player);
			String checkOutcome = checkRoom(playerRoom.get(player));
			if (player.contains("_BDI_"))
				addPercept(player, Literal.parseLiteral("checkOutcome("+checkOutcome+")"));
			else
				addPercept(player, Literal.parseLiteral("newState("+checkOutcome+")"));
			return true;
		} else if (action.getFunctor().equals("fixMachinery")) {
			//action performed by crewmates only, fixes one of the machinery in the crewmate's current room
			fixMachineryInRoom(playerRoom.get(player));
			//update of the beliefs of crewmates in the same room of the crewmate who is fixing the broken machinery
			updateTrust(player);
			if (player.contains("_RL_")) {
				clearPerceptsForPlayer(player);
				addPercept(player, Literal.parseLiteral("newState(standing)"));
			}
			return true;
		} else if (action.getFunctor().equals("searchCrewmatesInRoom")) {
			//action performed by impostors only, checks if there are any crewmates in the impostor's current room
			clearPerceptsForPlayer(player);
			searchForCrewmatesInRoom(player);
			return true;
		} else if (action.getFunctor().equals("kill")) {
			//action performed by impostors only, kills one of the crewmates in the impostor's current room
			String playerToRemove = action.getTerm(0).toString();
			if (expulsion(playerToRemove, "killed"))
				updateUntrust(player); //update of the beliefs of crewmates in the same room of the impostor who has just killed another crewmate
			delay();
			if (player.contains("_RL_")) {
				clearPerceptsForPlayer(player);
				addPercept(player, Literal.parseLiteral("newState(goalAccomplished)"));
			}
			return true;
		} else if (action.getFunctor().equals("deceive")) {
			//action performed by impostors only, sends to all of the crewmates in that
			//room a "fake" belief of trust towards that impostor acting as a crewmate
			updateTrust(player);
			if (player.contains("_RL_")) {
				clearPerceptsForPlayer(player);
				addPercept(player, Literal.parseLiteral("newState(goalAccomplished)"));
			}
			return true;
		} else if (action.getFunctor().equals("emergencyExpulsion")) {
			//action performed by crewmates only, removes one impostor from the game
			String presumedImpostor = action.getTerm(0).toString();
			expulsion(presumedImpostor, "voted");
			delay();
			return true;
		} else if (action.getFunctor().equals("trust")) {
			String otherPlayer = action.getTerm(0).toString();
			if (!trustList.contains(player+"_"+otherPlayer) && !untrustList.contains(player+"_"+otherPlayer))
				trustList.add(player+"_"+otherPlayer);
			return true;
		} else if (action.getFunctor().equals("untrust")) {
			String otherPlayer = action.getTerm(0).toString();
			trustList.remove(player+"_"+otherPlayer);
			if (!untrustList.contains(player+"_"+otherPlayer))
				untrustList.add(player+"_"+otherPlayer);
			return true;
		} else if (action.getFunctor().equals("removeTrust")) {
			String otherPlayer1 = action.getTerm(0).toString();
			String otherPlayer2 = action.getTerm(1).toString();
			trustList.remove(player+"_"+otherPlayer1);
			trustList.remove(player+"_"+otherPlayer2);
			return true;
		} else if (action.getFunctor().equals("reportImpostor")) {
			String accusedPlayer = action.getTerm(0).toString();
			for (ConcurrentHashMap.Entry<String, String> entry : playerColor.entrySet()) {
				if (entry.getKey().equals(player) || entry.getKey().equals(accusedPlayer) || !playerColor.containsKey(player))
					continue;
				if (entry.getKey().contains("_BDI_")) {
					int count = this.count++;
					addPercept(entry.getKey(), Literal.parseLiteral("reportedImpostor("+accusedPlayer+","+playerColor.get(player)+","+count+")"));
				} else
					queryBeliefs(entry.getKey(), accusedPlayer, playerColor.get(player));
			}
			return true;
		} else if (action.getFunctor().equals("print")) {
			addMessage(player, action);
			return true;
		} else {
			//this should never happen
			logger.info(action.getFunctor()+" action not implemented");
			return false;
		}
	}
	
	//given a room, tells whether there is something to fix in that room or not
	private synchronized String checkRoom(String room) {
			
		if (room==null)
			return "nothingToDo";
		
		if (roomSituation.containsKey(room)) {
			if(roomSituation.get(room) > 0)
				return "taskDetected";
		}
		
		return "nothingToDo";
	}
	
	//given a room, fixes one of the machines that needs to be fixed in the room
	private synchronized void fixMachineryInRoom(String room) {
		
		if (room == null)
			return;
		
		if (roomSituation.containsKey(room)) {
			
			int numberOfMachinesToFix = roomSituation.get(room);
			
			if(numberOfMachinesToFix > 0) {
				numberOfMachinesToFix--;
				roomSituation.replace(room, numberOfMachinesToFix);
			}
				
			if(machineryCount > 0) {
				machineryCount--;
				updateGUI("doTask");
				//logger.info(machineryCount + " remaining machines to fix");
				
				//after the update, if there are no more broken machines on the spaceship, the central computer
				//starts working again, impostors are detected and expelled and crewmates win the game
				if(machineryCount == 0) {
					finished = true;
					for (ConcurrentHashMap.Entry<String,String> entry : playerColor.entrySet()) {
						if (entry.getKey().startsWith("impostor")) {
							addPercept("gameManager", Literal.parseLiteral("emergencyExpulsion("+entry.getKey()+")"));
							updateGUI("remove",entry.getValue(),"expelled","impostor");
						}
					}
					addPercept("gameManager", Literal.parseLiteral("announceVictory(crewmates1)"));
				}
			}
			
			writeStats(false);
		}
	}
	
	//looks for crewmates in the same room of who called the function, returning the first one found
	private synchronized void searchForCrewmatesInRoom(String player) {
		
		String crewmate = "notFound";
		int count = 0;
		
		for (ConcurrentHashMap.Entry<String, String> entry : playerRoom.entrySet()) {
			if (entry.getValue().equals(playerRoom.get(player)) && entry.getKey().startsWith("crewmate")) {
				crewmate = entry.getKey();
				count++;
			}
		}
		
		if (player.contains("_BDI_"))
			addPercept(player, Literal.parseLiteral("searchOutcome("+playerColor.get(crewmate)+","+count+")"));
		else if (player.contains("_RL_")) {
			clearPerceptsForPlayer(player);
			if (count == 0)
				addPercept(player, Literal.parseLiteral("newState(notFound)"));
			else if (count == 1) {
				addPercept(player, Literal.parseLiteral("involving("+playerColor.get(crewmate)+")"));
				addPercept(player, Literal.parseLiteral("newState(found1)"));
			}
			else
				addPercept(player, Literal.parseLiteral("newState(found2orMore)"));
		}
	}
	
	//updates the belief of trust of crewmates seeing another crewmate fixing something in their same room
	private synchronized void updateTrust(String player) {
		
		if(playerRoom.containsKey(player)) {
			String room = playerRoom.get(player);
			
			for (ConcurrentHashMap.Entry<String, String> entry : playerRoom.entrySet()){
				if(entry.getValue().equals(room) && entry.getKey().startsWith("crewmate") && !entry.getKey().equals(player)){
					if(entry.getKey().contains("_BDI_"))
						addPercept(entry.getKey(), Literal.parseLiteral("wasFixing("+playerColor.get(player)+")"));
					else {
						int count = this.count++;
						addPercept(entry.getKey(), Literal.parseLiteral("involving("+playerColor.get(player)+","+count+")"));
						addPercept(entry.getKey(), Literal.parseLiteral("newSubState(wasFixing,"+count+")"));
					}
				}
			}
		}
	}
	
	//updates the belief of untrust of crewmates seeing an impostor killing another crewmate in their same room
	private synchronized void updateUntrust(String player) {
		
		if(playerRoom.containsKey(player)) {
			String room = playerRoom.get(player);
			String crewmate = null;
			for (ConcurrentHashMap.Entry<String, String> entry : playerRoom.entrySet()){
				if (entry.getValue().equals(room) && entry.getKey().startsWith("crewmate")){
					crewmate = entry.getKey();
					if (crewmate.contains("_BDI_"))
						addPercept(crewmate, Literal.parseLiteral("wasKilling("+playerColor.get(player)+")"));
					else {
						int count = this.count++;
						addPercept(crewmate, Literal.parseLiteral("involving("+playerColor.get(player)+","+count+")"));
						addPercept(crewmate, Literal.parseLiteral("newSubState(wasKilling,"+count+")"));
					}
				}
			}
			//models the "advantage of speaking first" option, randomly giving to the impostor or to one of the crewmates who saw it
			//the possibility of reporting the other one as an impostor
			if (crewmate != null) {
				Random rand = new Random();
				if (rand.nextInt(2) == 0){
					if (crewmate.contains("_BDI_")) {
						addPercept(crewmate, Literal.parseLiteral("advantageReceivedOn("+playerColor.get(player)+")"));
					} else {
						int count = this.count++;
						addPercept(crewmate, Literal.parseLiteral("involving("+playerColor.get(player)+","+count+")"));
						addPercept(crewmate, Literal.parseLiteral("newSubState(advantageReceived,"+count+")"));
					}
				} else {
					if (player.contains("_BDI_")) {
						addPercept(player, Literal.parseLiteral("advantageReceivedOn("+playerColor.get(crewmate)+")"));
					} else {
						int count = this.count++;
						addPercept(player, Literal.parseLiteral("involving("+playerColor.get(crewmate)+","+count+")"));
						addPercept(player, Literal.parseLiteral("newSubState(advantageReceived,"+count+")"));
					}
				}
			}
		}
	}
	
	private synchronized void queryBeliefs(String player, String accusedPlayer, String accuserPlayer) {
		
		int count = this.count++;
		
		if (player.startsWith("impostor")) {
			addPercept(player, Literal.parseLiteral("involving("+accusedPlayer+","+accuserPlayer+","+count+")"));
			addPercept(player, Literal.parseLiteral("newSubState(reported,"+count+")"));
			return;
		}
		
		String newSubState = "reported_dontKnow";
		String accusedKey = player + "_" + accusedPlayer;
		String accuserKey = player + "_" + accuserPlayer;
		
		if (!playerColor.get(player).equals(accusedPlayer) && !playerColor.get(player).equals(accuserPlayer)) {
			if (trustList.contains(accusedKey) && !trustList.contains(accuserKey))
				newSubState = "reported_trustAccused";
			else if (trustList.contains(accuserKey) && !trustList.contains(accusedKey))
				newSubState = "reported_trustAccuser";
			else if (untrustList.contains(accusedKey) && !untrustList.contains(accuserKey))
				newSubState = "reported_untrustAccused";
			else if (untrustList.contains(accuserKey) && !untrustList.contains(accusedKey))
				newSubState = "reported_untrustAccuser";
			
			addPercept(player, Literal.parseLiteral("involving("+accusedPlayer+","+accuserPlayer+","+count+")"));
			addPercept(player, Literal.parseLiteral("newSubState("+newSubState+","+count+")"));
		}
	}
	
	//removes one player from the spaceship
	private synchronized boolean expulsion(String playerToRemove, String scenario) {
		
		String playerName = findAgentNameFromColor(playerToRemove);
		
		if (playerName == null)
			return false;
		
		addPercept("gameManager", Literal.parseLiteral("expulsion("+playerName+")"));
		if(playerColor.containsKey(playerName)){
			updateGUI("remove",playerColor.get(playerName),scenario,playerName.startsWith("impostor") ? "impostor" : "crewmate");
			if(scenario.equals("voted"))
				logger.info(playerToRemove + " was expelled from the spaceship because two or more crewmates agreed about it");
			else if (scenario.equals("killed"))
				logger.info(playerToRemove + "'s corpse was expelled from the spaceship");
			playerRoom.remove(playerName);
			playerColor.remove(playerName);
			if (playerName.startsWith("crewmate")) {
				if (playerName.contains("_BDI_"))
					crewmateBDICount--;
				else
					crewmateRLCount--;
				if (crewmateBDICount == 0 && crewmateRLCount == 0) {
					finished = true;
					delay();
					addPercept("gameManager", Literal.parseLiteral("announceVictory(impostors)"));
				}
			} else {
				if (playerName.contains("_BDI_"))
					impostorBDICount--;
				else
					impostorRLCount--;
				if (impostorBDICount == 0 && impostorRLCount == 0) {
					finished = true;
					delay();
					addPercept("gameManager", Literal.parseLiteral("announceVictory(crewmates2)"));
				}
			}
			
			writeStats(false);
			return true;
		}
		
		return false;
	}
	
	//utility function, given a color, returns the name of the agent having that color
	private synchronized String findAgentNameFromColor(String color) {
		
		for (ConcurrentHashMap.Entry<String, String> entry : playerColor.entrySet()){
			if(entry.getValue().equals(color)){
				return entry.getKey();
			}
		}
		return null;
	}
	
	//utility function, saves the current statistics of the game into a file
	private synchronized void writeStats(boolean writeHeading) {
		try {
			FileWriter writer = new FileWriter(file, true);
			if(writeHeading)
				writer.write("tasks,crewmates_BDI,crewmates_RL,impostors_BDI,impostors_RL,timestamp\n");
			writer.write(machineryCount + "," + crewmateBDICount + "," + crewmateRLCount + "," + impostorBDICount + "," + impostorRLCount + "," + new Timestamp(System.currentTimeMillis()) + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//utility function, updates the GUI with new informations
	private synchronized void updateGUI(String scenario, String... args) {
		if (scenario.equals("add")) {
			String player = args[0];
			String room = args[1];
			gui.addPlayer(player,room);
			delay();
		} else if (scenario.equals("move")) {
			String player = args[0];
			String newRoom = args[1];
			gui.movePlayer(player,playerRoom.get(player),newRoom);
			delay();
		} else if (scenario.equals("remove")) {
			String player = args[0];
			String cause = args[1];
			String playerType = args[2];
			gui.removePlayer(player,cause,playerType);
			delay();
		} else if (scenario.equals("doTask")) {
			gui.updateTasks();
			delay();
		}
	}
	
	//utility function, adds a message to the chat GUI
	private synchronized void addMessage(String player, Structure action) {
		if(!player.equals("gameManager") && !playerColor.containsKey(player))
			return;
		
		String color = player.equals("gameManager") ? "" : playerColor.get(player);
		String message = "";
		for (Term term : action.getTerms()) {
			message = message + term.toString().replace("\"","");
		}
		String[] words = message.split(" ");
		String chunk = "";
		boolean padding = false;
		for (String word : words) {
			String tryChunk = chunk + (word.equals(words[0]) ? "" : " ") + word;
			if (tryChunk.length() > 70) {
				chat.addMessage(color, chunk, padding);
				chunk = word;
				padding = true;
			} else {
				chunk = tryChunk;
			}
		}

		chat.addMessage(color, chunk, padding);
	}
	
	//utility function, clears specific room percepts an agent can have
	private synchronized void clearPerceptsForPlayer(String player) {
		if(containsPercept(player, Literal.parseLiteral("checkOutcome(taskDetected)")))
			removePercept(player, Literal.parseLiteral("checkOutcome(taskDetected)"));
		if(containsPercept(player, Literal.parseLiteral("checkOutcome(nothingToDo)")))
			removePercept(player, Literal.parseLiteral("checkOutcome(nothingToDo)"));
		if(containsPercept(player, Literal.parseLiteral("searchOutcome(notFound,0)")))
			removePercept(player, Literal.parseLiteral("searchOutcome(notFound,0)"));
		if(containsPercept(player, Literal.parseLiteral("newState(standing)")))
			removePercept(player, Literal.parseLiteral("newState(standing)"));
		if(containsPercept(player, Literal.parseLiteral("newState(somethingHasToBeDone)")))
			removePercept(player, Literal.parseLiteral("newState(somethingHasToBeDone)"));
		if(containsPercept(player, Literal.parseLiteral("newState(nothingToDoHere)")))
			removePercept(player, Literal.parseLiteral("newState(nothingToDoHere)"));
		if(containsPercept(player, Literal.parseLiteral("newState(found1)")))
			removePercept(player, Literal.parseLiteral("newState(found1)"));
		if(containsPercept(player, Literal.parseLiteral("newState(found2orMore)")))
			removePercept(player, Literal.parseLiteral("newState(found2orMore)"));
		if(containsPercept(player, Literal.parseLiteral("newState(goalAccomplished)")))
			removePercept(player, Literal.parseLiteral("newState(goalAccomplished)"));
		if(containsPercept(player, Literal.parseLiteral("newState(notFound)")))
			removePercept(player, Literal.parseLiteral("newState(notFound)"));
	}
	
	//utility function, adds 1 second of delay to the execution of the current action
	private synchronized void delay() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch(Exception e) {
			logger.info(e.getMessage());
		}
	}
}