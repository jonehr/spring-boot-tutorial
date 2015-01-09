package boot.resource;

import boot.dto.ScheduleDTO;
import boot.service.TVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Date;

@RestController
public class TVResource {

    @Autowired
    private TVService tvService;

    @RequestMapping("/schedule/{channel}")
    public DeferredResult<ScheduleDTO[]> getSchedule(@PathVariable String channel) {
        return tvService.getScheduledPrograms(channel);
    }

    /**
     * Add a RequestMapping and method to lookup your favorite show
     */
    @RequestMapping("/schedule/next/{title}")
    public DeferredResult<Date> getNextAiringOfTitle(@PathVariable String title) {
        return tvService.nextOnAir(title);
    }

}
