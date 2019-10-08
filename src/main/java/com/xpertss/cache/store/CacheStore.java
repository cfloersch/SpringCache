/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package com.xpertss.cache.store;

import xpertss.cache.CacheParams;
import xpertss.lang.Objects;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.DELETE_ON_CLOSE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public class CacheStore {

   private final Path path;
   private final CacheParams params;

   public CacheStore(Path path, CacheParams params)
   {
      this.path = Objects.notNull(path, "path");
      this.params = Objects.notNull(params, "params");
   }

   public Path getPath()
   {
      return path;
   }

   public CacheParams getParams()
   {
      return params;
   }


   public CacheItem[] get(String key) throws IOException
   {
      return null;
   }

   public OutputStream cache(String key, CacheItem item, long size) throws IOException
   {
      Path fileName = path.resolve(Objects.toString(UUID.randomUUID()));

      // TODO store meta data, in database, create temp file (memory mapped??) for response
      // Store headers where???

      // TODO Maybe I need to open a memory mapped buffer, add it to the cache item, then
      // ensure the returned output stream can write to it.

      FileChannel cacheFile = FileChannel.open(fileName, CREATE_NEW, WRITE, READ, DELETE_ON_CLOSE);
      ByteBuffer entity = cacheFile.map(FileChannel.MapMode.READ_WRITE, 0, size);

      // TODO Need to wrap the buffer with an OutputStream???

      return Files.newOutputStream(fileName, CREATE_NEW, WRITE);
   }


   public void start()
   {
      Path dbPath = path.resolve("db");
      if(!Files.exists(dbPath)) {
         // create database
      }
   }


}