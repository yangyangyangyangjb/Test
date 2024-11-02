package com.example.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class JtgkDqckCsFhSjEntity {

    private String protocolId;
    private String busiStatus;
    private String protocolCode;
    private String protocolAccCode;
    private String currType;
    private String startDate;
    private String expireDate;
    private String isTranDeposit;
    private BigDecimal rate;
    private BigDecimal amt;
    private String floatType;
    private BigDecimal zjlvdcNlv;
    private String floatMethod;
    private BigDecimal floatValue;
    private String protocolType;
    private String prodFactId;
    private String isTranTake;
    private String tranDepositType;
    private String interestAccCode;
    private Date startAccualDate;
}
