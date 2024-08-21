package org.sculk.utils.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public interface Serializable {

    JsonObject toJson();

    class GsonHolder {
        public static Gson GSON = new Gson();
    }
}
