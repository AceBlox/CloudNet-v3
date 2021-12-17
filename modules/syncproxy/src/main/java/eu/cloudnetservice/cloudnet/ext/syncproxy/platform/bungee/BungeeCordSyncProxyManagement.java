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

package eu.cloudnetservice.cloudnet.ext.syncproxy.platform.bungee;

import de.dytanic.cloudnet.common.registry.IServicesRegistry;
import eu.cloudnetservice.cloudnet.ext.syncproxy.platform.PlatformSyncProxyManagement;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BungeeCordSyncProxyManagement extends PlatformSyncProxyManagement<ProxiedPlayer> {

  private final Plugin plugin;

  public BungeeCordSyncProxyManagement(@NotNull Plugin plugin) {
    this.plugin = plugin;
    this.init();
  }

  @Override
  public void registerService(@NotNull IServicesRegistry registry) {
    registry.registerService(PlatformSyncProxyManagement.class, "BungeeCordSyncProxyManagement", this);
  }

  @Override
  public void unregisterService(@NotNull IServicesRegistry registry) {
    registry.unregisterService(PlatformSyncProxyManagement.class, "BungeeCordSyncProxyManagement");
  }

  @Override
  public void schedule(@NotNull Runnable runnable, long time, @NotNull TimeUnit unit) {
    this.plugin.getProxy().getScheduler().schedule(this.plugin, runnable, time, unit);
  }

  @Override
  public @NotNull Collection<ProxiedPlayer> onlinePlayers() {
    return this.plugin.getProxy().getPlayers();
  }

  @Override
  public @NotNull String playerName(@NotNull ProxiedPlayer player) {
    return player.getName();
  }

  @Override
  public @NotNull UUID playerUniqueId(@NotNull ProxiedPlayer player) {
    return player.getUniqueId();
  }

  @Override
  public void playerTabList(@NotNull ProxiedPlayer player, @Nullable String header, @Nullable String footer) {
    player.setTabHeader(
      this.asComponent(this.replaceTabPlaceholder(header, player)),
      this.asComponent(this.replaceTabPlaceholder(footer, player)));
  }

  @Override
  public void disconnectPlayer(@NotNull ProxiedPlayer player, @NotNull String message) {
    player.disconnect(this.asComponent(message));
  }

  @Override
  public void messagePlayer(@NotNull ProxiedPlayer player, @Nullable String message) {
    if (message == null) {
      return;
    }

    player.sendMessage(this.asComponent(message));
  }

  @Override
  public boolean checkPlayerPermission(@NotNull ProxiedPlayer player, @NotNull String permission) {
    return player.hasPermission(permission);
  }

  private @Nullable BaseComponent[] asComponent(@Nullable String message) {
    if (message == null) {
      return null;
    }

    return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
  }

  private @Nullable String replaceTabPlaceholder(@Nullable String input, @NotNull ProxiedPlayer player) {
    if (input == null) {
      return null;
    }

    return input
      .replace("%ping%", String.valueOf(player.getPing()))
      .replace("%server%", player.getServer().getInfo().getName());
  }
}
