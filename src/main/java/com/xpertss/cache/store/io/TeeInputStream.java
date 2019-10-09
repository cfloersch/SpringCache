/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/9/2019
 */
package com.xpertss.cache.store.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * InputStream proxy that transparently writes a copy of all bytes read
 * from the proxied stream to a given OutputStream. Using {@link #skip(long)}
 * or {@link #mark(int)}/{@link #reset()} on the stream will result on some
 * bytes from the input stream being skipped or duplicated in the output
 * stream.
 * <p/>
 * The proxied input stream is closed when the {@link #close()} method is
 * called on this proxy. You may configure whether the input stream closes the
 * output stream.
 * <p/>
 * TODO When this completes I need to add the item to the cache
 */
public class TeeInputStream extends ProxyInputStream {

   /**
    * The output stream that will receive a copy of all bytes read from the
    * proxied input stream.
    */
   private final OutputStream branch;

   /**
    * Flag for closing the associated output stream when this stream is closed.
    */
   private final Runnable onclose;

   /**
    * Creates a TeeInputStream that proxies the given {@link InputStream}
    * and copies all read bytes to the given {@link OutputStream}. The given
    * output stream will not be closed when this stream gets closed.
    *
    * @param input input stream to be proxied
    * @param branch output stream that will receive a copy of all bytes read
    */
   public TeeInputStream(final InputStream input, final OutputStream branch)
   {
      this(input, branch, null);
   }

   /**
    * Creates a TeeInputStream that proxies the given {@link InputStream}
    * and copies all read bytes to the given {@link OutputStream}. The given
    * output stream will be closed when this stream gets closed if the
    * closeBranch parameter is {@code true}.
    *
    * @param input input stream to be proxied
    * @param branch output stream that will receive a copy of all bytes read
    * @param onclose a function to execute on close
    */
   public TeeInputStream(InputStream input, OutputStream branch, Runnable onclose)
   {
      super(input);
      this.branch = branch;
      this.onclose = onclose;
   }

   /**
    * Closes the proxied input stream and, if so configured, the associated
    * output stream. An exception thrown from one stream will not prevent
    * closing of the other stream.
    *
    * @throws IOException if either of the streams could not be closed
    */
   @Override
   public void close() throws IOException
   {
      try {
         super.close();
      } finally {
         branch.close();
         // TODO technically we only want to do this if we are complete (aka we've seen EOF)
         if(onclose != null) onclose.run();
      }
   }

   /**
    * Reads a single byte from the proxied input stream and writes it to
    * the associated output stream.
    *
    * @return next byte from the stream, or -1 if the stream has ended
    * @throws IOException if the stream could not be read (or written)
    */
   @Override
   public int read()
      throws IOException
   {
      final int ch = super.read();
      if (ch != EOF) {
         branch.write(ch);
      }
      return ch;
   }

   /**
    * Reads bytes from the proxied input stream and writes the read bytes
    * to the associated output stream.
    *
    * @param bts byte buffer
    * @param st start offset within the buffer
    * @param end maximum number of bytes to read
    * @return number of bytes read, or -1 if the stream has ended
    * @throws IOException if the stream could not be read (or written)
    */
   @Override
   public int read(byte[] bts, int st, int end)
      throws IOException
   {
      int n = super.read(bts, st, end);
      if (n != EOF) {
         branch.write(bts, st, n);
      }
      return n;
   }

   /**
    * Reads bytes from the proxied input stream and writes the read bytes
    * to the associated output stream.
    *
    * @param bts byte buffer
    * @return number of bytes read, or -1 if the stream has ended
    * @throws IOException if the stream could not be read (or written)
    */
   @Override
   public int read(byte[] bts)
      throws IOException
   {
      int n = super.read(bts);
      if (n != EOF) {
         branch.write(bts, 0, n);
      }
      return n;
   }

}