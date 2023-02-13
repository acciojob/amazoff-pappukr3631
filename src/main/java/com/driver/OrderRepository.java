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

    public void addOrderPartnerPair(String orderId, String partnerId) throws Exception {
        if(orderHashMap.containsKey(orderId) && deliveryPartnerHashMap.containsKey(partnerId)) {
            if(assignedOrdersHashMap.containsKey(orderId))
            {
                throw new Exception("Order Already Assigned To : " + assignedOrdersHashMap.get(orderId));
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

    public Order getOrderById(String orderId) throws Exception {
        if(orderHashMap.containsKey(orderId))
            return orderHashMap.get(orderId);
        throw new Exception("Order Not Found");
    }

    public DeliveryPartner getPartnerById(String partnerId) throws Exception {
        if(deliveryPartnerHashMap.containsKey(partnerId))
            return deliveryPartnerHashMap.get(partnerId);
        throw new Exception("Delivery Partner Not Found");
    }

    public Integer getOrderCountByPartnerId(String partnerId) throws Exception {
        if(deliveryPartnerHashMap.containsKey(partnerId))
            return deliveryPartnerHashMap.get(partnerId).getNumberOfOrders();
        throw new Exception("Delivery Partner Not Found");
    }

    public List<String> getOrdersByPartnerId(String partnerId) throws Exception {
        if(partnerOrderPairHashMap.containsKey(partnerId))
            return partnerOrderPairHashMap.get(partnerId);
        throw new Exception("Delivery Partner Not Found");
    }

    public List<String> getAllOrders() {
        return new ArrayList<>(orderHashMap.keySet());
    }

    public Integer getCountOfUnassignedOrders() {

//        int pairedCount = 0;
//        for(String partner : partnerOrderPairHashMap.keySet())
//        {
//            pairedCount += partnerOrderPairHashMap.get(partner).size();
//        }
//        return orderHashMap.size() - pairedCount;
        return orderHashMap.size() - assignedOrdersHashMap.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) throws Exception {
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
        throw new Exception("Delivery Partner Not Found");
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) throws Exception {

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
        throw new Exception("Delivery Partner Not Found");
    }

    public void deletePartnerById(String partnerId) throws Exception {
        if(deliveryPartnerHashMap.containsKey(partnerId)) {
            deliveryPartnerHashMap.remove(partnerId);
            for (String orderId : assignedOrdersHashMap.keySet()) {
                if (assignedOrdersHashMap.get(orderId).equals(partnerId)) {
                    assignedOrdersHashMap.remove(orderId);
                }
            }
            partnerOrderPairHashMap.remove(partnerId);
        }
        throw new Exception("Delivery Partner Not Found");
    }

    public void deleteOrderById(String orderId) throws Exception {
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
        throw new Exception("Order Not Found");
    }
}
