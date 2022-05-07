package fr.quentin.refSolver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * A liked list materializing the impact of an impacting element on an impacted
 * element
 */
public class ImpactChain implements JsonSerializable {

    /**
     *
     */
    private ImpactChain previous;
    private ImpactElement root;
    private ImpactElement current;
    private Integer size;
    private ImpactType impactType;
    private Map<String, Object> more;


    public ImpactType getType() {
        return impactType;
    }

    public <T> T getMD(String key) {
        return (T) more.get(key);
    }

    public <T> T getMD(String key, T deflt) {
        return (T) more.getOrDefault(key, deflt);
    }

    public <T> T putMD(String key, T value) {
        return (T) more.put(key, value);
    }

    public ImpactChain(ImpactElement impactingThing) {
        this.previous = null;
        this.root = impactingThing;
        this.current = impactingThing;
        this.size = 1;
        this.impactType = null;
        this.more = new HashMap<>();
    }

    protected ImpactChain(ImpactChain last, ImpactElement content, ImpactType impactType) {
        this(last, content, impactType, new HashMap<>());
    }

    protected ImpactChain(ImpactChain last, ImpactElement content, ImpactType impactType, Map<String, Object> more) {
        this.previous = last;
        this.root = last.getRoot();
        this.current = content;
        this.size = 1 + last.size();
        this.impactType = impactType;
        this.more = more;
    }

    public ImpactElement getRoot() {
        return root;
    }

    public Integer size() {
        return size;
    }

    public ImpactElement getLast() {
        return current;
    }

    public ImpactChain getPrevious() {
        return previous;
    }

    public ImpactChain extend(ImpactElement x, ImpactType impactType) {
        return extend(x, impactType, new HashMap<>());
    }

    public ImpactChain extend(ImpactElement x, ImpactType impactType, Map<String, Object> more) {
        return new ImpactChain(this, x, impactType, more);
    }

    @Override
    public String toString() {
        return "Impact [current=" + current + ", root=" + root + "]";
    }

    @Override
    public JsonElement toJson(ToJson f) {
        JsonArray a = new JsonArray();
        ImpactChain prev = this;
        a.add(size);
        while (prev != null) {
            a.add(f.apply(prev.getLast()));
            prev = prev.getPrevious();
        }
        return a;
    }

    private int prev_hash;
	public static final String TESTS_REACHED = "tests reached";
	public static final String WEIGHT = "weight";
	public static final String REDUNDANT = "redundant";

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((previous == null) ? 0 : (prev_hash == 0) ? (prev_hash = previous.hashCode()) : prev_hash);
        result = prime * result + ((impactType == null) ? 0 : impactType.hashCode());
        result = prime * result + ((current == null) ? 0 : current.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImpactChain other = (ImpactChain) obj;
        if (current == null) {
            if (other.current != null)
                return false;
        } else if (!current.equals(other.current))
            return false;
        if (impactType != other.impactType)
            return false;
        if (previous == null) {
            if (other.previous != null)
                return false;
        } else if (!previous.equals(other.previous))
            return false;
        return true;
    }

    // version ref by head and tail
    // @Override
    // public boolean equals(Object obj) {
    //     // TODO use more attributes to be able to analyse (here compare) more complexe
    //     // chains
    //     if (obj instanceof ImpactChain) {
    //         ImpactChain x = (ImpactChain) obj;
    //         return x.getRoot().equals(getRoot()) && x.getLast().equals(getLast());
    //     }
    //     return super.equals(obj);
    // }

    // @Override
    // public int hashCode() {
    //     final int prime = 31;
    //     int result = 1;
    //     result = prime * result + getRoot().hashCode();
    //     result = prime * result + (getLast().hashCode());
    //     return result;
    // }
}