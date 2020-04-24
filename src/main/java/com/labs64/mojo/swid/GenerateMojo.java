/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.labs64.mojo.swid;

import com.labs64.mojo.swid.configuration.Process;
import com.labs64.mojo.swid.configuration.*;
import com.labs64.utils.swid.SwidBuilder;
import com.labs64.utils.swid.builder.ProcessBuilder;
import com.labs64.utils.swid.builder.*;
import com.labs64.utils.swid.io.SwidWriter;
import com.labs64.utils.swid.processor.DefaultSwidProcessor;
import com.labs64.utils.swid.support.SwidUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.iso.standards.iso._19770.__2._2014_dis.schema.SoftwareIdentity;

import javax.xml.namespace.QName;
import java.io.File;
import java.math.BigInteger;
import java.util.*;

/**
 * A mojo that generates a SWID tag from a given POM.
 * 
 * @see <a href="http://l64.cc/swid">SoftWare IDentification (SWID) Tags Generator</a>
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateMojo extends AbstractSwidMojo {

    /**
     * Specifies the destination directory where the generated SWID tags files will be saved.
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.outputDirectory", required = true, defaultValue = "${project.build.directory}/generated-resources/swid")
    private File outputDirectory;

    /**
     * Specifies the extension of the generated SWID tags files.
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.extension", required = false, defaultValue = "swidtag")
    private String extension;

    /**
     * Specifies product name.
     * 
     * <br/>
     * <b>Fallback value(s):</b> <tt>${project.artifactId}</tt>
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.name", required = false, defaultValue = "${project.name}")
    private String name;

    /**
     * Specifies product version.
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.version", required = false, defaultValue = "${project.version}")
    private String version;

    /**
     * Specifies tag Id.
     *
     * @since 0.2.0
     */
    @Parameter(property = "swid.tagId", required = false, defaultValue = "${project.name}-${project.version}")
    private String tagId;

    /**
     * Specifies supplemental.
     *
     * @since 0.2.0
     */
    @Parameter(property = "swid.supplemental", required = false, defaultValue = "${supplemental}")
    private Boolean supplemental;

    /**
     * Specifies patch.
     *
     * @since 0.2.0
     */
    @Parameter(property = "swid.patch", required = false, defaultValue = "${patch}")
    private Boolean patch;

    /**
     * Specifies corpus.
     *
     * @since 0.2.0
     */
    @Parameter(property = "swid.corpus", required = false, defaultValue = "${corpus}")
    private Boolean corpus;

    /**
     * Specifies domain creation date which belongs to the software creator. <br/>
     * Format: <code>'yyyy-MM'</code> <br/>
     * Example: <code>'2010-04'</code><br/>
     * Default value: <tt>current date</tt>
     * 
     * @since 1.0.0
     */
    @Parameter(property = "swid.domain_creation_date", required = false)
    private String domain_creation_date;

    /**
     * Specifies list of Entities to be shown in SWID Tag
     *
     * <pre>
     * &lt;entities&gt;
     *     &lt;entity&gt;
     *         &lt;name&gt;Entity_Name&lt;/name&gt;
     *         &lt;regid&gt;Entity_Regid&lt;/regid&gt;
     *         &lt;roles&gt;
     *             &lt;role&gt;Entity_Role&lt;/role&gt;
     *         &lt;/roles&gt;
     *     &lt;/entity&gt;
     * &lt;/entities&gt;
     * </pre>
     */
    @Parameter(required = false, defaultValue = "${entities}")
    private List<Entity> entities;

    /**
     * Specifies list of Evidence to be shown in SWID Tag
     *
     * <pre>
     * &lt;evidence&gt;
     *     &lt;evidence&gt;
     *         &lt;date&gt;Entity_Name&lt;/date&gt;
     *         &lt;deviceId&gt;Device_ID&lt;/deviceId&gt;
     *     &lt;/evidence&gt;
     * &lt;/evidence&gt;
     * </pre>
     */
    @Parameter(required = false, defaultValue = "${evidence}")
    private List<Evidence> evidence;

    /**
     * Specifies list of Links to be shown in SWID Tag
     *
     * <pre>
     * &lt;links&gt;
     *     &lt;link&gt;
     *         &lt;rel&gt;relatie&lt;/rel&gt;
     *         &lt;href&gt;swid:href&lt;/href&gt;
     *     &lt;/link&gt;
     * &lt;/links&gt;
     * </pre>
     */
    @Parameter(required = false, defaultValue = "${links}")
    private List<Link> links;

    /**
     * Specifies list of Metadata to be shown in SWID Tag
     *
     * <pre>
     * &lt;metadata&gt;
     *     &lt;meta&gt;
     *         &lt;key&gt;key&lt;/key&gt;
     *         &lt;value&gt;value&lt;/value&gt;
     *     &lt;/meta&gt;
     * &lt;/metadata&gt;
     * </pre>
     */
    @Parameter(required = false, defaultValue = "${metadata}")
    private List<Meta> metadata;

    /**
     * Specifies list of Directories to be included in the SWID tag Payload
     * 
     * <pre>
     * &lt;payloadDirectories&gt;
     *     &lt;directory&gt;
     *         &lt;root&gt;/data&lt;/root&gt;
     *         &lt;extraKeys&gt;
     *             &lt;meta&gt;
     *                 &lt;key&gt;key&lt;/key&gt;
     *                 &lt;value&gt;value&lt;/value&gt;
     *             &lt;/meta&gt;
     *         &lt;/extraKeys&gt;
     *     &lt;/directory&gt;
     * &lt;/payloadDirectories&gt;
     * </pre>
     */
    @Parameter(required = false, defaultValue = "${payloadDirectories}")
    private List<Directory> payloadDirectories;

    /**
     * Specifies list of Files to be included in the SWID tag Payload
     *
     * &lt;payloadFiles&gt;
     *     &lt;file&gt;
     *         &lt;root&gt;/data&lt;/root&gt;
     *         &lt;name&gt;resources.txt&lt;/name&gt;
     *         &lt;size&gt;1024&lt;/size&gt;
     *         &lt;extraKeys&gt;
     *             &lt;meta&gt;
     *                 &lt;key&gt;key&lt;/key&gt;
     *                 &lt;value&gt;value&lt;/value&gt;
     *             &lt;/meta&gt;
     *         &lt;/extraKeys&gt;
     *     &lt;/file&gt;
     * &lt;/payloadFiles&gt;
     */
    @Parameter(required = false, defaultValue = "${payloadFiles}")
    private List<PayloadFile> payloadFiles;

    /**
     * Specifies list of Processes to be included in the SWID tag Payload
     *
     * &lt;payloadProcesses&gt;
     *     &lt;process&gt;
     *         &lt;name&gt;Process_Name&lt;/name&gt;
     *         &lt;pid&gt;12345&lt;/pid&gt;
     *         &lt;extraKeys&gt;
     *             &lt;meta&gt;
     *                 &lt;key&gt;key&lt;/key&gt;
     *                 &lt;value&gt;value&lt;/value&gt;
     *             &lt;/meta&gt;
     *         &lt;/extraKeys&gt;
     *     &lt;/process&gt;
     * &lt;/payloadProcesses&gt;
     */
    @Parameter(required = false, defaultValue = "${payloadProcesses}")
    private List<Process> payloadProcesses;

    /**
     * Specifies list of Resources to be included in the SWID tag Payload
     *
     * &lt;payloadResources&gt;
     *     &lt;resource&gt;
     *         &lt;type&gt;Resource_Type&lt;/type&gt;
     *         &lt;extraKeys&gt;
     *             &lt;meta&gt;
     *                 &lt;key&gt;key&lt;/key&gt;
     *                 &lt;value&gt;value&lt;/value&gt;
     *             &lt;/meta&gt;
     *         &lt;/extraKeys&gt;
     *     &lt;/resource&gt;
     * &lt;/payloadResources&gt;
     */
    @Parameter(required = false, defaultValue = "${payloadResources}")
    private List<Resource> payloadResources;


    public void execute() throws MojoExecutionException {
        getLog().debug("Generate SWID Tag...");

        // prepare mandatory elements
        ArtifactVersion artifactVersion = getArtifactVersion();
        prepareMandatoryElements();

        // prepare SWID Tag processor
        DefaultSwidProcessor processor = new DefaultSwidProcessor();
        processor.setName(name)
                .setTagId(tagId)
                .setVersion(version)
                .setCorpus(corpus)
                .setSupplemental(supplemental)
                .setPatch(patch);

        for (Entity ent : entities) {
            processor.addEntity(new EntityBuilder()
                    .name(ent.getName())
                    .regid(ent.getRegid())
                    .role(ent.getRoles())
                    .build());
        }

        for (Evidence evi : evidence) {
            processor.addEvidence(new EvidenceBuilder()
                    .date(evi.getDate())
                    .deviceId(evi.getDeviceId())
                    .build());
        }

        for (Link link : links) {
            processor.addLink(new LinkBuilder()
                    .href(link.getHref())
                    .rel(link.getRel())
                    .build());
        }

        Map<QName, String> softwareMeta = getqNameStringMap(metadata);
        if (!softwareMeta.isEmpty()) {
            processor.addMetaData(new SoftwareMetaBuilder().otherAttributes(softwareMeta).build());
        }

        PayloadBuilder payloadBuilder = new PayloadBuilder();
        boolean includePayload = generatePayloadDirectories(payloadBuilder, false);
        includePayload = generatePayloadFiles(payloadBuilder, includePayload);
        includePayload = generatePayloadProcesses(payloadBuilder, includePayload);
        includePayload = generatePayloadResources(payloadBuilder, includePayload);
        if (includePayload) {
            processor.addPayload(payloadBuilder.build());
        }

        // create builder and pass processor as build param
        SwidBuilder builder = new SwidBuilder();
        SoftwareIdentity swidTag = builder.build(processor);

        // output resulting object
        final String fileName = SwidUtils.generateSwidFileName(
                getDefaultRegId().getRegid(),
                getProject().getArtifactId(),
                version,
                extension);
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new MojoExecutionException("Cannot create directory '" + outputDirectory.toString() + "'");
            }
        }
        File swidFile = new File(outputDirectory, fileName);
        SwidWriter writer = new SwidWriter();
        writer.write(swidTag, swidFile);
    }

    private boolean generatePayloadDirectories(PayloadBuilder payloadBuilder, boolean includePayload) {
        for (Directory dir: payloadDirectories) {
            DirectoryBuilder builder = new DirectoryBuilder().root(dir.getRoot())
                    .key(dir.getKey())
                    .location(dir.getLocation());
            Map<QName, String> extraKeys = getqNameStringMap(dir.getExtraKeys());
            if (!extraKeys.isEmpty()) {
                builder.otherAttributes(extraKeys);
            }
            payloadBuilder.directory(builder.build());
            includePayload = true;
        }
        return includePayload;
    }

    private boolean generatePayloadFiles(PayloadBuilder payloadBuilder, boolean includePayload) {
        for (PayloadFile file: payloadFiles) {
            FileBuilder builder = new FileBuilder().name(file.getName())
                    .size(getBigIntegerFromString(file.getSize()))
                    .version(file.getVersion())
                    .root(file.getRoot())
                    .key(file.getKey())
                    .location(file.getLocation());
            Map<QName, String> extraKeys = getqNameStringMap(file.getExtraKeys());
            if (!extraKeys.isEmpty()) {
                builder.otherAttributes(extraKeys);
            }
            payloadBuilder.file(builder.build());
            includePayload = true;
        }
        return includePayload;
    }

    private boolean generatePayloadProcesses(PayloadBuilder payloadBuilder, boolean includePayload) {
        for (Process process: payloadProcesses) {
            ProcessBuilder builder = new ProcessBuilder().name(process.getName())
                    .pid(getBigIntegerFromString(process.getPid()));
            Map<QName, String> extraKeys = getqNameStringMap(process.getExtraKeys());
            if (!extraKeys.isEmpty()) {
                builder.otherAttributes(extraKeys);
            }
            payloadBuilder.process(builder.build());
            includePayload = true;
        }
        return includePayload;
    }

    private boolean generatePayloadResources(PayloadBuilder payloadBuilder, boolean includePayload) {
        for (Resource resource: payloadResources) {
            ResourceBuilder builder = new ResourceBuilder().type(resource.getType());
            Map<QName, String> extraKeys = getqNameStringMap(resource.getExtraKeys());
            if (!extraKeys.isEmpty()) {
                builder.otherAttributes(extraKeys);
            }
            payloadBuilder.resource(builder.build());
            includePayload = true;
        }
        return includePayload;
    }

    private Map<QName, String> getqNameStringMap(List<Meta> extraKeys2) {
        Map<QName, String> extraKeys = new HashMap();
        if (extraKeys2 != null) {
            for (Meta meta : extraKeys2) {
                extraKeys.put(new QName(meta.getKey()), meta.getValue());
            }
        }
        return extraKeys;
    }

    private BigInteger getBigIntegerFromString(String string) {
        return string == null? null: new BigInteger(string);
    }

    private ArtifactVersion getArtifactVersion() {
        if (ArtifactUtils.isSnapshot(version)) {
            version = StringUtils.substring(version, 0, version.length()
                    - Artifact.SNAPSHOT_VERSION.length() - 1);
        }
        return new DefaultArtifactVersion(version);
    }

    private String getDomainDate() {
        if (StringUtils.isBlank(domain_creation_date)) {
            domain_creation_date = SwidUtils.generateDomainDate(new Date());
        }
        return domain_creation_date;
    }

    private RegId getDefaultRegId() {
        final RegId regid = new RegId();

        regid.setName(getProject().getOrganization() == null ?
                getProject().getGroupId() : getProject().getOrganization().getName());

        final String url = getProject().getOrganization() == null ?
                getProject().getUrl() : getProject().getOrganization().getUrl();
        final String reverseDomainName = StringUtils.isBlank(url) ?
                getProject().getGroupId() : SwidUtils.revertDomainName(url);
        regid.setRegid(SwidUtils.generateRegId(getDomainDate(), reverseDomainName));

        return regid;
    }

    private void prepareMandatoryElements() {
        final RegId defaultRegId = getDefaultRegId();

        // entities
        if (entities == null) {
            entities = new ArrayList();
        }
        if (entities.size() == 0) {
            List<String> roles = new ArrayList();
            roles.add("softwareCreator");
            roles.add("tagCreator");
            roles.add("licensor");
            entities.add(new Entity(defaultRegId.getName(), defaultRegId.getRegid(), roles));
        }

        if (evidence == null) {
            evidence = new ArrayList();
        }

        if (links == null) {
            links = new ArrayList();
        }

        if (metadata == null) {
            metadata = new ArrayList();
        }
    }

}
