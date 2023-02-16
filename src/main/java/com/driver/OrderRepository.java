package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    //<OrderId,Order>
    HashMap<String,Order> orderHashMap;
    //<DeliveryPartnerId,DeliveryPartner>
    HashMap<String,DeliveryPartner> deliveryPartnerHashMap;
    //<DeliveryPartnerId,List<OrderId>>
    HashMap<String, List<String>> partnerOrderPairHashMap;
    //<OrderId,PartnerId>
    HashMap<String,String> assignedOrdersHashMap;

    public OrderRepository() {
        orderHashMap = new HashMap<>();
        deliveryPartnerHashMap = new HashMap<>();
        partnerOrderPairHashMap = new HashMap<>();
        assignedOrdersHashMap = new HashMap<>();
    }

    public void addOrder(Order order) {
        orderHashMap.put(order.getId(),order);
    }

    public void addPartner(String partnerId) {
        deliveryPartnerHashMap.put(partnerId,new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(orderHashMap.containsKey(orderId) && deliveryPartnerHashMap.containsKey(partnerId)) {
            if(assignedOrdersHashMap.containsKey(orderId))
            {
                return;
            }
            if (partnerOrderPairHashMap.containsKey(partnerId)) {
                partnerOrderPairHashMap.get(partnerId).add(orderId);
                deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(deliveryPartnerHashMap.get(partnerId).getNumberOfOrders() + 1);
                assignedOrdersHashMap.put(orderId,partnerId);
                return;
            }
            List<String> ls = new ArrayList<>();
            ls.add(orderId);
            deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(1);
            partnerOrderPairHashMap.put(partnerId, ls);
            assignedOrdersHashMap.put(orderId,partnerId);
        }
    }

    public Order getOrderById(String orderId) {
        if(orderHashMap.containsKey(orderId))
            return orderHashMap.get(orderId);
        return new Order("-1","0");
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        if(deliveryPartnerHashMap.containsKey(partnerId))
            return deliveryPartnerHashMap.get(partnerId);
        return new DeliveryPartner("-1");
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        if(deliveryPartnerHashMap.containsKey(partnerId))
            return deliveryPartnerHashMap.get(partnerId).getNumberOfOrders();
        return -1;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        if(partnerOrderPairHashMap.containsKey(partnerId))
            return partnerOrderPairHashMap.get(partnerId);
        return new ArrayList<>();
    }

    public List<String> getAllOrders() {
        return new ArrayList<>(orderHashMap.keySet());
    }

    public Integer getCountOfUnassignedOrders() {
        return orderHashMap.size() - assignedOrdersHashMap.size();
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
        return -1;
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
        return "0";
    }

    public void deletePartnerById(String partnerId) {
        if(deliveryPartnerHashMap.containsKey(partnerId)) {
            deliveryPartnerHashMap.remove(partnerId);
            for (String orderId : assignedOrdersHashMap.keySet()) {
                if (assignedOrdersHashMap.get(orderId).equals(partnerId)) {
                    assignedOrdersHashMap.remove(orderId);
                }
            }
            partnerOrderPairHashMap.remove(partnerId);
        }
    }

    public void deleteOrderById(String orderId) {
        if(orderHashMap.containsKey(orderId)) {
            orderHashMap.remove(orderId);
            if(assignedOrdersHashMap.containsKey(orderId)) {
                assignedOrdersHashMap.remove(orderId);
                for (String partner : partnerOrderPairHashMap.keySet()) {
                    List<String> orders = partnerOrderPairHashMap.get(partner);
                    if (orders.contains(orderId)) {
                        orders.remove(orderId);
                        break;
                    }
                }
            }
        }
    }
}
