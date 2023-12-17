package com.corgan.angularbackend.rest;

import com.corgan.angularbackend.dao.*;
import com.corgan.angularbackend.datamodels.*;
import com.corgan.angularbackend.entity.*;
import com.corgan.angularbackend.service.Aws3Service;
import com.corgan.angularbackend.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/action/dispatch")
public class AdminActionController {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    private CustomerRepository customerRepository;
    private UserMembershipRepository userMembershipRepository;
    private MembershipRepository membershipRepository;
    private AddressRepository addressRepository;
    private BookingsRepository bookingsRepository;
    private ClassesRepository classesRepository;
    private InstructorRepository instructorRepository;
    private LocationRepository locationRepository;
    private TimeslotsRepository timeslotsRepository;
    private ClassTypesRepository classTypesRepository;
    private MembershipClassesRepository membershipClassesRepository;
    private CustomerService customerService;

    private Aws3Service aws3Service;

    public AdminActionController(MembershipClassesRepository membershipClassesRepository,
                                 CustomerRepository customerRepository,
                                 CustomerService customerService,
                                 InstructorRepository instructorRepository,
                                 LocationRepository locationRepository,
                                 TimeslotsRepository timeslotsRepository,
                                 ClassTypesRepository classTypesRepository,
                                 UserMembershipRepository userMembershipRepository,
                                 MembershipRepository membershipRepository,
                                 AddressRepository addressRepository,
                                 BookingsRepository bookingsRepository,
                                 Aws3Service aws3Service,
                                 ClassesRepository classesRepository)
    {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
        this.userMembershipRepository = userMembershipRepository;
        this.membershipRepository = membershipRepository;
        this.addressRepository = addressRepository;
        this.bookingsRepository = bookingsRepository;
        this.classesRepository = classesRepository;
        this.instructorRepository = instructorRepository;
        this.locationRepository = locationRepository;
        this.timeslotsRepository = timeslotsRepository;
        this.classTypesRepository = classTypesRepository;
        this.membershipClassesRepository = membershipClassesRepository;
        this.aws3Service = aws3Service;

    }


    // Add privileges user Verification as a function for every endpoint

    @PostMapping("/createCustomer")
    public Object createCustomer(@RequestBody CustomerRequest customer, HttpServletRequest request){

        try {
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Customer referenceCustomer = this.customerRepository.findByEmail(customer.getEmail());

            if(referenceCustomer != null){
                throw new RuntimeException("Email already exists");
            }

            Customer newCustomer = new Customer();

            newCustomer.setEmail(customer.getEmail());
            newCustomer.setActive(true);
            newCustomer.setFirstName(customer.getFirstName());
            newCustomer.setLastName(customer.getLastName());
            newCustomer.setPhone(customer.getPhone());
            newCustomer.setPrivileges(false);
            newCustomer.setAddress(null);
            newCustomer.setUserMembership(null);
            newCustomer.setBookings(new HashSet<>());

            this.customerRepository.save(newCustomer);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }

    }

    @PostMapping("/getCustomers")
    public List<Customer> allCustomers(HttpServletRequest request){
        try {

            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            String email = request.getAttribute("emailRef").toString();
            Customer customer = this.customerService.getCustomerWithEmail(email);

            if(customer.isPrivileges()){
                return this.customerRepository.findAll();
            }

            throw new RuntimeException("User not authorized");
        }catch(Exception e){
            List<Customer> list = new ArrayList<>();
            return list;
        }
    }

    @PostMapping("/updateCustomer")
    public Object postThing(@RequestBody CustomerRequest customer, HttpServletRequest request){

        try {
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Customer referenceCustomer = this.customerRepository.getById(customer.getId());

            boolean changedEmailSuccess = this.customerService.changeFirebaseEmail(referenceCustomer.getEmail(), customer.getEmail());

            if(!changedEmailSuccess){
                ResponseEntity res = new ResponseEntity("Email Already Exists", HttpStatusCode.valueOf(409));
                return res;
            }

            // If value is not present or is null on the Request
            // Value will default to the existing one

            //If email is changed it should also be changed on FireBase
            referenceCustomer.setEmail((customer.getEmail() == null) ? referenceCustomer.getEmail() : customer.getEmail());
            referenceCustomer.setActive(customer.isActive());
            referenceCustomer.setFirstName((customer.getFirstName() == null) ? referenceCustomer.getFirstName() : customer.getFirstName());
            referenceCustomer.setLastName((customer.getLastName() == null) ? referenceCustomer.getLastName() : customer.getLastName());
            referenceCustomer.setPhone((customer.getPhone() == null) ? referenceCustomer.getPhone() : customer.getPhone());

            this.customerRepository.save(referenceCustomer);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }

    }

    @PostMapping("/deleteCustomer/{id}")
    @Transactional
    public ResponseEntity deleteCustomer(@PathVariable Long id, HttpServletRequest request){
        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Customer customerToDelete = this.customerRepository.getById(id);

            this.userMembershipRepository.delete(customerToDelete.getUserMembership());
            this.addressRepository.delete(customerToDelete.getAddress());
            this.bookingsRepository.deleteByCustomer(customerToDelete);

            this.customerRepository.deleteById(id);

            return ResponseEntity.ok(HttpStatus.OK);

        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/updateUsermembership")
    public ResponseEntity updateUsermembership(@RequestBody UserMembershipRequest userMembershipRequest, HttpServletRequest request){
        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            UserMembership userMembershipReference = this.userMembershipRepository.getById(userMembershipRequest.getId());

            userMembershipReference.setReference((userMembershipRequest.getReference() == null)? userMembershipReference.getReference(): userMembershipRequest.getReference());
            userMembershipReference.setPaid(userMembershipRequest.isPaid());
            userMembershipReference.setRenew(userMembershipRequest.isRenew());
            userMembershipReference.setActive(userMembershipRequest.isActive());
            userMembershipReference.setEndDate((userMembershipRequest.getEndDate() == null)? userMembershipReference.getEndDate() : userMembershipRequest.getEndDate());

            if(userMembershipRequest.getMembership() != null) {
                Membership membership = this.membershipRepository.getById(userMembershipRequest.getMembership());
                userMembershipReference.setMembership(membership);
            }

            this.userMembershipRepository.save(userMembershipReference);

            return ResponseEntity.ok(HttpStatus.OK);

        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }

    }

    @PostMapping("/resetUsermembership/{id}")
    public ResponseEntity resetUsermembership(@PathVariable Long id, HttpServletRequest request){

        //Might need to add interaction with Stripe to make sure the Membership is ended
        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            UserMembership userMembershipReference = this.userMembershipRepository.getById(id);

            userMembershipReference.setPaid(false);
            userMembershipReference.setRenew(false);
            userMembershipReference.setActive(false);
            userMembershipReference.setEndDate(null);
            userMembershipReference.setReference(null);
            userMembershipReference.setMembership(null);

            this.userMembershipRepository.save(userMembershipReference);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/updateBooking")
    public ResponseEntity updateBooking(@RequestBody BookingRequest bookingRequest, HttpServletRequest request){
        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Bookings booking = this.bookingsRepository.getById(bookingRequest.getId());

            booking.setValue((bookingRequest.getValue() != null)? bookingRequest.getValue(): booking.getValue());
            booking.setPaid(bookingRequest.isPaid());
            booking.setActive(bookingRequest.isActive());
            booking.setReference((bookingRequest.getReference() != null)? bookingRequest.getReference() : booking.getReference());
            booking.setDateCreated((bookingRequest.getDateCreated() != null)? bookingRequest.getDateCreated() : booking.getDateCreated() );

            if(bookingRequest.getClasses() != null) {
                Classes classes = this.classesRepository.getById(bookingRequest.getClasses());
                booking.setClasses(classes);
            }

            this.bookingsRepository.save(booking);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/deleteBooking/{id}")
    @Transactional
    public ResponseEntity deleteBooking(@PathVariable Long id, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Bookings booking = this.bookingsRepository.getById(id);

            booking.setCustomer(null);
            booking.setClasses(null);

            this.bookingsRepository.delete(booking);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/allClassTypes")
    public List<ClassTypes> getAllClassTypes(){

        try{
                return this.classTypesRepository.findAll();

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    @PostMapping("/updateClass")
    public ResponseEntity updateClass(@RequestBody ClassesRequest classesRequest, HttpServletRequest request){

        try{

            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Classes classReference = this.classesRepository.getById(classesRequest.getId());

            classReference.setName((classesRequest.getName() != null)? classesRequest.getName() : classReference.getName());
            classReference.setValue((classesRequest.getValue() != null)? classesRequest.getValue() : classReference.getValue());
            classReference.setCode((classesRequest.getCode() != null)? classesRequest.getCode() : classReference.getCode());
            classReference.setDescription((classesRequest.getDescription() != null)? classesRequest.getDescription() : classReference.getDescription());

            if(classesRequest.getInstructor() != null){
                Instructor instructor = this.instructorRepository.getById(classesRequest.getId());
                classReference.setInstructor(instructor);
            }


            if(classesRequest.getClassTypes() != null){
                ClassTypes type = this.classTypesRepository.getById(classesRequest.getClassTypes());
                classReference.setClassTypes(type);
            }


            if(classesRequest.getLocation() != null){
                Location location = this.locationRepository.getById(classesRequest.getLocation());
                classReference.setLocation(location);
            }

            this.classesRepository.save(classReference);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/classPicture/{id}")
    public ResponseEntity profilePicture(@PathVariable Long id,HttpServletRequest request, @RequestPart("file") MultipartFile file){

        try {

            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Classes classes = this.classesRepository.getById(id);

            byte[] bytes = file.getBytes();
            UUID uuid = UUID.randomUUID();

            aws3Service.uploadImage(bytes, bucketName, uuid.toString(), "");

            if(classes.getImage() != null){
                aws3Service.deleteImage(bucketName, classes.getImage());
            }

            classes.setImage(uuid.toString());
            this.classesRepository.save(classes);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }


    @PostMapping("/createClass")
    public ResponseEntity createClass(@RequestBody ClassesRequest classesRequest, HttpServletRequest request) {
        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Classes classes = new Classes();

            classes.setName(classesRequest.getName());
            classes.setValue(classesRequest.getValue());
            classes.setCode(classesRequest.getCode());
            classes.setDescription(classesRequest.getDescription());

            if(classesRequest.getInstructor() != null){
                Instructor instructor = this.instructorRepository.getById(classesRequest.getId());
                classes.setInstructor(instructor);
            }


            if(classesRequest.getClassTypes() != null){
                ClassTypes type = this.classTypesRepository.getById(classesRequest.getClassTypes());
                classes.setClassTypes(type);
            }


            if(classesRequest.getLocation() != null){
                Location location = this.locationRepository.getById(classesRequest.getLocation());
                classes.setLocation(location);
            }else{
                Location location = this.locationRepository.getById(1L);
                classes.setLocation(location);
            }

            this.classesRepository.save(classes);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }


    @PostMapping("/deleteClass/{id}")
    @Transactional
    public ResponseEntity deleteClass(@PathVariable Long id, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Classes classes = this.classesRepository.getById(id);

            this.membershipClassesRepository.deleteByClasses(classes);

            classes.setClassTypes(null);
            classes.setInstructor(null);
            classes.setLocation(null);

            this.classesRepository.delete(classes);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/addTimeslot")
    public ResponseEntity addTimeslot(@RequestBody TimeslotRequest timeslotRequest, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Timeslots timeslot = new Timeslots();

            timeslot.setStartTime(LocalTime.of(timeslotRequest.getStartHour(), timeslotRequest.getStartMinutes()));
            timeslot.setEndTime(LocalTime.of(timeslotRequest.getEndHour(), timeslotRequest.getEndMinutes()));

            Classes classes = this.classesRepository.getById(timeslotRequest.getClasses());

            classes.add(timeslot);

            this.classesRepository.save(classes);


            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/updateTimeslot")
    public ResponseEntity updateTimeslot(@RequestBody TimeslotRequest timeslotRequest, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Timeslots referenceTimeslot = this.timeslotsRepository.getById(timeslotRequest.getId());

            referenceTimeslot.setStartTime(LocalTime.of(timeslotRequest.getStartHour(), timeslotRequest.getStartMinutes()));
            referenceTimeslot.setEndTime(LocalTime.of(timeslotRequest.getEndHour(), timeslotRequest.getEndMinutes()));

            this.timeslotsRepository.save(referenceTimeslot);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/deleteTimeslot/{id}")
    @Transactional
    public ResponseEntity deleteTimeslot(@PathVariable Long id, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Timeslots timeslots = this.timeslotsRepository.getById(id);

            timeslots.setClasses(null);

            this.timeslotsRepository.delete(timeslots);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/addInstructor")
    public ResponseEntity addInstructor(@RequestBody InstructorRequest instructorRequest, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Instructor instructor = new Instructor();

            instructor.setEmail(instructorRequest.getEmail());
            instructor.setFirstName(instructorRequest.getFirstName());
            instructor.setLastName(instructorRequest.getLastName());
            instructor.setBios(instructorRequest.getBios());

            this.instructorRepository.save(instructor);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/updateInstructor")
    public ResponseEntity updateInstructor(@RequestBody InstructorRequest instructorRequest, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Instructor instructorReference = this.instructorRepository.getById(instructorRequest.getId());

            instructorReference.setEmail((instructorRequest.getEmail() != null)? instructorRequest.getEmail(): instructorReference.getEmail());
            instructorReference.setFirstName((instructorRequest.getFirstName() != null)? instructorRequest.getFirstName(): instructorReference.getFirstName());
            instructorReference.setLastName((instructorRequest.getLastName() != null)? instructorRequest.getLastName(): instructorReference.getLastName());
            instructorReference.setBios((instructorRequest.getBios() != null)? instructorRequest.getBios(): instructorReference.getBios());

            this.instructorRepository.save(instructorReference);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/instructorPicture/{id}")
    public ResponseEntity instructorPicture(@PathVariable Long id,HttpServletRequest request, @RequestPart("file") MultipartFile file){

        try {

            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Instructor instructor = this.instructorRepository.getById(id);

            byte[] bytes = file.getBytes();
            UUID uuid = UUID.randomUUID();

            aws3Service.uploadImage(bytes, bucketName, uuid.toString(), "");

            if(instructor.getImage() != null){
                aws3Service.deleteImage(bucketName, instructor.getImage());
            }

            instructor.setImage(uuid.toString());

            this.instructorRepository.save(instructor);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }


    @PostMapping("/deleteInstructor/{id}")
    @Transactional
    public ResponseEntity deleteInstructor(@PathVariable Long id, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Instructor instructorReference = this.instructorRepository.getById(id);

            this.instructorRepository.delete(instructorReference);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }


    @PostMapping("/assignClass")
    public ResponseEntity assignClass(@RequestBody InstructorClassRequest req, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Instructor instructorReference = this.instructorRepository.getById(req.getInstructor());

            Classes classes = this.classesRepository.getById(req.getClasses());
            classes.setInstructor(instructorReference);
            instructorReference.getClasses().add(classes);

            this.instructorRepository.save(instructorReference);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/removeInstructorClass")
    public ResponseEntity removeInstructorClass(@RequestBody InstructorClassRequest req, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Instructor instructorReference = this.instructorRepository.getById(req.getInstructor());

            Classes classes = this.classesRepository.getById(req.getClasses());

            classes.setInstructor(null);

            instructorReference.getClasses().remove(classes);

            this.instructorRepository.save(instructorReference);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }


    @PostMapping("/addClassToMembership")
    public ResponseEntity addClassToMembership(@RequestBody MembershipClassRequest memReq, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Membership membership = this.membershipRepository.getById(memReq.getMembership());
            Classes classes = this.classesRepository.getById(memReq.getClasses());

            MembershipClasses mem = new MembershipClasses(membership, classes);

            this.membershipClassesRepository.save(mem);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/removeClassFromMembership")
    @Transactional
    public ResponseEntity removeClassFromMembership(@RequestBody MembershipClassRequest memReq, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            CompositeKeyMembership key = new CompositeKeyMembership(memReq.getMembership().intValue(), memReq.getClasses().intValue());
            this.membershipClassesRepository.deleteById(key);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/addMembership")
    public ResponseEntity addMembership(@RequestBody MembershipRequest membershipRequest, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Classes classes = this.classesRepository.getById(membershipRequest.getClassId());
            if(classes == null){
                return new ResponseEntity("No proper class provided", HttpStatusCode.valueOf(409));
            }

            Membership membership = new Membership();

            membership.setName(membershipRequest.getName());
            membership.setCode(membershipRequest.getCode());
            membership.setActive(false);
            membership.setRenew(false);
            membership.setValue(membershipRequest.getValue());
            membership.setStartDate(new Date());
            membership.setExpireDate(new Date());

            this.membershipRepository.save(membership);

            MembershipClasses mem = new MembershipClasses(membership, classes);

            this.membershipClassesRepository.save(mem);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/updateMembership")
    public ResponseEntity updateMembership(@RequestBody MembershipRequest membershipRequest, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Membership referenceMembership = this.membershipRepository.getReferenceById(membershipRequest.getId());

            referenceMembership.setName((membershipRequest.getName() != null)? membershipRequest.getName() : referenceMembership.getName());
            referenceMembership.setCode((membershipRequest.getCode() != null)? membershipRequest.getCode() : referenceMembership.getCode());
            referenceMembership.setActive(membershipRequest.isActive());
            referenceMembership.setRenew(false);
            referenceMembership.setValue((membershipRequest.getValue() != null)? membershipRequest.getValue() : referenceMembership.getValue());

            this.membershipRepository.save(referenceMembership);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/membershipPicture/{id}")
    public ResponseEntity membershipPicture(@PathVariable Long id,HttpServletRequest request, @RequestPart("file") MultipartFile file){

        try {

            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Membership membership = this.membershipRepository.getById(id);

            byte[] bytes = file.getBytes();
            UUID uuid = UUID.randomUUID();

            aws3Service.uploadImage(bytes, bucketName, uuid.toString(), "");

            if(membership.getImage() != null){
                aws3Service.deleteImage(bucketName, membership.getImage());
            }

            membership.setImage(uuid.toString());
            this.membershipRepository.save(membership);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/deleteMembership/{id}")
    @Transactional
    public ResponseEntity deleteMembership(@PathVariable Long id, HttpServletRequest request){

        try{
            boolean adminCheck = verifyPermission(request);
            if(!adminCheck){
                throw new RuntimeException("not allowed");
            }

            Membership membership = this.membershipRepository.getById(id);

            this.membershipClassesRepository.deleteByMembership(membership);

            this.membershipRepository.delete(membership);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @GetMapping("/membershipClasses")
    public List<MembershipClasses> membershipClasses(){
        return this.membershipClassesRepository.findAll();
    }



    public boolean verifyPermission(HttpServletRequest request){
        try {
            String email = request.getAttribute("emailRef").toString();
            Customer customer = this.customerService.getCustomerWithEmail(email);

            if(customer.isPrivileges()){
                return true;
            }

            return false;
        }catch(Exception e){
            return false;
        }
    }


}
