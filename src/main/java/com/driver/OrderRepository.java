package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class OrderRepository {

    //<OrderId,Order>
    private HashMap<String,Order> orderHashMap;
    //<DeliveryPartnerId,DeliveryPartner>
    private HashMap<String,DeliveryPartner> deliveryPartnerHashMap;
    //<DeliveryPartnerId,List<OrderId>>
    private HashMap<String, HashSet<String>> partnerOrderPairHashMap;
    //<OrderId,PartnerId>
    private HashMap<String,String> assignedOrdersHashMap;

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
                deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(partnerOrderPairHashMap.get(partnerId).size());
                assignedOrdersHashMap.put(orderId,partnerId);
                return;
            }
            //This is first order assigned to the given delivery partner
            HashSet<String> hs = new HashSet<>();
            hs.add(orderId);
            deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(1);
            partnerOrderPairHashMap.put(partnerId, hs);
            assignedOrdersHashMap.put(orderId,partnerId);
        }
    }

    public Order getOrderById(String orderId) {
        if(orderHashMap.containsKey(orderId))
            return orderHashMap.get(orderId);
        return new Order("No-Order","0");
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        if(deliveryPartnerHashMap.containsKey(partnerId))
            return deliveryPartnerHashMap.get(partnerId);
        return new DeliveryPartner("-1");
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        if(deliveryPartnerHashMap.containsKey(partnerId))
            return deliveryPartnerHashMap.get(partnerId).getNumberOfOrders();
        else
            return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        if(partnerOrderPairHashMap.containsKey(partnerId)) {
            return new ArrayList<>(partnerOrderPairHashMap.get(partnerId));
        }
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
            HashSet<String> orders = partnerOrderPairHashMap.get(partnerId);

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
        return ordersLeft;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {

        if(partnerOrderPairHashMap.containsKey(partnerId))
        {
            int max = 0;
            HashSet<String> orders = partnerOrderPairHashMap.get(partnerId);
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
            List<String> assignedOrderList = new ArrayList<>(assignedOrdersHashMap.keySet());
            for (String orderId : assignedOrderList) {
                if (assignedOrdersHashMap.get(orderId).equals(partnerId)) {
                    assignedOrdersHashMap.remove(orderId);
                }
            }
            partnerOrderPairHashMap.remove(partnerId);
        }
    }

    public void deleteOrderById(String orderId) {
        orderHashMap.remove(orderId);
        assignedOrdersHashMap.remove(orderId);

        for(String partner : partnerOrderPairHashMap.keySet()) {
            HashSet<String> orders = partnerOrderPairHashMap.get(partner);
            orders.remove(orderId);
        }

       /* if(partnerOrderPairHashMap.containsKey(orderId)){

            //Removing this from the partner assigned orders List
//            String partnerId = orderToPartnerMap.get(orderId);
            HashSet<String> orders = partnerToOrderMap.get(partnerId);
            orders.remove(orderId);
            partnerToOrderMap.put(partnerId, orders);

            //change order count of partner
            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(orders.size());
            orderToPartnerMap.remove(orderId);

        }

        if(orderMap.containsKey(orderId)){
            orderMap.remove(orderId);
        }*/
    }
}
