/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache;

import xpertss.lang.Objects;

import java.nio.file.Path;

public class CacheStore {

   private final Path path;
   private final CacheType type;
   private final CachingPolicy policy;
   private final CacheParams params;

   public CacheStore(Path path, CacheType type, CacheParams params)
   {
      this.path = Objects.notNull(path, "path");
      this.type = Objects.notNull(type, "type");
      this.params = Objects.notNull(params, "params");
      this.policy = type.getPolicy();
   }

   public CacheType getType()
   {
      return type;
   }

   public Path getPath()
   {
      return path;
   }

   public CacheParams getParams()
   {
      return params;
   }


}
