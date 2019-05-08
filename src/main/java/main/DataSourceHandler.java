package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

public class DataSourceHandler {
	public DataSourceDataTypeFiles make(String folderPath, File[] csvs) throws IOException {
		DataSourceDataTypeFiles dataSourceDataTypeFiles = new DataSourceDataTypeFiles();
		dataSourceDataTypeFiles.setFolderPath(folderPath);
		
		// Variables definition
		String fileName = null;
		String[] columnName = null;
		String[] columnType = null;
		int dataTypeIterator = 1;
		DataSourceDataType dataSourceDataType = new DataSourceDataType(0, null, null);
		ArrayList<DataSourceDataType> tempDataTypeList = new ArrayList<DataSourceDataType>();
		
		// Iterating over CSV file, taking user input for data types of every column on each file, and
		// add it to the Map<File, Map<ColumnName,ColumnDataType>>
		System.out.println("=== CSV DATATYPE DEFINITION ===");
		for(File csv:csvs){
			tempDataTypeList = new ArrayList<DataSourceDataType>();
			BufferedReader br = new BufferedReader(new FileReader(csv));
			
			System.out.println("Defining datatypes for columns at file "+csv.getName());
			fileName = csv.getName().substring(0, csv.getName().length()-4);
			
			columnName= br.readLine().toLowerCase().split(",");
			columnType = br.readLine().toLowerCase().split(",");
			
			for(int i=0;i<columnType.length;i++) {
				if(isNumeric(columnType[i])) {
					dataSourceDataType = new DataSourceDataType(dataTypeIterator,columnName[i],"decimal");
				} else {
					dataSourceDataType = new DataSourceDataType(dataTypeIterator,columnName[i],"string");
				}
				dataTypeIterator++;
				tempDataTypeList.add(dataSourceDataType);
			}
			dataSourceDataTypeFiles.addDataSourceDataType(fileName, tempDataTypeList);
			br.close();
        }
		
		return dataSourceDataTypeFiles;
	}
	
	public void printAllDataSource(DataSourceDataTypeFiles dataSourceDataTypeFiles) {
		for(String fileCSV: dataSourceDataTypeFiles.getAllDataSourceDataTypes().keySet()) {
			ArrayList<DataSourceDataType> test =  dataSourceDataTypeFiles.getDataSourceDataTypes(fileCSV);
			System.out.println(fileCSV);
			for(DataSourceDataType a : test) {
				System.out.println(a.getId() + ". " + a.getColumnName() + "|" + a.getDataType());
			}
			System.out.println("======");
		}
	}
	
	public boolean isNumeric(String strNum) {
	    return strNum.matches("-?\\d+(\\.\\d+)?");
	}
}
