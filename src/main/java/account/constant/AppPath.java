package account.constant;

public final class AppPath {
    public static final String API = "/api";
    public static final String H2_CONSOLE = "/h2-console/**";
    public static final String ACTUATOR_SHUTDOWN = "/actuator/shutdown";

    public static final String SIGN_UP = API + "/auth/signup";
    public static final String CHANGE_PASS = API + "/auth/changepass";
    public static final String PAYMENT = API + "/empl/payment";
    public static final String PAYMENTS = API + "/acct/payments";
    public static final String SECURITY_EVENT = API + "/security/events";
    public static final String USER_ROLE = API + "/admin/user/role";
    public static final String USER = API + "/admin/user";
    public static final String USER_ACCESS = API +"/admin/user/access";
}
