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

package de.dytanic.cloudnet.cluster;

import de.dytanic.cloudnet.CloudNet;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public abstract class DefaultNodeServerProvider<T extends NodeServer> implements NodeServerProvider<T> {

  protected final CloudNet cloudNet;
  protected final LocalNodeServer localNode;
  protected final Set<T> nodeServers = new CopyOnWriteArraySet<>();

  protected volatile NodeServer headNode;

  public DefaultNodeServerProvider(@NonNull CloudNet cloudNet) {
    this.cloudNet = cloudNet;
    this.localNode = new LocalNodeServer(cloudNet, this);

    this.refreshHeadNode();
  }

  @Override
  public @NonNull Collection<T> nodeServers() {
    return Collections.unmodifiableCollection(this.nodeServers);
  }

  @Override
  public @Nullable T nodeServer(@NonNull String uniqueId) {
    for (var nodeServer : this.nodeServers) {
      if (nodeServer.nodeInfo().uniqueId().equals(uniqueId)) {
        return nodeServer;
      }
    }
    return null;
  }

  @Override
  public @NonNull NodeServer headnode() {
    return this.headNode;
  }

  @Override
  public @NonNull LocalNodeServer selfNode() {
    return this.localNode;
  }

  @Override
  public void refreshHeadNode() {
    NodeServer choice = this.localNode;
    for (var nodeServer : this.nodeServers) {
      if (nodeServer.available()) {
        // the head node is always the node which runs the longest
        var snapshot = nodeServer.nodeInfoSnapshot();
        if (snapshot != null && snapshot.startupMillis() < choice.nodeInfoSnapshot().startupMillis()) {
          choice = nodeServer;
        }
      }
    }

    this.headNode = choice;
  }
}