package com.ruoyi.bank.gateway;

import java.math.BigDecimal;

/**
 * OBK0071708（ylmxqy）监管账户交易明细行：银行返回 listnm 数组的逐笔进出账。
 * amntcd 交易方向 1=入账 0=出账；onlnbl 为该笔交易后账户余额。
 */
public class BankAccountDetail
{
    private String transq;
    private BigDecimal tranam;
    private String trandt;
    private String trantm;
    private String jgacct;
    private String jgacna;
    private String toacct;
    private String toacna;
    private String bankno;
    private String banknm;
    private String smrytx;
    private String dscrtx;
    private BigDecimal onlnbl;
    private String amntcd;
    private String totlno;

    public String getTransq()
    {
        return transq;
    }

    public void setTransq(String transq)
    {
        this.transq = transq;
    }

    public BigDecimal getTranam()
    {
        return tranam;
    }

    public void setTranam(BigDecimal tranam)
    {
        this.tranam = tranam;
    }

    public String getTrandt()
    {
        return trandt;
    }

    public void setTrandt(String trandt)
    {
        this.trandt = trandt;
    }

    public String getTrantm()
    {
        return trantm;
    }

    public void setTrantm(String trantm)
    {
        this.trantm = trantm;
    }

    public String getJgacct()
    {
        return jgacct;
    }

    public void setJgacct(String jgacct)
    {
        this.jgacct = jgacct;
    }

    public String getJgacna()
    {
        return jgacna;
    }

    public void setJgacna(String jgacna)
    {
        this.jgacna = jgacna;
    }

    public String getToacct()
    {
        return toacct;
    }

    public void setToacct(String toacct)
    {
        this.toacct = toacct;
    }

    public String getToacna()
    {
        return toacna;
    }

    public void setToacna(String toacna)
    {
        this.toacna = toacna;
    }

    public String getBankno()
    {
        return bankno;
    }

    public void setBankno(String bankno)
    {
        this.bankno = bankno;
    }

    public String getBanknm()
    {
        return banknm;
    }

    public void setBanknm(String banknm)
    {
        this.banknm = banknm;
    }

    public String getSmrytx()
    {
        return smrytx;
    }

    public void setSmrytx(String smrytx)
    {
        this.smrytx = smrytx;
    }

    public String getDscrtx()
    {
        return dscrtx;
    }

    public void setDscrtx(String dscrtx)
    {
        this.dscrtx = dscrtx;
    }

    public BigDecimal getOnlnbl()
    {
        return onlnbl;
    }

    public void setOnlnbl(BigDecimal onlnbl)
    {
        this.onlnbl = onlnbl;
    }

    public String getAmntcd()
    {
        return amntcd;
    }

    public void setAmntcd(String amntcd)
    {
        this.amntcd = amntcd;
    }

    public String getTotlno()
    {
        return totlno;
    }

    public void setTotlno(String totlno)
    {
        this.totlno = totlno;
    }
}
