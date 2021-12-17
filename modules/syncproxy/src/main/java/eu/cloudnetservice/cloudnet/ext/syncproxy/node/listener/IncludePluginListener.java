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

package eu.cloudnetservice.cloudnet.ext.syncproxy.node.listener;

import de.dytanic.cloudnet.common.io.FileUtils;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.util.DefaultModuleHelper;
import de.dytanic.cloudnet.event.service.CloudServicePreProcessStartEvent;
import eu.cloudnetservice.cloudnet.ext.syncproxy.node.NodeSyncProxyManagement;
import org.jetbrains.annotations.NotNull;

public final class IncludePluginListener {

  private final NodeSyncProxyManagement management;

  public IncludePluginListener(@NotNull NodeSyncProxyManagement management) {
    this.management = management;
  }

  @EventListener
  public void handleLifecycleUpdate(@NotNull CloudServicePreProcessStartEvent event) {
    var service = event.getService();
    if (!ServiceEnvironmentType.isMinecraftProxy(service.getServiceId().environment())) {
      return;
    }

    var syncProxyConfiguration = this.management.configuration();
    var groupEntryExists = syncProxyConfiguration.loginConfigurations().stream()
      .anyMatch(config -> service.getServiceConfiguration().groups().contains(config.targetGroup()))
      || syncProxyConfiguration.tabListConfigurations().stream()
      .anyMatch(config -> service.getServiceConfiguration().groups().contains(config.targetGroup()));

    if (groupEntryExists) {
      var pluginsFolder = event.getService().getDirectory().resolve("plugins");
      FileUtils.createDirectory(pluginsFolder);

      var targetFile = pluginsFolder.resolve("cloudnet-syncproxy.jar");
      FileUtils.delete(targetFile);

      if (DefaultModuleHelper.copyCurrentModuleInstanceFromClass(IncludePluginListener.class, targetFile)) {
        DefaultModuleHelper.copyPluginConfigurationFileForEnvironment(
          IncludePluginListener.class,
          event.getService().getServiceId().environment(),
          targetFile
        );
      }
    }
  }
}
