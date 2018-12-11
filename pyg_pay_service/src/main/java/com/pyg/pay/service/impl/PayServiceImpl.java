package com.pyg.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.mapper.TbOrderMapper;
import com.pyg.mapper.TbPayLogMapper;
import com.pyg.pay.service.PayService;
import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbPayLog;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.HttpClient;
import utils.IdWorker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PayServiceImpl implements PayService {

    @Value("${appid}")
    private String appid;  //公众号
    @Value("${partner}")
    private String partner; // 商户号
    @Value("${partnerkey}")
    private String partnerkey; // 商户号密码
    @Value("${notifyurl}")
    private String notifyurl; // 回调地址

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    private TbOrderMapper orderMapper;

    @Override
    public Map createNative(String userId) {


        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
        String out_trade_no = payLog.getOutTradeNo();
//调用统一下单API
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        Map<String, String> paramMap = new HashMap<String, String>();
//        公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
        paramMap.put("appid", appid);
//        商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
        paramMap.put("mch_id", partner);
//        随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，长度要求在32位以内。推荐随机数生成算法
        String nonceStr = WXPayUtil.generateNonceStr();
        paramMap.put("nonce_str", nonceStr);
//        商品描述	body	是	String(128)	腾讯充值中心-QQ会员充值 商品简单描述，该字段请按照规范传递，具体请见参数规定
        paramMap.put("body", "品优购支付");
//        商户订单号	out_trade_no	是	String(32)	20150806125346
        paramMap.put("out_trade_no", out_trade_no+"");
//        标价金额	total_fee	是	Int	88	订单总金额，单位为分，详见支付金额
//        paramMap.put("total_fee", payLog.getTotalFee()+"");//真实的代码
        paramMap.put("total_fee", "1"); //测试代码
//        终端IP	spbill_create_ip	是	String(16)	123.12.12.123	APP和网页支付提交用户端ip，Native支付填调用微
        paramMap.put("spbill_create_ip", "127.0.0.1");
//        通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        paramMap.put("notify_url", notifyurl);
//        交易类型	trade_type	是	String(16)	JSAPI        JSAPI -JSAPI支付        NATIVE -Native支付        APP -APP支付
        paramMap.put("trade_type", "NATIVE");
//        签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	通过签名算法计算得出的签名值，详见签名生成算法
        try {
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);//生成签名并且把map、转成xml
            httpClient.setXmlParam(paramXml);
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
//            页面显示订单号和支付金额
            resultMap.put("out_trade_no", out_trade_no);
            resultMap.put("total_fee", payLog.getTotalFee() + ""); //只是显示使用

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Map queryOrder(String out_trade_no) throws Exception{
        //调用查询订单的支付状态API
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        Map<String, String> paramMap = new HashMap<String, String>();
//        公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
        paramMap.put("appid", appid);
//        商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
        paramMap.put("mch_id", partner);
//        随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，长度要求在32位以内。推荐随机数生成算法
        String nonceStr = WXPayUtil.generateNonceStr();
        paramMap.put("nonce_str", nonceStr);
//        商户订单号	out_trade_no	是	String(32)	20150806125346
        paramMap.put("out_trade_no", out_trade_no);
//        签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	通过签名算法计算得出的签名值，详见签名生成算法
        String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);//生成签名并且把map、转成xml
        httpClient.setXmlParam(paramXml);
        httpClient.post();
        String content = httpClient.getContent();
        Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
        return resultMap;
    }

    @Override
    public void updateOrder(String userId, String transactionId) {
        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
        payLog.setPayTime(new Date()); // 支付时间
        payLog.setTradeState("1");
        payLog.setTransactionId(transactionId); //微信交易流水号
        payLogMapper.updateByPrimaryKey(payLog);

        String[] orderIds = payLog.getOrderList().split(",");
        for (String orderId : orderIds) {
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            tbOrder.setStatus("2");
            tbOrder.setUpdateTime(new Date());
            tbOrder.setPaymentTime(new Date());
            orderMapper.updateByPrimaryKey(tbOrder);
        }
        redisTemplate.boundHashOps("payLog").delete(userId); // 清空当前用户的支付日志
    }
}
