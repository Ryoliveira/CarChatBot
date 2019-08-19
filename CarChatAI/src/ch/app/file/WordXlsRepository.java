package ch.app.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class WordXlsRepository implements WordRepository {

	final private String FILE_NAME = "./InsultWords.xls";

	/**
	 * @return list of insult words with corresponding severity
	 */
	@Override
	public Map<String, Integer> loadFile() {
		Map<String, Integer> wordList = new HashMap<>();
		File file = new File(FILE_NAME);
		POIFSFileSystem fs;
		try {
			fs = new POIFSFileSystem(new FileInputStream(file));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			for (int i = 0; i < rows; i++) {
				String word = sheet.getRow(i).getCell(0).getStringCellValue();
				int severity = (int) sheet.getRow(i).getCell(1).getNumericCellValue();
				wordList.put(word, severity);
			}
			wb.close();
		} catch (IOException e) {
			e.getMessage();
		}
		return wordList;
	}

}
