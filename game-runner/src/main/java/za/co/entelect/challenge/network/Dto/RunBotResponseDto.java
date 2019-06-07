package za.co.entelect.challenge.network.Dto;

import com.google.gson.annotations.SerializedName;

public class RunBotResponseDto {

    @SerializedName("command")
    private String command;

    @SerializedName("std_out")
    private String stdOut;

    @SerializedName("std_error")
    private String stdError;

    @SerializedName("return_code")
    private String returnCode;

    @SerializedName("execution_time")
    private long executionTime;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getStdOut() {
        return stdOut;
    }

    public void setStdOut(String stdOut) {
        this.stdOut = stdOut;
    }

    public String getStdError() {
        return stdError;
    }

    public void setStdError(String stdError) {
        this.stdError = stdError;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
