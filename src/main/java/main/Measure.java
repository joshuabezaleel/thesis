package main;

public class Measure {
	public String measureName;
	public String measureDataType;
	public String measureAggregateFunction;
	
	public Measure(String measureName, String measureDataType, String measureAggregateFunction) {
		this.measureName = measureName;
		this.measureDataType = measureDataType;
		this.measureAggregateFunction = measureAggregateFunction;
	}
}
