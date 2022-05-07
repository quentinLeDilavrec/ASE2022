package fr.quentin.refSolver;

import com.google.gson.JsonElement;

public interface JsonSerializable {
    public default JsonElement toJson(){
        return toJson(
            ToJson.StaticIntance
        );
    }
    public default JsonElement toJson(ToJson f){
        return toJson();
    }
}
