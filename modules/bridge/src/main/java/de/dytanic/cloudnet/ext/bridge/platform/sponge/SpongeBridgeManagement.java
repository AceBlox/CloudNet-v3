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

package de.dytanic.cloudnet.ext.bridge.platform.sponge;

import de.dytanic.cloudnet.common.registry.IServicesRegistry;
import de.dytanic.cloudnet.driver.network.HostAndPort;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceHelper;
import de.dytanic.cloudnet.ext.bridge.platform.PlatformBridgeManagement;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.NetworkPlayerServerInfo;
import de.dytanic.cloudnet.ext.bridge.player.ServicePlayer;
import de.dytanic.cloudnet.ext.bridge.player.executor.PlayerExecutor;
import de.dytanic.cloudnet.wrapper.Wrapper;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;

final class SpongeBridgeManagement extends PlatformBridgeManagement<ServerPlayer, NetworkPlayerServerInfo> {

  private static final BiFunction<ServerPlayer, String, Boolean> PERM_FUNCTION = Subject::hasPermission;

  private final PlayerExecutor directGlobalExecutor;

  public SpongeBridgeManagement(@NotNull Wrapper wrapper) {
    super(wrapper);
    // init fields
    this.directGlobalExecutor = new SpongeDirectPlayerExecutor(
      PlayerExecutor.GLOBAL_UNIQUE_ID,
      () -> Sponge.server().onlinePlayers());
    // init the bridge properties
    BridgeServiceHelper.MAX_PLAYERS.set(Sponge.server().maxPlayers());
    BridgeServiceHelper.MOTD.set(PlainTextComponentSerializer.plainText().serialize(Sponge.server().motd()));
  }

  @Override
  public void registerServices(@NotNull IServicesRegistry registry) {
    registry.registerService(IPlayerManager.class, "PlayerManager", this.playerManager);
    registry.registerService(PlatformBridgeManagement.class, "SpongeBridgeManagement", this);
  }

  @Override
  public @NotNull ServicePlayer wrapPlayer(@NotNull ServerPlayer player) {
    return new ServicePlayer(player.uniqueId(), player.name());
  }

  @Override
  public @NotNull NetworkPlayerServerInfo createPlayerInformation(@NotNull ServerPlayer player) {
    return new NetworkPlayerServerInfo(
      player.uniqueId(),
      player.name(),
      null,
      new HostAndPort(player.connection().address()),
      this.ownNetworkServiceInfo);
  }

  @Override
  public @NotNull BiFunction<ServerPlayer, String, Boolean> getPermissionFunction() {
    return PERM_FUNCTION;
  }

  @Override
  public boolean isOnAnyFallbackInstance(@NotNull ServerPlayer player) {
    return this.isOnAnyFallbackInstance(this.ownNetworkServiceInfo.getServerName(), null, player::hasPermission);
  }

  @Override
  public @NotNull Optional<ServiceInfoSnapshot> getFallback(@NotNull ServerPlayer player) {
    return this.getFallback(
      player.uniqueId(),
      this.ownNetworkServiceInfo.getServerName(),
      null,
      player::hasPermission);
  }

  @Override
  public void handleFallbackConnectionSuccess(@NotNull ServerPlayer player) {
    this.handleFallbackConnectionSuccess(player.uniqueId());
  }

  @Override
  public @NotNull PlayerExecutor getDirectPlayerExecutor(@NotNull UUID uniqueId) {
    return uniqueId.equals(PlayerExecutor.GLOBAL_UNIQUE_ID)
      ? this.directGlobalExecutor
      : new SpongeDirectPlayerExecutor(
        uniqueId,
        () -> Collections.singleton(Sponge.server().player(uniqueId).orElse(null)));
  }

  @Override
  public void appendServiceInformation(@NotNull ServiceInfoSnapshot snapshot) {
    super.appendServiceInformation(snapshot);
    // append the bukkit specific information
    snapshot.getProperties().append("Online-Count", Sponge.server().onlinePlayers().size());
    snapshot.getProperties().append("Version", Sponge.platform().minecraftVersion().name());
    // players
    snapshot.getProperties().append("Players", Sponge.server().onlinePlayers().stream()
      .map(this::createPlayerInformation)
      .collect(Collectors.toList()));
  }
}