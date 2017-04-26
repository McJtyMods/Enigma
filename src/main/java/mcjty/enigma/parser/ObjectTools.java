package mcjty.enigma.parser;

public class ObjectTools {

    public static boolean asBoolSafe(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else if (o instanceof Integer) {
            return ((Integer) o) != 0;
        } else if (o instanceof String) {
            return "true".equals(o);
        } else {
            return false;
        }
    }

    public static int asIntSafe(Object o) {
        if (o instanceof Integer) {
            return (int) o;
        } else if (o instanceof Boolean) {
            return ((Boolean) o) ? 1 : 0;
        } else if (o instanceof String) {
            return Integer.parseInt((String) o);
        } else {
            return 0;
        }
    }

    public static String asStringSafe(Object o) {
        if (o instanceof Integer) {
            return Integer.toString((Integer) o);
        } else if (o instanceof Boolean) {
            return ((Boolean) o) ? "true" : "false";
        } else if (o instanceof String) {
            return (String) o;
        } else {
            return "";
        }
    }

    public static Object add(Object o1, Object o2) {
        if (o1 instanceof String) {
            return asStringSafe(o1) + asStringSafe(o2);
        } else if (o1 instanceof Integer) {
            return asIntSafe(o1) + asIntSafe(o2);
        } else {
            return "";
        }
    }

    public static Object sub(Object o1, Object o2) {
        if (o1 instanceof Integer) {
            return asIntSafe(o1) - asIntSafe(o2);
        } else {
            return "";
        }
    }

    public static Object mul(Object o1, Object o2) {
        if (o1 instanceof Integer) {
            return asIntSafe(o1) * asIntSafe(o2);
        } else {
            return "";
        }
    }

    public static Object div(Object o1, Object o2) {
        if (o1 instanceof Integer) {
            return asIntSafe(o1) / asIntSafe(o2);
        } else {
            return "";
        }
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }
}
