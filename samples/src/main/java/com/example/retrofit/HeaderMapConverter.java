/*
 * Copyright (C) 2015 Square, Inc.
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
package com.example.retrofit;

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public final class HeaderMapConverter {
  /**
   * A converter which extract specific information from the {@code @Body} params and put it
   * into the request header.
   */
  static class HeaderMapConverterFactory extends Converter.Factory {
    @Override
    public Converter<?, Map<String, String>> headerMapConverter(
        Type type, Annotation[] annotations, Retrofit retrofit) {
      if (Base.class.isAssignableFrom((Class<?>) type)) {
        return value -> {
          HashMap<String, String> headerMap = new HashMap<>(1);
          headerMap.put("ID", ((Base) value).getId());
          return headerMap;
        };
      }
      return null;
    }
  }

  static class Base {
    final transient String id;

    Base(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }
  }

  static class Repo extends Base {
    final String owner;
    final String name;

    Repo(String id, String owner, String name) {
      super(id);
      this.owner = owner;
      this.name = name;
    }
  }

  interface Service {
    @POST("/")
    Call<ResponseBody> example(@Body Repo repo);
  }

  public static void main(String... args) throws IOException, InterruptedException {
    MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse());
    server.start();

    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(new HeaderMapConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    Service service = retrofit.create(Service.class);

    Repo retrofitRepo = new Repo("1", "square", "retrofit");

    service.example(retrofitRepo).execute();
    RecordedRequest takeRequest = server.takeRequest();
    System.out.println("@Body header ID: " + takeRequest.getHeader("ID"));
    System.out.println("Request body: " + takeRequest.getBody());

    server.shutdown();
  }
}
