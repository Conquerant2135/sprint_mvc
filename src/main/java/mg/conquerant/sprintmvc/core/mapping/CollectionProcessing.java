package mg.conquerant.sprintmvc.core.mapping;

import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class CollectionProcessing {

    public String buildKey(Stack<String> keyStack) {
        StringBuilder sb = new StringBuilder();
        for (String token : keyStack) {
            if (!token.startsWith("[") && sb.length() > 0) {
                sb.append(".");
            }
            sb.append(token);
        }
        return sb.toString();
    }

    public Set<Integer> countElements(Set<String> params, String starter) {
        String start = starter + "[";
        Set<Integer> indexes = new TreeSet<>();
        for (String param : params) {
            if (!param.startsWith(start))
                continue;
            int end = param.indexOf(']', start.length());
            if (end == -1)
                continue;
            try {
                indexes.add(Integer.parseInt(param.substring(start.length(), end)));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return indexes;
    }

    public boolean hasKeysUnder(Set<String> params, String prefix) {
        return params.stream().anyMatch(p -> p.startsWith(prefix + ".") || p.startsWith(prefix + "["));
    }
}