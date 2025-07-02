package tech.vat78.orchestrators.camunda;

public class EodCamundaConstants {

    public static final String EOD_START_WORKER = "eodStartWorker";
    public static final String EOD_INTEREST_PAY_WORKER = "clientInterestPayment";
    public static final String EOD_INTEREST_CLAIM_WORKER = "debtInterestClaim";
    public static final String EOD_SWITCH_DAY_WORKER = "switchOpenDay";
    public static final String EOD_CLIENT_INTEREST_WORKER = "clientInterestCalculation";
    public static final String EOD_DEBT_INTEREST_WORKER = "debtInterestCalculation";
    public static final String EOD_OVERDUE_WORKER = "overdueStep";


    public static final String CONTEXT_VARIABLE = "eodContext";
    public static final String THREAD_ID_VARIABLE = "threadId";
}
