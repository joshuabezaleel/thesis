package main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import org.teiid.adminapi.Admin;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.VDB;
import org.teiid.adminapi.VDB.ConnectionType;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.adminapi.impl.VDBMetadataParser;
import org.teiid.adminapi.jboss.AdminFactory;
import org.teiid.jdbc.TeiidDriver;

import main.AdminUtil;

public class Main {
	// Declaration of static variable
	private static Admin admin;
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		/* 
		 * Creating the cube structure and input it to the data structure
		 */
		
		// Variable declaration
		String fileName;
		String[] columns;
		Map<String,String> columnDataTypes = new HashMap<String,String>();
		List<Measure> measures = new ArrayList<Measure>();
		Measure measure = new Measure(null,null,null);
		List<Dimension> dimensions = new ArrayList<Dimension>();
		Dimension dimension = new Dimension(null);
		String dimensionName;
		Hierarchy hierarchy = new Hierarchy(null);
		String hierarchyName;
		Level level = new Level(null);
		String levelName;
		Scanner reader = new Scanner(System.in);
		
		// Defining columns' data types.
		System.out.print("Input the path to the CSV file: ");
		reader = new Scanner(System.in);
		String filePath = reader.nextLine();
		String temp[] = filePath.split("/");
		fileName = temp[temp.length-1].substring(0, temp[temp.length-1].length()-4);
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		columns = br.readLine().toLowerCase().split(",");
		for(int i=0;i<columns.length;i++) {
			System.out.println("Choose the data type of the column "+columns[i]);
			System.out.println("1 for number (decimal), 2 for string");
			System.out.print("DataType chosen: ");
			String tempDataType = reader.next();
			if(tempDataType.equals("1")) {
				columnDataTypes.put(columns[i].toLowerCase(), "decimal");
			} else if(tempDataType.equals("2")) {
				columnDataTypes.put(columns[i].toLowerCase(), "string");
			}
		}
		br.close();
//		for (Map.Entry<String, String> entry : columnDataTypes.entrySet())
//		{
//		    System.out.println(entry.getKey() + "/" + entry.getValue());
//		}
		
//		System.out.println("This CLI program guide you through the process of making a OLAP cube structure\n");
//		String input;
//		// Dimensions definitions
//		do {
//			System.out.print("Input new Dimension (input \"end\" to end the process): ");
//			reader = new Scanner(System.in);
//			input = reader.nextLine();
//			
//			// Go to add hierarchies if input does not equal "end"
//			if(!input.equals("end")) {
//				dimensionName = input;
//				dimension = new Dimension(dimensionName);
//				do {
//					System.out.print("Input new Hierarchy for dimension "+dimensionName+" (input \"end\" to end the process): ");
//					reader = new Scanner(System.in);
//					input = reader.nextLine();
//					
//					// Go to add levels if input does not equal end
//					if(!input.equals("end")) {
//						hierarchyName = input;
//						hierarchy = new Hierarchy(hierarchyName);
//						do {
//							System.out.println("Input new Level for hierarchy "+hierarchyName+" (input \"end\" to end the process).");
//							System.out.print("The order of the input is from the lowest level to the highest level (ascending): ");
//							reader = new Scanner(System.in);
//							input = reader.nextLine();
//							
//							// Go to add level attributes if input does not equal end
//							if(!input.equals("end")) {
//								levelName = input;
//								level = new Level(levelName);
////								System.out.println(levelName);
//								do {
//									System.out.println("Choose which Level Attributes from the columns below for level "+levelName +" (input \"end\" to end the process).");
//									System.out.println("The first Level Attributes chosen will be the Primary Attribute (identifier).");
//									for(int i=0;i<columns.length;i++) {
//										System.out.println(i+1+". "+columns[i]);
//									}
//									System.out.print("LevelAttribute chosen: ");
//									reader = new Scanner(System.in);
//									input = reader.nextLine();
//									if(!input.equals("end")) {
//										String tempLevelAttribute = columns[Integer.parseInt(input)-1];
//										level.levelAttributes.put(tempLevelAttribute,columnDataTypes.get(tempLevelAttribute));
////										System.out.println(tempLevelAttribute+" "+columnDataTypes.get(tempLevelAttribute));
//									}
//								} while(!input.equals("end"));
////								System.out.println(hierarchy.hierarchyName+levelName);
//								hierarchy.levels.add(level);
//								level = new Level(null);
//								input="";
//							}
//							
//						} while(!input.equals("end"));
//						dimension.hierarchies.add(hierarchy);
//						hierarchy = new Hierarchy(null);
//						input = "";
//					}
//					 
//
//				} while(!input.equals("end"));
//				dimensions.add(dimension);
//				dimension = new Dimension(null);
//				input = "";
//			}
//		} while(!input.equals("end"));
//		
//		// Measures definition
//		do {
//			System.out.println("Choose Measure from the columns below (input \"end\" to end the process): ");
//			for(int i=0;i<columns.length;i++) {
//				System.out.println(i+1+". "+columns[i]);
//			}
//			reader = new Scanner(System.in);
//			input = reader.nextLine();
//			if(!input.equals("end")) {
//				String tempMeasure = columns[Integer.parseInt(input)-1];
//				System.out.println("Choose AggregateFunction for the Measure "+tempMeasure);
//				System.out.println("1=sum, 2=avg, 3=count, 4=min, 5=max");
//				reader = new Scanner(System.in);
//				input = reader.nextLine();
//				switch (Integer.parseInt(input)) {
//					case 1:
//						measure = new Measure(tempMeasure,columnDataTypes.get(tempMeasure),"sum");
//						break;
//					case 2:
//						measure = new Measure(tempMeasure,columnDataTypes.get(tempMeasure),"avg");
//						break;
//					case 3:
//						measure = new Measure(tempMeasure,columnDataTypes.get(tempMeasure),"count");
//						break;
//					case 4:
//						measure = new Measure(tempMeasure,columnDataTypes.get(tempMeasure),"min");
//						break;
//					case 5:
//						measure = new Measure(tempMeasure,columnDataTypes.get(tempMeasure),"max");
//						break;
//				}
//				measures.add(measure);
//				measure = new Measure(null,null,null);
//				input = "";
//			}
//		} while(!input.equals("end"));
//		
//		System.out.println("End of creating cube structure process\n");
//		//once finished
//		reader.close();
		
		// Printing the cube data structure
//		for(int i=0;i<dimensions.size();i++) {
//			System.out.println("Dimension= "+dimensions.get(i).dimensionName);
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				System.out.println("Hierarchy= "+dimensions.get(i).hierarchies.get(j).hierarchyName);
//				for(int k=0;k<dimensions.get(i).hierarchies.get(j).levels.size();k++) {
//					System.out.println("Levels= "+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName);
//					for(Map.Entry<String, String> entry : dimensions.get(i).hierarchies.get(j).levels.get(k).levelAttributes.entrySet()) {
//						System.out.println("LevelAttributes: "+ entry.getKey() + " DataType: "+ entry.getValue());
//					}
//				}
//			}
//		}
		
		// Print all the measures
//		for(int i=0;i<measures.size();i++) {
//			System.out.println(measures.get(i).measureName+measures.get(i).measureDataType+measures.get(i).measureAggregateFunction);
//		}
		
		/*
		 * End of creating the defined cube data structure from CSV file
		 */
		
		/*
		 * Creating the turtle file for the backbone of the cube structure
		 */
		
		// Setting up
//		ModelBuilder builder = new ModelBuilder();
//		FileOutputStream out = new FileOutputStream("/home/jobel/undergrad_thesis/software_and_data/data/thesis-1/eclipse_output/"+fileName+".ttl");
//		
//		// Setting namespaces
//		builder.setNamespace("data","http://example.com/data/");
//		builder.setNamespace("schema","http://example.com/schema/");
//		builder.setNamespace("property","http://example.com/property/");
//		builder.setNamespace(RDF.NS);
//		builder.setNamespace(RDFS.NS);
//		builder.setNamespace("xsd","http://www.w3.org/2001/XMLSchema#");
//		builder.setNamespace("qb","http://purl.org/linked-data/cube#");
//		builder.setNamespace("qb4o","http://purl.org/qb4olap/cubes#");
//		
//		// CubeDataSet
//		builder.subject("data:"+fileName+"CubeDataSet")
//			.add(RDF.TYPE, "qb:DataSet")
//			.add("qb:structure", "schema:"+fileName+"CubeDataStructureDefinition");
//		
//		// CubeDataStructureDefinition
//		ValueFactory vf = SimpleValueFactory.getInstance();
//		builder.subject("schema:"+fileName+"CubeDataStructureDefinition")
//			.add(RDF.TYPE, "qb:DataStructureDefinition");
//		
//		// CubeComponents
//		// Dimension
//		for(int i=0; i<dimensions.size();i++) {
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				String lowestLevel = dimensions.get(i).hierarchies.get(j).getLowestLevel().levelName;
//				BNode component = vf.createBNode();
//				builder.subject("schema:"+fileName+"CubeDataStructureDefinition")
//					.add("qb:component", component)
//				.subject(component)
//					.add("qb:level","schema:"+lowestLevel)
//					.add("qb4o:cardinality", "qb4o:ManyToOne");
//			}
//		}
//		// Measure
//		for(int i=0;i<measures.size();i++) {
//			BNode component = vf.createBNode();
//			builder.subject("schema:"+fileName+"CubeDataStructureDefinition")
//				.add("qb:component", component)
//			.subject(component)
//				.add("qb:measure", "schema:"+measures.get(i).measureName)
//				.add("qb4o:aggregateFunction", "qb4o:"+measures.get(i).measureAggregateFunction);
//		}
//		
//		// Measures definition (measureProperty)
//		for(int i=0; i<measures.size(); i++) {
//			builder.subject("schema:"+measures.get(i).measureName)
//				.add(RDF.TYPE, "qb:MeasureProperty")
//				.add(RDFS.RANGE, "xsd:"+measures.get(i).measureDataType);
//		}
//		
//		// Dimension definition (dimensionProperty)
//		for(int i=0;i<dimensions.size();i++) {
////			System.out.println("Dimension = "+dimensions.get(i).dimensionName);
//			// DimensionProperty
//			builder.add("schema:"+dimensions.get(i).dimensionName+"Dimension",RDF.TYPE, "qb:DimensionProperty");
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				// hasHierarchy
//				builder.add("schema:"+dimensions.get(i).dimensionName+"Dimension", "qb4o:hasHierarchy","schema:"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy");
//				// Hierarchy
//				builder.subject("schema:"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy")
//					.add(RDF.TYPE, "qb4o:Hierarchy")
//					// inDimension
//					.add("qb4o:inDimension", "schema:"+dimensions.get(i).dimensionName+"Dimension");
//				for(int k=0;k<dimensions.get(i).hierarchies.get(j).levels.size();k++) {
//					// hasLevel
//					builder.add("schema:"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy","qb4o:hasLevel","schema:"+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName);
//					// LevelProperty
//					builder.add("schema:"+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName,RDF.TYPE, "qb4o:LevelProperty");
//					for(Map.Entry<String, String> levelAttribute : dimensions.get(i).hierarchies.get(j).levels.get(k).levelAttributes.entrySet()) {
//						// hasAttribute
//						builder.add("schema:"+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName, "qb4o:hasAttribute","property:"+levelAttribute.getKey()+"Property");
//						// LevelAttribute
//						builder.add("property:"+levelAttribute.getKey()+"Property",RDF.TYPE,"qb4o:LevelAttribute");
//						// rdfs:range
//						builder.add("property:"+levelAttribute.getKey()+"Property",RDFS.RANGE,"xsd:"+levelAttribute.getValue());
//					}
//				}
//				
//			}
//			
//			// RollupProperty and HierarchyStep
//			System.out.println(dimensions.get(i).dimensionName+" Dimension Hierarchies size = " + Integer.toString(dimensions.get(i).hierarchies.size()-1));
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
////				System.out.println("j = " + j);
////				System.out.println("dimensions.get(i).hierarchies.size()-1 = " + Integer.toString(dimensions.get(i).hierarchies.size()-1));
//				for(int k=0;k<dimensions.get(i).hierarchies.get(j).levels.size()-1;k++) {
//					System.out.println("Dimension = "+dimensions.get(i).dimensionName);
//					System.out.println("Hierarchy = "+dimensions.get(i).hierarchies.get(j).hierarchyName);
//					System.out.println("Level = "+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName);
//					System.out.println("k = " + k);
//					System.out.println("dimensions.get(i).hierarchies.get(j).levels.size()-1 = " + Integer.toString(dimensions.get(i).hierarchies.get(j).levels.size()-1));
//					// RollupProperty
//					builder.add("schema:"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-Rollup"+j,RDF.TYPE,"qb4o:RollupProperty");
//					// HierarchyStep
//					builder.subject("schema:"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-Step"+j)
//						.add(RDF.TYPE, "qb4o:HierarchyStep")
//						.add("qb4o:inHierarchy", "schema:"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy")
//						.add("qb4o:pcCardinality", "qb4o:ManyToOne")
//						.add("qb4o:rollup","schema:"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-Rollup"+j)
//					// childLevel and parentLevel
//						.add("qb4o:childLevel", "schema:"+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName)
//						.add("qb4o:parentLevel", "schema:"+dimensions.get(i).hierarchies.get(j).levels.get(k+1).levelName);
//				}
//			}
//		}
//		
//		// Writing to the file
//		org.eclipse.rdf4j.model.Model model = builder.build();
//		try {
//			Rio.write(model, out, RDFFormat.TURTLE);
//		} finally {
//			try {
//				out.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		/*
		 * End of creating turtle file process
		 */
		
		/* 
		 * Creating VDB
		 */
		
		// Creating the VDB using VDBMetaData Class
		VDBMetaData vdbMD = new VDBMetaData();
		vdbMD.setName(fileName);
		vdbMD.setDescription(fileName+" vdb description");
		vdbMD.setVersion(1);
		vdbMD.setConnectionType(ConnectionType.BY_VERSION);
		
		// Model definition
		// The first model is the physical, for the connection to the physical CSV file
		ModelMetaData physicalModel = new ModelMetaData();
		physicalModel.setName("physicalModel");
		physicalModel.setModelType(Model.Type.PHYSICAL);
		// Setting the properties of the physical model
		physicalModel.addSourceMapping("text-connector", "file", "java:/textconnector-file");
		
		// Adding new HashMap, changing from decimal to bigdecimal for Teiid RDB compatibility
		Map<String,String> teiidDataTypes = new HashMap<String,String>();
		for(Map.Entry<String, String> columnDataType : columnDataTypes.entrySet()) {
			if(columnDataType.getValue().equals("decimal")){
				teiidDataTypes.put(columnDataType.getKey(), "bigdecimal");
			}else {
				teiidDataTypes.put(columnDataType.getKey(), "string");
			}
		}
		
		// The second model is the virtual one, for creating the virtual relational database from the CSV file
		ModelMetaData virtualModel = new ModelMetaData();
		virtualModel.setName("virtualModel");
		virtualModel.setModelType(Model.Type.VIRTUAL);
//		System.out.println("testing");
//		for(Map.Entry<String, String> columnDataType : columnDataTypes.entrySet()) {
//			System.out.println(columnDataType.getKey()+" "+columnDataType.getValue());
//		}
//		for(Map.Entry<String, String> teiidDataType : teiidDataTypes.entrySet()) {
//			System.out.println(teiidDataType.getKey()+" "+teiidDataType.getValue());
//		}
		String teiidDDLView = "CREATE VIEW "+fileName+" (\n";
		int tempIterator = 0;
		for(Map.Entry<String, String> teiidDataType : teiidDataTypes.entrySet()) {
			System.out.println("tempIterator="+tempIterator);
			System.out.println("size="+Integer.toString(teiidDataTypes.size()-1));
			teiidDDLView = teiidDDLView.concat("            "+teiidDataType.getKey()+" "+teiidDataType.getValue());
			if(tempIterator!=teiidDataTypes.size()-1) {
				teiidDDLView = teiidDDLView.concat(",");
			}
			teiidDDLView = teiidDDLView.concat("\n");
			tempIterator++;
		}
		teiidDDLView = teiidDDLView.concat("        ) AS  \n");
		teiidDDLView = teiidDDLView.concat("          SELECT ");
		tempIterator = 0;
		for(Map.Entry<String, String> teiidDataType : teiidDataTypes.entrySet()) {
			if(tempIterator!=teiidDataTypes.size()-1) {
				teiidDDLView = teiidDDLView.concat("file."+teiidDataType.getKey()+", ");
			}else {
				teiidDDLView = teiidDDLView.concat("file."+teiidDataType.getKey()+"\n");
			}
			tempIterator++;
		}
		teiidDDLView = teiidDDLView.concat("            FROM (EXEC physicalModel.getTextFiles('"+fileName+".csv')) AS f, \n");
		teiidDDLView = teiidDDLView.concat("            TEXTTABLE(f.file COLUMNS ");
		tempIterator=0;
		for(Map.Entry<String, String> teiidDataType : teiidDataTypes.entrySet()) {
			if(tempIterator!=teiidDataTypes.size()-1) {
				teiidDDLView = teiidDDLView.concat(teiidDataType.getKey()+" "+teiidDataType.getValue()+", ");
			}else if(tempIterator==teiidDataTypes.size()-1){
				teiidDDLView = teiidDDLView.concat(teiidDataType.getKey()+" "+teiidDataType.getValue()+" ");
			}
			tempIterator++;
		}
		teiidDDLView = teiidDDLView.concat("HEADER) AS file;");
		virtualModel.addSourceMetadata("DDL",teiidDDLView);
		
		System.out.println(teiidDDLView);
		vdbMD.addModel(physicalModel);
		vdbMD.addModel(virtualModel);
		
		// Marshalling
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		VDBMetadataParser.marshell(vdbMD, outStream);
		
		/* 
		 * End of creating VDB phase
		 */
		
		/*
		 * Creating obda file
		 */
//		FileWriter fw = new FileWriter("/home/jobel/undergrad_thesis/software_and_data/data/thesis-1/eclipse_output/"+fileName+".obda");
//		fw.write("[PrefixDeclaration]\n");
//		fw.write(":\t\t\t#\n");
//		fw.write("owl:\t\thttp://www.w3.org/2002/07/owl#\n");
//		fw.write("xml:\t\thttp://www.w3.org/XML/1998/namespace\n");
//		fw.write("xsd:\t\thttp://www.w3.org/2001/XMLSchema#\n");
//		fw.write("obda:\t\thttps://w3id.org/obda/vocabulary#\n");
//		fw.write("qb:\t\t\thttp://purl.org/linked-data/cube#\n");
//		fw.write("qb4o:\t\thttp://purl.org/qb4olap/cubes#\n");
//		fw.write("rdfs:\t\thttp://www.w3.org/2000/01/rdf-schema#\n");
//		fw.write("schema:\t\thttp://example.com/schema/\n");
//		fw.write("property:\thttp://example.com/property/\n");
//		fw.write("data:\t\thttp://example.com/data/\n");
//		fw.write("quest:\t\thttp://obda.org/quest#\n");
//		fw.write("\n");
//		fw.write("[MappingDeclaration] @collection [[\n");
//		
//		Level tempLevel = new Level(null);
//		Level nextLevel = new Level(null);
//		// Level members definition
//		for(int i=0;i<dimensions.size();i++) {
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				for(int k=0;k<dimensions.get(i).hierarchies.get(j).levels.size();k++) {
//					tempLevel = dimensions.get(i).hierarchies.get(j).levels.get(k);
//					// LevelMembers definition
//					fw.write("mappingId\tMAPID-"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-"+tempLevel.levelName+"Level-LevelMembers\n");
//					fw.write("target\t\tdata:"+tempLevel.levelName+"-{"+tempLevel.getPrimaryAttribute()+"} " + "qb4o:memberOf schema:"+tempLevel.levelName+" .\n");
//					fw.write("source\t\tselect distinct "+tempLevel.levelName+" from "+fileName+"\n");
//					fw.write("\n");
//					
//					// LevelAttribute definition
//					for(Map.Entry<String, String> levelAttribute : dimensions.get(i).hierarchies.get(j).levels.get(k).levelAttributes.entrySet()) {
//						fw.write("mappingId\tMAPID-"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-"+tempLevel.levelName+"Level-"+levelAttribute.getKey()+"LevelAttribute\n");
//						fw.write("target\t\tdata:"+tempLevel.levelName+"-{"+tempLevel.getPrimaryAttribute()+"} "
//								+ "property:"+levelAttribute.getKey()+"Property {"+levelAttribute.getKey()+"}^^xsd:"+levelAttribute.getValue()+" .\n");
//						fw.write("source\t\tselect distinct "+levelAttribute.getKey()+" from "+fileName+"\n");
//						fw.write("\n");
//					}
//					
//					// RollupProperty definition
//					// If this is not the last element, then add the next level as the upper level
//					if(k!=dimensions.get(i).hierarchies.get(j).levels.size()-1) {
//						nextLevel = dimensions.get(i).hierarchies.get(j).levels.get(k+1);
//						fw.write("mappingId\tMAPID-"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-"+tempLevel.levelName+"To"+nextLevel.levelName+"-RollupProperty\n");
//						fw.write("target\t\tdata:"+tempLevel.levelName+"-{"+tempLevel.getPrimaryAttribute()+"} " + "schema:"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-Rollup"+j+" data:"+nextLevel.levelName+"-{"+nextLevel.levelName+"} .\n");
//						fw.write("source\t\tselect distinct "+tempLevel.levelName+", "+nextLevel.levelName+" from "+fileName+"\n");
//						fw.write("\n");
//					}
//					nextLevel = new Level(null);
////					dimensions.get(i).hierarchies.get(j).levels.get(k).getPrimaryAttribute()
//				}
//				tempLevel = new Level(null);
//			}
//		}
//		// Observation definition
//		String tempTarget = "target\t\tdata:obs-";
//		String tempSource = "source\t\tselect distinct ";
////		List<String> primaryAttributes = new ArrayList<String>();
//		for(int i=0;i<dimensions.size();i++) {
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				tempTarget = tempTarget.concat("{"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"}.");
//				tempSource = tempSource.concat(dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+", ");
//			}
//		}
//		// Delete the last "."
//		tempTarget = tempTarget.substring(0, tempTarget.length()-1);
//		// Delete the last ", "
//		tempSource = tempSource.substring(0, tempSource.length()-2);
//		
//		fw.write("mappingId\tMAPID-Observation\n");
//		fw.write(tempTarget+" a qb:Observation .\n");
//		fw.write(tempSource+"  from "+fileName+"\n");
//		fw.write("\n");
//		
//		// Observation CubeDataSet definition
//		fw.write("mappingId\tMAPID-Observation-CubeDataSetDefinition\n");
//		fw.write(tempTarget+" qb:dataSet data:"+fileName+"CubeDataSet .\n");
//		fw.write(tempSource+"  from "+fileName+"\n");
//		fw.write("\n");
//		
//		// Observation with LevelAttributes definition
//		for(int i=0;i<dimensions.size();i++) {
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				fw.write("mappingId\tMAPID-Observation-"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"Level\n");
//				fw.write(tempTarget+" schema:"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+" data:"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"-{"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"} .\n");
//				fw.write(tempSource+" from "+fileName+"\n");
//				fw.write("\n");
//			}
//		}
//		
//		// Observation with Measures definition
//		for(int i=0;i<measures.size();i++) {
//			fw.write("mappingId\tMAPID-Observation-"+measures.get(i).measureName+"Measure\n");
//			fw.write(tempTarget+" schema:"+measures.get(i).measureName+" {"+measures.get(i).measureName+"}^^xsd:"+measures.get(i).measureDataType+" .\n");
//			fw.write(tempSource+", "+measures.get(i).measureName+" from "+fileName+"\n");
//			fw.write("\n");
//		}
//		
//		fw.write("]]");
//		fw.close();
		
		/*
		 * End of creating obda file
		 */
		
//		for(int i=0;i<dimensions.size();i++) {
//			System.out.println("Dimension= "+dimensions.get(i).dimensionName);
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				System.out.println("Hierarchy= "+dimensions.get(i).hierarchies.get(j).hierarchyName);
//				for(int k=0;k<dimensions.get(i).hierarchies.get(j).levels.size();k++) {
//					System.out.println("Levels= "+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName);
//					for(Map.Entry<String, String> entry : dimensions.get(i).hierarchies.get(j).levels.get(k).levelAttributes.entrySet()) {
//						System.out.println("LevelAttributes: "+ entry.getKey() + " DataType: "+ entry.getValue());
//					}
//				}
//			}
//		}
		
		/*
		 * Creating properties file
		 */
		
//		FileWriter fw2 = new FileWriter("/home/jobel/undergrad_thesis/software_and_data/data/thesis-1/eclipse_output/"+fileName+".docker.properties");
//		fw2.write("jdbc.url=jdbc\\:teiid\\:"+fileName+".1@mm\\://172.17.0.1\\:31000\n");
//		fw2.write("jdbc.driver=org.teiid.jdbc.TeiidDriver\n");
//		fw2.write("jdbc.user=usernew\n");
//		fw2.write("jdbc.name=\n");
//		fw2.write("jdbc.password=user1664!");
//		fw2.close();
		
		
		/*
		 * End of creating properties file
		 */
		
		/*
		 * Creating connection and deployment
		 */
		Properties connectionProps = new Properties();
		// (!!!) pathnya diambil dari filepath
		String asdf[] = filePath.split("/");
		String qwe = "";
		for(int i=0;i<asdf.length-1;i++) {
			if(i!=0) {
				qwe = qwe.concat("/");
			}
			qwe = qwe.concat(asdf[i]);
		}
		qwe = qwe.concat("/");
		System.out.println("qwe = "+qwe);
//		for (String asd : asdf) {
//			qwe.concat(asd);
//			if (!asd.equals(asdf[asdf.length]))
//			qwe.concat("/");
//		}
		connectionProps.setProperty("ParentDirectory", qwe);
//		System.out.println("asdfghjkl"+filePath.substring(filePath.lastIndexOf("/")+1));
//		System.out.println(filePath);
		connectionProps.setProperty("AllowParentPaths", "true");
		connectionProps.setProperty("class-name", "org.teiid.resource.adapter.file.FileManagedConnectionFactory");
		
		admin = AdminFactory.getInstance().createAdmin("localhost", AdminUtil.MANAGEMENT_PORT , "admin", "admin".toCharArray());
		AdminUtil.createDataSource(admin, "textconnector-file", "file", connectionProps);
		// nama xmlnya belum diambil dari filename
		admin.deploy(fileName+"-vdb.xml", new ByteArrayInputStream(outStream.toByteArray()));
		
		/*
		 * End of creating connection and deployment
		 */
		
		// VDB Status Test
		VDB vdbTest = admin.getVDB("sales", "1");
		System.out.println(vdbTest.getStatus());
		
		// Connection Test
		Connection conn = TeiidDriver.getInstance().connect("jdbc:teiid:sales.1@mm://localhost:31000;user=usernew;password=user1664!", null);
		System.out.println("status="+conn.isValid(0));
		Statement stmt = conn.createStatement();
		ResultSet resultSet = stmt.executeQuery("select * from sales");
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		for (int i=1; i<=columnsNumber; i++) {
			System.out.print(rsmd.getColumnName(i) + " ");
		}
		System.out.println("");
		while(resultSet.next()) {
			for (int i=1; i<=columnsNumber; i++) {
				if (i>1) System.out.print(",");
//							System.out.print(resultSet.getString(i));
					System.out.print(resultSet.getObject(i));
			}
			System.out.println("");
		}
	}
}
