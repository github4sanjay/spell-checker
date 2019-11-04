package com.paytmmall.spellchecker.library.spellchecker;

import java.util.Arrays;

// A growable list of elements that's optimized to support adds, but not deletes,
// of large numbers of elements, storing data in a way that's friendly to the garbage
// collector (not backed by a monolithic array object), and can grow without needing
// to copy the entire backing array contents from the old backing array to the new.
public class ChunkArray<T> {
    private static int chunkSize = 4096;//this must be a power of 2, otherwise can't optimize row and col functions
    private static int divShift = 12;   // number of bits to shift right to do division by chunkSize (the bit position of chunkSize)
    public SuggestionStage.Node[][] values;             // Note: Node (SymSpell.SuggestionStage.Node) is found in SymSpell.SymSpell.java.
    public int count;

    ChunkArray(int initialCapacity) // initialCapacity = 32768
    {
        int chunks = (initialCapacity + chunkSize - 1) / chunkSize; // 8
        values = new SuggestionStage.Node[chunks][];
        for (int i = 0; i < values.length; i++) values[i] = new SuggestionStage.Node[chunkSize];
    }

    public int add(SuggestionStage.Node value) {
        if (count == capacity()) {
            SuggestionStage.Node[][] newValues = Arrays.copyOf(values, values.length + 1);
            newValues[values.length] = new SuggestionStage.Node[chunkSize];
            values = newValues;
        }

        values[row(count)][col(count)] = value;
        count++;
        return count - 1;
    }

    public void clear() {
        count = 0;
    }

    public SuggestionStage.Node getValues(int index) {
        return values[row(index)][col(index)];
    }

    public void setValues(int index, SuggestionStage.Node value) {
        values[row(index)][col(index)] = value;
    }

    public void setValues(int index, SuggestionStage.Node value, SuggestionStage.Node[][] list) {
        list[row(index)][col(index)] = value;
    }

    private int row(int index) {
        return index >> divShift;
    } // same as index / chunkSize

    private int col(int index) {
        return index & (chunkSize - 1);
    } //same as index % chunkSize

    private int capacity() {
        return values.length * chunkSize;
    }
}