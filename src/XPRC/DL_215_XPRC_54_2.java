package XPRC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.cake.net._useage.Parameter;
import com.cake.net.http.html.htmlparser.Extractor_Property;
import com.cake.net.http.html.htmlparser.Extractor_Result;
import com.cake.net.http.html.htmlparser.HtmlParser;
import com.cake.net.http.connection.URLProxy;
import com.cake.net.http.html.HTML;
import com.cake.net.http.html.HTMLDL;
import com.cake.net.http.html.HTMLX;
import com.tej.error.ErrorTitle;
import com.tej.frame.DownloadFrame;
import com.tej.frame.Table;
import com.tej.setting.IP;


public class DL_215_XPRC_54_2 extends DownloadFrame {
	public Parameter parameter;
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	public DL_215_XPRC_54_2(Parameter parameter){
		this.parameter = parameter;	
	}

	/**
	 * 連結抓取資料處理
	 * 網頁連線 or 規則複雜的資料截取
	 */
	@Override
	public String getData(String url) throws Exception {
//		
		String host = "";
		String port = "";
		String source = "";

//		URLProxy newIP = null;
//		HTML htmlx = null;
		List<String> prox = IP.getIPFromTable();
		for (int i = prox.size(); i >= 0; i--) {
			try{
				HttpHost httpHost = null;
			if (i < prox.size()) {
				String proxy = prox.get(i);
				host = proxy.substring(0, proxy.indexOf(':'));
				port = proxy.substring(proxy.indexOf(':') + 1, proxy.length());
				logger.info("使用IP => " + host + ":" + port + "抓取資料..." + (prox.size() - i) + "/" + prox.size());
//				newIP = new URLProxy(host, port);
			}
				CloseableHttpResponse response = null;
				CloseableHttpClient httpclient = null;
				try {
					httpclient = HttpClients.createDefault();
					HttpPost post = new HttpPost(url);				
					post.addHeader("Referer", "http://www.sse.com.cn/");

					logger.info("設定完畢");
					Builder builder = RequestConfig.custom();
					builder.setConnectTimeout(30 * 1000);
					builder.setSocketTimeout(30 * 1000);
					logger.info("設定TIME OUT");
					post.setConfig(builder.build());
		
					response = httpclient.execute(post);
					BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		
					String output;
		
					StringBuilder sb = new StringBuilder();
					while ((output = br.readLine()) != null) {
						sb.append(output);
					}	
					source = sb.toString();
					if(source!=null) {
						break;
					}
			} catch (Exception e) {
				logger.error(ErrorTitle.CONNECT_TITLE.getTitle(), e);
			}
			finally {
				if(response!=null)response.close();
				if(httpclient!=null)httpclient.close();
			}
			
		}catch (Exception e) {
			logger.warn("抓取失敗!! 轉換ip");
			System.out.print("等待" + 3 + "秒");
			for (int j = 0; j < 3; j++) {
				Thread.sleep(1000);
				System.out.print(".");
			}
				System.out.println();
		}
	}
		return source;

	}
	
	
	
	/**
	 * 連結抓取資料處理
	 * 網頁連線 or 規則複雜的資料截取
	 */
	public String getData2(String url) throws Exception {
//		
		String host = "";
		String port = "";
		String source = "";

//		URLProxy newIP = null;
//		HTML htmlx = null;
		List<String> prox = IP.getIPFromTable();
		for (int i = prox.size(); i >= 0; i--) {
			try{
				HttpHost httpHost = null;
			if (i < prox.size()) {
				String proxy = prox.get(i);
				host = proxy.substring(0, proxy.indexOf(':'));
				port = proxy.substring(proxy.indexOf(':') + 1, proxy.length());
				logger.info("使用IP => " + host + ":" + port + "抓取資料..." + (prox.size() - i) + "/" + prox.size());
//				newIP = new URLProxy(host, port);
			}
				CloseableHttpResponse response = null;
				CloseableHttpClient httpclient = null;
				try {
					httpclient = HttpClients.createDefault();
					HttpPost post = new HttpPost(url);				
					post.addHeader("Referer", "http://www.sse.com.cn/");

					logger.info("設定完畢");
					Builder builder = RequestConfig.custom();
					builder.setConnectTimeout(30 * 1000);
					builder.setSocketTimeout(30 * 1000);
					logger.info("設定TIME OUT");
					post.setConfig(builder.build());
		
					response = httpclient.execute(post);
					BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		
					String output;
		
					StringBuilder sb = new StringBuilder();
					while ((output = br.readLine()) != null) {
						sb.append(output);
					}	
					source = sb.toString();
					if(source!=null) {
						break;
					}
			} catch (Exception e) {
				logger.error(ErrorTitle.CONNECT_TITLE.getTitle(), e);
			}
			finally {
				if(response!=null)response.close();
				if(httpclient!=null)httpclient.close();
			}
			
		}catch (Exception e) {
			logger.warn("抓取失敗!! 轉換ip");
			System.out.print("等待" + 3 + "秒");
			for (int j = 0; j < 3; j++) {
				Thread.sleep(1000);
				System.out.print(".");
			}
				System.out.println();
		}
	}
		return source;

	}
	/**
	 * 分析資料 存入暫存
	 * 網頁資料剖析  , 外部檔欄位
	 */
	@Override
	public Table[] parseListMap(String source) {
		
		
		//表格陣列
		List<Table> tableList= new ArrayList<Table>();
		
		//宣告表格資料暫存陣列
		List<Txssmar> listMap= new ArrayList<Txssmar>();
		
		//此處應只有網頁核心資料已截取過區塊
		//外部檔案讀取可直接由此處讀入並寫入暫存
		
		String zstk_id = parameter.getMenu().get("stk_id") ;


		try{
			
//			System.out.println("s="+source.replace("jsonpCallback64981",""));
			
			JSONObject json = new JSONObject(source.replace("jsonpCallback64981(",""));
			
			JSONArray jsonArray = json.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonItem = jsonArray.getJSONObject(i);
				
				System.out.println("j="+jsonItem);
				
					try{
												
						Txssmar bean = new Txssmar();
						
							bean.setStk_id(zstk_id);
							bean.setZdate(checkDate(jsonItem.optString("opDate")));
							bean.setLong_t(checkBigDecimal(jsonItem.optString("rzye"))); //融資餘額
							bean.setBuy_l(checkBigDecimal(jsonItem.optString("rzmre"))); //融資買入額
							bean.setShort_tv(checkBigDecimal(jsonItem.optString("rqyl"))); //融券餘量
							bean.setShort_t(checkBigDecimal(jsonItem.optString("rqylje"))); //融券餘量金額
							bean.setSell_sv(checkBigDecimal(jsonItem.optString("rqmcl"))); //融券賣出量
							bean.setSb_t(checkBigDecimal(jsonItem.optString("rzrqjyzl"))); //融資融券餘額

//						//分析資料寫入暫存部分
							System.out.println("1="+bean.toString());
						listMap.add(bean);
					}catch(Exception e){
						logger.error(ErrorTitle.CONTENT_TITLE.getTitle(),e);
					}
				}
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
	 * 分析資料 存入暫存
	 * 網頁資料剖析  , 外部檔欄位
	 */

	public Table[] parseListMap2(String source) {
		
		
		//表格陣列
		List<Table> tableList= new ArrayList<Table>();
		
		//宣告表格資料暫存陣列
		List<Txssmar> listMap= new ArrayList<Txssmar>();
		
		//此處應只有網頁核心資料已截取過區塊
		//外部檔案讀取可直接由此處讀入並寫入暫存
		
		String zstk_id = parameter.getMenu().get("stk_id") ;


		try{
			
//			System.out.println("ss="+source.replace("jsonpCallback75445(",""));
			
			JSONObject json = new JSONObject(source.replace("jsonpCallback75445(",""));
			
			JSONArray jsonArray = json.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonItem = jsonArray.getJSONObject(i);
				
//				System.out.println("j="+jsonItem);
				
					try{
												
						Txssmar bean = new Txssmar();
					
							bean.setStk_id(jsonItem.optString("stockCode"));//标的证券代码
							bean.setZdate(checkDate(jsonItem.optString("opDate")));
							bean.setLong_t(checkBigDecimal(jsonItem.optString("rzye"))); //融資餘額
							bean.setBuy_l(checkBigDecimal(jsonItem.optString("rzmre"))); //融資買入額
							bean.setCash_l(checkBigDecimal(jsonItem.optString("rzche"))); //融资偿还额(元)
							bean.setShort_tv(checkBigDecimal(jsonItem.optString("rqyl"))); //融券余量
							bean.setSell_sv(checkBigDecimal(jsonItem.optString("rqmcl"))); //融券賣出量
							bean.setPay_sv(checkBigDecimal(jsonItem.optString("rqchl"))); //融券偿还量

//						//分析資料寫入暫存部分
							System.out.println("2="+bean.toString());
						listMap.add(bean);
					}catch(Exception e){
						logger.error(ErrorTitle.CONTENT_TITLE.getTitle(),e);
					}
				}
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
	
}
