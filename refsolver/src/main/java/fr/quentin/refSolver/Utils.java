package fr.quentin.refSolver;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

public class Utils {
    public static CtElement matchExact(CtElement ele, int start, int end) {
        SourcePosition position = ele.getPosition();
        if (position == null || !position.isValidPosition()) {
            return null;
        }
        int sourceStart = position.getSourceStart();
        int sourceEnd = position.getSourceEnd();
        int ds = start - sourceStart;
        int de = sourceEnd - end;
        if (ds == 0 && de == 0) {
            return ele;
        } else if (ds >= 0 && de >= 0) {
            int i = 0;
            for (CtElement child : ele.getDirectChildren()) {
                CtElement r = matchExact(child, start, end);
                if (r != null) {
                    return r;
                }
                i++;
            }
            System.out.println("did not find " + start + " " + end + " in" + ele);
            return null;
        } else if (sourceEnd < start) {
            return null;
        } else if (end < sourceStart) {
            return null;
        } else {
            return null;
        }
    }

    public static CtElement matchApprox(CtElement ele, int start, int end) {
        SourcePosition position = ele.getPosition();
        if (position == null || !position.isValidPosition()) {
            return null;
        }
        int sourceStart = position.getSourceStart();
        int sourceEnd = position.getSourceEnd();
        int ds = start - sourceStart;
        int de = sourceEnd - end;
        if (ds == 0 && de == 0) {
            return ele;
        } else if (ds >= 0 && de >= 0) {
            int i = 0;
            for (CtElement child : ele.getDirectChildren()) {
                CtElement r = matchApprox(child, start, end);
                if (r != null) {
                    return r;
                }
                i++;
            }
            System.out.println("approx match of " + ds + " " + de + " at" + position);
            return ele;
        } else if (sourceEnd < start) {
            return null;
        } else if (end < sourceStart) {
            return null;
        } else {
            return null;
        }
    }

	public static boolean isContainingType(CtType<?> ele, int start, int end) {
		SourcePosition position = ele.getPosition();
		if (position == null || !position.isValidPosition()) {
			return false;
		}
		int sourceStart = position.getSourceStart();
		int sourceEnd = position.getSourceEnd();
		int ds = start - sourceStart;
		int de = sourceEnd - end;
		if (ds == 0 && de == 0) {
			return true;
		} else if (ds >= 0 && de >= 0) {
			return true;
		} else if (sourceEnd < start) {
			return false;
		} else if (end < sourceStart) {
			return false;
		} else {
			return false;
		}
    }
}