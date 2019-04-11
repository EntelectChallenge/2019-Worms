package za.co.entelect.challenge.network.Dto;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class RunnerFailedDto {

    public String matchId;
    public String exceptionMessage;

    public RunnerFailedDto(String matchId, Exception e) {
        this.matchId = matchId;
        this.exceptionMessage = ExceptionUtils.getStackTrace(e);
    }
}
