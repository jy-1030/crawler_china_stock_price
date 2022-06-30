package XPRC;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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


public class DL_215_XPRC_54 extends DownloadFrame {
	public Parameter parameter;
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	public DL_215_XPRC_54(Parameter parameter){
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
				
				if(line.replace("#tej#", "").equals(""))
					continue;
			
				if (ishead) {
					String column[] = (line+" ").split("#tej#");
					System.out.println(line);
					//資料回圈中每筆資料處理都要try..catch避免剖析段中斷
					try{
						
						
						Txssmar bean = new Txssmar();
						if(flag.equals("1")) {
							bean.setStk_id(zstk_id);
							
						}else {
							
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
				Sheet mySheet = myWorkBook.getSheet(myWorkBook.getSheetAt(x).getSheetName());
				
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
				int a = DL.download();
				if(a < 0) {
					logger.error(ErrorTitle.DOWNLOAD_TITLE.getTitle("下載檔案失敗"));
				}
			}catch(Exception e){
				logger.error(ErrorTitle.DOWNLOAD_TITLE.getTitle(),e);
				e.printStackTrace();
			}
		}
		
		public void dolXls(String url, String filenm)throws Exception {
		   
			String spt = System.getProperty("file.separator");
			String path = System.getProperty("user.dir") + spt + "data" + spt + filenm; //下載至data資料夾
//		    TEJ_215_XPRC_54_2 web = new TEJ_215_XPRC_54_2();

			//設定輸出的檔案
			File outFile = new File(path);
			try(CloseableHttpClient httpclient = HttpClients.createDefault();){
				
				HttpGet httpGet= new HttpGet(url);
				CloseableHttpResponse response = httpclient.execute(httpGet);
				HttpEntity responseEntity = response.getEntity();
				
				InputStream is = responseEntity.getContent();
				
				FileUtils.copyInputStreamToFile(is, outFile);
			}catch(Exception ex) {
				//第一次出錯，改成續傳
				logger.warn("HttpError, Try to resume download...");
				int retry=3; //最多三次
				while(retry > 0) {
					retry--;
					
					try {
						ResumeStatus s =  resume(outFile,url);
						FileUtils.writeByteArrayToFile(outFile, s.getOutput(),true);
						
						if(s.finished)
							break;
						
					} catch (IOException e) {
						//續傳仍出錯
						logger.error("Resume Error...",ex);
					}
				}
			}
			
			logger.info("Finished.");
		}
		
		/**
		 * 續傳
		 * @param resumeFile
		 * @return
		 * @throws IOException
		 */
		private static ResumeStatus resume(File resumeFile , String url) throws IOException {
			try(CloseableHttpClient httpclient = HttpClients.createDefault();){
				
				HttpGet httpGet= new HttpGet(url);
				httpGet.setHeader("Range","bytes="+resumeFile.length()+"-");
				
				CloseableHttpResponse response = httpclient.execute(httpGet);
				HttpEntity responseEntity = response.getEntity();
				
				InputStream is = responseEntity.getContent();
				
				return readInputStream(is);

			}catch(Exception ex) {
				
				logger.error("HttpResume Error",ex);
				throw new IOException("HttpResume Error");
			}
		}
		/**
		 * 讀取InputStream 到byteArray
		 * @param in
		 * @return
		 */
		private static ResumeStatus readInputStream(InputStream in) {
			
			boolean finished=false;
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    byte[] data = new byte[1024];
		    int x = 0;
		    
		    while (true) {
		    	try {
					x = in.read(data, 0, 1024);
			    	if(x<0){
			    		finished=true;
			    		throw new IOException("EOF");
			    	}
				} catch (IOException e) {
					break;
				}

		    	out.write(data, 0, x);
		    }
		    ResumeStatus s = new DL_215_XPRC_54.ResumeStatus();
		    s.setOutput(out.toByteArray());
		    s.setFinished(finished);
		    return s;
		}
		
		//紀錄狀態
		private static class ResumeStatus{
			private byte[] output;
			private boolean finished=false;
			public byte[] getOutput() {
				return output;
			}
			public void setOutput(byte[] output) {
				this.output = output;
			}
			public boolean isFinished() {
				return finished;
			}
			public void setFinished(boolean finished) {
				this.finished = finished;
			}
		}
}
