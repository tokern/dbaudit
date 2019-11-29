package io.tokern.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitState {
  public final String tags;
  public final String branch;
  public final String dirty;
  public final String remoteOriginUrl;

  public final String commitId;
  public final String commitIdAbbrev;
  public final String describe;
  public final String describeShort;
  public final String commitUserName;
  public final String commitUserEmail;
  public final String commitMessageFull;
  public final String commitMessageShort;
  public final String commitTime;
  public final String closestTagName;
  public final String closestTagCommitCount;

  public final String buildUserName;
  public final String buildUserEmail;
  public final String buildTime;
  public final String buildHost;
  public final String buildVersion;
  public final String totalCommitCount;

  /**
   * Capture git information from the project for introspection.
   * @param tags  =${git.tags} comma separated tag names
   * @param branch =${git.branch}
   * @param dirty =${git.dirty}
   * @param remoteOriginUrl =${git.remote.origin.url}
   * @param commitId =${git.commit.id.full} OR ${git.commit.id}
   * @param commitIdAbbrev =${git.commit.id.abbrev}
   * @param describe =${git.commit.id.describe}
   * @param describeShort =${git.commit.id.describe-short}
   * @param commitUserName =${git.commit.user.name}
   * @param commitUserEmail =${git.commit.user.email}
   * @param commitMessageFull =${git.commit.message.full}
   * @param commitMessageShort =${git.commit.message.short}
   * @param commitTime =${git.commit.time}
   * @param closestTagName =${git.closest.tag.name}
   * @param closestTagCommitCount =${git.closest.tag.commit.count}
   * @param buildUserName =${git.build.user.name}
   * @param buildUserEmail =${git.build.user.email}
   * @param buildTime =${git.build.time}
   * @param buildHost =${git.build.host}
   * @param buildVersion =${git.build.version}
   * @param totalCommitCount =${git.total.commit.count}
   */
  @JsonCreator
  public GitState(@JsonProperty("git.tags") String tags,
                  @JsonProperty("git.branch") String branch,
                  @JsonProperty("git.dirty") String dirty,
                  @JsonProperty("git.remote.origin.url") String remoteOriginUrl,
                  @JsonProperty("git.commit.id") String commitId,
                  @JsonProperty("git.commit.id.abbrev") String commitIdAbbrev,
                  @JsonProperty("git.commit.id.describe") String describe,
                  @JsonProperty("git.commit.id.describe-short") String describeShort,
                  @JsonProperty("git.commit.user.name") String commitUserName,
                  @JsonProperty("git.commit.user.email") String commitUserEmail,
                  @JsonProperty("git.commit.message.full") String commitMessageFull,
                  @JsonProperty("git.commit.message.short") String commitMessageShort,
                  @JsonProperty("git.commit.time") String commitTime,
                  @JsonProperty("git.closest.tag.name") String closestTagName,
                  @JsonProperty("git.closest.tag.commit.count") String closestTagCommitCount,
                  @JsonProperty("git.build.user.name") String buildUserName,
                  @JsonProperty("git.build.user.email") String buildUserEmail,
                  @JsonProperty("git.build.time") String buildTime,
                  @JsonProperty("git.build.host") String buildHost,
                  @JsonProperty("git.build.version") String buildVersion,
                  @JsonProperty("git.total.commit.count") String totalCommitCount) {
    this.tags = tags;
    this.branch = branch;
    this.dirty = dirty;
    this.remoteOriginUrl = remoteOriginUrl;
    this.commitId = commitId;
    this.commitIdAbbrev = commitIdAbbrev;
    this.describe = describe;
    this.describeShort = describeShort;
    this.commitUserName = commitUserName;
    this.commitUserEmail = commitUserEmail;
    this.commitMessageFull = commitMessageFull;
    this.commitMessageShort = commitMessageShort;
    this.commitTime = commitTime;
    this.closestTagName = closestTagName;
    this.closestTagCommitCount = closestTagCommitCount;
    this.buildUserName = buildUserName;
    this.buildUserEmail = buildUserEmail;
    this.buildTime = buildTime;
    this.buildHost = buildHost;
    this.buildVersion = buildVersion;
    this.totalCommitCount = totalCommitCount;
  }
}
