package fr.quentin.refSolver;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

public interface ToJson {
    public static ToJson StaticIntance = new ToJson() {};
    public default JsonElement apply(Object x){
        if (x == null) {
            return JsonNull.INSTANCE;
        } else {
            return new JsonPrimitive(x.getClass().getName()+"@"+x.hashCode());
        }
    }
}