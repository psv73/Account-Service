# Account Service

Structure of project:

Authentication

    POST api/auth/signup        allows the account to register on the service;
    POST api/auth/changepass    changes a account password.
    
Business functionality

    GET api/empl/payment        gives access to the employee's payrolls;
    POST api/acct/payments      uploads payrolls;
    PUT api/acct/payments       updates payment information.

Service functionality

    PUT api/admin/account/role      changes account roles;
    DELETE api/admin/user           deletes a account;
    GET api/admin/user              displays information about all users;
    PUT api/admin/user/access       lock/unlock the user.

The security of service:

                            Anonymous   User    Accountant Administrator   Auditor
    POST api/auth/signup        +       +           +           +           +
    POST api/auth/changepass    -       +           +           +           -
    GET api/empl/payment        -       +           +           -           -
    POST api/acct/payments      -       -           +           -           -
    PUT api/acct/payments       -       -           +           -           -
    GET api/admin/user          -       -           -           +           -
    DELETE api/admin/user       -       -           -           +           -
    PUT api/admin/user/role     -       -           -           +           -
    PUT api/admin/user/access   -       -           -           +           -
    GET api/security/events     -       -           -           -           +

The service must log information security events:
    
        Description	                    Event Name
    ----------------------------------------------------
    A user has been successfully        CREATE_USER 
    registered	

    A user has changed the password     CHANGE_PASSWORD 
    successfully	

    A user is trying to access a        ACCESS_DENIED
    resource without access rights	

    Failed authentication	            LOGIN_FAILED

    A role is granted to a user         GRANT_ROLE
    
    A role has been revoked	            REMOVE_ROLE

    The Administrator has locked        LOCK_USER 
    the user

    The Administrator has unlocked      UNLOCK_USER 
    a user	

    The Administrator has deleted       DELETE_USER
    a user

    A user has been blocked on          BRUTE_FORCE
    suspicion of a brute force attack

The composition of the security event fields is:

    {
        "date": "<date>",
        "action": "<event_name from table>",
        "subject": "<The user who performed the action>",
        "object": "<The object on which the action was performed>",
        "path": "<api>"
    }

If it is impossible to determine a user, output Anonymous in the subject field.

Endpoint POST api/auth/signup:

Request:

    {
        "name": "<String value, not empty>",
        "lastname": "<String value, not empty>",
        "email": "<String value, not empty>",
        "password": "<String value, not empty>"
    }

Response:

    {
        "name": "<String value>",
        "lastname": "<String value>",
        "email": "<String value>"
    }

