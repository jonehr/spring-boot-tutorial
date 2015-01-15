package boot.service;

import boot.dto.ScheduleDTO;
import boot.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class TVService {

    @Autowired
    private TVIntegration tvIntegration;

    private AsyncRestTemplate restTemplate = new AsyncRestTemplate();

    public DeferredResult<ScheduleDTO> nextOnAir(String showTitle) {
        DeferredResult<ScheduleDTO> deferredResult = new DeferredResult<>();
        DeferredResult<List<ScheduleDTO>> scheduledPrograms = getScheduledProgramsHystrix("svt1");

        scheduledPrograms.setResultHandler(svt1Program -> {
            ((List<ScheduleDTO>) svt1Program).stream()
                    .filter(program -> program.title.equalsIgnoreCase(showTitle))
                    .findFirst()
                    .ifPresent(deferredResult::setResult);

            if (!scheduledPrograms.hasResult()) {
                scheduledPrograms.setErrorResult(new NotFoundException(String.format("The program %s was not found", showTitle)));
            }
        });

        return deferredResult;
    }

    public DeferredResult<List<ScheduleDTO>> getScheduledPrograms(String channel) {
        DeferredResult<List<ScheduleDTO>> deferredResult = new DeferredResult<>();
        final ListenableFuture<ResponseEntity<ScheduleDTO[]>> listenableFuture = restTemplate.getForEntity("http://www.svt.se/play4api/channel/{channel}/schedule",
                ScheduleDTO[].class, channel);

        listenableFuture.addCallback(result -> {
            deferredResult.setResult(Arrays.asList(result.getBody()));
        }, deferredResult::setErrorResult);

        return deferredResult;
    }

    public DeferredResult<List<ScheduleDTO>> getScheduledProgramsHystrix(String channel) {
        // Create a result to return immediately, and to set callbacks on
        final DeferredResult<List<ScheduleDTO>> deferredResult = new DeferredResult<>();

        // Fetch an observable result of our Hystrix request
        Observable<List<ScheduleDTO>> observable = tvIntegration.getSchedule(channel);

        // subscribe to the observable and invoke the methods on next
        observable.subscribe(deferredResult::setResult,
                deferredResult::setErrorResult);

        return deferredResult;
    }

}
