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

import com.google.gson.Gson;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.FieldBody;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class FieldMapConverter {
  /**
   * A converter which convert {@code @FieldBody} params to filed map.
   */
  static class FieldMapConverterFactory extends Converter.Factory {
    private final Gson gson = new Gson();

    @Override
    public Converter<?, Map<String, String>> fieldMapConverter(
        Type type, Annotation[] annotations, Retrofit retrofit) {
      return value -> {
        HashMap<String, String> fieldMap = new HashMap<>();
        if (Base.class.isAssignableFrom((Class<?>) type)) {
          fieldMap.put("ID", ((Base) value).getId());
          fieldMap.put("Body", gson.toJson(value));
          fieldMap.put("Timestamp", System.currentTimeMillis() + "");
        }
        return fieldMap;
      };
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
    final String name;

    Repo(String id, String name) {
      super(id);
      this.name = name;
    }
  }

  interface Service {
    @FormUrlEncoded
    @POST("/")
    Call<ResponseBody> example(@FieldBody Repo repo);
  }

  public static void main(String... args) throws IOException, InterruptedException {
    MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse());
    server.start();

    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(new FieldMapConverterFactory())
            .build();
    Service service = retrofit.create(Service.class);

    Repo retrofitRepo = new Repo("1", "retrofit");

    service.example(retrofitRepo).execute();
    RecordedRequest takeRequest = server.takeRequest();
    System.out.println("Request body: " + takeRequest.getBody());

    server.shutdown();
  }
}
