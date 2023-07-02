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
import java.util.TreeMap;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.impl.ControlFieldImpl;

/**/
public class IhpAuthority {
	final static String DIR_NAME = "/home/marina/authority/";//"/home/adi/ihp/authority/";"D:\\Work\\ihp\\";
	static File inputFile = getXmlFile();
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
	static LocalDateTime now = LocalDateTime.now();
	static File outputFile = new File(DIR_NAME + inputFile.getName().substring(0, inputFile.getName().indexOf(".xml")) + "_" + dtf.format(now) + ".txt");
//	final static File inputFile = new File("C:\\Users\\ajacobsmo\\Desktop\\ihp\\AUTHORITY_46499859700002791_1_HAICHI.xml");
//	final static File outputFile = new File("C:\\Users\\ajacobsmo\\Desktop\\ihp\\AUTHORITY_46499859700002791_1_HAICHI" + dtf.format(now) + ".txt");
//	final static File inputFile = new File("D:\\Work\\ihp\\BIBLIOGRAPHIC_6475704930002792_1.xml");
//	final static File outputFile = new File("D:\\Work\\ihp\\BIBLIOGRAPHIC_6475704930002792_1" + dtf.format(now) + ".txt");
	static HashMap<String, Integer> tagToMaxOfTimes = new HashMap<String, Integer>();
//	static HashMap<String, Integer> tagToMaxOfTimes = new HashMap<String, Integer>() {{
//	    put("150", 0);
//	    put("450", 0);
//	    put("550", 0);
//	    put("100", 0);
//	}};
//	static HashMap<String, Integer> tagToMaxOfTimes = new HashMap<String, Integer>() {{
//	    put("100", 0);
//	    put("400", 0);
//	    put("500", 0);
//	    put("700", 0);
//	    put("110", 0);
//	    put("410", 0);
//	    put("510", 0);
//	    put("710", 0);
//	    put("111", 0);
//	    put("411", 0);
//	    put("511", 0);
//	    put("711", 0);
//	    put("130", 0);
//	    put("430", 0);
//	    put("530", 0);
//	    put("730", 0);
//	    put("150", 0);
//	    put("450", 0);
//	    put("550", 0);
//	    put("750", 0);
//	    put("151", 0);
//	    put("451", 0);
//	    put("551", 0);
//	    put("751", 0);
//	}};

	public static void main(String[] args) {
		try {
			for(String arg : args) {
				tagToMaxOfTimes.put(arg, 0);
			}
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
			TreeMap<String, Integer> sortedHash = new TreeMap<>(tagToMaxOfTimes);
			for (Map.Entry<String, Integer> set : sortedHash.entrySet()) {
				int index = set.getValue();
				for(int i=0; i<index; i++) {
					sb.append(set.getKey());
					sb.append(";");
				}
			}
			String titles = sb.toString().endsWith(";") ? sb.toString().substring(0, sb.toString().length()-1) : sb.toString();
			String[]arrTiltes = titles.split(";"); 
			writer.write(titles + System.lineSeparator());
			in.close();
			
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			SXSSFSheet worksheet = workbook.createSheet("AUTHORITY");        
			SXSSFRow rowTitle = worksheet.createRow(0);
	        
	        // set  titles	        
	        for(int i=0;i<arrTiltes.length;i++){
	        	SXSSFCell cellTitle = rowTitle.createCell(i);
	            cellTitle.setCellValue(arrTiltes[i]);	
	            //rowTitle.createCell(i).setCellValue(arrTiltes[i]);
	        }

			in = new FileInputStream(inputFile);
			MarcReader reades = new MarcXmlReader(in);
			int lineNum = 1;
			//Reads all records
			while (reades.hasNext()) {
                Record record = reades.next();
                SXSSFRow rowValue = worksheet.createRow(lineNum++);
                int cell=0;
//                XSSFRow bodyRow = spreadsheet.createRow(lineNum);
                sb = new StringBuilder();
                String id = record.getVariableField("001") != null ? ((ControlFieldImpl)record.getVariableField("001")).getData() : null;
                if(id == null) {
                	System.out.println("Problem in record " + record.getId() + " no 001 tag field");
                	continue;
                }
                if("9819205915802791".equalsIgnoreCase(id)) {
                	continue;
                }
//                bodyRow.createCell(0).setCellValue(id);
                SXSSFCell cell0 = rowValue.createCell(cell);
                cell0.setCellValue(id);
                cell++;
                sb.append(id);
                sb.append(";");
                //List<DataField> dataFields = record.getDataFields();
//                int cellNum = 1; 
                for (Map.Entry<String, Integer> entry : sortedHash.entrySet()) {
                	List<VariableField> tagInstances = record.getVariableFields(entry.getKey());
                	for(VariableField tagInstance : tagInstances) {
                		StringBuilder sbe = new StringBuilder();
                		DataField df = (DataField)tagInstance;
                    	String tag = df.getTag();
                		//sb.append(tag);
                		List<Subfield> subFields = df.getSubfields();
                		for(Subfield sf : subFields) {
                			sb.append(sf.getCode());
                			sbe.append(sf.getCode());
                			sb.append("--");
                			sbe.append("--");
                			sb.append(sf.getData());
                			sbe.append(sf.getData());
                			sb.append("@");
                			sbe.append("@");
                		}
                		sb.deleteCharAt(sb.length() -1);
                		sbe.deleteCharAt(sbe.length() -1);
                		rowValue.createCell(cell).setCellValue(sbe.toString());
                		cell++;
//                		bodyRow.createCell(cellNum).setCellValue(sb.toString());
//                		cellNum++;
                		sb.append(";");
                	}
                	int numOfEmptyCell = entry.getValue() - tagInstances.size();
                	while(numOfEmptyCell > 0) {
                		numOfEmptyCell--;
//                		bodyRow.createCell(cellNum).setCellValue(" ");
//                		cellNum++;
                		rowValue.createCell(cell).setCellValue(" ");
                		cell++;
                		sb.append(" ");
                		sb.append(";");
                	}
                }
                String str = sb.toString().endsWith(";") ? sb.toString().substring(0, sb.toString().length()-1) : sb.toString();
        		writer.write(str + System.lineSeparator());

        		//FileOutputStream out = new FileOutputStream(new File("C:\\Users\\ajacobsmo\\Desktop\\ihp\\savedexcel\\GFGsheet.xls"));
//        		FileOutputStream out = new FileOutputStream(new File(DIR_NAME + inputFile.getName().substring(0, inputFile.getName().indexOf(".xml")) + "_" + dtf.format(now) + ".xlsx"));
//        		workbook.write(out);
//        		out.flush();
//        		out.close();
        		//workbook.close();
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
			FileOutputStream out = new FileOutputStream(new File(DIR_NAME + inputFile.getName().substring(0, inputFile.getName().indexOf(".xml")) + "_" + dtf.format(now) + ".xlsx"));
    		workbook.write(out);
    		out.flush();
    		out.close();
    		workbook.close();
			writer.close();
			System.out.println("finished");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/*init hashmap to contain each tag with its max time of appearance in order to build excel columns*/
	private static void initMaxTagsAppearance(MarcReader r) {
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
	
	private static File getXmlFile() {
		File file = null;
		File folder = new File(DIR_NAME);
		File[] listOfFiles = folder.listFiles();

		for (int i=0; i<listOfFiles.length; i++){
		  if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".xml")){
			  file = listOfFiles[i];
		  }
		}
		return file;
	}

}
