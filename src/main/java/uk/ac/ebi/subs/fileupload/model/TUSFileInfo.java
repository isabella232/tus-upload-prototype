package uk.ac.ebi.subs.fileupload.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Arrays;

@Data
@Entity
public class TUSFileInfo {

    @JsonIgnore
    @Id
    @GeneratedValue
    private long id;

    @JsonProperty(value = "ID")
    @Column(name = "tusId")
    private String tusId;
    @JsonProperty(value = "Size")
    @Column(name = "size")
    private long size;
    @JsonProperty(value = "Offset")
    private long offsetValue;
    @JsonProperty(value = "MetaData")
    @OneToOne(cascade = CascadeType.ALL)
    private MetaData metadata;
    @JsonProperty(value = "IsPartial")
    @Column(name = "is_partial")
    private boolean isPartial;
    @JsonProperty(value = "IsFinal")
    @Column(name = "is_final")
    private boolean isFinal;
    @JsonProperty(value = "PartialUploads")
    private String[] partialUploads;

    @JsonIgnore
    private String eventType;

    public TUSFileInfo() {
    }

    @Data
    @Entity
    public static class MetaData {

        @JsonIgnore
        @Id
        @GeneratedValue
        private long id;

        @Column(name = "filename")
        private String filename;

        @Column(name = "submission_id")
        private String submissionID;

        public MetaData() {
        }

        @Override
        public String toString() {
            return "metadata{" +
                    "filename='" + filename + '\'' +
                    ", submissionID='" + submissionID + '\'' +
                    '}';
        }
    }

    public static MetaData buildMetaData(String fileName, String submissionID) {
        MetaData metadata = new MetaData();
        metadata.setFilename(fileName);
        metadata.setSubmissionID(submissionID);

        return metadata;
    }

    @Override
    public String toString() {
        return "TUSFileInfo{" +
                "tusId='" + tusId + '\'' +
                ", size=" + size +
                ", offsetValue=" + offsetValue +
                ", metadata=" + metadata +
                ", isPartial=" + isPartial +
                ", isFinal=" + isFinal +
                ", partialUploads=" + Arrays.toString(partialUploads) +
                '}';
    }
}
