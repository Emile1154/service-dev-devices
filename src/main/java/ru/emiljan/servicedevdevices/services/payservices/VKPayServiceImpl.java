package ru.emiljan.servicedevdevices.services.payservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.*;
import ru.emiljan.servicedevdevices.models.payment.*;
import ru.emiljan.servicedevdevices.repositories.OrderRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;
import ru.emiljan.servicedevdevices.repositories.payrepo.VKPayRepository;
import ru.emiljan.servicedevdevices.services.notify.NotifyService;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * @author EM1LJAN
 */
@Service
public class VKPayServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VKPayRepository vkPayRepository;
    private final NotifyService notifyService;

    private final String merchantPrivateKey;
    private final String clientSecret;

    @Autowired
    public VKPayServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            VKPayRepository vkPayRepository,
                            NotifyService notifyService, @Value(value = "${vkpay.clientId}") String merchantPrivateKey,
                            @Value(value = "${vkpay.clientSecret}") String clientSecret) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.vkPayRepository = vkPayRepository;
        this.notifyService = notifyService;
        this.merchantPrivateKey = merchantPrivateKey;
        this.clientSecret = clientSecret;
    }

    @Override
    public Payment createOrder(URI returnURI, Long orderId) {
        VKPayPayment vkPayPayment = new VKPayPayment();
        SerializeParam params = buildParam(orderId, vkPayRepository.getMaxId()+1,returnURI.toString());
        ObjectMapper mapper = new ObjectMapper();
        try{
            String json = mapper.writeValueAsString(params);
            Parameters parameters = new Parameters(params);
            params=null;
            parameters.setSign(createSign(json));
            parameters.setAction("pay-to-service");
            vkPayPayment.setParam(parameters);
            return vkPayPayment;
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }

    private String createSign(String input){// md5( all val concat (action not allowed!)  + secret_key)
        String trimBracket = input.substring(1, input.length()-1);
        String startLine = trimBracket.substring(0, trimBracket.indexOf("{"));
        String endLine = trimBracket.substring(trimBracket.lastIndexOf("}")+1);
        String attach = trimBracket.substring(trimBracket.indexOf("{"),trimBracket.lastIndexOf("}")+1);
        input = startLine + endLine;
        input = input.replaceAll("[\"|,]","");
        input = input.replaceAll(":","=");
        input = input.replaceAll("data=", "data="+attach);
        return DigestUtils.md5Hex(input+clientSecret);
    }

    private String getMerchantData(Long unixTime, Long id, Double price) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("order_id",id);
        node.put("ts", unixTime);
        node.put("amount", price);
        node.put("currency", "RUB");
        try{
            String json = mapper.writer().writeValueAsString(node);
            return Base64.encodeBase64String(json.getBytes(StandardCharsets.UTF_8));
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return null;
    }

    private String getMerchantSign(String merchantData){
        return DigestUtils.sha1Hex(merchantData + merchantPrivateKey);
    }

    private Data buildData(Long paymentId, Double price, String returnUrl){
        long unixTime = Instant.now().getEpochSecond();
        String merchantData = getMerchantData(unixTime,paymentId,price);
        String merchantSign = getMerchantSign(merchantData);
        return Data.builder()
                    .currency("RUB")
                    .merchant_data(merchantData)  //json (order_id, ts, amount, currency).base64 + merchant.private_key DONE!
                    .merchant_params(returnUrl)
                    .merchant_sign(merchantSign) //sha1 (merchant.data) DONE!
                    .order_id(String.valueOf(paymentId))
                    .ts(unixTime)
                .build();
    }

    private SerializeParam buildParam(Long orderId, Long paymentId, String returnUrl){
        CustomOrder order = orderRepository.findById(orderId).orElse(null);
        assert order != null;
        Double price = order.getPrice().doubleValue();
        return SerializeParam.builder()
                    .amount(price)
                    .data(buildData(paymentId, price, returnUrl))
                    .description("Test Payment")
                    .merchant_id(8084738)
                    .version(2)
                .build();
    }

    @Override
    public boolean captureOrder(String orderId) {
        // return JSON pay session
        return false;
    }

    @Override
    @Transactional
    public void save(Payment payment, String username, Long orderId) {
        VKPayPayment vkPayment = (VKPayPayment) payment;
        vkPayment.setPayStatus(PayStatus.CANCELED);
        vkPayment.setUser(userRepository.findByNickname(username));
        vkPayment.setOrder(orderRepository.findById(orderId).orElse(null));
        vkPayRepository.save(vkPayment);
    }

    @Override
    @Transactional
    public void update(String id) {
        VKPayPayment vkPayPayment = vkPayRepository.findVKPayPaymentById(Long.parseLong(id));
        vkPayPayment.setPayStatus(PayStatus.PAYED);
        notifyService.createNotify("buy", vkPayPayment.getUser());
        vkPayRepository.save(vkPayPayment);
    }
}
