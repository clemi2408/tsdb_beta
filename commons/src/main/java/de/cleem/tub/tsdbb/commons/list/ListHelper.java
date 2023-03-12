package de.cleem.tub.tsdbb.commons.list;

import java.util.ArrayList;
import java.util.List;

public class ListHelper {

    public static <T> List<List<T>> splitListIntoParts(List<T> inputList, int parts)
    {
        final int itemsPerPart = inputList.size() / parts;
        final int leftoverItems = inputList.size() % parts;

        final List<List<T>> partsList = new ArrayList<>();

        int from;
        int to;
        for (int i = 0; i < parts; i++) {

            from=i * itemsPerPart + Math.min(i, leftoverItems);
            to=(i + 1) * itemsPerPart + Math.min(i + 1, leftoverItems);

            partsList.add(inputList.subList(from, to));
        }

        return partsList;

    }
}
