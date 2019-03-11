package main;

import java.util.ArrayList;
import java.util.List;

public class Dimension {
	public String dimensionName;
	public List<Hierarchy> hierarchies = null;
	
	public Dimension(String dimensionName) {
		this.dimensionName = dimensionName;
		this.hierarchies = new ArrayList<Hierarchy>();
	}
}
