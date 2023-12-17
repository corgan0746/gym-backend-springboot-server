package com.corgan.angularbackend.rest;

import com.corgan.angularbackend.service.Aws3Service;
import com.corgan.angularbackend.dao.*;
import com.corgan.angularbackend.datamodels.*;
import com.corgan.angularbackend.datamodels.MembershipClassesResponse;
import com.corgan.angularbackend.entity.*;
import com.corgan.angularbackend.service.CustomerService;
import com.corgan.angularbackend.service.InstructorService;
import com.corgan.angularbackend.service.MembershipClassesService;
import com.corgan.angularbackend.service.StripeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DemoRestController {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private InstructorService instructorService;
    private MembershipClassesService membershipClassesService;
    private CustomerService customerService;
    private StripeService stripeService;
    private final ClassesRepository classesRepository;
    private final MembershipRepository membershipRepository;
    private final AdminRepository adminRepository;

    private BookingsRepository bookingsRepository;

    private MembershipClassesRepository membershipClassesRepository;
    private Aws3Service aws3;
    private ClassTypesRepository classTypesRepository;

    public DemoRestController(InstructorService instructorService,
                              MembershipClassesService membershipClassesService,
                              BookingsRepository bookingsRepository,
                              CustomerService customerService,
                              StripeService stripeService,
                              ClassesRepository classesRepository,
                              MembershipRepository membershipRepository,
                              AdminRepository adminRepository,
                              MembershipClassesRepository membershipClassesRepository,
                              ClassTypesRepository classTypesRepository,
                              Aws3Service aws3
                              ) {
        this.classesRepository = classesRepository;
        this.instructorService = instructorService;
        this.membershipClassesService = membershipClassesService;
        this.customerService = customerService;
        this.stripeService = stripeService;
        this.membershipRepository = membershipRepository;
        this.adminRepository = adminRepository;
        this.bookingsRepository = bookingsRepository;
        this.membershipClassesRepository = membershipClassesRepository;
        this.aws3 = aws3;
        this.classTypesRepository = classTypesRepository;
    }

    @PostMapping("/getCustomerByToken")
    public Customer customerByToken(HttpServletRequest request){

        try{
            String requestToken = request.getHeader("Authorization");
            requestToken = requestToken.replaceAll("Bearer ", "");

            String email = this.customerService.checkFirebaseToken(requestToken);

            return customerService.getCustomerWithEmail(email);

        }catch(Exception e){
            System.out.println("Error checking token");
            throw new RuntimeException("err");
        }
    }

    @PostMapping("/getCustomerWithMembership")
    public Customer customerByWithMembership(HttpServletRequest request){

        try{

            String requestToken = request.getHeader("Authorization");
            requestToken = requestToken.replaceAll("Bearer ", "");

            String email = this.customerService.checkFirebaseToken(requestToken);
            return customerService.getCustomerWithMembershipChecked(email);

        }catch(Exception e){
            System.out.println(e);
            throw new RuntimeException("err");
        }
    }

    @PostMapping("/getCustomerWithBookings")
    public Customer customerByWithBookings( HttpServletRequest request){

        try{

            String requestToken = request.getHeader("Authorization");
            requestToken = requestToken.replaceAll("Bearer ", "");

            String email = this.customerService.checkFirebaseToken(requestToken);

            return customerService.getCustomerWithBookingsChecked(email);

        }catch(Exception e){
            System.out.println("Error checking token " + e.getMessage());
            throw new RuntimeException("err");
        }
    }


    @PostMapping("/login")
    public Object loginFunc(HttpServletRequest request, HttpServletResponse response){

        String requestToken = request.getHeader("Authorization");
        requestToken = requestToken.replaceAll("Bearer ", "");

        try{
            String email = this.customerService.checkFirebaseToken(requestToken);
            return customerService.getCustomerWithEmail(email);

        }catch(Exception e){
            System.out.println("Error checking token");
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping("/register")
    public ResponseEntity registerAccount(@RequestBody RegistryPostRequest req){

        boolean emailFirebaseExist = false;
        boolean emailDb = false;
        boolean createdUser = false;


        String passwordCheck = "";
        try {
            emailFirebaseExist = this.customerService.checkFirebaseEmail(req.getEmail());
            emailDb = this.customerService.checkEmail(req.getEmail());

            if(this.customerService.checkAllOtherFields(req)){
                ResponseEntity res = new ResponseEntity("There was a problem with 1 or more Fields",HttpStatusCode.valueOf(400));
                return res;
            }

            passwordCheck = this.customerService.checkPasswordCreation(req.getPassword());

            if( passwordCheck == "") {
                createdUser = this.customerService.createFirebaseAndDbUser(req);
            }else{
                throw new RuntimeException("password error");
            }


        }catch(Exception e){
            emailFirebaseExist = false;
        }

        if(passwordCheck != ""){
            ResponseEntity res = new ResponseEntity(passwordCheck,HttpStatusCode.valueOf(400));
            return res;
        }

        if(createdUser){
            ResponseEntity<String> res = new ResponseEntity<>("{\"message\":\"User Created yey\"}", HttpStatus.OK);
            return res;
        }


        if(emailFirebaseExist || emailDb){
            ResponseEntity res = new ResponseEntity("Email Already Exists",HttpStatusCode.valueOf(409));
            return res;
        }


        ResponseEntity<String> res = new ResponseEntity<>("{\"message\":\"All good\"}", HttpStatus.OK);
        return res;
    }

    @PostMapping("/verifyToken")
    public ResponseEntity verify(HttpServletRequest request){

        try {
            String email = request.getAttribute("emailRef").toString();

            Customer customer = this.customerService.getCustomerWithEmail(email);

            if(customer != null) {
                return ResponseEntity.ok(HttpStatus.OK);
            }

            return new ResponseEntity("invalid Token", HttpStatusCode.valueOf(409));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    @GetMapping("/instructorClassesSort")
    public List instructorsClassesSort(){
        List instructors = instructorService.instructorsWithClassesCountSort();
        return instructors;
    }

    @GetMapping("/instructorClasses")
    public List instructorsWithClasses(){
        List instructors = instructorService.instructorsWithClasses();
        return instructors;
    }

    @PostMapping("/loginAdmin")
    public String loginToPanel(@RequestBody AdminCredentials credentials, HttpServletRequest request, HttpServletResponse response){
        String email = request.getAttribute("emailRef").toString();

        Customer customer = this.customerService.getCustomerWithEmail(email);

        if(customer.isPrivileges()){

            List<Admin> adminList = this.adminRepository.findByUsername(credentials.getUsername());

            if(adminList.size() < 0){
                return"less than 0";
            }
            Admin admin = adminList.get(0);

            if(admin.getPassword().compareTo(credentials.getPassword()) == 0){
                return "true";
            }
        }
        return "last";
    }

    @GetMapping("/membershipClasses")
    public List allMembershipClasses() {
        List classes = membershipClassesService.returnAllMembershipClasses();
        return classes;
    }

    @GetMapping("/allClassTypes")
    public List<ClassTypes> getAllClassTypes(){

        try{
            return this.classTypesRepository.findAll();
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/getByType/{id}")
    public List<Classes> getClassesByType(@PathVariable Long id){
        try{
            return this.classesRepository.findAllByClassTypesId(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    @GetMapping("/getClassesFromMembership/{id}")
    public MembershipClassesResponse getById(@PathVariable int id){
        return membershipClassesService.getMembershipWithClassesById(id);
    }


    @GetMapping("/getCustomer/{id}")
    public List getCustomer1(@PathVariable int id){
        List<Customer> list = customerService.customerDetails(id);
        return list;
    }

    @PostMapping("/changeDetails")
    public Object changeCustomerDetails(HttpServletRequest req,@RequestBody CustomerRequest customerRequest){
        try {
            Customer currentCustomer = customerFromEmail(req);
            boolean changedEmailSuccess = false;

            if(customerRequest.getEmail() != null) {
                changedEmailSuccess = this.customerService.changeFirebaseEmail(currentCustomer.getEmail(), customerRequest.getEmail());

                if (!changedEmailSuccess) {
                    ResponseEntity res = new ResponseEntity("Email Already Exists", HttpStatusCode.valueOf(409));
                    return res;
                }
            }

            currentCustomer.setFirstName(customerRequest.getFirstName());
            currentCustomer.setLastName(customerRequest.getLastName());
            currentCustomer.setPhone(customerRequest.getPhone());
            currentCustomer.setEmail(customerRequest.getEmail());

            this.customerService.saveCustomer(currentCustomer);

            return this.customerService.getCustomerWithEmail(customerRequest.getEmail());

        }catch(Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }




    @PostMapping("/buyOnce")
    public ResponseEntity sendCheckout(@RequestBody CodePostRequest code ,HttpServletRequest req, HttpServletResponse res){

        if(!this.customerService.checkTimeslotInClass(code.getCode(), code.getSlotId())){
            return null;
        }
        if(code.getCode() == null){
            return null;
        }

        if(code.getSlotId() == null){
            return null;
        }

        String requestToken = req.getHeader("Authorization");
        requestToken = requestToken.replaceAll("Bearer ", "");

        try {
            boolean membershipValue = false;

            String email = this.customerService.checkFirebaseToken(requestToken);

            Customer customer = customerService.getCustomerWithEmail(email);

            List<Classes> classes = classesRepository.findByCode(code.getCode());
            if(classes.size() != 1){
                return null;
            }



            List<Bookings> bookings = this.bookingsRepository.findAllByCustomerAndClasses(customer, classes.get(0));

            if(bookings.size() > 0){
                return new ResponseEntity("Already booked this class for today", HttpStatusCode.valueOf(409));
            }

            if(customer.getUserMembership().getMembership() != null) {
                CompositeKeyMembership key = new CompositeKeyMembership(customer.getUserMembership().getMembership().getId(), classes.get(0).getId());

                if(this.membershipClassesRepository.findById(key) != null){
                    membershipValue = true;
                }
            }

            CustomerAndUrl customerNewBooking = stripeService.buyOnce(req, res, classes.get(0), customer, code.getSlotId(), membershipValue);
            customerService.addBooking(customerNewBooking.getCustomer());

            RedirectPayload redirect = new RedirectPayload();
            redirect.setUrl((membershipValue)? "membership-value" :customerNewBooking.getUrl());

            ResponseEntity<RedirectPayload> response = new ResponseEntity<>(redirect, HttpStatus.OK);

            return response;

        }catch(Exception e){
            System.out.println(e);
            ResponseEntity res2 = new ResponseEntity(e.getMessage(),HttpStatusCode.valueOf(409));
            return res2;
        }
    }

    @PostMapping("/buyMembership")
    public ResponseEntity sendCheckoutMembership(@RequestBody CodePostRequest code ,HttpServletRequest req, HttpServletResponse res){
        if(code.getCode() == null){
            return null;
        }

        String requestToken = req.getHeader("Authorization");
        requestToken = requestToken.replaceAll("Bearer ", "");

        try {

            String email = this.customerService.checkFirebaseToken(requestToken);
            Customer customer = customerService.getCustomerWithEmail(email);

            List<Membership> membership = membershipRepository.findByCode(code.getCode());
            if(membership.size() != 1){
                return null;
            }

            CustomerAndUrl customerNewMembership = stripeService.buyMembership(req, res, membership.get(0), customer );
            customerService.addMembership(customerNewMembership.getCustomer());

            RedirectPayload redirect = new RedirectPayload();
            redirect.setUrl(customerNewMembership.getUrl());

            ResponseEntity<RedirectPayload> response = new ResponseEntity<>(redirect, HttpStatus.OK);
            return response;

        }catch(Exception e){
            System.out.println(e);
            ResponseEntity res2 = new ResponseEntity(e.getMessage(),HttpStatusCode.valueOf(409));
            return res2;
        }
    }

    @PostMapping("/profilePicture")
    public ResponseEntity profilePicture(HttpServletRequest request, @RequestPart("file") MultipartFile file){

        try {

            if(!file.getContentType().equals("image/webp")){
                throw new RuntimeException("Only webp Pictures allowed");
            }

            Customer currentCustomer = customerFromEmail(request);

            byte[] bytes = file.getBytes();
            UUID uuid = UUID.randomUUID();

            aws3.uploadImage(bytes, bucketName, uuid.toString(), "");

            if(currentCustomer.getImage() != null){
                aws3.deleteImage(bucketName, currentCustomer.getImage());
            }

            currentCustomer.setImage(uuid.toString());

            this.customerService.saveCustomer(currentCustomer);

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(409));
        }
    }

    public Customer customerFromEmail(HttpServletRequest request){

        try {
            String email = request.getAttribute("emailRef").toString();
            return this.customerService.getCustomerWithEmail(email);

        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
