package main;

import java.util.ArrayList;
import java.util.List;

public class Hierarchy {
	public String hierarchyName;
	public List<Level> levels = null; 
	
	public Hierarchy(String hierarchyName) {
		this.hierarchyName = hierarchyName;
		levels = new ArrayList<Level>();
	}
	
	public Level getLowestLevel() {
		return this.levels.get(0);
	}
}
