package XPRC;

import java.util.List;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.xml.DOMConfigurator;
import com.cake.net._useage.Parameter;
import com.gargoylesoftware.htmlunit.Page;
import com.tej.date.ComputeDates;
import com.tej.date.TradingDay;
import com.tej.error.ErrorTitle;
import com.tej.frame.PorcessFrame;
import com.tej.postgresql.DBAdminConnector;
import com.tej.postgresql.connection.ConnectorTableBuilder;
import com.tej.postgresql.connection.DBConnector;
import com.tej.postgresql.connection.IDBConnector;
import com.yen.system.Date.calendar;

/**
 * 獨立開發程式
 * @author : 
 *
 */
public class TEJ_215_XPRC_54 extends PorcessFrame{
	public void taskDescription(Parameter param , IDBConnector market,String[] args){
		super.parameter = param;  //參數檔
		super.market    = market; //匯入連線資訊
		
	    DL_215_XPRC_54 dl = new DL_215_XPRC_54(parameter);
	    TEJ_215_XPRC_54_2 web = new TEJ_215_XPRC_54_2();

	    List<String> dayList = null;
	    
	    ///////////////////////////////////////////////////
	    //test 切換抓檔方式
//			web.taskDescription(param, market, args);
		///////////////////////////////////////////////////
		  //此段 輸入日期參數	  
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
		// System.out.println("day="+day);
	    String url= parameter.getUrl();
		logger.info("網址 : "+url.replace("[date]",day));
//    	    	source = dl.getData(url.replace("[date]",day));
		String url1=url.replace("[date]",day);
		String filenm=day+".xls";
//    	dl.download(url1,filenm);
		dl.dolXls(url1,filenm);
	    }
	    	catch(Exception e){
			logger.error(ErrorTitle.ANALYSIS_TITLE.getTitle("下載網址失效，改抓網頁"),e);
//			web.taskDescription(param, market, args);//改抓網頁資料
		 }
	    }
	    
//    	for(String day : dayList){
//    		 try{
//	    		boolean chkday = chkTread(day);
//	    		if(chkday){
//	    			// System.out.println("day="+day);
//	    		    String url= parameter.getUrl();
//	    			logger.info("網址 : "+url.replace("[date]",day));
//	//    	    	source = dl.getData(url.replace("[date]",day));
//	    			String url1=url.replace("[date]",day);
//	    			String filenm=day+".xls";
////	    	    	dl.download(url1,filenm);
//	    			dl.dolXls(url1,filenm);
//	    	    	
//	    		}else{
//	 				logger.error(ErrorTitle.ANALYSIS_TITLE.getTitle(day+"非交易日，無檔案。"));
//	    		}
//    		 }catch(Exception e){
//    			 
//    			 
// 				logger.error(ErrorTitle.ANALYSIS_TITLE.getTitle("下載網址失效，改抓網頁"),e);
// 				web.taskDescription(param, market, args);
//
//    		 }
//    	}  
	    
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

			TEJ_215_XPRC_54 runPg = new TEJ_215_XPRC_54();
					
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
	
}
