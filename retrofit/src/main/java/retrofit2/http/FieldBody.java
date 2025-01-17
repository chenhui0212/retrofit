/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2.http;

import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use object for a form-encoded request.
 *
 * <p>Simple Example:
 *
 * <pre><code>
 * &#64;FormUrlEncoded
 * &#64;POST("/things")
 * Call&lt;ResponseBody&gt; things(@FieldBody SomeReq obj);
 * </code></pre>
 *
 * The object will be convert to field {@link java.util.Map Map} using the {@link Retrofit Retrofit}
 * instance {@link Converter Converter} you add to Retrofit, and the result will be handle just
 * like {@link FieldMap}.
 *
 * @see FormUrlEncoded
 * @see Field
 * @see FieldMap
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface FieldBody {
  /** Specifies whether the names and values are already URL encoded. */
  boolean encoded() default false;
}
