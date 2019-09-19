
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "test")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Patent {

    @Id
    @JsonProperty("patentNumber")
    private String patentNumber;

    @JsonProperty("classification")
    private String classification;

    @JsonProperty("applicationType")
    private String applicationType;

    @JsonProperty("xmlPaths")
    private List<String> xmlPaths;

    @JsonProperty("others")
    private List<String> others;

    @JsonProperty("documentId")
    private String documentId;

    @JsonProperty("applicationNumber")
    private String applicationNumber;

    @JsonProperty("documentType")
    private String documentType;

    @JsonProperty("publicationDate")
    private String publicationDate;

    @JsonProperty("documentDate")
    private String documentDate;

    @JsonProperty("productionDate")
    private String productionDate;

    @JsonProperty("applicationDate")
    private String applicationDate;

    @JsonProperty("applicant")
    private List<String> applicant = null;

    @JsonProperty("inventor")
    private List<String> inventor = null;

    @JsonProperty("assignee")
    @Nullable
    @Field("assignee")
    private List<String> assignee = new ArrayList<>();

    @JsonProperty("title")
    private String title;

    @JsonProperty("archiveUrl")
    private String archiveUrl;

    @JsonProperty("pdfPath")
    private String pdfPath;

    @JsonProperty("year")
    private String year;

    @JsonProperty("_version_")
    private String version;

    @JsonProperty("imgPath")
    private String imgPath;

    @JsonProperty("abstraction")
    private String abstraction;

    @JsonProperty("categoryId")
    private String categoryId;

    @JsonProperty("category")
    private String category;

    @JsonProperty("classificationText")
    private String classificationText;

    @Field("images")
    private List<String> images = new ArrayList<>();

    @JsonProperty private String claims;
    private boolean isListed;

    private Date listedOn;

    @JsonProperty("offerType")
    private String offerType;

    @JsonProperty("offerPrice")
    private BigDecimal offerPrice;

    @JsonProperty("togglePrice")
    private boolean togglePrice;

    @JsonProperty("claimStatus")
    private int claimStatus;

    @JsonProperty("ownedBy")
    private String ownedBy;

    @JsonProperty("ownerId")
    private String ownerId;

    @JsonProperty("bundleId")
    private List<String> bundleId;
}
