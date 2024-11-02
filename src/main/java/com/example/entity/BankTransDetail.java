package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankTransDetail {

    private String syscode;	//系统标识
    private String inorexp;	//收付方向
    private String accountunit;	//单位编号
    private String bankaccountcode;	//账号
    private String currency;	//币种编号
    private String oppbankaccountcode;	//对方账户编号
    private String oppbankaccountname;	//对方账户名称
    private String oppbankname;	//对方开户行
    private String summary;	//摘要
    private String transactiontime1;	//交易日期
    private String transactiontime2;	//交易日期
    private String lastchangedon1;	//制单时间
    private String lastchangedon2;	//制单时间
    private Integer pageindex;	//当前页码
    private Integer pagesize;	//每页行数

}

