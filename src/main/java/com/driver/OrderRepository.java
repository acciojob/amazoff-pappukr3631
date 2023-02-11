package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String,Order> orderHashMap;
    HashMap<String,DeliveryPartner> deliveryPartnerHashMap;
    HashMap<String, List<String>> partnerOrderPairHashMap;

    public OrderRepository() {
        orderHashMap = new HashMap<>();
        deliveryPartnerHashMap = new HashMap<>();
        partnerOrderPairHashMap = new HashMap<>();
    }

    public void addOrder(Order order) {

        orderHashMap.put(order.getId(),order);
    }

    public void addPartner(String partnerId) {
        deliveryPartnerHashMap.put(partnerId,new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(partnerOrderPairHashMap.containsKey(partnerId))
        {
            partnerOrderPairHashMap.get(partnerId).add(orderId);
            deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(deliveryPartnerHashMap.get(partnerId).getNumberOfOrders()+1);
            return;
        }
        List<String> ls = new ArrayList<>();
        ls.add(orderId);
        deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(1);
        partnerOrderPairHashMap.put(partnerId,ls);
    }

    public Order getOrderById(String orderId) {
        if(orderHashMap.containsKey(orderId))
            return orderHashMap.get(orderId);
        return null;
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return deliveryPartnerHashMap.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        return deliveryPartnerHashMap.get(partnerId).getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        List<String> list = new ArrayList<>();
        if(partnerOrderPairHashMap.containsKey(partnerId))
            list = partnerOrderPairHashMap.get(partnerId);
        return list;
    }

    public List<String> getAllOrders() {
        ArrayList<String> orders = new ArrayList<>(orderHashMap.keySet());
        return orders;
    }

    public Integer getCountOfUnassignedOrders() {

        int pairedCount = 0;
        for(String partner : partnerOrderPairHashMap.keySet())
        {
            pairedCount += partnerOrderPairHashMap.get(partner).size();
        }
        return orderHashMap.size() - pairedCount;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int t = Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3,5));
        int ordersLeft = 0;
        if(partnerOrderPairHashMap.containsKey(partnerId))
        {
            List<String> orders = partnerOrderPairHashMap.get(partnerId);

            for(String order : orders)
            {
                int deliveryTime = orderHashMap.get(order).getDeliveryTime();
                if(deliveryTime > t)
                {
                    ordersLeft++;
                }
            }
            return ordersLeft;
        }
        return 0;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {

        if(partnerOrderPairHashMap.containsKey(partnerId))
        {
            int max = 0;
            List<String> orders = partnerOrderPairHashMap.get(partnerId);
            for(String orderId : orders)
            {
                int time = orderHashMap.get(orderId).getDeliveryTime();
                if(time > max)
                    max = time;
            }
            String hh = String.valueOf(max/60);
            String mm = String.valueOf(max%60);
            if(hh.length() == 1)
                hh = "0" + hh;
            if(mm.length() == 1)
                mm = "0" + mm;
            return hh + ":" + mm;
        }
        return "Partner Not Found";
    }

    public void deletePartnerById(String partnerId) {
        deliveryPartnerHashMap.remove(partnerId);
        partnerOrderPairHashMap.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        orderHashMap.remove(orderId);
        for(String partner : partnerOrderPairHashMap.keySet())
        {
            List<String> orders = partnerOrderPairHashMap.get(partner);
            if(orders.contains(orderId))
            {
                orders.remove(orderId);
                break;
            }
        }
    }
}
