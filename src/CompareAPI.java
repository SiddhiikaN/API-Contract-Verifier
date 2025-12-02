import com.google.gson.*;
import java.nio.file.*;
import java.util.*;

public class CompareAPI {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java CompareAPI old.json new.json");
            return;
        }

        JsonElement oldJson = JsonParser.parseString(Files.readString(Path.of(args[0])));
        JsonElement newJson = JsonParser.parseString(Files.readString(Path.of(args[1])));

        List<String> added = new ArrayList<>();
        List<String> removed = new ArrayList<>();
        List<String> changed = new ArrayList<>();

        compare("", oldJson, newJson, added, removed, changed);

        printSection("--- CHANGED VALUES ---", changed, true);
        printSection("--- ADDED FIELDS ---", added, false);
        printSection("--- REMOVED FIELDS ---", removed, false);

        System.out.println("\n--- RISK SCORE ---");
        if (!removed.isEmpty())
            System.out.println("HIGH (Fields removed)");
        else if (!changed.isEmpty())
            System.out.println("MEDIUM (Value changes found)");
        else if (!added.isEmpty())
            System.out.println("LOW (Only new fields added)");
        else
            System.out.println("SAFE (No changes)");
    }

    static void compare(String path, JsonElement oldVal, JsonElement newVal,
            List<String> added, List<String> removed, List<String> changed) {

        // == null → key doesn’t exist
        // isJsonNull() → key exists and value is null

        /*
         * missing completely → oldVal == null
         * present but has null value → oldVal.isJsonNull()
         * In the new JSON: Field exists
         * Field has a real value, not null So:➜ New JSON DOES have this field with an
         * actual value.
         */
        if ((oldVal == null || oldVal.isJsonNull()) && newVal != null && !newVal.isJsonNull()) {
            added.add(path);
            return;
        }

        /*
         * In the new JSON, this field is either: missing completely → newVal == null
         * present but has null value → newVal.isJsonNull()
         * 
         * In the old JSON: Field exists
         * Field has a real value, not null
         * So: Old JSON had the field, new JSON does not.
         */
        if ((newVal == null || newVal.isJsonNull()) && oldVal != null && !oldVal.isJsonNull()) {
            removed.add(path);
            return;
        }

        if (oldVal.isJsonPrimitive() && newVal.isJsonPrimitive()) {
            if (!oldVal.equals(newVal))
                changed.add(path + "\n  OLD: " + oldVal + "\n  NEW: " + newVal);
            return;
        }
        

        if (oldVal.isJsonObject() && newVal.isJsonObject()) {
            JsonObject o = oldVal.getAsJsonObject();
            JsonObject n = newVal.getAsJsonObject();
            //the set contains every possible field inside this object.
            Set<String> keys = new HashSet<>();
            keys.addAll(o.keySet());
            keys.addAll(n.keySet());
            /*For every field inside the object:
            Build a new path (e.g., user.name, user.age, address.city)
            Call compare() again on that inner field
            This is recursion */
            for (String k : keys)
                compare(path.isEmpty() ? k : path + "." + k, o.get(k), n.get(k), added, removed, changed);
            return;
        }

        /*path is the location of the value inside the JSON.
        eg: { "users": [
        { "name": "Alice" }]}  so path is users[0].name */


        /*for eg path = "users"
        i = 0
        path + "[" + i + "]"
        "users" + "[" + 0 + "]"
        "users[0"

         */

        if (oldVal.isJsonArray() && newVal.isJsonArray()) {
            JsonArray oa = oldVal.getAsJsonArray();
            JsonArray na = newVal.getAsJsonArray();
            //If you loop only until old size, you will miss new fields.so max means everything 
            int max = Math.max(oa.size(), na.size());
            for (int i = 0; i < max; i++)
                compare(path + "[" + i + "]",
                        i < oa.size() ? oa.get(i) : null,
                        i < na.size() ? na.get(i) : null,
                        added, removed, changed);
            return;
        }
        /*old: i < old size? → 2 < 2 → false → null
        new: i < new size? → 2 < 3 → true → "orange"
        So compare("path[2]", null, "orange")
        Result: → added field at path[2] */

        changed.add(path + " | TYPE CHANGE");
    }

    static void printSection(String title, List<String> list, boolean multiline) {
        System.out.println("\n" + title);
        if (list.isEmpty())
            System.out.println("(none)");
        else {
            for (String s : list) {
                if (multiline)
                    System.out.println(s + "\n");
                else
                    System.out.println(s);
            }
        }
    }
}
