package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/23 15:20
 * @comment
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;
    @Value("${sky.shop.address}")
    private String shopAddress;
    @Value("${sky.baidu.ak}")
    private String ak;


    private Orders existedOrders;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 获取 addressBook
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);

        checkOutOfRange(addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());

        // 获取 shoppingCart
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        if (shoppingCarts == null || shoppingCarts.isEmpty())
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);

        // 构造 orders
        // 备注：amount已经在前端算完返回了 后端不必算
        Orders orders = Orders.builder()
                .number(String.valueOf(System.currentTimeMillis()))
                .status(Orders.PENDING_PAYMENT)  // 待付款
                .userId(BaseContext.getCurrentId())
                .orderTime(LocalDateTime.now())
                .payStatus(Orders.UN_PAID)  // 未支付
                .phone(addressBook.getPhone())
                .address(addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail())
                .consignee(addressBook.getConsignee())
                .build();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        // 插入 orders
        ordersMapper.insert(orders);
        this.existedOrders = orders;

        // 构造 details
        ArrayList<OrderDetail> details = new ArrayList<>();
        BigDecimal orderAmount = new BigDecimal(0);
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(cart, detail);
            detail.setOrderId(orders.getId());
            details.add(detail);
        }
        // 批量插入 details
        orderDetailMapper.insertBatch(details);

        // 清空 shoppingCart
        shoppingCartService.cleanShoppingCart();

        // return VO
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

/*      跳过微信支付
        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }*/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        Integer OrderPaidStatus = Orders.PAID;//支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;  //订单状态，待接单
        LocalDateTime check_out_time = LocalDateTime.now();//更新支付时间
        ordersMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, this.existedOrders.getId());
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        ordersMapper.update(orders);
    }

    @Override
    public PageResult<OrderVO> pageQuery(Integer page, Integer pageSize, Integer status) {
        OrdersPageQueryDTO pageQueryDTO = new OrdersPageQueryDTO();
        pageQueryDTO.setStatus(status);
        pageQueryDTO.setUserId(BaseContext.getCurrentId());

        PageHelper.startPage(page, pageSize);
        List<Orders> ordersList = ordersMapper.page(pageQueryDTO);
        PageInfo<Orders> pageInfo = new PageInfo<>(ordersList);
        ArrayList<OrderVO> voList = new ArrayList<>();
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders order : ordersList) {
                List<OrderDetail> details = orderDetailMapper.getByOrdersId(order.getId());
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order, orderVO);
                orderVO.setOrderDetailList(details);
                voList.add(orderVO);
            }
        }

        return new PageResult<OrderVO>(pageInfo.getTotal(), voList);
    }

    @Override
    public OrderVO getDetails(Long id) {
        Orders orders = ordersMapper.getById(id);
        List<OrderDetail> details = orderDetailMapper.getByOrdersId(id);
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(orders, vo);
        vo.setOrderDetailList(details);
        return vo;
    }

    @Override
    public void cancelByOrderId(Long id) throws Exception {
        Orders orderInDB = ordersMapper.getById(id);
        if (orderInDB == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (orderInDB.getStatus() > 2)
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        /* 由于跳过了付款步骤 不执行退款
        // 订单处于待接单状态下取消，需要进行退款
        if (orderInDB.getStatus() == Orders.TO_BE_CONFIRMED) {
            weChatPayUtil.refund(
                    orderInDB.getNumber(), //商户订单号
                    orderInDB.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额
        }*/

        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.CANCELLED)
                .cancelTime(LocalDateTime.now())
                .cancelReason("用户取消")
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {
        List<OrderDetail> details = orderDetailMapper.getByOrdersId(id);
        for (OrderDetail detail : details) {
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .createTime(LocalDateTime.now())
                    .userId(BaseContext.getCurrentId())
                    .build();
            BeanUtils.copyProperties(detail, shoppingCart, "id");  // 避免复制id
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public PageResult<OrderVO> conditionSearch(OrdersPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<Orders> ordersList = ordersMapper.page(dto);
        ArrayList<OrderVO> voList = new ArrayList<>();
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders order : ordersList) {
                OrderVO vo = new OrderVO();
                BeanUtils.copyProperties(order, vo);
                List<OrderDetail> details = orderDetailMapper.getByOrdersId(order.getId());
                vo.setOrderDetailList(details);
                vo.setOrderDishes(getDetailsStr(details));
                voList.add(vo);
            }
        }

        PageInfo<Orders> pageInfo = new PageInfo<>(ordersList);
        return new PageResult<>(pageInfo.getTotal(), voList);
    }

    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO vo = OrderStatisticsVO.builder()
                .toBeConfirmed(ordersMapper.getNumbersByStatus(Orders.TO_BE_CONFIRMED))
                .confirmed(ordersMapper.getNumbersByStatus(Orders.CONFIRMED))
                .deliveryInProgress(ordersMapper.getNumbersByStatus(Orders.DELIVERY_IN_PROGRESS))
                .build();
        return vo;
    }

    @Override
    public void confirmById(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void rejectionById(OrdersRejectionDTO ordersRejectionDTO) {
        Orders ordersInDB = ordersMapper.getById(ordersRejectionDTO.getId());
        if (ordersInDB == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (!ordersInDB.getStatus().equals(Orders.TO_BE_CONFIRMED))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        /* 由于跳过了付款步骤 不执行退款
        if (ordersInDB.getPayStatus() == Orders.PAID) {
            weChatPayUtil.refund(
                    ordersInDB.getNumber(), //商户订单号
                    ordersInDB.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额
        }*/

        Orders orders = Orders.builder()
                .id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .cancelTime(LocalDateTime.now())
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void cancelById(OrdersCancelDTO ordersCancelDTO) {
        Orders ordersInDB = ordersMapper.getById(ordersCancelDTO.getId());
        if (ordersInDB == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        /* 由于跳过了付款步骤 不执行退款
        if (ordersInDB.getPayStatus() == Orders.PAID) {
            weChatPayUtil.refund(
                    ordersInDB.getNumber(), //商户订单号
                    ordersInDB.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额
        }*/

        Orders orders = Orders.builder()
                .id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void deliveryById(Long id) {
        Orders ordersInDB = ordersMapper.getById(id);
        if (ordersInDB == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (!ordersInDB.getStatus().equals(Orders.CONFIRMED))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void completeById(Long id) {
        Orders ordersInDB = ordersMapper.getById(id);
        if (ordersInDB == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (!ordersInDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .build();
        ordersMapper.update(orders);
    }

    public String getDetailsStr(List<OrderDetail> details) {
        StringBuilder builder = new StringBuilder();
        for (OrderDetail detail : details) {
            String name = detail.getName();
            Integer number = detail.getNumber();
            builder.append(name).append("*").append(number).append("; ");
        }
        return builder.toString();
    }

    private void checkOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address",shopAddress);
        map.put("output","json");
        map.put("ak",ak);

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        log.info("到用户的距离为:{}m", distance);
        if(distance > 5000){
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }
    }
}
