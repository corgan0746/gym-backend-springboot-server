package com.corgan.angularbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "active")
    private boolean active;

    @Column(name = "phone")
    private BigInteger phone;

    @Column(name = "privileges")
    private boolean privileges;

    @Column(name = "image")
    private String image;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_membership_id")
    private UserMembership userMembership;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private Set<Bookings> bookings = new HashSet<>();


    public void add(Bookings booking){
        if(booking != null){
            if(bookings == null){
                bookings = new HashSet<>();
            }

            bookings.add(booking);
            booking.setCustomer(this);
        }
    }


    //    @JoinColumn(name = "order_id")
//    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
//    private Set<Order> orders = new HashSet<>();
//
//    public void add(Order order){
//        if(order!=null){
//            if(orders==null){
//                orders = new HashSet<>();
//            }
//
//            orders.add(order);
//            order.setCustomer(this);
//        }
//    }

}
