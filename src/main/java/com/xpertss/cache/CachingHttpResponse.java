/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package com.xpertss.cache;

import com.xpertss.cache.store.CacheItem;
import com.xpertss.cache.store.CacheStore;
import com.xpertss.cache.store.io.TeeInputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import xpertss.lang.Objects;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class proxies a ClientHttpResponse and wraps the InputStream
 * so that the entity is written to the cache file as it is read from
 * the network. Finally, once the complete entity has been written to
 * disk the CacheItem is actually stored in the cache.
 */
public class CachingHttpResponse implements ClientHttpResponse {

   /*
      TODO I need to modify this so that the CacheItem is added to the cache only after
      it has been fully processed (and thus the file cache is complete). This will allow
      me to know the size of the item even when there is no Content-Length header and it
      will prevent corrupt cache data because the entity body was not fully downloaded.
    */

   private final ClientHttpResponse proxied;
   private final CacheStore cache;
   private final CacheItem item;

   public CachingHttpResponse(ClientHttpResponse proxied, CacheStore cache, CacheItem item)
   {
      this.proxied = Objects.notNull(proxied, "proxied");
      this.cache = Objects.notNull(cache, "cache");
      this.item = Objects.notNull(item, "item");
   }



   @Override
   public HttpStatus getStatusCode()
      throws IOException
   {
      return proxied.getStatusCode();
   }

   @Override
   public int getRawStatusCode()
      throws IOException
   {
      return proxied.getRawStatusCode();
   }

   @Override
   public String getStatusText()
      throws IOException
   {
      return proxied.getStatusText();
   }


   @Override
   public HttpHeaders getHeaders()
   {
      return proxied.getHeaders();
   }




   @Override
   public InputStream getBody()
      throws IOException
   {
      TeeInputStream input = new TeeInputStream(proxied.getBody(), item.newOutput());
      // need to detect EOF and add cache item to the cache store (for just the first occurrence)
      return input;
   }

   @Override
   public void close()
   {
      proxied.close();
   }

}
