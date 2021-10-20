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

package de.dytanic.cloudnet.common.stream;

import de.dytanic.cloudnet.common.function.ThrowableConsumer;
import java.io.IOException;
import java.io.OutputStream;
import org.jetbrains.annotations.NotNull;

public final class ListeningOutputStream extends OutputStream {

  private final OutputStream wrapped;
  private final ThrowableConsumer<OutputStream, IOException> closeListener;

  public ListeningOutputStream(OutputStream wrapped, ThrowableConsumer<OutputStream, IOException> closeListener) {
    this.wrapped = wrapped;
    this.closeListener = closeListener;
  }

  @Override
  public void write(int b) throws IOException {
    this.wrapped.write(b);
  }

  @Override
  public void write(byte @NotNull [] b) throws IOException {
    this.wrapped.write(b);
  }

  @Override
  public void write(byte @NotNull [] b, int off, int len) throws IOException {
    this.wrapped.write(b, off, len);
  }

  @Override
  public void flush() throws IOException {
    this.wrapped.flush();
  }

  @Override
  public void close() throws IOException {
    this.closeListener.accept(this);
    this.wrapped.close();
  }
}