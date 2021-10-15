/*
 * Copyright 2019-2021 CloudNetService team & contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dytanic.cloudnet.driver.module;

import com.google.common.base.Verify;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a dependency of a module.
 */
@EqualsAndHashCode
public class ModuleDependency {

  private String repo;
  private String url;
  private String group;
  private String name;
  private String version;

  /**
   * Creates a new module dependency instance. This dependency will require another module to be loaded before.
   *
   * @param group   the group of the dependency.
   * @param name    the name of the dependency.
   * @param version the version of the dependency.
   */
  public ModuleDependency(@NotNull String group, @NotNull String name, @NotNull String version) {
    this.group = group;
    this.name = name;
    this.version = version;
  }

  /**
   * Creates a new module dependency instance. This dependency type will (when the repository is provided) require a
   * dependency from a remote repository which needs to be downloaded. If the repository is not provided (in this case
   * {@code null}) this dependency will require another module to be loaded before.
   *
   * @param repo    the repository in which this dependency is located.
   * @param group   the group of the dependency.
   * @param name    the name of the dependency.
   * @param version the version of the dependency.
   */
  public ModuleDependency(@Nullable String repo, @NotNull String group, @NotNull String name, @NotNull String version) {
    this.repo = repo;
    this.group = group;
    this.name = name;
    this.version = version;
  }

  /**
   * Creates a new module dependency instance. This dependency type will (when the repository is provided) require a
   * dependency from a remote repository which needs to be downloaded. If the direct download url is provided this
   * dependency will be loaded from the direct url. Please note: if both the repository and url is provided, the direct
   * download url will be ignored. If neither the repository nor the direct url is not provided (in this case {@code
   * null}) this dependency will require another module to be loaded before.
   *
   * @param repo    the repository in which this dependency is located.
   * @param url     the direct download url of this dependency.
   * @param group   the group of the dependency.
   * @param name    the name of the dependency.
   * @param version the version of the dependency.
   */
  public ModuleDependency(String repo, String url, String group, String name, String version) {
    this.repo = repo;
    this.url = url;
    this.group = group;
    this.name = name;
    this.version = version;
  }

  /**
   * @deprecated the name, group and version of this dependency is required. Creating a dependency using this
   * constructor will cause an exception when loading the module. See {@link #assertDefaultPropertiesSet()}.
   */
  @Deprecated
  @ScheduledForRemoval
  public ModuleDependency(String url) {
    this.url = url;
  }

  /**
   * This constructor is for internal use only. The name, group and version of this dependency is required. Creating a
   * dependency using this constructor will cause an exception when loading the module. See {@link
   * #assertDefaultPropertiesSet()}.
   */
  @Internal
  public ModuleDependency() {
  }

  /**
   * Get the repository this dependency is located in.
   *
   * @return the repository this dependency is located in or {@code null} if not located in a repository.
   */
  public @Nullable String getRepo() {
    return this.repo;
  }

  /**
   * Get the direct download url of this dependency.
   *
   * @return the direct download url of this dependency or {@code null} if there is no direct download url.
   */
  public @Nullable String getUrl() {
    return this.url;
  }

  /**
   * Get the group of this dependency.
   *
   * @return the group of this dependency.
   */
  public @NotNull String getGroup() {
    return this.group;
  }

  /**
   * Get the name of this dependency.
   *
   * @return the name of this dependency.
   */
  public @NotNull String getName() {
    return this.name;
  }

  /**
   * Get the version of this dependency.
   *
   * @return the version of this dependency.
   */
  public @NotNull String getVersion() {
    return this.version;
  }

  /**
   * Asserts that the required properties (group, name, version) are present.
   *
   * @throws com.google.common.base.VerifyException if one of required properties is not set.
   */
  public void assertDefaultPropertiesSet() {
    Verify.verifyNotNull(this.group, "Missing group of module dependency");
    Verify.verifyNotNull(this.name, "Missing name of module dependency");
    Verify.verifyNotNull(this.version, "Missing version of module dependency");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return this.group + ':' + this.name + ':' + this.version;
  }
}