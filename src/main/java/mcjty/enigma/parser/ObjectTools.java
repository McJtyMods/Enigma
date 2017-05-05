package mcjty.enigma.parser;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class ObjectTools {

    public static boolean asBoolSafe(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else if (o instanceof Integer) {
            return ((Integer) o) != 0;
        } else if (o instanceof String) {
            return "true".equals(o);
        } else if (o instanceof Double) {
            return ((Double) o) != 0;
        } else if (o instanceof Float) {
            return ((Float) o) != 0;
        } else {
            return false;
        }
    }

    public static int asIntSafe(Object o) {
        if (o instanceof Integer) {
            return (int) o;
        } else if (o instanceof Double) {
            return ((Double) o).intValue();
        } else if (o instanceof Float) {
            return ((Float) o).intValue();
        } else if (o instanceof Boolean) {
            return ((Boolean) o) ? 1 : 0;
        } else if (o instanceof String) {
            return Integer.parseInt((String) o);
        } else {
            return 0;
        }
    }

    public static double asDoubleSafe(Object o) {
        if (o instanceof Double) {
            return (Double) o;
        } else if (o instanceof Integer) {
            return ((Integer) o).doubleValue();
        } else if (o instanceof Float) {
            return ((Float) o).doubleValue();
        } else if (o instanceof Boolean) {
            return ((Boolean) o) ? 1.0 : 0.0;
        } else if (o instanceof String) {
            return Double.parseDouble((String) o);
        } else {
            return 0.0;
        }
    }

    public static String asStringSafe(Object o) {
        if (o instanceof Integer) {
            return Integer.toString((Integer) o);
        } else if (o instanceof Double) {
            return Double.toString((Double) o);
        } else if (o instanceof Float) {
            return Float.toString((Float) o);
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
        } else if (o1 instanceof Double || o1 instanceof Float) {
            return asDoubleSafe(o1) + asDoubleSafe(o2);
        } else {
            return "";
        }
    }

    public static Object sub(Object o1, Object o2) {
        if (o1 instanceof Integer) {
            return asIntSafe(o1) - asIntSafe(o2);
        } else if (o1 instanceof Double || o1 instanceof Float) {
            return asDoubleSafe(o1) - asDoubleSafe(o2);
        } else {
            return "";
        }
    }

    public static Object mul(Object o1, Object o2) {
        if (o1 instanceof Integer) {
            return asIntSafe(o1) * asIntSafe(o2);
        } else if (o1 instanceof Double || o1 instanceof Float) {
            return asDoubleSafe(o1) * asDoubleSafe(o2);
        } else {
            return "";
        }
    }

    public static Object div(Object o1, Object o2) {
        if (o1 instanceof Integer) {
            return asIntSafe(o1) / asIntSafe(o2);
        } else if (o1 instanceof Double || o1 instanceof Float) {
            return asDoubleSafe(o1) / asDoubleSafe(o2);
        } else {
            return "";
        }
    }

    public static Object mod(Object o1, Object o2) {
        if (o1 instanceof Integer) {
            return asIntSafe(o1) % asIntSafe(o2);
        } else if (o1 instanceof Double || o1 instanceof Float) {
            return asDoubleSafe(o1) % asDoubleSafe(o2);
        } else {
            return "";
        }
    }

    // This functions knows how to compare interned strings
    public static boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        if (o1 instanceof Integer && o2 instanceof String) {
            return o1.equals(STRINGS.get((String) o2));
        } else if (o1 instanceof String && o2 instanceof Integer) {
            return o2.equals(STRINGS.get((String) o1));
        }
        return o1.equals(o2);
    }

    public static boolean less(Object o1, Object o2) {
        if (o1 == null) {
            return false;
        }
        if (o2 == null) {
            return true;
        }
        if (o1 instanceof Integer) {
            return ((Integer) o1) < asIntSafe(o2);
        } else if (o1 instanceof Double) {
            return ((Double) o1) < asDoubleSafe(o2);
        } else if (o1 instanceof Float) {
            return ((Float) o1) < asDoubleSafe(o2);
        } else if (o1 instanceof String) {
            return ((String) o1).compareTo(asStringSafe(o2)) < 0;
        }
        return false;
    }

    public static boolean lessOrEqual(Object o1, Object o2) {
        if (equals(o1, o2)) {
            return true;
        }
        if (o1 == null) {
            return false;
        }
        if (o2 == null) {
            return true;
        }
        if (o1 instanceof Integer) {
            return ((Integer) o1) < asIntSafe(o2);
        } else if (o1 instanceof Double) {
            return ((Double) o1) < asDoubleSafe(o2);
        } else if (o1 instanceof Float) {
            return ((Float) o1) < asDoubleSafe(o2);
        } else if (o1 instanceof String) {
            return ((String) o1).compareTo(asStringSafe(o2)) < 0;
        }
        return false;
    }
}
