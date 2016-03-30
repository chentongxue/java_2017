package sacred.alliance.magic.util;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import sacred.alliance.magic.base.XlsSheetNameType;

import com.game.draco.GameContext;
import com.google.common.collect.Lists;

public class XlsPojoUtil {
	final static Logger logger = Log4jManager.CHECK ;

	public static <T extends Piecewise> PiecewiseWrapper<T> createPiecewiseWrapper(
			XlsSheetNameType xls, Class<T> clazz) {
		java.util.List<T> list = XlsPojoUtil.sheetToList(
				GameContext.getPathConfig().getXlsPath() + xls.getXlsName(),
				xls.getSheetName(),clazz);
		return new PiecewiseWrapper(list) ;
	}


	public static <K, V extends KeySupport<K>> Map<K, V> loadMap(
			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		return XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
				clazz, linked);
	}
	
	public static <T> boolean cellToProperty(String value, T message, String cluName){
		String propertyName = cluName;
		PropertyDescriptor prop = null;
		try {
			prop = new PropertyDescriptor(propertyName, message.getClass());
		} catch (Exception ex) {
			prop = null;
		}
		if (null == prop) {
			logger.warn("class: " + message.getClass().getSimpleName() +  " cluName("  + cluName+ ") not exist");
			return false ;
		}

		String type = prop.getPropertyType().getSimpleName();
		try {
			if (!prop.getPropertyType().isArray()) {
				// 数值型，默认值为：0
				if (value.equals("") && !type.equals("String")
						&& !type.equals("Date") && !type.equals("boolean")) {
					//value = "0";
					//直接用POJO中的默认值,不再将默认值设置为0
					return true ;
				} else if (type.equals("boolean")
						&& (value.trim().equals("1") || value.trim()
								.equalsIgnoreCase("true"))) {
					value = "true";
				}
				if (type.equals("int")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Integer(value) });
				} else if (type.equals("long")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Long(value) });
				} else if (type.equals("String")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { value });
				} else if (type.equals("Date")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Date(value) });
				} else if (type.equals("short")) {
					prop.getWriteMethod().invoke(
							message,
							new Object[] { /* new Short(value) */(short) Integer
									.parseInt(value) });
				} else if (type.equals("byte")) {
					prop.getWriteMethod().invoke(
							message,
							new Object[] { /* new Byte(value) */(byte) Short
									.parseShort(value) });
				} else if (type.equals("boolean")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Boolean(value) });
				} else if (type.equals("double")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Double(value) });
				} else if (type.equals("float")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Float(value) });
				}
			}
		}catch(Exception ex){
			Log4jManager.CHECK.error("config value error: class=" 
					+ message.getClass().getSimpleName() + " colName=" + cluName
					+" type=" + type + " value=" + value);
			Log4jManager.checkFail();
			return false ;
		}
		return true;
	}
	
	public static Set<String> sheetToStringSet(String sourceFile,String sheetName){
		ConfigBook book = ConfigBookFactory.getConfigBook();
		try {
			book.open(sourceFile, sheetName);
			int rsRows = book.getRowNum() ;
			int row = 0;
			Set<String> result = new HashSet<String>();
			for (row = 2; row < rsRows; row++) {
				//Cell cell = rs.getCell(0, row);
				//String key = toTrim(cell.getContents());
				String key = toTrim(book.getCellValue(0, row));
				result.add(key);
			}
			return result;
		} catch (Exception e) {
			Log4jManager.checkFail();
			logger.error("sheetToStringSet excel load error ：sourceFile=" + sourceFile +" sheetName=" + sheetName ,e);
		} finally {
			if (null != book) {
				book.close();
			}
		}
		return  new HashSet<String>();
	}
	
	public static List<String> sheetToStringList(String sourceFile,String sheetName){
		ConfigBook book = ConfigBookFactory.getConfigBook();
		try {
			book.open(sourceFile, sheetName);
			int rsRows = book.getRowNum() ;
			int row = 0;
			List<String> result = Lists.newArrayList();
			for (row = 2; row < rsRows; row++) {
				//Cell cell = rs.getCell(0, row);
				//String key = toTrim(cell.getContents());
				String key = toTrim(book.getCellValue(0, row));
				result.add(key);
			}
			return result;
		} catch (Exception e) {
			Log4jManager.checkFail();
			logger.error("sheetToStringList excel load error ：sourceFile=" + sourceFile +" sheetName=" + sheetName ,e);
		} finally {
			if (null != book) {
				book.close();
			}
		}
		return  Lists.newArrayList();
	}

	public static List<List<String>> sheetToList(String sourceFile, String sheetName){
		ConfigBook book = ConfigBookFactory.getConfigBook();
		int curRow = 0;
		int curCol = 0;
		String curKey = "";
		String curColName = "";
		try {
			book.open(sourceFile, sheetName);
			int rsRows = book.getRowNum() ;
			int rsColumns = book.getColNum() ;
			int row = 0, column = 0;
			List<List<String>> result = Lists.newArrayList();
			for (row = 2; row < rsRows; row++) {
				//String key = rs.getCell(0, row).getContents();
				String key = book.getCellValue(0, row);
				curRow = row;
				curKey = key;
				List<String> rowList = Lists.newArrayList();
				for (column = 0; column < rsColumns; column++) {
					curCol = column;
					//curColName = toTrim( rs.getCell(column, 0).getContents());
					curColName = toTrim(book.getCellValue(column, 0));
					if (curColName == null || curColName.equals("")) {
						continue;
					}
					//Cell cell = rs.getCell(column, row);
					//String content = cell.getContents();
					String content = book.getCellValue(column, row);
					String value = toTrim(null == content?"":content);
					rowList.add(value);
				}
				result.add(rowList);
			}
			return result;
		} catch (Exception e) {
			Log4jManager.checkFail();
			logger.error("Excel load error ：sourceFile : " + sourceFile + " sheetName = " + sheetName + " curKey=" + curKey + " curCol=" + curCol + " curRow=" + curRow, e);
		} finally {
			if (book != null){
				book.close();
			}
		}
		return  Lists.newArrayList();
	}
	/**
	 * 默认将EXCEL第一列的值作为KEY
	 * 
	 * @param <T>
	 * @param sourceFile
	 * @param sheetName
	 * @param t
	 * @return
	 */
	private static <T> Map<String, T> sheetToMap(String sourceFile, String sheetName, Class<T> t, boolean linked) {
		ConfigBook book = ConfigBookFactory.getConfigBook();
		int curRow = 0;
		int curCol = 0;
		String curKey = "";
		String curColName = "";
		try {
			book.open(sourceFile, sheetName);
			int rsRows = book.getRowNum() ;
			int rsColumns = book.getColNum() ;
			int row = 0, column = 0;
			Map<String, T> map = null;
			if(linked){
				map = new LinkedHashMap<String,T>();
			}else{
				map = new HashMap<String, T>();
			}
			for (row = 2; row < rsRows; row++) {
				curRow = row;
				T as = t.newInstance();
				boolean put = false ;
				boolean right = true ;
				for (column = 0; column < rsColumns; column++) {
					curCol = column;
					
					//Cell c = rs.getCell(column, 0);
					//curColName = toTrim(c.getContents());
					curColName = toTrim(book.getCellValue(column, 0));
					//第一行、第一列为空
					if (curColName == null || 0 == curColName.trim().length()) {
						continue;
					}

					//Cell cell = rs.getCell(column, row);
					//String value = cell.getContents();
					String value = book.getCellValue(column, row) ;
					
					value = (null== value)? "" : value.trim();
					if(value.length() > 0){
						put = true ;
					}
					//对象(属性名) 值 和列名  ------把值封装进对象  
					if(!cellToProperty(value, as, curColName)){
						right = false ;
					}
				}
				if(put){
					//Cell cell = rs.getCell(0, row);
					//String key = cell.getContents();
					String key = book.getCellValue(0, row);
					if(null == key || 0 == key.trim().length()){
						continue ;
					}
					if(!right){
						Log4jManager.checkFail();
						logger.error("have error, key:" + key +" in file:" + sourceFile + " sheet:" + sheetName);
					}
					if(map.containsKey(key)){
						Log4jManager.checkFail();
						logger.error("have same key:" + key +" in file:" + sourceFile + " sheet:" + sheetName);
					}
					map.put(key, as);
				}
			}
			return map;
		} catch (Exception e) {
			Log4jManager.checkFail();
			logger.error("excel load error ：sourceFile=" + sourceFile +" sheetName=" + sheetName +
					" curKey=" + curKey + " curCol=" + curCol + " curRow=" + curRow + " curColName=" + curColName,e);
		} finally {
			if (null != book) {
				book.close();
			}
		}
		return new HashMap<String, T>();
	}

	public static <K,T extends KeySupport<K>> Map<K, T> sheetToGenericLinkedMap(String sourceFile,String sheetName,Class<T> t){
		return sheetToGenericMap(sourceFile, sheetName, t, true);
	}
	
	public static <K,T extends KeySupport<K>> Map<K, T> sheetToGenericMap(String sourceFile,String sheetName,Class<T> t){
		return sheetToGenericMap(sourceFile, sheetName, t, false);
	}
	
	public static <K, T extends KeySupport<K>> Map<K, List<T>> sheetToMapList(String sourceFile, 
			String sheetName, Class<T> t,boolean linkedMap) {
		Map<K, List<T>> ret = null ;
		if(linkedMap){
			ret = new LinkedHashMap<K, List<T>>();
		}else {
			ret = new HashMap<K,List<T>>();
		}
		List<T> allList = sheetToList(sourceFile, sheetName, t);
		if (isEmpty(allList)) {
			return ret;
		}
		for (T item : allList) {
			if (null == item) {
				continue;
			}
			K key = item.getKey();
			List<T> itemList = ret.get(key);
			if (null == itemList) {
				itemList = new ArrayList<T>();
				ret.put(key, itemList);
			}
			itemList.add(item);
		}
		return ret;
	}
	
	
	private static boolean isEmpty(List list){
		return null == list || 0 == list.size() ;
	}
	
	/**
	 * 根据指定的列值作为KEY
	 * 
	 * @param <T>
	 * @param sourceFile
	 * @param sheetName
	 * @param t
	 * @param keyLists
	 * @return
	 */
	public static <K,T extends KeySupport<K>> Map<K,T> sheetToGenericMap(String sourceFile,String sheetName,Class<T> t, boolean linked) {
		ConfigBook book = ConfigBookFactory.getConfigBook();
		int curRow = 0;
		int curCol = 0;
		String curKey = "";
		String curColName = "";
		try {
			book.open(sourceFile, sheetName);
			
			int rsRows = book.getRowNum() ;
			int rsColumns = book.getColNum() ;
			int row = 0, column = 0;
			Map<K,T> map = null;
			if(linked){
				map = new LinkedHashMap<K,T>();
			}else{
				map = new HashMap<K, T>();
			}
			for (row = 2; row < rsRows; row++) {
				curRow = row;
				T as = t.newInstance();
				boolean put = false ;
				boolean right = true ;
				for (column = 0; column < rsColumns; column++) {
					curCol = column;
					
					//Cell c = rs.getCell(column, 0);
					//curColName = toTrim(c.getContents());
					curColName = toTrim(book.getCellValue(column, 0));
					
					// 第一行、第一列为空
					if (curColName == null || curColName.equals("")) {
						continue;
					}
					
					//Cell cell = rs.getCell(column, row);
					//String value = cell.getContents();
					
					String value = book.getCellValue(column, row) ;
					
					value = (null== value)? "" : value.trim();
					if(value.length() > 0){
						put = true ;
					}
					// 对象(属性名) 值 和列名 ------把值封装进对象
					if(!cellToProperty(value, as, curColName)){
						right = false ;
					}
				}
				if(put){
					if(!right){
						Log4jManager.checkFail();
						logger.error("have error, key:" + as.getKey() +" in file:" + sourceFile + " sheet:" + sheetName);
					}
					if(map.containsKey(as.getKey())){
						Log4jManager.checkFail();
						logger.error("have same key:" + as.getKey() +" in file:" + sourceFile + " sheet:" + sheetName);
					}
					map.put(as.getKey(), as);
				}
			}
			return map;
		} catch (Exception e) {
			Log4jManager.checkFail();
			logger.error("excel load error ：sourceFile=" + sourceFile +" sheetName=" + sheetName +
					" curKey=" + curKey + " curCol=" + curCol + " curRow=" + curRow + " curColName=" + curColName,e);
		} finally {
			if (null != book) {
				book.close();
			}
		}
		return new HashMap<K, T>();
	}

	public static <T> List<T> sheetToList(String sourceFile, String sheetName, Class<T> t) {
		ConfigBook book = ConfigBookFactory.getConfigBook();
		int curRow = 0;
		int curCol = 0;
		String curKey = "";
		String curColName = "";
		try {
			book.open(sourceFile, sheetName);
			
			int rsRows = book.getRowNum() ;
			int rsColumns = book.getColNum() ;
			int row = 0, column = 0;
			List<T> list = new ArrayList<T>();
			for (row = 2; row < rsRows; row++) {
				T as = t.newInstance();
				boolean put = false ;
				//String key = rs.getCell(0, row).getContents();
				String key = book.getCellValue(0, row) ;
				curRow = row;
				curKey = key;
				for (column = 0; column < rsColumns; column++) {
					curCol = column;
					
					//Cell c = rs.getCell(column, 0);
					//String cumName = toTrim(c.getContents());
					String cumName = toTrim(book.getCellValue(column, 0));
					curColName = cumName;
					//第一行、第一列为空
					if (cumName == null || cumName.equals("")) {
						continue;
					}
	
					//Cell cell = rs.getCell(column, row);
					//String value = cell.getContents();
					
					String value = book.getCellValue(column, row);
					
					value = (null== value)? "" : value.trim();
					if(value.length() > 0){
						put = true ;
					}
					//对象(属性名) 值 和列名  ------把值封装进对象  
					cellToProperty(value, as, cumName);
				}
				if(put){
					list.add(as);
				}
				
			}
			return list;
		} catch (Exception e) {
			Log4jManager.checkFail();
			logger.error("excel load error ：sourceFile=" + sourceFile +" sheetName=" + sheetName +
					" curKey=" + curKey + " curCol=" + curCol + " curRow=" + curRow + " curColName=" + curColName,e);
		} finally {
			if (book != null)
				book.close();
		}
		return new ArrayList<T>();
	}
	
	public static <T> Map<String, T> sheetToLinkedMap(String sourceFile, String sheetName, Class<T> t){
		return sheetToMap(sourceFile,sheetName,t,true);
	}
	
	public static <T> Map<String, T> sheetToMap(String sourceFile, String sheetName, Class<T> t){
		return sheetToMap(sourceFile,sheetName,t,false);
	}
	
	public static <T> T getEntity(String sourceFile, String sheetName, Class<T> t ){
		List<T> list = sheetToList(sourceFile,sheetName,t);
		if(Util.isEmpty(list)){
			return null ;
		}
		return list.get(0);
	}
	

	/**
	 * 去掉字符串的前后空格
	 * 
	 * @param str
	 * @return
	 */
	private static String toTrim(String str) {
		if (str != null) {
			str = str.trim();
		}
		return str;
	}
}