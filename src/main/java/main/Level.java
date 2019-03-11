package main;

import java.util.HashMap;
import java.util.Map;

public class Level {
	public String levelName;
	public Map<String, String> levelAttributes = null;
	
	public Level(String levelName){
		this.levelName = levelName;
		this.levelAttributes = new HashMap<String, String>();
	}
	
	public String getPrimaryAttribute (){
		return this.levelAttributes.keySet().stream().findFirst().get();
	}
}
