package com.corgan.angularbackend.stripe;

import com.corgan.angularbackend.entity.Classes;
import com.corgan.angularbackend.entity.Membership;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.*;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.*;

public class MainStripe {

    @Value("${allowed.origins}")
    private String origin;


    public MainStripe(){
        Stripe.apiKey = "///STRIPE API KEY///";
    }

    public Session buyOnce(HttpServletRequest request, HttpServletResponse response, Classes classes)throws StripeException {

        long unixTime = (System.currentTimeMillis() / 1000L) + 1800;

        Long expireDate = new Date().getTime() + 60;

        BigDecimal newVal = classes.getValue().multiply(new BigDecimal("100"));

        try {

                String YOUR_DOMAIN = request.getHeader("origin");
                SessionCreateParams params =
                        SessionCreateParams.builder()
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl(YOUR_DOMAIN + "/account/bookings")
                                .setCancelUrl(YOUR_DOMAIN + "/")
                                .setExpiresAt(unixTime)
                                .addLineItem(
                                        SessionCreateParams.LineItem.builder()
                                                .setQuantity(1L)
                                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                                    .setUnitAmountDecimal(newVal)
                                                    .setCurrency("gbp")
                                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder().setName(classes.getName()).build())
                                                    .build())
                                                .build())
                                .build();
                Session session = Session.create(params);

                return session;

        }catch(StripeException e){
            System.out.println("Stripe Exception MainStripe");
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Session buySubscription(HttpServletRequest request, HttpServletResponse response, Membership membership)throws StripeException {

        BigDecimal newVal = membership.getValue().multiply(new BigDecimal("100"));

        try {

            SessionCreateParams.LineItem.PriceData.Recurring recurring = SessionCreateParams.LineItem.PriceData.Recurring
                    .builder()
                    .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                    .build();

            String YOUR_DOMAIN = request.getHeader("origin");
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setSuccessUrl(YOUR_DOMAIN + "/account/memberships")
                            .setCancelUrl(YOUR_DOMAIN + "/")
                            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                                    .setUnitAmountDecimal(newVal)
                                                    .setCurrency("gbp")
                                                    .setRecurring(recurring)
                                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder().setName(membership.getCode()).build())
                                                    .build())
                                            .build())
                            .build();

            Session session = Session.create(params);
            return session;

        }catch(StripeException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Subscription checkIdSessionIsActive(String reference) throws StripeException{

        try{
            Session session = Session.retrieve(reference);

            Subscription subscription = Subscription.retrieve(session.getSubscription());

            if(subscription.getStatus().compareTo("active") == 0){
                return subscription;
            }
            return subscription;
        }catch(StripeException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }


    }

    public void cancelSubscriptionAsap(String sub)throws StripeException{
            try{

                Subscription subscription = Subscription.retrieve(sub);

                subscription.cancel(SubscriptionCancelParams.builder().build());

            }catch(StripeException e){
                throw new RuntimeException(e.getMessage());
            }
    }

    public Session checkBookingPaid(String booking) throws StripeException{
        try{
            Session session = Session.retrieve(booking);
            return session;

        }catch(StripeException e){
            throw new RuntimeException(e.getMessage());
        }


    }


}
