package s12;

import s05.GeneralSearchTreeMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ex05 {
    private GeneralSearchTreeMap<Integer, List<String>> dico;

    public Ex05() {
        dico = new GeneralSearchTreeMap<>(5);
    }

    public void add(String[] t) {
        for (String word : t) {
            add(word);
        }
    }

    public void add(String word) {
        int hashVal = hash(word);
        List<String> list = dico.get(hashVal);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(word);
        dico.put(hashVal, list);
    }

    public List<String> findAnagram(String target) {
        List<String> anagrams = dico.get(hash(target));
        List<String> results = new ArrayList<>();
        if (anagrams==null) return results;
        if(anagrams.isEmpty()) return results;
        char[] targetAnagram = target.toCharArray();
        Arrays.sort(targetAnagram);
        for (String word : anagrams) {
            char[] wordAnagram = word.toCharArray();
            Arrays.sort(wordAnagram);
            for (int i = 0; i < targetAnagram.length-1; i++) {
                if (wordAnagram[i] != targetAnagram[i]) continue;
            }
            results.add(word);
        }
        return results;
    }

    private int hash(String word) {
        int hash = 0;
        for (char c : word.toCharArray()) {
            hash += c;
        }
        hash = hash * 10 + word.length();
        return hash;
    }
}
