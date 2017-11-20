package uk.ac.ebi.subs.fileupload.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.subs.fileupload.dao.TUSFileInfoRepository;
import uk.ac.ebi.subs.fileupload.model.TUSFileInfo;

import java.util.logging.Logger;

@RestController
public class TusHookController {

    private static final Logger LOGGER = Logger.getLogger( TusHookController.class.getName() );

    private TUSFileInfoRepository tusFileInfoRepository;

    public TusHookController(TUSFileInfoRepository tusFileInfoRepository) {
        this.tusFileInfoRepository = tusFileInfoRepository;
    }

    @RequestMapping(value = "/tusevent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<TUSFileInfo> fileUploadEventPost(@RequestBody TUSFileInfo tusFileInfo,
                                                                         @RequestHeader(value = "Hook-Name") String eventName) throws Exception {
        LOGGER.info(String.format("Name of the POST event: %s", eventName));
        LOGGER.info(tusFileInfo.toString());
        tusFileInfo.setEventType(eventName);

        tusFileInfoRepository.save(tusFileInfo);

        return new ResponseEntity<>(tusFileInfo, HttpStatus.OK);
    }

}
