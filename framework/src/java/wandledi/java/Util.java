package wandledi.java;

public class Util {

    public static String getPath(String prefix, String path) {

        StringBuilder sb = new StringBuilder(prefix.length() + path.length());
        sb.append(prefix);
        if (!prefix.endsWith("/")) {
            sb.append("/");
        }
        int start = path.startsWith("/") ? 1 : 0;
        sb.append(path.substring(start));

        return sb.toString();
    }
}
