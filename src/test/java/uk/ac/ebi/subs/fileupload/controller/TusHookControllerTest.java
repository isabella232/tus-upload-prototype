package uk.ac.ebi.subs.fileupload.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.subs.fileupload.dao.TUSFileInfoRepository;
import uk.ac.ebi.subs.fileupload.model.TUSFileInfo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(value = SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TusHookControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Map<String, String> hookValues = new HashMap<>();
    private TUSFileInfo TUSFileInfo;

    private static final String TUS_FILEINFO_ID = "e473e12d07830e76ac0969f135117b12";

    @Autowired
    private TUSFileInfoRepository tusFileInfoRepository;
    
    private String submissionId;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        hookValues.put("before-start", "pre-create");
        hookValues.put("after-start", "post-create");
        hookValues.put("progress", "post-receive");
        hookValues.put("finish", "post-finish");
        hookValues.put("terminate", "post-terminate");

        submissionId = UUID.randomUUID().toString();

        TUSFileInfo = new TUSFileInfo();
        TUSFileInfo.setTusId(TUS_FILEINFO_ID);
        TUSFileInfo.setSize(2683615);
        TUSFileInfo.setMetadata(TUSFileInfo.buildMetaData("central_park2.jpg", submissionId));
    }

    @After
    public void tearDown() {
        tusFileInfoRepository.deleteAll();
    }

    @Test
    public void preCreateEventDispatchedWhenFileUploadStarts() throws Exception {
        TUSFileInfo.setTusId(null);
        TUSFileInfo.setOffsetValue(0);

        String beforeStartEvent = hookValues.get("before-start");

        mockMvc.perform(post("/tusevent")
                .content(json(TUSFileInfo))
                .header("Hook-Name", beforeStartEvent)
                .contentType(contentType))
                .andExpect(status().isOk());

        List<TUSFileInfo> fileInfos = tusFileInfoRepository.findByEventType(beforeStartEvent);
        assertThat(fileInfos.size(), is(1));
        assertThat(fileInfos.get(0).getTusId(), isEmptyOrNullString());
    }

    @Test
    public void postCreateEventDispatchedWhenFileUploadStarts() throws Exception {
        TUSFileInfo.setOffsetValue(0);

        String afterStartEvent = hookValues.get("after-start");

        mockMvc.perform(post("/tusevent")
                .content(json(TUSFileInfo))
                .header("Hook-Name", afterStartEvent)
                .contentType(contentType))
                .andExpect(status().isOk());

        List<TUSFileInfo> fileInfos = tusFileInfoRepository.findByEventType(afterStartEvent);
        assertThat(fileInfos.size(), is(1));
        assertThat(fileInfos.get(0).getTusId(), is(equalTo(TUS_FILEINFO_ID)));
    }

    @Test
    public void postReceiveEventDispatchedWhenFileUploadProgressing() throws Exception {
        final long offsetValue = 1048576L;
        TUSFileInfo.setOffsetValue(offsetValue);

        String progressEvent = hookValues.get("progress");

        mockMvc.perform(post("/tusevent")
                .content(json(TUSFileInfo))
                .header("Hook-Name", progressEvent)
                .contentType(contentType))
                .andExpect(status().isOk());

        List<TUSFileInfo> fileInfos = tusFileInfoRepository.findByEventType(progressEvent);
        assertThat(fileInfos.size(), is(1));
        assertThat(fileInfos.get(0).getOffsetValue(), is(equalTo(offsetValue)));
    }

    @Test
    public void postFinishEventDispatchedWhenFileUploadFinished() throws Exception {
        final long offsetValue = 2683615L;
        TUSFileInfo.setOffsetValue(offsetValue);

        String finishEvent = hookValues.get("finish");

        mockMvc.perform(post("/tusevent")
                .content(json(TUSFileInfo))
                .header("Hook-Name", finishEvent)
                .contentType(contentType))
                .andExpect(status().isOk());

        List<TUSFileInfo> fileInfos = tusFileInfoRepository.findByEventType(finishEvent);
        assertThat(fileInfos.size(), is(1));
        assertThat(fileInfos.get(0).getOffsetValue(), is(equalTo(offsetValue)));
    }

    @Test
    public void canDAddASpecificHeaderWhenUploadingAFile() throws Exception {
        String finishEvent = hookValues.get("finish");

        mockMvc.perform(post("/tusevent")
                .content(json(TUSFileInfo))
                .header("Hook-Name", finishEvent)
                .contentType(contentType))
                .andExpect(status().isOk());

        List<TUSFileInfo> fileInfos = tusFileInfoRepository.findByEventType(finishEvent);
        assertThat(fileInfos.size(), is(1));
        assertThat(fileInfos.get(0).getMetadata().getSubmissionID(), is(equalTo(submissionId)));
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
