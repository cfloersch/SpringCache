/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache;

/**
 * TODO Do I even need this? Only if I want to signal the caller of
 * cache() that it is actually caching. Otherwise, simply returning
 * a ClientHttpResponse proxy that actually stores the response
 * entity would be sufficient.
 */
public interface CacheRequest {



}
