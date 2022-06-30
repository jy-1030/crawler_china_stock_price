package XPRC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tej.error.ErrorTitle;
import com.tej.frame.DaoFrame;
import com.tej.postgresql.connection.IDBConnector;


public class TxssmarDAO extends DaoFrame{
	private String tableName;
	private IDBConnector market;
	public TxssmarDAO(String tableName  , IDBConnector market){
		this.tableName = tableName;
	    this.market = market;
	}

	
	/**
	 * 非版本別 先修改後新增
	 * @param ListMap
	 * @throws SQLException 
	 * @throws Exception
	 */
	public void modify(Object[] tempMap) throws Exception {
		
		if(tempMap.length == 0){
			logger.warn("本次資料0筆,不處理匯入");
			return ;
		}
		
		Txssmar[] ListMap = (Txssmar[])tempMap;

		int Counter = 0;
		PreparedStatement pstmt = null ;
		Connection conn = null;
		try{
			conn = this.market.getConnection();
			
			//設定連線
			conn.setAutoCommit(false);
			
			logger.info(this.getClass().getName()+" 本次資料共"+ListMap.length+"筆");
//			System.out.println("本次資料共"+ListMap.length+"筆");
			//建立表格
			logger.info("step1 更新資料(SQL):");
			
			String updateSQL = getUpadteSQL();
			pstmt = conn.prepareStatement(updateSQL);
			logger.debug("更新指令:"+updateSQL);
			
			int[] ok = new int[ListMap.length] ;
			for(int j=0;j<ListMap.length;j++){
				Txssmar obj = ListMap[j];
				try{
					pstmt.clearParameters();
					
					pstmt.setBigDecimal(1,obj.getBuy_l());
					pstmt.setBigDecimal(2,obj.getCash_l());
					pstmt.setBigDecimal(3,obj.getLong_t());
					pstmt.setBigDecimal(4,obj.getSell_sv());
					pstmt.setBigDecimal(5,obj.getPay_sv());
					pstmt.setBigDecimal(6,obj.getShort_tv());
					pstmt.setBigDecimal(7,obj.getShort_t());
					pstmt.setBigDecimal(8,obj.getSb_t());

					pstmt.setString(9,obj.getStk_id());
					pstmt.setDate(10,obj.getZdate());


					ok[j] = pstmt.executeUpdate();
					
					conn.commit();
					Counter++;
				}catch(Exception e){
				    logger.error(ErrorTitle.UPDATE_TITLE.getTitle(obj.getPK()) , e);
					
					ok[j] = -1;
					conn.rollback();
				}
			}
			logger.info("更新完畢, 共"+Counter+"筆執行更新指令");
			Counter = 0;
			
			logger.info("step2 新增資料(SQL):");
			String insertSQL = this.getInsertSQL();
			pstmt = conn.prepareStatement(insertSQL);
			logger.debug("新增指令:"+insertSQL);
			
			for(int i = 0;i<ListMap.length;i++){
				//已 update 過的不再新增
				if(ok[i]==1)continue ;
				Txssmar obj = ListMap[i];
				try{
					pstmt.clearParameters();
					
					pstmt.setString(1,obj.getStk_id());
					pstmt.setDate(2,obj.getZdate());
					pstmt.setBigDecimal(3,obj.getBuy_l());
					pstmt.setBigDecimal(4,obj.getCash_l());
					pstmt.setBigDecimal(5,obj.getLong_t());
					pstmt.setBigDecimal(6,obj.getSell_sv());
					pstmt.setBigDecimal(7,obj.getPay_sv());
					pstmt.setBigDecimal(8,obj.getShort_tv());
					pstmt.setBigDecimal(9,obj.getShort_t());
					pstmt.setBigDecimal(10,obj.getSb_t());

					
					pstmt.execute();
					conn.commit();
					Counter++;
				}catch(Exception e){
					logger.error(ErrorTitle.INSERT_TITLE.getTitle(obj.getPK()) , e);
					
					ok[i] = -1;
					conn.rollback();
				}
			}
			pstmt.close();
			logger.info("新增完畢, 共"+Counter+"筆新增");
			Counter = 0;
			
		}catch (SQLException e) {
			try { if (conn != null) conn.rollback(); } catch(Exception ee) { }
			throw e ;
		}catch (Exception e) {
			try { if (conn != null) conn.rollback(); } catch(Exception ee) { }
			throw e ;
		}finally{
            try { if (pstmt != null) pstmt.close();  } catch(Exception e) { e.printStackTrace();}
            try { if (conn != null) conn.close(); conn = null; } catch(Exception e) { e.printStackTrace();}
		}
		
		//此function例外錯誤(log紀錄)由抽象類別(DaoFrame)統一回傳
	}

	/**
	 * 新增
	 * @return
	 * @throws Exception
	 */
	public String getInsertSQL(){
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" INSERT INTO "+tableName);
		sql.append("( stk_id,zdate,buy_l,cash_l,long_t,sell_sv,pay_sv,short_tv,short_t,sb_t)");
		sql.append("  values ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? )");
				
		return sql.toString();
		
	}	
	/**
	 * 更新
	 * @return
	 * @throws Exception
	 */
	public String getUpadteSQL(){
		
		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE "+tableName);
		sql.append(" SET buy_l = ? ,cash_l = ? ,long_t = ? ,sell_sv = ? ,pay_sv = ? ,short_tv = ? ,short_t = ? ,sb_t = ?  "); 
		sql.append(" WHERE (stk_id = ?  AND zdate = ?  )"); 
				
		return sql.toString();
		
	}

	
	  
}
