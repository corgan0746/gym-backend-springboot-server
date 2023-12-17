package com.corgan.angularbackend.taskmanager;

import com.corgan.angularbackend.dao.BookingsRepository;
import com.corgan.angularbackend.service.StripeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.TimeZone;

@Component
public class ScheduledTasks {

    private BookingsRepository bookingsRepository;
    private StripeService stripeService;

    public ScheduledTasks(
            BookingsRepository bookingsRepository,
            StripeService stripeService
    ){
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        this.bookingsRepository = bookingsRepository;
        this.stripeService = stripeService;
    }

    @Scheduled(cron = "0 49 23 * * *")
    @Transactional
    public void pingText(){
        Long currentDate = (System.currentTimeMillis() / 1000L);

        this.bookingsRepository.deleteAll();

        this.stripeService.deleteExpiredMemberships(currentDate);

    }
}
