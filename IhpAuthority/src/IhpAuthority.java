import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.impl.ControlFieldImpl;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class IhpAuthority {
	
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
	static LocalDateTime now = LocalDateTime.now();
//	final static File inputFile = new File("C:\\Users\\ajacobsmo\\Desktop\\ihp\\AUTHORITY_46499859700002791_1_HAICHI.xml");
//	final static File outputFile = new File("C:\\Users\\ajacobsmo\\Desktop\\ihp\\AUTHORITY_46499859700002791_1_HAICHI" + dtf.format(now) + ".txt");
	final static File inputFile = new File("C:\\Users\\ajacobsmo\\Desktop\\ihp\\IHP10_Authority_file.xml");
	final static File outputFile = new File("C:\\Users\\ajacobsmo\\Desktop\\ihp\\IHP10_Authority_file" + dtf.format(now) + ".txt");
//	static HashMap<String, Integer> tagToMaxOfTimes = new HashMap<String, Integer>() {{
//	    put("150", 0);
//	    put("450", 0);
//	    put("550", 0);
//	    put("100", 0);
//	}};
	static HashMap<String, Integer> tagToMaxOfTimes = new HashMap<String, Integer>() {{
	    put("100", 0);
	    put("400", 0);
	    put("500", 0);
	    put("700", 0);
	    put("110", 0);
	    put("410", 0);
	    put("510", 0);
	    put("710", 0);
	    put("111", 0);
	    put("411", 0);
	    put("511", 0);
	    put("711", 0);
	    put("130", 0);
	    put("430", 0);
	    put("530", 0);
	    put("730", 0);
	    put("150", 0);
	    put("450", 0);
	    put("550", 0);
	    put("750", 0);
	    put("151", 0);
	    put("451", 0);
	    put("551", 0);
	    put("751", 0);
	}};

	public static void main(String[] args) {
		try {
			outputFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, StandardCharsets.UTF_8));
			//BufferedReader r = new BufferedReader(new FileReader(inputFile, StandardCharsets.UTF_8));
			//FileWriter writer = new FileWriter(outputFile);
			InputStream in = new FileInputStream(inputFile);
			//MarcReader reader = new MarcStreamReader(in, "UTF8");
			MarcReader r = new MarcXmlReader(in);
			initMaxTagsAppearance(r);
			StringBuilder sb = new StringBuilder();
			sb.append("001");
			sb.append(";");
			for (Map.Entry<String, Integer> set : tagToMaxOfTimes.entrySet()) {
				int index = set.getValue();
				for(int i=0; i<index; i++) {
					sb.append(set.getKey());
					sb.append(";");
				}
			}
			String s = sb.toString().endsWith(";") ? sb.toString().substring(0, sb.toString().length()-1) : sb.toString();
			writer.write(s + System.lineSeparator());
			in.close();
//			// workbook object
//			XSSFWorkbook workbook = new XSSFWorkbook();
//			XSSFSheet spreadsheet = workbook.createSheet("AUTHORITY");
//			XSSFRow row;
//			Map<String, Object[]> data = new TreeMap<String, Object[]>();
//			data.put("1", new Object[] { s.split(";") });
////	        XSSFSheet spreadsheet = workbook.createSheet("IhpAuthority");
////	        XSSFRow row;
			in = new FileInputStream(inputFile);
			MarcReader reades = new MarcXmlReader(in);
			//Reads all records
			while (reades.hasNext()) {
                Record record = reades.next();
                sb = new StringBuilder();
                String id = record.getVariableField("001") != null ? ((ControlFieldImpl)record.getVariableField("001")).getData() : null;
                if(id == null) {
                	System.out.println("Problem in record " + record.getId() + " no 001 tag field");
                	continue;
                }
                if("9819205915802791".equalsIgnoreCase(id)) {
                	continue;
                }
                sb.append(id);
                sb.append(";");
                //List<DataField> dataFields = record.getDataFields();
                for (Map.Entry<String, Integer> entry : tagToMaxOfTimes.entrySet()) {
                	List<VariableField> tagInstances = record.getVariableFields(entry.getKey());
                	for(VariableField tagInstance : tagInstances) {
                		DataField df = (DataField)tagInstance;
                    	String tag = df.getTag();
                		//sb.append(tag);
                		List<Subfield> subFields = df.getSubfields();
                		for(Subfield sf : subFields) {
                			sb.append(sf.getCode());
                			sb.append("--");
                			sb.append(sf.getData());
                			sb.append("@");
                		}
                		sb.deleteCharAt(sb.length() -1);
                		sb.append(";");
                	}
                	int numOfEmptyCell = entry.getValue() - tagInstances.size();
                	while(numOfEmptyCell > 0) {
                		numOfEmptyCell--;
                		sb.append(" ");
                		sb.append(";");
                	}
                }
                String str = sb.toString().endsWith(";") ? sb.toString().substring(0, sb.toString().length()-1) : sb.toString();
        		writer.write(str + System.lineSeparator());
//        		Set<String> keyid = data.keySet();
//        		int rowid = 0;
//        		for(String key : keyid) {
//        			row = spreadsheet.createRow(rowid++);
//        			Object[] objectArr = data.get(key);
//        			int cellid = 0;
//        			for (Object obj : objectArr) {
//        				Cell cell = row.createCell(cellid++);
//        				cell.setCellValue((String)obj);
//        			}
//        		}
//        		FileOutputStream out = new FileOutputStream(new File("C:\\Users\\ajacobsmo\\Desktop\\ihp\\savedexcel\\GFGsheet.xlsx"));
//        		workbook.write(out);
//        		out.close();
//        		workbook.close();
                /*Working good split under for new sulotion 
                List<DataField> dataFields = record.getDataFields();
                for(DataField df : dataFields) {
                	String tag = df.getTag();
                	//if(tag.length() == 3 && (tag.startsWith("1") || tag.startsWith("4") || tag.startsWith("7"))) {
                	if(tag.equalsIgnoreCase("150") || tag.equalsIgnoreCase("450") || tag.equalsIgnoreCase("550") || tag.equalsIgnoreCase("100")) {
                		String outputData = id + ";" + tag + ";";
                		StringBuilder sb = new StringBuilder(outputData);
                		List<Subfield> subFields = df.getSubfields();
                		for(Subfield sf : subFields) {
                			sb.append(sf.getCode());
                			sb.append(";");
                			sb.append(sf.getData());
                			sb.append(";");
                		}
                		String s = sb.toString().endsWith(";") ? sb.toString().substring(0, sb.toString().length()-1) : sb.toString();
                		writer.write(s + System.lineSeparator());
                	}
                }*/
                
           } 
			writer.close();
			System.out.println("finished");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/*init hashmap to contain each tag with its max time of appearance in order to build excel columns*/
	public static void initMaxTagsAppearance(MarcReader r) {
		while (r.hasNext()) {
            Record record = r.next();
            for (Map.Entry<String, Integer> entry : tagToMaxOfTimes.entrySet()) {
            	int tagInstances = record.getVariableFields(entry.getKey()).size();
            	if(tagInstances > entry.getValue()) {
            		tagToMaxOfTimes.replace(entry.getKey(), tagInstances);
            	}
            }
		}
	}

}
