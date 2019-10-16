/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/10/2019
 */
package com.xpertss.cache.store.util;

import org.springframework.http.HttpHeaders;

public final class HttpUtils {

   public static HttpHeaders clone(HttpHeaders headers)
   {
      HttpHeaders result = new HttpHeaders();
      result.addAll(headers);
      return result;
   }

}
