package XPRC;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.math.BigDecimal;


public class Txssmar {
	
	private String stk_id;
	private Date zdate;
	private BigDecimal buy_l;
	private BigDecimal cash_l;
	private BigDecimal long_t;
	private BigDecimal sell_sv;
	private BigDecimal pay_sv;
	private BigDecimal short_tv;
	private BigDecimal short_t;
	private BigDecimal sb_t;
	public String getStk_id(){
		return stk_id;
	}
	public void setStk_id(String Stk_id ){
		this.stk_id = Stk_id;
	}
	public Date getZdate(){
		return zdate;
	}
	public void setZdate(Date Zdate ){
		this.zdate = Zdate;
	}
	public BigDecimal getBuy_l(){
		return buy_l;
	}
	public void setBuy_l(BigDecimal Buy_l ){
		this.buy_l = Buy_l;
	}
	public BigDecimal getCash_l(){
		return cash_l;
	}
	public void setCash_l(BigDecimal Cash_l ){
		this.cash_l = Cash_l;
	}
	public BigDecimal getLong_t(){
		return long_t;
	}
	public void setLong_t(BigDecimal Long_t ){
		this.long_t = Long_t;
	}
	public BigDecimal getSell_sv(){
		return sell_sv;
	}
	public void setSell_sv(BigDecimal Sell_sv ){
		this.sell_sv = Sell_sv;
	}
	public BigDecimal getPay_sv(){
		return pay_sv;
	}
	public void setPay_sv(BigDecimal Pay_sv ){
		this.pay_sv = Pay_sv;
	}
	public BigDecimal getShort_tv(){
		return short_tv;
	}
	public void setShort_tv(BigDecimal Short_tv ){
		this.short_tv = Short_tv;
	}
	public BigDecimal getShort_t(){
		return short_t;
	}
	public void setShort_t(BigDecimal Short_t ){
		this.short_t = Short_t;
	}
	public BigDecimal getSb_t(){
		return sb_t;
	}
	public void setSb_t(BigDecimal Sb_t ){
		this.sb_t = Sb_t;
	}
	public String getPK(){
		return  stk_id+","+zdate;
	}
	
	@Override
	public String toString() {
		
		return "Tx [stk_id=" + stk_id + ", zdate=" + zdate + ", buy_l="+buy_l+ ", cash_l="+cash_l+ ", long_t="+long_t+", sell_sv="+sell_sv+", pay_sv="+pay_sv+", short_tv="+short_tv+", short_t="+short_t+", sb_t"+sb_t +"]";
	}
	
	

}
