package fish.payara.webcomponentjsfpoc.jsf;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;

/**
 *
 * @author aubi
 */
@Named(value = "bean")
@RequestScoped
public class Bean {

    private String hello = "Hello from server!";
    private String sampleInput;
    private String sampleInputVerification = "Click 'verify'";
    private String calendarInput = "0201";
    private String sampleOut2 = "";
    /**
     * Creates a new instance of Bean
     */
    public Bean() {
    }

    public void verifySampleInput() {
        sampleInputVerification = "Verified: '" + sampleInput + "'";
    }

    public String getSampleDate() {
        return "2612";
    }

    public String getSampleInput() {
        return sampleInput;
    }

    public void setSampleInput(String sampleInput) {
        this.sampleInput = sampleInput;
    }

    public String getSampleInputVerification() {
        return sampleInputVerification;
    }

    public void setSampleInputVerification(String sampleInputVerification) {
        this.sampleInputVerification = sampleInputVerification;
    }

    public String getCalendarInput() {
        return calendarInput;
    }

    public void setCalendarInput(String calendarInput) {
        this.calendarInput = calendarInput;
    }

    public String getSampleOut2() {
        return sampleOut2;
    }

    public void setSampleOut2(String sampleOut2) {
        this.sampleOut2 = sampleOut2;
    }

    public String getHello() {
        return hello;
    }

}
