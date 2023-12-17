#    VERY IMPORTANT!!!!!!!!!!!!!!!!!!
#    CAREFUL WHEN USING LOMBOK @DATA USE @GETTER AND @SETTER INSTEAD ON MAIN ENTITIES, AS IT WILL TRIGGER 
#    STACKOVERFLOW ON HEAVY LOADED ENTITIES (MAIN ENTITIES) LIKE CUSTOMERS OR CLASSES WITH MANY RELATION 
#    SHIPS, CAN ALSO BE RELATED AS TO WHICH ENTITY IS DRIVING THE RELATIONSHIP.


- Important: Need a Layer to Identify a User and Allow it to do Certain Actions based on $Role?

# -Working on the modified bookings to iterate through them, update and save/return to user
curent DB = myDataB2
Will probably integrate an automated system that will expire all the active bookings or just delete them
from the db.

Would think about actively check the active membership or just check by date, maybe set active to null to all
membership at 12pm so system will have to check again if the membership is active.

buyOnce should not create a stripe checkout and just add the booking to db with active and paid as true.

Aws S3 is finally working - need to verify CORS on it to work properly on the domain.
Need to add a way to compress images before upload, probably on the client side.

# Things to work on

-Add pictures to Instructors, Classes and Customers
-Add necessary endpoints to be exposed:
    Instructors:
        Instructors with Classes / order by assigned Classes Count - (Done
    Classes:
        Classes by type - (Done
        Classes sorted by value (Done
        Classes by Location (Done
    Membership:
        Membership by value sorted (Done
    MembershipClasses:
        Membership with Classes by id - (Done
    

-Internal Functions:
    Customer:
        Customer Add
        Customer Add Booking (Done
        Customer Add Membership (Done
        Edit
        Customer Remove Membership (Done
    Membership:
        Add Class
        Remove Class
        Modify Properties
        Delete
    Classes:
        Create Class
        Set Location
        Change Properties
    Instructor:
        Create
        Delete
        Assign Class
    MembershipClasses:
        Add/Delete Classes from Membership
    Location:
        Add/Delete/Edit
    Bookings:
        Create/Delete/Edit
        Change State
    

# Probably would have to Change the ErrorHandling on responses
# Now using ResponseEntity
    
    
        
-Block respective exposed Endpoints(Bookings,Customer, UserMembership)

# Learned 
- Can't use 2 repositories for the same entity as it will interfiere and won't work
  ( @RestResourceRepository and @Repository).
- @RestResourceRepository has http methods already assigned to the function depending on what
  action performs on the server, [ I think if it has a body it will be a POST request obviously]
  so i think i should be okay to include sensitive functions inside @RestResourceRepository as
  these won't be exposed to the client ( only GEt allowed)