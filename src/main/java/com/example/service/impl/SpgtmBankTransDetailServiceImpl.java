package com.example.service.impl;

import com.example.entity.BankTransDetail;
import com.example.entity.Result;
import com.example.service.SpgtmBankTransDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SpgtmBankTransDetailServiceImpl implements SpgtmBankTransDetailService {
    /**
     * 银行流水通用查询接口
     * 通过该接口可查询指定账户、指定时间段内的银行交易明细信息
     * @param bankTransDetail
     * @return
     */
    @Override
    public Result getBankTransDetail(BankTransDetail bankTransDetail) {
        log.info("银行流水通用查询接口参数service传入:{}",bankTransDetail.toString());
        //判断参数是否为空
        if(bankTransDetail == null ){
            return Result.fail("参数不能为空！");
        }
        //判断系统标识是否为空
        if(StringUtils.isEmpty(bankTransDetail.getSyscode())){
            return Result.fail("syscode参数不能为空！");
        }
        //判断制单时间是否为空
        String lastchangedon1 = bankTransDetail.getLastchangedon1();
        if(StringUtils.isEmpty(lastchangedon1)){
            return Result.fail("lastchangedon1不能为空！");
        }

        log.info("逻辑查询开始");
        //查询逻辑
        try{
            //获取where 条件
            StringBuffer sql = new StringBuffer("1=1");
            Result result = this.jointSql(bankTransDetail);
            if(result != null && Result.ok().getCode().equals(result.getCode())){
                sql.append(result.getDatas());
            }
            log.info("where条件为：{}",sql);

            //当前页码 从0开始
            Integer pageindex = bankTransDetail.getPageindex();
            //每页行数 (可空，默认50条，最大500条)
            Integer pagesize = bankTransDetail.getPagesize();

        }catch (Exception e){
            log.info("报错信息：{}",e);
            return Result.fail(e.getMessage());
        }
        return Result.ok();
    }

    /**
     * where 后条件拼接
     * @param bankTransDetail
     * @return
     */
    private Result jointSql(BankTransDetail bankTransDetail){
        try{
            StringBuffer sql = new StringBuffer(" ");
            log.info("初始化sql：{}",sql);
            String lastchangedon1 = bankTransDetail.getLastchangedon1();
            //拼接sql
            //收付方向 (1付款、2收款 可空，不传入时查询全部)
            String inorexp = bankTransDetail.getInorexp();
            if("1".equals(inorexp) || "2".equals(inorexp)){
                sql.append(" and detail.incomeorexpenditure = " + inorexp);
                log.info("收付方向拼接:{}",sql);
            }

            //单位编号 (可空，非空时准确匹配，使用英文逗号分隔多个单位编号)
            String accountunits = bankTransDetail.getAccountunit();
            if(!StringUtils.isEmpty(accountunits)){
                sql.append(" and detail.accountunit in ('" + accountunits.replaceAll(",","','") + "')");
                log.info("单位编号拼接:{}",sql);
            }

            //账号 (可空，非空时准确匹配)
            String bankaccountcode = bankTransDetail.getBankaccountcode();
            if(!StringUtils.isEmpty(bankaccountcode)){
                log.info("账号特殊字符检验");
                if (false == checkNotAllowedChar(bankaccountcode)) {
                    return Result.fail("我方账号不允许包括特殊字符;");
                }
                sql.append(" and account.accountno = '" + bankaccountcode +"'");
                log.info("账号拼接:{}",sql);
            }

            //币种编号 (不传入时查询全部，非空时准确匹配)
            String currency = bankTransDetail.getCurrency();
            if(!StringUtils.isEmpty(currency)){
                sql.append(" and currency.code = '" + currency +"'");
                log.info("币种编号:{}",sql);
            }

            //对方账户编号 (可空，模糊查询)
            String oppbankaccountcode = bankTransDetail.getOppbankaccountcode();
            if(!StringUtils.isEmpty(oppbankaccountcode)){
                log.info("对方账户编号特殊字符检验");
                if (false == checkNotAllowedChar(oppbankaccountcode)) {
                    return Result.fail("对方账号不允许包括特殊字符;");
                }
                sql.append(" and detail.reciprocalaccountno like '%" + oppbankaccountcode +"%'");
                log.info("对方账户编号:{}",sql);
            }

            //对方账户名称 (可空，模糊查询)
            String oppbankaccountname = bankTransDetail.getOppbankaccountname();
            if(!StringUtils.isEmpty(oppbankaccountname)){
                log.info("对方账户名称特殊字符检验");
                if (false == checkNotAllowedChar(oppbankaccountname)) {
                    return Result.fail("对方账户名称不允许包括特殊字符;");
                }
                sql.append(" and detail.reciprocalaccname like '%" + oppbankaccountname +"%'");
                log.info("对方账户名称:{}",sql);
            }

            //对方开户行 (可空，模糊查询)
            String oppbankname = bankTransDetail.getOppbankname();
            if(!StringUtils.isEmpty(oppbankname)){
                log.info("对方开户行特殊字符检验");
                if (false == checkNotAllowedChar(oppbankname)) {
                    return Result.fail("对方开户行不允许包括特殊字符;");
                }
                sql.append(" and detail.bankofreciprocalaccount like '%" + oppbankname +"%'");
                log.info("对方开户行:{}",sql);
            }

            //摘要 (可空，模糊查询)
            String summary = bankTransDetail.getSummary();
            if(!StringUtils.isEmpty(summary)){
                log.info("摘要特殊字符检验");
                if (false == checkNotAllowedChar(summary)) {
                    return Result.fail("摘要不允许包括特殊字符;");
                }
                sql.append(" and detail.summary like '%" + summary +"%'");
                log.info("摘要:{}",sql);
            }

            //交易日期1 (yyyyMMdd格式，可空，非空时从该日期开始查询)
            String transactiontime1 = bankTransDetail.getTransactiontime1();
            SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyyMMdd");
            Date date = null;
            if(!StringUtils.isEmpty(transactiontime1)){
                try{
                    log.info("交易日期1格式校验");
                    date = simpleFormatter.parse(transactiontime1);
                    simpleFormatter.applyPattern("yyyy-MM-dd");
                    transactiontime1 = simpleFormatter.format(date);
                    sql.append(" and to_char(detail.transactiondate,'yyyy-MM-dd') >= '" + transactiontime1 + "'");
                    log.info("交易日期1:{}",sql);
                }catch (Exception e){
                    log.info("交易日期1格式校验报错：{}",e);
                    log.error("交易日期1格式校验报错：{}",e);
                    return Result.fail("交易日期1格式错误");
                }

            }

            //交易日期2 (yyyyMMdd格式，可空，非空时查询截止到该日期)
            String transactiontime2 = bankTransDetail.getTransactiontime2();
            simpleFormatter.applyPattern("yyyyMMdd");
            if(!StringUtils.isEmpty(transactiontime2)){
                try{
                    log.info("交易日期2格式校验");
                    date = simpleFormatter.parse(transactiontime2);
                    simpleFormatter.applyPattern("yyyy-MM-dd");
                    transactiontime2 = simpleFormatter.format(date);
                    sql.append(" and to_char(detail.transactiondate,'yyyy-MM-dd') <= '" + transactiontime2 + "'");
                    log.info("交易日期2:{}",sql);
                }catch (Exception e){
                    log.info("交易日期2格式校验报错：{}",e);
                    log.error("交易日期2格式校验报错：{}",e);
                    return Result.fail("交易日期2格式错误");
                }
            }

            //制单时间1 (yyyyMMddHHmmss格式，不可为空，从该日期开始查询)
            simpleFormatter.applyPattern("yyyyMMddHHmmss");
            try{
                log.info("制单时间1格式验证");
                date = simpleFormatter.parse(lastchangedon1);
                simpleFormatter.applyPattern("yyyy-MM-dd HH:mm:ss");
                lastchangedon1 = simpleFormatter.format(date);
                sql.append(" and to_char(detail.timestamp_createdon,'yyyy-MM-dd HH:mm:ss') >= '" + lastchangedon1 + "'");
                log.info("制单时间1:{}",sql);
            }catch (Exception e){
                log.info("制单时间1格式校验报错：{}",e);
                log.error("制单时间1格式校验报错：{}",e);
                return Result.fail("制单时间1格式错误");
            }

            //制单时间2 (yyyyMMddHHmmss格式，查询截止到该日期 可空，不传入时截止到当前时间)
            simpleFormatter.applyPattern("yyyyMMddHHmmss");
            String lastchangedon2 = bankTransDetail.getLastchangedon2();
            try{
                log.info("格式化制单时间2");
                if(StringUtils.isEmpty(lastchangedon2)){
                    date = new Date();
                }else {
                    date = simpleFormatter.parse(lastchangedon2);
                }
                simpleFormatter.applyPattern("yyyy-MM-dd HH:mm:ss");
                lastchangedon2 = simpleFormatter.format(date);
                sql.append(" and to_char(detail.timestamp_createdon,'yyyy-MM-dd HH:mm:ss') <= '" + lastchangedon2 + "'");
                log.info("制单时间2:{}",sql);

            }catch (Exception e){
                log.info("制单时间2格式校验报错：{}",e);
                log.error("制单时间2格式校验报错：{}",e);
                return Result.fail("制单时间2格式错误");
            }

            return Result.ok(sql);
        }catch (Exception e){
            return Result.fail("sql拼接失败");
        }
    }

    /**
     * 检查文本内容是否包括不允许的特殊字符
     * @param str 要检查的内容
     * @return true不包括特殊字符，false有特殊字符
     */
    private boolean checkNotAllowedChar(String str) {
        //禁止使用的特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        return false == p.matcher(str).find();
    }

}
