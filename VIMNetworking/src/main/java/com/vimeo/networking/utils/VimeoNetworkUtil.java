/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Vimeo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vimeo.networking.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import retrofit2.GsonConverterFactory;

/**
 * A utility class that can eventually be shared across various retrofit/vimeo-api reliant libraries
 * <p/>
 * Created by kylevenn on 10/30/15.
 */
public class VimeoNetworkUtil {

    /**
     * Static helper method that automatically applies the VimeoClient Gson preferences
     * </p>
     * This includes formatting for dates as well as a LOWER_CASE_WITH_UNDERSCORES field naming policy
     *
     * @return Gson object that can be passed into a {@link GsonConverterFactory} create() method
     */
    public static Gson getGson() {
        // Example date: "2015-05-21T14:24:03+00:00"
        return getGsonBuilder().create();
    }

    /**
     * Static helper method that automatically applies the VimeoClient Gson preferences
     * </p>
     * This includes formatting for dates as well as a LOWER_CASE_WITH_UNDERSCORES field naming policy
     *
     * @return GsonBuilder that can be built upon and then created
     */
    public static GsonBuilder getGsonBuilder() {
        // Example date: "2015-05-21T14:24:03+00:00"
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, ISO8601.getDateSerializer())
                .registerTypeAdapter(Date.class, ISO8601.getDateDeserializer());
        /** Refer to {@link ISO8601} for explanation of deserialization */
        // .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ")
    }

    /**
     * Returns a simple query map from a provided uri. The simple map enforces there is exactly one value for
     * every name (multiple values for the same name are regularly allowed in a set of parameters)
     *
     * @param uri a uri optionally with a query
     * @return a query map with a one to one mapping of names to values or empty {@link HashMap<String,String>}
     * if no parameters are found on the uri
     */
    public static HashMap<String, String> getSimpleQueryMap(String uri) {
        HashMap<String, String> queryMap = new HashMap<>();
        HttpUrl httpUrl = HttpUrl.parse(uri);
        if (httpUrl == null) {
            return queryMap;
        }

        for (String name : httpUrl.queryParameterNames()) {
            // Iterate over all the names and generate a simplified map of parameters
            List<String> paramValues = httpUrl.queryParameterValues(name);
            if (!paramValues.isEmpty()) {
                // If the values list isn't empty, then let's just take the first value associated with the key
                queryMap.put(name, paramValues.get(0));
            }
        }
        return queryMap;
    }

    /**
     * Return a builder of the given cache control because for some reason this doesn't exist already.
     * Useful for adding more attributes to an already defined {@link CacheControl}
     *
     * @param cacheControl The CacheControl to convert to a builder
     * @return A builder with the same attributes as the CacheControl passed in
     */

    public static CacheControl.Builder getCacheControlBuilder(CacheControl cacheControl) {
        CacheControl.Builder builder = new CacheControl.Builder();
        if (cacheControl.maxAgeSeconds() > -1) {
            builder.maxAge(cacheControl.maxAgeSeconds(), TimeUnit.SECONDS);
        }
        if (cacheControl.maxStaleSeconds() > -1) {
            builder.maxStale(cacheControl.maxStaleSeconds(), TimeUnit.SECONDS);
        }
        if (cacheControl.minFreshSeconds() > -1) {
            builder.minFresh(cacheControl.minFreshSeconds(), TimeUnit.SECONDS);
        }

        if (cacheControl.noCache()) {
            builder.noCache();
        }
        if (cacheControl.noStore()) {
            builder.noStore();
        }
        if (cacheControl.noTransform()) {
            builder.noTransform();
        }
        if (cacheControl.onlyIfCached()) {
            builder.onlyIfCached();
        }
        return builder;
    }
}
