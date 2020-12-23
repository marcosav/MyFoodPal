package com.gmail.marcosav2010.myfoodpal.model.food.lister;

import com.gmail.marcosav2010.json.JSONObject;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ListerData {

    @NonNull
    @Setter
    private String data;
    private JSONObject config;

    @Getter
    private final Set<String>
            exceptions = createSet(),
            unitAliases = createSet(),
            buyMeasured = createSet(),
            take = createSet(),
            weight = createSet(),
            chop = createSet();
    @Getter
    private final Map<String, String> aliases = createMap();
    @Getter
    private final Map<String, Double> conversions = createMap();
    @Getter
    private final Multimap<String, Integer> datedFood = createMMap();

    @Getter
    private final Map<String, Object> allConfig = createMap();

    private void clear() {
        exceptions.clear();
        unitAliases.clear();
        buyMeasured.clear();
        take.clear();
        weight.clear();
        chop.clear();
        aliases.clear();
        conversions.clear();
        datedFood.clear();
    }

    public void load() {
        config = new JSONObject(data);

        clear();

        fillSet(exceptions, "exceptions");
        fillSet(unitAliases, "unit_aliases");
        fillSet(buyMeasured, "buy_measured");
        fillSet(take, "p_sacar");
        fillSet(weight, "p_pesar");
        fillSet(chop, "p_picar");

        fillMap(aliases, "aliases");
        fillMap(conversions, "conversions");

        fillMultimap(datedFood, "dated_food");
    }

    boolean isException(String n) {
        return containsStarting(exceptions, n);
    }

    boolean isMeasured(String n) {
        return containsStarting(buyMeasured, n);
    }

    boolean isChop(String n) {
        return containsStarting(chop, n);
    }

    boolean isWeight(String n) {
        return containsStarting(weight, n);
    }

    boolean isTake(String n) {
        return containsStarting(take, n);
    }

    boolean containsStarting(Collection<String> c, String n) {
        return c.stream().anyMatch(s -> n.toLowerCase().startsWith(s));
    }

    String getAlias(String n) {
        Entry<String, String> en = aliases
                .entrySet()
                .stream()
                .filter(e -> n.toLowerCase().startsWith(e.getKey()))
                .findFirst()
                .orElse(null);

        if (en == null)
            return n;

        return en.getValue();
    }

    boolean isUnitAlias(String n) {
        return containsStarting(unitAliases, n);
    }

    boolean dateMatches(String n, int weekday) {
        return datedFood.containsEntry(n.toLowerCase(), weekday);
    }

    Double getConversion(String n) {
        Entry<String, Double> en = conversions
                .entrySet()
                .stream()
                .filter(e -> n.toLowerCase().startsWith(e.getKey()))
                .findFirst()
                .orElse(null);

        if (en == null)
            return null;

        return Double.parseDouble(String.valueOf(en.getValue()));
    }

    private void save(String name, Object config) {
        allConfig.put(name, config);
    }

    private void fillSet(Set<String> set, String name) {
        save(name, set);
        if (config.has(name))
            config.getJSONArray(name).forEach(e -> set.add(((String) e).toLowerCase()));
    }

    @SuppressWarnings("unchecked")
    private <V> void fillMap(Map<String, V> map, String name) {
        save(name, map);
        if (config.has(name))
            config.getJSONObject(name).toMap().forEach((k, v) -> map.put(k.toLowerCase(), (V) v));
    }

    @SuppressWarnings("unchecked")
    private <V> void fillMultimap(Multimap<String, V> mmap, String name) {
        save(name, mmap);
        if (config.has(name))
            config.getJSONObject(name).toMap().forEach((k, v) -> mmap.putAll(k.toLowerCase(), (List<V>) v));
    }

    private Set<String> createSet() {
        return new HashSet<>();
    }

    private <V> Map<String, V> createMap() {
        return new HashMap<>();
    }

    private <V> Multimap<String, V> createMMap() {
        return HashMultimap.create();
    }
}
