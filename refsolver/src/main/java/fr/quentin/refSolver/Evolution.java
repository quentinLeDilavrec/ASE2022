package fr.quentin.refSolver;

import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public interface Evolution<T> extends JsonSerializable {
    public Set<Position> getPreEvolutionPositions();
    public Set<Position> getPostEvolutionPositions();
    public T getOriginal();
    public String getCommitIdBefore();
    public String getCommitIdAfter();

    @Override
    default public JsonObject toJson() {
        JsonObject r = new JsonObject();
        r.add("type", JsonNull.INSTANCE);
        r.addProperty("commitIdBefore", getCommitIdBefore());
        r.addProperty("commitIdAfter", getCommitIdAfter());
        JsonArray before = new JsonArray();
        for (Position p : getPreEvolutionPositions()) {
            before.add(p.toJson());
        }
        r.add("before", before);
        JsonArray after = new JsonArray();
        for (Position p : getPostEvolutionPositions()) {
            after.add(p.toJson());
        }
        r.add("after", after);
        return r;
    }

}
