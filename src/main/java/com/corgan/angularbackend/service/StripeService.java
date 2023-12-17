package com.corgan.angularbackend.service;

import com.corgan.angularbackend.dao.TimeslotsRepository;
import com.corgan.angularbackend.dao.UserMembershipRepository;
import com.corgan.angularbackend.datamodels.CustomerAndUrl;
import com.corgan.angularbackend.entity.*;
import com.corgan.angularbackend.stripe.MainStripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class StripeService {

    private MainStripe stripe;
    private TimeslotsRepository timeslotsRepository;
    private UserMembershipRepository userMembershipRepository;
    public StripeService(
            TimeslotsRepository timeslotsRepository,
            UserMembershipRepository userMembershipRepository
    ){
        this.timeslotsRepository = timeslotsRepository;
        this.userMembershipRepository = userMembershipRepository;
        this.stripe = new MainStripe();
    }



    public CustomerAndUrl buyOnce(HttpServletRequest req, HttpServletResponse res, Classes classes, Customer customer, Long slotId, boolean membershipValue ){

        try {
            Session reference = new Session();

            if(!membershipValue) {
                reference = this.stripe.buyOnce(req, res, classes);
            }

            Timeslots timeslot = this.timeslotsRepository.getById(slotId);

            Bookings booking = new Bookings();
            booking.setCustomer(customer);
            booking.setValue((membershipValue)? new BigDecimal("0") : classes.getValue());
            booking.setClasses(classes);
            booking.setDateCreated(new Date());
            booking.setReference((membershipValue)? "membership-value" :reference.getId());
            booking.setTimeslot(timeslot);
            booking.setActive(true);

            booking.setPaid((membershipValue)? true: false);

            customer.add(booking);

            CustomerAndUrl ref = new CustomerAndUrl();
            ref.setCustomer(customer);
            ref.setUrl((membershipValue)? null :reference.getUrl());

            return ref;

        }catch(Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Session checkBooking(String reference) throws StripeException {
        try {
            return this.stripe.checkBookingPaid(reference);
        }catch (StripeException e){
            throw new RuntimeException(e.getMessage());
        }

    }

    public Subscription checkMembership(String reference){
        try {
            return this.stripe.checkIdSessionIsActive(reference);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteExpiredMemberships(Long currentDate){

        List<UserMembership> userMembershipList = this.userMembershipRepository.findAllByEndDateLessThan(currentDate);

        if(userMembershipList.size() > 0){
            userMembershipList.forEach(membership -> {

                try {
                    this.stripe.cancelSubscriptionAsap(membership.getReference());
                }catch (StripeException e){
                    System.out.println(e);
                }

                membership.setReference(null);
                membership.setPaid(false);
                membership.setEndDate(null);
                membership.setRenew(false);
                membership.setActive(false);
                membership.setMembership(null);

            });
        }

        this.userMembershipRepository.saveAll(userMembershipList);
    }

    public CustomerAndUrl buyMembership(HttpServletRequest req, HttpServletResponse res, Membership membership, Customer customer ){

        try {
            Session reference = this.stripe.buySubscription(req, res, membership);

            customer.getUserMembership().setActive(false);
            customer.getUserMembership().setPaid(false);
            customer.getUserMembership().setRenew(false);
            customer.getUserMembership().setReference(reference.getId());
            customer.getUserMembership().setMembership(membership);

            CustomerAndUrl ref = new CustomerAndUrl();
            ref.setCustomer(customer);
            ref.setUrl(reference.getUrl());

            return ref;

        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }



}
