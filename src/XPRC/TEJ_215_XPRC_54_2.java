package XPRC;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.log4j.xml.DOMConfigurator;
import com.cake.net._useage.Parameter;
import com.tej.date.ComputeDates;
import com.tej.date.TradingDay;
import com.tej.error.ErrorTitle;
import com.tej.frame.PorcessFrame;
import com.tej.frame.Table;
import com.tej.postgresql.DBAdminConnector;
import com.tej.postgresql.connection.ConnectorTableBuilder;
import com.tej.postgresql.connection.DBConnector;
import com.tej.postgresql.connection.IDBConnector;

/**
 * 獨立開發程式
 * @author : 
 *
 */
public class TEJ_215_XPRC_54_2 extends PorcessFrame{
	public void taskDescription(Parameter param , IDBConnector market,String[] args){
		super.parameter = param;  //參數檔
		super.market    = market; //匯入連線資訊
		
		String url = parameter.getMenu().get("url_s");
		String url2 = parameter.getMenu().get("url_l");
	    String tableName = parameter.getTableName();
		String filePath = parameter.getOutdir();
		
	    DL_215_XPRC_54_2 dl = new DL_215_XPRC_54_2(parameter);
	    List<String> dayList = null;

	    
	    
	    //第一段 : 抓檔 與 網頁處理(若讀取外部檔匯入可以不需此段)

	    String source = "";
	    String source2 = "";
	    
	    
	    
//		  //此段 輸入日期參數	  
	    if(args.length == 0){
//	    	System.out.println("[WarningMessage]無輸入參數,抓取前一交易日!!");
	    	dayList = new ArrayList<String>();
	    	dayList.add(TradingDay.getCommonPropety("chn_date"));
//	    	dayList.add(calendar.today());
	    }else if(args.length == 1){
	    	if(args[0].matches("[0-9]{8}")){
	    		dayList = new ArrayList<String>();
	    		dayList.add(args[0]);
	    	}else{
				logger.error(ErrorTitle.ANALYSIS_TITLE.getTitle("日期輸入錯誤。"));
	      	}
	    }else if(args.length == 2){
      		if(args[0].matches("[0-9]{8}") && args[1].matches("[0-9]{8}")){
//      			ComputeDates cpDate = new ComputeDates();
      			dayList = ComputeDates.days(args[0], args[1]);
      		}else{
				logger.error(ErrorTitle.ANALYSIS_TITLE.getTitle("日期輸入錯誤。"));
      		}
      	}
	    
	    
	    for(String day : dayList){
	    try{
	    	logger.info("網址 ："+url.replace("[date]",day));
	    	logger.info("網址 ："+url2.replace("[date]",day));
	    	
	    	source = dl.getData(url.replace("[date]",day));
	    	source2 = dl.getData2(url2.replace("[date]",day));
	    	
	    }catch(Exception e){
	    	logger.error(ErrorTitle.CONNECT_TITLE.getTitle(),e);
	    	
	    }
	    }
	    
	    
//	    
//	    
//    	for(String day : dayList){
//   		 try{
//	    		boolean chkday = chkTread(day);
//	    		if(chkday){
//	    			 System.out.println("day="+day);
//	    			
//	    			
//	    		    try{
//	    		    	logger.info("網址 ："+url.replace("[date]",day));
//	    		    	logger.info("網址 ："+url2.replace("[date]",day));
//	    		    	
//	    		    	source = dl.getData(url.replace("[date]",day));
//	    		    	source2 = dl.getData2(url2.replace("[date]",day));
//	    		    	
//	    		    }catch(Exception e){
//	    		    	logger.error(ErrorTitle.CONNECT_TITLE.getTitle(),e);
//	    		    	
//	    		    }
//
//	    	    	
//	    		}else{
//	 				logger.error(ErrorTitle.ANALYSIS_TITLE.getTitle(day+"非交易日，無檔案。"));
//	    		}
//   		 }catch(Exception e){
//				logger.error(ErrorTitle.ANALYSIS_TITLE.getTitle("非交易日，無檔案。"),e);
//   		 }
//    	}
	    
//	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");  
//	    Date date = new Date();  
//	    
//	    String day = formatter.format(date).toString();
//	    
//	    System.out.println("day="+day);
//	    
//	    try{
////	    	logger.info("網址 ："+url.replace("[date]",day));
////	    	logger.info("網址 ："+url2.replace("[date]",day));
//	    	
//	    	source = dl.getData(url.replace("[date]","20220523"));
//	    	source2 = dl.getData2(url2.replace("[date]","20220523"));
//	    	
//	    }catch(Exception e){
//	    	logger.error(ErrorTitle.CONNECT_TITLE.getTitle(),e);
//	    	
//	    }
	    
	    
	    //第二段 : 分析資料存入暫存處理

	    Table[] tableList = null;
	    Table[] tableList2 = null;

	    try{
	    	tableList = dl.parseListMap(source);
	    	tableList2 = dl.parseListMap2(source2);
	    }catch(Exception e){
	    	logger.error(ErrorTitle.PROCESS_TITLE.getTitle(),e);
	    }
	    
	    //資料處理暫存筆數為0 不需再執行匯入	    
		if (parameter.isDltestMode()) {   //偵側網改程式，參數test
			if (tableList != null && tableList[0].getTableBean().length == 0)
				logger.error(ErrorTitle.IMPORT_TITLE.getTitle("截取 0 筆資料"));
		} else if (tableList != null){
			//第三段 : 匯入資料庫處理
			TxssmarDAO dao = new TxssmarDAO(tableName,market);
			try{
				//依照分析資料存入暫存  處理的內容決定使用的表格
				dao.modify(tableList[0].getTableBean());
				dao.modify(tableList2[0].getTableBean());
			}catch(Exception e){
				logger.error(ErrorTitle.IMPORT_TITLE.getTitle(),e);
			}
		}
    	}

	
	public static void main(String[] args){
		
		DOMConfigurator.configure(".\\log4j.xml");

		//admin連線資訊
		IDBConnector admin =  DBAdminConnector.getInstance();
				
		//宣告RFP名稱字串
		String spt = System.getProperty("file.separator");
		String propertyPath = 
				System.getProperty("user.dir") + spt + "property"+ spt + "TEJ_215_XPRC_54.property";
				
		//宣告參數檔物件(給予檔案路徑)
		Parameter param = new Parameter(propertyPath);
				
		//初始化物件
		param.initial();
				
		//欲匯入表格連線資訊
		ConnectorTableBuilder builder = 
				new ConnectorTableBuilder(admin,param.getDbName());

		try{
			IDBConnector market = builder.buildConnector();

			TEJ_215_XPRC_54_2 runPg = new TEJ_215_XPRC_54_2();
					
			runPg.taskDescription(param , market,args);

		}catch(Exception e){
		     e.printStackTrace();
		}
	}
	
	
	//連接sql判斷是否為交易日
	public boolean chkTread(String day) throws Exception{
		ConnectorTableBuilder builder = new ConnectorTableBuilder(DBAdminConnector.getInstance(),"chn");
		DBConnector Connector = builder.buildConnector();

		PreparedStatement pstmt = null ;
		ResultSet rs = null ;
		boolean chk = false ;
		StringBuilder sql=new StringBuilder();
		//抓sql的中國交易日
		sql.append(String.format(" select distinct zdate from stk.attr_stdodr where zdate = '%s' and date_rmk = '' " , day ));
		Connection conn =null;
		try {
			conn = Connector.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			//如果是交易日就回傳true
			while (rs.next()) {
				chk = true ;
			}
			pstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally{
            try { if (pstmt != null) pstmt.close();  } catch(Exception e) { }
            try { if (rs != null)  rs = null; } catch(Exception e) { }
            try { if (conn != null) conn.close(); conn = null; } catch(Exception e) { e.printStackTrace();}
		}
		return chk;
	}
	
//	/**
//	*  看路徑底下的檔案 ,回傳清單陣列
//	**/
//	public List<String> numbers(String filePath) {
//		File f1 = new File(filePath);
//		File[] files = f1.listFiles(); // 列出完整路徑，傳回一個File物件陣列
//		List<String> number = new ArrayList<String>();
//		if (files != null) {
//			for (File filename : files) {
//				if (filename.isFile() && filename.exists()) {
//					String f2Str = filename.getName();
//					if (f2Str.toLowerCase().endsWith(".xls") && !f2Str.startsWith("~"))
//						number.add(f2Str);
//				}
//			}
//		}
//		return number;
//	}	
}
