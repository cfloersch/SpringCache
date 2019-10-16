package com.xpertss.cache.test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
import com.xpertss.cache.store.CacheItem;
import com.xpertss.cache.store.CacheItemBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import xpertss.cache.Visibility;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/10/2019
 */
public class TestCacheTest {

   private TestCache objectUnderTest;

   @Before
   public void setup()
   {
      Cache<URI, CacheContainer> cache = CacheBuilder.newBuilder()
         .maximumWeight(10000)
         .expireAfterAccess(24, TimeUnit.HOURS)
         .weigher((Weigher<URI, CacheContainer>) (key, value) -> value.getWeight())
         .build();

      objectUnderTest = new TestCache(cache);
   }

   @Test
   public void test() throws Exception
   {
      URI aaa = URI.create("https://api.manheim.com/locations/AAA");
      // create
      objectUnderTest.put(aaa, create(HttpStatus.OK, System.currentTimeMillis() - (24 * 3600 * 1000), create(512 * 1024)));
      // replace
      objectUnderTest.put(aaa, create(HttpStatus.OK, "abcdefg", true, create(384 * 1024)));
      // update
      objectUnderTest.put(aaa, create(HttpStatus.OK, "zyxwrst", true, create(256 * 1024)));
      // add
      objectUnderTest.put(aaa, create(HttpStatus.OK, "efghijk", false, create(128 * 1024)));
   }

   private CacheItem create(HttpStatus status, String eTag, boolean pub, Path file)
   {
      return CacheItemBuilder.create()
               .setCacheFile(file)
               .setVisibility((pub) ? Visibility.Public : Visibility.Private)
               .setMaxAge(200)
               .setETag(eTag)
               .build(status, new HttpHeaders());
   }

   private CacheItem create(HttpStatus status, long lastMod, Path file)
   {
      return CacheItemBuilder.create()
               .setCacheFile(file)
               .setMaxAge(200)
               .setLastModified(lastMod)
               .build(status, new HttpHeaders());
   }


   public Path create(int size) throws IOException
   {
      byte[] data = new byte[1024];
      SecureRandom random = new SecureRandom();
      Path file = Paths.get("C:\\temp").resolve(Objects.toString(UUID.randomUUID()));
      try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
         while(size > 0) {
            random.nextBytes(data);
            int write = Math.min(data.length, size);
            out.write(data, 0, write);
            size -= write;
         }
      }
      return file;
   }

}