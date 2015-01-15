package boot.service;

import boot.dto.ScheduleDTO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.ObservableResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.util.Arrays;
import java.util.List;

@Component
public class TVIntegration {

    private RestTemplate restTemplate = new RestTemplate();

    @HystrixCommand
    public Observable<List<ScheduleDTO>> getSchedule(final String channel) {
        return new ObservableResult<List<ScheduleDTO>>() {
            @Override
            public List<ScheduleDTO> invoke() {
                return Arrays.asList(restTemplate.getForObject("http://www.svt.se/play4api/channel/{channel}/schedule",
                        ScheduleDTO[].class, channel));
            }
        };
    }

}