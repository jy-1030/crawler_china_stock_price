package XPRC;

import org.apache.log4j.Logger;

import com.tej.main.JobMainTest;

/**
 * 215-XPRC-54
 * 主程式 : 記錄抓檔時間 public.web_joblog
 * @author : 
 *
 */
public class StateMainTest {
	private static Logger logger = Logger.getLogger("IMPORT");
	public static void main(String[] args){
		try{
			//測試程式
			JobMainTest job = new JobMainTest(args);
			job.processJob("TEJ_215_XPRC_54");
		}catch(Exception e){
			logger.error("主程式運行例外", e);
		}
	}
}
