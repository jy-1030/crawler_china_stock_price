package XPRC;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

import org.apache.log4j.xml.DOMConfigurator;
import com.cake.net._useage.Parameter;
import com.tej.error.ErrorTitle;
import com.tej.frame.PorcessFrame;
import com.tej.frame.Table;
import com.tej.postgresql.DBAdminConnector;
import com.tej.postgresql.connection.ConnectorTableBuilder;
import com.tej.postgresql.connection.IDBConnector;

/**
 * 獨立開發程式
 * @author : 
 *
 */
public class TEJ_215_XPRC_54_1 extends PorcessFrame{
	public void taskDescription(Parameter param , IDBConnector market,String[] args){
		super.parameter = param;  //參數檔
		super.market    = market; //匯入連線資訊
		
	    String url       = parameter.getUrl();
	    String tableName = parameter.getTableName();
		String filePath = parameter.getOutdir();
		
	    DL_215_XPRC_54_1 dl = new DL_215_XPRC_54_1(parameter);

	    List<String> fils = new ArrayList<String>();
	    fils = numbers(filePath);
	    
	    for(String filenm:fils){
					
			//第二段 : 分析資料存入暫存處理
			logger.info("第二段 : 分析資料存入暫存處理");
			Table[] tableList = null;
			try{
				tableList = dl.parseListMap(filePath , filenm);
			}catch(Exception e){
				logger.error(ErrorTitle.PROCESS_TITLE.getTitle(),e);
			}
			

			//第三段 : 匯入資料庫處理
			logger.info("第三段 : 匯入資料庫處理");
			TxssmarDAO dao = new TxssmarDAO(tableName,market);
			try{
				//依照分析資料存入暫存  處理的內容決定使用的表格
				dao.modify(tableList[0].getTableBean());
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

			TEJ_215_XPRC_54_1 runPg = new TEJ_215_XPRC_54_1();
					
			runPg.taskDescription(param , market,args);

		}catch(Exception e){
		     e.printStackTrace();
		}
	}
	
	/**
	*  看路徑底下的檔案 ,回傳清單陣列
	**/
	public List<String> numbers(String filePath) {
		File f1 = new File(filePath);
		File[] files = f1.listFiles(); // 列出完整路徑，傳回一個File物件陣列
		List<String> number = new ArrayList<String>();
		if (files != null) {
			for (File filename : files) {
				if (filename.isFile() && filename.exists()) {
					String f2Str = filename.getName();
					if (f2Str.toLowerCase().endsWith(".xls") && !f2Str.startsWith("~"))
						number.add(f2Str);
				}
			}
		}
		return number;
	}	
}
