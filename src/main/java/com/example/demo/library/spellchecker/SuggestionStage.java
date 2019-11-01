package com.example.demo.library.spellchecker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/// <summary>An intentionally opacque class used to temporarily stage
/// dictionary data during the adding of many words. By staging the
/// data during the building of the dictionary data, significant savings
/// of time can be achieved, as well as a reduction in final memory usage.</summary>
public class SuggestionStage {
    SuggestionStage(int initialCapacity) { // initialCapacity = 16384
        deletes = new HashMap<>(initialCapacity);
        nodes = new ChunkArray<Node>(initialCapacity * 2); // 32768
    }

    public class Node {
        public String suggestion;
        public int next;
        public Node(String suggestion, int next) {
            this.suggestion = suggestion;
            this.next = next;
        }
    }
    public class Entry {
        public int count;
        public int first;
        Entry(int count, int first) {
            this.count = count;
            this.first = first;
        }
    }
    public Map<Integer, Entry> deletes; // {get; set; }
    public ChunkArray<Node> nodes;
    /// <summary>Create a new instance of SymSpell.SuggestionStage.</summary>
    /// <remarks>Specifying ann accurate initialCapacity is not essential,
    /// but it can help speed up processing by aleviating the need for
    /// data restructuring as the size grows.</remarks>
    /// <param name="initialCapacity">The expected number of words that will be added.</param>

    /// <summary>Gets the count of unique delete words.</summary>
    public int deleteCount() { return deletes.size(); }
    /// <summary>Gets the total count of all suggestions for all deletes.</summary>
    public int nodeCount() { return nodes.count; }
    /// <summary>Clears all the data from the SuggestionStaging.</summary>
    public void clear() {
        deletes.clear();
        nodes.clear();
    }

    void add(int deleteHash, String suggestion) {
        Entry entry = deletes.getOrDefault(deleteHash, new Entry(0, -1));
        int next = entry.first;
        entry.count++;
        entry.first = nodes.count;
        deletes.put(deleteHash, entry);
        nodes.add(new Node(suggestion, next));
    }

    void commitTo(Map<Integer, String[]> permanentDeletes) {
        deletes.forEach((key, value) -> {
            int i;
            String[] suggestions;
            if (permanentDeletes.containsKey(key)) {
                suggestions = permanentDeletes.get(key);
                i = suggestions.length;
                String[] newSuggestion = Arrays.copyOf(suggestions, i + value.count);

                permanentDeletes.put(key, newSuggestion);
                suggestions = newSuggestion;
            } else {
                i = 0;
                suggestions = new String[value.count];
                permanentDeletes.put(key, suggestions);
            }
            int next = value.first;
            Node node;
            while (next >= 0) {
                node = nodes.getValues(next);
                suggestions[i] = node.suggestion;
                next = node.next;
                i++;
            }
        });
    }
}