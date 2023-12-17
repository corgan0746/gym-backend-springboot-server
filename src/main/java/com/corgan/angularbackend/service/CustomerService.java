package com.corgan.angularbackend.service;

import com.corgan.angularbackend.customdao.CustomerDAOImpl;
import com.corgan.angularbackend.dao.BookingsRepository;
import com.corgan.angularbackend.dao.ClassesRepository;
import com.corgan.angularbackend.dao.CustomerRepository;
import com.corgan.angularbackend.dao.TimeslotsRepository;
import com.corgan.angularbackend.datamodels.RegistryPostRequest;
import com.corgan.angularbackend.entity.*;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Service
public class CustomerService {

    private CustomerDAOImpl dao;
    private StripeService stripeService;
    private BookingsRepository bookingsRepository;
    private ClassesRepository classesRepository;
    private TimeslotsRepository timeslotsRepository;
    private CustomerRepository customerRepository;

    public CustomerService(CustomerDAOImpl dao,
                           StripeService stripeService,
                           BookingsRepository bookingsRepository,
                           ClassesRepository classesRepository,
                           TimeslotsRepository timeslotsRepository,
                           CustomerRepository customerRepository
    ) {
        this.dao = dao;
        this.stripeService = stripeService;
        this.bookingsRepository = bookingsRepository;
        this.classesRepository = classesRepository;
        this.timeslotsRepository = timeslotsRepository;
        this.customerRepository =  customerRepository;
    }

    public List<Customer> customerDetails(int id){

        List<Customer> result = dao.getFullCustomerDetails(id);
        return result;
    }

    public boolean checkTimeslotInClass(String code, Long timeslotId){
        List<Classes> classes = this.classesRepository.findByCode(code);
        Classes classes2 = classes.get(0);

        Timeslots timeslot = this.timeslotsRepository.getById(timeslotId);
        return classes2.getTimeslots().contains(timeslot);
    }

    public boolean checkFirebaseEmail(String email){

        try {

            UserRecord record = FirebaseAuth.getInstance().getUserByEmail(email);
            return true;

        } catch (FirebaseAuthException e) {
            System.out.println("Error inside verify function: "+e.getMessage());
            return false;
        }

    }

    public boolean changeFirebaseEmail(String currentEmail, String newEmail){
        try {
            UserRecord record = FirebaseAuth.getInstance().getUserByEmail(currentEmail);
            FirebaseAuth.getInstance().updateUser(record.updateRequest().setEmail(newEmail));

            return true;
        } catch (FirebaseAuthException e) {
            System.out.println("Error changing email: "+e.getMessage());
            return false;
        }
    }

    public String checkFirebaseToken(String token){

        try {
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
            return firebaseToken.getEmail();

        }catch (FirebaseAuthException e){
            throw new RuntimeException(e);
        }


    }

    public boolean createFirebaseAndDbUser(RegistryPostRequest reg){
        try{

            UserRecord.CreateRequest req = new UserRecord.CreateRequest();

            req.setEmail(reg.getEmail());
            req.setPassword(reg.getPassword());

            FirebaseAuth.getInstance().createUser(req);

            Customer newCustomer = new Customer();

            BigInteger phoneNum = new BigInteger(reg.getPhone());

            Address address = new Address();
            address.setCity(reg.getCity());
            address.setStreet(reg.getAddress());
            address.setCountry(reg.getCountry());
            address.setPostCode(reg.getPostcode());

            UserMembership userMembership = new UserMembership();
            userMembership.setMembership(null);
            userMembership.setPaid(false);
            userMembership.setActive(false);
            userMembership.setRenew(false);
            userMembership.setEndDate(null);
            userMembership.setReference(null);

            newCustomer.setActive(true);
            newCustomer.setEmail(reg.getEmail());
            newCustomer.setAddress(address);
            newCustomer.setPhone(phoneNum);
            newCustomer.setFirstName(reg.getFirstName());
            newCustomer.setLastName(reg.getLastName());
            newCustomer.setBookings(new HashSet<>());
            newCustomer.setUserMembership(userMembership);

            this.dao.saveCustomer(newCustomer);

            return true;

        }catch (FirebaseException e){
            System.out.println(e);
            return false;
        }
    }

    public String checkPasswordCreation(String pass){
        if(!pass.matches("^.{7,64}$")) return "Password Length must be between 7 to 12 characters long.";
        if(!pass.matches(".*[!@#$%^&*()_+={}\\\\[\\\\]|\\\\\\\\:;\\\"'<>,.?/-].*")) return "Password must contain at least 1 special character.";
        if(!pass.matches("^[^\\\\s]+$")) return "Password can't contain blank spaces";
        if(!pass.matches(".*[A-Z].*")) return "Password must contain at least 1 uppercase character.";
        if(!pass.matches(".*[0-9].*")) return "Password must contain at least 1 number.";
        return "";
    }

    public boolean checkSingleProperty(String property, String value){

        if(property == "phone"){
            System.out.println("phone");
            return value.matches("^\\d{10,11}$");
        }
        if(property == "address"){
            System.out.println("address");
            return value.matches("^[a-zA-Z0-9 ]{6,45}$");
        }
        if(property == "postcode"){
            System.out.println("postcode");
            return value.matches("^[a-zA-Z0-9 ]{6,8}$");
        }
        if(property == "email"){
            System.out.println("email");
            return value.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        }

        System.out.println("other");
        return value.matches("^[a-zA-Z]{3,25}$");

    }

    public boolean checkAllOtherFields(RegistryPostRequest req){

        if(
                this.checkSingleProperty("email",req.getEmail()) &&
                this.checkSingleProperty("address",req.getAddress()) &&
                this.checkSingleProperty("",req.getCity()) &&
                this.checkSingleProperty("",req.getCountry()) &&
                this.checkSingleProperty("postcode",req.getPostcode()) &&
                this.checkSingleProperty("",req.getFirstName()) &&
                this.checkSingleProperty("",req.getLastName()) &&
                this.checkSingleProperty("phone",req.getPhone())
        ) {
            // If there is no problems with fields in general we send Ok response
            return false;
        }else{
            // If there is a problem with any field we trigger the Error
            return true;
        }
    }


    public void addBooking(Customer customer){
        dao.saveCustomer(customer);
    }

    public void addMembership(Customer customer) { dao.saveCustomer(customer); }

    public boolean checkEmail(String email){
        return dao.checkEmail(email);
    }

    public Customer getCustomerWithEmail(String email){ return dao.getCustomerByEmail(email);}

    @Transactional
    public Customer getCustomerWithBookingsChecked(String email){

        Customer updatedCustomer = dao.getCustomerByEmail(email);

        Set<Bookings> bookings = updatedCustomer.getBookings();

        Set<Bookings> newBookings = new HashSet<>();

        for(Bookings booking: bookings){

            if(booking.isPaid() && booking.isActive()){
                newBookings.add(booking);
                continue;
            }
            if(!booking.isActive() || (booking.getReference() == null)){
                booking.setCustomer(null);
                bookingsRepository.deleteById(booking.getId());
                continue;
            }

            Session checkoutStatus = null;

            try {
                checkoutStatus = this.stripeService.checkBooking(booking.getReference());
            }catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
                if(checkoutStatus.getStatus().compareTo("expired") == 0){
                    booking.setCustomer(null);
                    bookingsRepository.deleteById(booking.getId());
                    continue;
                }


                if(checkoutStatus.getPaymentStatus().compareTo("paid") == 0){
                    booking.setPaid(true);
                    newBookings.add(booking);
                    continue;
                }

                if(checkoutStatus.getPaymentStatus().compareTo("unpaid") == 0) {
                    continue;
                }

            newBookings.add(booking);
        }

        updatedCustomer.setBookings(newBookings);

        dao.saveCustomer(updatedCustomer);

        return updatedCustomer;

    }

    @Transactional
    public Customer getCustomerWithMembershipChecked(String email){

        Customer updatedCustomer = dao.getCustomerByEmail(email);

        try {
            if(updatedCustomer.getUserMembership().getEndDate() != null) {
                if (updatedCustomer.getUserMembership().getEndDate() < (System.currentTimeMillis() / 1000L)) {
                    if (updatedCustomer.getUserMembership().isActive()) {
                        return updatedCustomer;
                    }
                }
            }

            Subscription subscription = this.stripeService.checkMembership(updatedCustomer.getUserMembership().getReference());
            if (subscription.getStatus().compareTo("active") == 0) {
                updatedCustomer.getUserMembership().setActive(true);
                updatedCustomer.getUserMembership().setPaid(true);
                updatedCustomer.getUserMembership().setRenew(true);
                updatedCustomer.getUserMembership().setEndDate(subscription.getCurrentPeriodEnd());
                dao.saveCustomer(updatedCustomer);
                return updatedCustomer;
            }
            updatedCustomer.getUserMembership().setActive(false);
            updatedCustomer.getUserMembership().setPaid(false);
            updatedCustomer.getUserMembership().setRenew(false);
            updatedCustomer.getUserMembership().setMembership(null);
            updatedCustomer.getUserMembership().setEndDate(null);
            dao.saveCustomer(updatedCustomer);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            updatedCustomer.getUserMembership().setActive(false);
            updatedCustomer.getUserMembership().setPaid(false);
            updatedCustomer.getUserMembership().setRenew(false);
            updatedCustomer.getUserMembership().setMembership(null);
            updatedCustomer.getUserMembership().setEndDate(null);
            dao.saveCustomer(updatedCustomer);
            return updatedCustomer;
        }

        return updatedCustomer;
    }

    @Transactional
    public void saveCustomer(Customer customer){
        this.customerRepository.save(customer);
    }

}
