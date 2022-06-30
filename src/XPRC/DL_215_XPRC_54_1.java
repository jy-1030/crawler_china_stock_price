package XPRC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.cake.net._useage.Parameter;
import com.cake.net.http.html.HTMLDL;
import com.tej.error.ErrorTitle;
import com.tej.frame.DownloadFrame;
import com.tej.frame.Table;

public class DL_215_XPRC_54_1 extends DownloadFrame {
	public Parameter parameter;
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	public DL_215_XPRC_54_1(Parameter parameter){
		this.parameter = parameter;	
	}

	/**
	 * 連結抓取資料處理
	 * 網頁連線 or 規則複雜的資料截取
	 */
	@Override
	public String getData(String url) throws Exception {
		return "";
	}
	/**
	 * 分析資料 存入暫存
	 * 網頁資料剖析  , 外部檔欄位
	 */
	@Override
	public Table[] parseListMap(String filePath, String filename) {
		
		
		//表格陣列
		List<Table> tableList= new ArrayList<Table>();
		
		//宣告表格資料暫存陣列
		List<Txssmar> listMap= new ArrayList<Txssmar>();
		
		//此處應只有網頁核心資料已截取過區塊
		//外部檔案讀取可直接由此處讀入並寫入暫存

		// excel資料陣列s
		List<String> datas = new ArrayList<String>();
		String path = filePath + filename;
		String title = parameter.getMenu().get("title") ;
		String title1 = parameter.getMenu().get("title1") ;
		String zstk_id = parameter.getMenu().get("stk_id") ;
		String zdate = filename.split(".xls")[0] ;
		String flag = "1" ;
		try{
			datas = readXlsx(path);
			boolean ishead = false;
			for (String line : datas) {
				
				
				if (line.startsWith(title)) {
					ishead = true;
					continue;
				}
			
				if (line.startsWith(title1)) {
					flag = "2" ;
					ishead = true;
					continue;
				}
				
				if(line.replace("#tej#", "").equals("") || line.startsWith("注：") || line.contains("sheetName"))
					continue;
			
				if (ishead) {
					String column[] = (line+" ").split("#tej#");
//					System.out.println("l="+line);
					//資料回圈中每筆資料處理都要try..catch避免剖析段中斷
					try{
												
						Txssmar bean = new Txssmar();
						if(flag.equals("1")) {
							bean.setStk_id(zstk_id);
							bean.setZdate(checkDate(zdate));
							bean.setLong_t(checkBigDecimal(column[0]));
							bean.setBuy_l(checkBigDecimal(column[1]));
							bean.setShort_tv(checkBigDecimal(column[2]));
							bean.setShort_t(checkBigDecimal(column[3]));
							bean.setSell_sv(checkBigDecimal(column[4]));
							bean.setSb_t(checkBigDecimal(column[5]));
							
						}else {
							bean.setStk_id(column[0]);
							bean.setZdate(checkDate(zdate));
							bean.setLong_t(checkBigDecimal(column[2]));
							bean.setBuy_l(checkBigDecimal(column[3]));
							bean.setCash_l(checkBigDecimal(column[4]));
							bean.setShort_tv(checkBigDecimal(column[5]));
							bean.setSell_sv(checkBigDecimal(column[6]));
							bean.setPay_sv(checkBigDecimal(column[7]));
						}
						//分析資料寫入暫存部分
						
						listMap.add(bean);
					}catch(Exception e){
						logger.error(ErrorTitle.CONTENT_TITLE.getTitle(line),e);
					}
				}
			}
			
			if(!ishead)
				logger.error(ErrorTitle.CONTENT_TITLE.getTitle("表頭異動,無法匯入資料"));
			
		}catch(Exception e){
			//網頁轉換 或 其餘例外錯誤
			logger.error(ErrorTitle.ANALYSIS_TITLE.getTitle(),e);
		}finally{
			tableList.add(new Table(listMap.toArray(new Txssmar[0]) , "表格註解"));
		}
		
		//此function例外錯誤由抽象類別(DownloadFrame)統一回傳
		
		return tableList.toArray(new Table[0]);
	}
	/**
	 * 檢查日期格式
	 * 
	 * @param d
	 * @return
	 * @throws ParseException
	 */
	private java.sql.Date checkDate(String d) throws ParseException {
		if (d.trim().equals(""))
			return null;
		else
			return new java.sql.Date(sdf.parse(d).getTime());
	}
	
	
	/**
	 * 讀取xls & xlsx 將資料拼起來，回傳清單陣列
	 * 
	 * @param path
	 * 
	 */
	public List<String> readXlsx(String path) {
		Workbook myWorkBook = null;
		FileInputStream fis = null;
		List<String> number = new ArrayList<String>();
		String value = "";
		try {
			File myFile = new File(path);
			fis = new FileInputStream(myFile);

			myWorkBook = WorkbookFactory.create(fis);
			FormulaEvaluator evaluator = myWorkBook.getCreationHelper().createFormulaEvaluator();
			evaluator.evaluateAll();
			
			for (int x = 0; x < myWorkBook.getNumberOfSheets(); x++) {
				Sheet mySheet = myWorkBook.getSheetAt(x);
				number.add("目前sheetName="+myWorkBook.getSheetAt(x).getSheetName());
				for (int i = 0; i <= mySheet.getLastRowNum(); i++) {
					Row row = (Row) mySheet.getRow(i);
					if(row == null ) {
						continue ;
					}
					String rowLine = "";
					for (int j = 0; j < row.getLastCellNum(); j++) {
						try {
							Cell cell = (Cell) row.getCell(j);
							// value =
							// formatter.formatCellValue(cell,evaluator).trim();
							value = getCellValue(cell, evaluator).trim();
							rowLine += value + "#tej#";

						} catch (Exception e) {
							logger.error(ErrorTitle.CONTENT_TITLE.getTitle(String.format("第%s欄位處理錯誤:%s", (j + 1), value)),
									e);
						}

					} // end cell

					// System.out.println(rowLine);
					number.add(rowLine);
				} // end row
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (myWorkBook != null)
					myWorkBook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return number;
	}

	private SimpleDateFormat DtFormat = new SimpleDateFormat("yyyyMMdd");
	private DataFormatter formatter = new DataFormatter();

	public String getCellValue(Cell cell, FormulaEvaluator evaluator) {
		// System.out.println("celldata="+cell);
		if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
			return DtFormat.format(cell.getDateCellValue()).toString();
		}
		// System.out.println(cell+"~stype="+cell.getCellStyle().getDataFormat()
		// );

		if (cell != null && cell.getCellStyle().getDataFormatString() != null
				&& cell.getCellTypeEnum() == CellType.NUMERIC
				&& (cell.getCellStyle().getDataFormat() == 31 || cell.getCellStyle().getDataFormat() == 14)) {
			return DtFormat.format(cell.getDateCellValue()).toString();
		}
		return formatter.formatCellValue(cell, evaluator);
	}	
	
	/**
	 * BigDecimal 格式檢查
	 * 
	 * @param d
	 * @return
	 */
	private BigDecimal checkBigDecimal(String d) {

		d = d.replace(",", "").trim().replaceAll("[A-Za-z]", "");
		if (d.replace("-", "").trim().equals(""))
			return null;
		else
			return new BigDecimal(d);
	}
	
	
	//download(網址，網名)
		public void download(String url , String filenm) {
			//斜線正反
			String spt = System.getProperty("file.separator");
			try{
				HTMLDL	DL = new HTMLDL(url, false);
				DL.setIgnoreSSLCertificate(true);
				//DL.setDownloadParameter(路徑, 檔名);
				DL.setDownloadParameter(System.getProperty("user.dir")+spt+"data", filenm);
				DL.download();
			}catch(Exception e){
				logger.error(ErrorTitle.DOWNLOAD_TITLE.getTitle(),e);
				e.printStackTrace();
			}
		}
}
