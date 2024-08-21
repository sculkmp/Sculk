package org.sculk.utils.json;

import com.google.gson.JsonObject;

public interface Serializable {

    JsonObject toJson();
}
