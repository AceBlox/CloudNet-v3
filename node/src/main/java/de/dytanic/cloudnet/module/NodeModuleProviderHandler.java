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

package de.dytanic.cloudnet.module;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.module.DefaultModuleProviderHandler;
import de.dytanic.cloudnet.driver.module.ModuleProviderHandler;
import de.dytanic.cloudnet.driver.module.ModuleWrapper;
import de.dytanic.cloudnet.driver.network.NetworkChannel;
import de.dytanic.cloudnet.driver.network.rpc.defaults.object.DefaultObjectMapper;
import java.util.Collection;
import lombok.NonNull;

public final class NodeModuleProviderHandler extends DefaultModuleProviderHandler implements ModuleProviderHandler {

  private final CloudNet nodeInstance;

  public NodeModuleProviderHandler(@NonNull CloudNet nodeInstance) {
    this.nodeInstance = nodeInstance;
  }

  @Override
  public void handlePostModuleStop(@NonNull ModuleWrapper moduleWrapper) {
    super.handlePostModuleStop(moduleWrapper);

    // unregister all listeners from the http server
    this.nodeInstance.httpServer().removeHandler(moduleWrapper.classLoader());
    // unregister all listeners added to the network handlers
    this.nodeInstance.networkClient().packetRegistry().removeListeners(moduleWrapper.classLoader());
    this.nodeInstance.networkServer().packetRegistry().removeListeners(moduleWrapper.classLoader());
    // unregister all listeners added to the network channels
    this.removeListeners(this.nodeInstance.networkClient().channels(), moduleWrapper.classLoader());
    this.removeListeners(this.nodeInstance.networkServer().channels(), moduleWrapper.classLoader());
    // unregister all listeners
    this.nodeInstance.eventManager().unregisterListeners(moduleWrapper.classLoader());
    // unregister all commands
    this.nodeInstance.commandProvider().unregister(moduleWrapper.classLoader());
    // unregister everything the module syncs to the cluster
    this.nodeInstance.dataSyncRegistry().unregisterHandler(moduleWrapper.classLoader());
    // unregister all object mappers which are registered
    DefaultObjectMapper.DEFAULT_MAPPER.unregisterBindings(moduleWrapper.classLoader());
  }

  private void removeListeners(@NonNull Collection<NetworkChannel> channels, @NonNull ClassLoader loader) {
    for (var channel : channels) {
      channel.packetRegistry().removeListeners(loader);
    }
  }
}