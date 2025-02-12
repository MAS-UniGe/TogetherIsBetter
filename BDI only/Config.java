import java.util.Map;
import java.util.List;

public class Config {

	private Map<String,String> colorMap;
	private Map<String,String> initialRoomMap;
	private Map<String,String> skillMap;
	private Map<String,String> behaviorMap;
	private Map<String,String> roomsMap;
	private List<String> colorList;
	private List<String> playerList;
	
	public Map<String,String> getColorMap() {
		return this.colorMap;
	}
	
	public void setColorMap(Map<String,String> colorMap) {
		this.colorMap = colorMap;
	}
	
	public Map<String,String> getInitialRoomMap() {
		return this.initialRoomMap;
	}
	
	public void setInitialRoomMap(Map<String,String> initialRoomMap) {
		this.initialRoomMap = initialRoomMap;
	}
	
	public Map<String,String> getSkillMap() {
		return this.skillMap;
	}
	
	public void setSkillMap(Map<String,String> skillMap) {
		this.skillMap = skillMap;
	}
	
	public Map<String,String> getBehaviorMap() {
		return this.behaviorMap;
	}
	
	public void setBehaviorMap(Map<String,String> behaviorMap) {
		this.behaviorMap = behaviorMap;
	}
	
	public Map<String,String> getRoomsMap() {
		return this.roomsMap;
	}
	
	public void setRoomsMap(Map<String,String> roomsMap) {
		this.roomsMap = roomsMap;
	}
	
	public List<String> getColorList() {
		return this.colorList;
	}
	
	public void setColorList(List<String> colorList) {
		this.colorList = colorList;
	}
	
	public void removeColor(String color) {
		int position = this.colorList.indexOf(color);
		this.colorList.remove(position);
	}
	
	public List<String> getPlayerList() {
		return this.playerList;
	}
	
	public void setPlayerList(List<String> playerList) {
		this.playerList = playerList;
	}
}
